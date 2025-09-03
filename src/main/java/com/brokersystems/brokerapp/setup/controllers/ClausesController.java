package com.brokersystems.brokerapp.setup.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ClausesDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.setup.model.SubclassClauseBean;
import com.brokersystems.brokerapp.setup.model.SubclassClauses;
import com.brokersystems.brokerapp.setup.service.ClassesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping({ "/protected/setups/clauses" })
public class ClausesController {
	
	@Autowired
	private ClassesService classService;

	@Autowired
	private AuditTrailLogger auditTrailLogger;

	
	@RequestMapping(value = "clausesHome",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String classHome(Model model, HttpServletRequest request)
	  {
		  auditTrailLogger.log("Accessed clauses screen",request, "Clauses");
	  	return "clausesHome";
	  }

	
	@RequestMapping(value = { "clausesList/{type}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ClausesDef> getAllClauses(@DataTable DataTablesRequest pageable,@PathVariable String type)
			throws IllegalAccessException {
		return classService.findAllClauses(pageable,type);
	}
	
	@RequestMapping(value = { "createClauseDef" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createClause(ClausesDef clauseDef) throws IllegalAccessException, IOException, BadRequestException {
		classService.createClauses(clauseDef);
	}
	
	@RequestMapping(value = { "deleteClause/{clauseId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteClause(@PathVariable Long clauseId) {
		classService.deleteClause(clauseId);
	}
	
	
	@RequestMapping(value = { "subclassClauses/{subCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<SubclassClauses> getSubclassClause(@DataTable DataTablesRequest pageable,@PathVariable Long subCode,@RequestParam(value = "search", required = false) String search )
			throws IllegalAccessException {
		return classService.findSubClassClauses(pageable,subCode,search);
	}
	
	@RequestMapping(value={"subclassSelect"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  @ResponseBody
	  public Page<SubClassDef> selectSubclasses(@RequestParam(value="term", required=false) String term, Pageable pageable)
	    throws IllegalAccessException
	  {
	    return classService.findSubclassSelect(term, pageable);
	  }
	
	@RequestMapping(value = { "unassignedClauses" }, method = { RequestMethod.GET })
	@ResponseBody
	public List<ClausesDef> getUnassignedClauses(@RequestParam(value = "subCode", required = false) Long subCode,@RequestParam(value = "search", required = false) String search )
			throws IllegalAccessException {
		return classService.findUnassignedClauses(subCode,search);
	}
	
	@RequestMapping(value = { "createSubclassClauses" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<String> createSubclassClauses(@RequestBody SubclassClauseBean subclassClause) throws IllegalAccessException, IOException, BadRequestException {
		classService.createSubClauses(subclassClause);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}
	
	@RequestMapping(value = { "createSubClauseDef" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createSubClause(SubclassClauses subClause) throws IllegalAccessException, IOException, BadRequestException {
		classService.createSubClause(subClause);
	}
	
	@RequestMapping(value = { "deleteSubClause/{clauseId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSubClause(@PathVariable Long clauseId) {
		classService.deleteSubClause(clauseId);
	}
	

}
