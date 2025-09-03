package com.brokersystems.brokerapp.certs.model;

import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by peter on 1/28/2017.
 */
@Entity
@Table(name="sys_brk_brn_certs")
public class BranchCerts {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="bc_id")
    private Long brnCertId;

    @ManyToOne
    @JoinColumn(name="bc_cer_id",nullable=false)
    private CertLots certLots;

    @Column(name="bc_no_from")
    private Long noFrom;

    @Column(name="bc_no_to")
    private Long noTo;

    @Column(name="bc_count_certs")
    private Long countCerts;

    @ManyToOne
    @JoinColumn(name="bc_user_id",nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="bc_brn_id",nullable=false)
    private OrgBranch branch;

    @Column(name="bc_deallocated")
    private String deallocated;

    @ManyToOne
    @JoinColumn(name="bc_allocated_by")
    private User allocatedBy;

    @ManyToOne
    @JoinColumn(name="bc_deallocated_by")
    private User deallocatedBy;

    @Column(name="bc_allocated_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date allocatedDate;

    @Column(name="bc_deallocated_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date deallocatedDate;

    @Column(name="bc_current_lot")
    private String currentLot;

    @Column(name="remarks")
    @Lob
    private String remarks;

    @Column(name="bc_last_print_no")
    private Long lastPrintedNo;

    @Column(name="bc_status")
    private String status;

    @ManyToOne
    @JoinColumn(name="bc_bc_id")
    private BranchCerts reassignedBrnCert;

    @Transient
    private boolean showUser;

    public boolean isShowUser() {
        return showUser;
    }

    public void setShowUser(boolean showUser) {
        this.showUser = showUser;
    }

    public Long getBrnCertId() {
        return brnCertId;
    }

    public void setBrnCertId(Long brnCertId) {
        this.brnCertId = brnCertId;
    }

    public CertLots getCertLots() {
        return certLots;
    }

    public void setCertLots(CertLots certLots) {
        this.certLots = certLots;
    }

    public Long getNoFrom() {
        return noFrom;
    }

    public void setNoFrom(Long noFrom) {
        this.noFrom = noFrom;
    }

    public Long getNoTo() {
        return noTo;
    }

    public void setNoTo(Long noTo) {
        this.noTo = noTo;
    }

    public Long getCountCerts() {
        return countCerts;
    }

    public void setCountCerts(Long countCerts) {
        this.countCerts = countCerts;
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

    public String getDeallocated() {
        return deallocated;
    }

    public void setDeallocated(String deallocated) {
        this.deallocated = deallocated;
    }

    public String getCurrentLot() {
        return currentLot;
    }

    public void setCurrentLot(String currentLot) {
        this.currentLot = currentLot;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getLastPrintedNo() {
        return lastPrintedNo;
    }

    public void setLastPrintedNo(Long lastPrintedNo) {
        this.lastPrintedNo = lastPrintedNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getAllocatedBy() {
        return allocatedBy;
    }

    public void setAllocatedBy(User allocatedBy) {
        this.allocatedBy = allocatedBy;
    }

    public User getDeallocatedBy() {
        return deallocatedBy;
    }

    public void setDeallocatedBy(User deallocatedBy) {
        this.deallocatedBy = deallocatedBy;
    }

    public Date getAllocatedDate() {
        return allocatedDate;
    }

    public void setAllocatedDate(Date allocatedDate) {
        this.allocatedDate = allocatedDate;
    }

    public Date getDeallocatedDate() {
        return deallocatedDate;
    }

    public void setDeallocatedDate(Date deallocatedDate) {
        this.deallocatedDate = deallocatedDate;
    }

    public BranchCerts getReassignedBrnCert() {
        return reassignedBrnCert;
    }

    public void setReassignedBrnCert(BranchCerts reassignedBrnCert) {
        this.reassignedBrnCert = reassignedBrnCert;
    }
}
