package com.brokersystems.brokerapp.setup.dto;

import java.util.List;

public class CreateUserBranchesDTO {

    private Long userId;
    private List<Long> branches;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getBranches() {
        return branches;
    }

    public void setBranches(List<Long> branches) {
        this.branches = branches;
    }

    @Override
    public String toString() {
        return "CreateUserBranchesDTO{" +
                "userId=" + userId +
                ", branches=" + branches +
                '}';
    }
}
