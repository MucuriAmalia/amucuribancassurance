package com.brokersystems.brokerapp.accounts.controllers;

import com.brokersystems.brokerapp.accounts.dtos.PayeesDTO;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.BankAccountDTO;
import com.brokersystems.brokerapp.setup.dto.UserBranchesDTO;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.trans.dtos.ChequeTransDTO;
import com.brokersystems.brokerapp.trans.dtos.ChequeTransDtlsDTO;
import com.brokersystems.brokerapp.trans.dtos.RejectRequistionForm;
import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import com.brokersystems.brokerapp.trans.service.PaymentService;
import com.brokersystems.brokerapp.uw.model.StartRenForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;

@Controller
@RequestMapping({ "/protected/accounts/payments" })
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AuditTrailLogger auditTrailLogger;



    @ModelAttribute
    private StartRenForm getStartRenForm(){
        return new StartRenForm();
    }

    @RequestMapping(value = "requistion",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String requistion(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Requistion Screen ", request, "Requistion");
        return "requistion";
    }

    @RequestMapping(value = "approvedtrans",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String approvedtrans(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Requistion Screen ", request, "Approved Requistion");
        return "newrequistion";
    }

    @RequestMapping(value = "createrequistion",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String createrequistion(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Requistion Screen ", request, "Create Requistion");
        return "createrequistion";
    }


    @RequestMapping(value = { "selPayees" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<PayeesDTO> selectPayees(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
        return paymentService.findPayeeAccounts(term,pageable);
    }

    @RequestMapping(value = { "selBankAccounts" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<BankAccountDTO> selBankAccounts(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
        return paymentService.findAllBankAccountsLov(term,pageable);
    }

    @RequestMapping(value = { "startRequistion" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public String startRenewals(@Valid @ModelAttribute StartRenForm startRenForm, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        if(result.hasErrors()){
            redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.startRenForm", result);
            redirectAttrs.addFlashAttribute("startRenForm", startRenForm);
            return "redirect:/protected/accounts/payments/requistion";
        }

        if(startRenForm.getRenewalType()==null || StringUtils.isBlank(startRenForm.getRenewalType())){
            redirectAttrs.addFlashAttribute("error", "Select Option");
            redirectAttrs.addFlashAttribute("startRenForm", startRenForm);
            return "redirect:/protected/accounts/payments/requistion";
        }

        if(startRenForm.getRenewalType().equalsIgnoreCase("C")){
            return "createrequistion";
        }
        else if(startRenForm.getRenewalType().equalsIgnoreCase("V")){
            return "batchrens";
        }
        else{
            redirectAttrs.addFlashAttribute("error", "Option Selected not Supported");
            redirectAttrs.addFlashAttribute("startRenForm", startRenForm);
            return "redirect:/protected/accounts/payments/requistion";
        }
    }

    @RequestMapping(value = { "saveRequisition" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<Long> saveRequisition(@RequestBody ChequeTransDTO chequeTransDTO) throws BadRequestException {
        paymentService.createRequistion(chequeTransDTO);
        return new ResponseEntity<>(200l, HttpStatus.OK);
    }

    @RequestMapping(value = { "rejectRequistion" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public RejectRequistionForm rejectRequistion(RejectRequistionForm rejectRequistionForm) throws IllegalAccessException, BadRequestException {
        paymentService.rejectRequistion(rejectRequistionForm);
        return rejectRequistionForm;
    }

    @RequestMapping(value = { "rejectApprovedRequistion" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public RejectRequistionForm rejectApprovedRequistion(RejectRequistionForm rejectRequistionForm) throws IllegalAccessException, BadRequestException {
        paymentService.rejectApprovedRequistion(rejectRequistionForm);
        return rejectRequistionForm;
    }

    @RequestMapping(value = { "requistions" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ChequeTransDTO> getRequistions(@DataTable DataTablesRequest pageable)  {
        return paymentService.findRequistions(pageable,"AS");
    }

    @RequestMapping(value = { "approvedtransactions" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ChequeTransDTO> approvedTrans(@DataTable DataTablesRequest pageable)  {
        return paymentService.findRequistions(pageable,"TBP");
    }



    @RequestMapping(value = { "requistiondetails/{reqId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ChequeTransDtlsDTO> getRequistionDetails(@DataTable DataTablesRequest pageable, @PathVariable Long reqId) throws IllegalAccessException {
        return paymentService.findRequistionDetails(pageable,reqId);
    }

    @RequestMapping(value = { "authoriseRequistion/{transId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authoriseRequistion(@PathVariable Long transId) throws BadRequestException {
        paymentService.authRequistion(transId);

    }

    @RequestMapping(value = { "updateRequistion/{transId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRequistion(@PathVariable Long transId) throws BadRequestException {
        paymentService.updateRequistion(transId);

    }

}
