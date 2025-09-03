package com.brokersystems.brokerapp.jobs;

import com.brokersystems.brokerapp.audit.DeletedTransAudit;
import com.brokersystems.brokerapp.audit.repository.DeletedTransAuditRepo;
import com.brokersystems.brokerapp.quotes.model.QQuoteTrans;
import com.brokersystems.brokerapp.quotes.model.QuoteTrans;
import com.brokersystems.brokerapp.quotes.repository.QuotTransRepo;
import com.brokersystems.brokerapp.quotes.services.QuotationService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TransactionsJob extends AbstractJob {


    @Autowired
    private DeletedTransAuditRepo deletedTransAuditRepo;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private QuotTransRepo quotTransRepo;

    @Autowired
    private EndorseService endorseService;

    @Autowired
    private ParamService paramService;

    @Autowired
    private QuotationService quotationService;

    @Override
    public String getCronExpression() {
        String param = "0 0 0 * * ?";
        try {
            param = paramService.getParameterString("CLEAN_UP_JOB_CRON_EXPRESSION");
        } catch (BadRequestException e) {
        }

        return param;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Starting jobs....");
        String param = "N";
        try {
             param = paramService.getParameterString("CLEAN_UP_OLD_TRANS");
        } catch (BadRequestException e) {
        }
        if("Y".equalsIgnoreCase(param)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -7);
            Date dateBefore7Days = cal.getTime();
            Integer pageLimit = 200;
            Integer page = 0;
            int totalRecords = 0;
            do {

                PageRequest pageRequest = new PageRequest(0, pageLimit);
                Page<PolicyTrans> policyTransRepos = policyTransRepo.findAll(QPolicyTrans.policyTrans.authStatus.eq("D").and(QPolicyTrans.policyTrans.polCreateddt.before(dateBefore7Days)), pageRequest);
                if (policyTransRepos.getContent().size() > 0) {
                    Iterator<PolicyTrans> iterator = policyTransRepos.iterator();
                    while (iterator.hasNext()) {
                        PolicyTrans policyTrans = iterator.next();
                        Long count = quotTransRepo.count(QQuoteTrans.quoteTrans.convertedReference.policyId.eq(policyTrans.getPolicyId()));
                        if(count ==0) {
                            DeletedTransAudit deletedTransAudit = new DeletedTransAudit("Policy", policyTrans.getCreatedUser().getUsername(), policyTrans.getPolNo(), policyTrans.getPremium());
                            try {
                                endorseService.deletePolicyRecord(policyTrans.getPolicyId(), false);
                                deletedTransAuditRepo.save(deletedTransAudit);
                            } catch (BadRequestException e) {
                                System.out.println("Error deleting policy record ...");
                            }
                        }

                    }
                    page++;
                    totalRecords = policyTransRepos.getTotalPages();
                }
            } while (page < totalRecords);

            Integer pageLimits = 200;
            Integer pages = 0;
            int totalRecordss = 0;
            do {

                PageRequest pageRequest = new PageRequest(0, pageLimits);
                Page<QuoteTrans> policyTransRepos = quotTransRepo.findAll(QQuoteTrans.quoteTrans.quotStatus.eq("D").and(QQuoteTrans.quoteTrans.quotDate.before(dateBefore7Days)), pageRequest);
                if (policyTransRepos.getContent().size() > 0) {
                    Iterator<QuoteTrans> iterator = policyTransRepos.iterator();
                    while (iterator.hasNext()) {
                        QuoteTrans policyTrans = iterator.next();
                        DeletedTransAudit deletedTransAudit = new DeletedTransAudit("Quote", policyTrans.getPreparedBy().getUsername(), policyTrans.getQuotNo(), policyTrans.getPremium());
                        try {
                            quotationService.deleteQuote(policyTrans.getQuoteId());
                        } catch (BadRequestException e) {
                            throw new RuntimeException(e);
                        }
                        deletedTransAuditRepo.save(deletedTransAudit);

                    }

                    pages++;
                    totalRecordss = policyTransRepos.getTotalPages();
                }
            } while (pages < totalRecordss);
        }
    }
}
