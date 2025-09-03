package com.brokersystems.brokerapp.medical.service.impl;

import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.CategoryMembersRepo;
import com.brokersystems.brokerapp.medical.repository.CategoryRulesRepo;
import com.brokersystems.brokerapp.medical.repository.MedicalCategoryRepo;
import com.brokersystems.brokerapp.medical.service.RulesService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by peter on 4/28/2017.
 */
@Service
public class RulesServiceImpl implements RulesService{

    @Autowired
    private MedicalCategoryRepo medicalCategoryRepo;

    @Autowired
    private CategoryRulesRepo categoryRulesRepo;

    @Autowired
    private CategoryMembersRepo membersRepo;

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void validateMinimumAge(Long catId) throws BadRequestException {
        Iterable<CategoryMembers> members = membersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(catId));
        Long ruleCount = categoryRulesRepo.count(QCategoryRules.categoryRules.category.id.eq(catId).and(QCategoryRules.categoryRules.binderRules.shtDesc.eq("MINAM")));
        if(ruleCount==0) return;
        if(ruleCount > 1) throw new BadRequestException("You have duplicate Rule ID MINAM on this category. Delete the duplicate rule to continue...");
        CategoryRules rule = categoryRulesRepo.findOne(QCategoryRules.categoryRules.category.id.eq(catId).and(QCategoryRules.categoryRules.binderRules.shtDesc.eq("MINAM")));
        if(parseInteger(rule.getValue())==null) throw new BadRequestException("Provide the Rule Value to continue...");
          int ruleValue = parseInteger(rule.getValue());
        for(CategoryMembers member:members){
            if(member.getAge() < 1){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                Calendar dob = Calendar.getInstance();
                dob.setTime(member.getClient().getDob());
                if((calendar.get(Calendar.MONTH) - dob.get(Calendar.MONTH)) < ruleValue)
                throw new BadRequestException("Member added has violated the rule for minimum age of members...");
            }
            else{
                if((member.getAge()*12) < ruleValue ) throw new BadRequestException("Member added has violated the rule for minimum age of members...");
            }
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void validateMaximumAge(Long catId) throws BadRequestException {
        Iterable<CategoryMembers> members = membersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(catId));
        Long ruleCount = categoryRulesRepo.count(QCategoryRules.categoryRules.category.id.eq(catId).and(QCategoryRules.categoryRules.binderRules.shtDesc.eq("MAXAM")));
        if(ruleCount==0) return;
        if(ruleCount > 1) throw new BadRequestException("You have duplicate Rule ID MAXAM on this category. Delete the duplicate rule to continue...");
        CategoryRules rule = categoryRulesRepo.findOne(QCategoryRules.categoryRules.category.id.eq(catId).and(QCategoryRules.categoryRules.binderRules.shtDesc.eq("MAXAM")));
        if(parseInteger(rule.getValue())==null) throw new BadRequestException("Provide the Rule Value to continue...");
        int ruleValue = parseInteger(rule.getValue());
        for(CategoryMembers member:members){
            if(member.getAge() > ruleValue)
                throw new BadRequestException("Member added has violated the rule for Maximum age of members...");
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void validateNoDependents(Long catId) throws BadRequestException {
        Long ruleCount = categoryRulesRepo.count(QCategoryRules.categoryRules.category.id.eq(catId).and(QCategoryRules.categoryRules.binderRules.shtDesc.eq("MD")));
        if(ruleCount==0) return;
        if(ruleCount > 1) throw new BadRequestException("You have duplicate Rule ID MD on this category. Delete the duplicate rule to continue...");
        CategoryRules rule = categoryRulesRepo.findOne(QCategoryRules.categoryRules.category.id.eq(catId).and(QCategoryRules.categoryRules.binderRules.shtDesc.eq("MD")));
        if(parseInteger(rule.getValue())==null) throw new BadRequestException("Provide the Rule Value to continue...");
        int ruleValue = parseInteger(rule.getValue());
        MedicalCategory category = medicalCategoryRepo.findOne(catId);
        Long count = membersRepo.count(QCategoryMembers.categoryMembers.category.id.eq(category.getId()));
        if(count > ruleValue)
            throw new BadRequestException("Member added has violated the rule for Maximum Number of members for this category "+category.getShtDesc());
    }

    private  Integer parseInteger(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
