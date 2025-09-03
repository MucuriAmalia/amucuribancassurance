package com.brokersystems.brokerapp.medical.service;

import com.brokersystems.brokerapp.medical.model.MedicalBatchDTO;
import com.brokersystems.brokerapp.medical.model.MedicalCards;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;

import java.util.Date;

/**
 * Created by peter on 5/28/2017.
 */
public interface MedicalCardsService {

    void generateMedicalCards(Long policyId) throws BadRequestException;

    DataTablesResult<MedicalCards> findMedicalCards(DataTablesRequest request, String policyNo,
                                                           String memberNo, String memberName, String clientName ,String cardNo) throws IllegalAccessException;

    void receiveCards(MedicalBatchDTO batchDTO) throws BadRequestException;

    void requestCards(MedicalBatchDTO batchDTO) throws BadRequestException;

    void dispatchCards(MedicalBatchDTO batchDTO) throws BadRequestException;
    public void saveMemberCardNo(MedicalCards medicalCards) throws BadRequestException;

    DataTablesResult<MedicalCards> findPolicyMedicalCards(DataTablesRequest request, Long polCode) throws IllegalAccessException;



}
