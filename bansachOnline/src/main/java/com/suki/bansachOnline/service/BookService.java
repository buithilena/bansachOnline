package com.suki.bansachOnline.service;

import com.suki.bansachOnline.model.*;
import com.suki.bansachOnline.respository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final DoiTuongRepository doiTuongRepository;
    private final DonViGiaRepository donViGiaRepository;
    private final BookImageRepository bookImageRepository;
    private final ChiTietSachRepository chiTietSachRepository;
    private final CauHoiLienQuanRepository cauHoiLienQuanRepository;

    public Page<Book> getBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }



    public List<DoiTuong> getAllDoiTuong() {
        return doiTuongRepository.findAll();
    }

    public List<DoiTuong> getAllDanhMuc() {
        return doiTuongRepository.findAll();
    }


    public Book getBookById(int bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        return book.orElse(null);
    }

    public List<Book> getBooksByDoiTuongId(int doiTuongId, int limit) {
        List<Book> books = bookRepository.findByDoiTuongId(doiTuongId);
        return books.size() > limit ? books.subList(0, limit) : books;
    }

    public DonViGia getDonViGiaById(int bookId, int donViGiaId) {
        return donViGiaRepository.findByIdAndBookId(donViGiaId, bookId);
    }

    public List<BookImage> getBookImages(int bookId) {
        return bookImageRepository.findByBookId(bookId);
    }

    public ChiTietSach getChiTietSachByBookId(int bookId) {
        return chiTietSachRepository.findByBookId(bookId).orElse(null);
    }

    public List<CauHoiLienQuan> getCauHoiLienQuanByBookId(int bookId) {
        return cauHoiLienQuanRepository.findByBookId(bookId);
    }


    public List<Book> getRelatedBooks(int bookId, int limit) {
        List<Book> relatedBooks = bookRepository.findRelatedBooks(bookId);
        return relatedBooks.size() > limit ? relatedBooks.subList(0, limit) : relatedBooks;
    }
    public List<Book> getFlashSaleBooks(int minDiscountPercentage) {
        List<Book> flashSaleBooks = bookRepository.findAllWithDonViGias().stream()
                .filter(book -> book.getDonViGias() != null && !book.getDonViGias().isEmpty())
                .filter(book -> book.getDonViGias().stream().anyMatch(donViGia -> {
                    try {
                        Double discount = donViGia.getDiscount();
                        if (discount == null || donViGia.getGia() == null || donViGia.getGia() <= 0) {
                            return false;
                        }
                        // Giả sử discount là phần trăm
                        return discount >= minDiscountPercentage;
                    } catch (Exception e) {
                        System.out.println("Lỗi khi kiểm tra discount cho sách ID: " + book.getId() +
                                ", DonViGia ID: " + donViGia.getId() + ": " + e.getMessage());
                        return false;
                    }
                }))
                .collect(Collectors.toList());
        return flashSaleBooks;
    }

    // Phương thức hiện có


    // Thêm phương thức mới
    public Page<Book> getBooksByCategory(Integer categoryId, Pageable pageable) {
        return bookRepository.findByDanhMucId(categoryId, pageable);
    }



    // Cập nhật phương thức để lấy sách nổi bật có so_luong > 50
    public List<Book> getFeaturedBooksByCategory(Integer danhMucId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Book> books = bookRepository.findByDanhMucIdAndSoLuongGreaterThan50(danhMucId, pageable);
        return books.getContent();
    }







}