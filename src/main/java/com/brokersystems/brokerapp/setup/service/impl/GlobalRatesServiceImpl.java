package com.brokersystems.brokerapp.setup.service.impl;

import com.brokersystems.brokerapp.setup.dto.GlobalAdminFeeSetupDTO;
import com.brokersystems.brokerapp.setup.dto.GlobalCommissionRatesDTO;
import com.brokersystems.brokerapp.setup.model.GlobalAdminFeeSetup;
import com.brokersystems.brokerapp.setup.model.GlobalCommissionRates;
import com.brokersystems.brokerapp.setup.repository.GlobalAdminFeeSetupRepository;
import com.brokersystems.brokerapp.setup.repository.GlobalCommissionRatesRepository;
import com.brokersystems.brokerapp.setup.service.GlobalRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link GlobalRatesService} interface for managing global commission and admin fee rates.
 * Provides methods to create, retrieve, and paginate global commission and admin fee configurations.
 * Ensures validation of mandatory fields as defined in the entity models.
 */
@Service
public class GlobalRatesServiceImpl implements GlobalRatesService {

    @Autowired
    private GlobalCommissionRatesRepository globalCommissionRatesRepository;

    @Autowired
    private GlobalAdminFeeSetupRepository globalAdminFeeSetupRepository;

    /**
     * Creates a new global commission rate based on the provided DTO.
     * Validates mandatory fields (productCode, productGroup, productName, accountTypeName, accountType,
     * commRate, commDivFactor, rateType, commRangeFrom, commRangeTo) before persisting.
     *
     * @param globalCommissionRatesDTO the DTO containing commission rate details
     * @throws IllegalArgumentException if any mandatory field is null
     */
    @Override
    public void createGlobalCommissionRate(GlobalCommissionRatesDTO globalCommissionRatesDTO) {
        validateGlobalCommissionRatesDTO(globalCommissionRatesDTO);
        GlobalCommissionRates entity = mapToCommissionEntity(globalCommissionRatesDTO);
        globalCommissionRatesRepository.save(entity);
    }

    /**
     * Creates a new global admin fee rate based on the provided DTO.
     * Validates mandatory fields (productCode, productGroup, productName) before persisting.
     *
     * @param globalAdminFeeSetupDTO the DTO containing admin fee details
     * @throws IllegalArgumentException if any mandatory field is null
     */
    @Override
    public void createGlobalAdminFeesRate(GlobalAdminFeeSetupDTO globalAdminFeeSetupDTO) {
        validateGlobalAdminFeeSetupDTO(globalAdminFeeSetupDTO);
        GlobalAdminFeeSetup entity = mapToAdminFeeEntity(globalAdminFeeSetupDTO);
        globalAdminFeeSetupRepository.save(entity);
    }

    /**
     * Retrieves all global commission rates with pagination.
     *
     * @param pageable the pagination and sorting parameters
     * @return a {@link Page} of {@link GlobalCommissionRatesDTO} containing the commission rates
     */
    @Override
    public Page<GlobalCommissionRatesDTO> getAllGlobalCommissionRates(Pageable pageable) {
        return globalCommissionRatesRepository.findAll(pageable)
                .map(this::mapToCommissionDTO);
    }

    /**
     * Retrieves all global admin fee rates with pagination.
     *
     * @param pageable the pagination and sorting parameters
     * @return a {@link Page} of {@link GlobalAdminFeeSetupDTO} containing the admin fee rates
     */
    @Override
    public Page<GlobalAdminFeeSetupDTO> getAllGlobalAdminFees(Pageable pageable) {
        return globalAdminFeeSetupRepository.findAll(pageable)
                .map(this::mapToAdminFeeDTO);
    }

    /**
     * Retrieves a single global commission rate by its ID.
     *
     * @param commId the ID of the commission rate
     * @return the {@link GlobalCommissionRatesDTO} for the specified ID
     * @throws IllegalArgumentException if the commission rate is not found
     */
    @Override
    public GlobalCommissionRatesDTO getGlobalCommissionRate(Long commId) {
        GlobalCommissionRates entity = globalCommissionRatesRepository.findOne(commId);
        if (entity == null) {
            throw new IllegalArgumentException("Global commission rate with ID " + commId + " not found");
        }
        return mapToCommissionDTO(entity);
    }

    /**
     * Retrieves a single global admin fee rate by its ID.
     *
     * @param fcId the ID of the admin fee rate
     * @return the {@link GlobalAdminFeeSetupDTO} for the specified ID
     * @throws IllegalArgumentException if the admin fee rate is not found
     */
    @Override
    public GlobalAdminFeeSetupDTO getGlobalAdminFeeRate(Long fcId) {
        GlobalAdminFeeSetup entity = globalAdminFeeSetupRepository.findOne(fcId);
        if (entity == null) {
            throw new IllegalArgumentException("Global admin fee rate with ID " + fcId + " not found");
        }
        return mapToAdminFeeDTO(entity);
    }

    /**
     * Validates the mandatory fields of the {@link GlobalCommissionRatesDTO}.
     *
     * @param dto the DTO to validate
     * @throws IllegalArgumentException if any mandatory field is null
     */
    private void validateGlobalCommissionRatesDTO(GlobalCommissionRatesDTO dto) {
        if (dto.getProductCode() == null) {
            throw new IllegalArgumentException("Product code is mandatory");
        }
        if (dto.getProductGroup() == null) {
            throw new IllegalArgumentException("Product group is mandatory");
        }
        if (dto.getProductName() == null) {
            throw new IllegalArgumentException("Product name is mandatory");
        }
        if (dto.getAccountTypeName() == null) {
            throw new IllegalArgumentException("Account type name is mandatory");
        }
        if (dto.getAccountType() == null) {
            throw new IllegalArgumentException("Account type is mandatory");
        }
        if (dto.getCommRate() == null) {
            throw new IllegalArgumentException("Commission rate is mandatory");
        }
        if (dto.getCommDivFactor() == null) {
            throw new IllegalArgumentException("Commission division factor is mandatory");
        }
        if (dto.getRateType() == null) {
            throw new IllegalArgumentException("Rate type is mandatory");
        }
        if (dto.getCommRangeFrom() == null) {
            throw new IllegalArgumentException("Commission range from is mandatory");
        }
        if (dto.getCommRangeTo() == null) {
            throw new IllegalArgumentException("Commission range to is mandatory");
        }
    }

