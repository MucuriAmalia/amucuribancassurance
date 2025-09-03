package com.brokersystems.brokerapp.setup.service;


import java.util.List;

import com.brokersystems.brokerapp.setup.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.transaction.annotation.Transactional;


public interface ClassesService {
	
	DataTablesResult<ClassesDef> findAllClasses(DataTablesRequest request)  throws IllegalAccessException;
	
	void createClass(ClassesDef classDef);
	
	DataTablesResult<SubClassDef> findAllSubclass(DataTablesRequest request,Long classId)  throws IllegalAccessException;
	
	void createSubClass(SubClassDef subclassDef);
	
	public Page<ClassesDef> findClassesForSelect(String term, Pageable pageable);
	
	public void deleteClass(Long classCode);
	
	public void deleteSubclass(long subId);
	
	public Page<CoverTypesDef> findCoverTypesForSel(String term, Pageable pageable, Long subId, Long classId);
	
	void createCoverType(CoverTypesDef coverType);
	
	void deleteCoverType(long coverId);
	
	void createSubClassCoverType(SubclassCoverTypes subclassCover) throws BadRequestException;
	
	void deleteSubCoverType(long subCoverId);
	
	DataTablesResult<SubclassCoverTypes> findSubclassCoverTypes(DataTablesRequest request,Long subId)  throws IllegalAccessException;
	
	DataTablesResult<SubclassSections> findSubclassSections(DataTablesRequest request,Long subId)  throws IllegalAccessException;
	
	public Page<SectionsDef> findSectionsForSel(String term, Pageable pageable,Long subId);
	
	void createSection(SectionsDef section);
	
	void createSubclassSection(SubclassSections section);
	
	void deleteSection(Long id);
	
	void deleteSubSection(Long id);
	
	DataTablesResult<SubCoverTypeSections> findSubCoverTypesSections(DataTablesRequest request,Long scsCode)  throws IllegalAccessException;
	
	List<SubclassSections> findUnassignedSections(Long scsCode,Long SubId)  throws IllegalAccessException;
	
	void createCoverSections(CoverSectionBean section);
	
	void createCoverSection(SubCoverTypeSections section);
	
	void deleteCoverSection(Long id);
	
    public Page<ProductGroupDef> findProductGroupforSel(String paramString, Pageable paramPageable);

	@Transactional(readOnly = true)
	Page<ProductCodes> findSapCodesforSel(String paramString, Pageable paramPageable);

	void createProductGroup(ProductGroupDef group);
	
	DataTablesResult<ProductsDef> findAllProducts(DataTablesRequest request,Long prgCode)  throws IllegalAccessException;
	
	void createProduct(ProductsDef product) throws BadRequestException;
	
	DataTablesResult<ProductSubclasses> findProdSubClass(DataTablesRequest request,Long prodCode)  throws IllegalAccessException;
	
	public Page<ProductsDef> findProductsSel(String paramString, Pageable paramPageable);
	
	void deleteProductGroup(Long groupId);
	
	void deleteProduct(Long prodId);
	
	List<SubClassDef> findUnassignedSubclasses(Long prodCode,String subName)  throws IllegalAccessException;
	
	void createProdSubclasses(ProductSubcBean section);
	
	void createProductClass(ProductSubclasses prodSubclass);
	
	void deleteProdSubclass(Long subId);
	
	DataTablesResult<ClausesDef> findAllClauses(DataTablesRequest request,String type)  throws IllegalAccessException;
	
	void createClauses(ClausesDef clause) throws BadRequestException;
	
	void deleteClause(Long clauseId);
	
	DataTablesResult<SubclassClauses> findSubClassClauses(DataTablesRequest request,Long subCode, String subName)  throws IllegalAccessException;
	
	public Page<SubClassDef> findSubclassSelect(String term, Pageable pageable);
	
	List<ClausesDef> findUnassignedClauses(Long subCode,String subName)  throws IllegalAccessException;
	
	public void createSubClauses(SubclassClauseBean subclassClause);
	
	public void createSubClause(SubclassClauses subclassClause);
	
	void deleteSubClause(Long clauseId);

	public Page<ProductSubclasses> findProductSubclasses(String term, Pageable pageable);

    DataTablesResult<ProductsDef> findAllProduct(DataTablesRequest pageable) throws IllegalAccessException;

    DataTablesResult<ProductsDef> findAllProductPrg(DataTablesRequest pageable, Long prg);

    void addPrgProducts(Long product, Long group);

	void remPrgProducts(Long product, Long group);
}
