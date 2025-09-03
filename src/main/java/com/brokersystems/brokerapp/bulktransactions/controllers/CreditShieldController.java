package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.dtos.CreditCardDTO;
import com.brokersystems.brokerapp.bulktransactions.dtos.CreditShieldDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecord;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecordDTO;
import com.brokersystems.brokerapp.bulktransactions.service.BatchStatusService;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPremComputeService;
import com.brokersystems.brokerapp.bulktransactions.service.CreditCardService;
import com.brokersystems.brokerapp.bulktransactions.service.CreditShieldService;
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
import com.brokersystems.brokerapp.uw.model.RenewalRecord;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
@RequestMapping("/protected/credit/shield")
public class CreditShieldController {

    @Autowired
    private CreditShieldService creditShieldService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private BulkPremComputeService bulkPremComputeService;

    @Autowired
    private UserUtils userUtils;
    @Autowired
    private BatchStatusService batchStatusService;


    @RequestMapping(value = "createcreditshield", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed Credit Shield upload screen", request, "Create Credit Shield");
        return "createcreditshield";
    }

    @RequestMapping(value = "viewcreditshield", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed Credit shield upload authorization screen", request, "View Credit Shield");
        return "viewcreditshield";
    }

    @RequestMapping(value = "uploadcreditshield", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadFirstAssExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException, IOException, InvalidFormatException {
        auditTrailLogger.log("Uploaded Credit Card upload file: " + file, request, "Upload Credit Card");
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            // Validate only the first row (header)
            List<String> validationErrors = ExcelReaderUtil.validateCreditShieldPolicyTemplate(workbook);
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
        creditShieldService.uploadCreditShield(tempFile, userUtils.getCurrentUser().getId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Accepted");
        response.put("message", "File is being processed.");
        return response;
    }

    @RequestMapping(value = {"unprocesscreditshield"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<CreditShieldDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return creditShieldService.findUnprocessedCreditShield(pageable);
    }

    @RequestMapping(value = {"viewunprocesscreditshield"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<CreditShieldDTO> getUnProcessedPackagedInsur(@DataTable DataTablesRequest pageable) {
        return creditShieldService.viewUnprocessedCreditShield(pageable);
    }

    @RequestMapping(value = {"processsinglcreditshield/{shieldId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> processSingleCreditShieldPol(@PathVariable("shieldId") Long shieldId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed single Credit Shield policy with ID: " + shieldId, request, "Processing Single Credit Shield");
        System.out.println("shieldId: " + shieldId);
        PolicyTrans policyTrans = creditShieldService.processSingleCreditShieldPol(shieldId, userUtils.getCurrentUser().getId(), true);
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
    @RequestMapping(value = {"deletebulkcreditshield"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteBulkCreditShieldPols(@RequestBody List<Long> cardIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete Credit shield bulk policies with IDs: " + cardIds, request, "Delete upload Bulk Credit shield");
        return  creditShieldService.deleteBulkCreditShieldPol(cardIds);

    }

    @RequestMapping(value = {"deleteprocessedbulkcredShield"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteProcessedBulkCreditShieldPol(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("delete processed bulk policies with IDs: " + policyIds, request, "Delete Processed Bulk Credit Shield");
        return creditShieldService.deleteProcessedBulkCreditShieldPol(policyIds);
    }


    @RequestMapping(value = {"processbulkcreditshield"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> processBulkCreditShieldPols(@RequestBody List<Long> cardIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed Credit Card bulk policies with IDs: " + cardIds, request, "Process Bulk Credit Card");
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
            for (Long id : cardIds) {
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
                creditShieldService.processBulkCreditShieldPol(batch, true);
                batchStatusService.setStatus(jobId, "COMPLETED");
            } catch (BadRequestException e) {
                e.printStackTrace();
                batchStatusService.setStatus(jobId, "FAILED");
            }
        });
        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);
    }


    @RequestMapping(value = "/status/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getJobStatus(@PathVariable String jobId) {
        System.out.println(batchStatusService.getStatus(jobId));
        Map<String, String> response = new HashMap<>();
        response.put("status", batchStatusService.getStatus(jobId));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/all-ids", method = RequestMethod.GET)
    public ResponseEntity<List<Long>> getAllIds() {
        return  ResponseEntity.ok(creditShieldService.getAllCreditShieldIds());
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

    @RequestMapping(value = {"approvesinglercreditshield/{policyId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveSingleCreditCardPolicy(@PathVariable("policyId") Long policyId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved single Credit Card policy with ID: " + policyId, request, "Approve Single Credit Card");
        return creditShieldService.approveSingleCreditShieldPol(policyId);
    }

    @RequestMapping(value = {"approvebulkcreditshield"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveBulkPolicy(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk policies with IDs: " + policyIds, request, "Approve Bulk Credit Shield");
        return creditShieldService.approveBulkCreditShieldPol(policyIds);
    }




    @RequestMapping(value = {"bulkcreditshieldtemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-credit-shield-upload-template.xlsx");

        String[] sheet1Headers = {
                "ID No", "Client CIF", "Sales Agent",
                "Sales Manager", "Sales Code", "Card Type", "Card Account", "Insurer Code",
                "Product Group", "Product Name", "Cover Type", "Payment Frequency", "Currency ISO Code",
                "Branch", "Premium Amount", "Date Booked", "Start Date", "End Date"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Credit Shield");
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
}
