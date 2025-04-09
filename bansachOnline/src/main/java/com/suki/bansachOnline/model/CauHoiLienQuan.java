package com.suki.bansachOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cauhoilienquan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CauHoiLienQuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "cau_hoi", nullable = false)
    private String cauHoi;

    @Column(name = "cau_tra_loi", nullable = false)
    private String cauTraLoi;
}