package com.brokersystems.brokerapp.medical.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by peter on 5/9/2017.
 */
public class SubLimitsImport {

   private  MultipartFile file;

    private Long coverLimit;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getCoverLimit() {
        return coverLimit;
    }

    public void setCoverLimit(Long coverLimit) {
        this.coverLimit = coverLimit;
    }
}
