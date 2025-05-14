package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrdersRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findById(Integer id);

    Page<Order> findByUserNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);

    // Thêm phương thức để lấy đơn hàng theo userId và trạng thái (tùy chọn)
    Page<Order> findByUserIdAndStatus(UUID userId, Order.OrderStatus status, Pageable pageable);

    // Thêm phương thức để lấy tất cả đơn hàng theo userId
    Page<Order> findByUserId(UUID userId, Pageable pageable);

}