package com.brokersystems.brokerapp.jobs;

import com.brokersystems.brokerapp.life.model.PolicyAcruals;
import com.brokersystems.brokerapp.life.model.QPolicyAcruals;
import com.brokersystems.brokerapp.life.repository.PolicyAccrualPayRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class AccrualPaymentNotificationJob extends AbstractJob{
    @Autowired
    private ParamService paramService;
    @Autowired
    private PolicyAccrualPayRepo policyAccrualPayRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Override
    public String getCronExpression() {
        String param = "0 0 8 ? * *";
        try {
            param = paramService.getParameterString("INSTALLMENT_ACCRUAL_JOB_CRON_EXPRESSION");
        } catch (BadRequestException ignored) {
        }

        return param;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String instDays = "30";
        String overDueDays = "30";
        int accrualThrottleDays = 7;

        try {
            instDays = paramService.getParameterString("POLICY_ACCRUAL_NOTIFICATION");
            overDueDays = paramService.getParameterString("POLICY_ACCRUAL_OVERDUE_NOTIFICATION");
            accrualThrottleDays = paramService.getParamInt("POLICY_ACCRUAL_NOTIFICATION_THROTTLE_DAYS");
        } catch (BadRequestException ignored) {
        }
        //due date
        Calendar instCal = Calendar.getInstance();
        instCal.setTime(new Date());
        instCal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(instDays));

        //overdue
        Calendar overDueCal = Calendar.getInstance();
        overDueCal.setTime(new Date());
        overDueCal.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(overDueDays));

        Date instDueDate = instCal.getTime();
        Date overdueDate = overDueCal.getTime();

        int pageLimit = 200;
        int page = 0;
        int totalRecords = 0;

        List<BigInteger> accrualPolicies = policyTransRepo.findNonBulkProcessPolicytrans();
        if(accrualPolicies != null ) {
          for(BigInteger policyTrans: accrualPolicies){
            if(policyAccrualPayRepo.findPolRcts(policyTrans.longValue()) != null ) {
                PolicyTrans policy = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(policyTrans.longValue()));

                //System.out.println("Accrual date"+policy.getAccrualInstDate()+" polId"+policyTrans);
                PolicyAcruals acrualsInstExists =  policyAccrualPayRepo.findBypolId(policyTrans.longValue());
                if(acrualsInstExists == null && policy.getAccrualInstDate() != null) {
                    PolicyAcruals policyAcruals = new PolicyAcruals();
                    policyAcruals.setAccrlPaid("N");
                    policyAcruals.setNotificationSent(false);
                    policyAcruals.setDueDate(policy.getAccrualInstDate());
                    policyAcruals.setPolicyTrans(policy.getPolicyId());
                    policyAccrualPayRepo.save(policyAcruals);
                }
            }
        }
      }

        do {
            PageRequest pageRequest = new PageRequest(page, pageLimit);
            //check for list of accrual that are have a due and overdue date and have a status of not paid
            Page<PolicyAcruals> dueAccruals = policyAccrualPayRepo.findAll(
                    QPolicyAcruals.policyAcruals.dueDate.between(overdueDate, instDueDate)
                            .and(QPolicyAcruals.policyAcruals.accrlPaid.equalsIgnoreCase("N")), pageRequest);


            //Page<PolicyAcruals> dueAccruals = policyAccrualPayRepo.findAll(QPolicyInstallments.policyInstallments.dueDate.loe(instDueDate).and(QPolicyInstallments.policyInstallments.installPaid.equalsIgnoreCase("N")).and(QPolicyInstallments.policyInstallments.notificationSent.isFalse()),pageRequest);
            if (dueAccruals.getTotalElements() > 0) {
                for (PolicyAcruals acrual : dueAccruals) {
                    PolicyTrans policyTrans = policyTransRepo.findByIdWithCreatedUser(acrual.getPolicyTrans());  //policyTransRepo.findOne(acrual.getPolicyTrans());

                    BigDecimal paidAmount = policyAccrualPayRepo.findPolRcts(policyTrans.getPolicyId());
                    //if basic prem is null don't send notification and is not authorized
                    if (policyTrans.getBasicPrem() != null && policyTrans.getAuthStatus().equalsIgnoreCase("A")) {

                        if (paidAmount.compareTo(policyTrans.getBasicPrem()) < 0) {
                            BigDecimal balance = policyTrans.getBasicPrem().subtract(paidAmount);
                            //handle subsequent notifications, send each mail after a given time frame
                            // after a mail was sent

                            boolean shouldNotify = false;
                            if (acrual.getLastNotificationDate() == null) {
                                shouldNotify = true;
                            } else {
                                Calendar lastNotifCal = Calendar.getInstance();
                                lastNotifCal.setTime(acrual.getLastNotificationDate());
                                lastNotifCal.add(Calendar.DAY_OF_MONTH, accrualThrottleDays);

                                if (new Date().after(lastNotifCal.getTime())) {
                                    shouldNotify = true;
                                }
                            }
                            if (shouldNotify) {
                                log.info("sending accrual notifications to policy maker {}", acrual.getDueDate());
                                //makerCheckerService.notifyClient(insuranceType, clientPhone, clientEmail, "Policy Accrual Due", instDays);
                                makerCheckerService.notifyPolicyMaker(policyTrans, "Policy Overdue Payments", String.valueOf(acrual.getDueDate()), balance, paidAmount);
                                acrual.setNotificationSent(true);
                                acrual.setLastNotificationDate(new Date());
                            }
                        } else {
                            //accrual has been paid
                            acrual.setAccrlPaid("Y");
                        }
                        policyAccrualPayRepo.save(acrual);
                    }
                }
                page++;
                totalRecords = dueAccruals.getTotalPages();
            }
        } while (page < totalRecords);
    }
}
