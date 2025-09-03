package com.brokersystems.brokerapp.developermodule.controller;

import com.brokersystems.brokerapp.developermodule.dto.StretchyParameterDTO;
import com.brokersystems.brokerapp.developermodule.dto.StretchyReportDTO;
import com.brokersystems.brokerapp.developermodule.dto.StretchyReportParameterDTO;
import com.brokersystems.brokerapp.developermodule.model.StretchyParameter;
import com.brokersystems.brokerapp.developermodule.model.StretchyReport;
import com.brokersystems.brokerapp.developermodule.service.StretchyReportService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.ProspectsDTO;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.RiskSectionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping({"/protected/stretchyreports"})
public class StretchyReportController {

    @Autowired
    private StretchyReportService stretchyReportService;

    @Autowired
    AuditTrailLogger auditTrailLogger;

    @ModelAttribute
    public ModelHelperForm createHelperForm(){
        return new ModelHelperForm();
    }

    @RequestMapping(value = "rpts", method = RequestMethod.GET)
    public String reportsHome(Model model, HttpServletRequest request) {
        String message = "Accessed Stretchy Reports Screen";
        String resource = "Stretch Reports";
        auditTrailLogger.log(message, request, resource);
        return "stretchyreports";
    }
    @RequestMapping(value = {"createStretchyReport"}, method = {RequestMethod.POST})
    public ResponseEntity<String> createStretchyReport(@RequestBody StretchyReportDTO stretchyReport) throws IllegalAccessException, BadRequestException {
        System.out.println("Received StretchyReportDTO: " + stretchyReport);
        System.out.println("Parameters: " + stretchyReport.getParameters());
        try {
                stretchyReportService.createStretchyReport(stretchyReport);
                return ResponseEntity.status(HttpStatus.CREATED).body("Report created successfully.");
        } catch (Exception e) {
            System.err.println("Error creating/updating report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @RequestMapping(value = {"updateStretchyReport"}, method = {RequestMethod.PUT})
    public ResponseEntity<String> updateStretchyReport(@RequestBody StretchyReportDTO stretchyReport) throws IllegalAccessException, BadRequestException {
        System.out.println("Received StretchyReportDTO: " + stretchyReport);
        System.out.println("Parameters: " + stretchyReport.getParameters());

        try {
            // Ensure that the report ID is provided for updating
            if (stretchyReport.getStrId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Report ID is required for updating.");
            }

            stretchyReportService.updateStretchyReport(stretchyReport);
            return ResponseEntity.status(HttpStatus.OK).body("Report updated successfully.");
        } catch (Exception e) {
            System.err.println("Error updating report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @RequestMapping(value = "stretchyReportsForm", method = RequestMethod.GET)
    public String stretchyReportForm(Model model) {
        model.addAttribute("strId", -2000);
        return "stretchyreportsform";
    }

    @RequestMapping(value = {"stretchyrpts"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<StretchyReportDTO> getStretchyReports(@DataTable DataTablesRequest pageable)
        throws IllegalAccessException{
        return stretchyReportService.findAllStretchyReports(pageable);
    }

    @RequestMapping(value = { "deleteStretchyReport/{strId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStretchyReport(@PathVariable Long strId) {
        stretchyReportService.deleteStretchyReport(strId);
    }

    @RequestMapping(value = {"stretchyparams"}, method = {RequestMethod.GET})
    @ResponseBody
    public Page<StretchyParameterDTO> selectStretchyParameter(String searchValue,Pageable pageable){
        return stretchyReportService.selectStretchyParameter(searchValue, pageable);
    }

    @RequestMapping(value = "/viewstretchyreport", method = RequestMethod.POST)
    public String viewStretchyReport(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request, @RequestParam(required = false) String strId) throws BadRequestException {

        request.getSession().setAttribute("reportId", helperForm.getId());
        StretchyReport stretchyReport = stretchyReportService.findStretchyReportDetails(helperForm.getId());
        return "viewstretchyreport";
    }

    @RequestMapping(value = { "getStretchyReportDetails" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<StretchyReport> findStretchyReportDetails(HttpServletRequest request) throws BadRequestException {
        Long reportCode = (Long) request.getSession().getAttribute("reportId");
        System.out.println(reportCode);
        StretchyReport created = stretchyReportService.findStretchyReportDetails(reportCode);

        return new ResponseEntity<StretchyReport>(created, HttpStatus.OK);
    }

    @RequestMapping(value = "getStretchyReportParameters", method = RequestMethod.GET)
    public ResponseEntity<List<StretchyParameter>> getStretchyReportParameters(
            @RequestParam(value = "reportId", required = false) Long reportId) throws BadRequestException {
        List<StretchyParameter> parameters = stretchyReportService.getStretchyReportParameters(reportId);
        System.out.println(parameters);
        return new ResponseEntity<>(parameters, HttpStatus.OK);
    }

    @RequestMapping(value = "deleteStretchyReportParameter", method = RequestMethod.POST)
    public ResponseEntity<?> deleteStretchyReportParameter(@RequestBody Map<String, Long> ids) {
        Long reportId = ids.get("reportId");
        Long stpId = ids.get("stpId");
        stretchyReportService.deleteStretchyReportParameter(reportId, stpId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/editstretchyreport", method = RequestMethod.POST)
    public String editStretchyReport(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request, @RequestParam(required = false) String strId) throws BadRequestException {

        request.getSession().setAttribute("reportId", helperForm.getId());
        StretchyReport stretchyReport = stretchyReportService.findStretchyReportDetails(helperForm.getId());
        return "editstretchyreport";
    }

    @RequestMapping(value = {"updateStretchyReportParameter"}, method = {RequestMethod.PUT})
    public ResponseEntity<String> updateStretchyReportParameter(@RequestBody StretchyReportParameterDTO stretchyReportParameterDTO) throws IllegalAccessException, BadRequestException {
        System.out.println("Received StretchyReportDTO: " + stretchyReportParameterDTO);

        try {
            stretchyReportService.updateStretchyReportParameter(stretchyReportParameterDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Report updated successfully.");
        } catch (Exception e) {
            System.err.println("Error updating report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
