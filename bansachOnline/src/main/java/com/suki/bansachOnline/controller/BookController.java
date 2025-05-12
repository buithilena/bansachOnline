

package com.suki.bansachOnline.controller;

import com.suki.bansachOnline.model.*;
import com.suki.bansachOnline.service.BookService;
import com.suki.bansachOnline.service.GioHangService;
import com.suki.bansachOnline.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final UserService userService;
    private final BookService bookService;
    private final GioHangService gioHangService; // Thêm GioHangService

    @GetMapping("/sanpham")
    public String bookDetail(@RequestParam("id") int bookId,
                             @AuthenticationPrincipal Object principal,
                             HttpSession session,
                             Model model) {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            return "redirect:/";
        }

        // Lấy thông tin người dùng
        User user = getUserFromPrincipal(principal);

        List<Book> flashSaleBooks = bookService.getFlashSaleBooks(25);
        model.addAttribute("flashSaleBooks", flashSaleBooks);

        // Khởi tạo hoặc lấy giỏ hàng
        Cart cart = gioHangService.getOrCreateCart(user, session);
        List<CartItem> cartItems = gioHangService.getCartItems(user, session);
        int cartItemCount = gioHangService.getCartItemCount(cart);

        // Thêm thông tin giỏ hàng vào model
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartItemCount", cartItemCount);
        model.addAttribute("loggedInUser", user);

        List<BookImage> images = bookService.getBookImages(bookId);
        String mainImageUrl = images.stream()
                .filter(BookImage::getIsMain)
                .findFirst()
                .map(BookImage::getImageUrl)
                .orElse("/images/placeholder.png");

        ChiTietSach chiTietSach = bookService.getChiTietSachByBookId(bookId);
        List<Book> relatedBooks = bookService.getRelatedBooks(bookId, 4);
        List<CauHoiLienQuan> cauHoiLienQuan = bookService.getCauHoiLienQuanByBookId(bookId);

        model.addAttribute("product", book);
        model.addAttribute("images", images);
        model.addAttribute("mainImageUrl", mainImageUrl);
        model.addAttribute("chiTietSanPham", chiTietSach);
        model.addAttribute("products", relatedBooks);
        model.addAttribute("cauHoiLienQuan", cauHoiLienQuan);

        return "sanpham";
    }


    @GetMapping("/update-price")
    @ResponseBody
    public Map<String, Object> updatePrice(@RequestParam("bookId") int bookId,
                                           @RequestParam("donViGiaId") int donViGiaId) {
        Map<String, Object> response = new HashMap<>();
        Book book = bookService.getBookById(bookId);
        DonViGia selectedDonViGia = bookService.getDonViGiaById(bookId, donViGiaId);

        if (selectedDonViGia != null && book != null) {
            double discountedPrice = selectedDonViGia.getGia() * (1 - selectedDonViGia.getDiscount() / 100.0);
            response.put("discountedPrice", discountedPrice);
            response.put("gia", selectedDonViGia.getGia());
            response.put("donVi", selectedDonViGia.getDonVi());
            response.put("hasDiscount", selectedDonViGia.getDiscount() > 0);
            response.put("discount", selectedDonViGia.getDiscount());
        } else {
            response.put("error", "Không tìm thấy đơn vị giá hoặc sách.");
        }
        return response;
    }

    @GetMapping("/products-by-doituong")
    @ResponseBody
    public Map<String, Object> getProductsByDoiTuong(@RequestParam("doiTuongId") int doiTuongId) {
        Map<String, Object> response = new HashMap<>();
        List<Book> books = bookService.getBooksByDoiTuongId(doiTuongId, 10);
        response.put("products", books);
        return response;
    }

    // Thêm endpoint tìm kiếm sách
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam("query") String query,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Book> books = bookService.searchBooks(query, categoryId, pageable);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private User getUserFromPrincipal(Object principal) {
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) principal;
            return userService.findByEmail(userDetails.getUsername()).orElse(null);
        } else if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;
            String providerId = oAuth2User.getAttribute("sub") != null ? oAuth2User.getAttribute("sub") : oAuth2User.getAttribute("id");
            String provider = oAuth2User.getAttribute("sub") != null ? "google" : "facebook";
            return "google".equals(provider) ? userService.findByGoogleId(providerId).orElse(null)
                    : userService.findByFacebookId(providerId).orElse(null);
        }
        return null;
    }

    // Endpoint lấy sách nổi bật
    @GetMapping("/featured-books")
    @ResponseBody
    public ResponseEntity<List<Book>> getFeaturedBooksByCategory(@RequestParam("danhMucId") Integer danhMucId,
                                                                 @RequestParam(defaultValue = "2") int limit) {
        List<Book> books = bookService.getFeaturedBooksByCategory(danhMucId, limit);
        return ResponseEntity.ok(books);
    }
}