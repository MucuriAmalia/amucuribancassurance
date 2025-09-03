package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brokersystems.brokerapp.uw.model.RiskSectionBean;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRiskDTO {

    private static final Logger logger = LoggerFactory.getLogger(CreateRiskDTO.class);

    private Long riskId;

    private String riskShtDesc;

    private String riskDesc;

    private Long polBindCode;

    private Long insuredCode;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    private Long bindCode;

    private Long sclCode;

    private Long coverCode;

    private BigDecimal commRate;

    private List<RiskSectionBean> sections;

    private String prorata;

    private BigDecimal butchargePrem;

    private Long installmentNo;

    private String installmentPerc;

    private BigDecimal installAmount;

    private BigDecimal totalPercentage;

    private BigDecimal prevPercentage;

    private Long binderDet;

    private String autogenCert;

    private Integer workingAge;

    private String computeType;
    private BigDecimal sumInsured ;
    private BigDecimal premium ;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
            );
        }
    }


    public void setRiskShtDesc(String riskShtDesc) {
        validateAndSanitize(riskShtDesc, DESC_PATTERN, "Risk Short Description");
        this.riskShtDesc = riskShtDesc == null ? null : StringEscapeUtils.escapeHtml4(riskShtDesc.trim());
    }

    public void setRiskDesc(String riskDesc) {
        validateAndSanitize(riskDesc, DESC_PATTERN, "Risk Description");
        this.riskDesc = riskDesc == null ? null : StringEscapeUtils.escapeHtml4(riskDesc.trim());
    }

    public void setProrata(String prorata) {
        validateAndSanitize(prorata, NAME_PATTERN, "Prorata");
        this.prorata = prorata == null ? null : StringEscapeUtils.escapeHtml4(prorata.trim());
    }

    public void setInstallmentPerc(String installmentPerc) {
        validateAndSanitize(installmentPerc, REF_PATTERN, "Installment Percentage");
        this.installmentPerc = installmentPerc == null ? null : StringEscapeUtils.escapeHtml4(installmentPerc.trim());
    }

    public void setAutogenCert(String autogenCert) {
        validateAndSanitize(autogenCert, NAME_PATTERN, "Auto-generate Certificate");
        this.autogenCert = autogenCert == null ? null : StringEscapeUtils.escapeHtml4(autogenCert.trim());
    }

    public void setComputeType(String computeType) {
        validateAndSanitize(computeType, NAME_PATTERN, "Compute Type");
        this.computeType = computeType == null ? null : StringEscapeUtils.escapeHtml4(computeType.trim());
    }
}
