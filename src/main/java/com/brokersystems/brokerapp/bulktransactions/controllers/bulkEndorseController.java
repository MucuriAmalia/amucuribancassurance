package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.ErrorsCache.ErrorWorkbookCache;
import com.brokersystems.brokerapp.bulktransactions.dtos.BulkPolicyCreationDTO;
import com.brokersystems.brokerapp.bulktransactions.dtos.BulkRejectionRequest;
import com.brokersystems.brokerapp.bulktransactions.models.BulkRenewalError;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkRenewalErrorRepo;
import com.brokersystems.brokerapp.bulktransactions.service.BulkNonNBActionService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/protected/endorsement/bulk")
public class bulkEndorseController {

    @Autowired
    private BulkNonNBActionService bulkNonNBActionService;
    @Autowired
    private AuditTrailLogger auditTrailLogger;
    @Autowired
    private BulkRenewalErrorRepo bulkRenewalErrorRepo;

    @Autowired
    private ErrorWorkbookCache errorWorkbookCache;


    @RequestMapping(value = "endorsementcreation", method = {RequestMethod.GET})
    public String transEndorseHome(HttpServletRequest request) {
        return "endorsementcreation";
    }
    @RequestMapping(value = "endorsementauthorization", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        return "endorsementauthorization";
    }
    @RequestMapping(value = "createrenewals", method = {RequestMethod.GET})
    public String createRenewal(HttpServletRequest request){
        return "createrenewals";
    }

    @RequestMapping(value = "authorizerenewals", method = {RequestMethod.GET})
    public String authorizeRenewal(HttpServletRequest request){
        return "authorizerenewals";
    }
    @RequestMapping(value = "cancellationscreation", method = {RequestMethod.GET})
    public String createCancellation(HttpServletRequest request){
        return "cancellationscreation";
    }
    @RequestMapping(value = "cancellationsauthorization", method = {RequestMethod.GET})
    public String authorizeCancellation(HttpServletRequest request){
        return "cancellationsauthorization";
    }
    @RequestMapping(value = "refundscreations", method = {RequestMethod.GET})
    public String createRefunds(HttpServletRequest request){
        return "refundscreations";
    }
    @RequestMapping(value = "refundsauthorization", method = {RequestMethod.GET})
    public String authorizeRefunds(HttpServletRequest request){
        return "refundsauthorization";
    }


