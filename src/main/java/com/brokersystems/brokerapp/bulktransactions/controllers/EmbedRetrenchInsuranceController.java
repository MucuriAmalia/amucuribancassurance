package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.dtos.EmbedPackageInsuranceDTO;
import com.brokersystems.brokerapp.bulktransactions.dtos.EmbedRetrenchInsuranceDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecord;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecordDTO;
import com.brokersystems.brokerapp.bulktransactions.service.BatchStatusService;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPremComputeService;
import com.brokersystems.brokerapp.bulktransactions.service.EmbedRetrenchInsuranceService;
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
@RequestMapping("/protected/embedretrench/insurance")
public class EmbedRetrenchInsuranceController {

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private BulkPremComputeService bulkPremComputeService;

    @Autowired
    private EmbedRetrenchInsuranceService embedRetrenchInsuranceService;

    @Autowired
    private PolicyTransService policyService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private BatchStatusService batchStatusService;

    @RequestMapping(value = "creatembedretrench", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed embedded retrenchment policy processing screen", request, "Embedded retrenchment Policy");
        return "creatembedretrench";
    }

    @RequestMapping(value = "viewembedretrench", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed embedded retrenchment policy authorization screen", request, "View Embedded retrenchment Policy");
        return "viewembedretrench";
    }

    @RequestMapping(value = "uploadembedretrench", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadRetrenchmentExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException, IOException {
        auditTrailLogger.log("Uploaded retrenchment policy creation file: " + file, request, "Upload retrenchment Policy");

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            // Validate only the first row (header)
            List<String> validationErrors = ExcelReaderUtil.validateEmbRetrPolicyTemplate(workbook);
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
        embedRetrenchInsuranceService.uploadEmbedRetrench(tempFile, userUtils.getCurrentUser().getId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Accepted");
        response.put("message", "File is being processed.");
        return response;

//        // Validate template format
//        List<String> validationErrors = ExcelReaderUtil.validateEmbRetrPolicyTemplate(workbook);
//        if (!validationErrors.isEmpty()) {
//            StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
//            for (String error : validationErrors) {
//                errorMessage.append(error).append("; ");
//            }
//            throw new BadRequestException(errorMessage.toString());
//        }

        //return embedRetrenchInsuranceService.uploadEmbedRetrench(file);
    }

    @RequestMapping(value = {"unprocessembedretrench"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<EmbedRetrenchInsuranceDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return embedRetrenchInsuranceService.findUnprocessedEmbedRetrench(pageable);
    }

    @RequestMapping(value = {"viewunprocessembedretrench"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<EmbedRetrenchInsuranceDTO> getUnProcessedRetrench(@DataTable DataTablesRequest pageable) {
        return embedRetrenchInsuranceService.viewUnprocessedEmbedRetrench(pageable);
    }

    @RequestMapping(value = {"processsinglretrench/{retrenchId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> processSingleRetrenchPol(@PathVariable("retrenchId") Long retrenchId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed single retrenchment policy with ID: " + retrenchId, request, "Processing Single retrenchment");
        System.out.println("retrenchId: " + retrenchId);
        PolicyTrans policyTrans = embedRetrenchInsuranceService.processSingleEmbedRetrenchPol(retrenchId, userUtils.getCurrentUser().getId(), true);
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

    @RequestMapping(value = {"deletebulkretrench"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteBulkRetrenchPols(@RequestBody List<Long> retrenchIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete retrenchment bulk policies with IDs: " + retrenchIds, request, "Deleting Bulk retrenchment");
        return embedRetrenchInsuranceService.deleteBulkEmbedRetrenchPol(retrenchIds);
    }

    @RequestMapping(value = {"processbulkdelprocessedpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public String bulkDelProcessedPols(@RequestBody List<Long> retrenchIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete processed embedded retrenchment bulk policies with IDs: " + retrenchIds, request, "Bulk Delete Processed Policies");
        return embedRetrenchInsuranceService.bulkDelProcessedPolicies(retrenchIds);
    }

    @RequestMapping(value = {"processbulkretrench"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> processBulkRetrenchPols(@RequestBody List<Long> retrenchIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed retrenchment bulk policies with IDs: " + retrenchIds, request, "Processing Bulk retrenchment");
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
            for (Long id : retrenchIds) {
                counter++;
                BatchRecordDTO dto = new BatchRecordDTO();
                dto.setBatchId(id);
                dto.setUserId(userId);
                org.easybatch.core.record.Header header =
                        new Header(counter, "Emb Pkg Header", new Date());
                batchRecords.add(new BatchRecord(header, dto));
            }

            BatchCreditBatch batch = new BatchCreditBatch();
            batch.setBatchRecords(batchRecords);

            try {
                embedRetrenchInsuranceService.processBulkEmbedRetrenchPol(batch, true);
                batchStatusService.setStatus(jobId, "COMPLETED");
            } catch (BadRequestException e) {
                e.printStackTrace();
                batchStatusService.setStatus(jobId, "FAILED");
            }
        });
        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);

//        List<Long> polCodes = embedRetrenchInsuranceService.processBulkEmbedRetrenchPol(retrenchIds, true);
//        System.out.println("retrenchment polCodes: " + polCodes);
//        PolicyTrans policyTrans = null;
//        for(Long polCode:polCodes){
//            System.out.println("retrench Pol Code: " + polCode);
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
//        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
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

    @RequestMapping(value = {"approvesingleretrench/{policyId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveSingleRetrenchPolicy(@PathVariable("policyId") Long policyId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved single retrenchment policy with ID: " + policyId, request, "Approve Single retrenchment");
        return embedRetrenchInsuranceService.approveSingleEmbedRetrenchPol(policyId);
    }

    @RequestMapping(value = {"approvebulkretrench"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveBulkRetrenchPolicy(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk retrenchment policies with IDs: " + policyIds, request, "Approve Bulk retrenchment");
        return embedRetrenchInsuranceService.approveBulkEmbedRetrenchPol(policyIds);
    }

    @RequestMapping(value = {"bulkembedrettemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-embed-retrenchment-upload-template.xlsx");

        String[] sheet1Headers = {
                "Serial No", "Reg/ID No", "Client CIF", "Branch Code", "Insurer Code", "Product Group", "Product Name",
                "Cover Type", "Frequency", "Currency ISO Code", "Start Date", "End Date"
        };

        String[] sheet2Headers = {
                "Serial No", "Insured ID No", "Insured CIF", "Insured Account No", "Open Date",
                "Transaction Code", "Transaction Date", "Insured Branch Code", "Premium"
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
}
