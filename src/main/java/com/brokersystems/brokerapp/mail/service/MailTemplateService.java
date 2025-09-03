package com.brokersystems.brokerapp.mail.service;

import com.brokersystems.brokerapp.mail.model.MailTemplates;
import com.brokersystems.brokerapp.server.exception.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * Created by HP on 8/10/2017.
 */
public interface MailTemplateService {

    public String getMailTemplate(MailTemplates template, HttpServletRequest request) throws BadRequestException;

    public String getClaimReminderTemplate() throws BadRequestException;

}
