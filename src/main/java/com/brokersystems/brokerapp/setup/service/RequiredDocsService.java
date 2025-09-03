package com.brokersystems.brokerapp.setup.service;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.*;

import java.util.List;

/**
 * Created by peter on 3/5/2017.
 */
public interface RequiredDocsService {

    public DataTablesResult<RequiredDocs> findAllRequiredDocs(DataTablesRequest request) throws IllegalAccessException;

    public void defineRequiredDocs(RequiredDocs requiredDocs) throws BadRequestException;

    void deleteRequiredDocs(Long reqId);

    public DataTablesResult<SubClassReqdDocs> findSubclassReqDocs(Long subCode, DataTablesRequest request) throws IllegalAccessException;

    List<SubClassReqdDocs> getUnassignedReqDocs(Long subCode, String reqsearch)  throws IllegalAccessException;

    void createSubClassReqDocs(RequiredDocBean requiredDocBean);

    void deleteSubRequiredDocs(Long subReqId);

    void defineSubRequiredDocs(SubClassReqdDocs reqdDocs);


}
