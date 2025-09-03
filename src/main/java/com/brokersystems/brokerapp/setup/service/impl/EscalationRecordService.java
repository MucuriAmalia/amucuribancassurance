package com.brokersystems.brokerapp.setup.service.impl;

import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.mail.model.SendEmailPublisherBean;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.HolidayUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EscalationRecordService {

    @Autowired
    private EscalationRecordRepository escalationRecordRepository;

    @Autowired
    private TaskEscalationConfigRepository configRepository;

    @Autowired
    private EscalationLevelRepository escalationLevelRepository;

    @Autowired
    private UserBranchesRepository userBranchesRepository;

    @Autowired
    private MakerCheckerRepo makerCheckerRepo;

    @Autowired
    private EscalationActivityLoggerRepository activityLoggerRepository;
    @Autowired
    private SendEmailPublisherBean sendEmailPublisherBean;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PolicyTransRepo policyTransRepo;

    public EscalationRecord saveOrUpdateEscalationRecord(EscalationRecord record) {
        if (record.getEscId() != null) {  // Check if the record already has an ID
            // Find the existing record
            Optional<EscalationRecord> existingRecordOpt = Optional.ofNullable(escalationRecordRepository.findOne(record.getEscId()));

            if (existingRecordOpt.isPresent()) {
                EscalationRecord existingRecord = existingRecordOpt.get();

                // Copy properties from the incoming record to the existing record, excluding "escId" to avoid overwriting it
                BeanUtils.copyProperties(record, existingRecord, "escId");

                // Save the updated record
                return escalationRecordRepository.save(existingRecord);
            }
        }

        // If the record does not have an ID, or it doesn't exist in the database, create a new one
        return escalationRecordRepository.save(record);
    }




    // Get all pending escalations for a task
    public List<EscalationRecord> getPendingEscalationsForTask(MakerChecker task) {
        System.out.println("Pending Escalations: " + escalationRecordRepository.findByTask(task));
        return escalationRecordRepository.findByTask(task);
    }

//    public void handleOverdueEscalations(String taskType) {
//        // Fetch escalation configuration for the given task type
//        TaskEscalationConfig config = configRepository.findByTaskType(taskType);
//        if (config == null) {
//            System.out.println("No record found for task type: " + taskType);
//            return;
//        }
//
//        // Retrieve the allowed time (in minutes) from the config
//        int allowedTimeMinutes = config.getTimeLimitPerUser();
//
//        // Fetch escalation records with checkTime as null (pending) and calculate overdue status
//        List<EscalationRecord> overdueRecords = escalationRecordRepository.findByCheckTimeIsNull();
//
//        if (overdueRecords.isEmpty()) {
//            System.out.println("No overdue tasks for escalation.");
//            return;
//        }
//
//        for (EscalationRecord record : overdueRecords) {
//            // Calculate the difference between current time and the made time (in minutes)
//            long minutesPassed = (System.currentTimeMillis() - record.getMadeTime().getTime()) / (1000 * 60);
//
//            // Check if the time passed exceeds the allowed threshold
//            if (minutesPassed > allowedTimeMinutes) {
//                System.out.println("Task " + record.getTask().getId() + " is overdue by " + minutesPassed + " minutes.");
//
//                // Further escalation logic (find next user, notify, etc.)
//                EscalationLevel currentLevel = record.getEscalationLevel();
//                OrgBranch branch = getBranchForTask(record.getTask().getId());
//                EscalationLevel nextLevel = escalationLevelRepository.findFirstByHierarchyLevelGreaterThanOrderByHierarchyLevelAsc(
//                        currentLevel.getHierarchyLevel());
//
//                if (nextLevel == null) {
//                    System.out.println("Task " + record.getTask().getId() + " has reached the highest escalation level.");
//                    continue;
//                }
//
//                User nextUser = findUserForEscalation(nextLevel, branch);
//                System.out.println("Next User: "+ nextUser + "Next Level: "+ nextLevel);
//                if (nextUser == null) {
//                    System.out.println("No user found at the next escalation level for branch: " + branch.getObName());
//                    continue;
//                }
//                            // Update the escalation record to assign it to the next level
//            record.setEscalationLevel(nextLevel);
//            escalationRecordRepository.save(record);
//
//            EscalationActivityLogger escalationRecord = new EscalationActivityLogger();
//            escalationRecord.setTask(record.getTask());
//            escalationRecord.setEscalationLevel(nextLevel);
//            escalationRecord.setActivityTime(new Date());
//            escalationRecord.setComments("Line supervisor/manager notified");
//            escalationRecord.setUser(findUserForEscalation(nextLevel, branch));
//            activityLoggerRepository.save(escalationRecord);
//
//                // Notify the next officer
//            notifyOfficer(nextLevel, record, branch, nextUser);
//            }
//        }
//    }
    @Scheduled(fixedRate = 5 * 60 * 1000) // Every 6 minutes
    public void checkAndHandleOverdueTasks(String taskType) {
        System.out.println("Running overdue task escalation check...");

        if(HolidayUtils.isHolidayOrWeekend(LocalDate.now())){
            //Syste//out.println("Not handling escalation on non-working days.");
            return;
        }

        TaskEscalationConfig config = configRepository.findByTaskType(taskType);
        if (config == null) {
            System.out.println("No configuration found for task type: " + taskType);
            return;
        }
        // Retrieve all overdue tasks where check time is still null
        // TO IMPLEMENT DYNAMIC TIME FROM TASK CONFIG REPOSITORY ONCE SCHEDULED ON THE SYSTEM
        Date cutoffTime = new Date(System.currentTimeMillis() - config.getTimeLimitPerUser() * 60 * 1000); // 5 minutes ago
        List<EscalationRecord> overdueRecords = escalationRecordRepository.findByCheckTimeIsNullAndMadeTimeBefore(cutoffTime);

        if (!overdueRecords.isEmpty()) {
            for (EscalationRecord record : overdueRecords) {
              //  System.out.println("Handling escalations ");
                handleEscalationForRecord(record);
            }
        }

        //Check for overdue transactions
        Date cutoffInitiationTime = new Date(System.currentTimeMillis() - config.getTimeLimitPerTransaction() * 60 * 1000);
        List <EscalationRecord> overdueTransactions = escalationRecordRepository.findByCheckTimeIsNullAndTaskInitiationTimeBefore(cutoffInitiationTime);

        if (!overdueTransactions.isEmpty()) {
            for (EscalationRecord overdueTrans : overdueTransactions) {
                MakerChecker taskCheck = overdueTrans.getTask();
               // System.out.println("Transaction " + overdueTrans.getTask().getTaskName() + " has surpassed its allocated time");
                if (handleOverdueTransactions(overdueTrans) != null && taskCheck.getTaskOverdue() == null) {
                    taskCheck.setTaskOverdue("YES");
                    makerCheckerRepo.save(taskCheck);
                    System.out.println("Found new overdue task " + taskCheck.getTaskName());
                    EscalationActivityLogger overdueTransactionRecord = new EscalationActivityLogger();
                    overdueTransactionRecord.setTask(overdueTrans.getTask());
                    overdueTransactionRecord.setEscalationLevel(overdueTrans.getCurrentEscalationLevel());
                    overdueTransactionRecord.setUser(overdueTrans.getOwner());
                    overdueTransactionRecord.setActivityTime(new Date());
                    overdueTransactionRecord.setComments("Task has surpassed allocated time before verification");
                    overdueTransactionRecord.setSystemAction("Overall TAT overdue");
                    activityLoggerRepository.save(overdueTransactionRecord);
                }

            }
        }

    }


    private EscalationRecord handleEscalationForRecord(EscalationRecord record) {
        try {
            long minutesPassed = (System.currentTimeMillis() - record.getMadeTime().getTime()) / (1000 * 60);
            int allowedTimeMinutes = getAllowedTimeForTaskType(record.getTask().getTaskType()); // Fetch the threshold for the task type

            EscalationLevel currentLevel = record.getCurrentEscalationLevel();
            OrgBranch branch = getBranchForTask(record.getTask().getId());
            EscalationLevel nextLevel = escalationLevelRepository.findFirstByHierarchyLevelGreaterThanOrderByHierarchyLevelAsc(
                    currentLevel.getHierarchyLevel());
            if (nextLevel != null) {
                User nextUser = findUserForEscalation(nextLevel, branch);
                if (minutesPassed > allowedTimeMinutes && nextUser != null && nextLevel != null) {
                    // Update the existing escalation record
                    record.setMadeTime(new Date());
                    record.setCurrentEscalationLevel(nextLevel);
                    List<Long> previousLevels;
                    if (record.getPreviousLevels() != null) {
                        previousLevels = objectMapper.readValue(record.getPreviousLevels(), new TypeReference<List<Long>>() {
                        });
                    } else {
                        previousLevels = new ArrayList<>();
                    }
                    previousLevels.add(currentLevel.getEscMatrixId());
                    record.setPreviousLevels(previousLevels.toString());
                    escalationRecordRepository.save(record);

                    // Log the escalation activity
                    EscalationActivityLogger escalationActivity = new EscalationActivityLogger();
                    escalationActivity.setTask(record.getTask());
                    escalationActivity.setEscalationLevel(nextLevel);
                    escalationActivity.setActivityTime(new Date());
                    escalationActivity.setComments("Line supervisor/manager notified");
                    escalationActivity.setSystemAction("Task escalated");
                    escalationActivity.setUser(nextUser);
                    activityLoggerRepository.save(escalationActivity);
                    System.out.println("task escalation notification being sent her ");
                    // Notify the next officer
                    notifyOfficer(record, branch);
                    return record;
                }
            }
        } catch (Exception e) {
            //System.err.println("Error handling escalation for task " + record.getTask().getId() + ": " + e.getMessage());
            //e.printStackTrace();
        }
        return null;
    }

    private EscalationRecord handleOverdueTransactions(EscalationRecord overdueTransaction){
        long minutespassed = (System.currentTimeMillis() - overdueTransaction.getTaskInitiationTime().getTime()) / (1000 * 60);
        int allowedminutes = getAllowedTimeForTransactionTaskType(overdueTransaction.getTask().getTaskType());
        if (minutespassed > allowedminutes){
            return  overdueTransaction;
        } else{
            return null;
        }

    }

    private int getAllowedTimeForTaskType(String taskType) {
        TaskEscalationConfig config = configRepository.findByTaskType(taskType);
        return (config != null) ? config.getTimeLimitPerUser() : 0; // Default to 0 if not found
    }

    private int getAllowedTimeForTransactionTaskType(String taskType) {
        TaskEscalationConfig config = configRepository.findByTaskType(taskType);
        return (config != null) ? config.getTimeLimitPerTransaction() : 0; // Default to 0 if not found
    }


    /**
     * Finds the branch for a specific task based on its associated user.
     *
     * @param taskId The task ID.
     * @return The branch associated with the task.
     */
    private OrgBranch getBranchForTask(Long taskId) {
        MakerChecker makerChecker = makerCheckerRepo.findOne(taskId);
        if (makerChecker == null) {
            throw new RuntimeException("MakerChecker record not found for task ID: " + taskId);
        }

        User user = makerChecker.getMakerId();
        if (user == null) {
            throw new RuntimeException("No user associated with MakerChecker record for task ID: " + taskId);
        }

        UserBranches userBranch = userBranchesRepository.findByUser(user);
        if (userBranch == null || userBranch.getBranch() == null) {
            throw new RuntimeException("No branch found for user: " + user.getUsername());
        }

        return userBranch.getBranch();
    }

    /**
     * Finds the next user responsible for a specific escalation level and branch.
     *
     * @param escalationLevel The escalation level.
     * @param branch The branch.
     * @return The next responsible user, or null if no user is found.
     */
    private User findUserForEscalation(EscalationLevel escalationLevel, OrgBranch branch) {
        if (escalationLevel == null || escalationLevel.getHierarchyLevel() == null) {
            System.err.println("Escalation Level or Hierarchy Level is null. Level: " + escalationLevel);
            return null;
        }

        List<UserBranches> userBranches = userBranchesRepository.findByBranch(branch);

        if (userBranches == null || userBranches.isEmpty()) {
            System.err.println("No users found in branch: " + branch.getObName());
            return null;
        }

        return userBranches.stream()
                .map(UserBranches::getUser)
                .filter(user -> user != null && user.getEscalationLevel() != null)
                .filter(user -> user.getEscalationLevel().getHierarchyLevel().equals(escalationLevel.getHierarchyLevel()))
                .findFirst()
                .orElse(null);
    }



    /**
     * Notify the officer about the escalation.
     * @param record          The escalation record.
     * @param branch          The branch.
     */
    private void notifyOfficer(EscalationRecord record, OrgBranch branch) throws JsonProcessingException {
        MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.id.eq(record.getTask().getId()));
        PolicyTrans policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(makerChecker.getPolicyId()));
        String polNo = policyTrans.getPolNo();
        String subject = "Task Escalation Notification policy "+polNo;
        String insuranceType = policyTrans.getBinder().getBinName();
        String clientName = String.format("%s %s",policyTrans.getClient().getFname(),policyTrans.getClient().getOtherNames());
        String staffName = makerChecker.getMakerId().getUsername();
        String notificationMessage = String.format(
                "Dear Team, <br> this is to notify you that the %s application policy %S for customer %s, originated by %s, has breached the TAT. Kindly follow up for action.",
                insuranceType,
                polNo,
                clientName,
                staffName
        );

        // Implement your notification logic here
        System.out.println(notificationMessage);

        final MailMessageBean messageBean = new MailMessageBean();
        messageBean.setMessage(notificationMessage);
        Iterable<User> currentLevelUsers = userRepository.findAll(QUser.user.escalationLevel.escMatrixId.eq(record.getCurrentEscalationLevel().getEscMatrixId()));
        List<String> emailSendTo = new ArrayList<>();
        for (User user : currentLevelUsers) {
            emailSendTo.add(user.getEmail());
        }
        messageBean.setSendTo(String.join(", ", emailSendTo));
        if (record.getPreviousLevels() != null) {
            List<String> emailSendCC = new ArrayList<>();
            for (Long level : objectMapper.readValue(record.getPreviousLevels(), new TypeReference<List<Long>>() {})) {
                Iterable<User> previousLevelUsers = userRepository.findAll(QUser.user.escalationLevel.escMatrixId.eq(level));
                if (previousLevelUsers.iterator().hasNext()) {
                    for (User levelUser : previousLevelUsers) {
                        emailSendCC.add(levelUser.getEmail());
                    }
                }
            }
            if (!emailSendCC.isEmpty()) {
                messageBean.setSendCC(String.join(", ", emailSendCC));
            }
        }
        messageBean.setSubject(subject);
        System.out.println(new Gson().toJson(messageBean));
        sendEmailPublisherBean.sendEmail(messageBean);
    }

