package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByDoiTuongId(int doiTuongId);

    @Query("SELECT b FROM Book b WHERE b.danhMuc.id = (SELECT b2.danhMuc.id FROM Book b2 WHERE b2.id = :bookId) AND b.id != :bookId ORDER BY RAND() LIMIT :limit")
    List<Book> findRelatedBooks(int bookId, int limit);
}