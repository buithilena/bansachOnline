package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.CauHoiLienQuan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CauHoiLienQuanRepository extends JpaRepository<CauHoiLienQuan, Integer> {
    @Query("SELECT ch FROM CauHoiLienQuan ch WHERE ch.book.id = :bookId")
    List<CauHoiLienQuan> findByBookId(int bookId);
}