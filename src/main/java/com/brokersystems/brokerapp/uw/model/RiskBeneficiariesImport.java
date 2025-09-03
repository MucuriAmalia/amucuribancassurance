package com.brokersystems.brokerapp.uw.model;

import org.springframework.web.multipart.MultipartFile;

public class RiskBeneficiariesImport {

    private MultipartFile file;
    private Long riskId;


    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }
}
