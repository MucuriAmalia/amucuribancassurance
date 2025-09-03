package com.brokersystems.brokerapp.users.model;

import java.math.BigDecimal;

/**
 * Created by peter on 4/19/2017.
 */
public class PermLimitsBean {

    private Long roleId;
    private Long permId;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermId() {
        return permId;
    }

    public void setPermId(Long permId) {
        this.permId = permId;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }
}
