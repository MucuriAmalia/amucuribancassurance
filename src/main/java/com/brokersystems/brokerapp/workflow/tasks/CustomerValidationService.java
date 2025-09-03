package com.brokersystems.brokerapp.workflow.tasks;


import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("customerValidationService")
public class CustomerValidationService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>from customer validation service");

    }
}
