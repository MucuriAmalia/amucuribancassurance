package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;

/**
 * Created by peter on 3/5/2017.
 */
@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_provider_types")
public class ServiceProviderTypes {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "spr_tp_id")
    private Long typeId;

    @Column(name = "spr_type")
    private String providerType;

    private void validateAndSanitize(String input) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + "Provider Type" + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", "Provider Type", input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "Provider Type");
        }
    }

    public void setProviderType(String providerType) {
        validateAndSanitize(providerType);
        this.providerType = providerType == null ? null : StringEscapeUtils.escapeHtml4(providerType.trim());
    }
}