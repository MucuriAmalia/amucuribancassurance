package com.brokersystems.brokerapp.mail.events;

import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

public class SendEmailEvents extends ApplicationEvent implements Serializable {

    private MailMessageBean mailMessageBean;

    public SendEmailEvents(Object source, MailMessageBean mailMessageBean) {
        super(source);
        this.mailMessageBean = mailMessageBean;
    }

    public MailMessageBean getMailMessageBean() {
        return mailMessageBean;
    }
}
