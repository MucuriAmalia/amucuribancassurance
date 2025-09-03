package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.bulktransactions.dtos.CreditCardDTO;
import com.brokersystems.brokerapp.bulktransactions.service.BulkPremComputeService;
import com.brokersystems.brokerapp.bulktransactions.service.CreditCardService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/protected/credit/card")
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private BulkPremComputeService bulkPremComputeService;


    @RequestMapping(value = "createcreditcard", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed Credit Card upload screen", request, "Create Credit Card");
        return "createcreditcard";
    }

    @RequestMapping(value = "viewcreditcard", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed Credit Card upload authorization screen", request, "View Credit Card");
        return "viewcreditcard";
    }

    @RequestMapping(value = "uploadcreditcard", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadFirstAssExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Uploaded Credit Card upload file: " + file, request, "Upload Credit Card");
        return creditCardService.uploadCreditCard(file);
    }

    @RequestMapping(value = {"unprocesscreditcard"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<CreditCardDTO> getUnProcessedTrans(@DataTable DataTablesRequest pageable) {
        return creditCardService.findUnprocessedCreditCard(pageable);
    }

    @RequestMapping(value = {"viewunprocesscreditcard"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<CreditCardDTO> getUnProcessedPackagedInsur(@DataTable DataTablesRequest pageable) {
        return creditCardService.viewUnprocessedCreditCard(pageable);
    }

    @RequestMapping(value = {"processsinglcreditcard/{cardId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> processSingleCreditCardPol(@PathVariable("cardId") Long cardId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed single Credit Card policy with ID: " + cardId, request, "Processing Single Credit Card");
        System.out.println("cardId: " + cardId);
        PolicyTrans policyTrans = creditCardService.processSingleCreditCardPol(cardId, true);
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
    @RequestMapping(value = {"deletebulkcreditcard"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteBulkCreditCardPols(@RequestBody List<Long> cardIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete Credit Card bulk policies with IDs: " + cardIds, request, "Delete upload Bulk Credit Card");
        return  creditCardService.deleteBulkCreditCardPol(cardIds);

    }
    @RequestMapping(value = {"deleteprocessedbulkcredcard"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteProcessedBulkCreditLifePol(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("delete processed bulk policies with IDs: " + policyIds, request, "Delete Processed Bulk Credit Card");
        return creditCardService.deleteProcessedBulkCreditCardPol(policyIds);
    }


    @RequestMapping(value = {"processbulkcreditcard"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<PolicyTrans> processBulkCreditCardPols(@RequestBody List<Long> cardIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Processed Credit Card bulk policies with IDs: " + cardIds, request, "Process Bulk Credit Card");
        List<Long> polCodes = creditCardService.processBulkCreditCardPol(cardIds, true);
        System.out.println("Credit Card polCodes: " + polCodes);
        PolicyTrans policyTrans = null;
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
        return new ResponseEntity<PolicyTrans>(policyTrans, HttpStatus.OK);
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

    @RequestMapping(value = {"approvesinglercreditcard/{policyId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveSingleCreditCardPolicy(@PathVariable("policyId") Long policyId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved single Credit Card policy with ID: " + policyId, request, "Approve Single Credit Card");
        return creditCardService.approveSingleCreditCardPol(policyId);
    }

    @RequestMapping(value = {"approvebulkrcreditcard"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveBulkPolicy(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved bulk policies with IDs: " + policyIds, request, "Approve Bulk Credit Card");
        return creditCardService.approveBulkCreditCardPol(policyIds);
    }


    @RequestMapping(value = {"bulkcreditcardtemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-credit-card-upload-template.xlsx");

        String[] sheet1Headers = {
               "ID No", "Client CIF",
                "Sales Agent", "Sales Manager", "Sales Code", "Card Type", "Card Account",
                "Insurer Code", "Product Group", "Product Name", "Cover Type",
                "Payment Frequency", "Currency ISO Code", "Branch",
                "Premium Amount", "Date Booked", "Start Date", "End Date","Card Number","Cycles",
                "Accrual or Cash", "Accrual Inst Date", "Accrual Payment Type", "Seq"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Credit Card");
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
