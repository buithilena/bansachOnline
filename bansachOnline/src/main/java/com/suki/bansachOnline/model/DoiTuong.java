package com.suki.bansachOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

@Entity
@Table(name = "doituong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoiTuong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "image_max", nullable = false)
    private String imageMax;

    @Column(name = "image_min", nullable = false)
    private String imageMin;

    @Column(name = "ten_doi_tuong", nullable = false)
    private String tenDoiTuong;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @OneToMany(mappedBy = "doiTuong", cascade = CascadeType.ALL)
    private List<Book> books;
}