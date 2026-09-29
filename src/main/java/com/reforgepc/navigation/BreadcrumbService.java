package com.reforgepc.navigation;

import com.reforgepc.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BreadcrumbService {

    public List<BreadcrumbItem> getBreadcrumbs(String page) {
        PageId pageId = PageId.valueOf(
                page.toUpperCase().replace("-", "_")
        );

        return buildPageBreadcrumbs(pageId);
    }

    public List<BreadcrumbItem> getBreadcrumbsForRequest(Product product) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return Collections.emptyList();
        }

        String requestUri = attributes.getRequest().getRequestURI();

        if ("/".equals(requestUri)) {
            return Collections.emptyList();
        }

        if (product != null && isProductDetail(requestUri)) {
            return getProductBreadcrumbs(product);
        }

        PageId pageId = resolvePage(requestUri);

        if (pageId == null) {
            return Collections.emptyList();
        }

        return buildPageBreadcrumbs(pageId);
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

    private List<BreadcrumbItem> buildPageBreadcrumbs(PageId pageId) {
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

    private PageId resolvePage(String requestUri) {
        for (PageId pageId : PageId.values()) {
            if (pageId.getUrl().equals(requestUri)) {
                return pageId;
            }
        }

        if (requestUri.startsWith("/forgot-password/")) {
            return PageId.FORGOT_PASSWORD;
        }

        return null;
    }

    private boolean isProductDetail(String requestUri) {
        if (!requestUri.startsWith("/products/")) {
            return false;
        }

        String id = requestUri.substring("/products/".length());

        return id.matches("\\d+");
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