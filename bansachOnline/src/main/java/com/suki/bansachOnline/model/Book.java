package com.suki.bansachOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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

    @Column(name = "ten_sach", nullable = false)
    private String tenSach;

    @Column(name = "tac_gia")
    private String tacGia;

    @Column(name = "thuong_hieu")
    private String thuongHieu;

    @Column(name = "ngon_ngu")
    private String ngonNgu;

    @Column(name = "danhmuc_id")
    private Integer danhmucId;

    @Column(name = "doi_tuong_id")
    private Integer doiTuongId;

    @ManyToOne
    @JoinColumn(name = "danhmuc_id", insertable = false, updatable = false)
    private DanhMuc danhMuc;

    @ManyToOne
    @JoinColumn(name = "doi_tuong_id", insertable = false, updatable = false)
    private DoiTuong doiTuong;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<DonViGia> donViGias;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookImage> bookImages;

    public boolean hasDiscount() {
        return donViGias != null && !donViGias.isEmpty() && donViGias.get(0).getDiscount() > 0;
    }

    public DonViGia getDefaultDonViTinh() {
        return donViGias != null && !donViGias.isEmpty() ? donViGias.get(0) : null;
    }

    public String getMainImageUrl() {
        return bookImages != null && !bookImages.isEmpty() ?
                bookImages.stream().filter(BookImage::getIsMain).findFirst().map(BookImage::getImageUrl).orElse("/images/placeholder.png")
                : "/images/placeholder.png";
    }

    public BigDecimal getDiscountedPrice() {
        DonViGia defaultDvt = getDefaultDonViTinh();
        if (defaultDvt == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal basePrice = BigDecimal.valueOf(defaultDvt.getGia());
        double discount = defaultDvt.getDiscount();
        if (discount > 0) {
            return basePrice.multiply(BigDecimal.valueOf(1 - discount / 100));
        }
        return basePrice;
    }
}