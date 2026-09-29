package com.reforgepc.service;

import com.reforgepc.entity.Cart;
import com.reforgepc.entity.CartItem;
import com.reforgepc.entity.Product;
import com.reforgepc.entity.User;
import com.reforgepc.repository.CartItemRepository;
import com.reforgepc.repository.CartRepository;
import com.reforgepc.repository.ProductRepository;
import com.reforgepc.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "User not found: " + userId));

                    Cart cart = new Cart();
                    cart.setUser(user);

                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public void addToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId));

        CartItem existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        int currentQuantity = existingItem != null
                ? existingItem.getQuantity()
                : 0;

        int newQuantity = currentQuantity + quantity;

        if (newQuantity > product.getStock()) {
            throw new IllegalArgumentException(
                    "Only " + product.getStock() + " item(s) available in stock.");
        }

        if (existingItem == null) {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);

            cartItemRepository.save(cartItem);
            return;
        }

        existingItem.setQuantity(newQuantity);
        cartItemRepository.save(existingItem);
    }

    @Transactional
    public void updateQuantity(
            Long userId,
            Long productId,
            int quantity
    ) {
        if (quantity <= 0) {
            removeFromCart(userId, productId);
            return;
        }

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product is not in the cart."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId));

        if (quantity > product.getStock()) {
            throw new IllegalArgumentException(
                    "Only " + product.getStock() + " item(s) available in stock.");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);

        cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .ifPresent(cartItemRepository::delete);
    }
}
