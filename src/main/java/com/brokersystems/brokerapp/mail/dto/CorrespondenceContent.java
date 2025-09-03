package com.brokersystems.brokerapp.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorrespondenceContent {

    private String customerId;
    private String txnCurr;
    private String txnAmount;
    private String term1;
    private String rate1;
    private String defaultTerm;
}