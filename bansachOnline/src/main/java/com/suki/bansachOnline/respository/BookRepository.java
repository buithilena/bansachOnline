package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByDoiTuongId(int doiTuongId);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.donViGias WHERE b.danhMuc.id = (SELECT b2.danhMuc.id FROM Book b2 WHERE b2.id = :bookId) AND b.id != :bookId ORDER BY RAND()")
    List<Book> findRelatedBooks(@Param("bookId") int bookId);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.donViGias")
    List<Book> findAllWithDonViGias();

    List<Book> findByDoiTuongId(Integer doiTuongId);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.donViGias WHERE b.danhMuc.id = :danhMucId AND b.id != :bookId ORDER BY RAND()")
    List<Book> findRelatedBooks(@Param("bookId") Integer bookId, @Param("danhMucId") Integer danhMucId);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.donViGias LEFT JOIN FETCH b.bookImages")
    List<Book> findAllWithDetails();

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.donViGias LEFT JOIN FETCH b.bookImages WHERE b.id = :id")
    Book findByIdWithDetails(@Param("id") Integer id);

    Page<Book> findByDanhMucId(Integer danhMucId, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.danhMuc.id = :danhMucId AND b.soLuong > 50")
    Page<Book> findByDanhMucIdAndSoLuongGreaterThan50(@Param("danhMucId") Integer danhMucId, Pageable pageable);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.donViGias LEFT JOIN FETCH b.bookImages WHERE LOWER(b.tenSach) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Book> findByTenSachContainingIgnoreCase(@Param("query") String query, Pageable pageable);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.donViGias LEFT JOIN FETCH b.bookImages WHERE LOWER(b.tenSach) LIKE LOWER(CONCAT('%', :query, '%')) AND b.danhMuc.id = :danhMucId")
    Page<Book> findByTenSachContainingIgnoreCaseAndDanhMucId(@Param("query") String query, @Param("danhMucId") Integer danhMucId, Pageable pageable);

    // Đếm số lượng sản phẩm theo danh mục
    @Query("SELECT d.tenDanhMuc, COUNT(b) FROM Book b JOIN b.danhMuc d GROUP BY d.id, d.tenDanhMuc")
    List<Object[]> countBooksByCategory();
}