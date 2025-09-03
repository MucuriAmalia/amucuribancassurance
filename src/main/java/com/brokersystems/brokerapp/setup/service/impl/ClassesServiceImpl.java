package com.brokersystems.brokerapp.setup.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.setup.service.ClassesService;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

@Service
public  class ClassesServiceImpl implements ClassesService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
	private ClassesRepo classRepo;
	
	@Autowired
	private SubClassRepo subclassRepo;
	
	@Autowired
	private SubClassCoverRepo subCoverRepo;
	
	@Autowired
	private CoverTypesRepo coverRepo;
	
	@Autowired
	private SectionRepo sectionRepo;
	
	@Autowired
	private SubSectionRepo subSectionRepo;
	
	@Autowired
	private SubCoverSectRepo subcoverSecRepo;
	
	@Autowired
	private ProductGroupRepo groupRepo;
	
	@Autowired
	private ProductsRepo prodRepo;

	@Autowired
	private ParamService paramService;
	
	@Autowired
	private ProdSubclassRepo prodSubRepo;
	
	@Autowired
	private ClausesRepo clauseRepo;
	
	@Autowired
	private SubClausesRepo subclauseRepo;
    @Autowired
    private ProductCodesRepo productCodesRepo;


	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<ClassesDef> findAllClasses(DataTablesRequest request) throws IllegalAccessException {
		Page<ClassesDef> page = classRepo.findAll(request.searchPredicate(QClassesDef.classesDef), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = false)
	public void createClass(ClassesDef classDef) {
		classRepo.save(classDef);
	}
	
	@Override
	@Modifying
	@Transactional(readOnly = false)
	public void deleteClass(Long classCode) {
		classRepo.delete(classCode);

	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<SubClassDef> findAllSubclass(DataTablesRequest request, Long classId)
			throws IllegalAccessException {
		BooleanExpression pred = QSubClassDef.subClassDef.classDef.clId.eq(classId);
		Page<SubClassDef> page = subclassRepo.findAll(pred.and(request.searchPredicate(QSubClassDef.subClassDef)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = false)
	public void createSubClass(SubClassDef subclassDef) {
		subclassRepo.save(subclassDef);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ClassesDef> findClassesForSelect(String term, Pageable pageable) {
		term = "%" + StringUtils.defaultString(term) + "%";
	    return this.classRepo.findByClDescLikeIgnoreCase(term, pageable);
	}

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public void deleteSubclass(long subId) {
        try {
            System.out.println("Starting deletion for subclass ID: " + subId);

            entityManager.createNativeQuery("SET session_replication_role = replica").executeUpdate();

            int deletedRows = entityManager.createNativeQuery(
                    "DELETE FROM sys_brk_subclasses WHERE sub_id = ?"
            ).setParameter(1, subId).executeUpdate();

            entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();

            if (deletedRows > 0) {
                System.out.println("Deleted subclass " + subId + " (foreign keys were temporarily disabled)");
            } else {
                System.out.println("No subclass found with ID: " + subId);
            }

        } catch (Exception e) {
            try {
                entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();
            } catch (Exception ex) {
                System.err.println("Failed to re-enable foreign key checks: " + ex.getMessage());
            }

            System.err.println("DELETION FAILED for subclass " + subId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting subclass: " + e.getMessage());
        }
    }

	@Override
	@Transactional(readOnly = true)
	public Page<CoverTypesDef> findCoverTypesForSel(String term, Pageable pageable, Long subId, Long classId) {
			return coverRepo.getUnassignedCoverTypes(subId,classId,term, pageable);
	}

	@Override
	@Transactional(readOnly = false)
	public void createCoverType(CoverTypesDef coverType) {
		coverType.setCovShtDesc(StringUtils.trim(coverType.getCovShtDesc()));
		coverRepo.save(coverType);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCoverType(long coverId) {
		coverRepo.delete(coverId);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<SubclassCoverTypes> findSubclassCoverTypes(DataTablesRequest request, Long subId)
			throws IllegalAccessException {

		BooleanExpression pred = QSubclassCoverTypes.subclassCoverTypes.subclass.subId.eq(subId);
		BooleanExpression searchPred = null;
		if (request.getSearch() != null && request.getSearch().getValue() != null && !request.getSearch().getValue().trim().isEmpty()) {
			String searchValue = request.getSearch().getValue().trim();

			searchPred = QSubclassCoverTypes.subclassCoverTypes.coverTypes.covShtDesc.containsIgnoreCase(searchValue)
					.or(QSubclassCoverTypes.subclassCoverTypes.coverTypes.covName.containsIgnoreCase(searchValue))
					.or(QSubclassCoverTypes.subclassCoverTypes.minPrem.stringValue().containsIgnoreCase(searchValue));
		}

		BooleanExpression finalPred = pred;
		if (searchPred != null) {
			finalPred = pred.and(searchPred);
		}

		Page<SubclassCoverTypes> page = subCoverRepo.findAll(finalPred, request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = false)
	public void createSubClassCoverType(SubclassCoverTypes subclassCover) throws BadRequestException {
		if(subCoverRepo.countDefault(subclassCover.getSubclass().getSubId(),subclassCover.getCoverTypes().getCovId()) > 0){
			throw new BadRequestException("Another Default Cover Types is defined for this sub class...");
		}
		subCoverRepo.save(subclassCover);
		
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSubCoverType(long subCoverId) {
		subCoverRepo.delete(subCoverId);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<SubclassSections> findSubclassSections(DataTablesRequest request, Long subId)
			throws IllegalAccessException {

		BooleanExpression pred = QSubclassSections.subclassSections.subclass.subId.eq(subId);

		BooleanExpression searchPred = null;
		if (request.getSearch() != null && request.getSearch().getValue() != null && !request.getSearch().getValue().trim().isEmpty()) {
			String searchValue = request.getSearch().getValue().trim();

			searchPred = QSubclassSections.subclassSections.section.shtDesc.containsIgnoreCase(searchValue)
					.or(QSubclassSections.subclassSections.section.desc.containsIgnoreCase(searchValue))
					.or(QSubclassSections.subclassSections.section.type.stringValue().containsIgnoreCase(searchValue)); // Convert enum to string

			if ("yes".equalsIgnoreCase(searchValue)) {
				searchPred = searchPred.or(QSubclassSections.subclassSections.active.isTrue())
						.or(QSubclassSections.subclassSections.computeCommission.eq("Y"));
			} else if ("no".equalsIgnoreCase(searchValue)) {
				searchPred = searchPred.or(QSubclassSections.subclassSections.active.isFalse())
						.or(QSubclassSections.subclassSections.computeCommission.eq("N")
								.or(QSubclassSections.subclassSections.computeCommission.isNull()));
			}
		}

		BooleanExpression finalPred = pred;
		if (searchPred != null) {
			finalPred = pred.and(searchPred);
		}

		Page<SubclassSections> page = subSectionRepo.findAll(finalPred, request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<SectionsDef> findSectionsForSel(String term, Pageable pageable, Long subId) {
		// Convert the term to lowercase and add wildcards
		if (term == null || term.trim().isEmpty()) {
			term = "%";  // Match everything if the term is empty
		} else {
			term = "%" + term.toLowerCase() + "%";
		}

		// Call the repository method with the formatted term
		return sectionRepo.getUnassignedSections(subId, term, pageable);
	}


	@Override
	@Transactional(readOnly = false)
	public void createSection(SectionsDef section) {

		section.setShtDesc(StringUtils.trim(section.getShtDesc()));
		sectionRepo.save(section);
	}

	@Override
	@Transactional(readOnly = false)
	public void createSubclassSection(SubclassSections section) {
		if(section.getRefundable()!=null && "on".equalsIgnoreCase(section.getRefundable())){
			section.setRefundable("Y");
		}
		else section.setRefundable("N");
		if(section.getComputeCommission()!=null && "on".equalsIgnoreCase(section.getComputeCommission())){
			section.setComputeCommission("Y");
		}
		else section.setComputeCommission("N");
		subSectionRepo.save(section);
		
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSection(Long id) {
		sectionRepo.delete(id);
		
	}

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public void deleteSubSection(Long id) {
        try {
            System.out.println("Starting deletion for sub-section ID: " + id);

            entityManager.createNativeQuery("SET session_replication_role = replica").executeUpdate();

            int deletedRows = entityManager.createNativeQuery(
                    "DELETE FROM sys_brk_sub_sections WHERE ss_id = ?"
            ).setParameter(1, id).executeUpdate();

            entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();

            if (deletedRows > 0) {
                System.out.println("Deleted sub-section " + id + " and all related records!");
            } else {
                System.out.println(" No sub-section found with ID: " + id);
            }

        } catch (Exception e) {

            try {
                entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();
            } catch (Exception ex) {
                System.err.println("Failed to re-enable foreign key checks: " + ex.getMessage());
            }

            System.err.println("DELETION FAILED for sub-section " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting sub-section: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SubCoverTypeSections> findSubCoverTypesSections(DataTablesRequest request, Long scsCode)
            throws IllegalAccessException {

        BooleanExpression pred = QSubCoverTypeSections.subCoverTypeSections.subcoverType.scId.eq(scsCode);

        BooleanExpression searchPred = null;
        if (request.getSearch() != null && request.getSearch().getValue() != null && !request.getSearch().getValue().trim().isEmpty()) {
            String searchValue = request.getSearch().getValue().trim();

            searchPred = QSubCoverTypeSections.subCoverTypeSections.subSections.section.shtDesc.containsIgnoreCase(searchValue)
                    .or(QSubCoverTypeSections.subCoverTypeSections.subSections.section.desc.containsIgnoreCase(searchValue));


            if ("yes".equalsIgnoreCase(searchValue)) {
                searchPred = searchPred.or(QSubCoverTypeSections.subCoverTypeSections.mandatory.isTrue());
            } else if ("no".equalsIgnoreCase(searchValue)) {
                searchPred = searchPred.or(QSubCoverTypeSections.subCoverTypeSections.mandatory.isFalse());
            }
        }


        BooleanExpression finalPred = pred;
        if (searchPred != null) {
            finalPred = pred.and(searchPred);
        }


        Page<SubCoverTypeSections> page = subcoverSecRepo.findAll(finalPred, request);
        return new DataTablesResult<>(request, page);
    }

	@Override
	@Transactional(readOnly = true)
	public List<SubclassSections> findUnassignedSections(Long scsCode,Long subId)  throws IllegalAccessException{
		return subcoverSecRepo.getUnassignedSections(scsCode,subId,"");
	}

	@Override
	@Transactional(readOnly = false)
	public void createCoverSections(CoverSectionBean section) {
		List<SubCoverTypeSections> sections = new ArrayList<>();
		for(Long sect:section.getSections()){
			SubCoverTypeSections coverSection = new SubCoverTypeSections();
			coverSection.setSubcoverType(subCoverRepo.findOne(section.getCoverCode()));
			coverSection.setSubSections(subSectionRepo.findOne(sect));
			sections.add(coverSection);
		}
		subcoverSecRepo.save(sections);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void createCoverSection(SubCoverTypeSections section){
		if("on".equalsIgnoreCase(section.getIntegration())){
			section.setIntegration("Y");
		}
		else section.setIntegration("N");
		if("on".equalsIgnoreCase(section.getSupportsEarnings())){
			section.setSupportsEarnings("Y");
		}
		else section.setSupportsEarnings("N");
		subcoverSecRepo.save(section);
	}
	
	
	@Override
	@Transactional(readOnly = false)
	public void deleteCoverSection(Long id){
		subcoverSecRepo.delete(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductGroupDef> findProductGroupforSel(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QProductGroupDef.productGroupDef.isNotNull();
		} else {
			pred = QProductGroupDef.productGroupDef.prgDesc.containsIgnoreCase(paramString);
		}
		return groupRepo.findAll(pred, paramPageable);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ProductCodes> findSapCodesforSel(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QProductCodes.productCodes.isNotNull();
		} else {
			pred = QProductCodes.productCodes.productDescription.containsIgnoreCase(paramString);
		}
		return productCodesRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = false)
	public void createProductGroup(ProductGroupDef group) {
		groupRepo.save(group);
		
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<ProductsDef> findAllProducts(DataTablesRequest request, Long prgCode)
			throws IllegalAccessException {
		BooleanExpression pred = QProductsDef.productsDef.proGroup.prgCode.eq(prgCode);
		Page<ProductsDef> page = prodRepo.findAll(pred.and(request.searchPredicate(QProductsDef.productsDef)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = false)
	public void createProduct(ProductsDef product) throws BadRequestException {
//		if(product.getFile().isEmpty())
//			throw new BadRequestException("Policy Document File is Empty...");
//		String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
		if(product.getAgeApplicable()!=null && "on".equalsIgnoreCase(product.getAgeApplicable())){
			product.setAgeApplicable("Y");
		}
		else product.setAgeApplicable("N");
		if(product.getWibaProduct()!=null && "on".equalsIgnoreCase(product.getWibaProduct())){
			product.setWibaProduct("Y");
		}
		else product.setWibaProduct("N");
		prodRepo.save(product);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<ProductSubclasses> findProdSubClass(DataTablesRequest request, Long prodCode)
			throws IllegalAccessException {
		BooleanExpression pred = QProductSubclasses.productSubclasses.product.proCode.eq(prodCode);
		Page<ProductSubclasses> page = prodSubRepo.findAll(pred.and(request.searchPredicate(QProductSubclasses.productSubclasses)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductsDef> findProductsSel(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QProductsDef.productsDef.isNotNull();
		} else {
			pred = QProductsDef.productsDef.proDesc.containsIgnoreCase(paramString).or(QProductsDef.productsDef.proShtDesc.containsIgnoreCase(paramString));
		}
		return prodRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteProductGroup(Long groupId) {
		groupRepo.delete(groupId);
	}

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public void deleteProduct(Long prodId) {
        try {
            System.out.println("Starting  deletion for product ID: " + prodId);
            entityManager.createNativeQuery("SET session_replication_role = replica").executeUpdate();

            int deletedRows = entityManager.createNativeQuery(
                    "DELETE FROM sys_brk_products WHERE pr_code = ?"
            ).setParameter(1, prodId).executeUpdate();

            entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();

            if (deletedRows > 0) {
                System.out.println(" SUCCESS: Deleted product " + prodId + " and all related records!");
            } else {
                System.out.println("No product found with ID: " + prodId);
            }

        } catch (Exception e) {
            try {
                entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();
            } catch (Exception ex) {
                System.err.println("Failed to re-enable foreign key checks: " + ex.getMessage());
            }

            System.err.println("DELETION FAILED for product " + prodId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting product: " + e.getMessage());
        }
    }

	@Override
	@Transactional(readOnly = true)
	public List<SubClassDef> findUnassignedSubclasses(Long prodCode,String subName) throws IllegalAccessException {
		return prodSubRepo.getUnassignedSubclasses(prodCode,subName);
	}

	@Override
	@Transactional(readOnly = false)
	public void createProdSubclasses(ProductSubcBean prodSubclass) {
		List<ProductSubclasses> prodSubclasses = new ArrayList<>();
		for(Long subCode:prodSubclass.getSubclasses()){
			ProductSubclasses subclass = new ProductSubclasses();
			subclass.setActive(true);
			subclass.setSubclass(subclassRepo.findOne(subCode));
			subclass.setProduct(prodRepo.findOne(prodSubclass.getProCode()));
			prodSubclasses.add(subclass);
		}
		prodSubRepo.save(prodSubclasses);
	}

	@Override
	@Transactional(readOnly = false)
	public void createProductClass(ProductSubclasses prodSubclass) {
		prodSubRepo.save(prodSubclass);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteProdSubclass(Long subId) {
		prodSubRepo.delete(subId);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<ClausesDef> findAllClauses(DataTablesRequest request,String type) throws IllegalAccessException {
		BooleanExpression pred = QClausesDef.clausesDef.clauseType.eq(type);
		Page<ClausesDef> page = clauseRepo.findAll(pred.and(request.searchPredicate(QClausesDef.clausesDef)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void createClauses(ClausesDef clause) throws BadRequestException {
		if(clause.getClauShtDesc()==null || StringUtils.isBlank(clause.getClauShtDesc())){
			throw new BadRequestException("Clause ID is Mandatory");
		}

		if(clause.getClauseType()==null){
			throw new BadRequestException("Specify the Type...");
		}

		String type = "";
		if("C".equalsIgnoreCase(clause.getClauseType())) type="Clause";
		else if("L".equalsIgnoreCase(clause.getClauseType())) type="Limit";
		else if("E".equalsIgnoreCase(clause.getClauseType())) type="Excess";
		else if("X".equalsIgnoreCase(clause.getClauseType())) type="Exclusions";
		else if("W".equalsIgnoreCase(clause.getClauseType())) type="Warranty";
		Long count = clauseRepo.count(QClausesDef.clausesDef.clauShtDesc.equalsIgnoreCase(StringUtils.trim(clause.getClauShtDesc()))
		                              .and(QClausesDef.clausesDef.clauseType.eq(clause.getClauseType())));
		if(clause.getClauId()==null){
			if(count > 0) throw  new BadRequestException(type+" with ID Exists...");
		}
		else if(count > 1) throw  new BadRequestException(type+" with ID Exists...");
		clauseRepo.save(clause);
	}

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public void deleteClause(Long clauseId) {
        try {
            System.out.println("Starting deletion for clause ID: " + clauseId);

            entityManager.createNativeQuery("SET session_replication_role = replica").executeUpdate();

            int deletedRows = entityManager.createNativeQuery(
                    "DELETE FROM sys_brk_clauses WHERE clau_id = ?"
            ).setParameter(1, clauseId).executeUpdate();

            entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();

            if (deletedRows > 0) {
                System.out.println(" Deleted clause " + clauseId + " and all related records!");
            } else {
                System.out.println("No clause found with ID: " + clauseId);
            }

        } catch (Exception e) {
            // Hii ina re-enable foreign key checks
            try {
                entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();
            } catch (Exception ex) {
                System.err.println("Failed to re-enable foreign key checks: " + ex.getMessage());
            }

            System.err.println("DELETION FAILED for clause " + clauseId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting clause: " + e.getMessage());
        }
    }

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<SubclassClauses> findSubClassClauses(DataTablesRequest request, Long subCode , String subName)
			throws IllegalAccessException {
		BooleanExpression pred = QSubclassClauses.subclassClauses.subclass.subId.eq(subCode);
		System.out.println("SEARCH = "+subName);
		if(subName!=null &&  !StringUtils.isBlank(subName)) {
			subName = StringUtils.lowerCase("%"+subName+"%");
			 pred = QSubclassClauses.subclassClauses.subclass.subId.eq(subCode).and((QSubclassClauses.subclassClauses.clause.clauHeading.toLowerCase().like(subName)).or(QSubclassClauses.subclassClauses.clause.clauWording.toLowerCase().like(subName)).or(QSubclassClauses.subclassClauses.clause.clauShtDesc.toLowerCase().like(subName)));
		}
		Page<SubclassClauses> page = subclauseRepo.findAll(pred.and(request.searchPredicate(QSubclassClauses.subclassClauses)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<SubClassDef> findSubclassSelect(String term, Pageable pageable) {
		Predicate pred = null;
		if (term == null || StringUtils.isBlank(term)) {
			pred = QSubClassDef.subClassDef.isNotNull();
		} else {
			pred = QSubClassDef.subClassDef.subDesc.containsIgnoreCase(term);
		}
		return subclassRepo.findAll(pred, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClausesDef> findUnassignedClauses(Long subCode, String subName) throws IllegalAccessException {
		if(subName!=null &&  !StringUtils.isBlank(subName)) {
			subName = StringUtils.lowerCase(subName);
		}
		List<ClausesDef> clausesDefList = subclauseRepo.getUnassignedClauses(subCode, subName);
		clausesDefList.forEach(a -> {
			System.out.println(a.getClauHeading());
		});
		return clausesDefList;
	}

	@Override
	@Transactional(readOnly = false)
	public void createSubClauses(SubclassClauseBean subclassClause) {
		List<SubclassClauses> subclassClauses = new ArrayList<>();
		for(Long claCode:subclassClause.getClauses()){
			SubclassClauses clause = new SubclassClauses();
			clause.setSubclass(subclassRepo.findOne(subclassClause.getSubCode()));
			clause.setClause(clauseRepo.findOne(claCode));
			subclassClauses.add(clause);
		}
		subclauseRepo.save(subclassClauses);
		
	}

	@Override
	@Transactional(readOnly = false)
	public void createSubClause(SubclassClauses subclassClause) {
		subclauseRepo.save(subclassClause);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSubClause(Long clauseId) {
		subclauseRepo.delete(clauseId);
		
	}


	@Override
	public Page<ProductSubclasses> findProductSubclasses(String term, Pageable pageable) {
		Predicate pred = null;
		if (term == null || StringUtils.isBlank(term)) {
			pred = QProductSubclasses.productSubclasses.isNotNull();
		} else {
			pred = QProductSubclasses.productSubclasses.product.proDesc.containsIgnoreCase(term)
					.or(QProductSubclasses.productSubclasses.subclass.subDesc.containsIgnoreCase(term));
		}
		return prodSubRepo.findAll(pred, pageable);
	}

	@Override
	public DataTablesResult<ProductsDef> findAllProduct(DataTablesRequest request) throws IllegalAccessException {
		Page<ProductsDef> page=prodRepo.findAll(request.searchPredicate(QProductsDef.productsDef),request);
		return new DataTablesResult<>(request,page);
	}

	@Override
	public DataTablesResult<ProductsDef> findAllProductPrg(DataTablesRequest pageable, Long prg) {
		BooleanExpression booleanExpression=QProductsDef.productsDef.productReportGroup.rptId.eq(prg);
		Page<ProductsDef> page=prodRepo.findAll(booleanExpression,pageable);
		return new DataTablesResult<>(pageable,page);

	}

	@Override
	public void addPrgProducts(Long product, Long group) {
		ProductsDef productsDef=prodRepo.findOne(product);
		ProductGroupDef productGroupDef=groupRepo.findOne(group);
		productsDef.setProGroup(productGroupDef);
		prodRepo.save(productsDef);
	}

	@Override
	public void remPrgProducts(Long product, Long group) {
		BooleanExpression booleanExpression=QProductsDef.productsDef.proGroup.prgCode.eq(group).and(
				QProductsDef.productsDef.proCode.eq(product));
		ProductsDef productGroupDef=prodRepo.findOne(booleanExpression);
		prodRepo.delete(productGroupDef);
	}
}
