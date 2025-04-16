package com.suki.bansachOnline.controller;

import com.suki.bansachOnline.model.DoiTuong;
import com.suki.bansachOnline.service.QuanlyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/quanly")
public class QuanlyController {

    @Autowired
    private QuanlyService quanlyService;

    @GetMapping("/doituong")
    public ResponseEntity<List<DoiTuong>> getAllDoiTuong() {
        List<DoiTuong> doiTuongList = quanlyService.getAllDoiTuong();
        return ResponseEntity.ok(doiTuongList);
    }
}