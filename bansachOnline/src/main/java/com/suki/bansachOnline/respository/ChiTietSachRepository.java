package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.ChiTietSach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChiTietSachRepository extends JpaRepository<ChiTietSach, Integer> {
//    @Query("SELECT cts FROM ChiTietSach cts WHERE cts.book.id = :bookId")
//    ChiTietSach findByBookId(int bookId);


    Optional<ChiTietSach> findByBookId(int bookId);
}