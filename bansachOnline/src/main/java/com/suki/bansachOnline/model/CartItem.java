package com.suki.bansachOnline.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "don_vi_gia_id")
    private DonViGia donViGia;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Transient
    private BigDecimal itemTotalPrice;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }

    public BigDecimal calculatePrice() {
        if (donViGia == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal basePrice = BigDecimal.valueOf(donViGia.getGia());
        double discount = donViGia.getDiscount() != null ? donViGia.getDiscount() : 0.0;
        if (discount > 0) {
            return basePrice.multiply(BigDecimal.valueOf(1 - discount / 100));
        }
        return basePrice;
    }
}