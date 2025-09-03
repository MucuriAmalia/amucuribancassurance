package com.brokersystems.brokerapp.aki.dto;

import java.util.List;

public class CertPrintResponse {

    private CertificateDTO Inputs;

    private CallBackUrlDTO callbackObj;

    private Boolean success;

    private List<AkiErrorLog> Error;

    private String APIRequestNumber;

    public CertificateDTO getInputs() {
        return Inputs;
    }
    public void setInputs(CertificateDTO inputs) {
        Inputs = inputs;
    }
    public CallBackUrlDTO getCallbackObj() {
        return callbackObj;
    }
    public void setCallbackObj(CallBackUrlDTO callbackObj) {
        this.callbackObj = callbackObj;
    }
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    public List<AkiErrorLog> getError() {
        return Error;
    }
    public void setError(List<AkiErrorLog> error) {
        Error = error;
    }
    public String getAPIRequestNumber() {
        return APIRequestNumber;
    }
    public void setAPIRequestNumber(String aPIRequestNumber) {
        APIRequestNumber = aPIRequestNumber;
    }

}
