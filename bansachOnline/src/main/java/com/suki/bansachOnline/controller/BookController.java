package com.suki.bansachOnline.controller;

import com.suki.bansachOnline.model.*;
import com.suki.bansachOnline.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/sanpham")
    public String bookDetail(@RequestParam("id") int bookId, Model model) {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            return "redirect:/";
        }

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
            // Tính giá sau khi giảm dựa trên phần trăm
            double discountedPrice = selectedDonViGia.getGia() * (1 - selectedDonViGia.getDiscount() / 100.0);
            response.put("discountedPrice", discountedPrice);
            response.put("gia", selectedDonViGia.getGia());
            response.put("donVi", selectedDonViGia.getDonVi());
            response.put("hasDiscount", selectedDonViGia.getDiscount() > 0);
            response.put("discount", selectedDonViGia.getDiscount()); // Trả về phần trăm giảm giá
        } else {
            response.put("error", "Không tìm thấy đơn vị giá hoặc sách.");
        }
        return response;
    }

    @GetMapping("/products-by-doituong")
    @ResponseBody
    public Map<String, Object> getProductsByDoiTuong(@RequestParam("doiTuongId") int doiTuongId) {
        Map<String, Object> response = new HashMap<>();
        List<Book> books = bookService.getBooksByDoiTuongId(doiTuongId, 10); // Tăng giới hạn lên 10 sách
        response.put("products", books);
        return response;
    }
}