package com.brokersystems.brokerapp.trans.service.impl;

import com.brokersystems.brokerapp.accounts.model.InsPaymentBean;
import com.brokersystems.brokerapp.accounts.model.PaymentAudit;
import com.brokersystems.brokerapp.accounts.model.QPaymentAudit;
import com.brokersystems.brokerapp.accounts.repository.PaymentAuditRepo;
import com.brokersystems.brokerapp.bulktransactions.utils.ExcelReaderUtil;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.exception.AdminFeeException;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.QSystemSequence;
import com.brokersystems.brokerapp.setup.model.SystemSequence;
import com.brokersystems.brokerapp.setup.repository.AccountRepo;
import com.brokersystems.brokerapp.setup.repository.CurrencyRepository;
import com.brokersystems.brokerapp.setup.repository.SequenceRepository;
import com.brokersystems.brokerapp.trans.dtos.CommissionUnreconciledDataDTO;
import com.brokersystems.brokerapp.trans.repository.CommissionReconRepo;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.trans.dtos.CommissionDTO;
import com.brokersystems.brokerapp.trans.dtos.CommissionReconciliationDTO;
import com.brokersystems.brokerapp.trans.mappers.CommDtoMapper;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.*;
import com.brokersystems.brokerapp.trans.service.CommissionsPayinsService;
import com.brokersystems.brokerapp.uw.model.RiskUploadForm;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommissionsPayinsServiceImpl implements CommissionsPayinsService {

    @Autowired
    private CommissionTransRepo commissionTransRepo;

    @Autowired
    private SettlementRepo settlementRepo;

    @Autowired
    private PaymentAuditRepo auditRepo;

    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;

    @Autowired
    private CommissionUnreconciledDataRepository commissionUnreconciledRepo;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private SystemTransRepo systransRepo;

    @Autowired
    private CurrencyRepository currencyRepo;

    @Autowired
    private CommissionReconRepo commissionReconRepo;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private SystemTransactionsRepo transRepo;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private CommissionPaymentsRepo commissionPaymentsRepo;

    @Override
    public DataTablesResult<CommissionTrans> findAllLoadedCommissions(DataTablesRequest request, Long acct) throws IllegalAccessException {
        BooleanExpression pred = QCommissionTrans.commissionTrans.status.eq("D");
        Page<CommissionTrans> page = null;
        if(acct!=null)
        page = commissionTransRepo.findAll(pred.and(request.searchPredicate(QCommissionTrans.commissionTrans)).and(QCommissionTrans.commissionTrans.insurance.eq(acct)), request);
        return new DataTablesResult(request, page);
    }

    @Override
    public void importCommissions(MultipartFile file, RiskUploadForm uploadForm)throws BadRequestException, IOException {
        if(uploadForm.getReceipt()==null ){
            throw new BadRequestException("Select A commission Receipt to Continue!");
        }
        Long receipt = systemTransactionsRepo.findOne(uploadForm.getReceipt()).getTransno();
        String receiptNo = systemTransactionsRepo.findOne(uploadForm.getReceipt()).getRefNo();
        Long insurance = uploadForm.getInsurance().getAcctId();
        List<CommissionTrans> commissionTransList = new ArrayList<>();
        byte [] byteArr=file.getBytes();
        InputStream excelFile = new ByteArrayInputStream(byteArr);
        Workbook workbook = new HSSFWorkbook(excelFile);
        for(int i=0;i<workbook.getNumberOfSheets();i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (i == 0) {
                Iterator<Row> iterator = sheet.iterator();
                while (iterator.hasNext()) {
                    Row currentRow = iterator.next();

                    if (currentRow.getRowNum() != 0) {
                        CommissionTrans commissionTrans = new CommissionTrans();
                        commissionTrans.setStatus("D");
                        commissionTrans.setInsurance(insurance);
                        commissionTrans.setReceipt(receipt);
                        commissionTrans.setReceiptNo(receiptNo);
                        commissionTrans.setDate(new Date());
                        Iterator<Cell> cellIterator = currentRow.iterator();
                        while (cellIterator.hasNext()) {
                            Cell currentCell = cellIterator.next();
                            int columnIndex = currentCell.getColumnIndex();
                            switch (columnIndex) {
                                case 0:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        commissionTrans.setDebitNoteNo(currentCell.getStringCellValue());
                                    }
                                    break;
                                case 1:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        commissionTrans.setAmount(new BigDecimal(currentCell.getStringCellValue()));
                                    }
                                    else  if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        commissionTrans.setAmount(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                    }
                                    break;
                                case 2:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        commissionTrans.setPolicyNo(currentCell.getStringCellValue());
                                    }
                                    break;
                                case 3:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        commissionTrans.setInspolicyNo(currentCell.getStringCellValue());
                                    }
                                    break;
                            }
                        }
                        commissionTransList.add(commissionTrans);
                    }

                }

            }
        }
        commissionTransRepo.save(commissionTransList);

    }

    @Override
    public void processCommissions(CommissionData commissionData) throws BadRequestException {
        if(commissionData.getCommissionids()==null || commissionData.getCommissionids().size()==0 ){
            throw new BadRequestException("No commission to process...");
        }

        if(commissionData.getTransId()==null){ throw new BadRequestException("Select Insurance Receipt to Process...."); }

        SystemTransactions receipt = systemTransactionsRepo.findOne(commissionData.getTransId());

        if(receipt==null)  throw new BadRequestException("Select a Commission Receipt to Process");

        for(Long a:commissionData.getCommissionids()) {
            CommissionTrans commissionTrans = commissionTransRepo.findOne(a);
            Iterable<SystemTransactions> systemTransactions = systemTransactionsRepo.findAll(QSystemTransactions.systemTransactions.transType.eq("COMM").and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    .and(QSystemTransactions.systemTransactions.refNo.eq(commissionTrans.getDebitNoteNo())));
            BigDecimal receiptAmt = receipt.getBalance().abs();
            BigDecimal initialreceiptAmt = receipt.getBalance().abs();
            for (SystemTransactions transactions : systemTransactions) {
                BigDecimal balance = transactions.getBalance();
                    receiptAmt = receiptAmt.subtract(balance);
            }
            if (receiptAmt.compareTo(BigDecimal.ZERO) < 0 || receiptAmt.compareTo(initialreceiptAmt) == 0)
                throw new BadRequestException("The Receipt Amount is not enough to process selected debits or there are no matching commission records to process");

        commissionTrans.setStatus("P");
        commissionTransRepo.save(commissionTrans);
    }
    }

    @Override
    public void confirmCommissions(Long commReceipt) throws BadRequestException {
        if (commReceipt == null) {
            throw new BadRequestException("Select A record to Process....");
        }
        SystemTransactions receipt = systemTransactionsRepo.findOne(commReceipt);
        if (receipt == null) throw new BadRequestException("Select a Commission Receipt Record to Process...");
        Iterable<CommissionTrans> commissionData = commissionTransRepo.findAll(QCommissionTrans.commissionTrans.receiptNo.eq(receipt.getRefNo()).and(QCommissionTrans.commissionTrans.status.eq("P")));
        for (CommissionTrans commissionTrans : commissionData) {
            commissionTrans.setStatus("R");
            commissionTransRepo.save(commissionTrans);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void undoProcessCommissions(Long receiptId) throws BadRequestException {
        if (receiptId== null) {
            throw new BadRequestException("Select records to undo commission processing...");
        }
        Iterable<CommissionTrans> commissionTrans = commissionTransRepo.findAll(QCommissionTrans.commissionTrans.receipt.eq(receiptId).and(QCommissionTrans.commissionTrans.status.equalsIgnoreCase("P")));
        for (CommissionTrans comm : commissionTrans) {
            comm.setStatus("D");
        }
        commissionTransRepo.save(commissionTrans);
    }

    @Override
    public void authorizeCommissions(Long transId) throws BadRequestException {

        SystemTransactions receiptTrans = systemTransactionsRepo.findOne(transId);

        ReceiptTrans receipt = receiptRepository.findOne(QReceiptTrans.receiptTrans.receiptNo.equalsIgnoreCase(receiptTrans.getRefNo()));

        Iterable<CommissionTrans> commissionTrans = commissionTransRepo.findAll(QCommissionTrans.commissionTrans.receiptNo.eq(receipt.getReceiptNo()).and(QCommissionTrans.commissionTrans.status.equalsIgnoreCase("R")));

        if(receipt==null)
            throw new BadRequestException("Select a Commission Receipt to Process");

        List<SystemTransactions> transactionsList = new ArrayList<>();

        for(CommissionTrans comm :commissionTrans){
            Iterable<SystemTransactions> systemTransactions = systemTransactionsRepo.findAll(QSystemTransactions.systemTransactions.transType.eq("COMM").and(QSystemTransactions.systemTransactions.transdc.eq("D"))
            .and(QSystemTransactions.systemTransactions.refNo.eq(comm.getDebitNoteNo())));

             BigDecimal receiptAmt = receiptTrans.getBalance().abs();
             BigDecimal initialreceiptAmt = receiptTrans.getBalance().abs();

            for(SystemTransactions transactions:systemTransactions){
                BigDecimal balance = transactions.getBalance();
                if (receiptAmt.compareTo(transactions.getBalance()) == 1 || receiptAmt.compareTo(transactions.getBalance()) == 0) {
                    transactions.setSettleAmt((balance != null) ? transactions.getSettleAmt().add(balance.abs()) : balance.abs());
                    transactions.setBalance(BigDecimal.ZERO);
                    receiptAmt = receiptAmt.subtract(balance);
                    //prorataAgBalance = 1;
                } else {
                    if (transactions.getSettleAmt() == null) {
                        transactions.setSettleAmt(balance.abs());
                    } else
                        transactions.setSettleAmt(transactions.getSettleAmt().add(balance.abs()));
                    transactions.setBalance(transactions.getBalance().subtract(balance.abs()));
                    receiptAmt = receiptAmt.subtract(balance);
                }
                transactionsList.add(transactions);
            }

            if(receiptAmt.compareTo(BigDecimal.ZERO) < 0  || receiptAmt.compareTo(initialreceiptAmt)==0)
                throw new BadRequestException("Insurance Receipt Amount is not enough to process selected debits or no matching commission records to process");
            receiptTrans.setBalance(receiptAmt.negate());
            receiptTrans.setSettleAmt((receiptTrans.getSettleAmt() != null) ? receiptTrans.getSettleAmt().abs().add(initialreceiptAmt.subtract(receiptAmt)): initialreceiptAmt.subtract(receiptAmt));
            transactionsList.add(receiptTrans);
            comm.setStatus("A");
            commissionTransRepo.save(comm);
        }
        systemTransactionsRepo.save(transactionsList);
    }

//    @Override
//    public DataTablesResult<CommissionTrans> findAllConfirmedCommissions(DataTablesRequest request) throws IllegalAccessException {
//        BooleanExpression pred = QCommissionTrans.commissionTrans.status.eq("P");
//        Page<CommissionTrans> page = commissionTransRepo.findAll(pred.and(request.searchPredicate(QCommissionTrans.commissionTrans)), request);
//        return new DataTablesResult(request, page);
//    }
    @Override
    public DataTablesResult<CommissionDTO> findAllConfirmedCommissions(Long agent, DataTablesRequest pageable) throws IllegalAccessException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        CommDtoMapper mapper = new CommDtoMapper();
        long countProcessedComm = 0;
        List<CommissionDTO> commissionDTOList=null;
        if(agent!=null) {
            countProcessedComm = jdbcTemplate.queryForObject(CommissionQueries.countProcessedCommissions,Long.class,new Object[]{agent});
            commissionDTOList = jdbcTemplate.query(CommissionQueries.processedCommQuery,mapper,new Object[]{agent, pageable.getPageNumber(),pageable.getPageSize(),pageable.getPageNumber(),pageable.getPageSize()});
        }
        Page<CommissionDTO> page = new PageImpl<>(commissionDTOList, pageable, countProcessedComm);

        return new DataTablesResult(pageable, page);
    }


    @Override
    public File getCommissionsTemplate() throws BadRequestException{
        File file;
        try {
            file = ResourceUtils.getFile("classpath:templates/commission_upload_template.xls");
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        return file;
    }
    @Override
    @Transactional
    public void processCommExcel(MultipartFile file, Long acctId) throws IOException, BadRequestException {
        List<CommissionReconciliationData> commissionReconciliationDataList = new ArrayList<>();
        Map<Integer, String> columns = new HashMap<>();
        byte[] byteArr = file.getBytes();
        InputStream excelFile = new ByteArrayInputStream(byteArr);
        Workbook workbook = new HSSFWorkbook(excelFile);

        // Validate template format
//        List<String> validationErrors = ExcelReaderUtil.validateBulkCommRemp(workbook);
//        if (!validationErrors.isEmpty()) {
//            StringBuilder errorMessage = new StringBuilder("Template validation failed: ");
//            for (String error : validationErrors) {
//                errorMessage.append(error).append("; ");
//            }
//            throw new BadRequestException(errorMessage.toString());
//        }

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (i == 0) {
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    Row currentRow = rowIterator.next();
                    CommissionReconciliationData commissionReconciliationData = new CommissionReconciliationData();

                    if (currentRow.getRowNum() == 0) {
                        // Read header row to map column positions
                        Iterator<Cell> cellIterator = currentRow.iterator();
                        while (cellIterator.hasNext()) {
                            Cell currentCell = cellIterator.next();
                            int columnIndex = currentCell.getColumnIndex();
                            if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                switch (columnIndex) {
                                    case 0:
                                        columns.put(0, currentCell.getStringCellValue());
                                        break;
                                    case 1:
                                        columns.put(1, currentCell.getStringCellValue());
                                        break;
                                    case 2:
                                        columns.put(2, currentCell.getStringCellValue());
                                        break;
                                    case 3:
                                        columns.put(3, currentCell.getStringCellValue());
                                        break;
                                    case 4:
                                        columns.put(4, currentCell.getStringCellValue());
                                        break;
                                    case 5:
                                        columns.put(5, currentCell.getStringCellValue());
                                        break;
                                    case 6:
                                        columns.put(6, currentCell.getStringCellValue());
                                        break;
                                    case 7:
                                        columns.put(7, currentCell.getStringCellValue());
                                        break;
                                    case 8:
                                        columns.put(8, currentCell.getStringCellValue());
                                        break;
                                    case 9:
                                        columns.put(9, currentCell.getStringCellValue());
                                        break;
                                    case 10:
                                        columns.put(10, currentCell.getStringCellValue());
                                        break;
                                    case 11:
                                        columns.put(11, currentCell.getStringCellValue());
                                        break;
                                    case 12:
                                        columns.put(12, currentCell.getStringCellValue());
                                        break;
                                }
                            }
                        }
                    } else {
                        // Process data rows
                        Iterator<Cell> cellIterator = currentRow.iterator();
                        while (cellIterator.hasNext()) {
                            Cell currentCell = cellIterator.next();
                            int columnIndex = currentCell.getColumnIndex();

                                switch (columnIndex) {
                                    case 0:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setPolicyNumber(currentCell.getStringCellValue());
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setPolicyNumber(String.valueOf((long) currentCell.getNumericCellValue()));
                                        }
                                        break;
                                    case 1:
                                        commissionReconciliationData.setUnderWriterPolicyNo(currentCell.getStringCellValue());
                                        break;
                                    case 3:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setTransCode(currentCell.getStringCellValue());
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setTransCode(String.valueOf((long) currentCell.getNumericCellValue()));
                                        }
                                        break;
                                    case 4:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setDebitRefNo(currentCell.getStringCellValue());
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setDebitRefNo(String.valueOf((long) currentCell.getNumericCellValue()));
                                        }
                                        break;
                                    case 5:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setCreditRefNo(currentCell.getStringCellValue());
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setCreditRefNo(String.valueOf((long) currentCell.getNumericCellValue()));
                                        }
                                        break;
                                    case 2:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setRevisionNo(currentCell.getStringCellValue());
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setRevisionNo(String.valueOf((long) currentCell.getNumericCellValue()));
                                        }
                                        break;
                                    case 6:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setPayment(new BigDecimal(currentCell.getStringCellValue()));
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setPayment(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        }

                                        break;
                                    case 7:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setCommission(new BigDecimal(currentCell.getStringCellValue()));
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setCommission(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        }
                                        else   if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                            CellValue cellValue = evaluator.evaluate(currentCell);
                                            if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                                commissionReconciliationData.setCommission(BigDecimal.valueOf(cellValue.getNumberValue()));
                                            } else if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
                                                try {
                                                    BigDecimal value = new BigDecimal(cellValue.getStringValue());
                                                    commissionReconciliationData.setCommission(value);
                                                } catch (NumberFormatException e) {
                                                    // handle exception
                                                }
                                            }

                                        }
                                        break;
                                    case 8:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setAdminFee(new BigDecimal(currentCell.getStringCellValue()));
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setAdminFee(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        }
                                        else   if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                            CellValue cellValue = evaluator.evaluate(currentCell);
                                            if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                                commissionReconciliationData.setAdminFee(BigDecimal.valueOf(cellValue.getNumberValue()));
                                            } else if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
                                                try {
                                                    BigDecimal value = new BigDecimal(cellValue.getStringValue());
                                                    commissionReconciliationData.setAdminFee(value);
                                                } catch (NumberFormatException e) {
                                                    // handle exception
                                                }
                                            }

                                        }
                                        break;
                                    case 9:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setTotalRevenue(new BigDecimal(currentCell.getStringCellValue()));
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setTotalRevenue(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        }
                                        else   if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                            CellValue cellValue = evaluator.evaluate(currentCell);
                                            if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                                commissionReconciliationData.setTotalRevenue(BigDecimal.valueOf(cellValue.getNumberValue()));
                                            } else if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
                                                try {
                                                    BigDecimal value = new BigDecimal(cellValue.getStringValue());
                                                    commissionReconciliationData.setTotalRevenue(value);
                                                } catch (NumberFormatException e) {
                                                    // handle exception
                                                }
                                            }

                                        }
                                        break;
                                    case 10:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setWithholdingTax(new BigDecimal(currentCell.getStringCellValue()));
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setWithholdingTax(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        }
                                        else   if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                            CellValue cellValue = evaluator.evaluate(currentCell);
                                            if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                                commissionReconciliationData.setWithholdingTax(BigDecimal.valueOf(cellValue.getNumberValue()));
                                            } else if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
                                                try {
                                                    BigDecimal value = new BigDecimal(cellValue.getStringValue());
                                                    commissionReconciliationData.setWithholdingTax(value);
                                                } catch (NumberFormatException e) {
                                                    // handle exception
                                                }
                                            }

                                        }
                                        break;
                                    case 11:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setAdminFeeWhtx(new BigDecimal(currentCell.getStringCellValue()));
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setAdminFeeWhtx(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        }
                                        else   if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                            CellValue cellValue = evaluator.evaluate(currentCell);
                                            if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                                commissionReconciliationData.setAdminFeeWhtx(BigDecimal.valueOf(cellValue.getNumberValue()));
                                            } else if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
                                                try {
                                                    BigDecimal value = new BigDecimal(cellValue.getStringValue());
                                                    commissionReconciliationData.setAdminFeeWhtx(value);
                                                } catch (NumberFormatException e) {
                                                    // handle exception
                                                }
                                            }

                                        }
                                        break;
                                    case 12:
                                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                            commissionReconciliationData.setPayableCommission(new BigDecimal(currentCell.getStringCellValue()));
                                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            commissionReconciliationData.setPayableCommission(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        }
                                        else   if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                            CellValue cellValue = evaluator.evaluate(currentCell);
                                            if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                                commissionReconciliationData.setPayableCommission(BigDecimal.valueOf(cellValue.getNumberValue()));
                                            } else if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
                                                try {
                                                    BigDecimal value = new BigDecimal(cellValue.getStringValue());
                                                    commissionReconciliationData.setPayableCommission(value);
                                                } catch (NumberFormatException e) {
                                                    // handle exception
                                                }
                                            }

                                        }
                                        break;
                                }
                        }
                        commissionReconciliationData.setReconDate(new Date());
                        commissionReconciliationData.setStatus("N");
                        commissionReconciliationData.setUnderwriterCode(acctId);
                        commissionReconciliationDataList.add(commissionReconciliationData);
                    }
                }
                commissionReconRepo.save(commissionReconciliationDataList);
            }
        }
    }
    @Override
    @Transactional
    public DataTablesResult<CommissionReconciliationDTO> getCommissionReconciliationData(
            DataTablesRequest request, Long accountCode) {

        final String search = (request.getSearch() != null && request.getSearch().getValue() != null)
                ? "%" + request.getSearch().getValue().toLowerCase() + "%"
                : "%%";

        if(accountCode==null){
            accountCode = -2000L;
        }

        System.out.println("Account code.."+accountCode);

        // Get data and count separately
        List<Object[]> commissionReconDataList = commissionReconRepo.findCommissionReconciliationData(
                search,
                accountCode,
                request.getPageNumber(),
                request.getPageSize());

        int rowCount = 0;

        if(!commissionReconDataList.isEmpty()){
            rowCount = ((BigInteger)commissionReconDataList.get(0)[15]).intValue();
        }

        final List<CommissionReconciliationDTO> commissionReconciliationDTOS = new ArrayList<>();

        for (Object[] obj : commissionReconDataList) {
            CommissionReconciliationDTO dto = new CommissionReconciliationDTO();
            try {
                dto.setPolicyNumber((String) obj[0]);
                dto.setClientName((String) obj[1]);
                dto.setDebitRefNo((String) obj[2]);
                dto.setCreditRefNo((String) obj[3]);
                dto.setRevisionNo((String) obj[4]);
                dto.setPayment(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
                dto.setCommission(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
                dto.setWithholdingTax(obj[7] != null ? (BigDecimal) obj[7] : BigDecimal.ZERO);
                dto.setPayableCommission(obj[8] != null ? (BigDecimal) obj[8] : BigDecimal.ZERO);
                dto.setReconId(((BigInteger)obj[9]).longValue());
                dto.setTransCode((String) obj[10]);
                dto.setUnderwriterPolicyNo((String) obj[11]);
                dto.setAdminFee(obj[12] != null ? (BigDecimal) obj[12] : BigDecimal.ZERO);
                dto.setAdminFeeWhtx(obj[13] != null ? (BigDecimal) obj[13] : BigDecimal.ZERO);
                dto.setTotalRevenue(obj[14] != null ? (BigDecimal) obj[14] : BigDecimal.ZERO);
                commissionReconciliationDTOS.add(dto);
            } catch (Exception e) {
                // logger.error("Error mapping commission reconciliation data", e);
            }
        }
        Page<CommissionReconciliationDTO> page = new PageImpl<>(commissionReconciliationDTOS, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional
    public void deleteCommission(List<CommissionReconciliationDTO> commissionReconciliationData) throws BadRequestException {
        List<CommissionReconciliationData> reconciliationList = new ArrayList<>();
        for (CommissionReconciliationDTO dto : commissionReconciliationData) {
            reconciliationList.add(commissionReconRepo.findOne(dto.getReconId()));
        }
        commissionReconRepo.delete(reconciliationList);
    }

    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public List<CommissionReconciliationData> reconcileCommissionData(List<CommissionReconciliationDTO> commissionReconciliationDataList) throws BadRequestException {
        List<CommissionReconciliationData> reconciliationList = new ArrayList<>();
        List<CommissionUnreconciledData> unreconciledList = new ArrayList<>();
        String batchReference = "RECON_" + System.currentTimeMillis();

        System.out.println(commissionReconciliationDataList);

        if(commissionReconciliationDataList.isEmpty()){
            throw new BadRequestException("No Records to process. Select At Least one record to process...");
        }
        final Long accountCode = commissionReconciliationDataList.get(0).getUnderwriterCode();
        final Long currencyCode = commissionReconciliationDataList.get(0).getCurrencyCode();
        if(accountCode==null){
            throw new BadRequestException("Please select valid underwriter to process the commissions...");
        }
        if(currencyCode==null){
            throw new BadRequestException("Please select valid currency to process the commissions...");
        }
        final Currencies currencies = currencyRepo.findOne(currencyCode);
        AccountDef accountDef = accountRepo.findOne(accountCode);

        final String insuranceType = accountDef.getInsuranceType();
        if(insuranceType==null){
            throw new BadRequestException("Please select valid insurance type to process the commissions...");
        }

        Iterable<SystemTransactions> existingTrans = transRepo.findAll(QSystemTransactions.systemTransactions.clientType.eq("I")
                .and(QSystemTransactions.systemTransactions.transType.eq("COM"))
                .and(QSystemTransactions.systemTransactions.controlAcc.eq(accountDef.getShtDesc()))
                .and(QSystemTransactions.systemTransactions.agent.acctId.eq(accountDef.getAcctId()))
                .and(QSystemTransactions.systemTransactions.narrations.eq("Bulk Commissions"))
                .and((QSystemTransactions.systemTransactions.authorised.eq("P")).or(QSystemTransactions.systemTransactions.authorised.isNull())));

        Iterable<SystemTransactions> existingAdminFeeTrans = transRepo.findAll(QSystemTransactions.systemTransactions.clientType.eq("I")
                .and(QSystemTransactions.systemTransactions.transType.eq("ADM"))
                .and(QSystemTransactions.systemTransactions.controlAcc.eq(accountDef.getShtDesc()))
                .and(QSystemTransactions.systemTransactions.agent.acctId.eq(accountDef.getAcctId()))
                .and(QSystemTransactions.systemTransactions.narrations.eq("Bulk Admin Fee Trans"))
                .and((QSystemTransactions.systemTransactions.authorised.eq("P")).or(QSystemTransactions.systemTransactions.authorised.isNull())));

        final List<CommissionReconciliationDTO> transactionToProcess = new ArrayList<>();

        if(accountDef.getAccountType().getAccountType()== AccountTypeEnum.INS){
             for(CommissionReconciliationDTO commissionReconciliationDTO: commissionReconciliationDataList){
                 List<CommissionReconciliationData> commissioreconciledData  = commissionReconRepo.checkDuplicateDebitTrans(commissionReconciliationDTO.getDebitRefNo(),commissionReconciliationDTO.getCreditRefNo(),"Y");
                 List<CommissionReconciliationData> commissioreconciledDatas  = commissionReconRepo.checkDuplicateDebitTrans(commissionReconciliationDTO.getDebitRefNo(),commissionReconciliationDTO.getCreditRefNo(),"N");
                 if(!commissioreconciledData.isEmpty()){
                     CommissionUnreconciledData commissionUnreconciledData = new CommissionUnreconciledData();
                     BeanUtils.copyProperties(commissionReconciliationDTO, commissionUnreconciledData);
                     commissionUnreconciledData.setUnreconciledReason("Record Already Processed");
                     commissionUnreconciledData.setUnderwriterCode(accountCode);
                     commissionUnreconciledData.setBatchReference(batchReference);
                     unreconciledList.add(commissionUnreconciledData);

                 }
                 else {
                     if(commissioreconciledDatas.size()==1) {
                         if(commissionReconciliationDTO.getWithholdingTax()==null || commissionReconciliationDTO.getWithholdingTax().compareTo(BigDecimal.ZERO)==0){
                             CommissionUnreconciledData commissionUnreconciledData = new CommissionUnreconciledData();
                             BeanUtils.copyProperties(commissionReconciliationDTO, commissionUnreconciledData);
                             commissionUnreconciledData.setUnreconciledReason("Commission WHTX is zero");
                             commissionUnreconciledData.setBatchReference(batchReference);
                             commissionUnreconciledData.setUnderwriterCode(accountCode);
                             unreconciledList.add(commissionUnreconciledData);
                         }


                         else if(commissionReconciliationDTO.getAdminFee().compareTo(BigDecimal.ZERO)!= 0 && (commissionReconciliationDTO.getAdminFeeWhtx()==null || commissionReconciliationDTO.getAdminFeeWhtx().compareTo(BigDecimal.ZERO)==0)){

                             CommissionUnreconciledData commissionUnreconciledData = new CommissionUnreconciledData();
                             BeanUtils.copyProperties(commissionReconciliationDTO, commissionUnreconciledData);
                             commissionUnreconciledData.setUnreconciledReason("Admin WHTX is zero");
                             commissionUnreconciledData.setBatchReference(batchReference);
                             commissionUnreconciledData.setUnderwriterCode(accountCode);
                             unreconciledList.add(commissionUnreconciledData);
                         }
                         else if(commissionReconciliationDTO.getCreditRefNo()==null){

                             CommissionUnreconciledData commissionUnreconciledData = new CommissionUnreconciledData();
                             BeanUtils.copyProperties(commissionReconciliationDTO, commissionUnreconciledData);
                             commissionUnreconciledData.setUnreconciledReason("Credit Reference is null");
                             commissionUnreconciledData.setBatchReference(batchReference);
                             commissionUnreconciledData.setUnderwriterCode(accountCode);
                             unreconciledList.add(commissionUnreconciledData);
                         }
                         else {
                             List<Object[]> commTrans = commissionPaymentsRepo.getCreditCommissionByReference(commissionReconciliationDTO.getCreditRefNo(),
                                     commissionReconciliationDTO.getDebitRefNo(),"Commission");
                             if(commTrans.size() != 1){
                                 CommissionUnreconciledData commissionUnreconciledData = new CommissionUnreconciledData();
                                 BeanUtils.copyProperties(commissionReconciliationDTO, commissionUnreconciledData);
                                 commissionUnreconciledData.setUnreconciledReason("Unable to get Commission Record for the debit reference "+commissionReconciliationDTO.getDebitRefNo());
                                 commissionUnreconciledData.setUnderwriterCode(accountCode);
                                 commissionUnreconciledData.setBatchReference(batchReference);
                                 unreconciledList.add(commissionUnreconciledData);
                             }
                             else{
                                 transactionToProcess.add(commissionReconciliationDTO);
                             }
                         }
                     }
                     else{
                         CommissionUnreconciledData commissionUnreconciledData = new CommissionUnreconciledData();
                         BeanUtils.copyProperties(commissionReconciliationDTO, commissionUnreconciledData);
                         commissionUnreconciledData.setUnreconciledReason("Record Already Processed");
                         commissionUnreconciledData.setUnderwriterCode(accountCode);
                         commissionUnreconciledData.setBatchReference(batchReference);
                         unreconciledList.add(commissionUnreconciledData);
                     }

             }
        }

         BigDecimal totalPayable = BigDecimal.ZERO;
         BigDecimal totalAdminFeePayable = BigDecimal.ZERO;
            List<InsPaymentBean> insPaymentBeanList = new ArrayList<>();
            List<InsPaymentBean> insPaymentAdminFeeBeanList = new ArrayList<>();
        for(CommissionReconciliationDTO a:transactionToProcess) {
//            System.out.println(a);
            List<BigInteger> currencieList = commissionPaymentsRepo.getTransCurrency(a.getDebitRefNo(),"Commission");
            if(currencieList.size()!=1){
                currencieList =  commissionPaymentsRepo.getDebitTransCurrency(a.getDebitRefNo(),"Commission");
                if(currencieList.size()!=1) {
                    throw new BadRequestException("Error getting currency for transaction ref " + a.getDebitRefNo());
                }
            }
            BigInteger currCode = currencieList.get(0);
            List<Object[]> commTrans = commissionPaymentsRepo.getCreditCommissionByReference(a.getCreditRefNo(), a.getDebitRefNo(),"Commission");

            System.out.println("Debit Count "+commTrans.size()+" DR Ref "+a.getDebitRefNo()+" CR Ref "+a.getCreditRefNo());
            SystemTransactions transactions = systemTransactionsRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(a.getDebitRefNo())
                    .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    .and(QSystemTransactions.systemTransactions.transType.in("NBD", "RND", "APD","BUD", "RED"))
                    .and(QSystemTransactions.systemTransactions.clientType.eq("C")));
            if(insuranceType.equalsIgnoreCase("LIFE")){
                transactions = systemTransactionsRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(a.getDebitRefNo())
                        .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                        .and(QSystemTransactions.systemTransactions.transType.in("NBD", "RND", "APD","BUD", "RED"))
                        .and(QSystemTransactions.systemTransactions.clientType.eq("A")));
            }
            if(!commTrans.isEmpty()) {
                final BigDecimal payableComm = (a.getCommission().add(a.getWithholdingTax()));
                if (payableComm.compareTo(BigDecimal.ZERO) != 0) {
                    for (Object[] tran : commTrans) {
                        final Long commId = ((BigInteger) tran[3]).longValue();
                        final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(commId);
                        commissionPayments.setLoadAmount(a.getCommission());
                        commissionPayments.setLoadWhtx(a.getWithholdingTax());
                        commissionPayments.setLoadNetAmount(payableComm);
                        commissionPayments.setProcessedBy(userUtils.getCurrentUser());
                        CommissionPayments saved = commissionPaymentsRepo.save(commissionPayments);
                        InsPaymentBean insPaymentBean = new InsPaymentBean();
                        insPaymentBean.setAmount(a.getCommission().abs());
                        insPaymentBean.setDebiTrans(a.getDebitRefNo());
                        insPaymentBean.setTransType("NML");
                        insPaymentBean.setCreditTrans(a.getCreditRefNo());
                        insPaymentBean.setTransId(saved.getTransId());
                        insPaymentBeanList.add(insPaymentBean);
                        totalPayable = totalPayable.add(payableComm);
                    }
                }
            }
            List<Object[]>transAdmin = commissionPaymentsRepo.getCreditCommissionByReference(a.getCreditRefNo(), a.getDebitRefNo(),"Admin Fee");
            if(!transAdmin.isEmpty()) {
                final BigDecimal netAdminFee = a.getAdminFee().add(a.getAdminFeeWhtx());
                System.out.println("Net Admin Fee..." + netAdminFee);
                if (netAdminFee.compareTo(BigDecimal.ZERO) != 0) {
                    for (Object[] tran : transAdmin) {
                        final Long commId = ((BigInteger) tran[3]).longValue();
                        final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(commId);
                        commissionPayments.setLoadAmount(a.getAdminFee().abs());
                        commissionPayments.setLoadWhtx(a.getAdminFeeWhtx().abs());
                        commissionPayments.setProcessedBy(userUtils.getCurrentUser());
                        commissionPayments.setLoadNetAmount(netAdminFee.abs());
                        CommissionPayments saved = commissionPaymentsRepo.save(commissionPayments);
                        InsPaymentBean insPaymentBean = new InsPaymentBean();
                        insPaymentBean.setAmount(netAdminFee.abs());
                        insPaymentBean.setDebiTrans(a.getDebitRefNo());
                        insPaymentBean.setTransType("ADM");
                        insPaymentBean.setCreditTrans(a.getCreditRefNo());
                        insPaymentBean.setTransId(saved.getTransId());
                        insPaymentAdminFeeBeanList.add(insPaymentBean);
                        totalAdminFeePayable = totalAdminFeePayable.add(netAdminFee);
                    }
                }
            }
            for(InsPaymentBean paymentBean:insPaymentBeanList){
                List<CommissionReconciliationData> commissioreconciledDatas  = commissionReconRepo.checkDuplicateDebitTrans(paymentBean.getDebiTrans(),paymentBean.getCreditTrans(),"N");

                commissioreconciledDatas.forEach(x -> {
                    x.setStatus("Y");
                });
                commissionReconRepo.save(commissioreconciledDatas);
            }
        }
//            if(insPaymentBeanList.isEmpty() && insPaymentAdminFeeBeanList.isEmpty()){
//                //throw new BadRequestException("No Transaction to Process...Please check your data....");
//                throw new BadRequestException("Policies not fully paid cannot be bulk comm receipted. Please check your data....");
//            }
            System.out.println("Total Commission "+totalPayable+" Total Admin Fee "+totalAdminFeePayable);
//
//            if(totalPayable.compareTo(BigDecimal.ZERO)==0 && totalAdminFeePayable.compareTo(BigDecimal.ZERO)==0){
//                throw new BadRequestException("Unable to Process Bulk Commission Records Selected. The total Payable cannot be zero...");
//            }


            if(totalPayable.compareTo(BigDecimal.ZERO) > 0 && !insPaymentBeanList.isEmpty()) {
                processCreditorCommissions(existingTrans, totalPayable, accountDef, currencies, insPaymentBeanList, "Bulk Commissions");
            }
            if(totalAdminFeePayable.compareTo(BigDecimal.ZERO) > 0 && !insPaymentAdminFeeBeanList.isEmpty()){
                processCreditorAdmin(existingAdminFeeTrans,totalAdminFeePayable,accountDef,currencies,insPaymentAdminFeeBeanList,"Bulk Admin Fee");
            }





        }
//        // Save all unreconciled data
        if (!unreconciledList.isEmpty()) {
            List<CommissionReconciliationData> all = new ArrayList<>();
            unreconciledList.forEach(a -> {
                List<CommissionReconciliationData> commissioreconciledDatas  = commissionReconRepo.checkDuplicateDebitTrans(a.getDebitRefNo(),a.getCreditRefNo(),"N");
                commissioreconciledDatas.forEach(x -> {
                    x.setStatus("Y");
                });
                all.addAll(commissioreconciledDatas);
            });
            commissionReconRepo.save(all);

            commissionUnreconciledRepo.save(unreconciledList);
            System.out.println("SAVED " + unreconciledList.size() + " UNRECONCILED RECORDS");
        }

        return reconciliationList;
    }


    private void processCreditorCommissions(Iterable<SystemTransactions> existingTrans, BigDecimal totalCredit, AccountDef accountDef, Currencies currency, final List<InsPaymentBean> paymentBeanList, final String type) throws BadRequestException{
        if(existingTrans.spliterator().getExactSizeIfKnown() > 1) throw  new BadRequestException("There can be only one unauthorised transactions to be processed. Consult the admin..");
        if (existingTrans.spliterator().getExactSizeIfKnown() == 0) {
            Predicate adminPredicate = QSystemSequence.systemSequence.transType.eq("CRCP");
            if (sequenceRepo.count(adminPredicate) == 0)
                throw new AdminFeeException("Sequence for Creditor Commissions Payments has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(adminPredicate);
            Long seqNumber = sequence.getNextNumber();
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            SystemTrans transaction = new SystemTrans();
            transaction.setDoneDate(new Date());
            transaction.setDoneBy(userUtils.getCurrentUser());
            transaction.setTransLevel("U");
            transaction.setTransCode("COM"); //A way to setup and look up for transaction transcode
            transaction.setTransAuthorised("N");
            SystemTrans createdTrans = systransRepo.save(transaction);
            SystemTransactions trans = new SystemTransactions();
            trans.setAmount(totalCredit.abs());
            trans.setBalance(totalCredit.abs());
            trans.setClientType("I");
            trans.setAgent(accountDef);
            trans.setTransDate(new Date());
            trans.setRefNo(accountDef.getShtDesc() + "/" + String.format("%04d", seqNumber));
            trans.setTransType("COM");
            trans.setTransdc("D");
            trans.setControlAcc(accountDef.getShtDesc());
            trans.setCurrency(currencyRepo.findOne(currency.getCurCode()));
            trans.setCurrRate(BigDecimal.ONE);
            trans.setNarrations(type);
            trans.setAuthorised("P");
            trans.setPostedDate(new Date());
            trans.setPostedUser(userUtils.getCurrentUser());
            trans.setNetAmount(totalCredit.abs());
            trans.setTransaction(createdTrans);
            trans.setPayeeName(accountDef.getName());
            trans.setOrigin("U");
            SystemTransactions savedTrans = transRepo.save(trans);
            Currencies currencies = savedTrans.getCurrency();
            List<PaymentAudit> audits = new ArrayList<>();
            List<SystemTransactions> receipts = new ArrayList<>();
            for (InsPaymentBean insPaymentBean : paymentBeanList) {

                final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(insPaymentBean.getTransId());

                if(commissionPayments==null){
                    throw new BadRequestException("Unable to get Commission Transaction to process....");
                }

                commissionPayments.setAuthorised("P");
                commissionPaymentsRepo.save(commissionPayments);


//                Iterable<PaymentAudit> prevAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
//                BigDecimal prevSettledAmt = BigDecimal.ZERO;
//                for (PaymentAudit paymentAudit : prevAudits) {
//                    prevSettledAmt = prevSettledAmt.add(paymentAudit.getPaymentAmount());
//                }
//                SystemTransactions agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
//                        .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","RND")))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                SystemTransactions agentTrans = null;
                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                        .and(QSystemTransactions.systemTransactions.transType.in("APC"))) == 0) {
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND", "APC","BUD", "RED", "RPC", "NBR","CNC")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("C"))
                    );
                } else {
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("APC","BUD", "RED","RPC","NBR","CNC")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    );
                }

                //To take care of life policy transactions...
                if(agentTrans==null){
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","BUD", "RED","RPC","NBR","CNC")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    );
                }
                SystemTransactions creditTrans = null;
                ReceiptSettlementDetails settlements = null;
                List<BigInteger> settlement = settlementRepo.getCreditSettlements(insPaymentBean.getDebiTrans(),insPaymentBean.getCreditTrans());
                if(settlement.size()==1){
                    settlements = settlementRepo.findOne(settlement.get(0).longValue());
                }
//                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C"))) == 1)
//                    creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                            .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                else {
//                    long count = settlementRepo.count(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
//                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
//                    if (count == 1) {
//                        creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
//                                .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC")))).getCredit();
//
//                    }
//                }
//                if (!"CLB".equalsIgnoreCase(insPaymentBean.getTransType())) {
//                    if (creditTrans == null)
//                        throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
//                }
//                double prorationRate = insPaymentBean.getAmount().doubleValue() / (agentTrans.getNetAmount().abs().doubleValue());
                SystemTransactions sysTrans = transRepo.findOne(agentTrans.getTransno());
//                BigDecimal prorata = new BigDecimal(prorationRate);
                Iterable<PaymentAudit> paymentAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
                if (paymentAudits.spliterator().getExactSizeIfKnown() > 1) {
                    throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                }
                PaymentAudit audit = new PaymentAudit();

                if (paymentAudits.spliterator().getExactSizeIfKnown() == 0) {
                    audit = new PaymentAudit();
                } else {
                    for (PaymentAudit paymentAudit : paymentAudits) {
                        audit = paymentAudit;
                        break;
                    }
                }
                audit.setTransType("NML");
                audit.setSettlements(settlements);
                audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                audit.setCommAmount(commissionPayments.getAmount());
                audit.setPaymentAmount(commissionPayments.getNetAmount());
                audit.setOtherTransNo(savedTrans);
                audit.setCommissionPayments(commissionPayments);
                audit.setTransNo(sysTrans);
                audit.setWhtxAmount(commissionPayments.getWhtx());
                audits.add(audit);
//                audits.add(audit);
//                if (!"CLB".equalsIgnoreCase(insPaymentBean.getTransType())) {
//                    creditTrans.setTempSettleAmt((creditTrans.getTempSettleAmt() == null) ? insPaymentBean.getAmount() : creditTrans.getTempSettleAmt().add(insPaymentBean.getAmount()).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                    receipts.add(creditTrans);
//                }
            }
            auditRepo.save(audits);
            transRepo.save(receipts);
        } else {
            SystemTransactions transactions = null;
            for (SystemTransactions trans : existingTrans) {
                transactions = trans;
            }
            if (transactions == null)
                throw new BadRequestException("Error getting insurance transaction to process. Processing consult System Admin");
            Currencies currencies = transactions.getCurrency();
            transactions.setAmount(transactions.getAmount().add(totalCredit.abs()));
            transactions.setBalance(transactions.getBalance().add(totalCredit.abs()));
            transactions.setNetAmount(transactions.getNetAmount().add(totalCredit.abs()));
            transRepo.save(transactions);
            List<PaymentAudit> audits = new ArrayList<>();
            List<SystemTransactions> receipts = new ArrayList<>();
            for (InsPaymentBean insPaymentBean : paymentBeanList) {
//                Iterable<PaymentAudit> prevAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
//                BigDecimal prevSettledAmt = BigDecimal.ZERO;
//                for (PaymentAudit paymentAudit : prevAudits) {
//                    prevSettledAmt = prevSettledAmt.add(paymentAudit.getPaymentAmount());
//                }
                final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(insPaymentBean.getTransId());

                if(commissionPayments==null){
                    throw new BadRequestException("Unable to get Commission Transaction to process....");
                }

                commissionPayments.setAuthorised("P");
                commissionPaymentsRepo.save(commissionPayments);

//                SystemTransactions sysTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
//                        .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD")))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                double prorationRate = insPaymentBean.getAmount().doubleValue() / (sysTrans.getNetAmount().abs().doubleValue());
//                BigDecimal prorata = new BigDecimal(prorationRate);
//                SystemTransactions creditTrans = null;
                ReceiptSettlementDetails settlements = null;
                long counts = settlementRepo.count(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                        .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                if (counts == 1) {
                    settlements = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                    System.out.println("Settlement found...." + settlements);
                }

                SystemTransactions agentTrans = null;
                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                        .and(QSystemTransactions.systemTransactions.transType.in("APC"))) == 0) {
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND", "APC")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("C"))
                    );
                } else {
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("APC")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    );
                }

                //To take care of life policy transactions...
                if(agentTrans==null){
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    );
                }
                SystemTransactions sysTrans = transRepo.findOne(agentTrans.getTransno());

