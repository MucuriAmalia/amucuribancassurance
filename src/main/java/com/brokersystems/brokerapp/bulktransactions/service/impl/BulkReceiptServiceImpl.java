package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.bulktransactions.models.BatchRecord;
import com.brokersystems.brokerapp.bulktransactions.models.BulkReceipt;
import com.brokersystems.brokerapp.bulktransactions.models.QBulkReceipt;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkReceiptRepository;
import com.brokersystems.brokerapp.bulktransactions.service.BulkReceiptService;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.AccountRepo;
import com.brokersystems.brokerapp.setup.repository.OrgBranchRepository;
import com.brokersystems.brokerapp.setup.repository.ProductGroupRepo;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsTempRepo;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.uw.dtos.ReceiptsDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.validators.RenewalJobListener;
import org.apache.poi.ss.usermodel.*;
import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobReport;
import org.easybatch.core.processor.RecordProcessor;
import org.easybatch.core.reader.IterableRecordReader;
import org.easybatch.core.record.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class BulkReceiptServiceImpl implements BulkReceiptService {

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private ProductGroupRepo productGroupRepo;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private BulkReceiptRepository bulkReceiptRepo;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private SystemTransactionsRepo transRepo;
    @Autowired
    private SystemTransactionsTempRepo transTempRepo;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private UploadValidatorsUtils uploadValidatorsUtils;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private UserRepository userRepo;
    private  static final int BATCH_SIZE = 500;

    @Override
    @Async
    public void uploadBulkReceipt(File file, Long userId) throws BadRequestException {
        User user = userRepo.findOne(userId);

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<BulkReceipt> bulkReceipts = new ArrayList<>();

        //try (InputStream inputStream = file.getInputStream()) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            Workbook workbook = WorkbookFactory.create(inputStream);


            Sheet sheetOne = workbook.getSheetAt(0);

            int startRowIndex = findDataStartRow(sheetOne);
            System.out.println("startRowIndex" + startRowIndex);
            System.out.println("Last Row Num: " + sheetOne.getLastRowNum());
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }

            int totalDataRows = sheetOne.getLastRowNum() - startRowIndex + 1;
            int processedRows = 0;
            int errorRows = 0;


            for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                Row row = sheetOne.getRow(rowNum);
                if (isBlankRow(row)) {continue; }
                try{
                    BulkReceipt bulkReceipt = new BulkReceipt();
                    String polNo = ExcelReaderUtil.getCellValue(row, 0); // getCellStringValue(row.getCell(0));
                   // String productGroup = ExcelReaderUtil.getCellValue(row, 1); // getCellStringValue(row.getCell(1));
                    String riskProNo = ExcelReaderUtil.getCellValue(row, 1); //getCellStringValue(row.getCell(2));
                    Date documentDate = ExcelReaderUtil.parseFlexibleDate(ExcelReaderUtil.getCellValue(row, 2)); //getCellDateValue(row.getCell(3));
                    String receiptType = ExcelReaderUtil.getCellValue(row, 3); //getCellStringValue(row.getCell(4));
                    String insurerCode = ExcelReaderUtil.getCellValue(row, 4); //getCellStringValue(row.getCell(5));
                    String branchCode = ExcelReaderUtil.getCellValue(row, 5); //getCellStringValue(row.getCell(6));
                    //BigDecimal receiptAmt = BigDecimal.valueOf(Long.parseLong(ExcelReaderUtil.getCellValue(row, 7))); //getCellBigDecimalValue(row.getCell(7));
                    BigDecimal receiptAmt = ExcelReaderUtil.parseBigDecimalSafe(ExcelReaderUtil.getCellValue(row, 6));
                    if (receiptAmt == null || receiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BadRequestException("Invalid receipt amount in row " + (rowNum + 1));
                    }
                    String paidBy = ExcelReaderUtil.getCellValue(row, 7); //getCellStringValue(row.getCell(8));
                    String receiptRef = ExcelReaderUtil.getCellValue(row, 8); //getCellStringValue(row.getCell(9));
                    String manualRef = ExcelReaderUtil.getCellValue(row, 9); //getCellStringValue(row.getCell(10));
                    String narration = ExcelReaderUtil.getCellValue(row, 10); //getCellStringValue(row.getCell(11));

                if ( isNullOrEmpty(riskProNo) || isNullOrEmpty(polNo)
                        || isNullOrEmpty(receiptType) || isNullOrEmpty(branchCode) || isNullOrEmpty(insurerCode)
                        || receiptAmt.compareTo(BigDecimal.ZERO) <= 0  || documentDate == null
                        || isNullOrEmpty(paidBy) || isNullOrEmpty(receiptRef) || isNullOrEmpty(manualRef) || isNullOrEmpty(narration))
                {
                    throw new BadRequestException("All fields in excel are required. Please check the provided data in row " + (rowNum + 1));
                }

                    riskProNo = riskProNo.trim();
                    branchCode = branchCode.trim();
                    polNo = polNo.trim();
                    insurerCode = insurerCode.trim();
                    //String rptType =  receiptType.trim().toLowerCase().replace(" ", "");
                    //check receipt count by ref to be unique
                    int bulkReceiptCount = bulkReceiptRepo.countBulk(receiptRef, polNo);
                    if(bulkReceiptCount > 1){
                        throw new BadRequestException("A receipt exists with the receipt ref"+ receiptRef +" for polNo " + polNo+ "use a different receipt ref to be able to receipt the policy.");
                    }



                    AccountDef underWriter = accountRepo.findOne(QAccountDef.accountDef.shtDesc.eq(insurerCode));
                    if (underWriter == null) {
                        throw new BadRequestException("Insurer with code " + insurerCode + " is not setup in the system, please contact system admin");
                    }
               //     String normalizedProductGroup = productGroup.trim().toLowerCase().replace(" ", "");
                    //rptType = normalizedProductGroup;
//                    ProductGroupDef productG = productGroupRepo.findByNormalizedName(normalizedProductGroup);
//                    if (productG == null) {
//                        throw new BadRequestException("Product group " + productGroup + " not set in the system, please contact system admin.");
//                    }

                    String groupType = "";
                // );


//                    String groupType = null;
//                    if (rptType.equalsIgnoreCase("GeneralInsurance")) {
//                        groupType = "N";
//                        bulkReceipt.setReceiptType(groupType);
//                    } else if (rptType.equalsIgnoreCase("LifeInsurance")) {
//                        groupType = "L";
//                        bulkReceipt.setReceiptType(groupType);
//                    } else {
//                        throw new BadRequestException("Invalid Receipt Type " + receiptType + " , expected receipt type is either General Insurance or Life Insurance.");
//                    }

                    //search by (pol_ref_no or proposal) and pol_no
                    //PolicyTrans policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.refNo.equalsIgnoreCase(riskProNo).or(QPolicyTrans.policyTrans.proposalNo.equalsIgnoreCase(riskProNo)).and(QPolicyTrans.policyTrans.polNo.equalsIgnoreCase(polNo)));
                System.out.println("searching for "+ underWriter.getAcctId() +" "+ polNo +" " +riskProNo);

                PolicyTrans policyTrans = policyTransRepo.findBulkProcessPolicytrans(underWriter.getAcctId(),polNo,riskProNo,riskProNo,riskProNo);
                    if (policyTrans == null) {
                        throw new BadRequestException("No active policy transaction found for "+polNo+" reference number/Proposal  number " + riskProNo+" Underwriter "+ insurerCode);
                    }
                    String prgType = policyTrans.getProduct().getProGroup().getPrgType();

                    //check receipt amount equal policy prem
                    Currencies currencies = policyTrans.getTransCurrency();
                    BigDecimal basicPrem = policyTrans.getBasicPrem();
                    receiptAmt = receiptAmt.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
                    basicPrem = basicPrem.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);

                    System.out.println("basic prem"+ basicPrem + "receipt amt"+receiptAmt);

                    if(basicPrem.compareTo(receiptAmt) != 0){
                        throw new BadRequestException("Receipt amount must be equal to the policy premium amount for policy " + polNo);
                    }

                    //  String prgType =  productG.getPrgType();
                    System.out.println("prgType" + prgType);
                    if(prgType.equalsIgnoreCase("L")){
                        groupType = "L";
                    }else if(prgType.equalsIgnoreCase("F")){
                        groupType = "N";
                    }else {
                        throw new BadRequestException("Invalid product group type '" + prgType + "'. Expected 'F' (General) or 'L' (Life). Contact the Admin.");
                    }
                    bulkReceipt.setReceiptType(groupType);
                    bulkReceipt.setPaymentMode(receiptType);


                    bulkReceipt.setPolicy(policyTrans);

                    System.out.println("groupType " +groupType);
                    System.out.println("BusinessType " +policyTrans.getBusinessType());

//                    if (!policyTrans.getBusinessType().equals(groupType)) {
//                        throw new BadRequestException("Product group " + productGroup + " does not match system policy number for the reference number/Proposal  number " + riskProNo);
//                    }

                    if (!policyTrans.getAgent().equals(underWriter)) {
                        throw new BadRequestException("Insurer with the code " + insurerCode + " does not match system policy number for the reference number/Proposal  number " + riskProNo);
                    }
                    bulkReceipt.setInsurance(underWriter);

                    if (!policyTrans.getPolNo().equalsIgnoreCase(polNo)) {
                        throw new BadRequestException("Provided InsureMaster policy number " + polNo + " does not match system policy number for the reference number/Proposal  number " + riskProNo);
                    }
                    bulkReceipt.setPolNo(polNo);
                    OrgBranch branch = orgBranchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq(branchCode));
                    if (branch != null) {
                        bulkReceipt.setBranch(branch);
                    } else {
                        throw new BadRequestException("Provided branch code " + branchCode + " not set in the system, please contact system admin.");
                    }

                    bulkReceipt.setReceiptAmount(receiptAmt);
                    bulkReceipt.setReceiptDesc(narration);
                    bulkReceipt.setPaymentRef(receiptRef);
                    bulkReceipt.setManualRef(manualRef);
                    bulkReceipt.setPaidBy(paidBy);
                    bulkReceipt.setReceiptUploadedBy(user);
                    bulkReceipt.setDocumentDate(documentDate);
                    bulkReceipt.setUploadDate(new Date());
                    bulkReceipt.setReceiptStatus("N");

                    bulkReceipts.add(bulkReceipt);
                    if (bulkReceipts.size() >= BATCH_SIZE)  {
                        bulkReceiptRepo.save(bulkReceipts);
                        bulkReceipts.clear();
                    }
                    processedRows ++;

                } catch (BadRequestException e){
                    String errorMsg = "Row " + (rowNum + 1) + ": " + e.getMessage();
                    //invalidRecords.add(errorMsg);
                    errorRows++;
                    System.err.println("SKIPPING " + errorMsg);
                } catch (Exception e) {
                    // Log unexpected errors and skip this row
                    String errorMsg = "Row " + (rowNum + 1) + ": Unexpected error - " + e.getMessage();
                    //invalidRecords.add(errorMsg);
                    errorRows++;
                    System.err.println("SKIPPING " + errorMsg);
                    //e.printStackTrace();
                    // Continue processing next row - do not add to bulkReceipts list
                }
                }
                //int totalProcessed = totalRows - processedRows;
                //System.out.println("totalProcessed rows = " + totalProcessed);
                int successfulReceipts = bulkReceipts.size();

               // if (!(totalProcessed > 0) && successfulReceipts > 0) {
