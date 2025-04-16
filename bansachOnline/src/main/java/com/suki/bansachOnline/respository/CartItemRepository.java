package com.suki.bansachOnline.respository;

import com.suki.bansachOnline.model.Book;
import com.suki.bansachOnline.model.Cart;
import com.suki.bansachOnline.model.CartItem;
import com.suki.bansachOnline.model.DonViGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndBookAndDonViGia(Cart cart, Book book, DonViGia donViGia);
}