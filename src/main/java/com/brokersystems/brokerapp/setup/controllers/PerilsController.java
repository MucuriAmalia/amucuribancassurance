package com.brokersystems.brokerapp.setup.controllers;

import java.io.IOException;

import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.model.County;
import com.brokersystems.brokerapp.setup.model.SubclassPerils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ClausesDef;
import com.brokersystems.brokerapp.setup.model.PerilsDef;
import com.brokersystems.brokerapp.setup.service.SetupsService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({ "/protected/setups/perils" })
public class PerilsController {
	
	@Autowired
	private SetupsService setupService;

	@Autowired
	private AuditTrailLogger auditTrailLogger;

	@RequestMapping(value = "perilsdefHome",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String classHome(Model model, HttpServletRequest request)
	  {
		  auditTrailLogger.log("Accessed Perils screen ",request, "Perils");
	    return "perilsdef";
	  }
	
	@RequestMapping(value = { "perilsList" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PerilsDef> getAllPerils(@DataTable DataTablesRequest pageable)
			throws IllegalAccessException {
		return setupService.findPerils(pageable);
	}
	
	@RequestMapping(value = { "createPerilsDef" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createClause(PerilsDef peril, HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {

		setupService.definePerils(peril);
	}
	
	@RequestMapping(value = { "deletePeril/{perilCode}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePeril(@PathVariable Long perilCode) {
		setupService.deletePerils(perilCode);
	}

	@RequestMapping(value = { "subperilsList/{subCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<SubclassPerils> getSubclassPerils(@DataTable DataTablesRequest pageable,@PathVariable Long subCode)
			throws IllegalAccessException {
		return setupService.findSubclassPerils(subCode,pageable);
	}

	@RequestMapping(value = { "createSubPeril" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createSubPeril(SubclassPerils subclassPeril) throws IllegalAccessException, IOException, BadRequestException {
		setupService.defineSubClassPerils(subclassPeril);
	}

	@RequestMapping(value = { "deleteSubPeril/{subPerildId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSubPeril(@PathVariable Long subPerildId) {
		setupService.deleteSubPerils(subPerildId);
	}

	@RequestMapping(value={"selectperils"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<PerilsDef> selectCounties(@RequestParam(value="term", required=false) String term, @RequestParam("subCode") Long subCode, Pageable pageable)
			throws IllegalAccessException, BadRequestException
	{
		return this.setupService.findSelectPerils(term,pageable,subCode);
	}


}
