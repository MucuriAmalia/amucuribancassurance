package com.brokersystems.brokerapp.workflow.utils;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.util.ReflectUtil;
import org.activiti.engine.repository.Deployment;

import java.util.List;

/**
 * Created by HP on 7/28/2017.
 */
public class DemoDataGenerator {

    private transient ProcessEngine processEngine;
    private transient RepositoryService repositoryService;


    public void init() {
        this.repositoryService = processEngine.getRepositoryService();
        initProcessDefinitions();
   }

    protected void initProcessDefinitions() {

        String deploymentName = "Bancassurance Process Flows";
        List<Deployment> deploymentList = repositoryService.createDeploymentQuery().deploymentName(deploymentName).list();

        if (deploymentList == null || deploymentList.isEmpty()) {
            repositoryService.createDeployment()
                    .name(deploymentName)
                    .addInputStream("genUnderwriteProcess.bpmn20.xml", ReflectUtil.getResourceAsStream("diagrams/genUnderwriteProcess.bpmn20"))
                    .addInputStream("genQuotationProcess.bpmn20.xml", ReflectUtil.getResourceAsStream("diagrams/genQuotationProcess.bpmn20"))
                    .deploy();
        }

    }



    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
}
