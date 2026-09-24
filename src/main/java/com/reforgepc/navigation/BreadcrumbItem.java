package com.reforgepc.navigation;

public record BreadcrumbItem(
        String label,
        String url,
        boolean current
) {
}