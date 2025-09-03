package com.brokersystems.brokerapp.accounts.controllers;

import com.brokersystems.brokerapp.accounts.dtos.*;
import com.brokersystems.brokerapp.accounts.dtos.SettlementDTO;
import com.brokersystems.brokerapp.accounts.model.*;
import com.brokersystems.brokerapp.accounts.repository.PaymentAuditRepo;
import com.brokersystems.brokerapp.accounts.repository.RefundRepo;
import com.brokersystems.brokerapp.accounts.service.AccountsService;
import com.brokersystems.brokerapp.reconciliation.ReconciliationDTO;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.CoaSubAccountsDTO;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.PaymentModes;
import com.brokersystems.brokerapp.setup.repository.AccountRepo;
import com.brokersystems.brokerapp.setup.repository.ClientRepository;
import com.brokersystems.brokerapp.setup.repository.CurrencyRepository;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.trans.dtos.CommissionDTO;
import com.brokersystems.brokerapp.trans.dtos.CommissionReconciliationDTO;
import com.brokersystems.brokerapp.trans.dtos.CommissionUnreconciledDataDTO;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.ReceiptRepository;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.trans.service.CommissionsPayinsService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.RiskUploadForm;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by peter on 3/25/2017.
 */
@Controller
@RequestMapping({ "/protected/accounts" })
public class AccountsController {

    private final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private PaymentAuditRepo auditRepo;

    @Autowired
    private SystemTransactionsRepo transRepo;

    @Autowired
    private CurrencyRepository currencyRepo;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private DataSource datasource;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private RefundRepo refundRepo;

    @Autowired
    private CommissionsPayinsService commissionsPayinsService;

    @Autowired
    private ReceiptRepository receiptRepo;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        final String val="";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "payeesvw",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String payeesvwHoe(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Payees Screen ", request, "Payees");
        return "payeesvw";
    }

