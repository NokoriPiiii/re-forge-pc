package com.reforgepc.config;

import com.reforgepc.entity.Role;
import com.reforgepc.entity.User;
import com.reforgepc.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DevDataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            createUserIfNotExists(
                userRepository,
                passwordEncoder,
                "admin@reforge.pc",
                "123456",
                Role.ADMIN
            );

            createUserIfNotExists(
                userRepository,
                passwordEncoder,
                "user@reforge.pc",
                "123456",
                Role.USER
            );
        };
    }

    private void createUserIfNotExists(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String password,
            Role role
    ) {
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
