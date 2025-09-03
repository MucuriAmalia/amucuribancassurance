package com.brokersystems.brokerapp.reinsurance.service.impl;

import com.brokersystems.brokerapp.claims.dtos.ClaimantsDTO;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.reinsurance.dtos.TreatyClassesDTO;
import com.brokersystems.brokerapp.reinsurance.dtos.TreatyDefinitionDTO;
import com.brokersystems.brokerapp.reinsurance.dtos.TreatyParticipantsDTO;
import com.brokersystems.brokerapp.reinsurance.enums.TreatyTypes;
import com.brokersystems.brokerapp.reinsurance.model.TreatyClasses;
import com.brokersystems.brokerapp.reinsurance.model.TreatyDefinition;
import com.brokersystems.brokerapp.reinsurance.model.TreatyParticipants;
import com.brokersystems.brokerapp.reinsurance.repo.TreatyClassesRepository;
import com.brokersystems.brokerapp.reinsurance.repo.TreatyDefinitionRepository;
import com.brokersystems.brokerapp.reinsurance.repo.TreatyParticipantsRepository;
import com.brokersystems.brokerapp.reinsurance.service.ReinsuranceSetupService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.repository.AccountRepo;
import com.brokersystems.brokerapp.setup.repository.CurrencyRepository;
import com.brokersystems.brokerapp.setup.repository.RevenueItemsRepo;
import com.brokersystems.brokerapp.setup.repository.SubClassRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReinsuranceSetupServiceImpl implements ReinsuranceSetupService {

    @Autowired
    private TreatyDefinitionRepository treatyDefinitionRepo;

    @Autowired
    private TreatyParticipantsRepository treatyParticipantsRepository;
    @Autowired
    private TreatyClassesRepository treatyClassesRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private UserUtils userUtils;

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private RevenueItemsRepo revenueItemsRepo;
    @Autowired
    private SubClassRepo subClassRepo;

    @Override
    public DataTablesResult<TreatyDefinitionDTO> findAllTreaties(DataTablesRequest request) {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";
        List<Object[]> treaties = treatyDefinitionRepo.findTreaties(search, request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!treaties.isEmpty()) rowCount = ((BigInteger)treaties.get(0)[13]).intValue();
        List<TreatyDefinitionDTO> treatyList = new ArrayList<>();
        for(Object[] treaty :treaties){
            TreatyDefinitionDTO treatyDefinitionDTO = TreatyDefinitionDTO.data(((BigInteger)treaty[0]).longValue(),(String)treaty[1],
                    (Date)treaty[2],(Date)treaty[3],(BigDecimal)treaty[4],(BigDecimal) treaty[5],(String)treaty[6],null,
                    null,null,null,(BigDecimal) treaty[7],null,null,
                    null,(String) treaty[8],(String) treaty[10],(String) treaty[9],(String) treaty[11]);
            treatyDefinitionDTO.setSumInsuredFrom((BigDecimal) treaty[12]);
            treatyList.add(treatyDefinitionDTO);
        }
        Page<TreatyDefinitionDTO>  page = new PageImpl<>(treatyList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<TreatyParticipantsDTO> findTreatyParticipants(Long treatyId, DataTablesRequest request) {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";
        List<Object[]> participants = treatyParticipantsRepository.findTreatiesParticipants(treatyId,search, request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!participants.isEmpty()) rowCount = ((BigInteger)participants.get(0)[10]).intValue();
        List<TreatyParticipantsDTO> treatyParticipants = new ArrayList<>();
        for(Object[] participant :participants){
            TreatyParticipantsDTO treatyParticipantsDTO = TreatyParticipantsDTO.data(((BigInteger)participant[0]).longValue(),
                    null, (String)participant[8], (BigDecimal) participant[2],(String) participant[1],
                    (String) participant[4],null,(String)participant[7],null, RevenueItems.valueOf((String) participant[9]).getValue(),
                    (BigDecimal)participant[5],(String) participant[6],(BigDecimal)participant[3],null);
            treatyParticipants.add(treatyParticipantsDTO);
        }
        Page<TreatyParticipantsDTO>  page = new PageImpl<>(treatyParticipants,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<TreatyClassesDTO> findTreatyClasses(Long treatyId, DataTablesRequest request) {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";
        List<Object[]> treatyClasses = treatyClassesRepository.findTreatiesClasses(treatyId,search, request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!treatyClasses.isEmpty()) rowCount = ((BigInteger)treatyClasses.get(0)[8]).intValue();
        List<TreatyClassesDTO> treatyClassesDTO = new ArrayList<>();
        for(Object[] treatyClass :treatyClasses){
            TreatyClassesDTO treatyParticipantsDTO = TreatyClassesDTO.data(((BigInteger)treatyClass[0]).longValue(),
                    (BigDecimal)treatyClass[2] , (BigDecimal) treatyClass[4], (BigDecimal) treatyClass[3],null,
                    null,(String)treatyClass[1]);
            treatyParticipantsDTO.setClaimLimit((BigDecimal) treatyClass[6]);
            treatyParticipantsDTO.setInsuredLimit((BigDecimal) treatyClass[7]);
            treatyClassesDTO.add(treatyParticipantsDTO);
        }
        Page<TreatyClassesDTO>  page = new PageImpl<>(treatyClassesDTO,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public void saveTreatyType(TreatyDefinitionDTO treatyDefinitionDTO) throws BadRequestException {
        if(treatyDefinitionDTO.getCurrencyId()==null){
            throw new BadRequestException("Please Select Currency to continue...");
        }
        if(treatyDefinitionDTO.getWef().after(treatyDefinitionDTO.getWet())){
            throw new BadRequestException("Date From cannot be greater than Date To");
        }
        if(treatyDefinitionDTO.getTreatyType()==null){
            throw new BadRequestException("Select Treaty Type to continue...");
        }
        if(treatyDefinitionDTO.getCessionRate()==null || treatyDefinitionDTO.getCessionRate().compareTo(BigDecimal.ZERO) <= 0){
            throw new BadRequestException("Enter Cession Rate or Cession Rate is less than or equal to zero");
        }
        if(treatyDefinitionDTO.getLimit()==null || treatyDefinitionDTO.getLimit().compareTo(BigDecimal.ZERO) <=0){
            throw new BadRequestException("Enter Retention Limit or retention limit is less than or equal to zero");
        }
        if(treatyDefinitionDTO.getRateType()==null){
            throw new BadRequestException("Select Rate Type to continue...");
        }
        final Currencies currencies = currencyRepository.findOne(treatyDefinitionDTO.getCurrencyId());
        if(currencies==null){
            throw new BadRequestException("Select a Valid Currency...");
        }
        final TreatyDefinition treatyDefinition = new TreatyDefinition();
        treatyDefinition.setCurrency(currencies);
        treatyDefinition.setCessionRate(treatyDefinitionDTO.getCessionRate());
        treatyDefinition.setLimit(treatyDefinitionDTO.getLimit());
        treatyDefinition.setRateType(treatyDefinitionDTO.getRateType());
        treatyDefinition.setStatus("D");
        treatyDefinition.setRaisedBy(userUtils.getCurrentUser());
        treatyDefinition.setWef(treatyDefinitionDTO.getWef());
        treatyDefinition.setWet(treatyDefinitionDTO.getWet());
        treatyDefinition.setTreatyTypes(TreatyTypes.valueOf(treatyDefinitionDTO.getTreatyType()));
        treatyDefinition.setCashCall(treatyDefinitionDTO.getCashCall());
        treatyDefinition.setProfitCommission(treatyDefinitionDTO.getProfitCommission());
        treatyDefinition.setPremiumPortfolio(treatyDefinitionDTO.getPremiumPortfolio());
        treatyDefinition.setClaimsPortfolio(treatyDefinitionDTO.getClaimsPortfolio());
        treatyDefinition.setCommRate(treatyDefinitionDTO.getCommRate());
        treatyDefinition.setFacCedeRate(treatyDefinitionDTO.getFacCedeRate());
        treatyDefinition.setTreatyId(treatyDefinitionDTO.getTreatyId());
        treatyDefinition.setManagementFeeRate(treatyDefinitionDTO.getManagementFeeRate());
        treatyDefinition.setSumInsuredFrom(treatyDefinitionDTO.getSumInsuredFrom());
        treatyDefinitionRepo.save(treatyDefinition);
    }

    @Override
    public void saveTreatyClass(TreatyClassesDTO treatyClassesDTO) throws BadRequestException {
        if(treatyClassesDTO.getSubclassId()==null){
            throw new BadRequestException("Select Sub class to continue...");
        }
        if(treatyClassesDTO.getRetentionLimit()==null || treatyClassesDTO.getRetentionLimit().compareTo(BigDecimal.ZERO) <=0){
            throw new BadRequestException("Retention Limit cannot be negative or zero...");
        }
       if(treatyClassesDTO.getTreatyId()==null){
           throw new BadRequestException("Select Treaty to add Class..");
       }
        final TreatyDefinition treatyDefinition = treatyDefinitionRepo.findOne(treatyClassesDTO.getTreatyId());
        if(treatyDefinition==null){
            throw new BadRequestException("Select a Valid Treaty to add Class...");
        }
       final TreatyClasses treatyClasses = new TreatyClasses();
       treatyClasses.setTreatyDefinition(treatyDefinition);
       treatyClasses.setFacCedeRate(treatyClassesDTO.getFacCedeRate());
       treatyClasses.setSubClassDef(subClassRepo.findOne(treatyClassesDTO.getSubclassId()));
       treatyClasses.setMinEml(treatyClassesDTO.getMinEml());
       treatyClasses.setRetentionLimit(treatyClassesDTO.getRetentionLimit());
       treatyClasses.setRiPremTaxRate(treatyClassesDTO.getRiPremTaxRate());
       treatyClasses.setClaimLimit(treatyClassesDTO.getClaimLimit());
       treatyClasses.setInsuredLimit(treatyClassesDTO.getInsuredLimit());
       treatyClassesRepository.save(treatyClasses);
    }

    @Override
    public void saveTreatyParticipant(TreatyParticipantsDTO treatyParticipant) throws BadRequestException {
        if(treatyParticipant.getTreatyId()==null){
            throw new BadRequestException("Select Treaty to add participant...");
        }
        final TreatyDefinition treatyDefinition = treatyDefinitionRepo.findOne(treatyParticipant.getTreatyId());
        if(treatyDefinition==null){
            throw new BadRequestException("Select a Valid Treaty to add participant...");
        }
        if(treatyParticipant.getParticipantId()==null){
            throw new BadRequestException("Please select Participant to continue...");
        }
        final AccountDef accountDef = accountRepo.findOne(treatyParticipant.getParticipantId());
        if(accountDef==null){
            throw new BadRequestException("Participant cannot be null...");
        }
        AccountDef broker = null;
        String brokerType = "N";
        if(treatyParticipant.getBrokerType()!=null && treatyParticipant.getBrokerType().equalsIgnoreCase("on")){
            if(treatyParticipant.getBrokerId()==null){
                throw new BadRequestException("Select Broker to continue...");
            }
            broker =accountRepo.findOne(treatyParticipant.getBrokerId());
            brokerType = "Y";

        }
        if(treatyParticipant.getRevenueItemId()==null){
            throw new BadRequestException("Select Revenue Item to continue...");
        }

        final TreatyParticipants participants = new TreatyParticipants();
        participants.setTreatyDefinition(treatyDefinition);
        participants.setRate(treatyParticipant.getRate());
        participants.setParticipant(accountDef);
        participants.setBroker(broker);
        participants.setTaxRateType(treatyParticipant.getTaxRateType());
        participants.setTaxChargeable(treatyParticipant.getTaxChargeable());
        participants.setRevenueItems(revenueItemsRepo.findOne(treatyParticipant.getRevenueItemId()));
        participants.setTaxRate(treatyParticipant.getTaxRate());
        participants.setRecoveryPercent(treatyParticipant.getRecoveryPercent());
        participants.setBrokerType(brokerType);
        treatyParticipantsRepository.save(participants);
    }
}
