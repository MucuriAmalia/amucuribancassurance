package com.brokersystems.brokerapp.jobs;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.QUser;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.setup.service.impl.EscalationRecordService;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskEscalationJob extends AbstractJob{

    @Autowired
    private ParamService paramService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private EscalationRecordService escalationRecordService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public String getCronExpression() {
        String param = "0 0/20 * * * ?";
        try {
            param = paramService.getParameterString("ESCALATION_JOB_CRON_EXPRESSION");
        } catch (BadRequestException ignored) {
        }

        return param;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Iterable<User> checkers = userRepository.findAll(QUser.user.enabled.eq("1"));
        if (checkers != null) {
            for (User checker : checkers) {
                List<Object[]> ticketsList = makerCheckerRepo.findAllPendingTasks(checker.getId().toString());
                if (!ticketsList.isEmpty()) {
                    for (Object[] ticket : ticketsList) {
                        escalationRecordService.checkAndHandleOverdueTasks((String) ticket[5]);
                    }
                }
            }
        }
    }
}
