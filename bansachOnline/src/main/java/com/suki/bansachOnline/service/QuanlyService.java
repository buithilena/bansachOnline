package com.suki.bansachOnline.service;

import com.suki.bansachOnline.model.DoiTuong;
import com.suki.bansachOnline.respository.DoiTuongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuanlyService {

    @Autowired
    private DoiTuongRepository doiTuongRepository;

    public List<DoiTuong> getAllDoiTuong() {
        return doiTuongRepository.findAll();
    }
}