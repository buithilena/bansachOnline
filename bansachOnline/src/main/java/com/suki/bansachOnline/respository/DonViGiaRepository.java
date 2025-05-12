

package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.DonViGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonViGiaRepository extends JpaRepository<DonViGia, Integer> {
    @Query("SELECT d FROM DonViGia d WHERE d.id = :donViGiaId AND d.book.id = :bookId")
    DonViGia findByIdAndBookId(int donViGiaId, int bookId);

    void deleteByBookId(Integer bookId);

    @Query("SELECT d FROM DonViGia d WHERE d.book.id = :bookId")
    List<DonViGia> findByBookId(@Param("bookId") int bookId);
}