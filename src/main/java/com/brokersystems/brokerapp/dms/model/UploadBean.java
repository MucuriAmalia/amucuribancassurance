package com.brokersystems.brokerapp.dms.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by HP on 8/14/2017.
 */
public class UploadBean {

    private Long docId;

    private String fileId;

    private MultipartFile file;

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
