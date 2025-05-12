

package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookImageRepository extends JpaRepository<BookImage, Integer> {
    @Query("SELECT bi FROM BookImage bi WHERE bi.book.id = :bookId")
    List<BookImage> findByBookId(int bookId);


    void deleteByBookId(Integer bookId);

}