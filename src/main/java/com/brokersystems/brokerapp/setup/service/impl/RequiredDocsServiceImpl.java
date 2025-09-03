package com.brokersystems.brokerapp.setup.service.impl;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.RequiredDocsRepo;
import com.brokersystems.brokerapp.setup.repository.SubClassRepo;
import com.brokersystems.brokerapp.setup.repository.SubclassReqDocRepo;
import com.brokersystems.brokerapp.setup.service.RequiredDocsService;
import com.mysema.query.types.expr.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter on 3/5/2017.
 */
@Service
public class RequiredDocsServiceImpl implements RequiredDocsService {

    @Autowired
    private RequiredDocsRepo requiredDocsRepo;

    @Autowired
    private SubclassReqDocRepo subclassReqDocRepo;

    @Autowired
    private SubClassRepo subClassRepo;

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<RequiredDocs> findAllRequiredDocs(DataTablesRequest request) throws IllegalAccessException {
        Page<RequiredDocs> page = requiredDocsRepo.findAll(request.searchPredicate(QRequiredDocs.requiredDocs1), request);
        return new DataTablesResult<>(request, page);
    }


    @Override
    @Transactional(readOnly = false)
    public void defineRequiredDocs(RequiredDocs requiredDocs) throws BadRequestException {
        if(requiredDocs.getAppliesAccount()!=null && "on".equalsIgnoreCase(requiredDocs.getAppliesAccount())){
            requiredDocs.setAppliesAccount("Y");
        }
        else requiredDocs.setAppliesAccount("N");
        if(requiredDocs.getAppliesClient()!=null && "on".equalsIgnoreCase(requiredDocs.getAppliesClient())){
            requiredDocs.setAppliesClient("Y");
        }
        else requiredDocs.setAppliesClient("N");
        if(requiredDocs.getAppliesCorpClient()!=null && "on".equalsIgnoreCase(requiredDocs.getAppliesCorpClient())){
            requiredDocs.setAppliesCorpClient("Y");
        }
        else requiredDocs.setAppliesCorpClient("N");
        if(requiredDocs.getAppliesSubAgent()!=null && "on".equalsIgnoreCase(requiredDocs.getAppliesSubAgent())){
            requiredDocs.setAppliesSubAgent("Y");
        }
        else requiredDocs.setAppliesSubAgent("N");
        if (requiredDocs.getAccrualDoc() != null && "on".equalsIgnoreCase(requiredDocs.getAccrualDoc())){
            requiredDocs.setAccrualDoc("Y");
        }
        else requiredDocs.setAccrualDoc("N");
        requiredDocsRepo.save(requiredDocs);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRequiredDocs(Long reqId) {
        requiredDocsRepo.delete(reqId);
    }


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SubClassReqdDocs> findSubclassReqDocs(Long subCode, DataTablesRequest request) throws IllegalAccessException {
        QSubClassReqdDocs reqdDocs = QSubClassReqdDocs.subClassReqdDocs;
        BooleanExpression pred = reqdDocs.subclass.subId.eq(subCode);
        Page<SubClassReqdDocs> page = subclassReqDocRepo.findAll(pred.and(request.searchPredicate(QSubClassReqdDocs.subClassReqdDocs)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubClassReqdDocs> getUnassignedReqDocs(Long subCode, String reqsearch) throws IllegalAccessException {
        return subclassReqDocRepo.getUnassignedReqDocs(subCode, reqsearch);
    }

    @Override
    @Transactional(readOnly = false)
    public void createSubClassReqDocs(RequiredDocBean requiredDocBean) {
        List<SubClassReqdDocs> reqdDocs = new ArrayList<>();
        for (Long reqId:requiredDocBean.getRequiredDocs()){
            SubClassReqdDocs subClassReqdDoc = new SubClassReqdDocs();
            subClassReqdDoc.setRequiredDoc(requiredDocsRepo.findOne(reqId));
            subClassReqdDoc.setSubclass(subClassRepo.findOne(requiredDocBean.getSubCode()));
            reqdDocs.add(subClassReqdDoc);
        }
        subclassReqDocRepo.save(reqdDocs);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSubRequiredDocs(Long subReqId) {
        subclassReqDocRepo.delete(subReqId);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineSubRequiredDocs(SubClassReqdDocs reqdDocs) {
        subclassReqDocRepo.save(reqdDocs);
    }
}
