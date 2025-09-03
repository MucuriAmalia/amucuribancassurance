package com.brokersystems.brokerapp.jobs;

import com.brokersystems.brokerapp.jobs.jobs.receipttracker.ReceiptStatus;
import com.brokersystems.brokerapp.jobs.jobs.receipttracker.ReceiptTracker;
import com.brokersystems.brokerapp.jobs.jobs.receipttracker.ReceiptTrackerService;
import com.brokersystems.brokerapp.quotes.repository.QuotTransRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.sftp.SftpUtilFCR;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsTempRepo;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.brokersystems.brokerapp.jobs.jobs.receipttracker.ReceiptTracker.buildTracker;

@Component
public class EodPaymentsProcessingJob extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(EodPaymentsProcessingJob.class);

    @Autowired
    private ParamService paramService;

    @Autowired
    private ReceiptTrackerService receiptTrackerService;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Value("${ec2.host:10.240.235.23}")
    private String SFTP_HOST;

    @Value("${ec2.user:ubuntu}")
    private String SFTP_USER;

    @Value("${ssh.port:22}")
    private String SSH_PORT;

    @Value("${ec2.remote.path.fcr:/home/ubuntu/fcr_files}")
    private String REMOTE_DIR;

    @Value("${ec2.remote.path.fcr.success:/home/ubuntu/fcr_files_success}")
    private String REMOTE_DIR_SUCCESS;

    @Value("${ec2.remote.path.fcr.failed:/home/ubuntu/fcr_files_failed}")
    private String REMOTE_DIR_FAILURE;

    @Autowired
    private SftpUtilFCR sftpUtil;

    private static final String LOCAL_DIR = System.getProperty("user.home");

    @Autowired
    private QuotTransRepo quotTransRepo;

    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;

    @Autowired
    private SystemTransactionsTempRepo systemTransactionsTempRepo;

    @Autowired
    private ReceiptService receiptService;

    @Override
    public String getCronExpression() {
        String param = "0 */5 * * * ?";
        try {
            param = paramService.getParameterString("EOD_FCR_PAYMENTS_JOB_CRON_EXPRESSION");
        } catch (BadRequestException ignored) {
            logger.warn("Failed to load cron expression, using default: {}", param);
        }
        return param;
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            if (StringUtils.isAnyBlank(SFTP_HOST, SFTP_USER, SSH_PORT, REMOTE_DIR, REMOTE_DIR_SUCCESS, REMOTE_DIR_FAILURE)) {
                throw new JobExecutionException("Missing required SFTP configuration properties");
            }

            sftpUtil.authWithKey();

            String latestFile = sftpUtil.findLatestEodFile();

            if (latestFile != null) {
                Files.createDirectories(Paths.get(LOCAL_DIR));
                String localFilePath = sftpUtil.downloadFile(REMOTE_DIR + "/" + latestFile, LOCAL_DIR + File.separator + latestFile);

                String successTrackerFilePath = getTrackerFilePath(latestFile, true);
                String failureTrackerFilePath = getTrackerFilePath(latestFile, false);
                Set<String> processedRefs = loadProcessedReferences(successTrackerFilePath, failureTrackerFilePath);
                logger.info("Loaded {} processed transaction references from tracker files: {}, {}",
                        processedRefs.size(), successTrackerFilePath, failureTrackerFilePath);

                List<Map<String, String>> transactions = parseFile(localFilePath, processedRefs);
                logger.info("Found {} unprocessed transactions from file: {}", transactions.size(), latestFile);

                boolean isSuccess = processTransactions(transactions, successTrackerFilePath, failureTrackerFilePath, latestFile);

                try {
                    Files.deleteIfExists(Paths.get(localFilePath));
                    logger.info("Deleted local file: {}", localFilePath);
                } catch (IOException e) {
                    logger.warn("Failed to delete local file {}: {}", localFilePath, e.getMessage());
                }
            } else {
                logger.info("No file found for processing in directory: {}", REMOTE_DIR);
            }
        } catch (JSchException | SftpException | IOException | BadRequestException e) {
            logger.error("Error processing EOD payments: {}", e.getMessage(), e);
            throw new JobExecutionException("Error processing EOD payments: " + e.getMessage(), e);
        } finally {
            try {
                sftpUtil.close();
            } catch (Exception e) {
                logger.warn("Failed to close SFTP connection: {}", e.getMessage());
            }
        }
    }

    private String getTrackerFilePath(String fileName, boolean isSuccess) {
        String date = fileName.replace("EXT_EOD_BANCA_ACCT_TXNS_", "");
        String suffix = isSuccess ? "success" : "failed";
        return LOCAL_DIR + File.separator + "processed_transactions_" + suffix + "_" + date + ".txt";
    }

    private Set<String> loadProcessedReferences(String successTrackerFilePath, String failureTrackerFilePath) throws IOException {
        Set<String> processedRefs = new HashSet<>();
        for (String trackerFilePath : Arrays.asList(successTrackerFilePath, failureTrackerFilePath)) {
            File trackerFile = new File(trackerFilePath);
            if (trackerFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(trackerFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty()) {
                            processedRefs.add(line);
                        }
                    }
                }
            } else {
                try {
                    Files.createFile(Paths.get(trackerFilePath));
                    logger.info("Created new tracker file: {}", trackerFilePath);
                } catch (IOException e) {
                    logger.warn("Failed to create tracker file {}: {}", trackerFilePath, e.getMessage());
                }
            }
        }
        return processedRefs;
    }

    private void appendToTrackerFile(String trackerFilePath, String transRef) throws IOException {
        File trackerFile = new File(trackerFilePath);
        if (!trackerFile.exists()) {
            Files.createFile(Paths.get(trackerFilePath));
            logger.info("Created new tracker file: {}", trackerFilePath);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(trackerFilePath, true))) {
            writer.write(transRef);
            writer.newLine();
        }
    }

    public List<Map<String, String>> parseFile(String filePath, Set<String> processdeRefs) throws IOException {
        List<Map<String, String>> transactions = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] fields = line.trim().split("\\|", -1);
                logger.debug("Fields count: {}", fields.length);

                // Skip header line by checking if the first field is not a valid date or matches known header text
                if (isFirstLine) {
                    isFirstLine = false;
                    if (fields.length == 17 && (fields[0].trim().equalsIgnoreCase("TRANSACTION DATE") || !isValidDate(fields[0].trim(), dateFormat))) {
                        logger.info("Skipping header line: {}", line);
                        continue;
                    }
                }

                if (fields.length == 17) {
                    Map<String, String> transaction = new HashMap<>();
                    transaction.put("TransactionDate", fields[0].trim());
                    transaction.put("TransactionBranch", fields[1].trim());
                    transaction.put("UserNo", fields[2].trim());
                    transaction.put("AccountNumber", fields[3].trim());
                    transaction.put("TransactionDescription", fields[4].trim());
                    transaction.put("ValueDate", fields[5].trim());
                    transaction.put("ChequeNo", fields[6].trim());
                    transaction.put("DebitCredit", fields[7].trim());
                    transaction.put("MnemonicCode", fields[8].trim());
                    transaction.put("TransactionLiteral", fields[9].trim());
                    transaction.put("TransactionAmount", fields[10].trim());
                    transaction.put("PostingDate", fields[11].trim());
                    transaction.put("AmountTransactionCurrency", fields[12].trim());
                    transaction.put("TransactionCurrency", fields[13].trim());
                    transaction.put("TransactionReferenceNo", fields[14].trim());
                    transaction.put("UserReferenceNo", fields[15].trim());
                    transaction.put("UserDescription", fields[16].trim());

                    String transRef = transaction.get("TransactionReferenceNo");
                    if (StringUtils.isNotBlank(transRef)) {
                        transactions.add(transaction);
                    } else {
                        logger.debug("Skipping already processed or invalid transaction reference: {}", transRef);
                    }
                } else {
                    logger.warn("Skipping line with incorrect number of fields (expected 17, got {}): {}", fields.length, line);
                }
            }
        }
        logger.info("Total unprocessed transactions parsed: {}", transactions.size());
        return transactions;
    }

    private boolean isValidDate(String dateStr, SimpleDateFormat dateFormat) {
        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean processTransactions(List<Map<String, String>> transactions, String successTrackerFilePath, String failureTrackerFilePath, String fileName) throws BadRequestException, IOException {

        List<ReceiptTracker> receiptTrackers = new ArrayList<>();
        List<Map<String, String>> unsuccessfulTransactions = new ArrayList<>();

        for (Map<String, String> txn : transactions) {
            String transRef = null;
            try {
                String transReference = txn.get("UserDescription") != null ? txn.get("UserDescription") : "";
                transRef = StringUtils.isNotBlank(transReference) && transReference.contains(";") ? transReference.split(";")[0] : transReference;
                if (StringUtils.isBlank(transRef)) {
                    unsuccessfulTransactions.add(txn);
                    appendToTrackerFile(failureTrackerFilePath, txn.get("TransactionReferenceNo"));
                    logger.warn("Empty transaction reference, skipping transaction in file {}, ref: {}", fileName, txn.get("TransactionReferenceNo"));
                    continue;
                }

                logger.info("Processing transaction for reference: {} in file: {}", transRef, fileName);
                ReceiptTrans receiptTrans = new ReceiptTrans();
                PolicyTrans policyTrans;
                BigDecimal rctAmount;
                try {
                    String amountStr = txn.get("TransactionAmount").contains(",") ? txn.get("TransactionAmount").replace(",", "") : txn.get("TransactionAmount");
                    rctAmount = new BigDecimal(amountStr);
                    logger.info("Transaction amount: {}", rctAmount);
                } catch (NumberFormatException e) {
                    unsuccessfulTransactions.add(txn);
                    appendToTrackerFile(failureTrackerFilePath, txn.get("TransactionReferenceNo"));
                    logger.error("Invalid transaction amount format: {}, skipping transaction in file {}, ref: {}", txn.get("TransactionAmount"), fileName, txn.get("TransactionReferenceNo"));
                    continue;
                }

                policyTrans = getPolicyTrans(transRef, policyTransRepo);
                if (policyTrans == null) {
                    unsuccessfulTransactions.add(txn);
                    appendToTrackerFile(failureTrackerFilePath, txn.get("TransactionReferenceNo"));
                    logger.warn("No policy found for transaction reference {} in file {}", transRef, fileName);
                    continue;
                }

                logger.info("Found policy for transaction reference {} in file {}, policyId: {}", transRef, fileName, policyTrans.getPolicyId());
                ReceiptTransDtls receiptTransDtls = new ReceiptTransDtls();
                List<ReceiptTransDtls> details = new ArrayList<>();

                SystemTransactions systemTransactions = systemTransactionsRepo.findTransactionByTranNo(policyTrans.getPolicyId());
                      //  .orElse(null);

                SystemTransactionsTemp systemTransactionsTemp = null; //= systemTransactionsTempRepo.findPolicyByTransTempNo(policyTrans.getPolicyId());
                      //  .orElse(null);

                if (policyTrans.getBusinessType().equalsIgnoreCase("N")) {
                    if (systemTransactionsTemp == null && systemTransactions == null) {
                        appendToTrackerFile(failureTrackerFilePath, txn.get("TransactionReferenceNo"));
                        throw new BadRequestException("No Matching Transaction Found for policy ID: " + policyTrans.getPolicyId());
                    }
                    if (systemTransactions != null) {
                        logger.info("Found system transaction for policy ID: {}, transaction reference {} in file {}", policyTrans.getPolicyId(), transRef, fileName);
                        receiptTransDtls.setTransNo(systemTransactions.getTransno());
                    } else {
                        receiptTransDtls.setTransTempNo(systemTransactionsTemp.getTempTransno());
                        logger.info("Found system transaction temp for policy ID: {}, transaction reference {} in file {}", policyTrans.getPolicyId(), transRef, fileName);
                    }
                } else {
                    receiptTransDtls.setTransNo(policyTrans.getPolicyId());
                    receiptTrans.setPolicyId(policyTrans.getPolicyId());
                    logger.info("Found policy for transaction reference {} in file {}, policyId: {}", transRef, fileName, policyTrans.getPolicyId());
                }

                receiptTransDtls.setRctAmount(rctAmount);
                details.add(receiptTransDtls);
                receiptTrans.setDetails(details);
                logger.info("Found receipt transaction details for policy ID: {}, transaction reference {} in file {}: {}", policyTrans.getPolicyId(), transRef, fileName, receiptTransDtls);
                receiptTrans.setReceiptDate(new Date());
                receiptTrans.setDocumentDate(new Date());
                receiptTrans.setReceiptAmount(rctAmount);
                receiptTrans.setFundReceipt("N");
                receiptTrans.setReceiptType(policyTrans.getBusinessType());
                receiptTrans.setInsuranceId(policyTrans.getAgent() != null ? policyTrans.getAgent().getAcctId() : null);
                receiptTrans.setBrnCode(policyTrans.getBranch() != null ? policyTrans.getBranch().getObId() : null);
                receiptTrans.setManualRef(policyTrans.getPolNo());
                receiptTrans.setReceiptDesc(policyTrans.getPolNo());
                receiptTrans.setPaymentRef(txn.get("TransactionReferenceNo"));
                receiptTrans.setPaidBy(txn.get("UserReferenceNo"));
                receiptTrans.setFromFCR("Y");
                receiptTrans.setReceiptUser(policyTrans.getCreatedUser());

                logger.info("Now starting to call the receipt creation");
                Long receiptId = receiptService.createReceipt(receiptTrans, true);
                logger.info("Finished calling receipt creation, the receipt id is: {}", receiptId);

                receiptTrans.setReceiptId(receiptId);

                receiptService.markReceiptPrinted(receiptId, policyTrans.getCreatedUser());
                logger.info("Receipt created for transaction reference {} in file {}: {}", transRef, fileName, receiptId);
                appendToTrackerFile(successTrackerFilePath, txn.get("TransactionReferenceNo"));

                try {
                    ReceiptTracker successTracker = buildTracker(receiptTrans, policyTrans,
                            "Successfully processed transaction", ReceiptStatus.SUCCESS);
                    receiptTrackers.add(successTracker);
                } catch (Exception trackerException) {
                    logger.warn("Failed to create receipt tracker for successful transaction {}: {}",
                            transRef, trackerException.getMessage());
                }

                logger.info("Successfully processed transaction for reference: {} in file: {}", transRef, fileName);

            } catch (Exception e) {
                logger.info("The following transaction cannot be processed appending to unsuccessful: TRANSACTION FULL {}", txn);
                unsuccessfulTransactions.add(txn);
                appendToTrackerFile(failureTrackerFilePath, txn.get("TransactionReferenceNo"));
                logger.error("Failed to process transaction for reference {} in file {}: {}", transRef, fileName, e.getMessage(), e);

                try {

                    if (transRef != null) {
                        PolicyTrans policyForTracker = getPolicyTrans(transRef, policyTransRepo);
                        if (policyForTracker != null) {
                            ReceiptTrans failedReceiptTrans = new ReceiptTrans();
                            failedReceiptTrans.setPaymentRef(txn.get("TransactionReferenceNo"));
                            try {
                                String amountStr = txn.get("TransactionAmount");
                                if (StringUtils.isNotBlank(amountStr)) {
                                    amountStr = amountStr.contains(",") ? amountStr.replace(",", "") : amountStr;
                                    failedReceiptTrans.setReceiptAmount(new BigDecimal(amountStr));
                                }
                            } catch (Exception amountEx) {
                                failedReceiptTrans.setReceiptAmount(BigDecimal.ZERO);
                            }

                            ReceiptTracker failureTracker = buildTracker(failedReceiptTrans, policyForTracker,
                                    "Failed to process transaction: " + e.getMessage(), ReceiptStatus.FAILURE);
                            receiptTrackers.add(failureTracker);
                        }
                    }
                } catch (Exception trackerException) {
                    logger.warn("Failed to create receipt tracker for failed transaction {}: {}",
                            transRef, trackerException.getMessage());
                }
            }
        }

        logger.info("Processed {} transactions, {} unsuccessful from file: {}", transactions.size(), unsuccessfulTransactions.size(), fileName);
        if (!unsuccessfulTransactions.isEmpty()) {
            logger.warn("Unsuccessful transactions in file {}, refs: {}", fileName, unsuccessfulTransactions.stream().map(txn -> txn.get("TransactionReferenceNo")).collect(Collectors.joining(", ")));
        }

        logger.info("Created {} receipt trackers for file: {}", receiptTrackers.size(), fileName);

        receiptTrackerService.saveReadReceipts(receiptTrackers, extractDateFromFileName(fileName) );

        return unsuccessfulTransactions.isEmpty();
    }

    public static PolicyTrans getPolicyTrans(String transRef, PolicyTransRepo policyTransRepo) {
        // Define the predicate for the query
        Predicate predicate = QPolicyTrans.policyTrans.clientPolNo.equalsIgnoreCase(transRef)
                .or(QPolicyTrans.policyTrans.polNo.equalsIgnoreCase(transRef))
                .or(QPolicyTrans.policyTrans.refNo.equalsIgnoreCase(transRef))
                .or(QPolicyTrans.policyTrans.polRevNo.equalsIgnoreCase(transRef))
                .or(QPolicyTrans.policyTrans.proposalNo.equalsIgnoreCase(transRef));

        // Create a Pageable request to fetch all results (no pagination needed for small result set)
        Pageable pageRequest = new PageRequest(0, Integer.MAX_VALUE); // Fetch all records

        // Query for all matching policies
        Page<PolicyTrans> policyTransPage = policyTransRepo.findAll(predicate, pageRequest);
        List<PolicyTrans> policyTransList = policyTransPage.getContent();

        if (policyTransList.isEmpty()) {
            logger.info("No policy transaction found for reference: {}", transRef);
            return null;
        }

        // Look for policy with pol_auth_status = 'A'
        PolicyTrans selectedPolicy = policyTransList.stream()
                .filter(pt -> "A".equalsIgnoreCase(pt.getCurrentStatus()))
                .findFirst()
                .orElse(policyTransList.get(0));

        logger.info("Found policy transaction for reference: {} with auth status: {}", transRef, selectedPolicy.getAuthStatus());
        return selectedPolicy;
    }

    private Date extractDateFromFileName(String fileName) {
        if (fileName == null || !fileName.startsWith("EXT_EOD_BANCA_ACCT_TXNS_")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }

        String datePart = fileName.substring("EXT_EOD_BANCA_ACCT_TXNS_".length());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        try {
            LocalDate localDate = LocalDate.parse(datePart, formatter);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format in file name: " + fileName, e);
        }
    }
}