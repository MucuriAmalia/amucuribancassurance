package com.brokersystems.brokerapp.setup.model;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by HP on 9/20/2017.
 */
@Entity
@Table(name="sys_brk_prem_rates_tbl")
public class PremRatesTable {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="rate_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rate_det_code",nullable=false)
    private BinderDetails binderDetails;

    @Transient
    MultipartFile file;

    @Column(name="rate_table", length = 2000)
    private String rate_table;


    @Column(name="rate_wef_date",nullable = false)
    private Date effDate;


    @Column(name="rate_file_name",nullable = false)
    private String fileName;

    @Column(name="rate_table_type")
    private String tableType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BinderDetails getBinderDetails() {
        return binderDetails;
    }

    public void setBinderDetails(BinderDetails binderDetails) {
        this.binderDetails = binderDetails;
    }

    public MultipartFile getFile() {
        return file;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getRate_table() {
        return rate_table;
    }

    public void setRate_table(String rate_table) {
        this.rate_table = rate_table;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "PremRatesTable{" +
                "id=" + id +
                ", binderDetails=" + binderDetails +
                ", file=" + file +
                ", rate_table=" + rate_table +
                ", effDate=" + effDate +
                ", fileName='" + fileName + '\'' +
                ", tableType='" + tableType + '\'' +
                '}';
    }
}
