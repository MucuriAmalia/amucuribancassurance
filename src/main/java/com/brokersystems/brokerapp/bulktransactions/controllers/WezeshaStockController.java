package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecord;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecordDTO;
import com.brokersystems.brokerapp.bulktransactions.service.BatchStatusService;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPremComputeService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.bulktransactions.dtos.WezeshaStockDTO;
import com.brokersystems.brokerapp.bulktransactions.service.WezeshaStockService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/protected/wezesha/polcreation")
public class WezeshaStockController {

    @Autowired
    private AuditTrailLogger auditTrailLogger;
    @Autowired
    private BulkPremComputeService bulkPremComputeService;
    @Autowired
    private PremComputeService premiumService;
    @Autowired
    private PolicyTransService policyService;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private WezeshaStockService wezeshaStockService;
    @Autowired
    private BatchStatusService batchStatusService;
    @Autowired
    private UserUtils userUtils;

    @RequestMapping(value = "wezeshaCreation", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed Wezesha Policy Processing Home", request, "Wezesha Policy Processing");
        return "wezeshaCreation";
    }

    @RequestMapping(value = "viewWezesha", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed wezesha Policy Authorization Home", request, "Wezesha Policy Authorization");
        return "viewWezesha";
    }

    @RequestMapping(value = "uploadwezeshapol", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadWezeshaExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Uploaded wezesha policy creation file: " + file, request, "Upload Wezesha");
        return wezeshaStockService.uploadWezeshaStock(file);
    }

    @RequestMapping(value = {"uploadwezeshapoldelete"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadWezeshaPolDelete(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk uploaded wezesha policies with IDs: " + policyIds, request, "Delete bulk wezesha policies");
        return wezeshaStockService.deleteUploadWezeshaStock(policyIds);
    }

    @RequestMapping(value = {"processedwezeshapoldelete"}, method = {RequestMethod.POST})
    @ResponseBody
    public String ProcessedWezeshaPolDelete(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk uploaded wezesha policies with IDs: " + policyIds, request, "Delete bulk wezesha policies");
        return wezeshaStockService.deleteProcessedWezeshaStock(policyIds);
    }


    @RequestMapping(value = {"unprocessedwezeshapol"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<WezeshaStockDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return wezeshaStockService.findUnProcessedWezeshaPol(pageable);
    }

    @RequestMapping(value = {"viewwezeshapolicies"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<WezeshaStockDTO> getUnprocessedWezeshaPol(@DataTable DataTablesRequest pageable) {
        return wezeshaStockService.viewWezeshaPolicies(pageable);
    }

    @RequestMapping(value = {"processsinglewezesha/{wezeshaId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> processSingleWezeshaPolicy(@PathVariable("wezeshaId") Long wezeshaId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed single Wezesha policy with ID: " + wezeshaId, request, "Processing Single Wezesha");
        System.out.println("wezeshaId: " + wezeshaId);
        PolicyTrans singleWezeshPol = wezeshaStockService.processSingleWezeshPol(wezeshaId, userUtils.getCurrentUser().getId(), true);
//        Long polCode = singleWezeshPol.getPolicyId();
//        System.out.println("Pol Code: " + polCode);
//        if ("NB".equalsIgnoreCase(singleWezeshPol.getTransType()) || "SP".equalsIgnoreCase(singleWezeshPol.getTransType())
//                || "EX".equalsIgnoreCase(singleWezeshPol.getTransType()) || "RN".equalsIgnoreCase(singleWezeshPol.getTransType())
//                || "BU".equalsIgnoreCase(singleWezeshPol.getTransType()))
//            try {
//                System.out.println("Call Single Compute premium");
//                bulkPremComputeService.computeUploadGeneralPrem(polCode);
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new BadRequestException(e.getMessage());
//            }
//        else if ("EN".equalsIgnoreCase(singleWezeshPol.getTransType())) {
//            try {
//                bulkPremComputeService.computeUploadGeneralPrem(polCode);
//            } catch (IOException e) {
//                throw new BadRequestException(e.getMessage());
//            }
//        }
        return new ResponseEntity<PolicyTrans>(singleWezeshPol, HttpStatus.OK);
    }

    @RequestMapping(value = {"processbulkwezeshapol"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> bulkPolProcessing(@RequestBody List<Long> transIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed wezesha bulk policies with IDs: " + transIds, request, "Processing Wezesha Bulk");

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
            for (Long id : transIds) {
                counter++;
                BatchRecordDTO dto = new BatchRecordDTO();
                dto.setBatchId(id);
                dto.setUserId(userId);
                org.easybatch.core.record.Header header =
                        new Header(counter, "Wezesha Header", new Date());
                batchRecords.add(new BatchRecord(header, dto));
            }

            BatchCreditBatch batch = new BatchCreditBatch();
            batch.setBatchRecords(batchRecords);

            try {
                wezeshaStockService.bulkProcessWezeshaPolicies(batch, true);
                batchStatusService.setStatus(jobId, "COMPLETED");
            } catch (BadRequestException e) {
                e.printStackTrace();
                batchStatusService.setStatus(jobId, "FAILED");
            }
        });
        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        return ResponseEntity.ok(response);

//        List<Long> polCodes = wezeshaStockService.bulkProcessWezeshaPolicies(transIds, true);
//        System.out.println("polCodes: " + polCodes);
//        PolicyTrans policyTrans = null;
//        for(Long polCode:polCodes){
//            System.out.println("Pol Code: " + polCode);
//            policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(polCode));
//            if ("NB".equalsIgnoreCase(policyTrans.getTransType()) || "SP".equalsIgnoreCase(policyTrans.getTransType())
//                    || "EX".equalsIgnoreCase(policyTrans.getTransType()) || "RN".equalsIgnoreCase(policyTrans.getTransType())
//                    || "BU".equalsIgnoreCase(policyTrans.getTransType()))
//                try {
//                    System.out.println("Call Bulk Compute premium");
//                    bulkPremComputeService.computeUploadGeneralPrem(polCode);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    throw new BadRequestException(e.getMessage());
//                }
//            else if ("EN".equalsIgnoreCase(policyTrans.getTransType())) {
//                try {
//                    bulkPremComputeService.computeUploadGeneralPrem(polCode);
//                } catch (IOException e) {
//                    throw new BadRequestException(e.getMessage());
//                }
//            }
//        }
//        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }

    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Accessed edit underwriting transactions screen: " , request, "Edit Underwriting Transactions");
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

    @RequestMapping(value = {"approvesinglewezesha/{policyId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveSingleGroupLifePolicy(@PathVariable("policyId") Long policyId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved single wezesha policy with ID: " + policyId, request, "Approved single wezesha policy");
        return wezeshaStockService.approveSingleWezeshaPol(policyId);
    }

    @RequestMapping(value = {"approvebulkwezesha"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveBulkGroupLifePolicy(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk wezesha policies with IDs: " + policyIds, request, "Approved bulk wezesha policies");
        return wezeshaStockService.approveBulkWezeshaPol(policyIds);
    }

    @RequestMapping(value = {"bulkwezeshatemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-wezesha-stock-upload-template.xlsx");

        String[] sheet1Headers = {
                "Loan Id", "ID No", "Client CIF",
                "Business Location", "Type of Business", "Transaction Date", "Branch Code",
                "Insurer Code", "Product Group", "Product Name", "Cover Type", "Currency ISO Code",
                "Frequency", "Value of Stock to Insured", "Premium", "Start Date", "End Date",
                "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Wezesha stock");
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
