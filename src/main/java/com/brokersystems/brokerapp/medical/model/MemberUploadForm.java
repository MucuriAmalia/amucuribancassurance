package com.brokersystems.brokerapp.medical.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by peter on 5/26/2017.
 */
public class MemberUploadForm {

    private MultipartFile file;

    private String fileName;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
