package com.brokersystems.brokerapp.workflow.docs;

import java.util.Arrays;
import java.util.List;

/**
 * Created by HP on 7/25/2017.
 */
public enum DocType {

    QUOTATION_DOCUMENT("Quotation Process","genQuotationProcess.bpmn20"),
    GEN_UW_DOCUMENT("General Underwrite Process","genUnderwriteProcess.bpmn20"),
    CUSTOMER_ONBOARDING("Customer Onboarding","customerOnboarding.bpmn20"),
    NON_CAPTIVE_ONBOARDING("Non Captive Business Onboarding","nonCaptiveProdOnboardingProcessFlow.bpmn20"),
    CLAIMS_PROCESS_FLOW("Claims Process","claimsProcessFlow.bpmn20"),
    ENDORSEMENT_PROCESS_FLOW("Endorsement Process","endorsementProcessFlow.bpmn20"),
    FINANCE_ACCRUALS_PROCESS_FLOW("Finance Accruals Process","financeAccrualsProcessFlow.bpmn20"),
    FINANCE_INVOICE_PROCESS_FLOW("Finance Invoice Process","financeInvoiceProcessFlow.bpmn20"),
    FINANCE_WHT_PROCESS("Finance Withholding Tax Process","financeWhtProcessFlow.bpmn20"),
    ISP_CREATION_PROCESS("ISP Creation Process","ispCreationProcessFlow.bpmn20"),
    RENEWALS_PROCESS("Renewals Process","renewalsProcessFlow.bpmn20"),
    GEN_CLAIMS_DOCUMENT("General Claims Process","genClaimsProcess.bpmn20"),
    MED_CLAIMS_DOCUMENT("Medical Claims Process","medClaimsProcess.bpmn20");

    private String value;

    private String fileName;

    private DocType(String value,String fileName) {
        this.value = value;
        this.fileName = fileName;
    }


    public String getValue() {
        return value;
    }

    public String getFileName() {
        return fileName;
    }

    public static List<DocType> asList(){
        return Arrays.asList(DocType.values());
    }

}
