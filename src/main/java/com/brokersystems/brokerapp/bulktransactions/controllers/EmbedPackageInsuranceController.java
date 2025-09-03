package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.ErrorsCache.ErrorWorkbookCache;
import com.brokersystems.brokerapp.bulktransactions.dtos.EmbedPackageInsuranceDTO;
import com.brokersystems.brokerapp.bulktransactions.dtos.WezeshaStockDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecord;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecordDTO;
import com.brokersystems.brokerapp.bulktransactions.service.BatchStatusService;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPremComputeService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.bulktransactions.service.EmbedPackageInsuranceService;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import org.easybatch.core.record.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/protected/embedpackaged/insurance")
public class EmbedPackageInsuranceController {

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private EmbedPackageInsuranceService embedPackageInsuranceService;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private BulkPremComputeService bulkPremComputeService;
    @Autowired
    private ErrorWorkbookCache errorWorkbookCache;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private BatchStatusService batchStatusService;


    @RequestMapping(value = "creatembedpackageInsur", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed embedded packaged insurance policy processing screen", request, "Accessed embedded packaged insurance policy processing screen");
        return "creatembedpackageInsur";
    }

    @RequestMapping(value = "viewembedpackageInsur", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed embedded package insurance policy authorization screen", request, "Accessed embedded package insurance policy authorization screen");
        return "viewembedpackageInsur";
    }

