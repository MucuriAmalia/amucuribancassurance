package com.brokersystems.brokerapp.setup.service.impl;


import com.brokersystems.brokerapp.claims.dtos.ServiceProviderDTO;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.AccountsDTO;
import com.brokersystems.brokerapp.setup.dto.BranchDTO;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import com.brokersystems.brokerapp.users.model.QPermissionsDef;
import com.brokersystems.brokerapp.users.repository.PermissionsRepo;
import com.google.gson.Gson;
import com.mysema.query.types.expr.BooleanExpression;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationServiceImpl implements OrganizationService
{
  @Autowired
  private OrganizationRepository orgRepo;
  @Autowired
  private CountryRepository countryRepo;
  @Autowired
  private CountyRepository countyRepo;
  @Autowired
  private TownRepository townRepo;
  @Autowired
  private CurrencyRepository currencyrepo;
  @Autowired
  private OrgBranchRepository orgBranchrepo;
  @Autowired
  private RegionRepository regionRepo;
  
  @Autowired
  private UserRepository userRepo;

  @Autowired
  private PermissionsRepo permissionsRepo;


  @Override
  public OrganizationDTO getOrganizationLogoDetails() {
    List<Object[]> orgDetails = this.orgRepo.getOganizationLog();
    if (!orgDetails.isEmpty()) {
      Object[] detail = orgDetails.get(0);
      OrganizationDTO organization = new OrganizationDTO();
      organization.setOrgLogo((String) detail[0]);
      organization.setOrgLogoContentType((String) detail[1]);
      return organization;
    }
    return new OrganizationDTO();
  }

  @Transactional(readOnly=true)
  @Cacheable(value = "organization")
  public OrganizationDTO getOrganizationDetails()
  {
    List<Object[]> orgDetails = this.orgRepo.findAllOrganizations();
    if (!orgDetails.isEmpty()) {
      Object[] detail = orgDetails.get(0);
      OrganizationDTO organization = new OrganizationDTO();
      organization.setOrgCode(((BigInteger)detail[0]).longValue());
      organization.setAddAddress((String) detail[1]);
      organization.setEmailAddress((String) detail[2]);
      organization.setPhyAddress((String) detail[3]);
      organization.setCertNumber((String) detail[4]);
      organization.setDateIncorp((Date) detail[5]);
      organization.setOrgDesc((String) detail[6]);
      organization.setOrgFax((String) detail[7]);
      organization.setOrgMobile((String) detail[8]);
      organization.setOrgName((String) detail[9]);
      organization.setOrgPhone((String) detail[10]);
      organization.setOrgShtDesc((String) detail[11]);
      organization.setOrgWebsite((String) detail[12]);
      organization.setCouCode(((BigInteger)detail[13]).longValue());
      organization.setCurCode(((BigInteger)detail[14]).longValue());
      organization.setCurName((String) detail[15]);
      organization.setCouName((String) detail[16]);
      organization.setOrgLogo((String) detail[17]);
      organization.setOrgLogoContentType((String) detail[18]);
      organization.setPinNo((String) detail[19]);
      return organization;
    }
    return new OrganizationDTO();
  }

  @CacheEvict(value = "organization")
  public void createOrganization(OrganizationDTO org) throws BadRequestException {
    final Organization organization = new Organization();
    if(org.getCouCode()==null){
      throw new BadRequestException("Organization Country is mandatory...");
    }
    final Country country = countryRepo.findOne(org.getCouCode());
    if(country==null){
      throw new BadRequestException("Select a valid Country...");

    }
    if(org.getCurCode()==null){
      throw new BadRequestException("Organization Default Currency is mandatory...");
    }
    final Currencies currencies = currencyrepo.findOne(org.getCurCode());
    if(currencies==null){
      throw new BadRequestException("Select a valid Organization Default Currency...");
    }
    try {
      BeanUtils.copyProperties(organization,org);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new BadRequestException(e.getMessage());
    }
    Address address = new Address();
    address.setAddAddress(org.getAddAddress());
    address.setEmailAddress(org.getEmailAddress());
    address.setPhyAddress(org.getPhyAddress());
    organization.setCountry(country);
    organization.setCurrency(currencies);
    organization.setAddress(address);
    organization.setPinNo(org.getPinNo());
    organization.setCertNumber(org.getCertNumber());
    this.orgRepo.save(organization);
  }
  
  @Transactional(readOnly=true)
  public DataTablesResult<Country> findCountryDatatables(DataTablesRequest request)
    throws IllegalAccessException
  {
    Page<Country> page = this.countryRepo.findAll(request.searchPredicate(QCountry.country), request);
    return new DataTablesResult(request, page);
  }
  
  @Transactional(readOnly=true)
  public DataTablesResult<County> findCountiesByCountry(long countryCode, DataTablesRequest request)
    throws IllegalAccessException
  {
    QCountry country = QCounty.county.country;
    BooleanExpression pred = country.couCode.eq(Long.valueOf(countryCode));
    Page<County> page = this.countyRepo.findAll(pred.and(request.searchPredicate(QCounty.county)), request);
    return new DataTablesResult(request, page);
  }
  
  @Transactional(readOnly=true)
  public DataTablesResult<Town> findTownsByCounty(long countyCode, DataTablesRequest request)
    throws IllegalAccessException
  {
    QCounty county = QTown.town.county;
    BooleanExpression pred = county.countyId.eq(Long.valueOf(countyCode));
    Page<Town> page = this.townRepo.findAll(pred.and(request.searchPredicate(QTown.town)), request);
    return new DataTablesResult(request, page);
  }
  
  @Transactional(readOnly=true)
  public DataTablesResult<Currencies> findCurrencies(DataTablesRequest request)
    throws IllegalAccessException
  {
    Page<Currencies> page = this.currencyrepo.findAll(request.searchPredicate(QCurrencies.currencies), request);
    return new DataTablesResult(request, page);
  }
  
  @Transactional(readOnly=true)
  public DataTablesResult<OrgRegions> findOrgRegions(long orgCode, DataTablesRequest request)
    throws IllegalAccessException
  {
	QOrganization org  = QOrgRegions.orgRegions.organization;
    BooleanExpression pred =org.orgCode.eq(Long.valueOf(orgCode));
    Page<OrgRegions> page = this.regionRepo.findAll(pred.and(request.searchPredicate(QOrgRegions.orgRegions)), request);
    return new DataTablesResult(request, page);
  }


  @Transactional(readOnly=true)
  public DataTablesResult<BranchDTO> findOrgBranches(long regCode, DataTablesRequest request)
  {
    final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%";
    final List<Object[]> branches = orgBranchrepo.findRegionBranches(regCode,search,request.getPageNumber(), request.getPageSize());
    long rowCount = 0L;
    final List<BranchDTO> branchDTOList = new ArrayList<>();
    if(!branches.isEmpty()) rowCount = ((BigInteger)branches.get(0)[8]).intValue();
    for(Object[] branch:branches){
      BranchDTO branchDTO = new BranchDTO();
      branchDTO.setObId(((BigInteger)branch[0]).longValue());
      branchDTO.setObShtDesc((String) branch[1]);
      branchDTO.setObName((String) branch[2]);
      branchDTO.setAddress((String) branch[3]);
      branchDTO.setTelNumber((String) branch[4]);
      branchDTO.setUsername((String) branch[5]);
      branchDTO.setHeadoffice((String) branch[6]);
      if(branch[7]!=null) {
        branchDTO.setUserCode(((BigInteger) branch[7]).longValue());
      }
      branchDTOList.add(branchDTO);
    }
    Page<BranchDTO>  page = new PageImpl<>(branchDTOList,request, rowCount);
    return new DataTablesResult<>(request, page);
  }
  

  @Modifying
  @Transactional(readOnly=false)
  public void createRegionBranch(BranchDTO branchDTO) throws BadRequestException {
     OrgBranch branch = new OrgBranch();
    System.out.println(new Gson().toJson(branchDTO));
    System.out.println(branchDTO.getHeadoffice());
     final String isHeadOffice = ("on".equalsIgnoreCase(branchDTO.getHeadoffice()))?"Y":"N";
    if(branchDTO.getObId()!=null){
       branch = orgBranchrepo.findOne(branchDTO.getObId());
       if(branch==null){
         throw new BadRequestException("Error Updating Branch. Please contact Administrator...");
       }
       if(isHeadOffice.equalsIgnoreCase("Y")){
          if(orgBranchrepo.countHeadOffice(branchDTO.getObId()) > 0){
            throw new BadRequestException("Another Branch has already been marked as head office to the organization...");
          }
       }
    }
    else{
      if(isHeadOffice.equalsIgnoreCase("Y")){
        if(orgBranchrepo.countHeadOffice(-2000L) > 0){
          throw new BadRequestException("Another Branch has already been marked as head office to the organization...");
        }
      }
    }
    if(branchDTO.getRegCode()==null){
      throw new BadRequestException("Please Select A Region to Save A Branch...");
    }
    final OrgRegions regions = regionRepo.findOne(branchDTO.getRegCode());
    if(regions==null){
      throw new BadRequestException("Please Select A Region to Save A Branch...");
    }
    if(branchDTO.getUserCode()!=null){
      final User user = userRepo.findOne(branchDTO.getUserCode());
      if(user==null || user.getEnabled()==null || "0".equalsIgnoreCase(user.getEnabled())){
        throw new BadRequestException("Invalid user attached to the branch...");
      }
      branch.setBranchManager(user);
    }
    branch.setRegion(regions);
    branch.setAddress(branchDTO.getAddress());
    branch.setHeadOffice(branchDTO.getHeadoffice());
    branch.setTelNumber(branchDTO.getTelNumber());
    branch.setObName(branchDTO.getObName());
    branch.setHeadOffice(isHeadOffice);
    branch.setObShtDesc(branchDTO.getObShtDesc());
    branch.setObId(branchDTO.getObId());
    this.orgBranchrepo.save(branch);
  }
  
  @Modifying
  @Transactional(readOnly=false)
  public void deleteOrgBranch(Long branchCode)
  {
    this.orgBranchrepo.delete(branchCode);
  }

  @Transactional(readOnly=true)
  public Page<Country> findCountryForSelect(String term, Pageable pageable)
  {
    term = "%" + StringUtils.defaultString(term) + "%";
    return this.countryRepo.findByCouNameLikeIgnoreCase(term, pageable);
  }

  @Transactional(readOnly=true)
  public Page<County> findCountyForSelect(String term, Pageable pageable, long couId)
  {
    term = "%" + StringUtils.defaultString(term) + "%";
    Country country = (Country)this.countryRepo.findOne(Long.valueOf(couId));
    return this.countyRepo.findByCountyNameLikeIgnoreCaseAndCountry(term, pageable, country);
  }
  @Transactional(readOnly=true)
  public Page<Town> findTownForSelect(String term, Pageable pageable, long countyId)
  {
    term = "%" + StringUtils.defaultString(term) + "%";
    County county = (County)this.countyRepo.findOne(Long.valueOf(countyId));
    return this.townRepo.findByCtNameLikeIgnoreCaseAndCounty(term, pageable, county);
  }
  @Transactional(readOnly=true)
  public Page<Currencies> findCurrenciesForSelect(String term, Pageable pageable)
  {
    term = "%" + StringUtils.defaultString(term) + "%";
    return this.currencyrepo.findByCurNameLikeIgnoreCaseAndEnabled(term, pageable,true);
  }

  @Modifying
  @Transactional(readOnly=false)
	public void createOrgRegion(OrgRegions region) {
	     if(region.getRegWef().after(new Date())){
	    	 throw new IllegalArgumentException("Wef Date cannot be greater than today");
	     }
		regionRepo.save(region);
		
	}

    @Modifying
    @Transactional(readOnly=false)
	@Override
	public void deleteOrgRegion(Long regCode) {
		 regionRepo.delete(regCode);
	}

  @Transactional(readOnly=true)
  public Page<AccountsDTO> findUsersForSelect(String term, Pageable pageable) {
    final String search = "%" + StringUtils.defaultString(term) + "%";

    List<Object[]> users = userRepo.findUsersForSelect(search.toLowerCase(),
            pageable.getPageNumber(),
            pageable.getPageSize());
    final List<AccountsDTO> userDTOList = new ArrayList<>();
    long rowCount = 0l;

    if (!users.isEmpty()) rowCount = ((BigInteger) users.get(0)[4]).longValue();

    for (Object[] user : users) {
      AccountsDTO accountsDTO = new AccountsDTO();

      // Map user fields to AccountsDTO fields
      accountsDTO.setAcctId(((BigInteger) user[0]).longValue());    // user_id -> acctId
      accountsDTO.setName((String) user[1]);                       // user_name -> name
      accountsDTO.setShtDesc((String) user[2]);                    // user_username -> shtDesc
      accountsDTO.setAbsaNo((String) user[3]);                     // user_absa_no -> absaNo
      // user[4] is total_rows

      userDTOList.add(accountsDTO);
    }

    return new PageImpl<>(userDTOList, pageable, rowCount);
  }
  @Override
  public Page<PermissionsDef> findPerm(String term, Pageable pageable) {
    Long exception= Long.valueOf(8);
    BooleanExpression pred = QPermissionsDef.permissionsDef.module.moduleId.eq(exception).and(QPermissionsDef.permissionsDef.permName.containsIgnoreCase(term));

    return permissionsRepo.findAll(pred,pageable);
  }
    

	
}
