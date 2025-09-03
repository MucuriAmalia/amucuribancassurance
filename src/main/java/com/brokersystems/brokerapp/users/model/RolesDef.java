package com.brokersystems.brokerapp.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

/**
 * Created by peter on 4/15/2017.
 */
@Entity
//@Cacheable(true)
@Table(name ="sys_brk_roles")
public class RolesDef implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="role_id")
    private Long roleId;


    @Column(name="role_name")
    private String roleName;

    @XmlTransient
    @JsonIgnore
    @OneToMany(mappedBy="roles",fetch = FetchType.EAGER)
    private List<UserRole> userRoles;

    @XmlTransient
    @JsonIgnore
    @OneToMany(mappedBy="roles",fetch = FetchType.EAGER)
    private List<RolePermissions> rolePermissions;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<RolePermissions> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<RolePermissions> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
}
