package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.dtos.BulkRejectionRequest;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecord;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecordDTO;
import com.brokersystems.brokerapp.bulktransactions.service.BatchStatusService;
import com.brokersystems.brokerapp.bulktransactions.service.BulkReceiptService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.uw.dtos.ReceiptsDTO;
import com.brokersystems.brokerapp.uw.validators.RenewalJobListener;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobReport;
import org.easybatch.core.processor.RecordProcessor;
import org.easybatch.core.reader.IterableRecordReader;
import org.easybatch.core.record.Header;
import org.easybatch.core.record.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;

@Controller
@RequestMapping("/protected/receipt/bulk")
public class BulkReceiptController {


    @Autowired
    private AuditTrailLogger auditTrailLogger;
    @Autowired
    private BulkReceiptService receiptService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private BatchStatusService batchStatusService;

    @RequestMapping(value = "createbulkreceipt", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed Bulk receipt upload screen", request, "Create Bulk receipt");
        return "createbulkreceipt";
    }

    @RequestMapping(value = "viewbulkreceipt", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed Bulk receipt upload authorization screen", request, "View Bulk receipt");
        return "viewbulkreceipt";
    }

    @RequestMapping(value = "uploadbulkreceipt", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadReceipts(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException, IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        auditTrailLogger.log("Uploaded Bulk receipt upload file: " + file, request, "Upload Bulk receipt");

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            // Validate template format
            List<String> validationErrors = ExcelReaderUtil.validateBulkReceiptTemplate(workbook);
            if (!validationErrors.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
                for (String error : validationErrors) {
                    errorMessage.append(error).append("; ");
                }
                throw new BadRequestException(errorMessage.toString());
            }
        } catch (Exception e) {
            throw new BadRequestException("Error processing the Excel file: " + e.getMessage());
        }
        String filename = UUID.randomUUID().toString() ;
        File tempFile = File.createTempFile(filename, ".xlsx");
        file.transferTo(tempFile); // save contents to disk
        // Proceed to processing if validation passed
        receiptService.uploadBulkReceipt(tempFile, userUtils.getCurrentUser().getId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Accepted");
        response.put("message", "File is being processed.");
        return response;
        //
        //return receiptService.uploadBulkReceipt(file);
    }

    @RequestMapping(value = {"unprocessbulkreceipt"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ReceiptsDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return receiptService.findUnprocessedBulkReceipt(pageable);
    }

    @RequestMapping(value = {"processedbulkreceipt"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<MakerCheckDTO> getProcessedTrans(@DataTable DataTablesRequest pageable) {
        return receiptService.findProcessedBulkReceipt(pageable);
    }


    @RequestMapping(value = {"approvebulkreceipt"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> approveBulkPolicy(@RequestBody List<Long> receiptIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Process bulk receipts with IDs: " + receiptIds, request, "Process Bulk receipts");
//        if (receiptIds.size() <= 0) {
//            throw new BadRequestException("Select At least One Receipt To Process");
//        }
        //return receiptService.approveBulkReceipting(receiptIds);

        if (batchStatusService.isAnyJobRunning()) {
            throw new BadRequestException("Another batch is already running. Please wait for it to complete");
        }
        String jobId = UUID.randomUUID().toString();
        System.out.println("Processing job id "+jobId);
        batchStatusService.setStatus(jobId, "RUNNING");

        Long userId = userUtils.getCurrentUser().getId();

        Runnable task = () -> {
            List<BatchRecord> batchRecords = new ArrayList<>();
            long counter = 0L;
            for (Long id : receiptIds) {
                counter++;
                BatchRecordDTO dto = new BatchRecordDTO();
                dto.setBatchId(id);
                dto.setUserId(userId);
                org.easybatch.core.record.Header header =
                        new Header(counter, "Bulk receipting Header", new Date());
                batchRecords.add(new BatchRecord(header, dto));
            }

            BatchCreditBatch batch = new BatchCreditBatch();
            batch.setBatchRecords(batchRecords);

            try {
                receiptService.approveBulkReceipting(batch);
                batchStatusService.setStatus(jobId, "COMPLETED");
            } catch (BadRequestException e) {
                e.printStackTrace();
                batchStatusService.setStatus(jobId, "FAILED");
            }
        };

        Runnable securedTask = new DelegatingSecurityContextRunnable(task);
        CompletableFuture.runAsync(securedTask);

        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"approvebulkreceipttasks"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> approveBulkReceiptedPolicy(@RequestBody List<Long> taskids, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk receipts with IDs: " + taskids, request, "Approve Bulk receipts");
        //return receiptService.approveBulkReceiptingTaskId(taskids);

        if (batchStatusService.isAnyJobRunning()) {
            throw new BadRequestException("Another batch is already running. Please wait for it to complete");
        }
        String jobId = UUID.randomUUID().toString();
        System.out.println("Processing job id "+jobId);
        batchStatusService.setStatus(jobId, "RUNNING");

        Long userId = userUtils.getCurrentUser().getId();

        Runnable task = () -> {
            List<BatchRecord> batchRecords = new ArrayList<>();
            long counter = 0L;
            for (Long id : taskids) {
                counter++;
                BatchRecordDTO dto = new BatchRecordDTO();
                dto.setBatchId(id);
                dto.setUserId(userId);
                org.easybatch.core.record.Header header =
                        new Header(counter, "Bulk approve receipting Header", new Date());
                batchRecords.add(new BatchRecord(header, dto));
            }

            BatchCreditBatch batch = new BatchCreditBatch();
            batch.setBatchRecords(batchRecords);

            try {
                receiptService.approveBulkReceiptingTaskId(batch);
                batchStatusService.setStatus(jobId, "COMPLETED");
            } catch (BadRequestException e) {
                e.printStackTrace();
                batchStatusService.setStatus(jobId, "FAILED");
            }
        };

        Runnable securedTask = new DelegatingSecurityContextRunnable(task);
        CompletableFuture.runAsync(securedTask);

        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"rejectbulkreceipttasks"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> rejectBulkReceiptedPolicy(@RequestBody BulkRejectionRequest request, HttpServletRequest httpRequest) throws BadRequestException {
        auditTrailLogger.log("Reject bulk receipts with IDs: " + request.getTaskId(), httpRequest, "Reject Bulk receipts");
        //return receiptService.rejectBulkReceiptingTaskId(request.getTaskId(), null, request.getReason());
        if (batchStatusService.isAnyJobRunning()) {
            throw new BadRequestException("Another batch is already running. Please wait for it to complete");
        }
        String jobId = UUID.randomUUID().toString();
        System.out.println("Processing job id "+jobId);
        batchStatusService.setStatus(jobId, "RUNNING");

        Long userId = userUtils.getCurrentUser().getId();

        Runnable task = () -> {
            List<BatchRecord> batchRecords = new ArrayList<>();
            long counter = 0L;
            for (Long id : request.getTaskId()) {
                counter++;
                BatchRecordDTO dto = new BatchRecordDTO();
                dto.setBatchId(id);
                dto.setUserId(userId);
                dto.setRejectReason(request.getReason());
                org.easybatch.core.record.Header header =
                        new Header(counter, "Bulk approve receipting Header", new Date());
                batchRecords.add(new BatchRecord(header, dto));
            }

            BatchCreditBatch batch = new BatchCreditBatch();
            batch.setBatchRecords(batchRecords);

            try {
                receiptService.rejectBulkReceiptingTaskId(batch);
                batchStatusService.setStatus(jobId, "COMPLETED");
            } catch (BadRequestException e) {
                e.printStackTrace();
                batchStatusService.setStatus(jobId, "FAILED");
            }
        };

        Runnable securedTask = new DelegatingSecurityContextRunnable(task);
        CompletableFuture.runAsync(securedTask);

        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = {"deletebulkreceipt"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteBulkPolicy(@RequestBody List<Long> receiptIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk receipts with IDs: " + receiptIds, request, "Delete Bulk receipts upload");
        return receiptService.deleteBulkReceipting(receiptIds);
    }

    @RequestMapping(value = {"bulkreceiptemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-policy-receipt-upload-template.xls");

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bulk Receipt");

        // Create just the header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Policy No");
        header.createCell(1).setCellValue("Policy ref/Proposal No");
        header.createCell(2).setCellValue("Document Date");
        header.createCell(3).setCellValue("Payment Mode");
        header.createCell(4).setCellValue("Insurer Code");
        header.createCell(5).setCellValue("Branch Code");
        header.createCell(6).setCellValue("Receipt Amount");
        header.createCell(7).setCellValue("Paid By");
        header.createCell(8).setCellValue("Receipt Ref");
        header.createCell(9).setCellValue("Manual Ref");
        header.createCell(10).setCellValue("Narration");




        // Optionally: auto-size columns
        for (int i = 0; i <= 11; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        //workbook.close();
    }
}
