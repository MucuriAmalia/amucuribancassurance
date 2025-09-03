package com.brokersystems.brokerapp.webservices.model;

import java.io.Serializable;

/**
 * Created by HP on 5/8/2018.
 */
public class WebClient implements Serializable {

    private Long clientId;
    private String clientShtDesc;
    private String clientName;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientShtDesc() {
        return clientShtDesc;
    }

    public void setClientShtDesc(String clientShtDesc) {
        this.clientShtDesc = clientShtDesc;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
