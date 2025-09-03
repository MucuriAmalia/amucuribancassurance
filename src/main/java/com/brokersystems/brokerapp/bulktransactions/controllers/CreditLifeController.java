package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.ErrorsCache.ErrorWorkbookCache;
import com.brokersystems.brokerapp.bulktransactions.dtos.CreditLifeDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecord;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecordDTO;
import com.brokersystems.brokerapp.bulktransactions.models.CreditLife;
import com.brokersystems.brokerapp.bulktransactions.service.BatchStatusService;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPremComputeService;
import com.brokersystems.brokerapp.bulktransactions.service.CreditLifeService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.easybatch.core.record.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@RequestMapping("/protected/credit/life")
public class CreditLifeController {

    @Autowired
    private CreditLifeService creditLifeService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

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


    @RequestMapping(value = "createcreditlife", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed credit life upload screen", request, "Create Credit Life");
        return "createcreditlife";
    }

    @RequestMapping(value = "viewcreditlife", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed credit life upload authorization screen", request, "View Credit Life");
        return "viewcreditlife";
    }

    @RequestMapping(value = "uploadcreditlife", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadFirstAssExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException, IOException {
        auditTrailLogger.log("Uploaded credit life upload file: " + file, request, "Upload Credit Life");
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            // Validate only the first row (header)
            List<String> validationErrors = ExcelReaderUtil.validateCreditLifePolicyTemplate(workbook);
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
        creditLifeService.uploadCreditLife(tempFile, userUtils.getCurrentUser().getId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Accepted");
        response.put("message", "File is being processed.");
        return response;

//        String fileName = file.getOriginalFilename();
//        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
//            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
//        }
//
//        // Validate template format
//        List<String> validationErrors = ExcelReaderUtil.validateCreditLifePolicyTemplate(workbook);
//        if (!validationErrors.isEmpty()) {
//            StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
//            for (String error : validationErrors) {
//                errorMessage.append(error).append("; ");
//            }
//            throw new BadRequestException(errorMessage.toString());
//        }
//
        //return creditLifeService.uploadCreditLife(file);
    }

    @RequestMapping(value = {"deletebulkcreditlifeupload"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteBulkCreditLifeUpload(@RequestBody List<Long> creditIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete credit life bulk policies with IDs: " + creditIds, request, "Process Bulk Credit Life");
        return  creditLifeService.deleteUploadCreditLife(creditIds);
    }

            @RequestMapping(value = {"unprocesscreditlife"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<CreditLifeDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return creditLifeService.findUnprocessedCreditLife(pageable);
    }

    @RequestMapping(value = {"viewunprocesscreditlife"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<CreditLifeDTO> getUnProcessedPackagedInsur(@DataTable DataTablesRequest pageable) {
        return creditLifeService.viewUnprocessedCreditLife(pageable);
    }

    @RequestMapping(value = {"processsinglcreditlife/{creditId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> processSingleCreditlifePol(@PathVariable("creditId") Long creditId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed single credit life policy with ID: " + creditId, request, "Processing Single Credit Life");
        System.out.println("creditId: " + creditId);
        PolicyTrans policyTrans = creditLifeService.processSingleCreditLifePol(creditId, userUtils.getCurrentUser().getId(), true);
        Long polCode = policyTrans.getPolicyId();
        System.out.println("Pol Code: " + polCode);
//        if ("NB".equalsIgnoreCase(policyTrans.getTransType()) || "SP".equalsIgnoreCase(policyTrans.getTransType())
//                || "EX".equalsIgnoreCase(policyTrans.getTransType()) || "RN".equalsIgnoreCase(policyTrans.getTransType())
//                || "BU".equalsIgnoreCase(policyTrans.getTransType()))
//            try {
//                bulkPremComputeService.computeUploadLifePrem(polCode);
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new BadRequestException(e.getMessage());
//            }
//        else if ("EN".equalsIgnoreCase(policyTrans.getTransType())) {
//            try {
//                bulkPremComputeService.computeUploadLifePrem(polCode);
//            } catch (IOException e) {
//                throw new BadRequestException(e.getMessage());
//            }
//        }
        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }

    @RequestMapping(value = {"processbulkcreditlife"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> processBulkCreditlifePols(@RequestBody List<Long> creditIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed credit life bulk policies with IDs: " + creditIds, request, "Process Bulk Credit Life");
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
            for (Long id : creditIds) {
                counter++;
                BatchRecordDTO dto = new BatchRecordDTO();
                dto.setBatchId(id);
                dto.setUserId(userId);
                org.easybatch.core.record.Header header =
                        new Header(counter, "Credit Shield Header", new Date());
                batchRecords.add(new BatchRecord(header, dto));
            }

            BatchCreditBatch batch = new BatchCreditBatch();
            batch.setBatchRecords(batchRecords);

            try {
                creditLifeService.processBulkCreditLifePol(batch, true);
                batchStatusService.setStatus(jobId, "COMPLETED");
            } catch (BadRequestException e) {
                e.printStackTrace();
                batchStatusService.setStatus(jobId, "FAILED");
            }
        });
        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);
//        List<Long> polCodes = creditLifeService.processBulkCreditLifePol(creditIds, true);
//        System.out.println("credit life polCodes: " + polCodes);
//        PolicyTrans policyTrans = null;
//        for(Long polCode:polCodes){
//            System.out.println("credit Pol Code: " + polCode);
//            policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(polCode));
//            if ("NB".equalsIgnoreCase(policyTrans.getTransType()) || "SP".equalsIgnoreCase(policyTrans.getTransType())
//                    || "EX".equalsIgnoreCase(policyTrans.getTransType()) || "RN".equalsIgnoreCase(policyTrans.getTransType())
//                    || "BU".equalsIgnoreCase(policyTrans.getTransType()))
//                try {
//                    bulkPremComputeService.computeUploadLifePrem(polCode);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    throw new BadRequestException(e.getMessage());
//                }
//            else if ("EN".equalsIgnoreCase(policyTrans.getTransType())) {
//                try {
//                    bulkPremComputeService.computeUploadLifePrem(polCode);
//                } catch (IOException e) {
//                    throw new BadRequestException(e.getMessage());
//                }
//            }
//        }
        //return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }

    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Accessed edit underwriting transactions screen: " , request, "Edit Uw Trans");
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

    @RequestMapping(value = {"approvesinglecredlife/{policyId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveSingleCreditLifePolicy(@PathVariable("policyId") Long policyId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved single credit life policy with ID: " + policyId, request, "Approve Single Credit Life");
        return creditLifeService.approveSingleCreditLifePol(policyId);
    }

    @RequestMapping(value = {"approvebulkcredlife"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveBulkPolicy(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk policies with IDs: " + policyIds, request, "Approve Bulk Credit Life");
        return creditLifeService.approveBulkCreditLifePol(policyIds);
    }

    @RequestMapping(value = {"deleteprocessedbulkcredlife"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteProcessedBulkCreditLifePol(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("delete processed bulk policies with IDs: " + policyIds, request, "Delete Processed Bulk Credit Life");
        return creditLifeService.deleteProcessedBulkCreditLifePol(policyIds);
    }

    @RequestMapping(value = {"bulkcreditlifetemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-credit-life-upload-template.xlsx");

        String[] sheet1Headers = {
                "ID No","Client CIF","Insurer Code",
                "Product Group", "Product Name","Contract", "Cover Type", "Term", "Payment Frequency",
                "Currency ISO Code", "Policy Branch", "Sum Insured", "Premium Amount",
                "Transaction Date", "Start Date", "Loan Id"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Credit Life");
        // Create the header row
        Row headerRow = sheet1.createRow(0);
        for (int i = 0; i < sheet1Headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(sheet1Headers[i]);
            sheet1.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        //workbook.close();
    }

    @RequestMapping(value = "download-error-file/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadErrorFile(@PathVariable("fileId") String fileId, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream workbookData = errorWorkbookCache.getWorkbook(fileId);

        if (workbookData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Error file not found.");
            return;
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=CreditLifeUploadErrors.xlsx");
        response.getOutputStream().write(workbookData.toByteArray());
        response.flushBuffer();
    }
}
