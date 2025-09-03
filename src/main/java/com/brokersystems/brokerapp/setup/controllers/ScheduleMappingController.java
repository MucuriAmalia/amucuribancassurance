package com.brokersystems.brokerapp.setup.controllers;

import com.brokersystems.brokerapp.schedules.model.ScheduleMapping;
import com.brokersystems.brokerapp.schedules.service.ScheduleService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.brokersystems.brokerapp.setup.model.SectionsDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by peter on 2/20/2017.
 */
@Controller
@RequestMapping({ "/protected/setups/schedules" })
public class ScheduleMappingController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @RequestMapping(value = "scheduleHome",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String revitemsHome(Model model, HttpServletRequest request)
    {
        String message="Accessed Revenue Items Screen";
        String resource="Revenue Items";
        auditTrailLogger.log(message,request, resource);
        return "scheduledef";
    }

    @RequestMapping(value = { "schedmapping/{subCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ScheduleMapping> getScheduleMapping(@DataTable DataTablesRequest pageable, @PathVariable Long subCode)
            throws IllegalAccessException {
        return scheduleService.findSubclassMapping(pageable,subCode);
    }

    @RequestMapping(value={"sclsections"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<SectionsDef> selectSections(@RequestParam(value="term", required=false) String term, @RequestParam("subId") Long subId, Pageable pageable)
            throws IllegalAccessException, BadRequestException
    {
        return scheduleService.findSectionsForSel(term, pageable, subId);
    }


    @RequestMapping(value = { "createSchedMapping" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createSchedMapping(ScheduleMapping scheduleMapping) throws IllegalAccessException, IOException, BadRequestException {
        scheduleService.createScheduleMapping(scheduleMapping);
    }

    @RequestMapping(value = { "deleteSchedMapping/{schedId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedMapping(@PathVariable Long schedId) {
        scheduleService.deleteScheduleMapping(schedId);
    }


}
