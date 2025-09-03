package com.brokersystems.brokerapp.mail.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by HP on 8/10/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailMessageBean {

    private String subject;
    private String message;
    private String sendTo;
    private String sendCC;
    private String sendBcc;
    private String receiverType;
    private List<String> reports;
    private String transType;
    private Long transCode;

}
