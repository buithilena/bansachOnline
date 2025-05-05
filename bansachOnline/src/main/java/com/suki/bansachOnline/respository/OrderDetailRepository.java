package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    void deleteByOrderId(Integer orderId);
}