//                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C"))) == 1)
//                    creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                            .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                else
//                    creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
//                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC")))).getCredit();
//
//                if (creditTrans == null)
//                    throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
//
                Iterable<PaymentAudit> paymentAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
                if (paymentAudits.spliterator().getExactSizeIfKnown() > 1) {
                    throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                }
                PaymentAudit audit = new PaymentAudit();

                if (paymentAudits.spliterator().getExactSizeIfKnown() == 0) {
                    audit = new PaymentAudit();
                } else {
                    for (PaymentAudit paymentAudit : paymentAudits) {
                        audit = paymentAudit;
                        break;
                    }
                }

                System.out.println("Here..."+insPaymentBean.getAmount());
                audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                audit.setCommAmount(commissionPayments.getAmount());
                audit.setPaymentAmount(commissionPayments.getNetAmount());
                audit.setOtherTransNo(transactions);
                audit.setCommissionPayments(commissionPayments);
                audit.setTransNo(sysTrans);
                audit.setWhtxAmount(commissionPayments.getWhtx());
                audit.setSettlements(settlements);
                audits.add(audit);
//                creditTrans.setTempSettleAmt((creditTrans.getTempSettleAmt() == null) ? insPaymentBean.getAmount() : creditTrans.getTempSettleAmt().add(insPaymentBean.getAmount()).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                receipts.add(creditTrans);
            }
            auditRepo.save(audits);
            transRepo.save(receipts);
        }
    }


    private void processCreditorAdmin(Iterable<SystemTransactions> existingTrans, BigDecimal totalCredit, AccountDef accountDef, Currencies currency, final List<InsPaymentBean> paymentBeanList, final String type) throws BadRequestException{
        if(existingTrans.spliterator().getExactSizeIfKnown() > 1) throw  new BadRequestException("There can be only one unauthorised transactions to be processed. Consult the admin..");
        if (existingTrans.spliterator().getExactSizeIfKnown() == 0) {
            Predicate adminPredicate = QSystemSequence.systemSequence.transType.eq("CRCP");
            if (sequenceRepo.count(adminPredicate) == 0)
                throw new AdminFeeException("Sequence for Creditor Commissions Payments has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(adminPredicate);
            Long seqNumber = sequence.getNextNumber();
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            SystemTrans transaction = new SystemTrans();
            transaction.setDoneDate(new Date());
            transaction.setDoneBy(userUtils.getCurrentUser());
            transaction.setTransLevel("U");
            transaction.setTransCode("COM"); //A way to setup and look up for transaction transcode
            transaction.setTransAuthorised("N");
            SystemTrans createdTrans = systransRepo.save(transaction);
            SystemTransactions trans = new SystemTransactions();
            trans.setAmount(totalCredit.abs());
            trans.setBalance(totalCredit.abs());
            trans.setClientType("I");
            trans.setAgent(accountDef);
            trans.setTransDate(new Date());
            trans.setRefNo(accountDef.getShtDesc() + "/" + String.format("%04d", seqNumber));
            trans.setTransType("COM");
            trans.setTransdc("D");
            trans.setControlAcc(accountDef.getShtDesc());
            trans.setCurrency(currencyRepo.findOne(currency.getCurCode()));
            trans.setCurrRate(BigDecimal.ONE);
            trans.setNarrations(type);
            trans.setAuthorised("P");
            trans.setPostedDate(new Date());
            trans.setPostedUser(userUtils.getCurrentUser());
            trans.setNetAmount(totalCredit.abs());
            trans.setTransaction(createdTrans);
            trans.setPayeeName(accountDef.getName());
            trans.setOrigin("U");
            SystemTransactions savedTrans = transRepo.save(trans);
            Currencies currencies = savedTrans.getCurrency();
            List<PaymentAudit> audits = new ArrayList<>();
            List<SystemTransactions> receipts = new ArrayList<>();
            for (InsPaymentBean insPaymentBean : paymentBeanList) {

                final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(insPaymentBean.getTransId());

                if(commissionPayments==null){
                    throw new BadRequestException("Unable to get Commission Transaction to process....");
                }

                commissionPayments.setAuthorised("P");
                commissionPaymentsRepo.save(commissionPayments);


//                Iterable<PaymentAudit> prevAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
//                BigDecimal prevSettledAmt = BigDecimal.ZERO;
//                for (PaymentAudit paymentAudit : prevAudits) {
//                    prevSettledAmt = prevSettledAmt.add(paymentAudit.getPaymentAmount());
//                }
//                SystemTransactions agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
//                        .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","RND")))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                SystemTransactions agentTrans = null;
                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                        .and(QSystemTransactions.systemTransactions.transType.in("APC"))) == 0) {
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND", "APC","BUD", "RED")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("C"))
                    );
                } else {
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("APC","BUD", "RED")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    );
                }

                //To take care of life policy transactions...
                if(agentTrans==null){
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","BUD", "RED")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    );
                }
                SystemTransactions creditTrans = null;
                ReceiptSettlementDetails settlements = null;
                List<BigInteger> settlement = settlementRepo.getCreditSettlements(insPaymentBean.getDebiTrans(),insPaymentBean.getCreditTrans());
                if(settlement.size()==1){
                    settlements = settlementRepo.findOne(settlement.get(0).longValue());
                }
