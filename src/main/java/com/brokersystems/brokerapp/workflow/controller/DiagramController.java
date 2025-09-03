package com.brokersystems.brokerapp.workflow.controller;

import com.brokersystems.brokerapp.mail.service.Mailer;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.Organization;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.workflow.WorkFlowModel;
import com.brokersystems.brokerapp.workflow.docs.*;
import com.brokersystems.brokerapp.workflow.repository.SysWfDocsRepo;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HP on 7/25/2017.
 */

@Controller
@RequestMapping("/protected/workflow")
public class DiagramController {


    private static final Logger LOG = LoggerFactory.getLogger(DiagramController.class);


    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected WorkflowService workflowSrvc;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(value = "deployhome", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String workflowDeploymentHome(Model model) {
        return "deployhome";
    }


    @RequestMapping(value = { "tickets" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SysWfDocs> getUserPolTrans(@DataTable DataTablesRequest pageable, @RequestParam(value = "policyNo", required = false) String policyNo,
                                                       @RequestParam(value = "quoteNo", required = false) String quoteNo,
                                                       @RequestParam(value = "preparedBy", required = false) String preparedBy)
            throws IllegalAccessException {
        return policyService.findSearchTickets(pageable,policyNo,quoteNo,preparedBy);
    }

    @RequestMapping(value = "ticketing", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String workflowTicketing(Model model,HttpServletRequest request)
    {
        String message="Accessed Ticketing Screen";
        String resource="Ticketing";
        auditTrailLogger.log(message,request, resource);

        return "ticketing";
    }

    @RequestMapping(value = { "doctypes" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<DocTypeDTO> getDocTypes( )
            throws IllegalAccessException {
        return workflowSrvc.getDocTypes();
    }

    @RequestMapping(value = "/{docType}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public void getDiagramByDocType(
            @PathVariable(value = "docType") DocType docType,HttpServletResponse response) throws IOException {
        LOG.debug("finding diagram for docType = {}", docType);
        byte[] bytes = workflowSrvc.getProcessDefinitionDiagram(docType);

        response.setContentType("image/png");
        response.getOutputStream().write(bytes);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + docType.getFileName());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + docType.getValue());
        System.out.println(Arrays.toString(bytes));
        response.getOutputStream().close();
    }



    @RequestMapping(value = "/documents/{docId}", method = RequestMethod.GET)
    public void getActiveDocDiagram(
            @PathVariable("docId") String docId,HttpServletResponse response) throws IOException {
        System.out.println("Fetching diagram....");
        LOG.debug("fetching diagram for docId{}", docId);
        byte[] bytes = workflowSrvc.getActiveDocumentDiagram(docId);
        response.setContentType("image/png");
        response.getOutputStream().write(bytes);
        response.getOutputStream().close();

    }

    @RequestMapping(value = "/taskName/{docId}", method = RequestMethod.GET)
    public ResponseEntity<String> getCurrentTask(
            @PathVariable("docId") String docId) throws IOException {
        String taskName= workflowSrvc.getActiveTaskName(docId);
        return new ResponseEntity<String>(taskName, HttpStatus.OK);

    }

    @RequestMapping(value = { "updateWFDocument" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void uploadWFDocument(WorkFlowModel workFlowModel, HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        workflowSrvc.updateWorkFlow(workFlowModel);
    }

    @RequestMapping(value = { "assigneeTicket" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void assignUser(HttpServletRequest request,AssigneeForm assigneeForm) throws IllegalAccessException, BadRequestException, IOException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        System.out.println("username: "+assigneeForm.getUsername());
        workflowSrvc.assignTask(polCode,assigneeForm.getUsername());
    }

    @RequestMapping(value = { "reaassigneeTicket" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void reaassignUser(AssigneeForm assigneeForm) throws IllegalAccessException, BadRequestException, IOException {
        System.out.println("controller>>>> : " + assigneeForm.getBusinessKey());
        workflowSrvc.assignTask(assigneeForm.getBusinessKey(),assigneeForm.getUsername());
    }

    @RequestMapping(value = "/dochistory/{docId}", method = RequestMethod.GET)
    public ResponseEntity<List<WfTaskHistDTO>> getDocHistory(
            @PathVariable("docId") String docId) throws IOException {
        List<WfTaskHistDTO> history = workflowSrvc.getTransactionHistory(docId);
         return new ResponseEntity<List<WfTaskHistDTO>>(history, HttpStatus.OK);

    }

}
