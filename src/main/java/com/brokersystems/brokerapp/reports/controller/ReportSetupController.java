package com.brokersystems.brokerapp.reports.controller;

import com.brokersystems.brokerapp.reports.model.ReportDefinition;
import com.brokersystems.brokerapp.reports.model.ReportHeaders;
import com.brokersystems.brokerapp.reports.model.ReportParameters;
import com.brokersystems.brokerapp.reports.service.ReportService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.brokersystems.brokerapp.setup.model.County;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.users.model.ModulesDef;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by peter on 2/12/2017.
 */

@Controller
@RequestMapping({ "/protected/setup/reports" })
public class ReportSetupController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "rptsetups",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String reportSetups(Model model)
    {
        return "rptsetups";
    }

    @RequestMapping(value = { "rptHeaders" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ReportHeaders> getReportHeaders(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return reportService.findAllReportHeaders(pageable);
    }

    @RequestMapping(value = { "createRptHeader" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createReportHeader(ReportHeaders reportHeader) throws IllegalAccessException, IOException, BadRequestException {
        reportService.defineRptHeader(reportHeader);
    }

    @RequestMapping(value = { "deleteRptHeader/{rhId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReportHeader(@PathVariable Long rhId) {
        reportService.deleteRptHeader(rhId);
    }

    @RequestMapping(value = { "reportDefs/{rhId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ReportDefinition> getAllReportDefs(@DataTable DataTablesRequest pageable, @PathVariable Long rhId)
            throws IllegalAccessException {
        return reportService.findReportsByHeader(rhId,pageable);
    }

    @RequestMapping(value = { "createReportDefs" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createReportDefs(ReportDefinition reportDef) throws IllegalAccessException, IOException, BadRequestException {
        reportService.defineReportDef(reportDef);
    }

    @RequestMapping(value = { "deleteRptDef/{rptCode}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRptDef(@PathVariable Long rptCode) {
        reportService.deleteReportDef(rptCode);
    }

    @RequestMapping(value = { "reportParams/{rptId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ReportParameters> getAllReportParameters(@DataTable DataTablesRequest pageable, @PathVariable Long rptId)
            throws IllegalAccessException {
        return reportService.findParametersByReport(rptId,pageable);
    }

    @RequestMapping(value = { "createReportParams" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createReportParams(ReportParameters reportParam) throws IllegalAccessException, IOException, BadRequestException {
        reportService.definReportParam(reportParam);
    }

    @RequestMapping(value = { "deleteReportParam/{rpCode}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReportParam(@PathVariable Long rpCode) {
        reportService.deleteReportParam(rpCode);
    }
    @RequestMapping(value = { "moduleFetch" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public ModulesDef selproduct(@RequestParam String moduleName)
            throws IllegalAccessException {
        return userService.findReportModule(moduleName);
    }
    @RequestMapping(value = { "addPermission" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void selproducts(PermissionsDef permissionsDef)
            throws IllegalAccessException, BadRequestException {
        userService.setPermissionSetups(permissionsDef);
    }

    @RequestMapping(value = { "permReports" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<PermissionsDef> selectPermissions(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        Long moduleId=userService.findRepMod("Reports Module");
        return reportService.findPermissionRep(moduleId,term, pageable);
    }
}
