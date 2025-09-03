package com.brokersystems.brokerapp.medical.service;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.uw.model.RevisionForm;

/**
 * Created by peter on 7/11/2017.
 */
public interface MedicalEndorseService {

    public Long reviseTransaction(RevisionForm revisionForm) throws EndorsementsException;

    public void replaceCard(Long cardId, Long polCode) throws BadRequestException;
}
