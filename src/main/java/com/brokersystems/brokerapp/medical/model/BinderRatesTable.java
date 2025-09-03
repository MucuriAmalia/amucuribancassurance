package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by peter on 4/24/2017.
 */
@Entity
@Table(name="sys_brk_med_rates")
public class BinderRatesTable {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="rate_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="rate_bin_code",nullable=false)
    private BindersDef binder;

    @Transient
    MultipartFile file;

    @Lob
    @Column(name="rate_table")
    private byte[] rate_table;

    @Column(name="rate_wef_date",nullable = false)
    private Date effDate;


    @Column(name="rate_file_name",nullable = false)
    private String fileName;

    @Column(name="rate_table_type",nullable = false)
    private String tableType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public byte[] getRate_table() {
        return rate_table;
    }

    public void setRate_table(byte[] rate_table) {
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

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }
}
