package com.reforgepc.controller;

import com.reforgepc.entity.Cart;
import com.reforgepc.entity.User;
import com.reforgepc.repository.UserRepository;
import com.reforgepc.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping("/cart")
    public String cart(Authentication authentication, Model model) {
        User user = getAuthenticatedUser(authentication);
        Cart cart = cartService.getOrCreateCart(user.getId());
        model.addAttribute("cart", cart);
        return "cart/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = getAuthenticatedUser(authentication);

        try {
            cartService.addToCart(user.getId(), productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/products/" + productId;
    }

    @PostMapping("/cart/update")
    public String updateQuantity(
            @RequestParam Long productId,
            @RequestParam int quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = getAuthenticatedUser(authentication);

        try {
            cartService.updateQuantity(user.getId(), productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật giỏ hàng.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(
            @RequestParam Long productId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = getAuthenticatedUser(authentication);

        try {
            cartService.removeFromCart(user.getId(), productId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/cart";
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }
}
