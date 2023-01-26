package com.khtn.mybooks.model;

public class StaffPermission {
    private boolean staffManager;
    private boolean shopManager;
    private boolean orderManager;
    private boolean productManager;
    private boolean highPermission;

    public StaffPermission() {}

    public StaffPermission(boolean permission){
        this.staffManager = permission;
        this.shopManager = permission;
        this.orderManager = permission;
        this.productManager = permission;
        this.highPermission = false;
    }

    public StaffPermission(boolean staffManager, boolean shopManager, boolean orderManager, boolean productManager) {
        this.staffManager = staffManager;
        this.shopManager = shopManager;
        this.orderManager = orderManager;
        this.productManager = productManager;
    }

    public boolean isStaffManager() {
        return staffManager;
    }

    public void setStaffManager(boolean staffManager) {
        this.staffManager = staffManager;
    }

    public boolean isShopManager() {
        return shopManager;
    }

    public void setShopManager(boolean shopManager) {
        this.shopManager = shopManager;
    }

    public boolean isOrderManager() {
        return orderManager;
    }

    public void setOrderManager(boolean orderManager) {
        this.orderManager = orderManager;
    }

    public boolean isProductManager() {
        return productManager;
    }

    public void setProductManager(boolean productManager) {
        this.productManager = productManager;
    }

    public boolean isHighPermission() {
        return highPermission;
    }

    public void setHighPermission(boolean highPermission) {
        this.highPermission = highPermission;
    }

    public boolean checkPermission(StaffPermission permission){
        if (orderManager != permission.isOrderManager())
            return false;
        if (staffManager != permission.isStaffManager())
            return false;
        if (shopManager != permission.isShopManager())
            return false;
        return productManager == permission.isProductManager();
    }
}
