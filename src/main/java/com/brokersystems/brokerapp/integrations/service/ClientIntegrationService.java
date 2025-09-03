package com.brokersystems.brokerapp.integrations.service;


import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.netreveal.NetrevealRequest;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface ClientIntegrationService {
    Map<String,Object> validateID(String documentType, String documentId) throws BadRequestException;
    String validateKRA(Object authentication) throws BadRequestException;

    Map<String, Object> individualScreening(Object request) throws BadRequestException;

    Map<String, Object> entityScreening(Object request) throws BadRequestException;

    @Transactional
    void backgroundNetrevealScreening(ClientDef clientDef, NetrevealRequest request) throws BadRequestException;
}
