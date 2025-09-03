package com.brokersystems.brokerapp.webservices.model;

public class DocResponseObject {

    private boolean success;
    private String fileurl;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }
}
