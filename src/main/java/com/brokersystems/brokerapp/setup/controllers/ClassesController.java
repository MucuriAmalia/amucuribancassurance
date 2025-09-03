package com.brokersystems.brokerapp.setup.controllers;

import java.io.IOException;
import java.util.List;

import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.repository.ClassesRepo;
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
import com.brokersystems.brokerapp.setup.model.ClassesDef;
import com.brokersystems.brokerapp.setup.model.CoverSectionBean;
import com.brokersystems.brokerapp.setup.model.CoverTypesDef;
import com.brokersystems.brokerapp.setup.model.SectionsDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.setup.model.SubCoverTypeSections;
import com.brokersystems.brokerapp.setup.model.SubclassCoverTypes;
import com.brokersystems.brokerapp.setup.model.SubclassSections;
import com.brokersystems.brokerapp.setup.service.ClassesService;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping({ "/protected/setups/classes" })
public class ClassesController {

	@Autowired
	private ClassesService classService;

	@Autowired
	private AuditTrailLogger auditTrailLogger;
	@Autowired
	private ClassesRepo classesRepo;


	@RequestMapping(value = "classesHome",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String classHome(Model model, HttpServletRequest request)
	{
		auditTrailLogger.log("Accessed classes screen ",request, "Classes");
		return "classes";
	}

	@RequestMapping(value = { "classesList" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ClassesDef> getClasses(@DataTable DataTablesRequest pageable)
			throws IllegalAccessException {
		return classService.findAllClasses(pageable);
	}

	@RequestMapping(value = { "createClassDef" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createClass(ClassesDef classDef) throws IllegalAccessException, IOException, BadRequestException {
		classService.createClass(classDef);
	}

	@RequestMapping(value = { "subclassList/{classCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<SubClassDef> getSubclasses(@DataTable DataTablesRequest pageable,@PathVariable Long classCode)
			throws IllegalAccessException {
		return classService.findAllSubclass(pageable,classCode);
	}

	@RequestMapping(value = { "createSubClass" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createSubclass(SubClassDef subclass) throws IllegalAccessException, IOException, BadRequestException {
		classService.createSubClass(subclass);
	}

	@RequestMapping(value={"classesselect"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<ClassesDef> select(@RequestParam(value="term", required=false) String term, Pageable pageable)
			throws IllegalAccessException
	{
		return classService.findClassesForSelect(term, pageable);
	}

	@RequestMapping(value = { "deleteClass/{classId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteClass(@PathVariable Long classId) {
		classService.deleteClass(classId);
	}

	@RequestMapping(value = { "deleteSubClass/{subId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSubClass(@PathVariable Long subId) {
		classService.deleteSubclass(subId);
	}

	@RequestMapping(value = { "subCovertypes/{subId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<SubclassCoverTypes> getSubCoverTypes(@DataTable DataTablesRequest pageable,@PathVariable Long subId)
			throws IllegalAccessException {
		return classService.findSubclassCoverTypes(pageable,subId);
	}

	@RequestMapping(value={"sclcoverTypes"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<CoverTypesDef> selectCounties(@RequestParam(value="term", required=false) String term, @RequestParam("subId") Long subId, Pageable pageable, @RequestParam("classId") Long classId)
			throws IllegalAccessException, BadRequestException
	{
		return classService.findCoverTypesForSel(term, pageable, subId,classId );
	}

	@RequestMapping(value = { "createCoverType" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createCoverType(CoverTypesDef coverType,@RequestParam Long classId) throws IllegalAccessException, IOException, BadRequestException {
		ClassesDef classesDef = classesRepo.findOne(classId);
		coverType.setClassesDef(classesDef);
		classService.createCoverType(coverType);
	}

	@RequestMapping(value = { "deleteCovType/{covId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCoverType(@PathVariable Long covId) {
		classService.deleteCoverType(covId);
	}

	@RequestMapping(value = { "createSubCoverType" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createSubCoverType(SubclassCoverTypes coverType) throws IllegalAccessException, IOException, BadRequestException {
		classService.createSubClassCoverType(coverType);
	}

	@RequestMapping(value = { "deleteSubCovType/{covId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSubCoverType(@PathVariable Long covId) {
		classService.deleteSubCoverType(covId);
	}

	@RequestMapping(value = { "subSections/{subId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<SubclassSections> getSubSections(@DataTable DataTablesRequest pageable,@PathVariable Long subId)
			throws IllegalAccessException {
		return classService.findSubclassSections(pageable,subId);
	}

	@RequestMapping(value={"sclsections"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<SectionsDef> selectSections(@RequestParam(value="term", required=false) String term, @RequestParam("subId") Long subId, Pageable pageable)
			throws IllegalAccessException, BadRequestException
	{
		return classService.findSectionsForSel(term, pageable, subId);
	}

	@RequestMapping(value = { "createSection" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createSection(SectionsDef section) throws IllegalAccessException, IOException, BadRequestException {
		classService.createSection(section);
	}

	@RequestMapping(value = { "createSubclassSection" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createSubclassSection(SubclassSections section) throws IllegalAccessException, IOException, BadRequestException {
		classService.createSubclassSection(section);
	}

	@RequestMapping(value = { "deleteSection/{sectId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSection(@PathVariable Long sectId) {
		classService.deleteSection(sectId);
	}

	@RequestMapping(value = { "deleteSubSection/{sectId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSubSection(@PathVariable Long sectId) {
		classService.deleteSubSection(sectId);
	}
	@RequestMapping(value = { "deleteSubSectionsBatch" }, method = {org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<?> deleteSubSection(@RequestBody  List<Long> sectIds) {
		for (Long sectId : sectIds) {
			classService.deleteSubSection(sectId);
		}
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = { "covtypeSections/{scId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<SubCoverTypeSections> covtypeSections(@DataTable DataTablesRequest pageable,@PathVariable Long scId)
			throws IllegalAccessException {
		return classService.findSubCoverTypesSections(pageable,scId);
	}

	@RequestMapping(value = { "subclasscovtSections" }, method = { RequestMethod.GET })
	@ResponseBody
	public List<SubclassSections> getSubCovSections(@RequestParam(value = "secId", required = false) Long secId,@RequestParam(value = "subId", required = false) Long subId )
			throws IllegalAccessException {
		return classService.findUnassignedSections(secId,subId);
	}

	@RequestMapping(value = { "createCoverTypeSections" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<String> createCoverTypeSections(@RequestBody CoverSectionBean section) throws IllegalAccessException, IOException, BadRequestException {
		classService.createCoverSections(section);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = { "createCoverTypeSection" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void createCoverTypeSection(SubCoverTypeSections section) throws IllegalAccessException, IOException, BadRequestException {
		classService.createCoverSection(section);
	}

	@RequestMapping(value = { "deleteCoverSection/{covId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCoverSection(@PathVariable Long covId) {
		classService.deleteCoverSection(covId);
	}

	@RequestMapping(value = { "deleteCoverSectionsBatch" }, method = {org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<?> deleteCoverSection(@RequestBody  List<Long> covIds) {
		for (Long covId : covIds) {
			classService.deleteCoverSection(covId);
		}
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}
}
