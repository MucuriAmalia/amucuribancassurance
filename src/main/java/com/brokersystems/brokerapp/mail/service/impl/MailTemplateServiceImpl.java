package com.brokersystems.brokerapp.mail.service.impl;

import com.brokersystems.brokerapp.mail.model.MailTemplates;
import com.brokersystems.brokerapp.mail.service.MailTemplateService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * Created by HP on 8/10/2017.
 */
@Service
public class MailTemplateServiceImpl implements MailTemplateService {


    @Override
    public String getMailTemplate(MailTemplates template, HttpServletRequest request) throws BadRequestException {
        String path = request.getSession().getServletContext().getRealPath("/")+template.getFileName();
        File file = new File(path);
        String content = "";
        try {
            content = IOUtils.toString(new FileReader(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        return content;
    }

    @Override
    public String getClaimReminderTemplate() throws BadRequestException {
        String content = "";
        try {
           File file= ResourceUtils.getFile(
                    "classpath:templates/claims_reminder_template.vm");
            content = IOUtils.toString(new FileReader(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        return content;
    }
}
