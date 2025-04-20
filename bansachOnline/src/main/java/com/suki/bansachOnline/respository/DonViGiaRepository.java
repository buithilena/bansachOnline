package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.DonViGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DonViGiaRepository extends JpaRepository<DonViGia, Integer> {
    @Query("SELECT d FROM DonViGia d WHERE d.id = :donViGiaId AND d.book.id = :bookId")
    DonViGia findByIdAndBookId(int donViGiaId, int bookId);

    void deleteByBookId(Integer bookId);
}