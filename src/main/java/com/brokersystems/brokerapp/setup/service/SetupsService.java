package com.brokersystems.brokerapp.setup.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.brokersystems.brokerapp.accounts.dtos.BankBranchDTO;
import com.brokersystems.brokerapp.accounts.model.Banks;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import com.brokersystems.brokerapp.trans.model.TransactionMapping;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * This  is used to maintain crud and query services of several setups screens
 * in the system like Currency, Countries, Counties, Citys
 * @author mugenyq
 *
 */
/**
 * This  is used to maintain crud and query services of several setups screens
 * in the system like Currency, Countries, Counties, Citys
 * @author mugenyq
 *
 */
 public interface SetupsService {
	
	 DataTablesResult<SystemSequence> findAllSequences(DataTablesRequest request) throws IllegalAccessException;
	
	 void defineSequence(SystemSequence sequence) throws BadRequestException;
	
	void deleteSequence(Long seqCode);
	
	DataTablesResult<Currencies> findAllCurrencies(DataTablesRequest request)  throws IllegalAccessException;

     DataTablesResult<BusinessSourceGroups> findAllBusSrcGroups(DataTablesRequest request) throws IllegalAccessException;
     DataTablesResult<BusinessSources> findAllBusinessSources(long srcGroupId,DataTablesRequest request) throws IllegalAccessException;
     void deleteBusSrcGroup(Long busSrcGrpId );
     void deleteBusSrc(Long busSrcId );
     void defineBusSrcGroup(BusinessSourceGroups businessSourceGroups);
     void defineBusSource(BusinessSources businessSources);
	
	void defineCurrency(Currencies currency);
	
	void deleteCurrency(Long currCode);
	
	DataTablesResult<Country> findAllCountries(DataTablesRequest request)  throws IllegalAccessException;
	
	void defineCountry(Country country);
	
	void deleteCountry(Long couCode);
	
	DataTablesResult<County> findCountiesByCountry(long couCode,DataTablesRequest request)  throws IllegalAccessException;
	
    void defineCounty(County county);
	
	void deleteCounty(Long countyCode);
	
    DataTablesResult<Town> findTownsByCounty(long countyCode,DataTablesRequest request)  throws IllegalAccessException;
	
    void defineTown(Town town);
	
	void deleteTown(Long townCode);
	
	 Page<OrgBranch> findBranchForSelect(String paramString, Pageable paramPageable);
     
     DataTablesResult<PaymentModes> findAllPaymentModes(DataTablesRequest request)  throws IllegalAccessException;
 	
     void definePaymentMode(PaymentModes mode) throws BadRequestException;
 	
 	 void deletePaymentMode(Long pmId);
 	
 	 DataTablesResult<AccountTypes> findAllAccountTypes(DataTablesRequest request)  throws IllegalAccessException;

 	 DataTablesResult<SubAccountTypes> findAllSubAccountTypes(DataTablesRequest request, Long acctypeId)  throws IllegalAccessException;

 	 void defineSubAccountType(SubAccountTypes subAccountTypes) throws BadRequestException;

 	 void deleteSubAccountType(Long subAcctype);

 	void defineAccountType(AccountTypes accType) throws BadRequestException;
 	
	void deleteAccountType(Long acctId);
	
	 Page<AccountTypesDTO> findAccountTypesforSelect(String paramString, Pageable paramPageable);
    Page<AccountTypesDTO> findsubComAccountTypes(String paramString, Pageable paramPageable);
	 Page<AccountsDTO> findParentAccountsforSelect(String paramString, Pageable paramPageable);
	 Page<AccountsDTO> findReinsuranceforSelect(String paramString, Pageable paramPageable);
	 Page<AccountsDTO> findBrokersforSelect(String paramString, Pageable paramPageable);
     void approveAccount(Long acctId) throws BadRequestException;
     void rejectAccount(Long acctId, Long reasonId, String reason) throws BadRequestException;
     void deactivateAccount(Long acctId) throws BadRequestException;
	DataTablesResult<AccountsDTO> findAllAccounts(DataTablesRequest request, Long accId);

    AccountsDTO getAccountDetails(Long acctId);

    Long defineAccount(AccountsDTO account, boolean isApproved) throws BadRequestException;
    
    void deleteAccount(Long acctId);
    
    
    String defineClient(ClientDTO tenant, boolean isApproved) throws BadRequestException, ParseException;
    String updateClient(ClientDTO clientDef, boolean isApproved) throws BadRequestException, ParseException;

    ClientDTO getClientDetails(Long tenId);
    ClientDTO getClientDetailsByHash(String hash);

     DataTablesResult<MobilePrefixDef> findAllPrefixes(long couCode,DataTablesRequest request) throws IllegalAccessException;
     DataTablesResult<ContractDTO> findAllContracts(DataTablesRequest request);

    void definePrefix(MobilePrefixDef prefix) throws BadRequestException;
	
	void deletePrefix(Long prefCode);
    
	 Page<MobilePrefixDef> findSelPrefixes(String paramString, Pageable paramPageable,long coucode);
	
	DataTablesResult<ClientTypes> findAllClientTypes(DataTablesRequest request)  throws IllegalAccessException;
	
    void defineClientType(ClientTypes clientType) throws BadRequestException;
	
	void deleteClientType(Long typeCode);

    DataTablesResult<RelationshipTypes> findAllRelationshipTypes(DataTablesRequest request)  throws IllegalAccessException;

    void defineRelationshipType(RelationshipTypes relationshipTypes) throws BadRequestException;
	
	 Page<ClientTypes> findSelClientTypes(String paramString, Pageable paramPageable);

     Page<RelationshipTypes> findSelRelationTypes(String paramString, Pageable paramPageable);
	
	DataTablesResult<ClientTitle> findAllClientTitles(DataTablesRequest request)  throws IllegalAccessException;
	
	void defineClientTitle(ClientTitle clientTitle) throws BadRequestException;
	
	void deleteClientTitle(Long titleCode);
	
	 Page<ClientTitle> findSelClientTitles(String paramString, Pageable paramPageable);
	
	 DataTablesResult<PostalCodesDef> findPostalCodesByTown(long townCode,DataTablesRequest request)  throws IllegalAccessException;
	 
	 void definePostalCode(PostalCodesDef postalCode) throws BadRequestException;
		
     void deletePostalCode(Long pCode);
     
     Page<SectorDef> findSectorForSelect(String paramString, Pageable paramPageable);
     
     DataTablesResult<Occupation> findOccupationBySector(long sectorCode,DataTablesRequest request)  throws IllegalAccessException;
     
     void defineSector(SectorDef sector) throws BadRequestException;
     
     void deleteSector(Long sectCode);
     
     void defineOccupation(Occupation occup) throws BadRequestException;
     
     void deleteOccupation(Long occCode);
     
     Page<Occupation> findSectorOccupations(String paramString, Pageable paramPageable, long sectCode);
    
     DataTablesResult<TaxRates> findTaxRates(Long subCode,Long prodCode,DataTablesRequest request)  throws IllegalAccessException;
     
      void defineTaxRates(TaxRates tax) throws BadRequestException;
     
      void deleteTaxRates(Long taxCode);
     
     DataTablesResult<PerilsDef> findPerils(DataTablesRequest request)  throws IllegalAccessException;
     
      void definePerils(PerilsDef peril) throws BadRequestException;
     
      void deletePerils(Long perilCode);

    DataTablesResult<CommRecv> getAllInsurers(DataTablesRequest request)  throws IllegalAccessException;

     DataTablesResult<RevenueItemsDef> findPrgRevenueItems(long sclCode,DataTablesRequest request)  throws IllegalAccessException;

     DataTablesResult<RevenueItemsDef> findPrgRevenueItems(DataTablesRequest request)  throws IllegalAccessException;

      Set<RevenueItemBean> findUnassignedRevItems(Long sclCode) throws IllegalAccessException;
 	
      void createBulkRevenueItems(RevenueItemModel revenueModel) throws BadRequestException;
     
      void updateRevenueItems(RevenueItemsDef revItem);
     
      Page<RevenueItemsDef> getTaxRevenueItems(String paramString, Pageable paramPageable,long sclCode);
     
     DataTablesResult<ShortPeriodRates> findShortPeriodRates(DataTablesRequest request)  throws IllegalAccessException;
     
      void defineShortPeriodRates(ShortPeriodRates shtPeriods) throws BadRequestException;
     
      void deleteShortPrdRates(Long periodCode);
     
     DataTablesResult<SecShortPeriodRates> findSecShortPeriodRates(long subsecCode,DataTablesRequest request)  throws IllegalAccessException;
     
      void defineSecShortPeriodRts(SecShortPeriodRates shtPeriods) throws BadRequestException;
     
      void deleteSecShortPrdRts(Long periodCode);
     
     Page<SubclassSections> findAllSubClassSections(String paramString, Pageable paramPageable);
     
      void defineEndorseRemarks(EndorsementRemarks remarks) throws BadRequestException;
     
      void deleteEndorseRemarks(Long remarkCode);
     
     DataTablesResult<EndorsementRemarks> findAllEndorseRemarks(DataTablesRequest request)  throws IllegalAccessException;

     void defineSubClassPerils(SubclassPerils subclassPeril);

     void deleteSubPerils(Long subPerilId);

    DataTablesResult<SubclassPerils> findSubclassPerils(Long subCode,DataTablesRequest request)  throws IllegalAccessException;

    Page<PerilsDef> findSelectPerils(String paramString, Pageable paramPageable, long subCode);

    DataTablesResult<ClmCausations> findAllActivities(DataTablesRequest request)  throws IllegalAccessException;

     void defineActivity(ClmCausations clmStatus);

     void deleteActivity(Long activityId);

    DataTablesResult<TransactionMapping> findTransMapping(DataTablesRequest request)  throws IllegalAccessException;

    void deleteClaimActivitiesByCausationId(Long caId);

     void defineTransMapping(TransactionMapping mapping) throws BadRequestException;

     void deleteMapping(Long mappingId);

     void deleteRevenueItem(Long revenueId);

    Page<CoaSubAccountsDTO> findGLAccounts(String paramString, Pageable paramPageable);

     Date getTodayDate();

     Page<Town> findTownForSelect(String paramString, Pageable paramPageable);

    Page<PostalCodesDef> findTownPostalCOdes(String paramString, Pageable paramPageable, long townCode);

     Page<Banks> findBanksForSelect(String paramString, Pageable paramPageable);

    Page<BankBranchDTO> findBankBranchesForSel(String paramString, Pageable paramPageable, long bankCode);

    CountryDTO getDefaultCountry();

    Page<MobProviders> findMobProviders(String paramString, Pageable paramPageable);

    void defineMobProviders(MobProviders provider) throws BadRequestException;

    DataTablesResult<InterestedParties> findInterestedParties(DataTablesRequest request)  throws IllegalAccessException;

     void defineInterestedParties(InterestedPartiesDto interestedParties) throws BadRequestException;

     void deletInterestedParties(Long interestId);

    DataTablesResult<ProductChecks> findProductChecks(DataTablesRequest request, Long prodId)  throws IllegalAccessException;

    DataTablesResult<Checks> findAllChecks(DataTablesRequest request)  throws IllegalAccessException;

    void defineChecks(Checks currency) throws BadRequestException;

    void deleteChecks(Long checkCode);

    void  deleteRelationshipType(Long relationCode);


    void deleteProdChecks(Long checkCode);

    Page<PermissionsDef> findSelPermissions(String paramString, Pageable paramPageable);

    Page<ProductsDef> findSelProducts(String paramString, Pageable paramPageable);

     DataTablesResult<AccountsDocs> findAccountDocs(DataTablesRequest request, Long accountId)
            throws IllegalAccessException;

    List<RequiredDocs> findUnassignedAcctDocs(Long acctId, String docName)  throws IllegalAccessException;

    void createAcctRequiredDocs(RequiredDocBean requiredDocBean);

    Iterable<Checks> findUnassginedChecks(long prodId);

    void createProdChecks(ChecksBean checksBean);

    List<SingleQuizModel> findAllQuestions() throws IllegalAccessException;

    DataTablesResult<LifeQuestions> findQuestions(DataTablesRequest request) throws IllegalAccessException;

    void defineQuestions(LifeQuestions questions);

    DataTablesResult<LifeQuestionsChoices> findQuestionsChoices(DataTablesRequest request, Long quizId) throws IllegalAccessException;

    void defineQuestionsChoices(LifeQuestionsChoices questionsChoices);

    Page<SubClassDef> findSubclassSel(String paramString, Pageable paramPageable);

    Page<Year> findYearForSelect(String term, Pageable pageable);

    OrgBranch findThisBranchS(String branch) throws BadRequestException;

    AccountDef findThisAgentS(String user) throws BadRequestException;

    ProductReportGroup findThisProductS(String product) throws BadRequestException;

    Page<PermissionsDef> findSelPerm(String term, Long exception, Pageable pageable);

    Long getId(String exception);

    ReceiptTrans getReceipts(Long receiptCode);

    Page<ProductReportGroup> selRptGrps(String term, Pageable pageable);

    void createRptGroup(ProductReportGroup productReportGroup) throws BadRequestException;

    OrgBranch findThisBranch(String product, String name, String branch, String agent, Integer year, BigDecimal janPol, BigDecimal janProd, BigDecimal febProd, BigDecimal febPol, BigDecimal marProd, BigDecimal marPol, BigDecimal aprProd, BigDecimal aprPol, BigDecimal mayProd, BigDecimal mayPol, BigDecimal junProd, BigDecimal junPol, BigDecimal julProd, BigDecimal julPol, BigDecimal augProd, BigDecimal augPol, BigDecimal sepProd, BigDecimal sepPol, BigDecimal octProd, BigDecimal octPol, BigDecimal novProd, BigDecimal novPol, BigDecimal decProd, BigDecimal decPol) throws BadRequestException;

    AccountDef findThisAgent(String product, String name, String branch, String agent, Integer year, BigDecimal janPol, BigDecimal janProd, BigDecimal febProd, BigDecimal febPol, BigDecimal marProd, BigDecimal marPol, BigDecimal aprProd, BigDecimal aprPol, BigDecimal mayProd, BigDecimal mayPol, BigDecimal junProd, BigDecimal junPol, BigDecimal julProd, BigDecimal julPol, BigDecimal augProd, BigDecimal augPol, BigDecimal sepProd, BigDecimal sepPol, BigDecimal octProd, BigDecimal octPol, BigDecimal novProd, BigDecimal novPol, BigDecimal decProd, BigDecimal decPol) throws BadRequestException;

    ProductReportGroup findThisProduct(String product, String name, String branch, String agent, Integer year, BigDecimal janPol, BigDecimal janProd, BigDecimal febProd, BigDecimal febPol, BigDecimal marProd, BigDecimal marPol, BigDecimal aprProd, BigDecimal aprPol, BigDecimal mayProd, BigDecimal mayPol, BigDecimal junProd, BigDecimal junPol, BigDecimal julProd, BigDecimal julPol, BigDecimal augProd, BigDecimal augPol, BigDecimal sepProd, BigDecimal sepPol, BigDecimal octProd, BigDecimal octPol, BigDecimal novProd, BigDecimal novPol, BigDecimal decProd, BigDecimal decPol) throws BadRequestException;

    DataTablesResult<CurrencyExchangeRatesDTO> findCurrencyExchangeRates(DataTablesRequest request, Long curCode);

    void createCurExchangeRate(CurrencyExchangeRatesDTO exchangeRates) throws BadRequestException;
    Page<SubclassDTO> selectSubclasses(String term, Pageable pageable);
    Page<RevenueItemsDTO> selectAllRevenueItems(String term, Pageable pageable);

    Page<Segments> findSegments(String term, Pageable pageable);

    void saveSegment(Segments segment) throws BadRequestException;

    DataTablesResult<Segments> findAllSegments(DataTablesRequest request) throws IllegalAccessException;

    Segments findSegmentById(Long id)throws IllegalAccessException;

    Page<MainBankSegments> findMainBankSegments(String paramString, Pageable pageable);

    DataTablesResult<MainBankSegments> findAllMainBankSegments(DataTablesRequest request) throws IllegalAccessException;

    void saveMainBankSegment(MainBankSegments mainBankSegment) throws BadRequestException;

    MainBankSegments findMainBankSegmentById(Long id) throws IllegalAccessException;

    DataTablesResult<RejectedReasons> findAllRejectedReasons(DataTablesRequest request) throws IllegalAccessException;

    void defineRejectedReasons(RejectedReasons rejectedStatus);

    void deleteRejectedReasons(Long rejectedReasonsId);

    List<RejectedReasons> findActiveRejectionReasons();

    DataTablesResult<PublicHolidays> findAllPublicHolidays(DataTablesRequest request) throws IllegalAccessException;

    void createPublicHoliday(PublicHolidays publicHolidays);

    void deletePublicHoliday(Long holidayId);

    List<PublicHolidays> findActivePublicHolidays();

    void createUwAccountDef(CommRecvDto dto) throws  BadRequestException;

    /**
     * Check if a subclass has revenue item/GL mappings configured
     * @param subclassId The subclass ID to check
     * @return true if the subclass has proper revenue mappings, false otherwise
     */
    boolean hasSubclassRevenueMapping(Long subclassId);

    /**
     * Validate revenue mappings for multiple subclasses
     * @param subclassIds List of subclass IDs to validate
     * @return List of validation results for each subclass
     */
    List<SubclassMappingValidation> validateSubclassesMappings(List<Long> subclassIds);

    /**
     * Get subclasses with their revenue mapping status
     * @param searchTerm Search term for filtering subclasses
     * @param productCode Optional product code for filtering
     * @return List of subclasses with mapping status
     */
    List<SubclassWithMappingStatus> getSubclassesWithMappingStatus(String searchTerm, Long productCode);

    /**
     * Get detailed revenue mapping information for a subclass
     * @param subclassId The subclass ID
     * @return Detailed mapping information
     */
    SubclassRevenueDetails getSubclassRevenueDetails(Long subclassId);
}
