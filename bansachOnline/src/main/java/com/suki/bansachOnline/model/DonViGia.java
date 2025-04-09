package com.suki.bansachOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "don_vi_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonViGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "don_vi")
    private String donVi;

    @Column(name = "gia", nullable = false)
    private Double gia;

    @Column(name = "discount")
    private Double discount = 0.0;

    @Column(name = "ghi_chu")
    private String ghiChu;
}