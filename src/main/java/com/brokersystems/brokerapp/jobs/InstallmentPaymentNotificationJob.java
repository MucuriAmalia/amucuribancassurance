package com.brokersystems.brokerapp.jobs;

import com.brokersystems.brokerapp.life.model.PolicyInstallments;
import com.brokersystems.brokerapp.life.model.QPolicyInstallments;
import com.brokersystems.brokerapp.life.repository.PolicyInstallmentsRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class InstallmentPaymentNotificationJob extends AbstractJob{
    @Autowired
    private ParamService paramService;
    @Autowired
    private PolicyInstallmentsRepo policyInstallmentsRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;

    @Override
    public String getCronExpression() {
        String param = "0 0 0 * * ?";
        try {
            param = paramService.getParameterString("INSTALLMENT_NOTIFICATION_JOB_CRON_EXPRESSION");
        } catch (BadRequestException ignored) {
        }

        return param;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String instDays = "30";
        try {
            instDays = paramService.getParameterString("POLICY_INSTALLMENT_NOTIFICATION");
        } catch (BadRequestException ignored) {
        }
        Calendar instCal = Calendar.getInstance();
        instCal.setTime(new Date());
        instCal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(instDays));
        Date instDueDate = instCal.getTime();
        int pageLimit = 200;
        int page = 0;
        int totalRecords = 0;
        do {
            PageRequest pageRequest = new PageRequest(page, pageLimit);
            Page<PolicyInstallments> dueInstallments = policyInstallmentsRepo.findAll(QPolicyInstallments.policyInstallments.dueDate.loe(instDueDate).and(QPolicyInstallments.policyInstallments.installPaid.equalsIgnoreCase("N")).and(QPolicyInstallments.policyInstallments.notificationSent.isFalse()),pageRequest);
            if (dueInstallments.getTotalElements() > 0) {
                for (PolicyInstallments installment : dueInstallments) {
                    PolicyTrans policyTrans = installment.getPolicyTrans();
                    String insuranceType = policyTrans.getBinder().getBinName();
                    String clientPhone = (policyTrans.getClient().getPhonePrefix() != null ? policyTrans.getClient().getPhonePrefix().getPrefixName() : "254") + policyTrans.getClient().getPhoneNo();
                    String clientEmail = policyTrans.getClient().getEmailAddress();
                    installment.setNotificationSent(true);
                    try {
                        makerCheckerService.notifyClient(policyTrans, insuranceType, clientPhone, clientEmail, "Policy Installment Due", instDays);
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                    policyInstallmentsRepo.save(installment);
                }
                page++;
                totalRecords = dueInstallments.getTotalPages();
            }
        } while (page < totalRecords);
    }
}
