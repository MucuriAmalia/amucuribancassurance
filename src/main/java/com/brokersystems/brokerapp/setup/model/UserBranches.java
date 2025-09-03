package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;
import java.util.Date;

@Entity
//@Cacheable(true)
@Table(name ="sys_brk_user_branches")
public class UserBranches {

    @Id
    @GeneratedValue
    @Column(name="bub_id")
    private Long userBranchId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="bub_user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="bub_ob_id")
    private OrgBranch branch;

    @Column(name="bub_date_assigned",nullable = false)
    private Date dateAssigned;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="bub_user_assigned")
    private User userAssigned;

    public Long getUserBranchId() {
        return userBranchId;
    }

    public void setUserBranchId(Long userBranchId) {
        this.userBranchId = userBranchId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public User getUserAssigned() {
        return userAssigned;
    }

    public void setUserAssigned(User userAssigned) {
        this.userAssigned = userAssigned;
    }
}
