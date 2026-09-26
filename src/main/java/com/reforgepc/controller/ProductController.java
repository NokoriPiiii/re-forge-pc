package com.reforgepc.controller;

import com.reforgepc.entity.Product;
import com.reforgepc.navigation.BreadcrumbService;
import com.reforgepc.service.ProductAttributeValueService;
import com.reforgepc.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {

    private final ProductService productService;
    private final ProductAttributeValueService productAttributeValueService;
    private final BreadcrumbService breadcrumbService;

    public ProductController(
            ProductService productService,
            ProductAttributeValueService productAttributeValueService,
            BreadcrumbService breadcrumbService
    ) {
        this.productService = productService;
        this.productAttributeValueService = productAttributeValueService;
        this.breadcrumbService = breadcrumbService;
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        return "product/products";
    }

    @GetMapping("/products/components")
    public String components(Model model) {
        model.addAttribute(
                "products",
                productService.getComponents()
        );

        return "product/components";
    }

    @GetMapping("/products/pc")
    public String prebuiltPCs(Model model) {
        model.addAttribute(
                "products",
                productService.getPrebuiltPCs()
        );

        return "product/pcs";
    }

    @GetMapping("/products/{id}")
    public String detail(
            @PathVariable Long id,
            Model model
    ) {
        Product product = productService.getById(id);

        model.addAttribute(
                "product",
                product
        );

        model.addAttribute(
                "specificationGroups",
                productAttributeValueService.getGroupedByProductId(id)
        );

        model.addAttribute(
                "breadcrumbs",
                breadcrumbService.getProductBreadcrumbs(product)
        );

        return "product/detail";
    }
}