//            if(successfulReceipts > 0){
//                try {
//                    bulkReceiptRepo.save(bulkReceipts);
//                } catch (Exception e) {
//                    throw new BadRequestException("Error saving ");
//                }
//            } else {
//                System.out.println("No valid receipts records found");
//            }
            if (!bulkReceipts.isEmpty())  {
                bulkReceiptRepo.save(bulkReceipts);
                bulkReceipts.clear();
            }
                response.put("successfulReceipts", successfulReceipts);
                response.put("invalidReceipts", errorRows);
                response.put("totalRecords", totalDataRows);
                response.put("processedRecords", processedRows);

            if (!invalidRecords.isEmpty()) {
                response.put("invalidRecordDetails", invalidRecords);
                response.put("hasErrors", true);
            } else {
                response.put("hasErrors", false);
            }
            System.out.println("Processing complete - Total: " + totalDataRows + ", Successful: " + successfulReceipts + ", Failed: " + errorRows);
            //return response;

        } catch (Exception e) {
            throw new BadRequestException("Error processing the Excel file: " + e.getMessage());
        }
    }

    private boolean isHeaderRow(Row row) {
        if (row == null) return false;

        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell == null) continue;
            String cellValue = getCellStringValue(cell);
            if (cellValue != null) {
                cellValue = cellValue.toLowerCase().trim();
                if (cellValue.contains("system policy no.") ||
                        cellValue.contains("product group") ||
                        cellValue.contains("insurer code")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBlankRow(Row row) {
        if (row == null) {
            System.out.println("Blank row detected (null).");
            return true;
        }

        boolean allBlank = true;
        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                allBlank = false;
                break;
            }
        }

        if (allBlank) {
            System.out.println("Blank row detected at index: " + row.getRowNum());
        }

        return allBlank;
    }


    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private BigDecimal getCellBigDecimalValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_BOOLEAN:
                try {
                    return new BigDecimal(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private Date getCellDateValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getDateCellValue();
            default:
                return null;
        }
    }

    private BigInteger getCellBigIntegerValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return BigInteger.valueOf((long) cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                try {
                    return new BigInteger(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private int findDataStartRow(Sheet sheet) {
        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                System.out.println("Checking row: " + rowNum);
                for (Cell cell : row) {
                    System.out.print(getCellStringValue(cell) + " | ");
                }
                System.out.println();
            }
            if (isHeaderRow(row)) {
                return rowNum + 1; // Start after the header row
            }
        }
        return -1;
    }


    private Double getCellDoubleValue(Cell cell) {
        return (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) ? cell.getNumericCellValue() : null;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public DataTablesResult<ReceiptsDTO> findUnprocessedBulkReceipt(DataTablesRequest request) {
        List<Object[]> receipts = bulkReceiptRepo.findUnprocessedBulkReceipt((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<ReceiptsDTO> bulkReceiptDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!receipts.isEmpty())
            rowCount = ((BigInteger) receipts.get(0)[10]).intValue();
        for (Object[] receipt : receipts) {
            ReceiptsDTO bulkReceiptDTO = new ReceiptsDTO();
            bulkReceiptDTO.setReceiptBulkId(((BigInteger) receipt[0]).longValue());
            bulkReceiptDTO.setReceiptType((String) receipt[1]);
            bulkReceiptDTO.setInsurerName((String) receipt[2]);
            bulkReceiptDTO.setBranchName((String) receipt[3]);
            bulkReceiptDTO.setReceiptAmount((BigDecimal) receipt[4]);
            bulkReceiptDTO.setPaidBy((String) receipt[5]);
            bulkReceiptDTO.setDocumentDate((Date) receipt[6]);
            bulkReceiptDTO.setPaymentRef((String) receipt[7]);
            bulkReceiptDTO.setManualRef((String) receipt[8]);
            bulkReceiptDTO.setNarration((String) receipt[9]);

            bulkReceiptDTOS.add(bulkReceiptDTO);
            System.out.println("bulkReceiptDTOS "+bulkReceiptDTOS);
        }
        Page<ReceiptsDTO> page = new PageImpl<>(bulkReceiptDTOS, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<MakerCheckDTO> findProcessedBulkReceipt(DataTablesRequest request) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%%";
        Long checkerId = userUtils.getCurrentUser().getId();
        List<Object[]> receiptsList = makerCheckerRepo.findAllBulkReceiptTasks(checkerId.toString(), search.toLowerCase(), request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if (!receiptsList.isEmpty()) {
            rowCount = ((BigInteger) receiptsList.get(0)[11]).longValue(); // total_rows
        }
        final List<MakerCheckDTO> workFlowDTOList = new ArrayList<>();

        for (Object[] receipt : receiptsList) {
            MakerCheckDTO workFlowDTO = new MakerCheckDTO();
            workFlowDTO.setTaskId(((BigInteger) receipt[0]).longValue());
            workFlowDTO.setTaskName((String) receipt[1]);
            workFlowDTO.setMadeOnDate((java.sql.Timestamp) receipt[2]);
            workFlowDTO.setMadeBy((String) receipt[3]);
            workFlowDTO.setStatus((String) receipt[4]);
            workFlowDTO.setTaskType((String) receipt[5]);
            if (receipt[6] != null) {
                workFlowDTO.setPolicyId(((BigInteger) receipt[6]).longValue());
            }
            if (receipt[7] != null) {
                workFlowDTO.setAcctId(((BigInteger) receipt[7]).longValue());
            }
            workFlowDTO.setInitiatorName((String) receipt[9]);
            workFlowDTO.setPolicyNumber((String) receipt[10]);
            workFlowDTOList.add(workFlowDTO);
        }
        Page<MakerCheckDTO> page = new PageImpl<>(workFlowDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Transactional( propagation = Propagation.REQUIRED)
    public void processSingleBulkReceipt(Long receiptId) throws BadRequestException {
            ReceiptTrans receipt = new ReceiptTrans();
            System.out.println("receiptId " +receiptId);
                BulkReceipt bulkReceipt = bulkReceiptRepo.findOne(QBulkReceipt.bulkReceipt.receiptId.eq(receiptId));
                ReceiptTransDtls receiptTransDtls = new ReceiptTransDtls();
                List<ReceiptTransDtls> details = new ArrayList<>();
                Long policyId = bulkReceipt.getPolicy().getPolicyId();
                List<BigInteger> systemTransactions = transRepo.findBulkUploadReceiptItem(policyId);  //transRepo.findOne(QSystemTransactions.systemTransactions.policy.policyId.eq(policyId));
                SystemTransactionsTemp systemTransactionsTemp = transTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.policy.policyId.eq(policyId));
                if(bulkReceipt.getReceiptType().equalsIgnoreCase("N")){ //general
                    if(systemTransactions != null || systemTransactionsTemp != null){
                        if(systemTransactions != null && !systemTransactions.isEmpty()){
                            receiptTransDtls.setTransNo(systemTransactions.get(0).longValue());
                        }
                        if(systemTransactionsTemp != null){
                            receiptTransDtls.setTransTempNo(systemTransactionsTemp.getTempTransno());
                        }
                    }
                } else{ //life
                    receiptTransDtls.setTransNo(policyId);
                    receipt.setPolicyId(policyId);
                }
                receiptTransDtls.setRctAmount(bulkReceipt.getReceiptAmount());
                details.add(receiptTransDtls);
                receipt.setDetails(details);
                System.out.println("");
                receipt.setReceiptType(bulkReceipt.getReceiptType());
                receipt.setInsuranceId(bulkReceipt.getInsurance().getAcctId());
                receipt.setBrnCode(bulkReceipt.getBranch().getObId());
                receipt.setReceiptAmount(bulkReceipt.getReceiptAmount());
                receipt.setPaidBy(bulkReceipt.getPaidBy());
                receipt.setReceiptDesc(bulkReceipt.getReceiptDesc());
                receipt.setPaymentRef(bulkReceipt.getPaymentRef());
                receipt.setManualRef(bulkReceipt.getManualRef());
                receipt.setReceiptDate(new Date());
                receipt.setDocumentDate(bulkReceipt.getDocumentDate());
                receipt.setReceiptUser(userUtils.getCurrentUser());

                Long transId = receiptService.createReceipt(receipt, false);

                bulkReceipt.setTransId(transId);
                bulkReceipt.setReceiptStatus("Y");
                bulkReceiptRepo.save(bulkReceipt);
    }

    @Override
    public String approveBulkReceipting(BatchCreditBatch batchCreditBatch) throws BadRequestException {
        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        SecurityContext context = SecurityContextHolder.getContext();

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("bulk_receipt_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    SecurityContextHolder.setContext(context);

                    Long policyId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    processSingleBulkReceipt(policyId);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //Future<JobReport> report = executorService.submit(job);
        Future<JobReport> report = executorService.submit(() -> {
            SecurityContextHolder.setContext(context);
            return job.call();
        });


        //return  new ArrayList<>();
        return "Receipt Created Successfully";
    }

    @Override
    public String deleteBulkReceipting(List<Long> receiptIds) throws BadRequestException {
        if (receiptIds.size() <= 0) {
            throw new BadRequestException("Select At least One Receipt To Process");
        }
        for(Long receiptId : receiptIds) {
            System.out.println("receiptId " + receiptId);
            BulkReceipt bulkReceipt = bulkReceiptRepo.findOne(QBulkReceipt.bulkReceipt.receiptId.eq(receiptId));

            if("N".equals(bulkReceipt.getReceiptStatus())) {
                bulkReceiptRepo.delete(bulkReceipt);
            }
        }
        return "Bulk receipt upload deleted successfully.";
    }


    @Override
    public String approveBulkReceiptingTaskId(BatchCreditBatch batchCreditBatch) throws BadRequestException {
//        if (taskIds.size() <= 0) {
//            throw new BadRequestException("Select At least One Receipt To Process");
//        }
//
//        uploadValidatorsUtils.approveBulkReceipting(taskIds);
        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        SecurityContext context = SecurityContextHolder.getContext();

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("bulk_receipt_approve_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    SecurityContextHolder.setContext(context);

                    Long taskId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    uploadValidatorsUtils.approvBulkSingleReceipting(taskId);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //Future<JobReport> report = executorService.submit(job);
        Future<JobReport> report = executorService.submit(() -> {
            SecurityContextHolder.setContext(context);
            return job.call();
        });

        return "Bulk receipt approvals done successfully.";
    }

    @Override
    public String rejectBulkReceiptingTaskId(BatchCreditBatch batchCreditBatch) throws BadRequestException {
//        if (taskIds.size() <= 0) {
//            throw new BadRequestException("Select At least One Receipt To Process");
//        }
//
//        uploadValidatorsUtils.rejectBulkReceipting(taskIds, reasonId, reason);

        if (batchCreditBatch.getBatchRecords().size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        SecurityContext context = SecurityContextHolder.getContext();

        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(batchCreditBatch.getBatchRecords()))
                .named("bulk_receipt_reject_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor((RecordProcessor<Record, Record>) renForm -> {
                    SecurityContextHolder.setContext(context);

                    Long taskId = ((BatchRecord) renForm.getPayload()).getPayload().getBatchId();
                    Long userId = ((BatchRecord) renForm.getPayload()).getPayload().getUserId();
                    String reason = ((BatchRecord) renForm.getPayload()).getPayload().getRejectReason();
                    uploadValidatorsUtils.rejectBulkReceipting(taskId, null, reason);
                    return renForm;
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //Future<JobReport> report = executorService.submit(job);
        Future<JobReport> report = executorService.submit(() -> {
            SecurityContextHolder.setContext(context);
            return job.call();
        });
        return "Bulk receipt approvals done successfully.";
    }
}
