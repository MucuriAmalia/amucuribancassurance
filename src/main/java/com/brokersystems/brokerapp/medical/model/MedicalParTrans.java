package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.Country;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * Created by peter on 5/12/2017.
 */
@Entity
@Table(name = "sys_brk_med_par_trans")
public class MedicalParTrans {

    @Id
    @SequenceGenerator(name = "medParTransSeq",sequenceName = "med_par_trans_seq",allocationSize=1)
    @GeneratedValue(generator = "medParTransSeq")
    @Column(name = "par_id")
    private Long parId;

    @Column(name="par_app_type",nullable = false)
    private String approvalType;

    @Column(name="par_trans_type")
    private String transType;

    @Column(name="par_event_date",nullable = false)
    private Date eventDate;

    @Column(name="par_event_time")
    private Date eventTime;

    @Column(name="par_not_date",nullable = false)
    private Date notDate;

    @Column(name="par_auth_date")
    private Date authDate;

    @Column(name="par_trans_date")
    private Date transDate;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="par_auth_user")
    private User authUser;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="par_trans_user")
    private User transUser;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="par_event_id",nullable = false)
    private MedicalEvents events;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="par_cou_id",nullable = false)
    private Country country;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="par_policy_id",nullable = false)
    private PolicyTrans policyTrans;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="par_category_id",nullable = false)
    private MedicalCategory category;



    @XmlTransient
    @ManyToOne
    @JoinColumn(name="par_client_id",nullable = false)
    private ClientDef clientDef;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="par_cat_member_id")
    private CategoryMembers categoryMember;

    @Column(name="par_no",nullable = false)
    private String parNo;

    @Column(name="par_rev_no",nullable = false)
    private String parRevisionNo;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="par_spc_id")
    private ServiceProviderContracts providerContracts;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="par_net_id")
    private MedicalNetworks medicalNetworks;

    @Column(name="par_adm_date")
    private Date admissionDate;

    @Column(name="par_exp_adm_date")
    private Date expectedAdmDate;

    @Column(name="par_status")
    private String parStatus;

    @Column(name="par_curr_status")
    private String currentStatus;

    @Column(name="par_converted")
    private String converted;

    @Column(name="par_convert_date")
    private Date convertDate;

    @Column(name="par_request_amt")
    private BigDecimal totalRequestAmount;

    @Column(name="par_invoice_amt")
    private  BigDecimal totalInvoiceAmount;

    @Column(name="par_calc_amt")
    private  BigDecimal totalCalcAmount;

    @Column(name="par_appr_amt")
    private BigDecimal totalApprAmount;

    @Column(name="par_rej_amt")
    private BigDecimal totalRejectedAmount;

    @Column(name="par_exgratia_amt")
    private BigDecimal totalexgratiaAmount;

    @Column(name="par_batched")
    private String batched;

    @XmlTransient
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="par_reference_no")
    private MedicalParTrans convertReference;

    @Transient
    private Date requestDate;

    @Transient
    private Date invoiceDate;

    @Transient
    private String invoiceNumber;

    @Transient
    private BigDecimal invoiceAmount;

    @Transient
    private String invoiceDescription;

    @Transient
    private String requestType;

    @Transient
    private Long diagnosisId;

    @Transient
    private Long serviceCode;

    @Transient
    private Date serviceDate;

    @Transient
    private Long activityId;

    @Transient
    private BigDecimal qty;

    @Transient
    private BigDecimal price;

    @Transient
    private BigDecimal approvedQty;

    @Transient
    private BigDecimal approvedPrice;

    @Transient
    private String requestDescription;

    @Transient
    private String servicePlace;

    @Transient
    private String manualControl;

    @Transient
    private String exgratiaService;

    @Transient
    private BigDecimal exgratiaAmount;

    public Long getParId() {
        return parId;
    }

    public void setParId(Long parId) {
        this.parId = parId;
    }

    public String getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(String approvalType) {
        this.approvalType = approvalType;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Date getNotDate() {
        return notDate;
    }

    public void setNotDate(Date notDate) {
        this.notDate = notDate;
    }

    public MedicalEvents getEvents() {
        return events;
    }

    public void setEvents(MedicalEvents events) {
        this.events = events;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public ClientDef getClientDef() {
        return clientDef;
    }

    public void setClientDef(ClientDef clientDef) {
        this.clientDef = clientDef;
    }

    public Date getAuthDate() {
        return authDate;
    }

    public void setAuthDate(Date authDate) {
        this.authDate = authDate;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public User getAuthUser() {
        return authUser;
    }

    public void setAuthUser(User authUser) {
        this.authUser = authUser;
    }

    public User getTransUser() {
        return transUser;
    }

    public void setTransUser(User transUser) {
        this.transUser = transUser;
    }

    public String getParNo() {
        return parNo;
    }

    public void setParNo(String parNo) {
        this.parNo = parNo;
    }

    public String getParRevisionNo() {
        return parRevisionNo;
    }

    public void setParRevisionNo(String parRevisionNo) {
        this.parRevisionNo = parRevisionNo;
    }

    public ServiceProviderContracts getProviderContracts() {
        return providerContracts;
    }

    public void setProviderContracts(ServiceProviderContracts providerContracts) {
        this.providerContracts = providerContracts;
    }

    public MedicalNetworks getMedicalNetworks() {
        return medicalNetworks;
    }

    public void setMedicalNetworks(MedicalNetworks medicalNetworks) {
        this.medicalNetworks = medicalNetworks;
    }

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public Date getExpectedAdmDate() {
        return expectedAdmDate;
    }

    public void setExpectedAdmDate(Date expectedAdmDate) {
        this.expectedAdmDate = expectedAdmDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Long getDiagnosisId() {
        return diagnosisId;
    }

    public void setDiagnosisId(Long diagnosisId) {
        this.diagnosisId = diagnosisId;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getRequestDescription() {
        return requestDescription;
    }

    public void setRequestDescription(String requestDescription) {
        this.requestDescription = requestDescription;
    }

    public String getParStatus() {
        return parStatus;
    }

    public void setParStatus(String parStatus) {
        this.parStatus = parStatus;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public Long getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(Long serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }

    public BigDecimal getApprovedQty() {
        return approvedQty;
    }

    public void setApprovedQty(BigDecimal approvedQty) {
        this.approvedQty = approvedQty;
    }

    public BigDecimal getApprovedPrice() {
        return approvedPrice;
    }

    public void setApprovedPrice(BigDecimal approvedPrice) {
        this.approvedPrice = approvedPrice;
    }

    public String getConverted() {
        return converted;
    }

    public void setConverted(String converted) {
        this.converted = converted;
    }

    public Date getConvertDate() {
        return convertDate;
    }

    public void setConvertDate(Date convertDate) {
        this.convertDate = convertDate;
    }

    public MedicalParTrans getConvertReference() {
        return convertReference;
    }

    public void setConvertReference(MedicalParTrans convertReference) {
        this.convertReference = convertReference;
    }

    public MedicalCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalCategory category) {
        this.category = category;
    }

    public BigDecimal getTotalRequestAmount() {
        return totalRequestAmount;
    }

    public void setTotalRequestAmount(BigDecimal totalRequestAmount) {
        this.totalRequestAmount = totalRequestAmount;
    }

    public BigDecimal getTotalInvoiceAmount() {
        return totalInvoiceAmount;
    }

    public void setTotalInvoiceAmount(BigDecimal totalInvoiceAmount) {
        this.totalInvoiceAmount = totalInvoiceAmount;
    }

    public BigDecimal getTotalCalcAmount() {
        return totalCalcAmount;
    }

    public void setTotalCalcAmount(BigDecimal totalCalcAmount) {
        this.totalCalcAmount = totalCalcAmount;
    }

    public String getServicePlace() {
        return servicePlace;
    }

    public void setServicePlace(String servicePlace) {
        this.servicePlace = servicePlace;
    }

    public CategoryMembers getCategoryMember() {
        return categoryMember;
    }

    public void setCategoryMember(CategoryMembers categoryMember) {
        this.categoryMember = categoryMember;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public BigDecimal getTotalRejectedAmount() {
        return totalRejectedAmount;
    }

    public void setTotalRejectedAmount(BigDecimal totalRejectedAmount) {
        this.totalRejectedAmount = totalRejectedAmount;
    }

    public BigDecimal getTotalApprAmount() {
        return totalApprAmount;
    }

    public void setTotalApprAmount(BigDecimal totalApprAmount) {
        this.totalApprAmount = totalApprAmount;
    }

    public String getManualControl() {
        return manualControl;
    }

    public void setManualControl(String manualControl) {
        this.manualControl = manualControl;
    }

    public String getExgratiaService() {
        return exgratiaService;
    }

    public void setExgratiaService(String exgratiaService) {
        this.exgratiaService = exgratiaService;
    }

    public BigDecimal getExgratiaAmount() {
        return exgratiaAmount;
    }

    public void setExgratiaAmount(BigDecimal exgratiaAmount) {
        this.exgratiaAmount = exgratiaAmount;
    }

    public BigDecimal getTotalexgratiaAmount() {
        return totalexgratiaAmount;
    }

    public void setTotalexgratiaAmount(BigDecimal totalexgratiaAmount) {
        this.totalexgratiaAmount = totalexgratiaAmount;
    }

    public String getBatched() {
        return batched;
    }

    public void setBatched(String batched) {
        this.batched = batched;
    }


}