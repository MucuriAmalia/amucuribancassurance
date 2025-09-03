package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

@Entity
@Table(name = "sys_brk_product_codes")
public class ProductCodes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sap_prod_id")
    private Long prodId;
    @Column(name = "sap_prod_code", unique = true)
    private String productCode;
    @Column(name = "sap_prod_descriptipn")
    private String productDescription;

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescriptipn) {
        this.productDescription = productDescriptipn;
    }
}
