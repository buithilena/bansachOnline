package com.suki.bansachOnline.controller;


import com.suki.bansachOnline.model.Cart;
import com.suki.bansachOnline.model.Order;
import com.suki.bansachOnline.model.User;

import com.suki.bansachOnline.respository.OrdersRepository;
import com.suki.bansachOnline.respository.UserRepository;
import com.suki.bansachOnline.service.GioHangService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/thanhtoan")
public class PaymentController {

    private final PayOS payOS;
    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    public PaymentController(PayOS payOS, OrdersRepository ordersRepository, UserRepository userRepository) {
        this.payOS = payOS;
        this.ordersRepository = ordersRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/payos", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> createPayOSPayment(
            @RequestBody Map<String, Object> orderData,
            @AuthenticationPrincipal Object principal,
            HttpServletRequest request,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String customerName = (String) orderData.get("customerName");
            String customerPhone = (String) orderData.get("customerPhone");
            String customerAddress = (String) orderData.get("customerAddress");
            int totalAmount = ((Number) orderData.get("totalAmount")).intValue();
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");

            // Tạo mã đơn hàng duy nhất
            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

            // Chuẩn bị dữ liệu thanh toán
            String baseUrl = getBaseUrl(request);
            String returnUrl = baseUrl + "/thanhtoan/success?orderCode=" + orderCode;
            String cancelUrl = baseUrl + "/thanhtoan/cancel";

            List<ItemData> itemList = new ArrayList<>();
            for (Map<String, Object> item : items) {
                String productName = "Sản phẩm ID: " + item.get("productId");
                int quantity = ((Number) item.get("quantity")).intValue();
                int price = ((Number) item.get("price")).intValue();
                itemList.add(ItemData.builder()
                        .name(productName)
                        .quantity(quantity)
                        .price(price)
                        .build());
            }

            String description = "ĐH #" + orderCode;
            if (description.length() > 25) {
                description = description.substring(0, 25);
            }

            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount(totalAmount)
                    .description(description)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .items(itemList)
                    .build();

            // Tạo link thanh toán
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            // Lưu thông tin đơn hàng
            Order order = new Order();
            order.setOrderDate(LocalDateTime.now());
            order.setTotalPrice(new BigDecimal(totalAmount));
            order.setStatus(Order.OrderStatus.pending);
            order.setCustomerName(customerName);
            order.setCustomerPhone(customerPhone);
            order.setCustomerAddress(customerAddress);
            order.setPaymentMethod("payos");

            // Lấy thông tin người dùng
            User user = getUserFromPrincipal(principal);
            if (user != null) {
                order.setUserId(user.getId());
            } else {
                // Kiểm tra giỏ hàng để lấy thông tin người dùng (nếu có)
                Cart cart = gioHangService.getOrCreateCart(null, session);
                if (cart.getUser() != null) {
                    order.setUserId(cart.getUser().getId());
                }
            }

            ordersRepository.save(order);

            response.put("success", true);
            response.put("checkoutUrl", data.getCheckoutUrl());
            response.put("orderCode", orderCode);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Không thể tạo link thanh toán: " + e.getMessage());
        }
        return response;
    }

    private User getUserFromPrincipal(Object principal) {
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) principal;
            return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        } else if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;
            String providerId = oAuth2User.getAttribute("sub") != null ? oAuth2User.getAttribute("sub") : oAuth2User.getAttribute("id");
            String provider = oAuth2User.getAttribute("sub") != null ? "google" : "facebook";
            return "google".equals(provider) ? userRepository.findByGoogleId(providerId).orElse(null)
                    : userRepository.findByFacebookId(providerId).orElse(null);
        }
        return null;
    }


    // Trang thành công
    @GetMapping("/success")
    public String success(@RequestParam(value = "orderCode", required = false) String orderCode, Model model) {
        try {
            if (orderCode != null) {
                // Cập nhật trạng thái đơn hàng
                Order order = ordersRepository.findById(Integer.parseInt(orderCode)).orElse(null);
                if (order != null) {
                    order.setStatus(Order.OrderStatus.completed);
                    ordersRepository.save(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("page", "success");
        model.addAttribute("pageTitle", "Thanh toán thành công");
        return "payment";
    }

    // Trang hủy
    @GetMapping("/cancel")
    public String cancel(Model model) {
        model.addAttribute("page", "cancel");
        model.addAttribute("pageTitle", "Thanh toán thất bại");
        return "payment";
    }

    // Helper method: Lấy base URL
    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        String url = scheme + "://" + serverName;
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            url += ":" + serverPort;
        }
        url += contextPath;
        return url;
    }
}