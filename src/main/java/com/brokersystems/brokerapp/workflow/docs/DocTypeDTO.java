package com.brokersystems.brokerapp.workflow.docs;

/**
 * Created by HP on 7/27/2017.
 */
public class DocTypeDTO {

    private String docTypeName;
    private String docTypeValue;

    public DocTypeDTO(String docTypeName, String docTypeValue) {
        this.docTypeName = docTypeName;
        this.docTypeValue = docTypeValue;
    }

    public String getDocTypeName() {
        return docTypeName;
    }

    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    public String getDocTypeValue() {
        return docTypeValue;
    }

    public void setDocTypeValue(String docTypeValue) {
        this.docTypeValue = docTypeValue;
    }
}
