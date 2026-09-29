package com.reforgepc.controller;

import com.reforgepc.entity.Cart;
import com.reforgepc.entity.User;
import com.reforgepc.repository.CartItemRepository;
import com.reforgepc.repository.CartRepository;
import com.reforgepc.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class GlobalControllerAdvice {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public GlobalControllerAdvice(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    @Transactional(readOnly = true)
    public void addGlobalAttributes(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return;
        }

        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

        if (cart != null) {
            model.addAttribute(
                    "cartItemCount",
                    cartItemRepository.countByCartId(cart.getId())
            );
        }
    }
}