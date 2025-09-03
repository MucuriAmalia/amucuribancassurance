package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.bulktransactions.dtos.TransProcessingAuthDTO;
import com.brokersystems.brokerapp.bulktransactions.models.TransactionProcessing;
import com.brokersystems.brokerapp.bulktransactions.service.TransProcessingService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/protected/transprocessing"})
public class TransProcessingController {

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private TransProcessingService transProcessingService;

    @Autowired
    private PolicyTransService policyService;

    @RequestMapping(value = "transHome", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed Transaction Processing Home", request, "Transaction Processing");
        return "transProcessing";
    }

    @RequestMapping(value = "authorizedBulkPolicy", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed Bulk Policy Authorization Home", request, "Bulk Policy Authorization");
        return "authorizedPolicy";
    }

    @RequestMapping(value = "transUploadExcel", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Uploaded bulk policy file: " + file, request, "Bulk Policy Upload");
        return transProcessingService.uploadAndSaveExcelData(file);
    }

    @RequestMapping(value = {"unProcessedTrans"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<TransactionProcessing> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return transProcessingService.findUnprocessedTrans(pageable);
    }

    @RequestMapping(value = {"unAuthorizedPolicy"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<TransProcessingAuthDTO> getUnauthorizedPolicy(@DataTable DataTablesRequest pageable) {
        return transProcessingService.findUnauthorizedPolicies(pageable);
    }

    @RequestMapping(value = {"processSingleTrans/{transId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String processTrans(@PathVariable("transId") Long transId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed single policy with ID: " + transId, request, "Processed Single Policy");
        return transProcessingService.processSingleTrans(transId);
    }

    @RequestMapping(value = {"processBulkTransactions"}, method = {RequestMethod.POST})
    @ResponseBody
    public String bulkTransProcessing(@RequestBody List<Long> transIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed bulk policies with IDs: " + transIds, request, "Processed Bulk Policies");
        return transProcessingService.bulkProcessTrans(transIds);
    }

    @RequestMapping(value = {"deleteBulkTransactions"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteBulkTransProcessing(@RequestBody List<Long> transIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk transactions with IDs: " + transIds, request, "Deleted Bulk Policies");
        return transProcessingService.deleteBulkTransProcessing(transIds);
    }

    @RequestMapping(value = {"approveSinglePolicy/{policyId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveSinglePolicy(@PathVariable("policyId") Long policyId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved single policy with ID: " + policyId, request, "Approved single policy");
        return transProcessingService.approveSingleLoadedPolicy(policyId);
    }

    @RequestMapping(value = {"approveBulkpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveBulkPolicy(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk policies with IDs: " + policyIds, request, "Appproved Bulk Policies");
        return transProcessingService.approveBulkLoadedPolicy(policyIds);
    }

    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        request.getSession().setAttribute("policyCode", helperForm.getId());
        PolicyTrans policyTrans = policyService.getPolicyDetails(helperForm.getId());
        if("MD".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType()))
            return  "redirect:/protected/medical/policies/edituwpolicy";
        else if("L".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType())){
            return "redirect:/protected/uw/policies/editlifepolicy";
        }
        else{
            return "redirect:/protected/uw/policies/edituwpolicy";
        }

    }
    @RequestMapping(value = {"bulkTransTemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Bulk-upload-transactions-template.xlsx");

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Policy Details");
        String[] headers = {
                "Serial No", "Client First Name", "Client Other Names", "Client ID No.", "Client Kra pin",
                "Client Phone No.", "Client Email", "Client DOB", "Client CIF", "Client Type", "Policy Number",
                "Underwriter Code", "Product Name", "Cover Type", "Payment Frequency", "CoverDateFrom",
                "CoverDateTo", "Currency ISO code", "InceptionDate", "Policy Renew Date", "Sum Insured",
                "Gross Premium", "Balance", "Premium", "Net Premium", "Paid Premium", "WHtx", "Phcf",
                "Stamp Duty", "Training Levy", "Extras", "Bank Commission", "SubAgent/Marketer comm",
                "SubAgent AB No.", "Marketer AB No.", "Leads AB No."
        };
        // Create Header Row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            sheet.autoSizeColumn(i);
        }

        Sheet sheet2 = workbook.createSheet("Risk Details");
        String[] headers2 = {
                "Serial No", "Insured First Name", "Insured Other Names", "Insured ID No.",
                "Insured kra pin", "Insured Phone No.", "Insured Email", "Insured DOB", "Insured CIF",
                "Insured Type", "Risk/Property ID", "Risk Description", "Negotiated Premium",
                "Paid Installments", "Chasis No.", "Engine No.", "Year Of Manufacture",
                "Logbook No.", "Color"
        };
        // Create Header Row
        Row headerRow2 = sheet2.createRow(0);
        for (int i = 0; i < headers2.length; i++) {
            headerRow2.createCell(i).setCellValue(headers2[i]);
            sheet.autoSizeColumn(i);
        }

        Sheet sheet3 = workbook.createSheet("Premium Items");
        String[] headers3 = { "Serial No", "Sections", "Amount" };
        // Create Header Row
        Row headerRow3 = sheet3.createRow(0);
        for (int i = 0; i < headers3.length; i++) {
            headerRow3.createCell(i).setCellValue(headers3[i]);
            sheet.autoSizeColumn(i);
        }

        // Write the file to the response output stream
        workbook.write(response.getOutputStream());
        //workbook.close();
    }
}
