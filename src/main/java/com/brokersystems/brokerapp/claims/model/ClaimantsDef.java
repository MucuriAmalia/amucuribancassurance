package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.setup.model.Occupation;
import com.brokersystems.brokerapp.setup.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Getter
@Entity
@Table(name = "sys_brk_claimants")
@Slf4j
public class ClaimantsDef {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clmnt_id")
    private Long claimantId;

    @Column(name = "clmnt_surname", nullable = false)
    private String surname;

    @Column(name = "clmnt_othernames", nullable = false)
    private String otherNames;

    // Assume validated elsewhere (e.g., dropdown)
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clmnt_occup")
    private Occupation occupation;

    @Column(name = "clmnt_idno")
    private String idNumber;

    @Column(name = "clmnt_email")
    private String email;

    @Column(name = "clmnt_address", length = 500)
    private String address;

    @Column(name = "clmnt_mob_no", nullable = false)
    private String mobileNo;

    @Setter
    @Column(name = "created_date")
    private Date createdDate;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_user")
    private User createdUser;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setSurname(String surname) {
        validateAndSanitize(surname, NAME_PATTERN, "Surname");
        this.surname = surname == null ? null : StringEscapeUtils.escapeHtml4(surname.trim());
    }

    public void setOtherNames(String otherNames) {
        validateAndSanitize(otherNames, NAME_PATTERN, "Other Names");
        this.otherNames = otherNames == null ? null : StringEscapeUtils.escapeHtml4(otherNames.trim());
    }

    public void setIdNumber(String idNumber) {
        validateAndSanitize(idNumber, ID_NUMBER_PATTERN, "ID Number");
        this.idNumber = idNumber == null ? null : StringEscapeUtils.escapeHtml4(idNumber.trim());
    }

    public void setEmail(String email) {
        validateAndSanitize(email, EMAIL_PATTERN, "Email");
        this.email = email == null ? null : StringEscapeUtils.escapeHtml4(email.trim());
    }

    public void setAddress(String address) {
        validateAndSanitize(address, ADDRESS_PATTERN, "Address");
        this.address = address == null ? null : StringEscapeUtils.escapeHtml4(address.trim());
    }

    public void setMobileNo(String mobileNo) {
        validateAndSanitize(mobileNo, MOBILE_PATTERN, "Mobile Number");
        this.mobileNo = mobileNo == null ? null : StringEscapeUtils.escapeHtml4(mobileNo.trim());
    }

}