    @RequestMapping(value = "uploadembedpackagedInsur", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadFirstAssExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException, IOException {
        auditTrailLogger.log("Uploaded embedded package insurance policy creation file: " + file, request, "Uploaded embedded package insurance policy creation");
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            // Validate only the first row (header)
            List<String> validationErrors = ExcelReaderUtil.validateEmbedPackageInsuranceTemplate(workbook);
            if (!validationErrors.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
                for (String error : validationErrors) {
                    errorMessage.append(error).append("; ");
                }
                throw new BadRequestException(errorMessage.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while processing Excel file", e);
        }
        String filename = UUID.randomUUID().toString() ;
        File tempFile = File.createTempFile(filename, ".xlsx");
        file.transferTo(tempFile); // save contents to disk
        // Proceed to processing if validation passed
        embedPackageInsuranceService.uploadEmbedFirstAss(tempFile, userUtils.getCurrentUser().getId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Accepted");
        response.put("message", "File is being processed.");
        return response;

//
//        String fileName = file.getOriginalFilename();
//        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
//            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
//        }
//
//        // Validate template format
//        List<String> validationErrors = ExcelReaderUtil.validateEmbedPackageInsuranceTemplate(workbook);
//        if (!validationErrors.isEmpty()) {
//            StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
//            for (String error : validationErrors) {
//                errorMessage.append(error).append("; ");
//            }
//            throw new BadRequestException(errorMessage.toString());
//        }
//
//        return embedPackageInsuranceService.uploadEmbedFirstAss(file);
    }

    @RequestMapping(value = {"unprocessembedpackage"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<EmbedPackageInsuranceDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return embedPackageInsuranceService.findUnprocessedPackagedInsur(pageable);
    }

    @RequestMapping(value = {"viewunprocessembedpackage"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<EmbedPackageInsuranceDTO> getUnProcessedPackagedInsur(@DataTable DataTablesRequest pageable) {
        return embedPackageInsuranceService.viewUnprocessedEmbedPackageInsur(pageable);
    }

    @RequestMapping(value = {"processsinglembedinsur/{embedId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> processSinglePackagedInsur(@PathVariable("embedId") Long embedId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed single embedded packaged insurance policy with ID: " + embedId, request, "Processed single embedded packaged insurance policy");
        System.out.println("embedId: " + embedId);
        PolicyTrans policyTrans = embedPackageInsuranceService.processSinglePackagedInsurPol(embedId, userUtils.getCurrentUser().getId(), true);

        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }


    @RequestMapping(value = {"deletebulkembedinsurpols"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteBulkPackagedInsur(@RequestBody List<Long> embedIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete uploaded embedded packaged insurance bulk policies with IDs: " + embedIds, request, "Delete Bulk Embed Insurance Policies");
        return embedPackageInsuranceService.deleteBulkPackagedInsurPol(embedIds);
    }

    @RequestMapping(value = {"processbulkdelprocessedpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public String bulkDelProcessedPols(@RequestBody List<Long> embedIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete processed embedded packaged insurance bulk policies with IDs: " + embedIds, request, "Bulk Delete Processed Policies");
        return embedPackageInsuranceService.bulkDelProcessedPolicies(embedIds);
    }

    @RequestMapping(value = {"processbulkembedinsurpols"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> processBulkPackagedInsur(@RequestBody List<Long> embedIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed embedded packaged insurance bulk policies with IDs: " + embedIds, request, "Process Bulk Embed Insurance Policies");
        if (batchStatusService.isAnyJobRunning()) {
            throw new BadRequestException("Another batch is already running. Please wait for it to complete");
        }
        String jobId = UUID.randomUUID().toString();
        System.out.println("Processing job id "+jobId);
        batchStatusService.setStatus(jobId, "RUNNING");

        Long userId = userUtils.getCurrentUser().getId();

        CompletableFuture.runAsync(() -> {
            List<BatchRecord> batchRecords = new ArrayList<>();
            long counter = 0L;
            for (Long id : embedIds) {
                counter++;
                BatchRecordDTO dto = new BatchRecordDTO();
                dto.setBatchId(id);
                dto.setUserId(userId);
                org.easybatch.core.record.Header header =
                        new Header(counter, "Embed Pkg Header", new Date());
                batchRecords.add(new BatchRecord(header, dto));
            }

            BatchCreditBatch batch = new BatchCreditBatch();
            batch.setBatchRecords(batchRecords);

            try {
                embedPackageInsuranceService.processBulkPackagedInsurPol(batch, true);
                batchStatusService.setStatus(jobId, "COMPLETED");
            } catch (BadRequestException e) {
                e.printStackTrace();
                batchStatusService.setStatus(jobId, "FAILED");
            }
        });
        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);

//        List<Long> polCodes = embedPackageInsuranceService.processBulkPackagedInsurPol(embedIds, true);
//        System.out.println("polCodes embedded: " + polCodes.toString());
//        PolicyTrans policyTrans = null;
//
//        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }

    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Accessed edit underwriting transactions screen: " , request, "Accessed edit underwriting transactions screen");
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

    @RequestMapping(value = {"approvesinglembedinsur/{policyId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveSinglePackagedInsurPolicy(@PathVariable("policyId") Long policyId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved single embedded packaged insurance policy with ID: " + policyId, request, "Approved single embedded packaged insurance policy");
        return embedPackageInsuranceService.approveSinglePackagedInsurPol(policyId);
    }

    @RequestMapping(value = {"approvebulkembedinsurpols"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approvePackagedInsurPolicy(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk embedded packaged insurance policies with IDs: " + policyIds, request, "Approved bulk embedded packaged insurance policies");
        return embedPackageInsuranceService.approveBulkPackagedInsurPol(policyIds);
    }

    @RequestMapping(value = {"bulkembpkgintemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-embed-packaged-insurance-template.xlsx");

        String[] sheet1Headers = {
                "Serial No","Reg/ID No","Client CIF", "Branch Code",
                "Transaction Date", "Insurer Code", "Product Group", "Product Name", "Cover Type",
                "Frequency", "Currency ISO Code", "Start Date", "End Date",
                "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type"
        };

        String[] sheet2Headers = {
                "Serial No", "Insured ID No", "Insured CIF",
                "Account No.", "Acct Open Date", "Branch Code", "Category", "Month", "Premium"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Policy Details");
        // Create the header row
        Row headerRow = sheet1.createRow(0);
        for (int i = 0; i < sheet1Headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(sheet1Headers[i]);
            sheet1.autoSizeColumn(i);
        }

        Sheet sheet2 = workbook.createSheet("Risk Details");
        Row headerRow2 = sheet2.createRow(0);
        for (int i = 0; i < sheet2Headers.length; i++) {
            headerRow2.createCell(i).setCellValue(sheet2Headers[i]);
            sheet2.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        //workbook.close();
    }

    @RequestMapping(value = "download-error-file/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadErrorFile(@PathVariable("fileId") String fileId, HttpServletResponse response) throws IOException {
        File workbookFile = errorWorkbookCache.getExcelWorkbookFile(fileId);

        if (workbookFile == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Error file not found.");
            return;
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=BulkEmbdPkgInsurploadErrors.xlsx");

        try (FileInputStream fis = new FileInputStream(workbookFile)) {
            StreamUtils.copy(fis, response.getOutputStream());
            response.flushBuffer();
        } finally {
            workbookFile.delete(); // Delete temp file after download
            errorWorkbookCache.removeExcelWorkbook(fileId);
        }
    }

}
