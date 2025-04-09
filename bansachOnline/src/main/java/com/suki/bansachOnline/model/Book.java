package com.suki.bansachOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "mo_ta_ngan")
    private String moTaNgan;

    @Column(name = "nha_xuat_ban")
    private String nhaXuatBan;

    @Column(name = "nam_xuat_ban")
    private Integer namXuatBan;

    @Column(name = "so_trang")
    private Integer soTrang;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "ten_sach")
    private String tenSach;

    @Column(name = "tac_gia")
    private String tacGia;

    @Column(name = "thuong_hieu")
    private String thuongHieu;

    @Column(name = "ngon_ngu")
    private String ngonNgu;

    @ManyToOne
    @JoinColumn(name = "danhmuc_id")
    private DanhMuc danhMuc;

    @ManyToOne
    @JoinColumn(name = "doi_tuong_id")
    private DoiTuong doiTuong;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookImage> bookImages;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonViGia> donViGias;
}