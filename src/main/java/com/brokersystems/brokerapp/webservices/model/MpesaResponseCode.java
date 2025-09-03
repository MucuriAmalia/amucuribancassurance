package com.brokersystems.brokerapp.webservices.model;

/**
 * Created by HP on 4/29/2018.
 */
public class MpesaResponseCode {

    private int ResultCode;
    private String ResultDesc;
    private int ThirdPartyTransID;

    public int getResultCode() {
        return ResultCode;
    }

    public void setResultCode(int resultCode) {
        ResultCode = resultCode;
    }

    public String getResultDesc() {
        return ResultDesc;
    }

    public void setResultDesc(String resultDesc) {
        ResultDesc = resultDesc;
    }

    public int getThirdPartyTransID() {
        return ThirdPartyTransID;
    }

    public void setThirdPartyTransID(int thirdPartyTransID) {
        ThirdPartyTransID = thirdPartyTransID;
    }
}
