package com.brokersystems.brokerapp.setup.controllers;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.service.RequiredDocsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by peter on 3/5/2017.
 */
@Controller
@RequestMapping({ "/protected/setups/requireddocs" })
public class RequiredDocsController {

    @Autowired
    private RequiredDocsService requiredDocsService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @RequestMapping(value = "reqdocs",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String requiredDocsHome(Model model, HttpServletRequest request)
    {
        String message="Accessed Required Documents Screen";
        String resource = "Required Documents";
        auditTrailLogger.log(message,request, resource);
        return "requireddocs";
    }

    @RequestMapping(value = { "reqdocslist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<RequiredDocs> getAllReqDocs(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return requiredDocsService.findAllRequiredDocs(pageable);
    }

    @RequestMapping(value = { "createReqdDocs" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createReqdDocs(RequiredDocs requiredDocs) throws IllegalAccessException, IOException, BadRequestException {
        requiredDocsService.defineRequiredDocs(requiredDocs);
    }

    @RequestMapping(value = { "deleteReqdDoc/{reqdDocId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReqdDoc(@PathVariable Long reqdDocId) {
        requiredDocsService.deleteRequiredDocs(reqdDocId);
    }

    @RequestMapping(value = { "subrequireddocs/{subCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SubClassReqdDocs> getSubclassReqdDocs(@DataTable DataTablesRequest pageable, @PathVariable Long subCode)
            throws IllegalAccessException {
        return requiredDocsService.findSubclassReqDocs(subCode,pageable);
    }

    @RequestMapping(value = { "subclassreqdocs" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<SubClassReqdDocs> getSubclassReqDocs(@RequestParam(value = "subCode", required = false) Long subCode, @RequestParam(value = "searchName", required = false) String searchName )
            throws IllegalAccessException {
        return requiredDocsService.getUnassignedReqDocs(subCode,searchName);
    }

    @RequestMapping(value = { "createSubclassReqDocs" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createSubclassReqDocs(@RequestBody RequiredDocBean requiredDocBean) throws IllegalAccessException, IOException, BadRequestException {
        requiredDocsService.createSubClassReqDocs(requiredDocBean);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "deleteSubReqdDoc/{reqdDocId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubReqdDoc(@PathVariable Long reqdDocId) {
        requiredDocsService.deleteSubRequiredDocs(reqdDocId);
    }

    @RequestMapping(value = { "createSubReqdDocs" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createSubReqdDocs(SubClassReqdDocs requiredDocs) {
        requiredDocsService.defineSubRequiredDocs(requiredDocs);
    }

}
