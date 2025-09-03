package com.brokersystems.brokerapp.jobs;

import com.brokersystems.brokerapp.audit.DeletedTransAudit;
import com.brokersystems.brokerapp.audit.repository.DeletedTransAuditRepo;
import com.brokersystems.brokerapp.claims.model.ClaimActivities;
import com.brokersystems.brokerapp.claims.model.QClaimActivities;
import com.brokersystems.brokerapp.claims.repository.ClmActivitiesRepo;
import com.brokersystems.brokerapp.quotes.model.QQuoteTrans;
import com.brokersystems.brokerapp.quotes.model.QuoteTrans;
import com.brokersystems.brokerapp.quotes.repository.QuotTransRepo;
import com.brokersystems.brokerapp.quotes.services.QuotationService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.TemplateMerger;
import com.brokersystems.brokerapp.setup.model.User;
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

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

@Component
public class ClaimsNotificationJob extends AbstractJob {

    @Autowired
    TemplateMerger templateMerger;


    @Autowired
    private ParamService paramService;

    @Autowired
    ClmActivitiesRepo clmActivitiesRepo;

    @Override
    public String getCronExpression() {
        String param = "0 00 06 ? * * *";
        try {
            param = paramService.getParameterString("CLAIMS_REMINDER_CRON_EXPRESSION");
        } catch (BadRequestException e) {
        }

        return param;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Starting Claims Reminder Job....");
        int param =-1;
        try {
            param = paramService.getParamInt("CLAIMS_REMINDER_DAYS");
        } catch (BadRequestException e) {
        }
        if(param>0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, param);
            Integer pageLimit = 200;
            Integer page = 0;
            int totalRecords = 0;
            do {
                PageRequest pageRequest = new PageRequest(0, pageLimit);
                Page<ClaimActivities> claimActivities = clmActivitiesRepo.findAll(QClaimActivities.claimActivities.activityDate.isNotNull()
                        .and(QClaimActivities.claimActivities.currentActivity.eq("Y"))
                        .and(QClaimActivities.claimActivities.reviewUser.isNotNull())
                        .and(QClaimActivities.claimActivities.activityDate.loe(calendar.getTime())), pageRequest);

                System.out.println("Date  >>>>>> " + calendar.getTime());
                System.out.println("claimActivities >>>>>> " + claimActivities);
                if (claimActivities.getContent().size() > 0) {
                    Iterator<ClaimActivities> iterator = claimActivities.iterator();
                    while (iterator.hasNext()) {
                        ClaimActivities activities = iterator.next();

                        try {
                            templateMerger.sendClaimReminders(activities);
                        } catch (BadRequestException e) {
                            System.out.println("Error Occured when Sending the Claim Reminder " + e.getMessage());
                        }
                    }
                    page++;
                    totalRecords = claimActivities.getTotalPages();
                }
            } while (page < totalRecords);
        }

    }
}