    @RequestMapping(value = "accountingPeriods",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String accountingPeriods(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Accounting Periods Screen ", request, "Accounting Periods");
        return "acctperiods";
    }

    @RequestMapping(value = "gltrans",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String glTransactions(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed GL Transactions Screen ", request, "GL Transactions");
        return "gltransactions";
    }

    @RequestMapping(value = "openingbal",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String accountOpeningBal(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Accounts Opening Balances Screen ", request, "Accounts Opening Balances");
        return "openingbal";
    }

    @RequestMapping(value = "reportformats",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String reportFormats(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Report Format Payees Screen ", request, "Report Format Payees");
        return "finreportformats";
    }

    @RequestMapping(value = "creditpaymt",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String creditsHome(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Credit Payment Screen ", request, "Credit Payment");
        request.getSession().removeAttribute("currCode");
        request.getSession().removeAttribute("agentCode");
        request.getSession().removeAttribute("wefDate");
        request.getSession().removeAttribute("wetDate");
        request.getSession().removeAttribute("pstatus");
        return "creditpayment";
    }

    @RequestMapping(value = "creditcommpaymt",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String creditorCommHome(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Credit Payment Screen ", request, "Credit Payment");
        request.getSession().removeAttribute("currCode");
        request.getSession().removeAttribute("agentCode");
        request.getSession().removeAttribute("wefDate");
        request.getSession().removeAttribute("wetDate");
        return "creditorcommpaymt";
    }
    @RequestMapping(value = "bulkCreditcommpaymt",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String bulkCreditorCommHome(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Bulk Credit Payment Screen ", request, "Credit Payment");
        return "bulkCreditcommpaymt";
    }

    @RequestMapping(value = "subAgentcommpaymt",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String subAgentCommHome(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed SubAgent Commision Payment Screen ", request, "Sub Agent Commission");
        request.getSession().removeAttribute("currCode");
        request.getSession().removeAttribute("agentCode");
        request.getSession().removeAttribute("wefDate");
        request.getSession().removeAttribute("wetDate");
        return "subAgentcommpaymt";
    }

    @RequestMapping(value = "adminFeeCommission",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String adminFee(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Admin Fee Payment Screen ", request, "Admin Fees Commissions");
        request.getSession().removeAttribute("currCode");
        request.getSession().removeAttribute("agentCode");
        request.getSession().removeAttribute("wefDate");
        request.getSession().removeAttribute("wetDate");
        return "adminFeecommpaymt";
    }

    @RequestMapping(value = "commissionpymt",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String commissionPymt(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Commission Payment Screen ",request, "Commission Payment");
        return "commissionprocess";
    }

    @RequestMapping(value = "authTrans",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String authTrans(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Account Authorization Screen ",request, "Account Authorization");
        return "accountAuth";
    }

    @RequestMapping(value = "alloctrans",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String allocations(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Account Allocation Screen ",request, "Account Allocation");
        return "allocations";
    }

    @RequestMapping(value = "processRefund",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String refunds(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Refunds Screen ",request, "Process Refund");
        return "refunds";
    }

    @RequestMapping(value = "chartsofaccounts",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String coa(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Chart of Accounts Screen ",request, "Charts of Accounts");
        return "coa";
    }

    @RequestMapping(value = "banksdef",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String banksdef(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Banks Definition Screen ",request, "Banks Definition");
        return "banks";
    }

    @RequestMapping(value = "collectaccts",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String collaccts(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Collection Accounts Screen ",request, "Collection Accounts");
        return "collaccts";
    }

    @RequestMapping(value = { "coatrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CoaMainAccounts> getCoa(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return accountsService.findChartOfAccounts(pageable);
    }

    @RequestMapping(value = { "coasubtrans/{mainAccId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CoaSubAccountsDTO> getSubAccounts(@DataTable DataTablesRequest pageable, @PathVariable Long mainAccId)
            throws IllegalAccessException {
        return accountsService.findChartSubAccounts(pageable,mainAccId);
    }

    @RequestMapping(value = { "accounttrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SystemTransDTO> accountTrans(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return accountsService.findAuthorizeTrans(pageable);
    }

    @RequestMapping(value = { "crtrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<SettlementDTO> getInsurancePaymentTrans(@RequestParam(value = "currCode", required = false) Long currCode,
                                                        @RequestParam(value = "agentCode", required = false) Long agentCode,
                                                        @RequestParam(value = "wefDate", required = false) Date wefDate,
                                                        @RequestParam(value = "wetDate", required = false) Date wetDate,
                                                        @RequestParam(value = "pstatus", required = false) String pstatus, HttpServletRequest request)
            throws IllegalAccessException {
        auditTrailLogger.log("1. Searched for insurance payments using the paramaters Insurance Co. "+
                ((agentCode!=null)? accountRepo.findOne(agentCode).getShtDesc():"") +" Wef Date: "+
                new SimpleDateFormat("yyyy-MM-dd").format(wefDate)+" Wet Date: "+new SimpleDateFormat("yyyy-MM-dd").format(wetDate)+
                " in "+ currencyRepo.findOne(currCode).getCurName(),request, "Insurance Payments");
        request.getSession().setAttribute("currCode",currCode);
        request.getSession().setAttribute("agentCode",agentCode);
        request.getSession().setAttribute("wefDate",wefDate);
        request.getSession().setAttribute("wetDate",wetDate);
        request.getSession().setAttribute("pstatus",pstatus);
        return accountsService.findAllocationDetails(agentCode,currCode,wefDate,wetDate,pstatus);
    }
    @RequestMapping(value = { "subAgentTranscomm" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<SettlementDTO> getSubAgentPaymentTransComm(@RequestParam(value = "currCode", required = false) Long currCode,
                                                           @RequestParam(value = "agentCode", required = false) List<Long> agentCode,
                                                            @RequestParam(value = "wefDate", required = false) Date wefDate,
                                                            @RequestParam(value = "wetDate", required = false) Date wetDate,
                                                            HttpServletRequest request)
            throws IllegalAccessException {
        auditTrailLogger.log("3. Searched for insurance payments using the paramaters Insurance Co. "+
                ((agentCode!=null)? accountRepo.findOne(agentCode.get(0)).getShtDesc():"") +" Wef Date: "+
                new SimpleDateFormat("yyyy-MM-dd").format(wefDate)+" Wet Date: "+new SimpleDateFormat("yyyy-MM-dd").format(wetDate)+
                " in "+ currencyRepo.findOne(currCode).getCurName(),request, "Insurance Payments");
        request.getSession().setAttribute("currCode",currCode);
        request.getSession().setAttribute("agentCode",agentCode);
        request.getSession().setAttribute("wefDate",wefDate);
        request.getSession().setAttribute("wetDate",wetDate);
        List<SettlementDTO> settlementList = new ArrayList<>();
        if(agentCode==null){
            settlementList.addAll(accountsService.findSubAgentCommDetails(-2000L,currCode,wefDate,wetDate));
            return settlementList;
        }
        for(Long agentCodes:agentCode){
            settlementList.addAll(accountsService.findSubAgentCommDetails(agentCodes,currCode,wefDate,wetDate));
        }
        return settlementList;
    }

    @RequestMapping(value = { "creditortranscomm" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<SettlementDTO> getInsurancePaymentTransComm(@RequestParam(value = "currCode", required = false) Long currCode,
                                                        @RequestParam(value = "agentCode", required = false) Long agentCode,
                                                        @RequestParam(value = "wefDate", required = false) Date wefDate,
                                                        @RequestParam(value = "wetDate", required = false) Date wetDate,
                                                        @RequestParam(value = "commtype", required = false) String commtype,
                                                        HttpServletRequest request)
            throws IllegalAccessException {
        auditTrailLogger.log("3. Searched for insurance payments using the paramaters Insurance Co. "+
                ((agentCode!=null)? accountRepo.findOne(agentCode).getShtDesc():"") +" Wef Date: "+
                new SimpleDateFormat("yyyy-MM-dd").format(wefDate)+" Wet Date: "+new SimpleDateFormat("yyyy-MM-dd").format(wetDate)+
                " in "+ currencyRepo.findOne(currCode).getCurName(),request, "Insurance Payments");
        request.getSession().setAttribute("currCode",currCode);
        request.getSession().setAttribute("agentCode",agentCode);
        request.getSession().setAttribute("wefDate",wefDate);
        request.getSession().setAttribute("wetDate",wetDate);
        request.getSession().setAttribute("commtype", commtype);
        return accountsService.findAllocationCommDetails(agentCode,currCode,wefDate,wetDate, commtype);
    }

    @RequestMapping(value = { "subagenttrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<SettlementDTO> getSubAgentTrans(   @RequestParam(value = "agentCode", required = false) Long agentCode,
                                                   @RequestParam(value = "wefDate", required = false) Date wefDate,
                                                   @RequestParam(value = "wetDate", required = false) Date wetDate,
                                                   @RequestParam(value = "subagentId", required = false) Long subagentId)
            throws IllegalAccessException {
        return accountsService.findSubAgentTransactions(agentCode,wefDate,wetDate,subagentId);
    }

    @RequestMapping(value = { "paymentaudits" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PaymentAudit> getPaymentAudits(@DataTable DataTablesRequest pageable,
                                                           @RequestParam(value = "transNo", required = false) Long transNo)
            throws IllegalAccessException {
        return accountsService.findTransPaymentAudits(pageable,transNo);
    }

    @RequestMapping(value = { "creditorcommaudits" }, method = { RequestMethod.GET })
    @ResponseBody
    public Map<String,Object> findAllocationCommDetails(@RequestParam(value = "transNo", required = false) Long transNo)
            throws IllegalAccessException {
        return accountsService.findAllocationCommDetails(transNo);
    }

    @RequestMapping(value = { "subAgentcommaudits" }, method = { RequestMethod.GET })
    @ResponseBody
    public Map<String,Object> findAllocationSubAgentCommDetails(@RequestParam(value = "transNo", required = false) Long transNo)
            throws IllegalAccessException {
        return accountsService.findAllocationSubAgentCommDetails(transNo);
    }

    @RequestMapping(value = { "instrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SystemTransactions> getInsuranceTrans(@DataTable DataTablesRequest pageable,
                                                                  @RequestParam(value = "currCode", required = false) Long currCode,
                                                                  @RequestParam(value = "agentCode", required = false) Long agentCode)
            throws IllegalAccessException {
        return accountsService.findAgentsTransactions(pageable,agentCode,currCode);
    }

    @RequestMapping(value = { "creditorCommTrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SystemTransactions> getCreditorCommTrans(@DataTable DataTablesRequest pageable,
                                                                  @RequestParam(value = "currCode", required = false) Long currCode,
                                                                  @RequestParam(value = "agentCode", required = false) Long agentCode,
                                                                  @RequestParam(value = "transType", required = false) String transType
                                                                     )
            throws IllegalAccessException {
        return accountsService.findCreditorCommTransactions(pageable,agentCode,currCode,transType);
    }

    @RequestMapping(value = { "bulkCommTrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SystemTransactions> getBulkCreditorCommTrans(@DataTable DataTablesRequest pageable,
                                                                     @RequestParam(value = "currCode", required = false) Long currCode,
                                                                     @RequestParam(value = "agentCode", required = false) Long agentCode)
            throws IllegalAccessException {
        return accountsService.findBulkCreditorCommTransactions(pageable,agentCode,currCode);
    }

    @RequestMapping(value = { "subAgentCommTrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SystemTransactions> getSubAgentCommTrans(@DataTable DataTablesRequest pageable,
                                                                     @RequestParam(value = "currCode", required = false) Long currCode,
                                                                     @RequestParam(value = "agentCode", required = false) List<Long> agentCode)
            throws IllegalAccessException {
        return accountsService.findSubAgentCommTransactions(pageable,agentCode,currCode);
    }


    @RequestMapping(value = { "subagentpayments" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SystemTransactions> getSubagentInsuranceTrans(@DataTable DataTablesRequest pageable,
                                                                          @RequestParam(value = "currCode", required = false) Long currCode,
                                                                          @RequestParam(value = "agentCode", required = false) Long agentCode)
            throws IllegalAccessException {
        return accountsService.findIAAgentsTransactions(pageable,agentCode,currCode);
    }

    @RequestMapping(value = { "getpayableamt" }, method = { RequestMethod.GET })
    @ResponseBody
    public BigDecimal getPayableAmount(@RequestParam(value = "receiptNo", required = false) String receiptRefNo,
                                       @RequestParam(value = "debitRef", required = false) String debitRef,
                                       @RequestParam(value = "allocAmt", required = false) BigDecimal allocAmt)
            throws BadRequestException {
        return accountsService.getPayableAmount(receiptRefNo,debitRef,allocAmt);
    }
    @RequestMapping(value = { "getpayablecommamt" }, method = { RequestMethod.GET })
    @ResponseBody
    public BigDecimal getPayableCommAmount(@RequestParam(value = "receiptNo", required = false) String receiptRefNo,
                                           @RequestParam(value = "debitRef", required = false) String debitRef,
                                           @RequestParam(value = "locAmt", required = false) BigDecimal locAmt,
                                           @RequestParam(value = "allocAmt", required = false) BigDecimal allocAmt)
            throws BadRequestException {
        System.out.println("all amount " +allocAmt );
        System.out.println("all amount commissions " +locAmt );
        return accountsService.getPayableCommAmount(receiptRefNo,debitRef, locAmt, allocAmt);
    }
    @RequestMapping(value = { "getpayableSubCommamt" }, method = { RequestMethod.GET })
    @ResponseBody
    public BigDecimal getPayableSubAgentCommAmount(@RequestParam(value = "receiptNo", required = false) String receiptRefNo,
                                           @RequestParam(value = "debitRef", required = false) String debitRef,
                                           @RequestParam(value = "locAmt", required = false) BigDecimal locAmt,
                                           @RequestParam(value = "allocAmt", required = false) BigDecimal allocAmt)
            throws BadRequestException {
        System.out.println("all amount " +allocAmt );
        System.out.println("all amount commissions " +locAmt );
        return accountsService.getPayableSubAgentCommAmount(receiptRefNo,debitRef, locAmt, allocAmt);
    }

    @RequestMapping(value = { "getTotalAuditAmt" }, method = { RequestMethod.GET })
    @ResponseBody
    public BigDecimal getTotalAuditAmt(@RequestParam(value = "transId", required = false) Long transId)
            throws BadRequestException {
        return accountsService.getSumAuditAmt(transId);
    }


    @RequestMapping(value = { "processInsuranceTrans" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public ResponseEntity<String> processInsuranceTrans(@RequestBody ProcessingBean processingBean, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("processed Insurance Payment Transactions for: "
                +((processingBean.getAccountCode()!=null)?accountRepo.findOne(processingBean.getAccountCode()).getShtDesc():" ")
                +" For Transactions : " +processingBean.getCredits().toString(),request, "Process Insurance Trans");
        accountsService.consolidatePayments(processingBean);
        return new ResponseEntity<String>("Ok", HttpStatus.OK);
    }

    @RequestMapping(value = { "processCreditorCommissions" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public ResponseEntity<String> processCreditorCommissions(@RequestBody ProcessingBean processingBean, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("processed Insurance Payment Transactions for: "
                +((processingBean.getAccountCode()!=null)?accountRepo.findOne(processingBean.getAccountCode()).getShtDesc():" ")
                +" For Transactions : " +processingBean.getCredits().toString(),request, "Process Creditor Commissions");
        accountsService.consolidateCreditorCommissionsPayments(processingBean);
        return new ResponseEntity<String>("Ok", HttpStatus.OK);
    }

    @RequestMapping(value = { "processSubAgentCommissions" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public ResponseEntity<String> processSubAgentCommissions(@RequestBody ProcessingBean processingBean, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("processed Subagent Commission Payment Transactions for: ",request, "Process SubAgent Commissions");
        accountsService.consolidateCommissionPayments(processingBean);
        return new ResponseEntity<String>("Ok", HttpStatus.OK);
    }

    @RequestMapping(value = { "processSubAgentCommTrans" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public ResponseEntity<String> processSubAgentCommTrans(@RequestBody ProcessingBean processingBean, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("processed Subagent Commission Payment Transactions for: ",request, "Process SubAgent Comm Trans");
        accountsService.consolidateSubAgentCommissionPayments(processingBean);
        return new ResponseEntity<String>("Ok", HttpStatus.OK);
    }


    @RequestMapping(value = { "deletePaymentAudit/{auditId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePaymentAudit(@PathVariable Long auditId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Deleted Payment Audit: "+auditRepo.findOne(auditId).toString(),request, "Delete Payment Audit");
        accountsService.deleteAudit(auditId);
    }

    @RequestMapping(value = { "deleteRefund/{refId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRefund(@PathVariable Long refId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Deleted Refund Audit: "+refundRepo.findOne(refId).toString(),request, "Delete Refund");
        accountsService.deleteRefund(refId);
    }


    @RequestMapping(value = { "policyTrans" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<SystemTransactions> selectPolicyTrans(@RequestParam(value = "term", required = false) String term, @RequestParam("policyId") Long policyId, Pageable pageable)
            throws IllegalAccessException {
        return accountsService.findPolicyTrans(term, pageable,policyId);
    }

    @RequestMapping(value = { "clientAllPolicies" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<PolicyTrans> selectClientPolicy(@RequestParam(value = "term", required = false) String term,@RequestParam("clientId") Long clientId, Pageable pageable)
            throws IllegalAccessException {
        return accountsService.findClientPolicies(term, pageable,clientId);
    }


    @RequestMapping(value = { "deleteInsuranceTrans/{transId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInsuranceTrans(@PathVariable Long transId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Cancelled Insurance Payment Audit: "+transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transId)).toString().toString(),request, "Delete Insurance Trans");
        accountsService.cancelPaymentTrans(transId);
    }

    @RequestMapping(value = { "deleteAuthTrans/{transId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthTrans(@PathVariable Long transId, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Rejected Commission Payment Audit: "+transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transId)).toString(),request, "Delete Auth Trans");
        accountsService.rejectAuthTrans(transId);
    }

    @RequestMapping(value = { "deleteBulkAuthTrans" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void deleteBulkAuthTrans(@RequestBody List<Long> transIds, HttpServletRequest request) throws BadRequestException {
        for(Long transId : transIds ) {
            auditTrailLogger.log("Rejected Commission Payment Audit: "+transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transId)).toString(),request, "Delete Auth Trans");
            accountsService.rejectAuthTrans(transId);
        }
    }

    @RequestMapping(value = { "authAccountTrans/{transId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authTrans(@PathVariable Long transId, HttpServletRequest request) throws BadRequestException {
        Boolean  isReceipt = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transId)).getTransType().equalsIgnoreCase("RCT");
        if(!isReceipt){
            System.out.println("auth Insurance Payment >>");
            auditTrailLogger.log("Authorised Insurance Payment for : "+transRepo.findOne(transId).getAgent().getShtDesc()+" : "+transRepo.findOne(transId).toString(),request, "Auth Trans");
            accountsService.authAccountTrans(transId);
        }else if(isReceipt){
            System.out.println("comm bank >>");
            auditTrailLogger.log("Authorised Commission Payment for : "+transRepo.findOne(transId).getAgent().getShtDesc()+" Receipt Number: "+transRepo.findOne(transId).getRefNo(),request, "Auth Trans");
            commissionsPayinsService.authorizeCommissions(transId);
        }

    }

    @RequestMapping(value = { "authBulkAccountTrans" }, method = {org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void authTrans(@RequestBody List<Long> transIds, HttpServletRequest request) throws BadRequestException {
        for(Long transId : transIds) {
            Boolean isReceipt = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transId)).getTransType().equalsIgnoreCase("RCT");
            if (!isReceipt) {
                System.out.println("auth Insurance Payment >>");
                auditTrailLogger.log("Authorised Insurance Payment for : " + transRepo.findOne(transId).getAgent().getShtDesc() + " : " + transRepo.findOne(transId).toString(), request, "Auth Trans");
                accountsService.authAccountTrans(transId);
            } else if (isReceipt) {
                System.out.println("comm bank >>");
                auditTrailLogger.log("Authorised Commission Payment for : " + transRepo.findOne(transId).getAgent().getShtDesc() + " Receipt Number: " + transRepo.findOne(transId).getRefNo(), request, "Auth Trans");
                commissionsPayinsService.authorizeCommissions(transId);
            }
        }
    }

    @RequestMapping(value = { "approveCreditorComm/{transId}" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> approveCreditorComm(@PathVariable Long transId, HttpServletRequest request) throws BadRequestException {
        SystemTransactions trans = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transId));

        if (trans == null) {
            throw new BadRequestException("Transaction not found.");
        }

        Boolean isReceipt = trans.getTransType().equalsIgnoreCase("COM")||trans.getTransType().equalsIgnoreCase("ADM");

        if (isReceipt) {
            auditTrailLogger.log("Approve Commission Requistion for: " + trans.getAgent().getShtDesc() + " : " + trans.toString(), request, "Approve Commission");
            accountsService.approveCreditorComm(transId);
            return ResponseEntity.ok("Transaction approved successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid transaction type for approval.");
    }

    @RequestMapping(value = { "approvebULKCreditorComm" }, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> approvebULKCreditorComm(@RequestBody List<Long> transIds, HttpServletRequest request) throws BadRequestException {
        for(Long transId : transIds) {
            SystemTransactions trans = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transId));

            if (trans == null) {
                throw new BadRequestException("Transaction not found.");
            }

            Boolean isReceipt = trans.getTransType().equalsIgnoreCase("COM") || trans.getTransType().equalsIgnoreCase("ADM");

            if (isReceipt) {
                auditTrailLogger.log("Approve Commission Requistion for: " + trans.getAgent().getShtDesc() + " : " + trans.toString(), request, "Approve Commission");
                accountsService.approveCreditorComm(transId);

            }
        }
        return ResponseEntity.ok("Transaction approved successfully.");
    }

    @RequestMapping(value = { "rejectbULKCreditorComm" }, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> rejectbULKCreditorComm(@RequestBody List<Long> transIds, HttpServletRequest request) throws BadRequestException {
        for(Long transId: transIds) {
            accountsService.cancelPaymentTrans(transId);
        }
        return ResponseEntity.ok("Transactions rejected successfully.");
    }

    @RequestMapping(value = { "approveSubAgentComm/{transId}" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> approveSubAgentComm(@PathVariable Long transId, HttpServletRequest request) throws BadRequestException {
        SystemTransactions trans = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transId));

        if (trans == null) {
            throw new BadRequestException("Transaction not found.");
        }

        Boolean isReceipt = trans.getTransType().equalsIgnoreCase("SAG");

        if (isReceipt) {
            auditTrailLogger.log("Approve Commission Requistion for: " + trans.getAgent().getShtDesc() + " : " + trans.toString(), request, "Approve Sub Agent Commission");
            accountsService.approveSubAgentComm(transId);
            return ResponseEntity.ok("Transaction approved successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid transaction type for approval.");
    }


    @RequestMapping(value = "rpt_inspayment_prelist.pdf", method = RequestMethod.GET)
    public ModelAndView clientQuote(ModelMap modelMap,
                                    HttpServletRequest request,
                                    ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long currCode =(Long) request.getSession().getAttribute("currCode");
        Long agentCode =  (Long)request.getSession().getAttribute("agentCode");
        Date wefDate = (Date)  request.getSession().getAttribute("wefDate");
        Date wetDate = (Date) request.getSession().getAttribute("wetDate");
        String status = (String) request.getSession().getAttribute("pstatus");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
//        Calendar wef = Calendar.getInstance();
//        Calendar wet = Calendar.getInstance();
//        wef.set(2018,1,1);
//        wet.set(2019,5,1);
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("currency", currCode); //currCode
        modelMap.put("Agent", agentCode); //agentCode
        modelMap.put("dateFrom", wefDate); //wefDate
        modelMap.put("dateTo", wetDate); //wetDate
        modelMap.put("status", status);
        System.out.println(modelMap);
        modelAndView = new ModelAndView("rpt_inspayment_prelist", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = "rpt_creditorCommissions", method = RequestMethod.GET)
    public ModelAndView creditorCommissionsRpt(ModelMap modelMap,
                                    HttpServletRequest request,
                                    ModelAndView modelAndView,
                                    @RequestParam("transno") Integer transno,
                                    @RequestParam(value = "format", defaultValue = "pdf") String format)
            throws BadRequestException, IOException {
        Long currCode =(Long) request.getSession().getAttribute("currCode");
        Long agentCode =  (Long)request.getSession().getAttribute("agentCode");
        Date wefDate = (Date)  request.getSession().getAttribute("wefDate");
        Date wetDate = (Date) request.getSession().getAttribute("wetDate");
        String status = (String) request.getSession().getAttribute("pstatus");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", format);
        modelMap.put("currency", currCode); //currCode
        modelMap.put("Agent", agentCode); //agentCode
        modelMap.put("dateFrom", wefDate); //wefDate
        modelMap.put("dateTo", wetDate); //wetDate
        modelMap.put("status", status);
        modelMap.put("transno", transno);
        System.out.println(modelMap);
        modelAndView = new ModelAndView("rpt_creditorCommissions", modelMap);
        return modelAndView;
    }


    @RequestMapping(value = { "credittrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SystemTransactions> getCreditTrans(@DataTable DataTablesRequest pageable,HttpServletRequest request,
                                                               @RequestParam(value = "currCode", required = false) Long currCode,
                                                               @RequestParam(value = "clientCode", required = false) Long clientCode,
                                                               @RequestParam(value = "brnCode", required = false) Long brnCode,
                                                               @RequestParam(value = "alltrans", required = false) String alltrans)
            throws IllegalAccessException {
        if(currCode!=null ||clientCode!=null|| brnCode!=null){
            auditTrailLogger.log("Searched for "+(((clientCode!=null)?
                    (clientRepo.findOne(clientCode).getFname()+" " +clientRepo.findOne(clientCode).getOtherNames()):"")+" Credit Transactions"),request, "Credit Trans");
        }
        return accountsService.findCreditTrans(pageable,clientCode,currCode,brnCode,alltrans);
    }

    @RequestMapping(value = { "debittrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SystemTransactions> getDebitTrans(@DataTable DataTablesRequest pageable,HttpServletRequest request,
                                                              @RequestParam(value = "currCode", required = false) Long currCode,
                                                              @RequestParam(value = "clientCode", required = false) Long clientCode,
                                                              @RequestParam(value = "brnCode", required = false) Long brnCode,
                                                              @RequestParam(value = "alltrans", required = false) String alltrans)
            throws IllegalAccessException {
        if(currCode!=null ||clientCode!=null|| brnCode!=null){
            auditTrailLogger.log("Searched for "+(((clientCode!=null)?
                    (clientRepo.findOne(clientCode).getFname()+" " +clientRepo.findOne(clientCode).getOtherNames()):"")+" Debit Transactions"),request, "Debit Trans");
        }
        return accountsService.findDebitTrans(pageable,clientCode,currCode,brnCode,alltrans);
    }

    @RequestMapping(value = { "getAllocatedAmt" }, method = { RequestMethod.GET })
    @ResponseBody
    public BigDecimal getAllocatedAmt(@RequestParam(value = "transId", required = false) Long transId)
            throws BadRequestException {
        return accountsService.getAllocateAmount(transId);
    }

    @RequestMapping(value = { "allocateTrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public String allocateCredit(@RequestParam(value = "crTransNo", required = false) Long crTransNo  ,
                                 @RequestParam(value = "drTransNo", required = false) Long drTransNo,
                                 @RequestParam(value = "allocAmount", required = false) BigDecimal allocAmount  , HttpServletRequest request)
            throws BadRequestException {
        accountsService.allocateCreditTrans(crTransNo,drTransNo,allocAmount);
        auditTrailLogger.log("Allocated "+allocAmount+" on "+((crTransNo!=null)? transRepo.findOne(crTransNo).toString():" ")+
                " Against "+((drTransNo!=null)? transRepo.findOne(drTransNo).toString():" "),request, "Allocate Trans");
        return "Y";
    }

    @RequestMapping(value = { "createMainAccount" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createMainAccount(CoaMainAccounts mainAccount) throws IllegalAccessException, BadRequestException {
        accountsService.defineCoa(mainAccount);
    }

    @RequestMapping(value = { "deleteMainAccount/{coId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMainAcc(@PathVariable Long coId) {
        accountsService.deleteCoa(coId);
    }


    @RequestMapping(value = { "createSubAccount" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createSubAccount(CoaSubAccounts subAccount) throws IllegalAccessException, BadRequestException {
        accountsService.defineSubCoa(subAccount);
    }

    @RequestMapping(value = { "deleteSubAccount/{coId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubAcc(@PathVariable Long coId) {
        accountsService.deleteSubCoa(coId);
    }

    @RequestMapping(value = { "banks" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<Banks> getBanks(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return accountsService.findBanks(pageable);
    }

    @RequestMapping(value = { "bankbranches/{bbId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<BankBranches> getBankBranches(@DataTable DataTablesRequest pageable,@PathVariable Long bbId)
            throws IllegalAccessException {
        return accountsService.findBankBranches(pageable,bbId);
    }

    @RequestMapping(value =  "createBank" , method = {org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createBank(Banks bank) throws IllegalAccessException, BadRequestException {
        accountsService.defineBank(bank);
    }

    @RequestMapping(value = { "createBankBranch" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createBankBranch(BankBranches branch) throws IllegalAccessException, BadRequestException {
        accountsService.defineBankBranch(branch);
    }

    @RequestMapping(value = { "deleteBank/{bnId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBank(@PathVariable Long bnId) {
        accountsService.deleteBank(bnId);
    }

    @RequestMapping(value = { "deleteBankBranch/{bbId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBankBranch(@PathVariable Long bbId) {
        accountsService.deleteBankBranch(bbId);
    }


    @RequestMapping(value = { "collectionAccts" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CollectionAccounts> getCollectionAccts(@DataTable DataTablesRequest pageable,
                                                                   @RequestParam(value = "pmId", required = false) Long pmId)
            throws IllegalAccessException {
        return accountsService.findCollectionAccts(pageable, pmId);
    }

    @RequestMapping(value = { "payees" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PayeesDTO> getPayees(@DataTable DataTablesRequest pageable) {
        return accountsService.findAllPayees(pageable);
    }

    @RequestMapping(value = { "finalReportFormat" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<FinalReportFormatDTO> getFinalReportFormat(@RequestParam(value = "type", required = false) String type,@DataTable DataTablesRequest pageable) {
        return accountsService.findFinalReportFormats(type,pageable);
    }

    @RequestMapping(value = { "finalReportFormatTotals" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<FinalReportFormatTotalDTO> getFinalReportFormatTotals(@RequestParam(value = "type", required = false) String type,@DataTable DataTablesRequest pageable) {
        return accountsService.findFinalReportFormatsTotals(type,pageable);
    }

    @RequestMapping(value = { "finalReportFormatAccounts" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<FinalReportFormatAcctsDTO> getFinalReportFormatAccounts(@RequestParam(value = "formatId", required = false) Long formatId,@DataTable DataTablesRequest pageable) {
        return accountsService.findFinalReportFormatsAccts(formatId,pageable);
    }

    @RequestMapping(value = { "finalReportFormatGroupAccounts" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<FinalReportFormatAcctsDTO> getFinalReportFormatGroupAccounts(@RequestParam(value = "formatId", required = false) Long formatId,@DataTable DataTablesRequest pageable) {
        return accountsService.findFinalReportFormatsGroupAccts(formatId,pageable);
    }


    @RequestMapping(value = { "accountingYears" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<AccountYearDTO> getAccountingYears(@RequestParam(value = "obId", required = false) Long obId,
                                                               @RequestParam(value = "year",required = false) Long year,
                                                               @DataTable DataTablesRequest pageable) {
        return accountsService.findAccountingYears(year,obId,pageable);
    }

    @RequestMapping(value = { "accountingYearPeriods" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<AccountingPeriodDTO> getAccountingYearPeriods( @RequestParam(value = "yearId",required = false) Long yearId,
                                                                           @DataTable DataTablesRequest pageable) {
        return accountsService.findAccountingYearsPeriods(yearId,pageable);
    }

    @RequestMapping(value = { "payeesaccounts/{payId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PayeeAccountsDTO> getPayeesAccounts(@PathVariable Long payId, @DataTable DataTablesRequest pageable) {
        return accountsService.findAllPayeesAccounts(payId, pageable);
    }

    @RequestMapping(value = { "openingbalances" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<OpeningBalanceDTO> getOpeningBalances(@DataTable DataTablesRequest pageable) throws BadRequestException {
        return accountsService.findAllOpeningBalances( pageable);
    }

    @RequestMapping(value = { "gltransactions" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<GlTransactions> getGlTransactions(
            @DataTable DataTablesRequest pageable,
            @RequestParam(required = false) String accountNo,
            @RequestParam(required = false) String asAtDate) throws IllegalAccessException {
        return accountsService.findAllGlTransactions(pageable, accountNo, asAtDate);
    }

    @RequestMapping(value = "rpt_gltransactions", method = RequestMethod.POST)
    public ModelAndView generateGlTransactionsReport(
            @RequestParam("asAtDate") @DateTimeFormat(pattern = "dd/MM/yyyy") Date asAtDate,
            @RequestParam(value = "accountNo", required = false) String accountNo,
            @RequestParam(value = "format", defaultValue = "pdf") String format,
            ModelMap modelMap,
            HttpServletRequest request) throws BadRequestException, IOException {

        // Get organization details for report header
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);

        // Add parameters to model
        modelMap.put("logo", image);
        modelMap.put("asAtDate", asAtDate);
        modelMap.put("datasource", datasource);
        modelMap.put("format", format); // Accepts pdf, csv, xlsx

        // Add accountNo to model if provided (for optional filtering in the report)
        if (accountNo != null && !accountNo.trim().isEmpty()) {
            modelMap.put("accountNo", accountNo);
        }

        auditTrailLogger.log("Generated GL Transactions Report" +
                        (accountNo != null && !accountNo.trim().isEmpty() ?
                                " for account " + accountNo : " for all accounts"),
                request, "GL Listings Report");

        // Return report view
        return new ModelAndView("rpt_gl_listing", modelMap);
    }
//    bulk uploads report
    @RequestMapping(value = "rpt_bulk_uploads", method = RequestMethod.POST)
    public ModelAndView generateBulkUploadsReport(
            @RequestParam("dateFrom") @DateTimeFormat(pattern = "dd/MM/yyyy") Date dateFrom,
            @RequestParam("dateTo") @DateTimeFormat(pattern = "dd/MM/yyyy") Date dateTo,
            @RequestParam(value = "agent", required = false) String agent,
            @RequestParam(value = "format", defaultValue = "pdf") String format,
            ModelMap modelMap,
            HttpServletRequest request) throws BadRequestException, IOException {

        // Get organization details for report header
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);

        // Add parameters to model
        modelMap.put("logo", image);
        modelMap.put("dateFrom", dateFrom);
        modelMap.put("dateTo", dateTo);
        modelMap.put("datasource", datasource);
        modelMap.put("format", format); // Accepts pdf, csv, xlsx

        // Add accountNo to model if provided (for optional filtering in the report)
//        if (accountNo != null && !accountNo.trim().isEmpty()) {
//            modelMap.put("accountNo", accountNo);
//        }
//
//        auditTrailLogger.log("Generated GL Transactions Report" +
//                        (accountNo != null && !accountNo.trim().isEmpty() ?
//                                " for account " + accountNo : " for all accounts"),
//                request, "GL Listings Report");

        // Return report view
        return new ModelAndView("rpt_bulk_uploads", modelMap);
    }


    @RequestMapping(value = { "createCollectAcct" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createCollectAccounts(CollectionAccounts accounts) throws IllegalAccessException, BadRequestException {
        accountsService.defineCollectAccount(accounts);
    }

    @RequestMapping(value = { "deleteCollectAcct/{accId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCollectAcct(@PathVariable Long accId) {
        accountsService.deleteCollectAcct(accId);
    }

    @RequestMapping(value = { "selPaymentModes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<PaymentModes> selPaymentModes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return accountsService.findPaymentModes(term, pageable);
    }

    @RequestMapping(value = { "selBankBranches" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<BankBranchDTO> selBankBranches(@RequestParam(value = "term", required = false) String term, Pageable pageable){
        return accountsService.findBankBranches(term, pageable);
    }

    @RequestMapping(value = { "selAccountingPeriods" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<AccountingPeriodDTO> selAccountingPeriods(@RequestParam(value = "term", required = false) String term, Pageable pageable){
        return accountsService.findAccountingPeriods(term, pageable);
    }

    @RequestMapping(value = { "creditSettlements/{crId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ReceiptSettlementDetails> getCreditSettlements(@DataTable DataTablesRequest pageable,@PathVariable Long crId)
            throws IllegalAccessException {
        return accountsService.findCreditSettlements(pageable,crId);
    }

    @RequestMapping(value = { "createRefund" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void createRefund(Refunds refunds) throws IllegalAccessException, IOException, BadRequestException {
        accountsService.createRefund(refunds,"D");
    }
    @RequestMapping(value = { "makeRefundReady/{refundId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void makeRefundReady(@PathVariable Long refundId,HttpServletRequest request) throws BadRequestException {

        accountsService.makeRefundReady(refundId);

    }

    @RequestMapping(value = { "authorizeRefund/{refundId}/{accountNo}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void authorizeRefund(@PathVariable Long refundId, @PathVariable String accountNo, HttpServletRequest request) throws BadRequestException {

        accountsService.authorizeRefund(refundId, accountNo);

    }

    @RequestMapping(value = {"rejectRefund/{refundId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectRefund(@PathVariable Long refundId, @RequestParam(value = "remarks", required = false) String remarks,HttpServletRequest request) throws BadRequestException {
        accountsService.rejectRefund(refundId, remarks);
    }

    @RequestMapping(value = { "getRefunds" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<Refunds> getRefundTrans(@DataTable DataTablesRequest pageable,
                                                    @RequestParam(value = "dateFrom", required = false) Date from,
                                                    @RequestParam(value = "dateTo", required = false) Date dateTo,
                                                    @RequestParam(value = "refno", required = false) String refno,
                                                    @RequestParam(value = "status", required = false) String status,
                                                    @RequestParam(value = "policyNo", required = false) String policyNo,
                                                    @RequestParam(value = "clientId", required = false) Long clientId
    ) throws IllegalAccessException {
        return accountsService.findRefundTrans(pageable, from,dateTo,refno,status,policyNo,clientId);
    }

    @RequestMapping(value = { "subagentAccounts" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<AccountDef> selAccounts(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return accountsService.findSubAgentAccounts(term, pageable);
    }

//    @ModelAttribute("integrationReceipts")
//    public IntegrationReceipts getIntegrationReceipts() {
//        return new IntegrationReceipts();
//    }

    @RequestMapping(value = "receiptsIntegration",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String integrationReceipts(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Receipt Integration Screen ", request, "Receipts Integration");
        return "integrationReceipts";
    }

//    @RequestMapping(value = { "integrationReceipts" }, method = {
//            org.springframework.web.bind.annotation.RequestMethod.POST })
//    public String startClmTrans(@Valid @ModelAttribute IntegrationReceipts integrationReceipts, BindingResult result, RedirectAttributes redirectAttrs, Model model){
//        if(integrationReceipts.getTransType()==null || StringUtils.isBlank(integrationReceipts.getTransType())){
//            redirectAttrs.addFlashAttribute("error", "Select Receipt Type");
//            redirectAttrs.addFlashAttribute("integrationReceipts", integrationReceipts);
//            return "redirect:/protected/accounts/receiptsIntegration";
//        }
//        switch(integrationReceipts.getTransType()){
//            case "Mpesa":
//                return "mobmoney";
//            case "Bank":
//                return "bankreceiptlist";
//            default:
//                return "redirect:/protected/accounts/receiptsIntegration";
//        }
//    }

    @RequestMapping(value = "processCommission",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String processcommissions(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Process Commission Screen ", request, "Process Commission");
        return "processCommission";
    }


    @RequestMapping(value = { "importCommissions" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void importCommissionTrans(RiskUploadForm uploadForm, HttpServletRequest request) throws BadRequestException, IOException {
        commissionsPayinsService.importCommissions(uploadForm.getFile(),uploadForm);
    }

    @RequestMapping(value = "uploadExcel",method = RequestMethod.POST)
    @ResponseBody
    public String uploadExcel(@RequestParam MultipartFile file, @RequestParam("acctId") Long acctId) throws IOException, BadRequestException {
        if(acctId==null){
            throw new BadRequestException("No Underwriter Selected....");
        }
        commissionsPayinsService.processCommExcel(file,acctId);
        return "File uploaded successfully";
    }

    @RequestMapping(value = "reconcileCommission", method = RequestMethod.POST)
    @ResponseBody
    public List<CommissionReconciliationData> reconcileCommission(@RequestBody List<CommissionReconciliationDTO> commissionReconciliationData) throws BadRequestException {
        // No more session attributes needed
        return commissionsPayinsService.reconcileCommissionData(commissionReconciliationData);
    }

    @RequestMapping(value = "deleteCommissions", method = RequestMethod.POST)
    @ResponseBody
    public List<CommissionReconciliationData> deleteCommissions(@RequestBody List<CommissionReconciliationDTO> commissionReconciliationData) throws BadRequestException {
        // No more session attributes needed
         commissionsPayinsService.deleteCommission(commissionReconciliationData);
         return new ArrayList<>();
    }



    @RequestMapping(value = "reconciliationCommissionData", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<CommissionReconciliationDTO> getReconciliationData(@DataTable DataTablesRequest request, @RequestParam(required = false) Long accountCode, @RequestParam(required = false) Date dateFrom, @RequestParam(required = false) Date dateTo){
        return commissionsPayinsService.getCommissionReconciliationData(request, accountCode);
    }
    @RequestMapping(value = "unreconciledCommissionData", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<CommissionUnreconciledDataDTO> getUnreconciledData(@DataTable DataTablesRequest request,@RequestParam(required = false) Long accountCode) {
        System.out.println("Account Code "+accountCode);
        return commissionsPayinsService.getUnreconciledCommissionData(request,accountCode);
    }


    @RequestMapping(value = { "commissiontrans/{acctId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CommissionTrans> getCommission(@DataTable DataTablesRequest pageable, @PathVariable Long acctId)
            throws IllegalAccessException {
        return commissionsPayinsService.findAllLoadedCommissions(pageable,acctId);
    }

    @RequestMapping(value = { "pendingcommissiontrans/{acctId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CommissionDTO> getPendingCommission(@DataTable DataTablesRequest pageable, @PathVariable Long acctId)
            throws IllegalAccessException {
        return commissionsPayinsService.findAllConfirmedCommissions(acctId,pageable);
    }

//    @RequestMapping(value = "uploadExcel",method = RequestMethod.POST)
//    @ResponseBody
//    public String uploadExcel(@RequestParam MultipartFile file) throws IOException {
//        commissionsPayinsService.processCommExcel(file);
//        return "File uploaded successfully";
//    }

    @RequestMapping(value = { "currentAccountingYear" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Map<String, Long> getCurrentYear(){
        return accountsService.getCurrentAccountPeriod();
    }


    @RequestMapping(value = { "insurancereceipts" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<SystemTransactions> selectReceipts(@RequestParam(value = "term", required = false) String term,@RequestParam("accountId") Long accountId,  Pageable pageable)
            throws IllegalAccessException {
        return accountsService.findInsuranceReceipts(term,accountId, pageable);
    }


    @RequestMapping(value = {"processCommissions"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> processCommissions(@RequestBody CommissionData commissionData) throws  BadRequestException {
        commissionsPayinsService.processCommissions(commissionData);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = {"confirmCommissions"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> confirmCommissions(@RequestBody List<Long> commissionReceipts) throws  BadRequestException {
        for(Long commReceipt:commissionReceipts){
            commissionsPayinsService.confirmCommissions(commReceipt);
        }
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = {"undoProcessCommissions/{transId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void undoProcessCommissions(@PathVariable Long transId,HttpServletRequest request) throws  BadRequestException {
        commissionsPayinsService.undoProcessCommissions(transId);
    }



//    @RequestMapping(value = "commissionsTemplate",method={org.springframework.web.bind.annotation.RequestMethod.GET})
//    @ResponseBody
//    public Response commissionsTemplate(HttpServletRequest request) throws BadRequestException
//    {
//        String name ="commission_upload_template.xls";
//        File file = commissionsPayinsService.getCommissionsTemplate();
//        auditTrailLogger.log("Downloaded The Commission Template ", request);
//        ResponseBuilder response = Response.ok((Object) file);
//        response.header("Content-Disposition","attachment; filename="+name);
//        return response.build();
//    }
//
//    @RequestMapping(value = "commissionsTemplate",method={org.springframework.web.bind.annotation.RequestMethod.GET})
//    @ResponseBody
//    public ResponseEntity commissionsTemplate(HttpServletRequest request) throws BadRequestException,IOException
//    {
//        String name ="commission_upload_template.xls";
//        File file = commissionsPayinsService.getCommissionsTemplate();
//        Path path = Paths.get(file.getAbsolutePath());
//        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
//        auditTrailLogger.log("Downloaded The Commission Template ", request);
//        return ResponseEntity.ok()
////                .headers(headers)
//                .header("Content-Disposition","attachment; filename="+name)
//                .contentLength(file.length())
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(resource);
//    }

//    @RequestMapping(value = "commissionsTemplate",method={org.springframework.web.bind.annotation.RequestMethod.GET})
//    @ResponseBody
//    public void  commissionsTemplate(HttpServletRequest request, HttpServletResponse response) throws BadRequestException,IOException
//    {
//        String name ="commission_upload_template";
//        File myFile = commissionsPayinsService.getCommissionsTemplate();
//
//        // create full filename and get input stream
//        InputStream file = new FileInputStream(myFile);
//        auditTrailLogger.log("Downloaded The Commission Template ", request);
//
//        // set file as attached data and copy file data to response output stream
//        response.setHeader("Content-Disposition", "attachment; filename=\"" + name + ".xls\"");
//        FileCopyUtils.copy(file, response.getOutputStream());
//
//        // delete file on server file system
////        licenseFile.delete();
//
//        // close stream and return to view
//        response.flushBuffer();
//    }


    @RequestMapping(value = "commissionsTemplate",method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public void  commissionsTemplate(HttpServletRequest request, HttpServletResponse response) throws BadRequestException,IOException
    {
        String name ="commission_upload_template";
        try {
            File myFile = commissionsPayinsService.getCommissionsTemplate();
            // get your file as InputStream
            InputStream is =  new FileInputStream(myFile);

            // Audit User Actions
            auditTrailLogger.log("Downloaded The Commission Template: "+ name+ ".xls", request, "Commissions Template");

            // copy it to response's OutputStream
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + name + ".xls\"");
            response.setContentType("application/vnd.ms-excel");
            response.flushBuffer();
        } catch (IOException ex) {
            ex.printStackTrace();
            //log.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    @RequestMapping(value = { "createPayee" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createPayee(PayeesDTO payeesDTO) throws  BadRequestException {
        accountsService.createPayee(payeesDTO);
    }

    @RequestMapping(value = { "createPayeeAccount" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createPayeeAccount(PayeeAccountsDTO payeesDTO) throws  BadRequestException {
        accountsService.createPayeeAccount(payeesDTO);
    }

    @RequestMapping(value = { "createAccountingYear" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccountingYear(AccountYearDTO yearDTO) throws  BadRequestException {
        accountsService.createAccountingYear(yearDTO);
    }

    @RequestMapping(value = { "createReportFormatAccount" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createReportFormatAccount(FinalReportFormatAcctsDTO reportFormatAcctsDTO) throws  BadRequestException {
        accountsService.createReportFormatAccount(reportFormatAcctsDTO);
    }

    @RequestMapping(value = { "createReportFormatGroupAccount" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createReportFormatGroupAccount(FinalReportFormatAcctsDTO reportFormatAcctsDTO) throws  BadRequestException {
        accountsService.createReportFormatGroupAccount(reportFormatAcctsDTO);
    }

//    @RequestMapping(value = { "getSystemTransByClientType" }, method = {
//            org.springframework.web.bind.annotation.RequestMethod.GET })
//    @ResponseStatus(HttpStatus.OK)
//    public Page<SystemTransactions> findSystemTransactionsByClientType(String clientType, Pageable pageable){
//        return accountsService.findSystemTransByClientType(clientType, pageable);
//    };
@RequestMapping(value = "printcoa", method = RequestMethod.GET)
public ModelAndView riskNote(
        @RequestParam(value = "format", defaultValue = "pdf") String format,
        ModelMap modelMap)
        throws BadRequestException, IOException {
  OrganizationDTO org = orgService.getOrganizationLogoDetails();
    InputStream in = new ByteArrayInputStream(
            Files.readAllBytes(Paths.get(org.getOrgLogo())));
    BufferedImage image = ImageIO.read(in);

    modelMap.put("logo", image);
    modelMap.put("datasource", datasource);
    modelMap.put("format", format);


    return new ModelAndView("rpt_coa", modelMap);
}

    @RequestMapping(value = {"bulkreconciletemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-comm-upload-template.xls");

        String[] sheet1Headers = {
                "InsureMaster Policy No", "underwriter Policy No", "Reference Number", "Underwriter Trans Code","Debit Ref No","Credit Ref No", "Gross Premiums","Gross Commission","Gross Admin Fee", "Gross Revenue", "WHT Commission", "WHT Admin Fee","Net Revenue"
        };

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Bulk Comm");
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