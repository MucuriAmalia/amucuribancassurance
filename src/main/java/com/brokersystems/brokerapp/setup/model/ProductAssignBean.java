package com.brokersystems.brokerapp.setup.model;

import java.util.List;

public class ProductAssignBean {
    private Long prgCode;
    private List<Long> products;

    public Long getPrgCode() {
        return prgCode;
    }

    public void setPrgCode(Long prgCode) {
        this.prgCode = prgCode;
    }

    public List<Long> getProducts() {
        return products;
    }

    public void setProducts(List<Long> products) {
        this.products = products;
    }
}
