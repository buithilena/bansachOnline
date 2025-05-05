package com.suki.bansachOnline.respository;



import com.suki.bansachOnline.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findById(Integer id);

    Page<Order> findByUserNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);



}