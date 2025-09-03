package com.brokersystems.brokerapp.reinsurance.controllers;

import com.brokersystems.brokerapp.claims.dtos.ClaimRisksDTO;
import com.brokersystems.brokerapp.claims.dtos.ClaimantsDTO;
import com.brokersystems.brokerapp.claims.model.ClaimantsDef;
import com.brokersystems.brokerapp.reinsurance.dtos.TreatyClassesDTO;
import com.brokersystems.brokerapp.reinsurance.dtos.TreatyDefinitionDTO;
import com.brokersystems.brokerapp.reinsurance.dtos.TreatyParticipantsDTO;
import com.brokersystems.brokerapp.reinsurance.service.ReinsuranceSetupService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping({ "/protected/setups/reinsurance" })
public class ReinsuranceSetupController {

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private ReinsuranceSetupService reinsuranceSetupService;


    @RequestMapping(value = "treaties",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String classHome(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Treaty Setup screen",request, "Treaty Setup");
        return "treatiesdef";
    }


    @RequestMapping(value = { "treatylist" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<TreatyDefinitionDTO> getTreaties(@DataTable DataTablesRequest request) throws IllegalAccessException {
        return reinsuranceSetupService.findAllTreaties(request);
    }

    @RequestMapping(value = { "treatyparticipants/{treatyId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<TreatyParticipantsDTO> getParticipants(@PathVariable Long treatyId, @DataTable DataTablesRequest request) throws IllegalAccessException {
        return reinsuranceSetupService.findTreatyParticipants(treatyId,request);
    }

    @RequestMapping(value = { "treatyclasses/{treatyId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<TreatyClassesDTO> getClasses(@PathVariable Long treatyId, @DataTable DataTablesRequest request) throws IllegalAccessException {
        return reinsuranceSetupService.findTreatyClasses(treatyId,request);
    }

    @RequestMapping(value = { "createTreaty" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createTreaty(TreatyDefinitionDTO treaty) throws BadRequestException {
        reinsuranceSetupService.saveTreatyType(treaty);
    }

    @RequestMapping(value = { "createTreatyParticipant" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createTreatyParticipant(TreatyParticipantsDTO treaty) throws BadRequestException {
        reinsuranceSetupService.saveTreatyParticipant(treaty);
    }

    @RequestMapping(value = { "createTreatyClasses" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createTreatyClasses(TreatyClassesDTO treaty) throws BadRequestException {
        reinsuranceSetupService.saveTreatyClass(treaty);
    }
}
