package com.brokersystems.brokerapp.uw.model;

import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.model.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peter on 5/31/2017.
 */
@Entity
@Table(name="sys_brk_admin_fees")
public class AdminFee {

    @Id
    @SequenceGenerator(name = "myAdminFeeSeq",sequenceName = "admin_fee_seq",allocationSize=1)
    @GeneratedValue(generator = "myAdminFeeSeq")
    @Column(name="af_id")
    private Long adminFeeId;

    @Column(name="af_appl_at")
    private String applicableAt;

    @Column(name="af_prepared_date", nullable = false)
    private Date preparedDate;

    @Column(name="af_payment_date")
    private Date paymentDate;

    @Column(name="af_auth_date")
    private Date authDate;

    @Column(name="af_vat_rate")
    private BigDecimal vatRate;

    @Column(name="af_vat_amt")
    private BigDecimal vatAmt;

    @Column(name="af_excise_dt_rate")
    private BigDecimal exciseDutyRate;

    @Column(name="af_vat_excise_duty")
    private BigDecimal vatExciseDuty;

    @Column(name="af_vat_excise_rate")
    private BigDecimal vatExciseDutyRate;

    @Column(name="af_excise_duty")
    private BigDecimal exciseDuty;

    @Column(name="af_admin_fee_amt")
    private BigDecimal adminFeeAmt;

    @Column(name="af_admin_fee_net")
    private BigDecimal adminNetAmt;

    @Column(name="af_ref_no", nullable = false)
    private String refNo;

    @ManyToOne
    @JoinColumn(name="af_cur_id")
    private Currencies currencies;

    @Column(name="af_curr_rate")
    private BigDecimal curRate;

    @ManyToOne
    @JoinColumn(name="af_clnt_id")
    private ClientDef clientDef;

    @Column(name="af_authorised",length = 1)
    private String authorised;

    @ManyToOne
    @JoinColumn(name="af_auth_user_id")
    private User authorisedBy;

    @ManyToOne
    @JoinColumn(name="af_prep_user_id")
    private User preparedBy;

    @ManyToOne
    @JoinColumn(name="af_branch_id")
    private OrgBranch branch;

    @Column(name="af_paid", length = 1)
    private String paid;

    @Column(name="af_pr_date", nullable = false)
    private Date asAtDate;

    @Column(name="af_remarks",length = 2000)
    private String remarks;

    public Long getAdminFeeId() {
        return adminFeeId;
    }

    public void setAdminFeeId(Long adminFeeId) {
        this.adminFeeId = adminFeeId;
    }

    public String getApplicableAt() {
        return applicableAt;
    }

    public void setApplicableAt(String applicableAt) {
        this.applicableAt = applicableAt;
    }

    public Date getPreparedDate() {
        return preparedDate;
    }

    public void setPreparedDate(Date preparedDate) {
        this.preparedDate = preparedDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getVatAmt() {
        return vatAmt;
    }

    public void setVatAmt(BigDecimal vatAmt) {
        this.vatAmt = vatAmt;
    }

    public BigDecimal getExciseDutyRate() {
        return exciseDutyRate;
    }

    public void setExciseDutyRate(BigDecimal exciseDutyRate) {
        this.exciseDutyRate = exciseDutyRate;
    }

    public BigDecimal getExciseDuty() {
        return exciseDuty;
    }

    public void setExciseDuty(BigDecimal exciseDuty) {
        this.exciseDuty = exciseDuty;
    }

    public BigDecimal getAdminFeeAmt() {
        return adminFeeAmt;
    }

    public void setAdminFeeAmt(BigDecimal adminFeeAmt) {
        this.adminFeeAmt = adminFeeAmt;
    }

    public BigDecimal getAdminNetAmt() {
        return adminNetAmt;
    }

    public void setAdminNetAmt(BigDecimal adminNetAmt) {
        this.adminNetAmt = adminNetAmt;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public Currencies getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Currencies currencies) {
        this.currencies = currencies;
    }

    public BigDecimal getCurRate() {
        return curRate;
    }

    public void setCurRate(BigDecimal curRate) {
        this.curRate = curRate;
    }

    public ClientDef getClientDef() {
        return clientDef;
    }

    public void setClientDef(ClientDef clientDef) {
        this.clientDef = clientDef;
    }

    public String getAuthorised() {
        return authorised;
    }

    public void setAuthorised(String authorised) {
        this.authorised = authorised;
    }

    public User getAuthorisedBy() {
        return authorisedBy;
    }

    public void setAuthorisedBy(User authorisedBy) {
        this.authorisedBy = authorisedBy;
    }

    public User getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(User preparedBy) {
        this.preparedBy = preparedBy;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public Date getAsAtDate() {
        return asAtDate;
    }

    public void setAsAtDate(Date asAtDate) {
        this.asAtDate = asAtDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public BigDecimal getVatExciseDuty() {
        return vatExciseDuty;
    }

    public void setVatExciseDuty(BigDecimal vatExciseDuty) {
        this.vatExciseDuty = vatExciseDuty;
    }

    public BigDecimal getVatExciseDutyRate() {
        return vatExciseDutyRate;
    }

    public void setVatExciseDutyRate(BigDecimal vatExciseDutyRate) {
        this.vatExciseDutyRate = vatExciseDutyRate;
    }

    public Date getAuthDate() {
        return authDate;
    }

    public void setAuthDate(Date authDate) {
        this.authDate = authDate;
    }
}
