package com.reforgepc.config;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.reforgepc.entity.CaseProduct;
import com.reforgepc.entity.ComponentType;
import com.reforgepc.entity.CoolerProduct;
import com.reforgepc.entity.CpuProduct;
import com.reforgepc.entity.GpuProduct;
import com.reforgepc.entity.MotherboardProduct;
import com.reforgepc.entity.Product;
import com.reforgepc.entity.ProductType;
import com.reforgepc.entity.RamProduct;
import com.reforgepc.entity.Role;
import com.reforgepc.entity.StorageProduct;
import com.reforgepc.entity.User;
import com.reforgepc.repository.ComponentTypeRepository;
import com.reforgepc.repository.ProductRepository;
import com.reforgepc.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DevDataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            createUserIfNotExists(userRepository, passwordEncoder, "admin@reforge.pc", "123456", Role.ADMIN);
            createUserIfNotExists(userRepository, passwordEncoder, "user@reforge.pc", "123456", Role.USER);
        };
    }

    @Bean
    CommandLineRunner initializeCatalog(
            ComponentTypeRepository componentTypeRepository,
            ProductRepository productRepository,
            ObjectMapper objectMapper) {

        return args -> {
            Map<String, ComponentType> componentTypes = loadComponentTypes(componentTypeRepository, objectMapper);
            loadProductsFromJson(productRepository, componentTypes, objectMapper);
        };
    }

    private Map<String, ComponentType> loadComponentTypes(
            ComponentTypeRepository repository,
            ObjectMapper objectMapper) throws IOException {

        Map<String, ComponentType> map = new HashMap<>();

        InputStream stream = getClass().getClassLoader().getResourceAsStream("db/component_types.json");
        if (stream == null) {
            return map;
        }

        JsonNode root = objectMapper.readTree(stream);
        if (!root.isArray()) {
            return map;
        }

        for (JsonNode node : root) {
            String name = node.asString();
            ComponentType type = repository.findAll().stream()
                    .filter(t -> t.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElseGet(() -> {
                        ComponentType newType = new ComponentType();
                        newType.setName(name);
                        return repository.save(newType);
                    });

            map.put(name, type);
        }

        return map;
    }

    private void loadProductsFromJson(
            ProductRepository repository,
            Map<String, ComponentType> types,
            ObjectMapper objectMapper) throws IOException {

        InputStream stream = getClass().getClassLoader().getResourceAsStream("db/products.json");
        if (stream == null) {
            return;
        }

        JsonNode root = objectMapper.readTree(stream);
        if (!root.isArray()) {
            return;
        }

        for (JsonNode item : root) {
            String name = item.path("name").asString(null);
            if (name == null || repository.findByName(name).isPresent()) {
                continue;
            }

            String category = item.path("category").asString("");
            ComponentType componentType = types.get(category);

            Product product = switch (category) {
                case "CPU" -> objectMapper.treeToValue(item, CpuProduct.class);
                case "Card đồ hoạ" -> objectMapper.treeToValue(item, GpuProduct.class);
                case "RAM" -> objectMapper.treeToValue(item, RamProduct.class);
                case "Bo mạch chủ" -> objectMapper.treeToValue(item, MotherboardProduct.class);
                case "Ổ cứng" -> objectMapper.treeToValue(item, StorageProduct.class);
                case "Tản nhiệt CPU" -> objectMapper.treeToValue(item, CoolerProduct.class);
                case "Case" -> objectMapper.treeToValue(item, CaseProduct.class);
                default -> objectMapper.treeToValue(item, Product.class);
            };

            product.setProductType(ProductType.COMPONENT);
            product.setComponentType(componentType);

            repository.save(product);
        }
    }

    private void createUserIfNotExists(UserRepository repository, PasswordEncoder encoder,
            String email, String password, Role role) {
        if (repository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(encoder.encode(password));
            user.setRole(role);
            user.setEnabled(true);
            repository.save(user);
        }
    }
}