package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.DoiTuong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoiTuongRepository extends JpaRepository<DoiTuong, Integer> {
}