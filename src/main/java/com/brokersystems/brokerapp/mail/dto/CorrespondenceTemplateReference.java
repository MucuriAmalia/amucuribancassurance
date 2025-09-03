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
public class CorrespondenceTemplateReference {

    private static final Logger logger = LoggerFactory.getLogger(CorrespondenceTemplateReference.class);

    private String smsDestinationType;
    private String smsEventId;
    private String emailDestinationType;
    private String emailEventId;

}