package com.brokersystems.brokerapp.uw.model;

import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.BinderDetails;
import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by HP on 9/28/2017.
 */
public class RiskUploadForm {

    private Long polCode;

    private MultipartFile file;

    private BinderDetails binderDetails;

    private AccountDef insurance;

    private Long receipt;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getPolCode() {
        return polCode;
    }

    public void setPolCode(Long polCode) {
        this.polCode = polCode;
    }

    public BinderDetails getBinderDetails() {
        return binderDetails;
    }

    public void setBinderDetails(BinderDetails binderDetails) {
        this.binderDetails = binderDetails;
    }

    public AccountDef getInsurance() {
        return insurance;
    }

    public void setInsurance(AccountDef insurance) {
        this.insurance = insurance;
    }

    public Long getReceipt() {
        return receipt;
    }

    public void setReceipt(Long receipt) {
        this.receipt = receipt;
    }
}
