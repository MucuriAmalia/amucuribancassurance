package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.dtos.TimizaDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecord;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecordDTO;
import com.brokersystems.brokerapp.bulktransactions.service.BatchStatusService;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPremComputeService;
import com.brokersystems.brokerapp.bulktransactions.service.TimizaService;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/protected/bulk/timiza")
public class TimizaController {

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private BulkPremComputeService bulkPremComputeService;

    @Autowired
    private TimizaService timizaService;
    @Autowired
    private BatchStatusService batchStatusService;
    @Autowired
    private UserUtils userUtils;


    @RequestMapping(value = "createtimiza", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed timiza upload screen", request, "Create timiza");
        return "createtimiza";
    }

    @RequestMapping(value = "viewtimiza", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed timiza upload authorization screen", request, "View timiza");
        return "viewtimiza";
    }

    @RequestMapping(value = "uploadtimiza", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadTimizaExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException, IOException {
        auditTrailLogger.log("Uploaded timiza upload file: " + file, request, "Upload timiza");

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            // Validate only the first row (header)
            List<String> validationErrors = ExcelReaderUtil.validateTimizaPolicyTemplate(workbook);
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
        timizaService.uploadTimiza(tempFile, userUtils.getCurrentUser().getId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Accepted");
        response.put("message", "File is being processed.");
        return response;

//        String fileName = file.getOriginalFilename();
//        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
//            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
//        }
//        // Validate template format
//        List<String> validationErrors = ExcelReaderUtil.validateTimizaPolicyTemplate(workbook);
//        if (!validationErrors.isEmpty()) {
//            StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
//            for (String error : validationErrors) {
//                errorMessage.append(error).append("; ");
//            }
//            throw new BadRequestException(errorMessage.toString());
//        }
//
//
//        return timizaService.uploadTimiza(file);
    }

    @RequestMapping(value = {"deleteuploadtimiza"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteUploadBulkTimizaPols(@RequestBody List<Long> timizaIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete uploaded timiza bulk policies with IDs: " + timizaIds, request, "delete Bulk timiza");
        return  timizaService.deleteUploadBulkTimizaPols(timizaIds);
    }

    @RequestMapping(value = {"deleteprocessedbulkTimiza"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteProcessedTimizaPols(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("delete processed bulk policies with IDs: " + policyIds, request, "Delete Processed timiza");
        return timizaService.deleteProcessedTimizaPols(policyIds);
    }



    @RequestMapping(value = {"unprocesstimiza"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<TimizaDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return timizaService.findUnprocessedTimiza(pageable);
    }

    @RequestMapping(value = {"viewunprocesstimiza"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<TimizaDTO> getUnProcessedTimizaPol(@DataTable DataTablesRequest pageable) {
        return timizaService.viewUnprocessedTimiza(pageable);
    }

    @RequestMapping(value = {"processsingltimiza/{timizaId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> processSingleTimizaPol(@PathVariable("timizaId") Long timizaId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed single timiza policy with ID: " + timizaId, request, "Processing Single timiza");
        System.out.println("timizaId: " + timizaId);
        PolicyTrans policyTrans = timizaService.processSingleTimizaPol(timizaId, userUtils.getCurrentUser().getId(), true);
//        Long polCode = policyTrans.getPolicyId();
//        System.out.println("Pol Code: " + polCode);
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

    @RequestMapping(value = {"processbulktimiza"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> processBulkTimizaPols(@RequestBody List<Long> timizaIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed timiza bulk policies with IDs: " + timizaIds, request, "Processing Bulk timiza");


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
            for (Long id : timizaIds) {
                counter++;
                BatchRecordDTO dto = new BatchRecordDTO();
                dto.setBatchId(id);
                dto.setUserId(userId);
                org.easybatch.core.record.Header header =
                        new Header(counter, "Timiza Header", new Date());
                batchRecords.add(new BatchRecord(header, dto));
            }

            BatchCreditBatch batch = new BatchCreditBatch();
            batch.setBatchRecords(batchRecords);

            try {
                timizaService.processBulkTimizaPol(batch, true);
                batchStatusService.setStatus(jobId, "COMPLETED");
            } catch (BadRequestException e) {
                e.printStackTrace();
                batchStatusService.setStatus(jobId, "FAILED");
            }
        });
        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);
//        List<Long> polCodes = timizaService.processBulkTimizaPol(timizaIds, true);
//        System.out.println("timiza polCodes: " + polCodes);
//        PolicyTrans policyTrans = null;
//        for(Long polCode:polCodes){
//            System.out.println("timiza Pol Code: " + polCode);
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
        //}
        //return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }

    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Accessed edit underwriting transactions screen: " , request, "Edit uw trans");
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

    @RequestMapping(value = {"approvesingletimiza/{policyId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveSingleTimizaPolicy(@PathVariable("policyId") Long policyId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved single timiza policy with ID: " + policyId, request, "Approve Single timiza");
        return timizaService.approveSingleTimizaPol(policyId);
    }

    @RequestMapping(value = {"approvebulktimiza"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveBulkTimizaPolicy(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk timiza policies with IDs: " + policyIds, request, "Approve Bulk timiza");
        return timizaService.approveBulkTimizaPol(policyIds);
    }

    @RequestMapping(value = {"bulktimizatemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Bulk-upload-timiza-template.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Timiza");

        String[] headers = {
                "ID No", "Client CIF","Transaction Date",
                "Transaction ID", "Payment Mode", "Branch Code", "Insurer Code", "Product Group",
                "Product Name", "Cover Type", "Currency ISO Code", "Frequency", "Premium",
                "Start Date", "End Date", "Term"
        };

        // Create Header Row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            sheet.autoSizeColumn(i);
        }

        // Write the file to the response output stream
        workbook.write(response.getOutputStream());
        //workbook.close();
    }
}
