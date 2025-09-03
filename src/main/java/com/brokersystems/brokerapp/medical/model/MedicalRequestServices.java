package com.brokersystems.brokerapp.medical.model;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by peter on 5/16/2017.
 */
@Entity
@Table(name = "sys_brk_med_clm_services")
public class MedicalRequestServices {

    @Id
    @SequenceGenerator(name = "medParServicesSeq",sequenceName = "med_par_services_seq",allocationSize=1)
    @GeneratedValue(generator = "medParServicesSeq")
    @Column(name = "rs_id")
    private Long reqServiceId;

    @Column(name="rs_date",nullable = false)
    private Date serviceDate;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="rs_act_id",nullable = false)
    private MedActivities medActivities;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="rs_req_id",nullable = false)
    private MedicalParRequest request;

    @Column(name="rs_req_qty",nullable = false)
    private BigDecimal reqQuantity;

    @Column(name="rs_req_price",nullable = false)
    private BigDecimal reqPrice;

    @Column(name="rs_req_amt",nullable = false)
    private BigDecimal reqAmount;

    @Column(name="rs_app_qty")
    private BigDecimal appQuantity;

    @Column(name="rs_app_price")
    private BigDecimal appPrice;

    @Column(name="rs_app_amt")
    private BigDecimal appAmount;

    @Column(name="rs_calc_amt")
    private BigDecimal calcAmount;

    @Column(name="rs_rej_amt")
    private BigDecimal rejAmount;

    @Column(name="rs_manual_ctrl",length = 1)
    private String manualControl;

    @Column(name="rs_exgratia",length = 1)
    private String exgratiaService;

    @Column(name="rs_exgratia_amt")
    private BigDecimal exgratiaAmount;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="rs_cat_ben_id")
    private MedCategoryBenefits categoryBenefits;

    public Long getReqServiceId() {
        return reqServiceId;
    }

    public void setReqServiceId(Long reqServiceId) {
        this.reqServiceId = reqServiceId;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public MedActivities getMedActivities() {
        return medActivities;
    }

    public void setMedActivities(MedActivities medActivities) {
        this.medActivities = medActivities;
    }

    public BigDecimal getReqQuantity() {
        return reqQuantity;
    }

    public void setReqQuantity(BigDecimal reqQuantity) {
        this.reqQuantity = reqQuantity;
    }

    public BigDecimal getReqPrice() {
        return reqPrice;
    }

    public void setReqPrice(BigDecimal reqPrice) {
        this.reqPrice = reqPrice;
    }

    public BigDecimal getReqAmount() {
        return reqAmount;
    }

    public void setReqAmount(BigDecimal reqAmount) {
        this.reqAmount = reqAmount;
    }

    public BigDecimal getAppQuantity() {
        return appQuantity;
    }

    public void setAppQuantity(BigDecimal appQuantity) {
        this.appQuantity = appQuantity;
    }

    public BigDecimal getAppPrice() {
        return appPrice;
    }

    public void setAppPrice(BigDecimal appPrice) {
        this.appPrice = appPrice;
    }

    public BigDecimal getAppAmount() {
        return appAmount;
    }

    public void setAppAmount(BigDecimal appAmount) {
        this.appAmount = appAmount;
    }

    public MedicalParRequest getRequest() {
        return request;
    }

    public void setRequest(MedicalParRequest request) {
        this.request = request;
    }

    public MedCategoryBenefits getCategoryBenefits() {
        return categoryBenefits;
    }

    public void setCategoryBenefits(MedCategoryBenefits categoryBenefits) {
        this.categoryBenefits = categoryBenefits;
    }

    public BigDecimal getRejAmount() {
        return rejAmount;
    }

    public void setRejAmount(BigDecimal rejAmount) {
        this.rejAmount = rejAmount;
    }

    public BigDecimal getCalcAmount() {
        return calcAmount;
    }

    public void setCalcAmount(BigDecimal calcAmount) {
        this.calcAmount = calcAmount;
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
}