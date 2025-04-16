package com.suki.bansachOnline.respository;


import com.suki.bansachOnline.controller.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    Optional<Orders> findById(Integer id);
}