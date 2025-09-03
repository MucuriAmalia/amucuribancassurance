package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.medical.model.MedicalBinderRules;
import com.brokersystems.brokerapp.setup.model.Checks;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by HP on 10/5/2017.
 */
@Entity
@Table(name="sys_brk_trans_checks")
public class TransChecks {

    @Id
    @SequenceGenerator(name = "brkChecksSeq",sequenceName = "sys_checks_seq",allocationSize=1)
    @GeneratedValue(generator = "brkChecksSeq")
    @Column(name="tc_no")
    private Long tcNo;

    @ManyToOne
    @JoinColumn(name="tc_check_id",nullable=false)
    private Checks checks;


    @ManyToOne
    @JoinColumn(name="tc_bind_check_id")
    private MedicalBinderRules binderRules;

    @ManyToOne
    @JoinColumn(name="tc_pol_id",nullable=false)
    private PolicyTrans policyTrans;

    @ManyToOne
    @JoinColumn(name="tc_perm_id",nullable=false)
    private PermissionsDef permission;

    @Column(name="tc_authorised")
    private String authorised;

    @ManyToOne
    @JoinColumn(name="tc_auth_by")
    private User authBy;

    @Column(name="tc_auth_date")
    private Date authDate;


    public Long getTcNo() {
        return tcNo;
    }

    public void setTcNo(Long tcNo) {
        this.tcNo = tcNo;
    }

    public Checks getChecks() {
        return checks;
    }

    public void setChecks(Checks checks) {
        this.checks = checks;
    }

    public PermissionsDef getPermission() {
        return permission;
    }

    public void setPermission(PermissionsDef permission) {
        this.permission = permission;
    }

    public String getAuthorised() {
        return authorised;
    }

    public void setAuthorised(String authorised) {
        this.authorised = authorised;
    }

    public User getAuthBy() {
        return authBy;
    }

    public void setAuthBy(User authBy) {
        this.authBy = authBy;
    }

    public Date getAuthDate() {
        return authDate;
    }

    public void setAuthDate(Date authDate) {
        this.authDate = authDate;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public MedicalBinderRules getBinderRules() {
        return binderRules;
    }

    public void setBinderRules(MedicalBinderRules binderRules) {
        this.binderRules = binderRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransChecks that = (TransChecks) o;

        if (checks != null ? !checks.equals(that.checks) : that.checks != null) return false;
        if (binderRules != null ? !binderRules.equals(that.binderRules) : that.binderRules != null) return false;
        return policyTrans != null ? policyTrans.equals(that.policyTrans) : that.policyTrans == null;

    }

    @Override
    public int hashCode() {
        int result = checks != null ? checks.hashCode() : 0;
         result =31 * result + (binderRules != null ? binderRules.hashCode() : 0);
        result = 31 * result + (policyTrans != null ? policyTrans.hashCode() : 0);
        return result;
    }

}
