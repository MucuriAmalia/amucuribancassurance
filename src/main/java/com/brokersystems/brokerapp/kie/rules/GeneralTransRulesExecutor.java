package com.brokersystems.brokerapp.kie.rules;

import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.CategoryRulesRepo;
import com.brokersystems.brokerapp.medical.repository.MedicalRulesRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.ErrorUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.ChecksRepo;
import com.brokersystems.brokerapp.setup.repository.ProductChecksRepo;
import com.brokersystems.brokerapp.trans.model.QTransChecks;
import com.brokersystems.brokerapp.trans.model.TransChecks;
import com.brokersystems.brokerapp.trans.repository.TransChecksRepo;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by HP on 10/4/2017.
 */
@Component
public class GeneralTransRulesExecutor {

    @Autowired
    @Qualifier(value = "uwKieServices")
    private KieContainer servicesBean;

    @Autowired
    private ErrorUtils errorUtils;

    @Autowired
    private BinderRulesObject binderRulesObject;

    @Autowired
    private ChecksRepo checksRepo;

    @Autowired
    private TransChecksRepo transChecksRepo;

    @Autowired
    private CategoryRulesRepo categoryRulesRepo;


    @Autowired
    private MedicalRulesRepo medicalRulesRepo;
    @Autowired
    private ProductChecksRepo productChecksRepo;


    private Set<TransChecks> createChecksFromRules(PolicyTrans policyTrans) throws BadRequestException{
        Set<TransChecks> transChecks = new HashSet<>();
        Iterable<CategoryRules> binderRules = categoryRulesRepo.findAll(QCategoryRules.categoryRules.category.policy.policyId.eq(policyTrans.getPolicyId()));
        binderRules.forEach(a -> {
            TransChecks transCheck = new TransChecks();
            transCheck.setChecks(a.getBinderRules().getChecks().getChecks());
            transCheck.setPermission(a.getBinderRules().getChecks().getChecks().getPermissionsDef());
            transCheck.setPolicyTrans(policyTrans);
            transChecks.add(transCheck);
        });
        return transChecks;
    }

    public Set<TransChecks> createLifeChecks(PolicyTrans policyTrans) throws BadRequestException{
        Set<TransChecks> transChecks = new HashSet<>();
        Iterable<ProductChecks> productChecks = productChecksRepo.findAll(QProductChecks.productChecks.product.eq(policyTrans.getProduct()));
        productChecks.forEach(a -> {
            TransChecks transCheck = new TransChecks();
            transCheck.setChecks(a.getChecks());
            transCheck.setPermission(a.getChecks().getPermissionsDef());
            transCheck.setPolicyTrans(policyTrans);
            transChecks.add(transCheck);
        });
        return transChecks;
    }

    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public boolean handlePolicyChecks(PolicyTrans policyTrans) throws BadRequestException {
        KieSession sessionBean = servicesBean.newKieSession();
        errorUtils.clearErrors();
        if(policyTrans.getBinder()!=null) {
            Iterable<MedicalBinderRules> medicalBinderRules = medicalRulesRepo.findAll(QMedicalBinderRules.medicalBinderRules.binder.binId.eq(policyTrans.getBinder().getBinId()));
            binderRulesObject.setBinderRules(medicalBinderRules);
        }
        boolean hasChecks = false;

        sessionBean.setGlobal("errorUtils", errorUtils);
        sessionBean.insert(binderRulesObject);
        sessionBean.insert(policyTrans);
        sessionBean.fireAllRules();
        Iterable<TransChecks> existingChecks = transChecksRepo.findAll(QTransChecks.transChecks.policyTrans.policyId.eq(policyTrans.getPolicyId()));
        Set<TransChecks> catRules = createChecksFromRules(policyTrans);
        Set<TransChecks> transChecks = new HashSet<>();
        transChecks.addAll(catRules);
        existingChecks.forEach(transChecks::add);
        Set<TransChecks> sysChecks = new HashSet<>();
        if(errorUtils.getErrors().size() > 0){
            for(String error:errorUtils.getErrors()) {
                long count = checksRepo.count(QChecks.checks.checkShtDesc.eq(error));
                if(count==0)
                    throw new BadRequestException(String.format("Check with ID %s has not been defined",error));
               Checks checks = checksRepo.findOne(QChecks.checks.checkShtDesc.eq(error));
               Long cnt = medicalRulesRepo.count(QMedicalBinderRules.medicalBinderRules.checks.checks.checkShtDesc.eq(error).and(QMedicalBinderRules.medicalBinderRules.binder.binId.eq(policyTrans.getBinder().getBinId())));
               if(cnt > 1) throw new BadRequestException(String.format("Check with ID %s has been defined multiple times on the binder....",error));
               if(cnt == 1){
                   MedicalBinderRules binderRules = medicalRulesRepo.findOne(QMedicalBinderRules.medicalBinderRules.checks.checks.checkShtDesc.eq(error).and(QMedicalBinderRules.medicalBinderRules.binder.binId.eq(policyTrans.getBinder().getBinId())));
                   if (binderRules.getChecks().getChecks().getCheckType().equalsIgnoreCase("ER"))
                       throw new BadRequestException(binderRules.getDesc());
                   else {
                       if (binderRules.getChecks().getChecks().getPermissionsDef() == null) {
                           throw new BadRequestException("Check " + binderRules.getChecks().getChecks().getCheckShtDesc() + " does not have permission...");
                       }
                       TransChecks transCheck = new TransChecks();
                       transCheck.setChecks(checks);
                       transCheck.setBinderRules(binderRules);
                       transCheck.setPermission(checks.getPermissionsDef());
                       transCheck.setPolicyTrans(policyTrans);
                       sysChecks.add(transCheck);
                       hasChecks = true;
                   }

               }
               else {
                   if (checks.getCheckType().equalsIgnoreCase("ER"))
                       throw new BadRequestException(checks.getCheckName());
                   else {
                       if (checks.getPermissionsDef() == null) {
                           throw new BadRequestException("Check " + checks.getCheckShtDesc() + " does not have permission...");
                       }
                       TransChecks transCheck = new TransChecks();
                       transCheck.setChecks(checks);
                       transCheck.setPermission(checks.getPermissionsDef());
                       transCheck.setPolicyTrans(policyTrans);
                       sysChecks.add(transCheck);
                       hasChecks = true;
                   }
               }
            }
             sysChecks.removeAll(transChecks);
            transChecksRepo.save(sysChecks);
        }
        return  hasChecks;

    }



}

