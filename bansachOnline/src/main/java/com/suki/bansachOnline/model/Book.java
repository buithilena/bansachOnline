package com.suki.bansachOnline.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "nam_xuat_ban")
    private Integer namXuatBan;

    @Column(name = "so_trang")
    private Integer soTrang;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "ten_sach", nullable = false)
    private String tenSach;

    @Column(name = "thuong_hieu")
    private String thuongHieu;

    @Column(name = "ngon_ngu")
    private String ngonNgu;

    @ManyToOne
    @JoinColumn(name = "nhaxuatban_id")
    private NhaXuatBan nhaXuatBan;

    @ManyToOne
    @JoinColumn(name = "tacgia_id")
    private TacGia tacGia;

    @ManyToOne
    @JoinColumn(name = "danhmuc_id")
    private DanhMuc danhMuc;

    @ManyToOne
    @JoinColumn(name = "doi_tuong_id")
    private DoiTuong doiTuong;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<BookImage> bookImages = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<DonViGia> donViGias = new HashSet<>();

    public boolean hasDiscount() {
        return donViGias != null && !donViGias.isEmpty() && donViGias.iterator().next().getDiscount() > 0;
    }

    public DonViGia getDefaultDonViTinh() {
        return donViGias != null && !donViGias.isEmpty() ? donViGias.iterator().next() : null;
    }

    public String getMainImageUrl() {
        return bookImages != null && !bookImages.isEmpty() ?
                bookImages.stream()
                        .filter(BookImage::getIsMain)
                        .findFirst()
                        .map(BookImage::getImageUrl)
                        .orElse("/images/placeholder.png")
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