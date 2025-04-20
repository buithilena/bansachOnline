package com.suki.bansachOnline.service;

import com.suki.bansachOnline.model.*;
import com.suki.bansachOnline.respository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuanlyService {

    @Autowired
    private TacGiaRepository tacGiaRepository;

    @Autowired
    private NhaXuatBanRepository nhaXuatBanRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookImageRepository bookImageRepository;

    @Autowired
    private DonViGiaRepository donViGiaRepository;

    @Autowired
    private DoiTuongRepository doiTuongRepository;

    // Các phương thức hiện có cho tác giả, nhà xuất bản, danh mục, đối tượng không cần sửa
    public List<TacGia> getAllTacGia() {
        return tacGiaRepository.findAll();
    }

    public Optional<TacGia> getTacGiaById(Integer id) {
        return tacGiaRepository.findById(id);
    }

    public TacGia addTacGia(TacGia tacGia) {
        return tacGiaRepository.save(tacGia);
    }

    public TacGia updateTacGia(Integer id, TacGia tacGia) {
        if (tacGiaRepository.existsById(id)) {
            tacGia.setId(id);
            return tacGiaRepository.save(tacGia);
        }
        return null;
    }

    public boolean deleteTacGia(Integer id) {
        if (tacGiaRepository.existsById(id)) {
            tacGiaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<NhaXuatBan> getAllNhaXuatBan() {
        return nhaXuatBanRepository.findAll();
    }

    public Optional<NhaXuatBan> getNhaXuatBanById(Integer id) {
        return nhaXuatBanRepository.findById(id);
    }

    public NhaXuatBan addNhaXuatBan(NhaXuatBan nhaXuatBan) {
        return nhaXuatBanRepository.save(nhaXuatBan);
    }

    public NhaXuatBan updateNhaXuatBan(Integer id, NhaXuatBan nhaXuatBan) {
        if (nhaXuatBanRepository.existsById(id)) {
            nhaXuatBan.setId(id);
            return nhaXuatBanRepository.save(nhaXuatBan);
        }
        return null;
    }

    public boolean deleteNhaXuatBan(Integer id) {
        if (nhaXuatBanRepository.existsById(id)) {
            nhaXuatBanRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<DanhMuc> getAllDanhMuc() {
        return danhMucRepository.findAll();
    }

    public Optional<DanhMuc> getDanhMucById(Integer id) {
        return danhMucRepository.findById(id);
    }

    public DanhMuc addDanhMuc(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    public DanhMuc updateDanhMuc(Integer id, DanhMuc danhMuc) {
        if (danhMucRepository.existsById(id)) {
            danhMuc.setId(id);
            return danhMucRepository.save(danhMuc);
        }
        return null;
    }

    public boolean deleteDanhMuc(Integer id) {
        if (danhMucRepository.existsById(id)) {
            danhMucRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<DoiTuong> getAllDoiTuong() {
        return doiTuongRepository.findAll();
    }

    public Optional<DoiTuong> getDoiTuongById(Integer id) {
        return doiTuongRepository.findById(id);
    }

    public DoiTuong addDoiTuong(DoiTuong doiTuong) {
        return doiTuongRepository.save(doiTuong);
    }

    public DoiTuong updateDoiTuong(Integer id, DoiTuong doiTuong) {
        if (doiTuongRepository.existsById(id)) {
            doiTuong.setId(id);
            return doiTuongRepository.save(doiTuong);
        }
        return null;
    }

    public boolean deleteDoiTuong(Integer id) {
        if (doiTuongRepository.existsById(id)) {
            doiTuongRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAllWithDetails();
    }

    public Optional<Book> getBookById(Integer id) {
        return Optional.ofNullable(bookRepository.findByIdWithDetails(id));
    }

    // Thêm sách mới
    @Transactional
    public Book addBook(Book book, List<BookImage> bookImages, DonViGia donViGia) {
        // Lấy các thực thể liên quan từ cơ sở dữ liệu
        if (book.getTacGia() != null && book.getTacGia().getId() != null) {
            TacGia tacGia = tacGiaRepository.findById(book.getTacGia().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Tác giả không tồn tại"));
            book.setTacGia(tacGia);
        }
        if (book.getDanhMuc() != null && book.getDanhMuc().getId() != null) {
            DanhMuc danhMuc = danhMucRepository.findById(book.getDanhMuc().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
            book.setDanhMuc(danhMuc);
        }
        if (book.getNhaXuatBan() != null && book.getNhaXuatBan().getId() != null) {
            NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findById(book.getNhaXuatBan().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Nhà xuất bản không tồn tại"));
            book.setNhaXuatBan(nhaXuatBan);
        }
        if (book.getDoiTuong() != null && book.getDoiTuong().getId() != null) {
            DoiTuong doiTuong = doiTuongRepository.findById(book.getDoiTuong().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Đối tượng không tồn tại"));
            book.setDoiTuong(doiTuong);
        }

        // Lưu sách
        Book savedBook = bookRepository.save(book);

        // Lưu đơn vị giá
        if (donViGia != null) {
            donViGia.setBook(savedBook);
            donViGiaRepository.save(donViGia);
        }

        // Lưu hình ảnh
        if (bookImages != null && !bookImages.isEmpty()) {
            for (BookImage bookImage : bookImages) {
                bookImage.setBook(savedBook);
                bookImageRepository.save(bookImage);
            }
        }

        return savedBook;
    }

    // Cập nhật sách
    @Transactional
    public Book updateBook(Integer id, Book book, List<BookImage> bookImages, DonViGia donViGia) {
        if (!bookRepository.existsById(id)) {
            return null;
        }

        // Lấy sách hiện tại từ cơ sở dữ liệu
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại"));

        // Cập nhật các thuộc tính của sách
        existingBook.setTenSach(book.getTenSach());
        existingBook.setMoTaNgan(book.getMoTaNgan());
        existingBook.setNamXuatBan(book.getNamXuatBan());
        existingBook.setSoTrang(book.getSoTrang());
        existingBook.setSoLuong(book.getSoLuong());

        // Lấy các thực thể liên quan từ cơ sở dữ liệu
        if (book.getTacGia() != null && book.getTacGia().getId() != null) {
            TacGia tacGia = tacGiaRepository.findById(book.getTacGia().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Tác giả không tồn tại"));
            existingBook.setTacGia(tacGia);
        } else {
            existingBook.setTacGia(null);
        }
        if (book.getDanhMuc() != null && book.getDanhMuc().getId() != null) {
            DanhMuc danhMuc = danhMucRepository.findById(book.getDanhMuc().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
            existingBook.setDanhMuc(danhMuc);
        } else {
            existingBook.setDanhMuc(null);
        }
        if (book.getNhaXuatBan() != null && book.getNhaXuatBan().getId() != null) {
            NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findById(book.getNhaXuatBan().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Nhà xuất bản không tồn tại"));
            existingBook.setNhaXuatBan(nhaXuatBan);
        } else {
            existingBook.setNhaXuatBan(null);
        }
        if (book.getDoiTuong() != null && book.getDoiTuong().getId() != null) {
            DoiTuong doiTuong = doiTuongRepository.findById(book.getDoiTuong().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Đối tượng không tồn tại"));
            existingBook.setDoiTuong(doiTuong);
        } else {
            existingBook.setDoiTuong(null);
        }

        // Lưu sách đã cập nhật
        Book updatedBook = bookRepository.save(existingBook);

        // Xóa đơn vị giá cũ và lưu đơn vị giá mới
        if (donViGia != null) {
            donViGiaRepository.deleteByBookId(id);
            donViGia.setBook(updatedBook);
            donViGiaRepository.save(donViGia);
        }

        // Xóa hình ảnh cũ và lưu hình ảnh mới
        if (bookImages != null) {
            bookImageRepository.deleteByBookId(id);
            for (BookImage bookImage : bookImages) {
                bookImage.setBook(updatedBook);
                bookImageRepository.save(bookImage);
            }
        }

        return updatedBook;
    }

    public boolean deleteBook(Integer id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
}