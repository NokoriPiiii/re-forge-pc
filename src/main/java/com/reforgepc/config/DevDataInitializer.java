package com.reforgepc.config;

import com.reforgepc.entity.Attribute;
import com.reforgepc.entity.AttributeOption;
import com.reforgepc.entity.ComponentType;
import com.reforgepc.entity.ComponentTypeAttribute;
import com.reforgepc.entity.Product;
import com.reforgepc.entity.ProductAttributeValue;
import com.reforgepc.entity.ProductType;
import com.reforgepc.entity.Role;
import com.reforgepc.entity.SpecificationGroup;
import com.reforgepc.entity.User;
import com.reforgepc.repository.AttributeOptionRepository;
import com.reforgepc.repository.AttributeRepository;
import com.reforgepc.repository.ComponentTypeAttributeRepository;
import com.reforgepc.repository.ComponentTypeRepository;
import com.reforgepc.repository.ProductAttributeValueRepository;
import com.reforgepc.repository.ProductRepository;
import com.reforgepc.repository.SpecificationGroupRepository;
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
import java.util.HashMap;
import java.util.Map;

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
    CommandLineRunner initializeCatalog(
            SpecificationGroupRepository specificationGroupRepository,
            AttributeRepository attributeRepository,
            AttributeOptionRepository attributeOptionRepository,
            ComponentTypeRepository componentTypeRepository,
            ComponentTypeAttributeRepository componentTypeAttributeRepository,
            ProductRepository productRepository,
            ProductAttributeValueRepository productAttributeValueRepository) {

        return args -> initializeCatalogData(
                specificationGroupRepository,
                attributeRepository,
                attributeOptionRepository,
                componentTypeRepository,
                componentTypeAttributeRepository,
                productRepository,
                productAttributeValueRepository);
    }

    private void initializeCatalogData(
            SpecificationGroupRepository specificationGroupRepository,
            AttributeRepository attributeRepository,
            AttributeOptionRepository attributeOptionRepository,
            ComponentTypeRepository componentTypeRepository,
            ComponentTypeAttributeRepository componentTypeAttributeRepository,
            ProductRepository productRepository,
            ProductAttributeValueRepository productAttributeValueRepository)
            throws IOException {

        Map<String, SpecificationGroup> groups =
                loadSpecificationGroups(specificationGroupRepository);

        Map<String, Attribute> attributes =
                loadAttributes(attributeRepository, groups);

        loadAttributeOptions(attributeOptionRepository, attributes);

        Map<String, ComponentType> componentTypes =
                loadComponentTypes(componentTypeRepository);

        loadComponentTypeAttributes(
                componentTypeAttributeRepository,
                componentTypes,
                attributes);

        loadProducts(
                productRepository,
                componentTypes);

        loadProductAttributeValues(
                productAttributeValueRepository,
                productRepository,
                attributes);
    }

    private Map<String, SpecificationGroup> loadSpecificationGroups(
            SpecificationGroupRepository repository) throws IOException {

        Map<String, SpecificationGroup> groups = new HashMap<>();

        readCsv(
                "db/specification_groups.csv",
                values -> {

                    String name = values[0];
                    int displayOrder = Integer.parseInt(values[1]);

                    SpecificationGroup group =
                            findOrCreateGroup(repository, name);

                    group.setName(name);
                    group.setDisplayOrder(displayOrder);

                    repository.save(group);

                    groups.put(name, group);
                });

        return groups;
    }

    private Map<String, Attribute> loadAttributes(
            AttributeRepository repository,
            Map<String, SpecificationGroup> groups)
            throws IOException {

        Map<String, Attribute> attributes = new HashMap<>();

        readCsv(
                "db/attributes.csv",
                values -> {

                    String name = values[0];
                    String key = values[1];
                    String groupName = values[2];

                    SpecificationGroup group = groups.get(groupName);

                    if (group == null) {
                        throw new IllegalStateException(
                                "Unknown specification group: " + groupName);
                    }

                    Attribute attribute =
                            findOrCreateAttribute(repository, key);

                    attribute.setName(name);
                    attribute.setKey(key);
                    attribute.setGroup(group);

                    repository.save(attribute);

                    attributes.put(key, attribute);
                });

        return attributes;
    }

    private void loadAttributeOptions(
            AttributeOptionRepository repository,
            Map<String, Attribute> attributes)
            throws IOException {

        readCsv(
                "db/attribute_options.csv",
                values -> {

                    String attributeKey = values[0];
                    String optionValue = values[1];

                    Attribute attribute = attributes.get(attributeKey);

                    if (attribute == null) {
                        throw new IllegalStateException(
                                "Unknown attribute: " + attributeKey);
                    }

                    findOrCreateOption(
                            repository,
                            attribute,
                            optionValue);
                });
    }

    private Map<String, ComponentType> loadComponentTypes(
            ComponentTypeRepository repository)
            throws IOException {

        Map<String, ComponentType> componentTypes = new HashMap<>();

        readCsv(
                "db/component_types.csv",
                values -> {

                    String name = values[0];

                    ComponentType componentType =
                            findOrCreateComponentType(repository, name);

                    componentType.setName(name);

                    repository.save(componentType);

                    componentTypes.put(name, componentType);
                });

        return componentTypes;
    }

    private void loadComponentTypeAttributes(
            ComponentTypeAttributeRepository repository,
            Map<String, ComponentType> componentTypes,
            Map<String, Attribute> attributes)
            throws IOException {

        readCsv(
                "db/component_type_attributes.csv",
                values -> {

                    String componentTypeName = values[0];
                    String attributeKey = values[1];
                    boolean required = Boolean.parseBoolean(values[2]);

                    ComponentType componentType =
                            componentTypes.get(componentTypeName);

                    Attribute attribute =
                            attributes.get(attributeKey);

                    if (componentType == null) {
                        throw new IllegalStateException(
                                "Unknown component type: "
                                        + componentTypeName);
                    }

                    if (attribute == null) {
                        throw new IllegalStateException(
                                "Unknown attribute: "
                                        + attributeKey);
                    }

                    addComponentTypeAttribute(
                            repository,
                            componentType,
                            attribute,
                            required);
                });
    }

    private void loadProducts(
            ProductRepository repository,
            Map<String, ComponentType> componentTypes)
            throws IOException {

        readCsv(
                "db/products.csv",
                values -> {

                    String name = values[0];
                    BigDecimal price =
                            new BigDecimal(values[1]);
                    ProductType productType =
                            ProductType.valueOf(values[2]);
                    String componentTypeName = values[3];

                    ComponentType componentType =
                            componentTypes.get(componentTypeName);

                    if (componentType == null) {
                        throw new IllegalStateException(
                                "Unknown component type: "
                                        + componentTypeName);
                    }

                    Product product =
                            repository.findByName(name)
                                    .orElseGet(Product::new);

                    product.setName(name);
                    product.setPrice(price);
                    product.setProductType(productType);

                    if (productType == ProductType.COMPONENT) {
                        product.setComponentType(componentType);
                    } else {
                        product.setComponentType(null);
                    }

                    repository.save(product);
                });
    }

    private void loadProductAttributeValues(
            ProductAttributeValueRepository repository,
            ProductRepository productRepository,
            Map<String, Attribute> attributes)
            throws IOException {

        readCsv(
                "db/product_attribute_values.csv",
                values -> {

                    String productName = values[0];
                    String attributeKey = values[1];
                    String attributeValue = values[2];

                    Product product =
                            productRepository.findByName(productName)
                                    .orElseThrow(() ->
                                            new IllegalStateException(
                                                    "Unknown product: "
                                                            + productName));

                    Attribute attribute =
                            attributes.get(attributeKey);

                    if (attribute == null) {
                        throw new IllegalStateException(
                                "Unknown attribute: "
                                        + attributeKey);
                    }

                    ProductAttributeValue existing =
                            findProductAttributeValue(
                                    repository,
                                    product.getId(),
                                    attribute.getId());

                    if (existing == null) {
                        existing = new ProductAttributeValue();
                        existing.setProduct(product);
                        existing.setAttribute(attribute);
                    }

                    existing.setValue(attributeValue);

                    repository.save(existing);
                });
    }

    private SpecificationGroup findOrCreateGroup(
            SpecificationGroupRepository repository,
            String name) {

        return repository.findAll()
                .stream()
                .filter(group -> group.getName().equals(name))
                .findFirst()
                .orElseGet(SpecificationGroup::new);
    }

    private Attribute findOrCreateAttribute(
            AttributeRepository repository,
            String key) {

        return repository.findAll()
                .stream()
                .filter(attribute -> attribute.getKey().equals(key))
                .findFirst()
                .orElseGet(Attribute::new);
    }

    private AttributeOption findOrCreateOption(
            AttributeOptionRepository repository,
            Attribute attribute,
            String value) {

        return repository.findByAttributeId(attribute.getId())
                .stream()
                .filter(option -> option.getValue().equals(value))
                .findFirst()
                .orElseGet(() -> {
                    AttributeOption option = new AttributeOption();
                    option.setAttribute(attribute);
                    option.setValue(value);
                    return repository.save(option);
                });
    }

    private ComponentType findOrCreateComponentType(
            ComponentTypeRepository repository,
            String name) {

        return repository.findAll()
                .stream()
                .filter(type -> type.getName().equals(name))
                .findFirst()
                .orElseGet(ComponentType::new);
    }

    private void addComponentTypeAttribute(
            ComponentTypeAttributeRepository repository,
            ComponentType componentType,
            Attribute attribute,
            boolean required) {

        ComponentTypeAttribute existing =
                repository.findByComponentTypeId(
                                componentType.getId())
                        .stream()
                        .filter(item ->
                                item.getAttribute()
                                        .getId()
                                        .equals(attribute.getId()))
                        .findFirst()
                        .orElse(null);

        if (existing == null) {
            existing = new ComponentTypeAttribute();
            existing.setComponentType(componentType);
            existing.setAttribute(attribute);
        }

        existing.setRequired(required);

        repository.save(existing);
    }

    private ProductAttributeValue findProductAttributeValue(
            ProductAttributeValueRepository repository,
            Long productId,
            Long attributeId) {

        return repository.findByProductId(productId)
                .stream()
                .filter(item ->
                        item.getAttribute()
                                .getId()
                                .equals(attributeId))
                .findFirst()
                .orElse(null);
    }

    private void readCsv(
            String resource,
            CsvRowHandler handler) throws IOException {

        InputStream inputStream =
                getClass()
                        .getClassLoader()
                        .getResourceAsStream(resource);

        if (inputStream == null) {
            throw new IllegalStateException(
                    "Could not find " + resource);
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream,
                                     StandardCharsets.UTF_8))) {

            String line;
            boolean header = true;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                if (header) {
                    header = false;
                    continue;
                }

                String[] values = line.split(",", -1);

                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }

                try {
                    handler.handle(values);
                } catch (Exception exception) {
                    throw new IllegalStateException(
                            "Invalid data in "
                                    + resource
                                    + " at line "
                                    + lineNumber
                                    + ": "
                                    + line,
                            exception);
                }
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
        user.setPassword(
                passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);

        userRepository.save(user);
    }

    @FunctionalInterface
    private interface CsvRowHandler {
        void handle(String[] values);
    }
}