//    public void handleOverdueEscalations(String taskType) {
//        // Fetch escalation configuration for the given task type
//        TaskEscalationConfig config = configRepository.findByTaskType(taskType);
//        if (config == null) {
//            System.out.println("No record found for task type: " + taskType);
//            return;
//        }
//
//        // Calculate the cutoff time based on the per-user time limit
//        Date cutoffTime = new Date(System.currentTimeMillis() - config.getTimeLimitPerUser() * 60 * 1000);
//
//        // Fetch overdue escalation records
//        List<EscalationRecord> overdueRecords = escalationRecordRepository.findByCheckTimeIsNullAndMadeTimeBefore(cutoffTime);
//        if (overdueRecords.isEmpty()) {
//            System.out.println("No overdue tasks for escalation.");
//            return;
//        }
//
//        for (EscalationRecord record : overdueRecords) {
//            // Get current escalation level and branch
//            EscalationLevel currentLevel = record.getEscalationLevel();
//            OrgBranch branch = getBranchForTask(record.getTask().getId());
//
//            // Find the next escalation level and user within the same branch
//            EscalationLevel nextLevel = escalationLevelRepository.findFirstByHierarchyLevelGreaterThanAndBranchOrderByHierarchyLevelAsc(
//                    currentLevel.getHierarchyLevel(), branch);
//
//            if (nextLevel == null) {
//                System.out.println("Task " + record.getTask().getId() + " has reached the highest escalation level in the branch.");
//                continue;
//            }
//
////            // Update the escalation record to assign it to the next level
////            record.setEscalationLevel(nextLevel);
////            record.setTask(record.getTask());
////            record.setVerifier(findUserForEscalation(nextLevel, branch));
////            record.setMadeTime(new Date()); // Reset made time for the next escalation
////            escalationRecordRepository.save(record);
//            EscalationActivityLogger escalationRecord = new EscalationActivityLogger();
//            escalationRecord.setTask(record.getTask());
//            escalationRecord.setEscalationLevel(nextLevel);
//            escalationRecord.setActivityTime(new Date());
//            escalationRecord.setComments("Line supervisor/manager notified");
//            escalationRecord.setUser(findUserForEscalation(nextLevel, branch));
//            // Notify the next officer
//            notifyOfficer(nextLevel, record, branch);
//        }
//    }
//
//    /**
//     * Finds the branch for a specific task based on its associated user.
//     *
//     * @param taskId The task ID.
//     * @return The branch associated with the task.
//     */
//    private OrgBranch getBranchForTask(Long taskId) {
//        MakerChecker makerChecker = makerCheckerRepo.findOne(taskId);
//        User user = makerCheckerRepo.findByMakerId(makerChecker);
//        UserBranches userBranch = userBranchesRepository.findByUser(user);
//        return userBranch.getBranch();
//    }
//
//    /**
//     * Finds the next user responsible for a specific escalation level and branch.
//     *
//     * @param escalationLevel The escalation level.
//     * @param branch The branch.
//     * @return The next responsible user, or null if no user is found.
//     */
//    private User findUserForEscalation(EscalationLevel escalationLevel, OrgBranch branch) {
//        List<UserBranches> userBranches = userBranchesRepository.findByBranchAndUser_EscalationLevel(branch, escalationLevel.getHierarchyLevel());
//
//        return userBranches.stream()
//                .filter(ub -> ub.getUser().getEscalationLevel().equals(escalationLevel.getHierarchyLevel()))
//                .map(UserBranches::getUser)
//                .findFirst()
//                .orElse(null); // Return null if no user is found
//    }
//
//    /**
//     * Notify the officer about the escalation.
//     *
//     * @param escalationLevel The next escalation level.
//     * @param record The escalation record.
//     * @param branch The branch.
//     */
//    private void notifyOfficer(EscalationLevel escalationLevel, EscalationRecord record, OrgBranch branch) {
//        String notificationMessage = String.format(
//                "Task %s has been escalated to %s (Level %d) in branch %s.",
//                record.getTask().getId(),
//                escalationLevel.getRoleName(),
//                escalationLevel.getHierarchyLevel(),
//                branch.getObName()
//        );
//        // Implement your notification logic here
//        System.out.println(notificationMessage);
//    }



    // Get overdue escalations based on config
    public List<EscalationRecord> getOverdueEscalations(String taskType) {
        TaskEscalationConfig config = configRepository.findByTaskType(taskType);
        System.out.println(taskType);
        if (config == null) {
            System.out.println("No record found");
            return Collections.emptyList();
        }

        // Calculate cutoff time using LocalDateTime
        LocalDateTime localCutoffTime = LocalDateTime.now().minusMinutes(config.getTimeLimitPerUser());

        // Convert LocalDateTime to java.util.Date
        Date cutoffTime = Timestamp.valueOf(localCutoffTime);

        System.out.println("Tasks to be escalated Escalations: "
                + escalationRecordRepository.findByCheckTimeIsNullAndMadeTimeBefore(cutoffTime));

        return escalationRecordRepository.findByCheckTimeIsNullAndMadeTimeBefore(cutoffTime);
    }

    // Mark escalation as checked
    public EscalationRecord markAsChecked(Long escId) {
        EscalationRecord record = escalationRecordRepository.findOne(escId);
        record.setCheckTime(new Date());
        return escalationRecordRepository.save(record);
    }
    public List<EscalationRecord> getEscalationRecordsByBranchAndDateRange(Long branchId, Date startDate, Date endDate) {
        System.out.println("From service: Branch: " + branchId + " start: " +startDate+ " end: " + endDate);
        return escalationRecordRepository.findEscalationRecordsByBranchAndDateRange(branchId, startDate, endDate);
    }

}

