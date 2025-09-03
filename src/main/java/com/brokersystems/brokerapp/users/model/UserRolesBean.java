package com.brokersystems.brokerapp.users.model;

import java.util.List;

/**
 * Created by peter on 4/19/2017.
 */
public class UserRolesBean {

    private Long userId;
    private List<Long> roles;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getRoles() {
        return roles;
    }

    public void setRoles(List<Long> roles) {
        this.roles = roles;
    }
}
