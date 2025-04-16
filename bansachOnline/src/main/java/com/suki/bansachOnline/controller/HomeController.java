package com.suki.bansachOnline.controller;

import com.suki.bansachOnline.model.*;
import com.suki.bansachOnline.service.BookService;
import com.suki.bansachOnline.service.GioHangService;
import com.suki.bansachOnline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private GioHangService gioHangService; // Thêm GioHangService

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @AuthenticationPrincipal OAuth2User oAuth2User,
                       HttpSession session) {
        User loggedInUser = null;
        if (oAuth2User != null) {
            String providerId = oAuth2User.getAttribute("sub") != null ? oAuth2User.getAttribute("sub") : oAuth2User.getAttribute("id");
            String provider = oAuth2User.getAttribute("sub") != null ? "google" : "facebook";
            Optional<User> userOpt = "google".equals(provider) ? userService.findByGoogleId(providerId) : userService.findByFacebookId(providerId);
            if (userOpt.isPresent()) {
                loggedInUser = userOpt.get();
                model.addAttribute("loggedInUser", loggedInUser);
                if ("ADMIN".equals(loggedInUser.getRole())) {
                    return "quanly"; // ADMIN vào trang quanly.html
                }
            }
        }

        // Khởi tạo hoặc lấy giỏ hàng
        Cart cart = gioHangService.getOrCreateCart(loggedInUser, session);
        List<CartItem> cartItems = gioHangService.getCartItems(loggedInUser, session);
        int cartItemCount = gioHangService.getCartItemCount(cart);

        // Thêm thông tin giỏ hàng vào model để sử dụng trong header
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartItemCount", cartItemCount);

        // Logic sách và đối tượng
        Pageable pageable = PageRequest.of(page, 12); // Hiển thị 12 sách mỗi trang
        Page<Book> bookPage = bookService.getBooks(pageable);
        List<DoiTuong> doiTuongList = bookService.getAllDoiTuong();

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("doiTuongList", doiTuongList);

        return "trangchu"; // USER hoặc không đăng nhập vào trang chủ
    }

    @GetMapping("/quanly")
    public String quanLy(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @AuthenticationPrincipal OAuth2User oAuth2User,
                         HttpSession session) {
        if (oAuth2User != null) {
            String providerId = oAuth2User.getAttribute("sub") != null ? oAuth2User.getAttribute("sub") : oAuth2User.getAttribute("id");
            String provider = oAuth2User.getAttribute("sub") != null ? "google" : "facebook";
            Optional<User> userOpt = "google".equals(provider) ? userService.findByGoogleId(providerId) : userService.findByFacebookId(providerId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (!"ADMIN".equals(user.getRole())) {
                    return "redirect:/"; // Nếu không phải ADMIN, chuyển về trang chủ
                }
                model.addAttribute("loggedInUser", user);

                // Khởi tạo hoặc lấy giỏ hàng
                Cart cart = gioHangService.getOrCreateCart(user, session);
                List<CartItem> cartItems = gioHangService.getCartItems(user, session);
                int cartItemCount = gioHangService.getCartItemCount(cart);

                // Thêm thông tin giỏ hàng vào model
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("cartItemCount", cartItemCount);

                // Logic sách và đối tượng
                Pageable pageable = PageRequest.of(page, 12); // Hiển thị 12 sách mỗi trang
                Page<Book> bookPage = bookService.getBooks(pageable);
                List<DoiTuong> doiTuongList = bookService.getAllDoiTuong();

                model.addAttribute("books", bookPage.getContent());
                model.addAttribute("totalPages", bookPage.getTotalPages());
                model.addAttribute("currentPage", page);
                model.addAttribute("doiTuongList", doiTuongList);

                return "quanly"; // Trả về quanly.html
            } else {
                return "redirect:/"; // Nếu không tìm thấy user, chuyển về trang chủ
            }
        } else {
            return "redirect:/"; // Nếu chưa đăng nhập, chuyển về trang chủ
        }
    }
}