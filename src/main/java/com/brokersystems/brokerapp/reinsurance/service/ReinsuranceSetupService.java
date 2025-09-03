package com.brokersystems.brokerapp.reinsurance.service;

import com.brokersystems.brokerapp.reinsurance.dtos.TreatyClassesDTO;
import com.brokersystems.brokerapp.reinsurance.dtos.TreatyDefinitionDTO;
import com.brokersystems.brokerapp.reinsurance.dtos.TreatyParticipantsDTO;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReinsuranceSetupService {

    DataTablesResult<TreatyDefinitionDTO> findAllTreaties(DataTablesRequest request);
    DataTablesResult<TreatyParticipantsDTO> findTreatyParticipants(Long treatyId, DataTablesRequest request);
    DataTablesResult<TreatyClassesDTO> findTreatyClasses(Long treatyId, DataTablesRequest request);
    void saveTreatyType(TreatyDefinitionDTO treatyDefinitionDTO) throws BadRequestException;
    void saveTreatyParticipant(TreatyParticipantsDTO treatyParticipant) throws BadRequestException;
    void saveTreatyClass(TreatyClassesDTO treatyParticipant) throws BadRequestException;

}
