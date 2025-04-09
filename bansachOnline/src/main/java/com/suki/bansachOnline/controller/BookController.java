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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // Hiển thị chi tiết sách
    @GetMapping("/sanpham")
    public String bookDetail(@RequestParam("id") int bookId, Model model) {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            return "redirect:/";
        }

        List<BookImage> images = bookService.getBookImages(bookId);
        ChiTietSach chiTietSach = bookService.getChiTietSachByBookId(bookId);
        List<CauHoiLienQuan> cauHoiLienQuan = bookService.getCauHoiLienQuanByBookId(bookId);
        List<Book> relatedBooks = bookService.getRelatedBooks(bookId, 6);

        model.addAttribute("book", book);
        model.addAttribute("images", images);
        model.addAttribute("chiTietSach", chiTietSach);
        model.addAttribute("cauHoiLienQuan", cauHoiLienQuan);
        model.addAttribute("relatedBooks", relatedBooks);

        return "sanpham";
    }

    // Lấy danh sách sách theo đối tượng độc giả
    @GetMapping("/books-by-doituong")
    public String getBooksByDoiTuong(@RequestParam("doiTuongId") int doiTuongId, Model model) {
        List<Book> books = bookService.getBooksByDoiTuongId(doiTuongId);
        model.addAttribute("books", books);
        return "trangchu :: book-by-doituong-fragment";
    }

    // Cập nhật giá sách dựa trên đơn vị giá
    @GetMapping("/update-price")
    @ResponseBody
    public Map<String, Object> updatePrice(@RequestParam("bookId") int bookId,
                                           @RequestParam("donViGiaId") int donViGiaId) {
        Map<String, Object> response = new HashMap<>();
        Book book = bookService.getBookById(bookId);
        DonViGia selectedDonViGia = bookService.getDonViGiaById(bookId, donViGiaId);

        if (selectedDonViGia != null && book != null) {
            BigDecimal discountedPrice = BigDecimal.valueOf(selectedDonViGia.getGia() - selectedDonViGia.getDiscount());
            response.put("discountedPrice", discountedPrice);
            response.put("gia", selectedDonViGia.getGia());
            response.put("donViGia", selectedDonViGia.getDonVi());
            response.put("hasDiscount", selectedDonViGia.getDiscount() > 0);
            response.put("discount", selectedDonViGia.getDiscount() != null ? selectedDonViGia.getDiscount() : BigDecimal.ZERO);
        } else {
            response.put("error", "Không tìm thấy đơn vị giá hoặc sách.");
        }
        return response;
    }
}