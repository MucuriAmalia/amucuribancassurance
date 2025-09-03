package com.brokersystems.brokerapp.kie.utils;

import com.brokersystems.brokerapp.uw.model.QRiskDocs;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskDocsRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.mysema.query.types.expr.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by waititu on 14/07/2018.
 */
@Component
public class PolicyRulesUtils {

    @Autowired
    private static RiskTransRepo riskTransRepo;

    @Autowired
    private static RiskDocsRepo docsRepo;

    @Autowired
    private RiskTransRepo transRepo;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @PostConstruct
    public void init() {
        PolicyRulesUtils.riskTransRepo = this.transRepo;
        PolicyRulesUtils.docsRepo = this.riskDocsRepo;
    }

    public static boolean countPolicyDocs(Long riskId){
        RiskTrans riskTrans = riskTransRepo.findOne(riskId);
        BooleanExpression pred = QRiskDocs.riskDocs.risk.riskIdentifier.eq(riskTrans.getRiskIdentifier());
        return docsRepo.count(pred)==0;
    }


}
