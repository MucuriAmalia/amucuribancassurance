package com.brokersystems.brokerapp.mail.model;

import com.brokersystems.brokerapp.mail.events.SendEmailEvents;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class SendEmailPublisherBean implements ApplicationEventPublisherAware {

    ApplicationEventPublisher publisher;

    public void sendEmail (MailMessageBean mailMessageBean) {
        final SendEmailEvents sendEmailEvent = new SendEmailEvents(this,mailMessageBean);
        publisher.publishEvent(sendEmailEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
