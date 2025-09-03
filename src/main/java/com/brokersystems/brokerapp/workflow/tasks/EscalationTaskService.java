package com.brokersystems.brokerapp.workflow.tasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * Created by HP on 7/28/2017.
 */
public class EscalationTaskService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
          System.out.println("Process ID..."+delegateExecution.getProcessInstanceId());
    }
}
