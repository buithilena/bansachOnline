package com.suki.bansachOnline.service;

import com.suki.bansachOnline.model.*;
import com.suki.bansachOnline.respository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GioHangService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookService bookService;
    private final DonViGiaRepository donViGiaRepository;

    public Cart getOrCreateCart(User user, HttpSession session) {
        if (user != null) {
            return cartRepository.findByUser(user)
                    .orElseGet(() -> {
                        Cart cart = new Cart();
                        cart.setUser(user);
                        cart.setSessionId(session.getId());
                        return cartRepository.save(cart);
                    });
        } else {
            String sessionId = session.getId();
            return cartRepository.findBySessionId(sessionId)
                    .orElseGet(() -> {
                        Cart cart = new Cart();
                        cart.setSessionId(sessionId);
                        return cartRepository.save(cart);
                    });
        }
    }

    public Cart addToCart(User user, HttpSession session, int productId, int donViTinhId, int quantity) {
        Cart cart = getOrCreateCart(user, session);
        Book book = bookService.getBookById(productId);
        if (book == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại!");
        }

        DonViGia donViGia = donViTinhId == 0 ? book.getDefaultDonViTinh() : bookService.getDonViGiaById(productId, donViTinhId);
        if (donViGia == null) {
            throw new IllegalArgumentException("Đơn vị tính không hợp lệ!");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndBookAndDonViGia(cart, book, donViGia);
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setPrice(cartItem.calculatePrice());
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setBook(book);
            cartItem.setDonViGia(donViGia);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(cartItem.calculatePrice());
            cartItemRepository.save(cartItem);
            cart.getCartItems().add(cartItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public int getCartItemCount(Cart cart) {
        return cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

    public List<CartItem> getCartItems(User user, HttpSession session) {
        Cart cart = getOrCreateCart(user, session);
        return cartItemRepository.findByCart(cart);
    }

    public void removeFromCart(int cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public Map<String, Object> getCartIdentifiers(User user, HttpSession session) {
        Map<String, Object> identifiers = new HashMap<>();
        identifiers.put("session_id", session.getId());
        identifiers.put("user_id", user != null ? user.getId().toString() : null);
        return identifiers;
    }
}