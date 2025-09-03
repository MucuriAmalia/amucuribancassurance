package com.brokersystems.brokerapp.dataloading.controllers;

import com.brokersystems.brokerapp.dataloading.model.PolicyData;
import com.brokersystems.brokerapp.dataloading.service.DataLoadService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.uw.model.RiskUploadForm;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping({ "/protected/dataloading" })
public class DataLoadController {

    @Autowired
    private DataLoadService dataLoadService;

    @Autowired
    private PolicyTransService policyService;

    @RequestMapping(value = { "loadPolicyRecord" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void loadPolicyRecord(HttpServletRequest request) throws BadRequestException {
        dataLoadService.loadPolicies();
    }
    @RequestMapping(value = "policydataloading", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String policyDataLoading(Model model) {
        return "policydataloading";
    }


    @RequestMapping(value = { "policyToload" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PolicyData> getPolicyToload(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return dataLoadService.getPoliciesToLoad(pageable,"N");
    }

    @RequestMapping(value = { "loadedPolicies" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PolicyData> getLoadedPolicies(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return dataLoadService.getPoliciesToLoad(pageable,"Y");
    }

    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model,HttpServletRequest request) throws BadRequestException {
        System.out.println("Passed");
        request.getSession().setAttribute("policyCode", helperForm.getId());
            return  "redirect:/protected/uw/policies/edituwpolicy";


    }

    @RequestMapping(value = { "importPolicies" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void importCommissionTrans(RiskUploadForm uploadForm, HttpServletRequest request) throws BadRequestException, IOException {
        dataLoadService.importPolicies(uploadForm.getFile());
    }

    @RequestMapping(value = { "removeUnloadedpol" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUnloadedPol(HttpServletRequest request) throws BadRequestException {
        dataLoadService.removeUnloadedPolicies();
    }

}
