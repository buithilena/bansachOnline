package com.suki.bansachOnline.controller;

import com.suki.bansachOnline.model.*;
import com.suki.bansachOnline.service.QuanlyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quanly")
public class QuanlyController {

    @Autowired
    private QuanlyService quanlyService;

    // Các endpoint hiện có cho tác giả, nhà xuất bản, danh mục, đối tượng không cần sửa
    @GetMapping("/authors")
    public ResponseEntity<List<TacGia>> getAllTacGia() {
        List<TacGia> tacGiaList = quanlyService.getAllTacGia();
        return ResponseEntity.ok(tacGiaList);
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<TacGia> getTacGiaById(@PathVariable Integer id) {
        Optional<TacGia> tacGia = quanlyService.getTacGiaById(id);
        return tacGia.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/authors")
    public ResponseEntity<TacGia> addTacGia(@RequestBody TacGia tacGia) {
        TacGia newTacGia = quanlyService.addTacGia(tacGia);
        return ResponseEntity.ok(newTacGia);
    }

    @PutMapping("/authors/{id}")
    public ResponseEntity<TacGia> updateTacGia(@PathVariable Integer id, @RequestBody TacGia tacGia) {
        TacGia updatedTacGia = quanlyService.updateTacGia(id, tacGia);
        if (updatedTacGia != null) {
            return ResponseEntity.ok(updatedTacGia);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteTacGia(@PathVariable Integer id) {
        boolean deleted = quanlyService.deleteTacGia(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/publishers")
    public ResponseEntity<List<NhaXuatBan>> getAllNhaXuatBan() {
        List<NhaXuatBan> nhaXuatBanList = quanlyService.getAllNhaXuatBan();
        return ResponseEntity.ok(nhaXuatBanList);
    }

    @GetMapping("/publishers/{id}")
    public ResponseEntity<NhaXuatBan> getNhaXuatBanById(@PathVariable Integer id) {
        Optional<NhaXuatBan> nhaXuatBan = quanlyService.getNhaXuatBanById(id);
        return nhaXuatBan.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/publishers")
    public ResponseEntity<NhaXuatBan> addNhaXuatBan(@RequestBody NhaXuatBan nhaXuatBan) {
        NhaXuatBan newNhaXuatBan = quanlyService.addNhaXuatBan(nhaXuatBan);
        return ResponseEntity.ok(newNhaXuatBan);
    }

    @PutMapping("/publishers/{id}")
    public ResponseEntity<NhaXuatBan> updateNhaXuatBan(@PathVariable Integer id, @RequestBody NhaXuatBan nhaXuatBan) {
        NhaXuatBan updatedNhaXuatBan = quanlyService.updateNhaXuatBan(id, nhaXuatBan);
        if (updatedNhaXuatBan != null) {
            return ResponseEntity.ok(updatedNhaXuatBan);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/publishers/{id}")
    public ResponseEntity<Void> deleteNhaXuatBan(@PathVariable Integer id) {
        boolean deleted = quanlyService.deleteNhaXuatBan(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<DanhMuc>> getAllDanhMuc() {
        List<DanhMuc> danhMucList = quanlyService.getAllDanhMuc();
        return ResponseEntity.ok(danhMucList);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<DanhMuc> getDanhMucById(@PathVariable Integer id) {
        Optional<DanhMuc> danhMuc = quanlyService.getDanhMucById(id);
        return danhMuc.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/categories")
    public ResponseEntity<DanhMuc> addDanhMuc(@RequestBody DanhMuc danhMuc) {
        DanhMuc newDanhMuc = quanlyService.addDanhMuc(danhMuc);
        return ResponseEntity.ok(newDanhMuc);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<DanhMuc> updateDanhMuc(@PathVariable Integer id, @RequestBody DanhMuc danhMuc) {
        DanhMuc updatedDanhMuc = quanlyService.updateDanhMuc(id, danhMuc);
        if (updatedDanhMuc != null) {
            return ResponseEntity.ok(updatedDanhMuc);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteDanhMuc(@PathVariable Integer id) {
        boolean deleted = quanlyService.deleteDanhMuc(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/doituongs")
    public ResponseEntity<List<DoiTuong>> getAllDoiTuong() {
        List<DoiTuong> doiTuongList = quanlyService.getAllDoiTuong();
        return ResponseEntity.ok(doiTuongList);
    }

    @GetMapping("/doituongs/{id}")
    public ResponseEntity<DoiTuong> getDoiTuongById(@PathVariable Integer id) {
        Optional<DoiTuong> doiTuong = quanlyService.getDoiTuongById(id);
        return doiTuong.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/doituongs")
    public ResponseEntity<DoiTuong> addDoiTuong(@RequestBody DoiTuong doiTuong) {
        DoiTuong newDoiTuong = quanlyService.addDoiTuong(doiTuong);
        return ResponseEntity.ok(newDoiTuong);
    }

    @PutMapping("/doituongs/{id}")
    public ResponseEntity<DoiTuong> updateDoiTuong(@PathVariable Integer id, @RequestBody DoiTuong doiTuong) {
        DoiTuong updatedDoiTuong = quanlyService.updateDoiTuong(id, doiTuong);
        if (updatedDoiTuong != null) {
            return ResponseEntity.ok(updatedDoiTuong);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/doituongs/{id}")
    public ResponseEntity<Void> deleteDoiTuong(@PathVariable Integer id) {
        boolean deleted = quanlyService.deleteDoiTuong(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = quanlyService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        Optional<Book> book = quanlyService.getBookById(id);
        return book.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Thêm sách mới - Nhận JSON
    @PostMapping("/books")
    public ResponseEntity<?> addBook(@RequestBody Map<String, Object> request) {
        try {
            Book book = mapToBook(request.get("book"));
            DonViGia donViGia = mapToDonViGia(request.get("donViGia"));
            List<BookImage> images = mapToBookImages(request.get("images"));

            Book newBook = quanlyService.addBook(book, images, donViGia);
            return ResponseEntity.ok(newBook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // Cập nhật sách - Nhận JSON
    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            Book book = mapToBook(request.get("book"));
            DonViGia donViGia = mapToDonViGia(request.get("donViGia"));
            List<BookImage> images = mapToBookImages(request.get("images"));

            Book updatedBook = quanlyService.updateBook(id, book, images, donViGia);
            if (updatedBook != null) {
                return ResponseEntity.ok(updatedBook);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        boolean deleted = quanlyService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Hàm ánh xạ Map thành Book
    private Book mapToBook(Object bookObj) {
        if (bookObj == null) {
            throw new IllegalArgumentException("Thông tin sách không được để trống");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> bookMap = (Map<String, Object>) bookObj;
        Book book = new Book();
        book.setTenSach((String) bookMap.get("tenSach"));
        book.setMoTaNgan((String) bookMap.get("moTaNgan"));
        book.setNamXuatBan(bookMap.get("namXuatBan") != null ? Integer.parseInt(bookMap.get("namXuatBan").toString()) : null);
        book.setSoTrang(bookMap.get("soTrang") != null ? Integer.parseInt(bookMap.get("soTrang").toString()) : null);
        book.setSoLuong(Integer.parseInt(bookMap.get("soLuong").toString()));

        if (bookMap.get("tacGia") != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> tacGiaMap = (Map<String, Object>) bookMap.get("tacGia");
            TacGia tacGia = new TacGia();
            tacGia.setId(Integer.parseInt(tacGiaMap.get("id").toString()));
            book.setTacGia(tacGia);
        }
        if (bookMap.get("danhMuc") != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> danhMucMap = (Map<String, Object>) bookMap.get("danhMuc");
            DanhMuc danhMuc = new DanhMuc();
            danhMuc.setId(Integer.parseInt(danhMucMap.get("id").toString()));
            book.setDanhMuc(danhMuc);
        }
        if (bookMap.get("nhaXuatBan") != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nhaXuatBanMap = (Map<String, Object>) bookMap.get("nhaXuatBan");
            NhaXuatBan nhaXuatBan = new NhaXuatBan();
            nhaXuatBan.setId(Integer.parseInt(nhaXuatBanMap.get("id").toString()));
            book.setNhaXuatBan(nhaXuatBan);
        }
        if (bookMap.get("doiTuong") != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> doiTuongMap = (Map<String, Object>) bookMap.get("doiTuong");
            DoiTuong doiTuong = new DoiTuong();
            doiTuong.setId(Integer.parseInt(doiTuongMap.get("id").toString()));
            book.setDoiTuong(doiTuong);
        }

        return book;
    }

    // Hàm ánh xạ Map thành DonViGia
    private DonViGia mapToDonViGia(Object donViGiaObj) {
        if (donViGiaObj == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> donViGiaMap = (Map<String, Object>) donViGiaObj;
        DonViGia donViGia = new DonViGia();
        donViGia.setDonVi((String) donViGiaMap.get("donVi"));
        donViGia.setGia(Double.parseDouble(donViGiaMap.get("gia").toString()));
        donViGia.setDiscount(donViGiaMap.get("discount") != null ? Double.parseDouble(donViGiaMap.get("discount").toString()) : 0.0);
        return donViGia;
    }

    // Hàm ánh xạ Map thành List<BookImage>
    private List<BookImage> mapToBookImages(Object imagesObj) {
        List<BookImage> images = new ArrayList<>();
        if (imagesObj == null) {
            return images;
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> imagesList = (List<Map<String, Object>>) imagesObj;
        for (Map<String, Object> imageMap : imagesList) {
            BookImage image = new BookImage();
            image.setImageUrl((String) imageMap.get("imageUrl"));
            image.setIsMain((Boolean) imageMap.get("isMain"));
            image.setImageOrder(Integer.parseInt(imageMap.get("imageOrder").toString()));
            images.add(image);
        }
        return images;
    }
}