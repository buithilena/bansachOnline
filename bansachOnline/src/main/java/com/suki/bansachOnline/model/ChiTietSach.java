package com.suki.bansachOnline.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chitiet_sach")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietSach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "tacpham", columnDefinition = "TEXT")
    private String tacpham;

    @Column(name = "tacgia", columnDefinition = "TEXT")
    private String tacgia;

    @ManyToOne
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private Book book;
}