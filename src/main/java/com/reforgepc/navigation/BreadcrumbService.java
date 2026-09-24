package com.reforgepc.navigation;

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
}