    @RequestMapping(value = "uploadBulkENPolExcel", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Uploaded bulk endorsement creation file: " + file, request, "Bulk EN Policy Upload");
        return bulkNonNBActionService.uploadBulkENPolExcel(file);
    }
    @RequestMapping(value = {"unProcessedBulkEnPol"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BulkPolicyCreationDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return bulkNonNBActionService.unProcessedBulkEnPol(pageable);
    }

    @RequestMapping(value = {"viewBulkEnPolicies"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BulkPolicyCreationDTO> getUnprocessedBulkPol(@DataTable DataTablesRequest pageable) {
        return bulkNonNBActionService.viewBulkEnPolicies(pageable);
    }
    @RequestMapping(value = {"processbulkenpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> bulkPolProcessing(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException, EndorsementsException, IOException, IllegalAccessException {
        auditTrailLogger.log("Processed bulk En policies with IDs: " + bulkIds, request, "Bulk Process Policies");
        List<Long> polCodes = bulkNonNBActionService.bulkProcessENPolicies(bulkIds, true);
        System.out.println("polCodes: " + polCodes);

        PolicyTrans policyTrans = null;

        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }

    @RequestMapping(value = {"authbulkenreceipt"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> bulkAuthorizeEndorsements(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Auth bulk en with IDs: " + bulkIds, request, "Auth Bulk en upload");
        return bulkNonNBActionService.bulkAuthorizeEndorsement(bulkIds);
    }
    @RequestMapping(value = {"deletebulkenreceipt"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> deleteBulkENPolicy(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk EN with IDs: " + bulkIds, request, "Delete Bulk EN upload");
        return bulkNonNBActionService.bulkDeleteEndorsements(bulkIds);
    }
    @RequestMapping(value = {"processbulkdelenprocessedpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> bulkDelProcessedENPols(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk EN with IDs: " + bulkIds, request, "Delete Bulk EN processed");
        return bulkNonNBActionService.bulkDelProcessedENPolicies(bulkIds);
    }


    //=================
    // RENEWALS
    //==================
    @RequestMapping(value = "uploadBulkRNPolExcel", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadRNExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException, IOException, InvalidFormatException {
        auditTrailLogger.log("Uploaded bulk renwals creation file: " + file, request, "Bulk RN Policy Upload");
        return bulkNonNBActionService.uploadBulkRNPolExcel(file);
    }
    @RequestMapping(value = {"unProcessedBulkRNPol"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BulkPolicyCreationDTO> getUnProcessedRNTrans(@DataTable DataTablesRequest pageable) {
        return bulkNonNBActionService.unProcessedBulkRNPol(pageable);
    }

    @RequestMapping(value = {"viewBulkRNPolicies"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BulkPolicyCreationDTO> getUnprocessedRNBulkPol(@DataTable DataTablesRequest pageable) {
        return bulkNonNBActionService.viewBulkRNPolicies(pageable);
    }
    @RequestMapping(value = {"processbulkrnpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> bulkPolrnProcessing(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException, EndorsementsException {
        auditTrailLogger.log("Processed bulk En policies with IDs: " + bulkIds, request, "Bulk Process Policies");
        List<Long> polCodes = bulkNonNBActionService.bulkProcessRNPolicies(bulkIds, true);
        System.out.println("polCodes: " + polCodes);

        PolicyTrans policyTrans = null;

        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }
    @RequestMapping(value = {"deletebulkrnreceipt"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> deleteBulkRNPolicy(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk RN with IDs: " + bulkIds, request, "Delete Bulk RN upload");
        return bulkNonNBActionService.bulkDeleteRenewals(bulkIds);
    }

    @RequestMapping(value = {"processbulkdelrnprocessedpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> bulkDelProcessedRNPolicies(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk RN with IDs: " + bulkIds, request, "Delete Bulk RN processed");
        return bulkNonNBActionService.bulkDelProcessedRNPolicies(bulkIds);
    }

    @RequestMapping(value = {"bulkauthrenewals"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> bulkAuthRNPolicy(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Auth bulk RN with IDs: " + bulkIds, request, "Auth Bulk RN upload");
        bulkNonNBActionService.bulkAuthorizeRenewals(bulkIds);

        return bulkIds;
    }

    @RequestMapping(value = {"bulkauthreject"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> bulkAuthRejectRNPolicy(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Reject bulk RN with IDs: " + bulkIds, request, "Reject Bulk RN upload");
        bulkNonNBActionService.bulkRejectRenewals(bulkIds);

        return bulkIds;
    }

    @RequestMapping(value = {"uploadErrors"}, method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<List<BulkRenewalError>> getBulkUploadErrors(@RequestParam String batchId) {
        List<BulkRenewalError> errors = bulkRenewalErrorRepo.findByBatchId(batchId);
        return ResponseEntity.ok(errors);
    }

    //======================
     // CANCELLATIONS
     //====================

    @RequestMapping(value = "uploadBulkCNPolExcel", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadCNExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Uploaded bulk CANCELATIONS creation file: " + file, request, "Bulk CN Policy Upload");
        return bulkNonNBActionService.uploadBulkCNPolExcel(file);
    }
    @RequestMapping(value = {"unProcessedBulkCNPol"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BulkPolicyCreationDTO> getUnProcessedCNTrans(@DataTable DataTablesRequest pageable) {
        return bulkNonNBActionService.unProcessedBulkCNPol(pageable);
    }

    @RequestMapping(value = {"viewBulkCNPolicies"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BulkPolicyCreationDTO> getUnprocessedCNBulkPol(@DataTable DataTablesRequest pageable) {
        return bulkNonNBActionService.viewBulkCNPolicies(pageable);
    }
    @RequestMapping(value = {"processbulkcnpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> bulkPolCNProcessing(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed bulk CN policies with IDs: " + bulkIds, request, "Bulk Process Policies");
        List<Long> polCodes = bulkNonNBActionService.bulkProcessCNPolicies(bulkIds, true);
        System.out.println("polCodes: " + polCodes);

        PolicyTrans policyTrans = null;
        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }

    @RequestMapping(value = {"authbulkcnreceipt"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> bulkAuthorizeCancellation(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Auth bulk cn with IDs: " + bulkIds, request, "Auth Bulk cn upload");
        return bulkNonNBActionService.bulkAuthorizeCancellation(bulkIds);
    }

    @RequestMapping(value = {"processbulkdelcnprocessedpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> bulkDelProcessedCNPolicies(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk cn with IDs: " + bulkIds, request, "Delete Bulk cn processed");
        return bulkNonNBActionService.bulkDelProcessedCNPolicies(bulkIds);
    }

    @RequestMapping(value = {"deletebulkcnreceipt"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> deleteBulkCNPolicy(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk cn with IDs: " + bulkIds, request, "Delete Bulk cn upload");
        return bulkNonNBActionService.bulkDeleteCancellation(bulkIds);
    }

    //====================
    //refunds
    //====================

    @RequestMapping(value = "uploadBulkrefundsPolExcel", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadRefundsExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Uploaded bulk refunds creation file: " + file, request, "Bulk Refunds Policy Upload");
        return bulkNonNBActionService.uploadBulkRefundPolExcel(file);
    }
    @RequestMapping(value = {"unProcessedBulkRefundsPol"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BulkPolicyCreationDTO> getUnProcessedRefundsTrans(@DataTable DataTablesRequest pageable) {
        return bulkNonNBActionService.unProcessedBulkRefundPol(pageable);
    }

    @RequestMapping(value = {"viewBulkRefundsPolicies"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BulkPolicyCreationDTO> getUnprocessedRefundsBulkPol(@DataTable DataTablesRequest pageable) {
        return bulkNonNBActionService.viewBulkRefundPolicies(pageable);
    }
    @RequestMapping(value = {"processBulkRefundPolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> bulkPolRefundProcessing(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException, IllegalAccessException {
        auditTrailLogger.log("Processed bulk refunds policies with IDs: " + bulkIds, request, "Bulk Process Policies");
        List<Long> polCodes = bulkNonNBActionService.bulkProcessRefundPolicies(bulkIds, true);
        System.out.println("polCodes: " + polCodes);

        PolicyTrans policyTrans = null;
        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }

    @RequestMapping(value = {"authbulkrefundsreceipt"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> bulkAuthorizeRefunds(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Auth bulk refunds with IDs: " + bulkIds, request, "Auth Bulk refund upload");
        return bulkNonNBActionService.bulkAuthorizeRefunds(bulkIds);
    }

    @RequestMapping(value = {"rejectbulkrefundsreceipt"}, method = {RequestMethod.POST})
    @ResponseBody
    public String bulkRejectRefunds(@RequestBody BulkRejectionRequest request, HttpServletRequest httpRequest) throws BadRequestException {
        auditTrailLogger.log("Reject bulk refund receipts with IDs: " + request.getTaskId(), httpRequest, "Reject Bulk refund receipts");
        return bulkNonNBActionService.bulkARejectRefunds(request.getTaskId(), null, request.getReason());
    }

    @RequestMapping(value = {"deletebulkrefundreceipt"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Long> deleteBulkRefundPolicy(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete bulk refunds with IDs: " + bulkIds, request, "Delete Bulk refunds upload");
        return bulkNonNBActionService.bulkDeleteRefunds(bulkIds);
    }

    //================
    //templates
    //================
    @RequestMapping(value = {"bulkpolentemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcelEnTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-policy-endorse-upload-template.xlsx");
//        System.out.println("Template");
        String[] sheet1Headers = {"Policy No", "Sum Insured", "Premium",  "Endorsement Remarks", "Effective Date"};

        String[] sheet2Headers = {"Policy No", "Sections", "Amount"};

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Endorsement Template");
        // Create the header row
        Row headerRow = sheet1.createRow(0);
        for (int i = 0; i < sheet1Headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(sheet1Headers[i]);
            sheet1.autoSizeColumn(i);
        }

        Sheet sheet2 = workbook.createSheet("Premium Items");
        Row headerRow2 = sheet2.createRow(0);
        for (int i = 0; i < sheet2Headers.length; i++) {
            headerRow2.createCell(i).setCellValue(sheet2Headers[i]);
            sheet2.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        //workbook.close();
    }
    @RequestMapping(value = {"bulkpolcntemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcelCnTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-policy-cancellation-upload-template.xlsx");

        String[] sheet1Headers = {
                "Policy No", "Cancellation Amt", "Effective Date"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Cancellation Template");
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

    @RequestMapping(value = {"bulkpolrefundstemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcelRefundsTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-policy-refunds-upload-template.xlsx");

        String[] sheet1Headers = {
                "Policy No", "Policy Ref No"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Refunds Template");
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

    @RequestMapping(value = {"bulkpolrntemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcelRnTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-policy-renewal-upload-template.xlsx");

        String[] sheet1Headers = {
//                "Policy No", "Sum Insured", "Premium", "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type"
                "Policy No", "Sum Insured", "Premium", "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type", "Renewal Date"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Renewal Template");
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

//    @RequestMapping(value = {"download-error-file/{fileId}"} , method = {RequestMethod.GET})
//    @ResponseBody
//        public ResponseEntity<byte[]> downloadErrorFile(@PathVariable("fileId") String fileId) {
//            ByteArrayOutputStream bos = errorWorkbookCache.getWorkbook(fileId);
//            if (bos == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            }
//
//            byte[] fileContent = bos.toByteArray();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDispositionFormData("attachment", "BulkUploadENErrors_" + fileId + ".xlsx");
//
//            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
//    }


    @RequestMapping(value = "download-error-file/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadErrorFile(@PathVariable("fileId") String fileId, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream workbookData = errorWorkbookCache.getWorkbook(fileId);

        if (workbookData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Error file not found.");
            return;
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=BulkENUploadErrors.xlsx");
        response.getOutputStream().write(workbookData.toByteArray());
        response.flushBuffer();
    }


}