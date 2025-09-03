package com.brokersystems.brokerapp.setup.service;

import com.brokersystems.brokerapp.setup.dto.GlobalAdminFeeSetupDTO;
import com.brokersystems.brokerapp.setup.dto.GlobalCommissionRatesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GlobalRatesService {
    void createGlobalCommissionRate(GlobalCommissionRatesDTO globalCommissionRatesDTO);

    void createGlobalAdminFeesRate(GlobalAdminFeeSetupDTO globalAdminFeeSetupDTO);

    Page<GlobalCommissionRatesDTO> getAllGlobalCommissionRates(Pageable pageable);

    Page<GlobalAdminFeeSetupDTO> getAllGlobalAdminFees(Pageable pageable);

    GlobalCommissionRatesDTO getGlobalCommissionRate(Long commId);

    GlobalAdminFeeSetupDTO getGlobalAdminFeeRate(Long fcId);
}
