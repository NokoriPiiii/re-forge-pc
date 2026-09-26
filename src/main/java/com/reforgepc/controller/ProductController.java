package com.reforgepc.controller;

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

    public ProductController(
            ProductService productService,
            ProductAttributeValueService productAttributeValueService
    ) {
        this.productService = productService;
        this.productAttributeValueService = productAttributeValueService;
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
        model.addAttribute(
                "product",
                productService.getById(id)
        );

        model.addAttribute(
                "specificationGroups",
                productAttributeValueService.getGroupedByProductId(id)
        );

        return "product/detail";
    }
}