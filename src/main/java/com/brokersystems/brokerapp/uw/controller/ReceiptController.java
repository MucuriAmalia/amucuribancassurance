package com.brokersystems.brokerapp.uw.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import com.brokersystems.brokerapp.accounts.dtos.SystemTransDTO;
import com.brokersystems.brokerapp.accounts.model.CollectionAccounts;
import com.brokersystems.brokerapp.medical.model.SelfFundParams;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.CurrencyDTO;
import com.brokersystems.brokerapp.setup.service.SetupsService;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.webservices.service.MobileMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.trans.service.ReceiptService;

@Controller
@RequestMapping({ "/protected/uw/receipts" })
public class ReceiptController {
	
	@Autowired
	private ReceiptService receiptService;

	@Autowired
	private PolicyTransService transService;
	
	
	@Autowired
	  private DataSource datasource;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private MobileMoneyService mobileMoneyService;
	
	@Autowired
	private AuditTrailLogger auditTrailLogger;

	@Autowired
	SetupsService setupsService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
	
	@RequestMapping(value = "receiptList", method = RequestMethod.GET)
	public String receiptList(Model model) {
		return "receiptslist";
	}


	@RequestMapping(value = "reprintrecpt", method = RequestMethod.GET)
	public String reprintrcts(Model model,HttpServletRequest request) {
		String message="Accessed Reprint Receipts Screen";
		String resource="Reprint Receipts";
		auditTrailLogger.log(message,request, resource);
		return "reprintrcts";
	}


	@RequestMapping(value = "cancelrec", method = RequestMethod.GET)
	public String canelReceipts(Model model,HttpServletRequest request) {
		String message="Accessed Cancel Receipts Screen";
		String resource="Cancel Receipts";
		auditTrailLogger.log(message,request, resource);

		return "cancelrcts";
	}

	@RequestMapping(value = "uprintedrcts", method = RequestMethod.GET)
	public String uprintedrcts(Model model,HttpServletRequest request) {
		String message="Accessed Unprinted Receipts Screen";
		String resource="Unprinted Receipts";
		auditTrailLogger.log(message,request, resource);

		return "unprintedrcts";
	}
	
	@RequestMapping(value = "receiptentry", method = RequestMethod.GET)
	public String receiptlist(Model model, HttpServletRequest request)
	{
		String message="Accessed New Receipts Screen";
		String resource="Receipts";
		auditTrailLogger.log(message,request, resource);
		return "receiptsform";
	}
	@RequestMapping(value = "overpaidpolicies", method = RequestMethod.GET)
	public String overpaidPolicies(Model model, HttpServletRequest request)
	{
		String message="Accessed Overpaid Policies Screen";
		String resource="Overpaid Policies";
		auditTrailLogger.log(message,request, resource);
		return "overpaidpolicies";
	}

