package com.suki.bansachOnline.service;

import com.suki.bansachOnline.model.*;
import com.suki.bansachOnline.respository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Book getBookById(int bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        return book.orElse(null);
    }

    public List<Book> getBooksByDoiTuongId(int doiTuongId) {
        return bookRepository.findByDoiTuongId(doiTuongId);
    }

    public DonViGia getDonViGiaById(int bookId, int donViGiaId) {
        return donViGiaRepository.findByIdAndBookId(donViGiaId, bookId);
    }

    public List<BookImage> getBookImages(int bookId) {
        return bookImageRepository.findByBookId(bookId);
    }

    public ChiTietSach getChiTietSachByBookId(int bookId) {
        return chiTietSachRepository.findByBookId(bookId);
    }

    public List<CauHoiLienQuan> getCauHoiLienQuanByBookId(int bookId) {
        return cauHoiLienQuanRepository.findByBookId(bookId);
    }

    public List<Book> getRelatedBooks(int bookId, int limit) {
        return bookRepository.findRelatedBooks(bookId, limit);
    }
}