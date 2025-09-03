package com.brokersystems.brokerapp.workflow.tasks;

import com.brokersystems.brokerapp.quotes.services.QuotationService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("confirmQuoteService")
public class ConfirmQuoteService implements JavaDelegate {

    @Autowired
    private QuotationService quotationService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String businessKey = delegateExecution.getProcessBusinessKey();
        if(businessKey!=null && businessKey.startsWith("Q")){
            businessKey = businessKey.substring(1);
        }
        quotationService.confirmQuote(Long.valueOf(businessKey));
    }
}
