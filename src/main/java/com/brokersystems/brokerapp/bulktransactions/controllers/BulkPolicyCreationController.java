package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.ErrorsCache.ErrorWorkbookCache;
import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyCreation;
import com.brokersystems.brokerapp.bulktransactions.models.QBulkPolicyCreation;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPremComputeService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.bulktransactions.dtos.BulkPolicyCreationDTO;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPolicyCreationService;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/protected/bulk/policycreation")
public class BulkPolicyCreationController {

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private BulkPolicyCreationService bulkPolicyCreationService;

    @Autowired
    private PremComputeService premiumService;

    @Autowired
    private PolicyTransService policyService;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private BulkPremComputeService bulkPremComputeService;

    @Autowired
    private ErrorWorkbookCache errorWorkbookCache;

//    @RequestMapping(value = "endorsementcreation", method = {RequestMethod.GET})
//    public String transEndorseHome(HttpServletRequest request) {
//        return "endorsementcreation";
//    }

    @RequestMapping(value = "bulkPolHome", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request) {
        auditTrailLogger.log("Accessed Transaction Processing Home", request, "Bulk Policy");
        return "bulkPolCreation";
    }


    @RequestMapping(value = "viewbulkpolicyhome", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request) {
        auditTrailLogger.log("Accessed Bulk Policy Authorization Home", request, "Bulk Policy view");
        return "viewbulkpolicy";
    }

    @RequestMapping(value = "uploadBulkPolExcel", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Uploaded bulk policy creation file: " + file, request, "Bulk Policy Upload");
        return bulkPolicyCreationService.uploadBulkPolicy(file);
    }

    @RequestMapping(value = {"unProcessedBulkPol"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BulkPolicyCreationDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return bulkPolicyCreationService.findUnProcessedBulkPol(pageable);
    }

    @RequestMapping(value = {"viewBulkPolicies"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BulkPolicyCreationDTO> getUnprocessedBulkPol(@DataTable DataTablesRequest pageable) {
        return bulkPolicyCreationService.viewBulkPolicies(pageable);
    }

    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Accessed edit underwriting transactions screen: ", request, "Edit Uw Trans");
        request.getSession().setAttribute("policyCode", helperForm.getId());
        PolicyTrans policyTrans = policyService.getPolicyDetails(helperForm.getId());
        if ("MD".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType()))
            return "redirect:/protected/medical/policies/edituwpolicy";
        else if ("L".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType())) {
            return "redirect:/protected/uw/policies/editlifepolicy";
        } else {
            return "redirect:/protected/uw/policies/edituwpolicy";
        }

    }

    @RequestMapping(value = {"processSingleBulkPolicy/{bulkId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> processSingleBulkPolicy(@PathVariable("bulkId") Long bulkId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed single policy with ID: " + bulkId, request, "Single Bulk Policy Processing");
        System.out.println("BulkId: " + bulkId);
        PolicyTrans bulkPolicyCreation = bulkPolicyCreationService.processSingleBulkPolicy(bulkId, true);
        Long polCode = bulkPolicyCreation.getPolicyId();
        System.out.println("Pol Code: " + polCode);
//        if ("NB".equalsIgnoreCase(bulkPolicyCreation.getTransType()) || "SP".equalsIgnoreCase(bulkPolicyCreation.getTransType())
//                || "EX".equalsIgnoreCase(bulkPolicyCreation.getTransType()) || "RN".equalsIgnoreCase(bulkPolicyCreation.getTransType())
//                || "BU".equalsIgnoreCase(bulkPolicyCreation.getTransType()) || "LD".equalsIgnoreCase(bulkPolicyCreation.getTransType()))
//            try {
//                //premiumService.computePrem(polCode);
//                bulkPremComputeService.computeUploadGeneralPrem(polCode);
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new BadRequestException(e.getMessage());
//            }
//        else if ("EN".equalsIgnoreCase(bulkPolicyCreation.getTransType())) {
//            try {
//                //premiumService.computeEndorsePremium(polCode);
//                bulkPremComputeService.computeUploadGeneralPrem(polCode);
//            } catch (IOException e) {
//                throw new BadRequestException(e.getMessage());
//            }
//        }

//        String response = policyService.makeReady(polCode);
////        bulkPolicyCreation.setAuthStatus(response);
//        final Long hashCode = Long.parseLong(String.valueOf(bulkPolicyCreation.hashCode()));
//        List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
//        List<Long> checkerIds = new ArrayList<Long>();
//        if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP").and(QMakerChecker.makerChecker.policyId.eq(polCode)))) {
//            for (UserDTO eligibleChecker : eligibleCheckers) {
//                checkerIds.add(eligibleChecker.getId());
//            }
//            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
//            makerCheckDTO.setStatus("N");
//            makerCheckDTO.setTaskName(String.format("Policy %s pending authorization", bulkPolicyCreation.getPolNo()));
//            makerCheckDTO.setTaskType("ANP");
//            makerCheckDTO.setJson("{}");
//            makerCheckDTO.setTaskCode(hashCode);
//            makerCheckDTO.setAssignedCheckers(checkerIds.toString());
//            makerCheckDTO.setPolicyId(bulkPolicyCreation.getPolicyId());
//            makerCheckerService.checkExists(makerCheckDTO);
//            makerCheckerService.createMakerChecker(makerCheckDTO);
//        }

        return new ResponseEntity<PolicyTrans>(bulkPolicyCreation, HttpStatus.OK);
    }

