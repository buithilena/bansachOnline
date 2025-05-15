package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrdersRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findById(Integer id);

    Page<Order> findByUserNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);

    Page<Order> findByUserIdAndStatus(UUID userId, Order.OrderStatus status, Pageable pageable);

    Page<Order> findByUserId(UUID userId, Pageable pageable);

    // Đếm số lượng đơn hàng theo trạng thái
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") Order.OrderStatus status);
}