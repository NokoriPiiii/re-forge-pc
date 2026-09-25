package com.reforgepc.controller;

import com.reforgepc.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
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
}