package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="sys_brk_segments")
public class Segments {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "seg_id")
    private Long segId;

    @Column(name = "seg_name")
    private String segName;

    @Column(name = "seg_desc")
    private String segDescription;

    @Column(name = "seg_segment")
    private String segSegment;

    @Column(name = "sap_seg_segment_mapping")
    private String sapSegment;

    @Column(name = "seg_code")
    private String segCode;

    @Column(name = "seg_comp_code")
    private String segCompCode;

    @Column(name = "seg_main_bank_segment_id")
    private Long segMainBankSegmentId;

    @Column(name = "seg_main_bank_segment")
    private String segMainBankSegment;

    @Column(name = "seg_created")
    private Date created;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name="modified_by")
    private String modifiedBy;

    // getters and setters

    public Long getSegId() {
        return segId;
    }

    public void setSegId(Long segId) {
        this.segId = segId;
    }

    public String getSegName() {
        return segName;
    }

    public void setSegName(String segName) {
        this.segName = segName;
    }

    public String getSegDescription() {
        return segDescription;
    }

    public void setSegDescription(String segDescription) {
        this.segDescription = segDescription;
    }

    public String getSegSegment() {
        return segSegment;
    }

    public void setSegSegment(String segSegment) {
        this.segSegment = segSegment;
    }

    public String getSegCode() {
        return segCode;
    }

    public void setSegCode(String segCode) {
        this.segCode = segCode;
    }

    public String getSegCompCode() {
        return segCompCode;
    }

    public void setSegCompCode(String segCompCode) {
        this.segCompCode = segCompCode;
    }

    public String getSegMainBankSegment() {
        return segMainBankSegment;
    }

    public void setSegMainBankSegment(String segMainBankSegment) {
        this.segMainBankSegment = segMainBankSegment;
    }

    public Long getSegMainBankSegmentId() {
        return segMainBankSegmentId;
    }

    public void setSegMainBankSegmentId(Long segMainBankSegmentId) {
        this.segMainBankSegmentId = segMainBankSegmentId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getSapSegment() {
        return sapSegment;
    }

    public void setSapSegment(String sapSegment) {
        this.sapSegment = sapSegment;
    }

}