package com.brokersystems.brokerapp.schedules.model;

import com.brokersystems.brokerapp.setup.model.SectionsDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.setup.model.SubclassSections;

import javax.persistence.*;
import java.util.List;

/**
 * Created by peter on 2/20/2017.
 */
@Entity
@Table(name="sys_brk_sched_mappings")
public class ScheduleMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sm_id")
    private Long smId;

    @ManyToOne
    @JoinColumn(name = "sm_sub_code",nullable = false)
    private SubClassDef subclass;

    @Column(name = "sm_col_index", nullable = false)
    private String columnIndex;

    @Column(name = "sm_col_name", nullable = false)
    private String columnName;

    @Column(name = "sm_col_type", nullable = false)
    private String columnType;

    @Column(name = "sm_prem_item_appl",length = 1)
    private String premItem;

    @ManyToOne
    @JoinColumn(name = "sm_sec_code")
    private SectionsDef sections;

    @Column(name = "sm_options",length = 2000)
    private String options;

    @Column(name = "sm_col_lov_name")
    private String lovName;

    @Column(name = "sm_col_risk_column")
    private String mappedRiskColumn;

    @ManyToOne
    @JoinColumn(name = "sm_map_sec_code")
    private SectionsDef mappedSections;

    public Long getSmId() {
        return smId;
    }

    public void setSmId(Long smId) {
        this.smId = smId;
    }

    public SubClassDef getSubclass() {
        return subclass;
    }

    public void setSubclass(SubClassDef subclass) {
        this.subclass = subclass;
    }

    public String getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(String columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getLovName() {
        return lovName;
    }

    public void setLovName(String lovName) {
        this.lovName = lovName;
    }

    public String getMappedRiskColumn() {
        return mappedRiskColumn;
    }

    public void setMappedRiskColumn(String mappedRiskColumn) {
        this.mappedRiskColumn = mappedRiskColumn;
    }

    public String getPremItem() {
        return premItem;
    }

    public void setPremItem(String premItem) {
        this.premItem = premItem;
    }

    public SectionsDef getSections() {
        return sections;
    }

    public void setSections(SectionsDef sections) {
        this.sections = sections;
    }

    public SectionsDef getMappedSections() {
        return mappedSections;
    }

    public void setMappedSections(SectionsDef mappedSections) {
        this.mappedSections = mappedSections;
    }
}


