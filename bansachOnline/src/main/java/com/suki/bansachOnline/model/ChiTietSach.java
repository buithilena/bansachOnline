package com.suki.bansachOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "mo_ta_chi_tiet")
    private String moTaChiTiet;

    @Column(name = "danh_gia_ngan")
    private String danhGiaNgan;

    @Column(name = "thong_tin_them")
    private String thongTinThem;
}