package com.suki.bansachOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tacgia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TacGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_tac_gia", nullable = false)
    private String tenTacGia;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "quoc_tich")
    private String quocTich;
}