package com.reforgepc.navigation;

public enum PageId {

    MAIN(null, "Trang chủ", "/"),

    LOGIN(MAIN, "Đăng nhập", "/login"),
    FORGOT_PASSWORD(LOGIN, "Quên mật khẩu", "/forgot-password"),
    REGISTER(MAIN, "Đăng ký", "/register"),

    PRODUCTS(MAIN, "Sản phẩm", "/products"),
    COMPONENTS(PRODUCTS, "Linh kiện", "/products/components"),
    CPU(COMPONENTS, "CPU", "/products/components/cpu"),
    GPU(COMPONENTS, "GPU", "/products/components/gpu"),
    RAM(COMPONENTS, "RAM", "/products/components/ram"),
    PC_GAMING(PRODUCTS, "PC Gaming", "/products/gaming-pc"),

    PC_BUILDER(MAIN, "Build PC", "/builder"),

    ACCOUNT(MAIN, "Tài khoản", "/account"),
    PROFILE(ACCOUNT, "Thông tin tài khoản", "/account/profile"),
    CHANGE_PASSWORD(ACCOUNT, "Đổi mật khẩu", "/account/change-password"),
    ORDERS(ACCOUNT, "Đơn hàng", "/account/orders"),

    ADMIN(MAIN, "Quản trị", "/admin"),
    ADMIN_PRODUCTS(ADMIN, "Sản phẩm", "/admin/products"),
    ADMIN_ORDERS(ADMIN, "Đơn hàng", "/admin/orders"),
    ADMIN_USERS(ADMIN, "Người dùng", "/admin/users");

    private final PageId parent;
    private final String label;
    private final String url;

    PageId(PageId parent, String label, String url) {
        this.parent = parent;
        this.label = label;
        this.url = url;
    }

    public PageId getParent() {
        return parent;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }
}