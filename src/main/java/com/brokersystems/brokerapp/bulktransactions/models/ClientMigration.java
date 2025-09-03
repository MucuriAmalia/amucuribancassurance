package com.brokersystems.brokerapp.bulktransactions.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "client_migration")
public class ClientMigration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "kra_pin")
    private String kraPin;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "client_type")
    private String clientType;

    @Column(name = "client_short_description")
    private String clientShortDesc;

    @Column(name = "client_surname")
    private String clientSurname;

    @Column(name = "client_other_names")
    private String clientOtherNames;

    @Column(name = "client_gender")
    private String clientGender;

    @Column(name = "client_passport_number")
    private String clientPassportNumber;

    @Column(name = "client_title")
    private String clientTitle;

    @Column(name = "client_credit_allowed")
    private String clientCreditAllowed;

    @Column(name = "client_credit_limit")
    private String clientLimitAllowed;

    @Column(name = "client_country_code")
    private String clientCountryCode;

    @Column(name = "client_marital_status")
    private String clientMaritalStatus;

    @Column(name = "client_default_communication")
    private String clientDefaultCommunication;

    @Column(name = "client_code")
    private String clientCode;

    @Column(name = "preferred_paymode")
    private String prefferedPaymentMode;

    @Column(name = "postal_address")
    private String postalAddress;

    @Column(name = "physical_address")
    private String physicalAddress;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "processed")
    private Boolean processed = false;

    @CreationTimestamp
    @Column(name = "upload_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadDate;

    @Column(name = "processed_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date processedDate;

    @Column(name = "processed_by")
    private String processedBy;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.processed == null) {
            this.processed = false;
        }
    }
}