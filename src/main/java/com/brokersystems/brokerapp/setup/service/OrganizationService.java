package com.brokersystems.brokerapp.setup.service;


import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.AccountsDTO;
import com.brokersystems.brokerapp.setup.dto.BranchDTO;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.setup.model.*;
/**
 * Organization Service
 * Mainly used for crud service on organization setups
 * @author Peter
 *
 */
public abstract interface OrganizationService
{
   OrganizationDTO getOrganizationDetails();
   OrganizationDTO getOrganizationLogoDetails();

  void createOrganization(OrganizationDTO paramOrganization) throws BadRequestException;
  
  public abstract DataTablesResult<Country> findCountryDatatables(DataTablesRequest paramDataTablesRequest)
    throws IllegalAccessException;
  
  public abstract DataTablesResult<County> findCountiesByCountry(long paramLong, DataTablesRequest paramDataTablesRequest)
    throws IllegalAccessException;
  
  public abstract DataTablesResult<Town> findTownsByCounty(long paramLong, DataTablesRequest paramDataTablesRequest)
    throws IllegalAccessException;
  
  public abstract DataTablesResult<Currencies> findCurrencies(DataTablesRequest paramDataTablesRequest)
    throws IllegalAccessException;
  
  public Page<AccountsDTO> findUsersForSelect(String term, Pageable pageable);
  
   DataTablesResult<BranchDTO> findOrgBranches(long paramLong, DataTablesRequest paramDataTablesRequest);
   void createRegionBranch(BranchDTO branchDTO) throws BadRequestException;
  
  
  public abstract void createOrgRegion(OrgRegions region);
  
  public abstract void deleteOrgBranch(Long paramLong);
  
  public abstract void deleteOrgRegion(Long regCode);
  
  public abstract Page<Country> findCountryForSelect(String paramString, Pageable paramPageable);
  
  public abstract Page<County> findCountyForSelect(String paramString, Pageable paramPageable, long paramLong);
  
  public abstract Page<Town> findTownForSelect(String paramString, Pageable paramPageable, long paramLong);
  
  public abstract Page<Currencies> findCurrenciesForSelect(String paramString, Pageable paramPageable);
  
  public DataTablesResult<OrgRegions> findOrgRegions(long orgCode, DataTablesRequest request) throws IllegalAccessException;

    Page<PermissionsDef> findPerm(String term, Pageable pageable);
}
