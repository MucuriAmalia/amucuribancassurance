package com.brokersystems.brokerapp.aki.dto;

public class CertificateDTO {

    private String CertificateNumber;

    public String getCertificateNumber() {
        return CertificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        CertificateNumber = certificateNumber;
    }

    @Override
    public String toString() {
        return "CertificateDTO [CertificateNumber=" + CertificateNumber + "]";
    }

}
