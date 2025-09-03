package com.brokersystems.brokerapp.life.service;

import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.uw.model.RevisionForm;

/**
 * Created by waititu on 22/06/2019.
 */
public interface LifeEndorseService {
     Long reviseTransaction(RevisionForm revisionForm) throws EndorsementsException;

     Long countUnauthTransactions(String policyNumber);
}
