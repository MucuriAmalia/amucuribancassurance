package com.brokersystems.brokerapp.workflow.tasks;

import com.brokersystems.brokerapp.quotes.services.QuotationService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("cancelQuoteService")
public class CancelQuoteService implements JavaDelegate {

    @Autowired
    private QuotationService quotationService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String businessKey = delegateExecution.getProcessBusinessKey();
        if(businessKey!=null && businessKey.startsWith("Q")){
            businessKey = businessKey.substring(1);
        }
        String cancelReason = (String)delegateExecution.getVariable("cancelReason");
        quotationService.cancelQuote(Long.valueOf(businessKey),cancelReason);


    }
}
