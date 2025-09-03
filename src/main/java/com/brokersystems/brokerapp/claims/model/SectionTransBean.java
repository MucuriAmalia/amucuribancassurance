package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by peter on 3/6/2017.
 */
@Getter
@Slf4j
public class SectionTransBean {

    @Setter
    private Long sectId;

    private String section;

    private void validateAndSanitize(String input) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + "Section" + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", "Section", input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "Section");
        }
    }

    public void setSection(String section) {
        validateAndSanitize(section);
        this.section = section == null ? null : StringEscapeUtils.escapeHtml4(section.trim());
    }
}