//                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C"))) == 1)
//                    creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                            .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                else {
//                    long count = settlementRepo.count(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
//                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
//                    if (count == 1) {
//                        creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
//                                .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC")))).getCredit();
//
//                    }
//                }
//                if (!"CLB".equalsIgnoreCase(insPaymentBean.getTransType())) {
//                    if (creditTrans == null)
//                        throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
//                }
//                double prorationRate = insPaymentBean.getAmount().doubleValue() / (agentTrans.getNetAmount().abs().doubleValue());
                SystemTransactions sysTrans = transRepo.findOne(agentTrans.getTransno());
//                BigDecimal prorata = new BigDecimal(prorationRate);
                Iterable<PaymentAudit> paymentAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
                if (paymentAudits.spliterator().getExactSizeIfKnown() > 1) {
                    throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                }
                PaymentAudit audit = new PaymentAudit();

                if (paymentAudits.spliterator().getExactSizeIfKnown() == 0) {
                    audit = new PaymentAudit();
                } else {
                    for (PaymentAudit paymentAudit : paymentAudits) {
                        audit = paymentAudit;
                        break;
                    }
                }
                audit.setTransType("ADM");
                audit.setSettlements(settlements);
                audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                audit.setCommAmount(commissionPayments.getAmount());
                audit.setPaymentAmount(commissionPayments.getNetAmount());
                audit.setOtherTransNo(savedTrans);
                audit.setCommissionPayments(commissionPayments);
                audit.setTransNo(sysTrans);
                audit.setWhtxAmount(commissionPayments.getWhtx());
                audits.add(audit);
