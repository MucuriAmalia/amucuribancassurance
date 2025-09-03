package com.brokersystems.brokerapp.aki.dto;

import java.util.Map;

public class AkiCertUrl {

    private String[] messages;
    private Map<String,String> object;

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }


    public void setObject(Map<String, String> object) {
        this.object = object;
    }

    public Map<String, String> getObject() {
        return object;
    }

}
