
package com.brokersystems.brokerapp.webservices.portalmodel;

import com.brokersystems.brokerapp.setup.dto.ClientDTO;
import com.brokersystems.brokerapp.webservices.model.PaymentInfo;
import com.brokersystems.brokerapp.webservices.model.RiskData;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by HP on 6/12/2018.
 */
public class PolicyData {

    private String wefDate;
    private String paymentMode;
    private String currency;
    private String product;
    private ClientDTO client;
    private List<RiskData> risks;
    private PaymentInfo paymentInfo;
    private String agentCode;


    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String  getWefDate() {
        return wefDate;
    }

    public void setWefDate(String wefDate) {
        this.wefDate = wefDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public List<RiskData> getRisks() {
        return risks;
    }

    public void setRisks(List<RiskData> risks) {
        this.risks = risks;
    }
}