//                audits.add(audit);
//                if (!"CLB".equalsIgnoreCase(insPaymentBean.getTransType())) {
//                    creditTrans.setTempSettleAmt((creditTrans.getTempSettleAmt() == null) ? insPaymentBean.getAmount() : creditTrans.getTempSettleAmt().add(insPaymentBean.getAmount()).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                    receipts.add(creditTrans);
//                }
            }
            auditRepo.save(audits);
            transRepo.save(receipts);
        } else {
            SystemTransactions transactions = null;
            for (SystemTransactions trans : existingTrans) {
                transactions = trans;
            }
            if (transactions == null)
                throw new BadRequestException("Error getting insurance transaction to process. Processing consult System Admin");
            Currencies currencies = transactions.getCurrency();
            transactions.setAmount(transactions.getAmount().add(totalCredit.abs()));
            transactions.setBalance(transactions.getBalance().add(totalCredit.abs()));
            transactions.setNetAmount(transactions.getNetAmount().add(totalCredit.abs()));
            transRepo.save(transactions);
            List<PaymentAudit> audits = new ArrayList<>();
            List<SystemTransactions> receipts = new ArrayList<>();
            for (InsPaymentBean insPaymentBean : paymentBeanList) {
//                Iterable<PaymentAudit> prevAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
//                BigDecimal prevSettledAmt = BigDecimal.ZERO;
//                for (PaymentAudit paymentAudit : prevAudits) {
//                    prevSettledAmt = prevSettledAmt.add(paymentAudit.getPaymentAmount());
//                }
                final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(insPaymentBean.getTransId());

                if(commissionPayments==null){
                    throw new BadRequestException("Unable to get Commission Transaction to process....");
                }

                commissionPayments.setAuthorised("P");
                commissionPaymentsRepo.save(commissionPayments);

//                SystemTransactions sysTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
//                        .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD")))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                double prorationRate = insPaymentBean.getAmount().doubleValue() / (sysTrans.getNetAmount().abs().doubleValue());
//                BigDecimal prorata = new BigDecimal(prorationRate);
//                SystemTransactions creditTrans = null;
                ReceiptSettlementDetails settlements = null;
                long counts = settlementRepo.count(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                        .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                if (counts == 1) {
                    settlements = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                    System.out.println("Settlement found...." + settlements);
                }

                SystemTransactions agentTrans = null;
                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                        .and(QSystemTransactions.systemTransactions.transType.in("APC"))) == 0) {
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND", "APC")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("C"))
                    );
                } else {
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("APC")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    );
                }

                //To take care of life policy transactions...
                if(agentTrans==null){
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    );
                }
                SystemTransactions sysTrans = transRepo.findOne(agentTrans.getTransno());

