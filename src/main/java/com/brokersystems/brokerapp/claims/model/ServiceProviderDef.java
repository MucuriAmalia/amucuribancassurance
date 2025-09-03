package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
import com.brokersystems.brokerapp.setup.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by peter on 3/5/2017.
 */
@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_serv_providers")
public class ServiceProviderDef {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "provd_id")
    private Long providerId;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="provd_type_id")
    private ServiceProviderTypes providerTypes;

    @Column(name = "provd_name", nullable = false)
    private String name;

    @Column(name = "provd_mobile", nullable = false)
    private String phoneNumber;

    @Column(name = "provd_email")
    private String email;

    @Setter
    private Date createdDate;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="created_user")
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

    public void setName(String name) {
        validateAndSanitize(name, Constants.NAME_PATTERN, "Name");
        this.name = name == null ? null : StringEscapeUtils.escapeHtml4(name.trim());
    }

    public void setPhoneNumber(String phoneNumber) {
        validateAndSanitize(phoneNumber, Constants.PHONE_PATTERN, "Phone Number");
        this.phoneNumber = phoneNumber == null ? null : StringEscapeUtils.escapeHtml4(phoneNumber.trim());
    }

    public void setEmail(String email) {
        validateAndSanitize(email, Constants.EMAIL_PATTERN, "Email");
        this.email = email == null ? null : StringEscapeUtils.escapeHtml4(email.trim());
    }
}