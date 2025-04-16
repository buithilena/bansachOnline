package com.suki.bansachOnline.controller;

import com.suki.bansachOnline.model.*;
import com.suki.bansachOnline.respository.UserRepository;
import com.suki.bansachOnline.service.GioHangService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class GioHangController {

    private static final Logger logger = LoggerFactory.getLogger(GioHangController.class);

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(
            @AuthenticationPrincipal Object principal,
            HttpSession session,
            @RequestParam("productId") int productId,
            @RequestParam("donViTinhId") int donViTinhId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        User user = getUserFromPrincipal(principal);
        Map<String, Object> response = new HashMap<>();
        try {
            Cart cart = gioHangService.addToCart(user, session, productId, donViTinhId, quantity);
            int cartItemCount = gioHangService.getCartItemCount(cart);
            response.put("success", true);
            response.put("cartItemCount", cartItemCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<List<CartItem>> getCartItems(
            @AuthenticationPrincipal Object principal,
            HttpSession session) {
        User user = getUserFromPrincipal(principal);
        try {
            List<CartItem> cartItems = gioHangService.getCartItems(user, session);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách mục trong giỏ hàng: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getCartCount(
            @AuthenticationPrincipal Object principal,
            HttpSession session) {
        User user = getUserFromPrincipal(principal);
        try {
            Cart cart = gioHangService.getOrCreateCart(user, session);
            Map<String, Integer> response = new HashMap<>();
            response.put("cartItemCount", gioHangService.getCartItemCount(cart));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy số lượng mục trong giỏ hàng: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/remove")
    @ResponseBody
    public Map<String, Object> removeFromCart(@RequestParam("cartItemId") int cartItemId) {
        Map<String, Object> response = new HashMap<>();
        try {
            gioHangService.removeFromCart(cartItemId);
            response.put("success", true);
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    @PostMapping("/update-quantity")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @AuthenticationPrincipal Object principal,
            HttpSession session,
            @RequestParam("productId") int productId,
            @RequestParam("donViTinhId") int donViTinhId,
            @RequestParam("quantity") int quantity) {
        User user = getUserFromPrincipal(principal);
        Map<String, Object> response = new HashMap<>();
        try {
            Cart cart = gioHangService.addToCart(user, session, productId, donViTinhId, quantity);
            response.put("success", true);
            response.put("price", gioHangService.getCartItems(user, session)
                    .stream()
                    .filter(item -> item.getBook().getId() == productId && item.getDonViGia().getId() == donViTinhId)
                    .findFirst().map(CartItem::getPrice).orElse(BigDecimal.ZERO));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCart(
            @AuthenticationPrincipal Object principal,
            HttpSession session) {
        User user = getUserFromPrincipal(principal);
        Map<String, Object> response = new HashMap<>();
        try {
            Cart cart = gioHangService.getOrCreateCart(user, session);
            gioHangService.getCartItems(user, session).forEach(item -> gioHangService.removeFromCart(item.getId()));
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public String viewCart(
            @AuthenticationPrincipal Object principal,
            HttpSession session, Model model) {
        User user = getUserFromPrincipal(principal);

        try {
            Cart cart = gioHangService.getOrCreateCart(user, session);
            List<CartItem> cartItems = gioHangService.getCartItems(user, session);

            BigDecimal cartTotal = BigDecimal.ZERO;
            BigDecimal directDiscount = BigDecimal.ZERO;
            BigDecimal voucherDiscount = BigDecimal.ZERO;

            for (CartItem item : cartItems) {
                BigDecimal unitPrice = BigDecimal.valueOf(item.getDonViGia().getGia());
                BigDecimal discountedPrice = item.getPrice();
                BigDecimal quantity = new BigDecimal(item.getQuantity());

                BigDecimal itemTotalPrice = discountedPrice.multiply(quantity);
                item.setItemTotalPrice(itemTotalPrice);

                if (!unitPrice.equals(discountedPrice)) {
                    BigDecimal discountAmount = (unitPrice.subtract(discountedPrice)).multiply(quantity);
                    directDiscount = directDiscount.add(discountAmount);
                }

                cartTotal = cartTotal.add(itemTotalPrice);
            }

            BigDecimal totalSavings = directDiscount.add(voucherDiscount);
            BigDecimal finalTotal = cartTotal;

            model.addAttribute("loggedInUser", user);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartItemCount", gioHangService.getCartItemCount(cart));
            model.addAttribute("cartTotal", cartTotal);
            model.addAttribute("directDiscount", directDiscount);
            model.addAttribute("voucherDiscount", voucherDiscount);
            model.addAttribute("totalSavings", totalSavings);
            model.addAttribute("finalTotal", finalTotal);
        } catch (Exception e) {
            logger.error("Lỗi khi tải giỏ hàng: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể tải giỏ hàng. Vui lòng thử lại sau.");
            model.addAttribute("cartItems", Collections.emptyList());
            model.addAttribute("cartItemCount", 0);
            model.addAttribute("cartTotal", BigDecimal.ZERO);
            model.addAttribute("directDiscount", BigDecimal.ZERO);
            model.addAttribute("voucherDiscount", BigDecimal.ZERO);
            model.addAttribute("totalSavings", BigDecimal.ZERO);
            model.addAttribute("finalTotal", BigDecimal.ZERO);
        }

        return "giohang";
    }

    // Endpoint mới để lấy user_id và session_id
    @GetMapping("/identifiers")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartIdentifiers(
            @AuthenticationPrincipal Object principal,
            HttpSession session) {
        User user = getUserFromPrincipal(principal);
        try {
            Map<String, Object> identifiers = gioHangService.getCartIdentifiers(user, session);
            return ResponseEntity.ok(identifiers);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin định danh giỏ hàng: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
}