    /**
     * Validates the mandatory fields of the {@link GlobalAdminFeeSetupDTO}.
     *
     * @param dto the DTO to validate
     * @throws IllegalArgumentException if any mandatory field is null
     */
    private void validateGlobalAdminFeeSetupDTO(GlobalAdminFeeSetupDTO dto) {
        if (dto.getProductCode() == null) {
            throw new IllegalArgumentException("Product code is mandatory");
        }
        if (dto.getProductGroup() == null) {
            throw new IllegalArgumentException("Product group is mandatory");
        }
        if (dto.getProductName() == null) {
            throw new IllegalArgumentException("Product name is mandatory");
        }
    }

    /**
     * Maps a {@link GlobalCommissionRatesDTO} to a {@link GlobalCommissionRates} entity.
     *
     * @param dto the DTO to map
     * @return the mapped entity
     */
    private GlobalCommissionRates mapToCommissionEntity(GlobalCommissionRatesDTO dto) {
        GlobalCommissionRates entity = new GlobalCommissionRates();
        entity.setCommId(dto.getCommId());
        entity.setProductCode(dto.getProductCode());
        entity.setProductGroup(dto.getProductGroup());
        entity.setProductName(dto.getProductName());
        entity.setAccountTypeName(dto.getAccountTypeName());
        entity.setAccountType(dto.getAccountType());
        entity.setCommRate(dto.getCommRate());
        entity.setActive(dto.isActive());
        entity.setRateDesc(dto.getRateDesc());
        entity.setCommDivFactor(dto.getCommDivFactor());
        entity.setRateType(dto.getRateType());
        entity.setCommRangeFrom(dto.getCommRangeFrom());
        entity.setCommRangeTo(dto.getCommRangeTo());
        entity.setApplicableAt(dto.getApplicableAt());
        return entity;
    }

    /**
     * Maps a {@link GlobalAdminFeeSetupDTO} to a {@link GlobalAdminFeeSetup} entity.
     *
     * @param dto the DTO to map
     * @return the mapped entity
     */
    private GlobalAdminFeeSetup mapToAdminFeeEntity(GlobalAdminFeeSetupDTO dto) {
        GlobalAdminFeeSetup entity = new GlobalAdminFeeSetup();
        entity.setFcId(dto.getFcId());
        entity.setProductCode(dto.getProductCode());
        entity.setProductGroup(dto.getProductGroup());
        entity.setProductName(dto.getProductName());
        entity.setAdminFeeRate(dto.getAdminFeeRate());
        entity.setAdminFeeRateType(dto.getAdminFeeRateType());
        entity.setVatRate(dto.getVatRate());
        entity.setVateRateType(dto.getVateRateType());
        entity.setExciseRate(dto.getExciseRate());
        entity.setExciseRateType(dto.getExciseRateType());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    /**
     * Maps a {@link GlobalCommissionRates} entity to a {@link GlobalCommissionRatesDTO}.
     *
     * @param entity the entity to map
     * @return the mapped DTO
     */
    private GlobalCommissionRatesDTO mapToCommissionDTO(GlobalCommissionRates entity) {
        GlobalCommissionRatesDTO dto = new GlobalCommissionRatesDTO();
        dto.setCommId(entity.getCommId());
        dto.setProductCode(entity.getProductCode());
        dto.setProductGroup(entity.getProductGroup());
        dto.setProductName(entity.getProductName());
        dto.setAccountTypeName(entity.getAccountTypeName());
        dto.setAccountType(entity.getAccountType());
        dto.setCommRate(entity.getCommRate());
        dto.setActive(entity.isActive());
        dto.setRateDesc(entity.getRateDesc());
        dto.setCommDivFactor(entity.getCommDivFactor());
        dto.setRateType(entity.getRateType());
        dto.setCommRangeFrom(entity.getCommRangeFrom());
        dto.setCommRangeTo(entity.getCommRangeTo());
        dto.setApplicableAt(entity.getApplicableAt());
        return dto;
    }

    /**
     * Maps a {@link GlobalAdminFeeSetup} entity to a {@link GlobalAdminFeeSetupDTO}.
     *
     * @param entity the entity to map
     * @return the mapped DTO
     */
    private GlobalAdminFeeSetupDTO mapToAdminFeeDTO(GlobalAdminFeeSetup entity) {
        GlobalAdminFeeSetupDTO dto = new GlobalAdminFeeSetupDTO();
        dto.setFcId(entity.getFcId());
        dto.setProductCode(entity.getProductCode());
        dto.setProductGroup(entity.getProductGroup());
        dto.setProductName(entity.getProductName());
        dto.setAdminFeeRate(entity.getAdminFeeRate());
        dto.setAdminFeeRateType(entity.getAdminFeeRateType());
        dto.setVatRate(entity.getVatRate());
        dto.setVateRateType(entity.getVateRateType());
        dto.setExciseRate(entity.getExciseRate());
        dto.setExciseRateType(entity.getExciseRateType());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}