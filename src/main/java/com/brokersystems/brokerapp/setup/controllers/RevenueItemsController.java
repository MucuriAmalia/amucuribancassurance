package com.brokersystems.brokerapp.setup.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;
import com.brokersystems.brokerapp.claims.dtos.ClaimantsDTO;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.BankAccountDTO;
import com.brokersystems.brokerapp.setup.dto.CoaSubAccountsDTO;
import com.brokersystems.brokerapp.setup.dto.CommRecvDto;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.SetupsService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({ "/protected/setups/revitems" })
public class RevenueItemsController {
	
	@Autowired
	private SetupsService service;

	@Autowired
	private BankService bankService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;
	
	@RequestMapping(value = "revitemsHome",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String revitemsHome( HttpServletRequest request)
	  {
		  auditTrailLogger.log("Accessed revenue mapping screen ",request, "Revenue Mapping");
	  	return "revitemsdef";
	  }

	@RequestMapping(value = "uwrevitemsHome",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String uwrevitemsHome( HttpServletRequest request)
	{
		auditTrailLogger.log("Accessed uw revenue mapping screen ",request, "uw Revenue Mapping");
		return "uwrevitemsdef";
	}

	@RequestMapping(value = "bankAccounts",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String bankAccountsHome(HttpServletRequest request)
	{
		auditTrailLogger.log("Accessed Bank Accounts screen ",request, "Bank Accounts");
		return "bankAcctsDef";
	}

	@RequestMapping(value = { "getAllInsurers" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<CommRecv> getAllInsurers(@DataTable DataTablesRequest pageable)
			throws IllegalAccessException {
		return service.getAllInsurers(pageable);
	}
	
	@RequestMapping(value = { "revenueitems/{prgCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RevenueItemsDef> getRevenueItems(@DataTable DataTablesRequest pageable,@PathVariable Long prgCode)
			throws IllegalAccessException {
		return service.findPrgRevenueItems(prgCode, pageable);
	}

	@RequestMapping(value = { "revenueitems" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RevenueItemsDef> getRevenueItems(@DataTable DataTablesRequest pageable)
			throws IllegalAccessException {
		return service.findPrgRevenueItems( pageable);
	}
	
	@RequestMapping(value = { "unassigrevitems" }, method = { RequestMethod.GET })
	@ResponseBody
	public Set<RevenueItemBean> getUnassignedRevItems(@RequestParam(value = "prgCode", required = false) Long prgCode )
			throws IllegalAccessException {
		return service.findUnassignedRevItems(prgCode);
	}
	
	@RequestMapping(value = { "createBulkRevItems" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> createProdSubclass(@RequestBody RevenueItemModel revModel) throws IllegalAccessException, BadRequestException {
        service.createBulkRevenueItems(revModel);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
}
	
	@RequestMapping(value = { "createRevenueItem" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createRevenueItem(RevenueItemsDef revItem) throws IllegalAccessException, IOException, BadRequestException {
		service.updateRevenueItems(revItem);
	}

	@RequestMapping(value = {"createUwRevenueItem"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST })
	public  ResponseEntity<String>  saveRevenueItem(@RequestBody CommRecvDto dto) throws BadRequestException, IllegalAccessException {
		service.createUwAccountDef(dto);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = { "deleteRevenueItem/{revenueId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSequence(@PathVariable Long revenueId) {
		service.deleteRevenueItem(revenueId);
	}

	@RequestMapping(value = { "selGlAccount" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<CoaSubAccountsDTO> selGlAccount(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return service.findGLAccounts(term, pageable);
	}

	@RequestMapping(value = { "selSubclass" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<SubClassDef> selSubclass(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return service.findSubclassSel(term, pageable);
	}

	@RequestMapping(value = { "bankaccounts" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<BankAccountDTO> getAllBankAccounts(@DataTable DataTablesRequest pageable) {
		return bankService.findAllBankAccounts(pageable);
	}

	@RequestMapping(value = { "createBankAccount" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createBankAccount(BankAccountDTO accountDTO) throws  BadRequestException {
		bankService.createBankAccount(accountDTO);
	}

	@RequestMapping(value = { "deleteBankAccount/{accountId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBankAccount(@PathVariable Long accountId) throws BadRequestException {
		bankService.deleteBankAccount(accountId);
	}

}
