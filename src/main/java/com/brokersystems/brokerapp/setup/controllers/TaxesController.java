package com.brokersystems.brokerapp.setup.controllers;

import java.io.IOException;

import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.ProductSubclasses;
import com.brokersystems.brokerapp.setup.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.brokersystems.brokerapp.setup.model.RevenueItemsDef;
import com.brokersystems.brokerapp.setup.model.TaxRates;
import com.brokersystems.brokerapp.setup.service.SetupsService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({ "/protected/setups/taxes" })
public class TaxesController {
	
	@Autowired
	private SetupsService service;

	@Autowired
	private ClassesService classesService;


	@Autowired
	private AuditTrailLogger auditTrailLogger;
	
	@RequestMapping(value = "taxesdefHome",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String classHome(Model model, HttpServletRequest request)
	  {
		  auditTrailLogger.log("Accessed taxes screen ",request, "Taxes Definition");
	  	return "taxratesdef";
	  }
	
	@RequestMapping(value = { "taxRates" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<TaxRates> getTaxRates(@DataTable DataTablesRequest pageable,@RequestParam(value = "subCode", required = false) Long subCode,
												  @RequestParam(value = "prodCode", required = false) Long prodCode)
			throws IllegalAccessException {
		return service.findTaxRates(subCode,prodCode, pageable);
	}
	
	@RequestMapping(value = { "createTaxRates" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createTaxRates(TaxRates tax) throws IllegalAccessException, IOException, BadRequestException {
		service.defineTaxRates(tax);
	}
	
	 @RequestMapping(value = { "deletTaxRates/{taxCode}" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseStatus(HttpStatus.NO_CONTENT)
		public void deleteBinder(@PathVariable Long taxCode) {
		 service.deleteTaxRates(taxCode);
		}
	 
	 @RequestMapping(value = { "selTaxRevenueItems" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseBody
		public Page<RevenueItemsDef> selTaxRevenueItems(@RequestParam(value = "term", required = false) String term,@RequestParam("prodCode") long prodCode, Pageable pageable)
				throws IllegalAccessException {
			return service.getTaxRevenueItems(term, pageable,prodCode);
		}

	@RequestMapping(value = { "selProdSubClasses" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<ProductSubclasses> selProdSubClasses(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return classesService.findProductSubclasses(term, pageable);
	}

}
