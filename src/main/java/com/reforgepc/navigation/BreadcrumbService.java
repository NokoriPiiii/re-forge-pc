package com.reforgepc.navigation;

import com.reforgepc.entity.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BreadcrumbService {

    public List<BreadcrumbItem> getBreadcrumbs(String page) {
        PageId pageId = PageId.valueOf(page.toUpperCase());

        List<BreadcrumbItem> breadcrumbs = new ArrayList<>();

        PageId current = pageId;

        while (current != null) {
            breadcrumbs.add(new BreadcrumbItem(
                    current.getLabel(),
                    current.getUrl(),
                    current == pageId
            ));

            current = current.getParent();
        }

        Collections.reverse(breadcrumbs);

        return breadcrumbs;
    }

    public List<BreadcrumbItem> getProductBreadcrumbs(Product product) {
        PageId productPage = resolveProductPage(product);

        List<BreadcrumbItem> breadcrumbs = new ArrayList<>();

        PageId current = productPage;

        while (current != null) {
            breadcrumbs.add(new BreadcrumbItem(
                    current.getLabel(),
                    current.getUrl(),
                    false
            ));

            current = current.getParent();
        }

        Collections.reverse(breadcrumbs);

        breadcrumbs.add(new BreadcrumbItem(
                product.getName(),
                null,
                true
        ));

        return breadcrumbs;
    }

    private PageId resolveProductPage(Product product) {
        if (product.getComponentType() == null) {
            return PageId.PRODUCTS;
        }

        String componentType = product.getComponentType().getName();

        return switch (componentType.trim()) {
            case "CPU" -> PageId.CPU;
            case "Card đồ hoạ" -> PageId.GPU;
            case "RAM" -> PageId.RAM;
            case "Bo mạch chủ" -> PageId.MOTHERBOARD;
            case "Ổ cứng" -> PageId.STORAGE;
            case "Tản nhiệt CPU" -> PageId.CPU_COOLER;
            case "Case" -> PageId.CASE;
            default -> PageId.COMPONENTS;
        };
    }
}