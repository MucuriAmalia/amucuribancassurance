package com.brokersystems.brokerapp.jobs;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class RenewalNotificationJob extends AbstractJob {
    @Autowired
    private ParamService paramService;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;

    @Override
    public String getCronExpression() {
        String param = "0 0 0 * * ?";
        try {
            param = paramService.getParameterString("RENEWAL_NOTIFICATION_JOB_CRON_EXPRESSION");
        } catch (BadRequestException ignored) {
        }

        return param;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String renewalDays = "5";
        try {
            renewalDays = paramService.getParameterString("POLICY_RENEWAL_NOTIFICATION");
        } catch (BadRequestException ignored) {
        }
        Calendar renewalCal = Calendar.getInstance();
        renewalCal.setTime(new Date());
        renewalCal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(renewalDays));
        Date renewalDueDate = renewalCal.getTime();
        int pageLimit = 200;
        int page = 0;
        int totalRecords = 0;
        do {
            PageRequest pageRequest = new PageRequest(page, pageLimit);
            Page<PolicyTrans> dueRenewals = policyTransRepo.findAll(QPolicyTrans.policyTrans.renewable.isTrue().and(QPolicyTrans.policyTrans.renewed.isFalse()).and(QPolicyTrans.policyTrans.renewalDate.loe(renewalDueDate)).and(QPolicyTrans.policyTrans.notificationSent.isFalse()), pageRequest);
            if (dueRenewals.getTotalElements() > 0) {
                for (PolicyTrans policyTrans : dueRenewals) {
                    String insuranceType = policyTrans.getBinder().getBinName();
                    String clientPhone = (policyTrans.getClient().getPhonePrefix() != null ? policyTrans.getClient().getPhonePrefix().getPrefixName() : "254") + policyTrans.getClient().getPhoneNo();
                    String clientEmail = policyTrans.getClient().getEmailAddress();
                    policyTrans.setNotificationSent(true);
                    try {
                        makerCheckerService.notifyClient(policyTrans, insuranceType, clientPhone, clientEmail, "Policy Renewal Due", renewalDays);
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                    policyTransRepo.save(policyTrans);
                }
                page++;
                totalRecords = dueRenewals.getTotalPages();
            }
        } while (page < totalRecords);
    }
}
