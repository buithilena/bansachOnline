package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.Book;
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
}