	@RequestMapping(value = "mobmoney", method = RequestMethod.GET)
	public String mobMoney(Model model,HttpServletRequest request) {
		String message="Accessed Mobile Money Screen";
		String resource="Mobile Money";
		auditTrailLogger.log(message,request, resource);
		return "mobmoney";
	}
	@RequestMapping(value = { "getRcpts/{receiptCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public ReceiptTrans getAccountReceipts(@PathVariable Long receiptCode) {
		ReceiptTrans receipts =  setupsService.getReceipts(receiptCode);
		return receipts;
	}
	@RequestMapping(value = "bankreceipts", method = RequestMethod.GET)
	public String bankReceiptList(Model model) {
		return "bankreceiptlist";
	}

	@RequestMapping(value = { "receipts" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ReceiptTrans> getAllReceipts(@DataTable DataTablesRequest pageable)
			throws IllegalAccessException {
		return receiptService.findAllReceipts(pageable);
	}
	
	@RequestMapping(value = { "selclienttrans" }, method = { RequestMethod.GET })
	@ResponseBody
	public Page<SystemTransDTO> getTransactions(@RequestParam(value = "term", required = false) String term, Pageable pageable,
			                                     @RequestParam("acctId")Long acctId)
			throws IllegalAccessException {
		return receiptService.findReceiptTransactions(term,acctId, pageable);
	}
	@RequestMapping(value = { "selcommtrans" }, method = { RequestMethod.GET })
	@ResponseBody
	public Page<SystemTransactions> getCommissionTrans(@RequestParam(value = "term", required = false) String term,
													Pageable pageable,@RequestParam("acctId")Long acctId)
			throws IllegalAccessException {
		return receiptService.findAgentCommisionTrans(term,pageable,acctId);
	}

	@RequestMapping(value = { "creditorcommtrans" }, method = { RequestMethod.GET })
	@ResponseBody
	public Page<SystemTransactions> findCreditorCommisionTrans(@RequestParam(value = "term", required = false) String term,
													   Pageable pageable)
			throws IllegalAccessException {
		return receiptService.findCreditorCommisionTrans(term,pageable);
	}

	@RequestMapping(value = { "selfundtrans" }, method = { RequestMethod.GET })
	@ResponseBody
	public Page<SelfFundParams> getFundTransactions(@RequestParam(value = "term", required = false) String term,
													Pageable pageable)
			throws IllegalAccessException {
		return receiptService.findSelfFundTransactions(term,pageable);
	}

	@RequestMapping(value = { "lifepolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public Page<PolicyTrans> getLifePolicies(@RequestParam(value = "term", required = false) String term,
											 Pageable pageable)
			throws IllegalAccessException {
		return receiptService.findLifeTransactions(term,pageable);
	}
	
	@RequestMapping(value = { "currencies" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<CurrencyDTO> allCurrencies(@RequestParam(value = "term", required = false) String term,
										   Pageable pageable) throws IllegalAccessException {
		return transService.findCurrencies(term, pageable);
	}
	
	@RequestMapping(value = { "collectAccts" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<CollectionAccounts> collectionAccounts(@RequestParam(value = "term", required = false) String term,
												 Pageable pageable) throws IllegalAccessException {
		return receiptService.findCollectionAccts(term, pageable);
	}
	
	@RequestMapping(value = { "createReceipt" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<Long> createReceipt(@RequestBody ReceiptTrans receipt) throws BadRequestException {
		if(receipt.getFundReceipt()==null || "off".equalsIgnoreCase(receipt.getFundReceipt()))
			receipt.setFundReceipt("N");
		else if("on".equalsIgnoreCase(receipt.getFundReceipt()))
			receipt.setFundReceipt("Y");
		else
			receipt.setFundReceipt("N");
		Long created = -200l;
		if("N".equalsIgnoreCase(receipt.getFundReceipt()))
		   created = receiptService.createReceipt(receipt, false);
		else
			created = receiptService.createFundReceipt(receipt);
		return new ResponseEntity<Long>(created,HttpStatus.OK);
	}
	
	@RequestMapping(value = { "allocateReceipt" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void allocateReceipt(@RequestParam(value = "receiptCode", required = false) Long receiptCode) throws BadRequestException {
		receiptService.markReceiptPrinted(receiptCode,userUtils.getCurrentUser());
	}
	
	@RequestMapping(value = "receipt_rpt/{receiptNo}", method = RequestMethod.GET)
	public ModelAndView receiptRpt(ModelMap modelMap, ModelAndView modelAndView, @PathVariable Long receiptNo) throws BadRequestException { 
	  modelMap.put("datasource", datasource);
	  modelMap.put("format", "pdf");
	  modelMap.put("receiptNo", receiptNo);
	  modelAndView = new ModelAndView("rpt_receipt", modelMap);
	  return modelAndView;
	}

	@RequestMapping(value = { "printedReceipts" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ReceiptTrans> getPrintedReceipts(@DataTable DataTablesRequest pageable,
															@RequestParam(value = "dateFrom", required = false) Date from,
															@RequestParam(value = "dateTo", required = false) Date dateTo) throws IllegalAccessException {
		return receiptService.findPrintedReceipts(pageable, from,dateTo);
	}

	@RequestMapping(value = { "cancelReceipts" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ReceiptTrans> getReceiptsToCancel(@DataTable DataTablesRequest pageable,
															 @RequestParam(value = "dateFrom", required = false) Date from,
															 @RequestParam(value = "dateTo", required = false) Date dateTo,
															 @RequestParam(value = "refno", required = false) String refno,
															 @RequestParam(value = "receiptNo", required = false) String receiptNo,
															 @RequestParam(value = "policyNo", required = false) String policyNo,
															 @RequestParam(value = "clientId", required = false) Long clientId
															  ) throws IllegalAccessException {
		System.out.println("Dr no "+refno);
		return receiptService.findReceiptsToCancel(pageable, from,dateTo,refno,receiptNo,policyNo,clientId);
	}


	@RequestMapping(value = { "unprintedReceipts" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ReceiptTrans> getUnprintedReceipts(@DataTable DataTablesRequest pageable,
															 @RequestParam(value = "dateFrom", required = false) Date from,
															 @RequestParam(value = "dateTo", required = false) Date dateTo) throws IllegalAccessException {
		return receiptService.findUnPrintedReceipts(pageable, from,dateTo);
	}



	@RequestMapping(value = "printReceipts", method = RequestMethod.POST)
	public ResponseEntity<String> printReport(@RequestBody PrintReceipts printReceipts)
			throws BadRequestException, IOException {
		receiptService.createReceipts(printReceipts.getReceipts());
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = "printbatchreceipt", method = RequestMethod.GET)
	public ModelAndView printBatchReceipt(ModelMap modelMap, ModelAndView modelAndView) throws BadRequestException {
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelMap.put("user_code", userUtils.getCurrentUser().getId());
		modelAndView = new ModelAndView("rpt_receipt_printed", modelMap);
		return modelAndView;
	}

	@RequestMapping(value = "deleteReceipts", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void markPrintedCerts()
			throws BadRequestException, IOException {
		receiptService.deleteCertTrans();
	}

	@RequestMapping(value = "markReceiptsPrinted", method = RequestMethod.POST)
	public ResponseEntity<String> printMarkReceipted(@RequestBody PrintReceipts printReceipts)
			throws BadRequestException, IOException {
		receiptService.markReceiptsPrinted(printReceipts.getReceipts());
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = "cancelReceipts", method = RequestMethod.POST)
	public ResponseEntity<String> cancelReceipts(@RequestBody CancelRecData printReceipts)
			throws BadRequestException, IOException {
		boolean isApproved = printReceipts.isApproved();
		receiptService.cancelReceipts(printReceipts.getReceipts(),isApproved);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}


	@RequestMapping(value = { "integrationDtls" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<IntegrationDtls> getIntegrationDtls(@DataTable DataTablesRequest pageable,@RequestParam(value = "receipted", required = false) String receipted) throws IllegalAccessException {
		System.out.println("Receipted..."+receipted);
		return receiptService.findIntegrationDtls(pageable,receipted);
	}

	@RequestMapping(value = { "updateIntegrationDtls" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void updateIntegrationDtls(IntegrationDtls integrationDtls) throws IllegalAccessException, BadRequestException {
		receiptService.updateIntegrationDtls(integrationDtls);
	}

	@RequestMapping(value = { "processMpesaTrans/{transId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void processMpesaTrans(@PathVariable String transId) throws BadRequestException {
		mobileMoneyService.autoReceipt(transId);
	}
 }
