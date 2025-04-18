package com.suki.bansachOnline.service;

import com.suki.bansachOnline.model.DanhMuc;
import com.suki.bansachOnline.model.NhaXuatBan;
import com.suki.bansachOnline.model.TacGia;
import com.suki.bansachOnline.respository.DanhMucRepository;
import com.suki.bansachOnline.respository.NhaXuatBanRepository;
import com.suki.bansachOnline.respository.TacGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuanlyService {

    @Autowired
    private TacGiaRepository tacGiaRepository;

    // Lấy danh sách tất cả tác giả
    public List<TacGia> getAllTacGia() {
        return tacGiaRepository.findAll();
    }

    // Lấy tác giả theo ID
    public Optional<TacGia> getTacGiaById(Integer id) {
        return tacGiaRepository.findById(id);
    }

    // Thêm tác giả mới
    public TacGia addTacGia(TacGia tacGia) {
        return tacGiaRepository.save(tacGia);
    }

    // Cập nhật tác giả
    public TacGia updateTacGia(Integer id, TacGia tacGia) {
        if (tacGiaRepository.existsById(id)) {
            tacGia.setId(id);
            return tacGiaRepository.save(tacGia);
        }
        return null;
    }

    // Xóa tác giả
    public boolean deleteTacGia(Integer id) {
        if (tacGiaRepository.existsById(id)) {
            tacGiaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Autowired
    private NhaXuatBanRepository nhaXuatBanRepository;

    // Lấy danh sách tất cả nhà xuất bản
    public List<NhaXuatBan> getAllNhaXuatBan() {
        return nhaXuatBanRepository.findAll();
    }

    // Lấy nhà xuất bản theo ID
    public Optional<NhaXuatBan> getNhaXuatBanById(Integer id) {
        return nhaXuatBanRepository.findById(id);
    }

    // Thêm nhà xuất bản mới
    public NhaXuatBan addNhaXuatBan(NhaXuatBan nhaXuatBan) {
        return nhaXuatBanRepository.save(nhaXuatBan);
    }

    // Cập nhật nhà xuất bản
    public NhaXuatBan updateNhaXuatBan(Integer id, NhaXuatBan nhaXuatBan) {
        if (nhaXuatBanRepository.existsById(id)) {
            nhaXuatBan.setId(id);
            return nhaXuatBanRepository.save(nhaXuatBan);
        }
        return null;
    }

    // Xóa nhà xuất bản
    public boolean deleteNhaXuatBan(Integer id) {
        if (nhaXuatBanRepository.existsById(id)) {
            nhaXuatBanRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Autowired
    private DanhMucRepository danhMucRepository;
    public List<DanhMuc> getAllDanhMuc() {
        return danhMucRepository.findAll();
    }

    // Lấy danh mục theo ID
    public Optional<DanhMuc> getDanhMucById(Integer id) {
        return danhMucRepository.findById(id);
    }

    // Thêm danh mục mới
    public DanhMuc addDanhMuc(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    // Cập nhật danh mục
    public DanhMuc updateDanhMuc(Integer id, DanhMuc danhMuc) {
        if (danhMucRepository.existsById(id)) {
            danhMuc.setId(id);
            return danhMucRepository.save(danhMuc);
        }
        return null;
    }

    // Xóa danh mục
    public boolean deleteDanhMuc(Integer id) {
        if (danhMucRepository.existsById(id)) {
            danhMucRepository.deleteById(id);
            return true;
        }
        return false;
    }
}