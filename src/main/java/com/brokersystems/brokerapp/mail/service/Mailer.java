package com.brokersystems.brokerapp.mail.service;

import com.brokersystems.brokerapp.mail.model.*;
import com.brokersystems.brokerapp.server.exception.BadRequestException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by HP on 8/9/2017.
 */
public interface Mailer {


    void sendSmsAttachmentsANE(MailMessageBean message, Long transCode, String transType, HttpServletRequest request) throws BadRequestException;

    void sendEmailAttachments(MailMessageBean message, Long transCode, String transType, HttpServletRequest request) throws BadRequestException;

    String getEmailReceivers(Long transCode, String transType, String receiver) throws BadRequestException;

    String getSMSReceivers(Long transCode, String transType, String receiver) throws BadRequestException;

    void sendEmail(MailMessageBean messageBean) throws BadRequestException;

    void sendSmsAttachments(MailMessageBean message,Long transCode, String transType,HttpServletRequest request) throws BadRequestException;


}
