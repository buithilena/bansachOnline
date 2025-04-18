package com.suki.bansachOnline.controller;

import com.suki.bansachOnline.model.DanhMuc;
import com.suki.bansachOnline.model.NhaXuatBan;
import com.suki.bansachOnline.model.TacGia;
import com.suki.bansachOnline.service.QuanlyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quanly")
public class QuanlyController {

    @Autowired
    private QuanlyService quanlyService;

    // Lấy danh sách tất cả tác giả
    @GetMapping("/authors")
    public ResponseEntity<List<TacGia>> getAllTacGia() {
        List<TacGia> tacGiaList = quanlyService.getAllTacGia();
        return ResponseEntity.ok(tacGiaList);
    }

    // Lấy tác giả theo ID
    @GetMapping("/authors/{id}")
    public ResponseEntity<TacGia> getTacGiaById(@PathVariable Integer id) {
        Optional<TacGia> tacGia = quanlyService.getTacGiaById(id);
        return tacGia.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Thêm tác giả mới
    @PostMapping("/authors")
    public ResponseEntity<TacGia> addTacGia(@RequestBody TacGia tacGia) {
        TacGia newTacGia = quanlyService.addTacGia(tacGia);
        return ResponseEntity.ok(newTacGia);
    }

    // Cập nhật tác giả
    @PutMapping("/authors/{id}")
    public ResponseEntity<TacGia> updateTacGia(@PathVariable Integer id, @RequestBody TacGia tacGia) {
        TacGia updatedTacGia = quanlyService.updateTacGia(id, tacGia);
        if (updatedTacGia != null) {
            return ResponseEntity.ok(updatedTacGia);
        }
        return ResponseEntity.notFound().build();
    }

    // Xóa tác giả
    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteTacGia(@PathVariable Integer id) {
        boolean deleted = quanlyService.deleteTacGia(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


    // Lấy danh sách tất cả nhà xuất bản
    @GetMapping("/publishers")
    public ResponseEntity<List<NhaXuatBan>> getAllNhaXuatBan() {
        List<NhaXuatBan> nhaXuatBanList = quanlyService.getAllNhaXuatBan();
        return ResponseEntity.ok(nhaXuatBanList);
    }

    // Lấy nhà xuất bản theo ID
    @GetMapping("/publishers/{id}")
    public ResponseEntity<NhaXuatBan> getNhaXuatBanById(@PathVariable Integer id) {
        Optional<NhaXuatBan> nhaXuatBan = quanlyService.getNhaXuatBanById(id);
        return nhaXuatBan.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Thêm nhà xuất bản mới
    @PostMapping("/publishers")
    public ResponseEntity<NhaXuatBan> addNhaXuatBan(@RequestBody NhaXuatBan nhaXuatBan) {
        NhaXuatBan newNhaXuatBan = quanlyService.addNhaXuatBan(nhaXuatBan);
        return ResponseEntity.ok(newNhaXuatBan);
    }

    // Cập nhật nhà xuất bản
    @PutMapping("/publishers/{id}")
    public ResponseEntity<NhaXuatBan> updateNhaXuatBan(@PathVariable Integer id, @RequestBody NhaXuatBan nhaXuatBan) {
        NhaXuatBan updatedNhaXuatBan = quanlyService.updateNhaXuatBan(id, nhaXuatBan);
        if (updatedNhaXuatBan != null) {
            return ResponseEntity.ok(updatedNhaXuatBan);
        }
        return ResponseEntity.notFound().build();
    }

    // Xóa nhà xuất bản
    @DeleteMapping("/publishers/{id}")
    public ResponseEntity<Void> deleteNhaXuatBan(@PathVariable Integer id) {
        boolean deleted = quanlyService.deleteNhaXuatBan(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Lấy danh sách tất cả danh mục
    @GetMapping("/categories")
    public ResponseEntity<List<DanhMuc>> getAllDanhMuc() {
        List<DanhMuc> danhMucList = quanlyService.getAllDanhMuc();
        return ResponseEntity.ok(danhMucList);
    }

    // Lấy danh mục theo ID
    @GetMapping("/categories/{id}")
    public ResponseEntity<DanhMuc> getDanhMucById(@PathVariable Integer id) {
        Optional<DanhMuc> danhMuc = quanlyService.getDanhMucById(id);
        return danhMuc.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Thêm danh mục mới
    @PostMapping("/categories")
    public ResponseEntity<DanhMuc> addDanhMuc(@RequestBody DanhMuc danhMuc) {
        DanhMuc newDanhMuc = quanlyService.addDanhMuc(danhMuc);
        return ResponseEntity.ok(newDanhMuc);
    }

    // Cập nhật danh mục
    @PutMapping("/categories/{id}")
    public ResponseEntity<DanhMuc> updateDanhMuc(@PathVariable Integer id, @RequestBody DanhMuc danhMuc) {
        DanhMuc updatedDanhMuc = quanlyService.updateDanhMuc(id, danhMuc);
        if (updatedDanhMuc != null) {
            return ResponseEntity.ok(updatedDanhMuc);
        }
        return ResponseEntity.notFound().build();
    }

    // Xóa danh mục
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteDanhMuc(@PathVariable Integer id) {
        boolean deleted = quanlyService.deleteDanhMuc(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}