    @RequestMapping(value = {"processbulkdelpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public void bulkPolDelProcessing(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete not processed bulk policies with IDs: " + bulkIds, request, "Bulk Delete Policies");
        //delete bulk uploaded policy
        bulkPolicyCreationService.bulkDelUploadPolicies(bulkIds);
    }

    @RequestMapping(value = {"processbulkdelprocessedpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public String bulkDelProcessedPols(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete processed bulk policies with IDs: " + bulkIds, request, "Bulk Delete Processed Policies");
        return bulkPolicyCreationService.bulkDelProcessedPolicies(bulkIds);
    }


    @RequestMapping(value = {"processbulkpolicies"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> bulkPolProcessing(@RequestBody List<Long> bulkIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed bulk policies with IDs: " + bulkIds, request, "Bulk Process Policies");
        List<Long> polCodes = bulkPolicyCreationService.bulkProcessPolicies(bulkIds, true);
        System.out.println("polCodes: " + polCodes);
        PolicyTrans policyTrans = null;
//        for (Long polCode : polCodes) {
//            System.out.println("Pol Code: " + polCode);
//            policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(polCode));
//            if ("NB".equalsIgnoreCase(policyTrans.getTransType()) || "SP".equalsIgnoreCase(policyTrans.getTransType())
//                    || "EX".equalsIgnoreCase(policyTrans.getTransType()) || "RN".equalsIgnoreCase(policyTrans.getTransType())
//                    || "BU".equalsIgnoreCase(policyTrans.getTransType()) || "LD".equalsIgnoreCase(policyTrans.getTransType()))
//                try {
//                    //premiumService.computePrem(polCode);
//                    bulkPremComputeService.computeUploadGeneralPrem(polCode);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    throw new BadRequestException(e.getMessage());
//                }
//            else if ("EN".equalsIgnoreCase(policyTrans.getTransType())) {
//                try {
//                    //premiumService.computeEndorsePremium(polCode);
//                    bulkPremComputeService.computeUploadGeneralPrem(polCode);
//                } catch (IOException e) {
//                    throw new BadRequestException(e.getMessage());
//                }
//            }

//            String response = policyService.makeReady(polCode);
////            policyTrans.setAuthStatus(response);
//            final Long hashCode = Long.parseLong(String.valueOf(policyTrans.hashCode()));
//            List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
//            List<Long> checkerIds = new ArrayList<Long>();
//            if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP").and(QMakerChecker.makerChecker.policyId.eq(polCode)))) {
//                for (UserDTO eligibleChecker : eligibleCheckers) {
//                    checkerIds.add(eligibleChecker.getId());
//                }
//                MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
//                makerCheckDTO.setStatus("N");
//                makerCheckDTO.setTaskName(String.format("Policy %s pending authorization", policyTrans.getPolNo()));
//                makerCheckDTO.setTaskType("ANP");
//                makerCheckDTO.setJson("{}");
//                makerCheckDTO.setTaskCode(hashCode);
//                makerCheckDTO.setAssignedCheckers(checkerIds.toString());
//                makerCheckDTO.setPolicyId(policyTrans.getPolicyId());
//                makerCheckerService.checkExists(makerCheckDTO);
//                makerCheckerService.createMakerChecker(makerCheckDTO);
//            }
        //}
        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
    }

    @RequestMapping(value = {"approvesinglepol/{policyId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveSingleGroupLifePolicy(@PathVariable("policyId") Long policyId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved single wezesha policy with ID: " + policyId, request, "Approved single policy");
        return bulkPolicyCreationService.approveSinglePol(policyId);
    }

    @RequestMapping(value = {"approvebulkpol"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveBulkGroupLifePolicy(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk wezesha policies with IDs: " + policyIds, request, "Approved bulk  policies");
        return bulkPolicyCreationService.approveBulkPol(policyIds);
    }

    @RequestMapping(value = {"bulkpoltemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-policy-upload-template.xlsx");

        String[] sheet1Headers = {
                "Serial No", "Client ID No.", "Client CIF", "Sales Agent", "Sales Code",
                "Underwriter Code", "Product Group",
                "Product Name", "Contract", "Cover Type", "Branch Code", "Payment Frequency",
                "CoverDateFrom", "Currency ISO code", "Risk/Property ID", "Risk Description", "sum_insured","premium",
                "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type"
        };

        String[] sheet2Headers = {"Serial No", "Sections", "Amount"};

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Policy Details");
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

    @RequestMapping(value = "download-error-file/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadErrorFile(@PathVariable("fileId") String fileId, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream workbookData = errorWorkbookCache.getWorkbook(fileId);

        if (workbookData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Error file not found.");
            return;
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=BulkDscUploadErrors.xlsx");
        response.getOutputStream().write(workbookData.toByteArray());
        response.flushBuffer();
    }
}
