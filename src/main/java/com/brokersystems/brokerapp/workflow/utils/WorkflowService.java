package com.brokersystems.brokerapp.workflow.utils;

import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.mail.service.Mailer;
import com.brokersystems.brokerapp.medical.model.MedicalParTrans;
import com.brokersystems.brokerapp.quotes.model.QuoteTrans;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.workflow.WorkFlowModel;
import com.brokersystems.brokerapp.workflow.docs.*;
import com.brokersystems.brokerapp.workflow.repository.SysWfDocsRepo;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.util.ReflectUtil;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by HP on 7/25/2017.
 */
@Service
public class WorkflowService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowService.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repoSrvc;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private SysWfDocsRepo sysWfDocsRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private Mailer mailer;




    public List<DocTypeDTO> getDocTypes(){
        List<DocType> docTypes = DocType.asList();
        List<DocTypeDTO> docTypeDTOs = new ArrayList<>();
        for(DocType docType:docTypes){
            docTypeDTOs.add(new DocTypeDTO(docType.name(),docType.getValue()));
            System.out.println(docTypeDTOs);
        }
        System.out.println(docTypeDTOs);
        return docTypeDTOs;
    }
    /**
     * @param docType
     * @return the base workflow for a given document type (i.e. the process id of the process is {@code DocType_NONE})
     * or null if no base document exists.
     */
    @Transactional(readOnly = true)
    public ProcessDefinition findBaseProcDef(DocType docType) {
        LOG.debug("Checking for base workflow exists of doctype={}", docType.name());
        String processIdStr = docType.name();
        ProcessDefinition pd = this.repoSrvc.createProcessDefinitionQuery().processDefinitionCategory(WFConstants.NAMESPACE_CATEGORY).
                processDefinitionKey(processIdStr).latestVersion().singleResult();
        return pd;
    }

    @Transactional(readOnly = true)
    public List<WfTaskHistDTO> getTransactionHistory(String businessKey){
        List<WfTaskHistDTO> history = new ArrayList<>();

        List<HistoricProcessInstance> pi = historyService.createHistoricProcessInstanceQuery().includeProcessVariables().processInstanceBusinessKey(businessKey).list();
        if (pi == null || pi.size()==0) {
            return history;
        }
        List<HistoricTaskInstance> hTasks;
        hTasks = historyService.createHistoricTaskInstanceQuery().includeTaskLocalVariables().processInstanceBusinessKey(businessKey).list();
        for (HistoricTaskInstance hti : hTasks) {
           WfTaskHistDTO wfTaskHistDTO = new WfTaskHistDTO();

            wfTaskHistDTO.setAssignee(hti.getAssignee());
            wfTaskHistDTO.setId(hti.getId());
            wfTaskHistDTO.setName(hti.getName());
            wfTaskHistDTO.setStartTime(hti.getStartTime());
            wfTaskHistDTO.setEndTime(hti.getEndTime());
            history.add(wfTaskHistDTO);
        }
        return history;
    }

    @Transactional(readOnly = true)
    public boolean groupWorkflowExists(DocType docType) {
         return (this.repoSrvc.createProcessDefinitionQuery().processDefinitionCategory(WFConstants.NAMESPACE_CATEGORY).
                processDefinitionKey(docType.name()).latestVersion().singleResult()) != null;
    }

    @Transactional(readOnly = false,propagation = Propagation.REQUIRED)
    public String startNewWorkFlow(DocType docType, String businessKey, PolicyTrans policyTrans, String medical, QuoteTrans quoteTrans, ClaimBookings bookings, MedicalParTrans parTranss, ClientDef clientDef){
        ProcessInstance process =   this.runtimeService.startProcessInstanceByKey(docType.name(),businessKey);
        Task task = taskService.createTaskQuery().processInstanceId(process.getId()).singleResult();
        saveWorkflowAttributes(task.getName(),task.getId(),docType,policyTrans,quoteTrans,null,null,medical,task.getCreateTime(), clientDef);
        task.setAssignee(userUtils.getCurrentUser().getUsername());
        taskService.saveTask(task);
        return task.getId();
    }


    @Transactional(readOnly = false,propagation = Propagation.REQUIRED)
    public String startNewWorkFlowRest(DocType docType, String businessKey, PolicyTrans policyTrans, String medical, QuoteTrans quoteTrans, ClaimBookings bookings, MedicalParTrans parTranss, String username, ClientDef clientDef){
        ProcessInstance process =   this.runtimeService.startProcessInstanceByKey(docType.name(),businessKey);
        Task task = taskService.createTaskQuery().processInstanceId(process.getId()).singleResult();
        saveWorkflowAttributes(task.getName(),task.getId(),docType,policyTrans,quoteTrans,null,null,medical,task.getCreateTime(), null);
        task.setAssignee(username);
        taskService.saveTask(task);
        return task.getId();
    }
    @Transactional(readOnly = true)
    public byte[] getProcessDefinitionDiagram(DocType docType)  {
        ProcessDefinition procDefinition = this.findBaseProcDef(docType);
        if(procDefinition==null){
            return new byte[0];
        }
        InputStream in =  this.repoSrvc.getResourceAsStream(procDefinition.getDeploymentId(),procDefinition.getDiagramResourceName());
        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(in);
            System.out.println(bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return bytes;
    }

    @Transactional(readOnly = true)
    public String getActiveTaskName(String docId){
        ProcessInstance pi =
                runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(docId).singleResult();
        if(pi==null){
            return null;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        return task.getName();
    }


    /**
     * @param docId The document id.
     * @return png image of diagram with current activity highlighted.
     */
    @Transactional(readOnly = true)
    public byte[] getActiveDocumentDiagram(String docId) throws IOException {
        LOG.debug("getting active diagram for doc: {}", docId);
        //http://forums.activiti.org/content/process-diagram-highlighting-current-process
        ProcessInstance pi =
                runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(docId).active().singleResult();
        if(pi==null)
        {
            return new byte[0];
        }

        RepositoryServiceImpl impl = (RepositoryServiceImpl) repoSrvc;
        ProcessDefinitionEntity pde = (ProcessDefinitionEntity) impl.getDeployedProcessDefinition(pi.getProcessDefinitionId());
        BpmnModel bpmnModel = repoSrvc.getBpmnModel(pde.getId());
        InputStream in = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel, "png", runtimeService.getActiveActivityIds(pi.getProcessInstanceId()));
//        InputStream in = this.appContext.getResource("classpath:800x200.png").getInputStream();
        byte[] bytes = IOUtils.toByteArray(in);
        IOUtils.closeQuietly(in);
        LOG.debug("Got bytes of size: " + bytes.length);
        return bytes;
    }


    public void deployProcessFlow(WorkFlowModel workFlowModel,HttpServletRequest request) throws BadRequestException{
        try {
            ServletContext context = request.getSession().getServletContext();
            String folder = context.getRealPath("/WEB-INF/classes");
        }
        catch (Exception ex){
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Transactional(readOnly = false,propagation = Propagation.REQUIRED)
    public void completeTask(String docId, Map<String,Object> variables, PolicyTrans policyTrans,DocType docType, String medical,QuoteTrans quoteTrans,ClaimBookings bookings,MedicalParTrans parTrans, ClientDef clientDef){
        List<ProcessInstance> processInstances =
                runtimeService.createProcessInstanceQuery()
                        .processInstanceBusinessKey(docId)
                        .active()
                        .list();

        if (processInstances.isEmpty()) {
            return;
        }

        for (ProcessInstance pi : processInstances) {
            // Retrieve the active task for this process instance
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(pi.getId())
                    .active()
                    .list();

            if (tasks.isEmpty()) {
                continue;
            }

            for (Task task : tasks) {
                // Inactivate the task
                inactivateTasks(task.getId());

                // Set variables and complete the task
                taskService.setVariables(task.getId(), variables);
                taskService.complete(task.getId());

                // Query the next active task in the process instance
                Task newTask = taskService.createTaskQuery()
                        .processInstanceId(pi.getId())
                        .active()
                        .singleResult();

                // Save workflow attributes if a new task exists
                if (newTask != null) {
                    saveWorkflowAttributes(newTask.getName(), newTask.getId(), docType, policyTrans, quoteTrans, null, null, medical, newTask.getCreateTime(), clientDef);
                }
            }
        }
//        ProcessInstance pi =
//                runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(docId).active().singleResult();
//        if(pi==null){
//            return;
//        }
//        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).active().singleResult();
//        inactivateTasks(task.getId());
//        taskService.setVariables(task.getId(),variables);
//        taskService.complete(task.getId());
//        Task newTask = taskService.createTaskQuery().processInstanceId(pi.getId()).active().singleResult();
//        if(newTask!=null)
//        saveWorkflowAttributes(newTask.getName(),newTask.getId(),docType,policyTrans,quoteTrans,null,null,medical,newTask.getCreateTime(), clientDef);

    }

    @Transactional(readOnly = false,propagation = Propagation.REQUIRED)
    public void completeTask(String docId, PolicyTrans policyTrans,DocType docType, String medical,QuoteTrans quoteTrans,ClaimBookings bookings,MedicalParTrans parTrans, ClientDef clientDef){
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(docId).list();

        if (processInstances.isEmpty()) {
            return;
        }

        for (ProcessInstance pi : processInstances) {
            Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
            if (task != null) {
                inactivateTasks(task.getId());
                taskService.complete(task.getId());

                Task newTask = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
                if (newTask != null) {
                    saveWorkflowAttributes(newTask.getName(), newTask.getId(), docType, policyTrans, quoteTrans, null, null, medical, newTask.getCreateTime(), clientDef);
                }
            }
        }
//        ProcessInstance pi =
//                (ProcessInstance) runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(docId);
//        if(pi==null){
//            return;
//        }
//        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
//        inactivateTasks(task.getId());
//        taskService.complete(task.getId());
//        Task newTask = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
//        if(newTask!=null)
//        saveWorkflowAttributes(newTask.getName(),newTask.getId(),docType,policyTrans,quoteTrans,null,null,medical,newTask.getCreateTime(),clientDef);

    }

    @Transactional(readOnly = false,propagation = Propagation.REQUIRED)
    void saveWorkflowAttributes(String taskName, String taskId, DocType docType, PolicyTrans policyTrans, QuoteTrans quoteTrans, MedicalParTrans parTrans, ClaimBookings bookings, String medical, Date date, ClientDef clientDef){
        SysWfDocs wfDocs = new SysWfDocs();
        wfDocs.setActive(true);
        wfDocs.setActiveProcess(taskName);
        wfDocs.setTaskId(taskId);
        wfDocs.setDocType(docType);
        if(policyTrans!=null && policyTrans.getCreatedUser()!=null)
        wfDocs.setUserId(policyTrans.getCreatedUser());
        else
            wfDocs.setUserId(userUtils.getCurrentUser());
        wfDocs.setPolicyTrans(policyTrans);
        wfDocs.setMedical(medical);
        wfDocs.setCreatedDate(date);
        wfDocs.setClientId(clientDef);
        wfDocs.setQuoteTrans(quoteTrans);
        sysWfDocsRepo.save(wfDocs);
    }

    private void inactivateTasks(String taskId){
        SysWfDocs wfDocs = sysWfDocsRepo.findOne(QSysWfDocs.sysWfDocs.taskId.eq(taskId));
        if(wfDocs!=null) {
            wfDocs.setActive(false);
            sysWfDocsRepo.save(wfDocs);
        }
    }

    public void updateWorkFlow(WorkFlowModel workFlowModel) throws BadRequestException{
        if(workFlowModel.getProcessName()==null){
            throw new BadRequestException("Select Deployment to Update...");
        }
        String deploymentName = "Brokerage Process Flows";
        DocType docType = DocType.valueOf(workFlowModel.getProcessName());
        if(docType==null) throw new BadRequestException("Enum cannot be null");
        List<Deployment> deploymentList = repositoryService.createDeploymentQuery().deploymentName(deploymentName).processDefinitionKey(docType.name()).list();
        if(deploymentList!=null && deploymentList.size()==50){
            throw new BadRequestException("You have reached threshhold of updating deployments...Consult System Vendor for more details");
        }
        repositoryService.createDeployment()
                .name(deploymentName)
                .addInputStream(docType.getFileName()+".xml", ReflectUtil.getResourceAsStream("diagrams/"+docType.getFileName()))
                .deploy();
    }

    @Transactional(readOnly = false)
    public void assignTask(Long businessKey,String username ) throws BadRequestException{
        System.out.println("Business key>>>> : " + businessKey);
        if(businessKey==null)
            throw new BadRequestException("Cannot assign to a null transaction....");
        String docId = String.valueOf(businessKey);
        if(StringUtils.isBlank(username)){
            throw new BadRequestException("Select User to assign...");
        }
        ProcessInstance pi =
                runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(docId).singleResult();
        if(pi==null){
            return;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        if(StringUtils.equalsIgnoreCase(task.getAssignee(),username)){
            throw new BadRequestException("You cannot assign the ticket to yourself....Select Another User");
        }

        task.setAssignee(username);
        taskService.saveTask(task);
        if(sysWfDocsRepo.count(QSysWfDocs.sysWfDocs.taskId.eq(task.getId()))==1){

            UserDTO user = userService.findByUserName(username);
            SysWfDocs docs = sysWfDocsRepo.findOne(QSysWfDocs.sysWfDocs.taskId.eq(task.getId()));
            String assignee =docs.getUserId().getName();
            docs.setUserId(userService.findById(user.getId()));
            sysWfDocsRepo.save(docs);

            MailMessageBean messageBean = new MailMessageBean();
            if((docs.getPolicyTrans()!=null))
            messageBean.setMessage("You have a new task assigned to you from "+assignee+" under Policy No"+docs.getPolicyTrans().getPolNo()+", Please log in to the system to process");
            else if(docs.getQuoteTrans()!=null){
                messageBean.setMessage("You have a new task assigned to you from "+assignee+" under Quote No"+docs.getQuoteTrans().getQuotNo()+", Please log in to the system to process");
            }
            messageBean.setSendTo(user.getEmail());
            messageBean.setSubject("Reassigned Ticket");
            mailer.sendEmail(messageBean);

        }




    }
}

