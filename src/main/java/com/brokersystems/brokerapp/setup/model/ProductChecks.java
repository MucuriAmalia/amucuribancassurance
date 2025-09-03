package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

/**
 * Created by HP on 11/5/2017.
 */
@Entity
@Table(name="sys_brk_product_checks")
public class ProductChecks {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="pr_check_id")
    private Long checkId;

    @ManyToOne
    @JoinColumn(name="pr_product_code",nullable = false)
    private ProductsDef product;

    @ManyToOne
    @JoinColumn(name="pr_check_code",nullable = false)
    private Checks checks;

    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public ProductsDef getProduct() {
        return product;
    }

    public void setProduct(ProductsDef product) {
        this.product = product;
    }

    public Checks getChecks() {
        return checks;
    }

    public void setChecks(Checks checks) {
        this.checks = checks;
    }

    @Override
    public String toString() {
        return "ProductChecks{" +
                "checkId=" + checkId +
                ", product=" + product +
                ", checks=" + checks +
                '}';
    }
}
