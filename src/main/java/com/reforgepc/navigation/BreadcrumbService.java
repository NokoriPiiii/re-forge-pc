package com.reforgepc.navigation;

import com.reforgepc.entity.Product;
import com.reforgepc.entity.ProductType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BreadcrumbService {

    private static final Map<String, PageId> URL_TO_PAGE_MAP = new HashMap<>();
    private static final Map<PageId, List<BreadcrumbItem>> CACHED_PAGE_BREADCRUMBS = new EnumMap<>(PageId.class);
    private static final Map<PageId, List<BreadcrumbItem>> CACHED_PARENT_CHAINS = new EnumMap<>(PageId.class);

    static {
        for (PageId pageId : PageId.values()) {
            URL_TO_PAGE_MAP.put(pageId.getUrl(), pageId);

            // Compute parent chain where all items have current = false (for product details / child pages)
            List<BreadcrumbItem> parentChain = new ArrayList<>();
            PageId current = pageId;
            while (current != null) {
                parentChain.add(new BreadcrumbItem(current.getLabel(), current.getUrl(), false));
                current = current.getParent();
            }
            Collections.reverse(parentChain);
            CACHED_PARENT_CHAINS.put(pageId, Collections.unmodifiableList(parentChain));

            // Compute full breadcrumbs for this page where the last item has current = true
            List<BreadcrumbItem> pageTrail = new ArrayList<>();
            current = pageId;
            while (current != null) {
                pageTrail.add(new BreadcrumbItem(current.getLabel(), current.getUrl(), current == pageId));
                current = current.getParent();
            }
            Collections.reverse(pageTrail);
            CACHED_PAGE_BREADCRUMBS.put(pageId, Collections.unmodifiableList(pageTrail));
        }
    }

    public List<BreadcrumbItem> getBreadcrumbs(String page) {
        if (page == null || page.isBlank()) {
            return Collections.emptyList();
        }

        try {
            PageId pageId = PageId.valueOf(page.trim().toUpperCase().replace("-", "_"));
            return CACHED_PAGE_BREADCRUMBS.getOrDefault(pageId, Collections.emptyList());
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    public List<BreadcrumbItem> getBreadcrumbsForRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return Collections.emptyList();
        }

        HttpServletRequest request = attributes.getRequest();
        Object productAttr = request.getAttribute("product");
        Product product = (productAttr instanceof Product p) ? p : null;

        return getBreadcrumbsForRequest(product);
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

        return CACHED_PAGE_BREADCRUMBS.getOrDefault(pageId, Collections.emptyList());
    }

    public List<BreadcrumbItem> getProductBreadcrumbs(Product product) {
        if (product == null) {
            return Collections.emptyList();
        }

        PageId productPage = resolveProductPage(product);
        List<BreadcrumbItem> baseChain = CACHED_PARENT_CHAINS.getOrDefault(productPage, Collections.emptyList());

        List<BreadcrumbItem> breadcrumbs = new ArrayList<>(baseChain.size() + 1);
        breadcrumbs.addAll(baseChain);
        breadcrumbs.add(new BreadcrumbItem(
                product.getName(),
                null,
                true
        ));

        return breadcrumbs;
    }

    private PageId resolvePage(String requestUri) {
        String normalizedUri = normalizeUri(requestUri);

        PageId pageId = URL_TO_PAGE_MAP.get(normalizedUri);
        if (pageId != null) {
            return pageId;
        }

        if (normalizedUri.startsWith("/forgot-password")) {
            return PageId.FORGOT_PASSWORD;
        }

        return null;
    }

    private static String normalizeUri(String uri) {
        if (uri == null) {
            return "";
        }
        if (uri.length() > 1 && uri.endsWith("/")) {
            return uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    private boolean isProductDetail(String requestUri) {
        if (!requestUri.startsWith("/products/")) {
            return false;
        }

        String id = requestUri.substring("/products/".length());

        return id.matches("\\d+");
    }

    private PageId resolveProductPage(Product product) {
        if (product.getProductType() == ProductType.PREBUILT_PC) {
            return PageId.PC;
        }

        if (product.getComponentType() == null) {
            return PageId.PRODUCTS;
        }

        String componentType = product.getComponentType().getName();
        if (componentType == null) {
            return PageId.COMPONENTS;
        }

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