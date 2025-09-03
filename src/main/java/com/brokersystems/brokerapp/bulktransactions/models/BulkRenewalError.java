package com.brokersystems.brokerapp.bulktransactions.models;


import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sys_brk_bulk_renewal_errors")
@Getter
@Setter
public class BulkRenewalError {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer rowNumber;

    private String policyNumber;

    private String proposalNumber;

    @Column(length = 2000)
    private String errorMessage;

    private String batchId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();
}
