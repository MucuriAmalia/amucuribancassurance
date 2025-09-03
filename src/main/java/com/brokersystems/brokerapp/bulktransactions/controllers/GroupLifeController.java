package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.dtos.GroupLifeDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecord;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecordDTO;
import com.brokersystems.brokerapp.bulktransactions.service.BatchStatusService;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPremComputeService;
import com.brokersystems.brokerapp.bulktransactions.service.GroupLifeService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
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
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/protected/group/life")
public class GroupLifeController {

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private BulkPremComputeService bulkPremComputeService;

    @Autowired
    private GroupLifeService groupLifeService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private BatchStatusService batchStatusService;

    @RequestMapping(value = "creategrouplife", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed group life upload screen", request, "Create Group Life");
        return "creategrouplife";
    }

    @RequestMapping(value = "viewgrouplife", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed group life upload authorization screen", request, "View Group Life");
        return "viewgrouplife";
    }

    @RequestMapping(value = "uploadgrouplife", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadFirstAssExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException, IOException {
        auditTrailLogger.log("Uploaded group life upload file: " + file, request, "Upload Group Life");
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            // Validate only the first row (header)
            List<String> validationErrors = ExcelReaderUtil.validateGroupLifePolicyTemplate(workbook);
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
        groupLifeService.uploadGroupLife(tempFile, userUtils.getCurrentUser().getId());

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
//        List<String> validationErrors = ExcelReaderUtil.validateGroupLifePolicyTemplate(workbook);
//        if (!validationErrors.isEmpty()) {
//            StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
//            for (String error : validationErrors) {
//                errorMessage.append(error).append("; ");
//            }
//            throw new BadRequestException(errorMessage.toString());
//        }
//
//        return groupLifeService.uploadGroupLife(file);
    }

    @RequestMapping(value = {"unprocessgrouplife"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<GroupLifeDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return groupLifeService.findUnprocessedGroupLife(pageable);
    }

    @RequestMapping(value = {"viewunprocessgrouplife"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<GroupLifeDTO> getUnProcessedGroupLifePol(@DataTable DataTablesRequest pageable) {
        return groupLifeService.viewUnprocessedGroupLife(pageable);
    }

    @RequestMapping(value = {"processsinglgrouplife/{groupId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> processSingleGroupLifePol(@PathVariable("groupId") Long groupId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed single group life policy with ID: " + groupId, request, "Processing Single Group Life");
        System.out.println("groupId: " + groupId);
        PolicyTrans policyTrans = groupLifeService.processSingleGroupLifePol(groupId, userUtils.getCurrentUser().getId(), true);
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

    @RequestMapping(value = {"processbulkgrouplife"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> processBulkGroupLifePols(@RequestBody List<Long> groupIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed group life bulk policies with IDs: " + groupIds, request, "Processing Bulk Group Life");
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
            for (Long id : groupIds) {
                counter++;
                BatchRecordDTO dto = new BatchRecordDTO();
                dto.setBatchId(id);
                dto.setUserId(userId);
                org.easybatch.core.record.Header header =
                        new Header(counter, "Group life Header", new Date());
                batchRecords.add(new BatchRecord(header, dto));
            }

            BatchCreditBatch batch = new BatchCreditBatch();
            batch.setBatchRecords(batchRecords);

            try {
                groupLifeService.processBulkGroupLifePol(batch, true);
                batchStatusService.setStatus(jobId, "COMPLETED");
            } catch (BadRequestException e) {
                e.printStackTrace();
                batchStatusService.setStatus(jobId, "FAILED");
            }
        });
        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);


//        List<Long> polCodes = groupLifeService.processBulkGroupLifePol(groupIds, true);
//        System.out.println("group life polCodes: " + polCodes);
//        PolicyTrans policyTrans = null;
//        for(Long polCode:polCodes){
//            System.out.println("group Pol Code: " + polCode);
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

    @RequestMapping(value = {"deletebulkgrouplife"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteBulkGroupLifePols(@RequestBody List<Long> groupIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete group life bulk policies with IDs: " + groupIds, request, "Processing Bulk Group Life");
         return  groupLifeService.deleteBulkGroupLifePol(groupIds);
    }

    @RequestMapping(value = {"processbulkdelprocessedpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public String bulkDelProcessedPols(@RequestBody List<Long> groupIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete processed group life bulk policies with IDs: " + groupIds, request, "Bulk Delete Processed Policies");
        return groupLifeService.bulkDelProcessedPolicies(groupIds);
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

    @RequestMapping(value = {"approvesinglegrouplife/{policyId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveSingleGroupLifePolicy(@PathVariable("policyId") Long policyId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved single group life policy with ID: " + policyId, request, "Approve Single group Life");
        return groupLifeService.approveSingleGroupLifePol(policyId);
    }

    @RequestMapping(value = {"approvebulkgrouplife"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveBulkGroupLifePolicy(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk Group Life policies with IDs: " + policyIds, request, "Approve Bulk Group Life");
        return groupLifeService.approveBulkGroupLifePol(policyIds);
    }

    @RequestMapping(value = {"bulkgrouplifetemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-group-life-upload-template.xlsx");

        String[] sheet1Headers = {
                "Serial No", "Reg/ID No", "Client CIF", "Branch Code", "Insurer Code", "Product Group",
                "Product Name", "Cover Type", "Term", "Frequency", "Currency ISO Code",
                "Start Date", "End Date", "Contract"
        };

        String[] sheet2Headers = {
                "Serial No", "ID No", "Insured CIF", "Employee Code",
                "Insured Branch Code", "Insurer Code", "Sum Insured",
                "Premium", "Transaction Date"
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
