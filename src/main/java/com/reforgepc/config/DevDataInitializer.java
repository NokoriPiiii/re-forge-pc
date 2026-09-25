package com.reforgepc.config;

import com.reforgepc.entity.Product;
import com.reforgepc.entity.ProductType;
import com.reforgepc.entity.Role;
import com.reforgepc.entity.User;
import com.reforgepc.repository.ProductRepository;
import com.reforgepc.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Configuration
public class DevDataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "admin@reforge.pc",
                    "123456",
                    Role.ADMIN);

            createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "user@reforge.pc",
                    "123456",
                    Role.USER);
        };
    }

    @Bean
    CommandLineRunner initializeProducts(
            ProductRepository productRepository) {
        return args -> loadProductsFromCsv(productRepository);
    }

    private void loadProductsFromCsv(
            ProductRepository productRepository) throws IOException {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db/products.csv");

        if (inputStream == null) {
            throw new IllegalStateException("Could not find db/products.csv");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                if (header) {
                    header = false;
                    continue;
                }

                String[] values = line.split(",", -1);

                if (values.length != 3) {
                    throw new IllegalStateException("Invalid product CSV row: " + line);
                }

                String name = values[0].trim();
                BigDecimal price = new BigDecimal(values[1].trim());
                ProductType productType = ProductType.valueOf(values[2].trim());

                Product product = productRepository
                        .findByName(name)
                        .orElseGet(Product::new);

                product.setName(name);
                product.setPrice(price);
                product.setProductType(productType);

                productRepository.save(product);
            }
        }
    }

    private void createUserIfNotExists(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String password,
            Role role) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);

        userRepository.save(user);
    }
}