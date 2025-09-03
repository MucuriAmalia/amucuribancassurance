package com.brokersystems.brokerapp.dms.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "uw_doc_client_pol_no")
public class IntegrationUw {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "uw_doc_id")
    private Long uwDocId;

    @Column(name = "uw_pol_id")
    private Long polId;

    @Column(name = "client_pol_no")
    private  String clientPolNo;
}