//                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C"))) == 1)
//                    creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                            .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                else
//                    creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
//                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC")))).getCredit();
//
//                if (creditTrans == null)
//                    throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
//
                Iterable<PaymentAudit> paymentAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
                if (paymentAudits.spliterator().getExactSizeIfKnown() > 1) {
                    throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                }
                PaymentAudit audit = new PaymentAudit();

                if (paymentAudits.spliterator().getExactSizeIfKnown() == 0) {
                    audit = new PaymentAudit();
                } else {
                    for (PaymentAudit paymentAudit : paymentAudits) {
                        audit = paymentAudit;
                        break;
                    }
                }

                System.out.println("Here..."+insPaymentBean.getAmount());
                audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                audit.setCommAmount(commissionPayments.getAmount());
                audit.setTransType("ADM");
                audit.setPaymentAmount(commissionPayments.getNetAmount());
                audit.setOtherTransNo(transactions);
                audit.setCommissionPayments(commissionPayments);
                audit.setTransNo(sysTrans);
                audit.setWhtxAmount(commissionPayments.getWhtx());
                audit.setSettlements(settlements);
                audits.add(audit);
//                creditTrans.setTempSettleAmt((creditTrans.getTempSettleAmt() == null) ? insPaymentBean.getAmount() : creditTrans.getTempSettleAmt().add(insPaymentBean.getAmount()).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                receipts.add(creditTrans);
            }
            auditRepo.save(audits);
            transRepo.save(receipts);
        }
    }

    // Helper method to create unreconciled data
    private CommissionUnreconciledData createUnreconciledData(CommissionReconciliationData savedData,
                                                              String systemClientName,
                                                              BigDecimal systemCommission,
                                                              BigDecimal systemWhtx,
                                                              BigDecimal systemPayment,
                                                              String reason,
                                                              String batchReference) {
        CommissionUnreconciledData unreconciledData = new CommissionUnreconciledData();
        unreconciledData.setPolicyNumber(savedData.getPolicyNumber());
        unreconciledData.setClientName(savedData.getClientName());
        unreconciledData.setDebitRefNo(savedData.getDebitRefNo());
        unreconciledData.setCreditRefNo(savedData.getCreditRefNo());
        unreconciledData.setPayment(savedData.getPayment());
        unreconciledData.setCommission(savedData.getCommission());
        unreconciledData.setWithholdingTax(savedData.getWithholdingTax());
        unreconciledData.setPayableCommission(savedData.getPayableCommission());
        unreconciledData.setUnreconciledReason(reason);
        unreconciledData.setSystemClientName(systemClientName);
        unreconciledData.setSystemCommission(systemCommission);
        unreconciledData.setSystemWhtx(systemWhtx);
        unreconciledData.setSystemPayment(systemPayment);
        unreconciledData.setCreatedDate(new Date());
        unreconciledData.setBatchReference(batchReference);

        return unreconciledData;
    }

    // Helper method for amount comparison
    private boolean isAmountEqual(BigDecimal amount1, BigDecimal amount2, BigDecimal tolerance) {
        if (amount1 == null && amount2 == null) return true;
        if (amount1 == null || amount2 == null) return false;

        BigDecimal difference = amount1.subtract(amount2).abs();
        return difference.compareTo(tolerance) <= 0;
    }
    @Override
    @Transactional
    public DataTablesResult<CommissionUnreconciledDataDTO> getUnreconciledCommissionData(DataTablesRequest request, Long accountCode) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null)
                ? "%" + request.getSearch().getValue().toLowerCase() + "%"
                : "%%";

        List<Object[]> unreconciledDataList = commissionUnreconciledRepo.findUnreconciledCommissionData(
                search,
                request.getPageNumber(),
                request.getPageSize(),
                accountCode);

        final List<CommissionUnreconciledDataDTO> unreconciledDTOS = new ArrayList<>();

        long rowCount = 0L;
        if (!unreconciledDataList.isEmpty() && unreconciledDataList.get(0)[17] != null) {
            rowCount = ((BigInteger) unreconciledDataList.get(0)[17]).longValue();
        }
        System.out.println("Row Count.."+rowCount+" search "+search+" Page No "+request.getPageNumber()+" Page Size "+request.getPageSize());

        for (Object[] obj : unreconciledDataList) {
            CommissionUnreconciledDataDTO dto = new CommissionUnreconciledDataDTO();
                dto.setId(((BigInteger) obj[0]).longValue());
                dto.setPolicyNumber((String) obj[1]);
                dto.setClientName((String) obj[2]);
                dto.setDebitRefNo((String) obj[3]);
                dto.setCreditRefNo((String) obj[4]);
                dto.setRevisionNo((String) obj[5]);
                dto.setPayment(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
                dto.setCommission(obj[7] != null ? (BigDecimal) obj[7] : BigDecimal.ZERO);
                dto.setWithholdingTax(obj[8] != null ? (BigDecimal) obj[8] : BigDecimal.ZERO);
                dto.setPayableCommission(obj[9] != null ? (BigDecimal) obj[9] : BigDecimal.ZERO);
                dto.setUnreconciledReason((String) obj[10]);
                dto.setSystemClientName((String) obj[11]);
                dto.setSystemCommission(obj[12] != null ? (BigDecimal) obj[12] : null);
                dto.setSystemWhtx(obj[13] != null ? (BigDecimal) obj[13] : null);
                dto.setSystemPayment(obj[14] != null ? (BigDecimal) obj[14] : null);
                dto.setCreatedDate((Date) obj[15]);
                dto.setBatchReference((String) obj[16]);
                unreconciledDTOS.add(dto);

        }


        Page<CommissionUnreconciledDataDTO> page = new PageImpl<>(unreconciledDTOS, request, rowCount);
        return new DataTablesResult<>(request, page);

    }



}
