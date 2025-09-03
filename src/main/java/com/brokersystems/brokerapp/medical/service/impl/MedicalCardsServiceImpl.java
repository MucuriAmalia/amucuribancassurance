package com.brokersystems.brokerapp.medical.service.impl;

import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.CategoryMembersRepo;
import com.brokersystems.brokerapp.medical.repository.MedicalCardsRepo;
import com.brokersystems.brokerapp.medical.repository.MedicalCategoryRepo;
import com.brokersystems.brokerapp.medical.service.MedicalCardsService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.QClientDef;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 5/28/2017.
 */
@Service
public class MedicalCardsServiceImpl implements MedicalCardsService {

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private MedicalCategoryRepo categoryRepo;

    @Autowired
    private CategoryMembersRepo categoryMembersRepo;

    @Autowired
    private MedicalCardsRepo medicalCardsRepo;

    @Autowired
    private UserUtils userUtils;

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void generateMedicalCards(Long policyId) throws BadRequestException {
        List<MedicalCards> medicalCards = new ArrayList<>();

        Iterable<MedicalCategory> categories = categoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(policyId));
        for(MedicalCategory category:categories){
            Iterable<CategoryMembers> members  =null;
            if (category.getPolicy().getTransType().equalsIgnoreCase("NB")||category.getPolicy().getTransType().equalsIgnoreCase("RN")){
                members  = categoryMembersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(category.getId()));
            }else if (category.getPolicy().getTransType().equalsIgnoreCase("AD"))
                members  = categoryMembersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(category.getId())
                .and(QCategoryMembers.categoryMembers.memberStatus.eq("N")));
            if (members!=null){
                for(CategoryMembers member:members){
                    MedicalCards cards = new MedicalCards();
                    if (member.getCardNo()==null || StringUtils.isBlank(member.getCardNo()))
                    cards.setStatus("Draft");
                    else cards.setStatus("Dispatched");
                    cards.setCardNo(member.getCardNo());
                    cards.setDateProcessed(new Date());
                    cards.setWefDate(member.getWefDate());
                    cards.setWetDate(member.getWetDate());
                    cards.setProcessedBy(userUtils.getCurrentUser());
                    cards.setMember(member);
                    medicalCards.add(cards);
                }
            }
            medicalCardsRepo.save(medicalCards);
        }


    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalCards> findPolicyMedicalCards(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        Page<MedicalCards> page = medicalCardsRepo.searchPolicyMedicalCards(polCode,request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalCards> findMedicalCards(DataTablesRequest request, String policyNo, String memberNo, String memberName, String clientName, String cardNo) throws IllegalAccessException {
        if(policyNo==null)
            policyNo="";
        if(memberNo==null)
            memberNo="";
        if(memberName==null)
            memberName="";
        if(clientName==null)
            clientName="";
        if(cardNo==null)
            cardNo="";
        Page<MedicalCards> page = medicalCardsRepo.searchMedicalCards(memberNo,memberName,clientName,policyNo,cardNo,request);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void receiveCards(MedicalBatchDTO batchDTO) throws BadRequestException {
        if(batchDTO.getCards().size()==0) throw new BadRequestException("No Cards Selected to Dispatch...");
        List<MedicalCards> medicalCards = new ArrayList<>();
        for(Long cardId:batchDTO.getCards()){
            MedicalCards cards = medicalCardsRepo.findOne(cardId);
            if("Requested".equalsIgnoreCase(cards.getStatus())){
                cards.setStatus("Received");
                cards.setDateReceived(new Date());
                cards.setUserReceived(userUtils.getCurrentUser());
                medicalCards.add(cards);
            }

        }
        medicalCardsRepo.save(medicalCards);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void requestCards(MedicalBatchDTO batchDTO) throws BadRequestException {
        if(batchDTO.getCards().size()==0) throw new BadRequestException("No Cards Selected to Request...");
        List<MedicalCards> medicalCards = new ArrayList<>();
        for(Long cardId:batchDTO.getCards()){
            MedicalCards cards = medicalCardsRepo.findOne(cardId);
            if("Draft".equalsIgnoreCase(cards.getStatus())){
                cards.setStatus("Requested");
                cards.setDateRequested(new Date());
                cards.setUserRequested(userUtils.getCurrentUser());
                medicalCards.add(cards);
            }

        }
        medicalCardsRepo.save(medicalCards);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void dispatchCards(MedicalBatchDTO batchDTO) throws BadRequestException {
        if(batchDTO.getCards().size()==0) throw new BadRequestException("No Cards Selected to Dispatch...");
        List<MedicalCards> medicalCards = new ArrayList<>();
        for(Long cardId:batchDTO.getCards()){
            MedicalCards cards = medicalCardsRepo.findOne(cardId);
            if("Received".equalsIgnoreCase(cards.getStatus())){
                cards.setStatus("Dispatched");
                cards.setDateDispatched(new Date());
                cards.setUserDispatched(userUtils.getCurrentUser());
                medicalCards.add(cards);
            }

        }
        medicalCardsRepo.save(medicalCards);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void saveMemberCardNo(MedicalCards medicalCards) throws BadRequestException {
        MedicalCards existingCard = medicalCardsRepo.findOne(medicalCards.getCardId());
        existingCard.setCardNo(medicalCards.getCardNo());
        medicalCardsRepo.save(existingCard);
    }
}