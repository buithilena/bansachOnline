package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.Cart;
import com.suki.bansachOnline.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findBySessionId(String sessionId);
    List<Cart> findAllByUser(User user);
    void deleteBySessionId(String sessionId); // Thêm để xóa giỏ hàng tạm thời
}