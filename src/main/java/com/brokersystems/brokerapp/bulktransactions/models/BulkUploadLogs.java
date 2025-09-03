package com.brokersystems.brokerapp.bulktransactions.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "bulk_upload_error_logs")
public class BulkUploadLogs {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "table_name")
    private String errorMessage;

    @Column(name = "upload_id")
    private Long rowId;
}
