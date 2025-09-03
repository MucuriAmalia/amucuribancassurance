package com.brokersystems.brokerapp.workflow.tasks;


import com.brokersystems.brokerapp.setup.service.SetupsService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("customerCreationService")
public class CustomerCreationService implements JavaDelegate {

    @Autowired
    private SetupsService setupsService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>from customer CREATION service");

    }
}
