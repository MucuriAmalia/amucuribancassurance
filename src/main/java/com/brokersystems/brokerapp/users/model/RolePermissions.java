package com.brokersystems.brokerapp.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by peter on 4/15/2017.
 */
@Entity
//@Cacheable(true)
@Table(name ="sys_brk_role_permissions")
public class RolePermissions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="rp_id")
    private Long rpId;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="rp_role")
    private RolesDef roles;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="rp_perm")
    private PermissionsDef permission;


    @Column(name="perm_min_amount")
    private BigDecimal minAmount;

    @Column(name="perm_max_amount")
    private BigDecimal maxAmount;



    public Long getRpId() {
        return rpId;
    }

    public void setRpId(Long rpId) {
        this.rpId = rpId;
    }

    public RolesDef getRoles() {
        return roles;
    }

    public void setRoles(RolesDef roles) {
        this.roles = roles;
    }

    public PermissionsDef getPermission() {
        return permission;
    }

    public void setPermission(PermissionsDef permission) {
        this.permission = permission;
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
