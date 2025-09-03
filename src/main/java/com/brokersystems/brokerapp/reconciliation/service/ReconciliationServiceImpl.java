package com.brokersystems.brokerapp.reconciliation.service;

import com.brokersystems.brokerapp.accounts.model.QPaymentAudit;
import com.brokersystems.brokerapp.accounts.repository.PaymentAuditRepo;
import com.brokersystems.brokerapp.reconciliation.Reconciliation;
import com.brokersystems.brokerapp.reconciliation.ReconciliationDTO;
import com.brokersystems.brokerapp.reconciliation.ReconciliationData;
import com.brokersystems.brokerapp.reconciliation.repository.ReconciliationDataRepo;
import com.brokersystems.brokerapp.reconciliation.repository.ReconciliationRepo;
import com.brokersystems.brokerapp.reconciliation.TransReconciliation;
import com.brokersystems.brokerapp.reconciliation.TransReconciliationDTO;
import com.brokersystems.brokerapp.reconciliation.TransReconciliationData;
import com.brokersystems.brokerapp.reconciliation.repository.TransReconciliationDataRepo;
import com.brokersystems.brokerapp.reconciliation.repository.TransReconciliationRepo;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.trans.model.QSystemTransactions;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReconciliationServiceImpl implements ReconciliationService {
    @Autowired
    private ReconciliationRepo reconciliationRepo;
    @Autowired
    private ReconciliationDataRepo reconciliationDataRepo;
    @Autowired
    private TransReconciliationRepo transReconciliationRepo;
    @Autowired
    private TransReconciliationDataRepo transReconciliationDataRepo;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;
    @Autowired
    private PaymentAuditRepo paymentAuditRepo;


    @Override
    @Transactional
    public void processReconExcel(MultipartFile file) throws IOException {
        List<ReconciliationData> reconciliationDataList = new ArrayList<>();
        Map<Integer, String> columns = new HashMap<>();
        byte[] byteArr = file.getBytes();
        InputStream excelFile = new ByteArrayInputStream(byteArr);
        Workbook workbook = new HSSFWorkbook(excelFile);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (i == 0) {

                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    Row currentRow = rowIterator.next();
                    ReconciliationData reconciliationData = new ReconciliationData();
                    if (currentRow.getRowNum() == 0) {
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
                        Iterator<Cell> cellIterator = currentRow.iterator();
                        while (cellIterator.hasNext()) {
                            Cell currentCell = cellIterator.next();
                            int columnIndex = currentCell.getColumnIndex();
                            if (columns.get(columnIndex) != null) {
                                switch (columns.get(columnIndex).toUpperCase()) {
                                    case "POLICY":
                                        reconciliationData.setPolicyNumber(String.valueOf(currentCell.getStringCellValue()));
                                        break;
                                    case "CLIENT":
                                        reconciliationData.setClientName(String.valueOf(currentCell.getStringCellValue()));
                                        break;
                                    case "RISK NOTE NUMBER":
                                        reconciliationData.setRiskNoteNumber(String.valueOf(currentCell.getStringCellValue()));
                                        break;
                                    case "UNDERWRITER POLICY NUMBER":
                                        reconciliationData.setUnderwriterPolicyNumber(String.valueOf(currentCell.getStringCellValue()));
                                        break;
                                    case "PREMIUM":
                                        reconciliationData.setPremium(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        break;
                                    case "COMMISSION":
                                        reconciliationData.setCommission(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        break;
                                    case "TOTAL COMMISSION PAYABLE":
                                        reconciliationData.setPayableCommission(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        break;
                                    case "WITHHOLDING TAX":
                                        reconciliationData.setWithholdingTax(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        break;
                                }
                            }
                        }
                        reconciliationData.setReconDate(new Date());
                        reconciliationData.setStatus("N");
                        reconciliationDataList.add(reconciliationData);
                    }
                }
                reconciliationDataRepo.save(reconciliationDataList);
            }
        }
    }

    @Override
    @Transactional
    public DataTablesResult<ReconciliationDTO> getReconciliationData(DataTablesRequest request, Long accountCode, Date dateFrom, Date dateTo) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null)
                ? "%" + request.getSearch().getValue().toLowerCase() + "%"
                : "%%";
        List<Object[]> reconciledDataList = reconciliationDataRepo.findReconciliationData(search.toLowerCase(), dateFrom, dateTo, accountCode, request.getPageNumber(), request.getPageSize());
        final List<ReconciliationDTO> reconciliationDTOS = new ArrayList<>();

        long rowCount = 0L;
        if (!reconciledDataList.isEmpty()) {
            rowCount = ((BigInteger) reconciledDataList.get(0)[8]).intValue();
        }

        for (Object[] obj : reconciledDataList) {
            ReconciliationDTO reconciliationDTO = new ReconciliationDTO();
            reconciliationDTO.setPolicyNumber((String) obj[0]);
            reconciliationDTO.setClientName((String) obj[1]);
            reconciliationDTO.setRiskNoteNumber((String) obj[2]);
            reconciliationDTO.setUnderwriterPolicyNumber((String) obj[3]);
            reconciliationDTO.setPremium((BigDecimal) obj[4]);
            reconciliationDTO.setCommission((BigDecimal) obj[5]);
            reconciliationDTO.setPaidCommission((BigDecimal) obj[6]);
            reconciliationDTO.setPayableCommission((BigDecimal) obj[7]);
            reconciliationDTOS.add(reconciliationDTO);
        }
        Page<ReconciliationDTO> page = new PageImpl<>(reconciliationDTOS, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional
    public List<Reconciliation> reconcileData(List<ReconciliationDTO> reconciliationDataList) throws BadRequestException {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        for (ReconciliationDTO reconciliationData : reconciliationDataList) {
            List<ReconciliationData> savedDataList = reconciliationDataRepo.findByPolicyNumber(reconciliationData.getPolicyNumber());
            if (savedDataList != null && !savedDataList.isEmpty()) {
                for (ReconciliationData savedData : savedDataList) {
                    PolicyTrans policyTrans = policyTransRepo.findByPolNo(savedData.getPolicyNumber());
                    if (policyTrans != null) {
                        savedData.setStatus("Y");
//                        BigDecimal payableCommission = policyTrans.getPolTotComm().negate().subtract(policyTrans.getPolTotWhtx());
                        BigDecimal totalCommission = policyTrans.getPolTotComm() != null ? policyTrans.getPolTotComm().negate() : BigDecimal.ZERO;
                        BigDecimal totalWhtx = policyTrans.getPolTotWhtx() != null ? policyTrans.getPolTotWhtx() : BigDecimal.ZERO;
                        BigDecimal payableCommission = totalCommission.subtract(totalWhtx);
                        System.out.println("TOTAL WITHHOLDING TAX: " + policyTrans.getPolTotWhtx());
                        System.out.println("TOTAL COMMISSION: " + policyTrans.getPolTotComm().negate());
                        System.out.println("PAYABLE COMMISSION: " + payableCommission);
                        Reconciliation reconciliation = new Reconciliation();
                        reconciliation.setReconciliationData(savedData);
                        reconciliation.setReconDate(new Date());
                        String firstName = policyTrans.getClient().getFname();
                        String lastName = policyTrans.getClient().getOtherNames();
                        String name = firstName + " " + lastName;
                        String underwriterPolNumber = policyTrans.getClientPolNo() != null ? policyTrans.getClientPolNo() : "";
                        String riskNoteNumber = policyTrans.getRefNo() != null ? policyTrans.getRefNo() : "";
                        List<BigInteger> mainTransList = reconciliationRepo.getMainTrans();
                        System.out.println("MAIN TRANS LIST: " + mainTransList);
                        if (mainTransList != null && !mainTransList.isEmpty()) {
                            for (BigInteger trans : mainTransList) {
                                if(reconciliationRepo.getAudits(trans.longValue()) != null) {
                                    List<Object[]> childTransList = reconciliationRepo.findChildTrans(trans.longValue());
                                    for (Object[] obj : childTransList) {
                                        System.out.println("child trans: " + Arrays.toString(obj));
                                        BigDecimal transCommission = (BigDecimal) obj[7];
                                        String transPosted = (String) obj[10];
                                        BigDecimal transBalance = (BigDecimal) obj[11];
                                        if (policyTrans.getPolTotPrem().equals(savedData.getPremium())
                                                && name.contains(savedData.getClientName())
                                                && (riskNoteNumber.isEmpty() || riskNoteNumber.equals(savedData.getRiskNoteNumber()))
                                                && (underwriterPolNumber.isEmpty() || underwriterPolNumber.equals(savedData.getUnderwriterPolicyNumber()))
                                                && payableCommission.equals(savedData.getPayableCommission())
                                                && transPosted.equalsIgnoreCase("Y")
                                                && !Objects.equals(transBalance, BigDecimal.ZERO)
                                                && policyTrans.getPolTotComm().negate().equals(savedData.getCommission())) {
                                            reconciliation.setStatus("Paid");
                                            reconciliation.setUnmatchedEntries("None");

                                        } else {
                                            reconciliation.setStatus("Unpaid");
                                            List<String> unmatchedEntries = new ArrayList<>();
                                            if (!policyTrans.getPremium().equals(savedData.getPremium())) {
                                                unmatchedEntries.add("Premium");
                                            }
                                            if (!name.contains(savedData.getClientName())) {
                                                unmatchedEntries.add("Client");
                                            }
                                            if (!riskNoteNumber.equals(savedData.getRiskNoteNumber())) {
                                                unmatchedEntries.add("Risk Note Number");
                                            }
                                            if (!underwriterPolNumber.equals(savedData.getUnderwriterPolicyNumber())) {
                                                unmatchedEntries.add("Underwriter Policy Number");
                                            }
                                            if (!payableCommission.equals(savedData.getPayableCommission())) {
                                                unmatchedEntries.add("Amount Payable");
                                            }
                                            if (!transPosted.equalsIgnoreCase("Y")) {
                                                unmatchedEntries.add("Not Posted");
                                            }
//                                if (!policyTrans.getPolTotComm().equals(savedData.getPaidCommission())) {
//                                    unmatchedEntries.add("Amount Paid");
//                                }
                                            if (!policyTrans.getPolTotComm().negate().equals(savedData.getCommission())) {
                                                unmatchedEntries.add("Commission");
                                            }
                                            reconciliation.setUnmatchedEntries(String.join(",", unmatchedEntries));
                                        }
                                    }
                                }
                            }

                            reconciliationRepo.save(reconciliation);
                            reconciliationList.add(reconciliation);
                        } else {
                            throw new BadRequestException("Transaction List is Empty");
                        }
                    } else {
                        throw new BadRequestException("Reconciliation Data Not Found");
                    }
                }
            }
        }
        return reconciliationList;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ReconciliationDTO> getReconciledData(DataTablesRequest request, Long accountCode, Date dateFrom, Date dateTo) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null)
                ? "%" + request.getSearch().getValue().toLowerCase() + "%"
                : "%%";
        List<Object[]> reconciledDataList = reconciliationRepo.findReconciledData(search.toLowerCase(), dateFrom, dateTo, accountCode, request.getPageNumber(), request.getPageSize());
        final List<ReconciliationDTO> reconciliationDTOS = new ArrayList<>();

        long rowCount = 0L;
        if (!reconciledDataList.isEmpty()) {
            rowCount = ((BigInteger) reconciledDataList.get(0)[10]).intValue();
        }

        for (Object[] obj : reconciledDataList) {
            ReconciliationDTO reconciliationDTO = new ReconciliationDTO();
            reconciliationDTO.setPolicyNumber((String) obj[0]);
            reconciliationDTO.setClientName((String) obj[1]);
            reconciliationDTO.setRiskNoteNumber((String) obj[2]);
            reconciliationDTO.setUnderwriterPolicyNumber((String) obj[3]);
            reconciliationDTO.setPremium((BigDecimal) obj[4]);
            reconciliationDTO.setCommission((BigDecimal) obj[5]);
            reconciliationDTO.setPaidCommission((BigDecimal) obj[6]);
            reconciliationDTO.setPayableCommission((BigDecimal) obj[7]);
            reconciliationDTO.setStatus((String) obj[8]);
            reconciliationDTO.setUnmatchedEntries((String) obj[9]);
            reconciliationDTOS.add(reconciliationDTO);
        }
        Page<ReconciliationDTO> page = new PageImpl<>(reconciliationDTOS, request, rowCount);
        return new DataTablesResult<>(request, page);
    }


    @Override
    @Transactional
    public void processTransReconExcel(MultipartFile file) throws IOException {
        List<TransReconciliationData> transReconciliationDataList = new ArrayList<>();
        Map<Integer, String> columns = new HashMap<>();
        byte[] byteArr = file.getBytes();
        InputStream excelFile = new ByteArrayInputStream(byteArr);
        Workbook workbook = new HSSFWorkbook(excelFile);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (i == 0) {

                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    Row currentRow = rowIterator.next();
                    TransReconciliationData transReconciliationData = new TransReconciliationData();
                    if (currentRow.getRowNum() == 0) {
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
                        Iterator<Cell> cellIterator = currentRow.iterator();
                        while (cellIterator.hasNext()) {
                            Cell currentCell = cellIterator.next();
                            int columnIndex = currentCell.getColumnIndex();
                            if (columns.get(columnIndex) != null) {
                                switch (columns.get(columnIndex).toUpperCase()) {
                                    case "POLICY":
                                        transReconciliationData.setPolicyNumber(String.valueOf(currentCell.getStringCellValue()));
                                        break;
                                    case "CLIENT":
                                        transReconciliationData.setClientName(String.valueOf(currentCell.getStringCellValue()));
                                        break;
                                    case "RISK NOTE NUMBER":
                                        transReconciliationData.setRiskNoteNumber(String.valueOf(currentCell.getStringCellValue()));
                                        break;
                                    case "UNDERWRITER POLICY NUMBER":
                                        transReconciliationData.setUnderwriterPolicyNumber(String.valueOf(currentCell.getStringCellValue()));
                                        break;
                                    case "UNDERWRITER TRANS CODE":
                                        transReconciliationData.setUnderwriterTransCode(String.valueOf(currentCell.getStringCellValue()));
                                        break;
                                    case "PREMIUM":
                                        transReconciliationData.setPremium(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        break;
                                    case "SETTLEMENT":
                                        transReconciliationData.setSettlement(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        break;
                                    case "BALANCE":
                                        transReconciliationData.setBalance(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                        break;
                                }
                            }
                        }
                        transReconciliationData.setReconDate(new Date());
                        transReconciliationData.setStatus("N");
                        transReconciliationDataList.add(transReconciliationData);
                    }
                }
                transReconciliationDataRepo.save(transReconciliationDataList);
            }
        }
    }

    @Override
    @Transactional
    public DataTablesResult<TransReconciliationDTO> getTransReconciliationData(DataTablesRequest request, Long accountCode, Date dateFrom, Date dateTo, String unifiedSearch) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null)
                ? "%" + request.getSearch().getValue().toLowerCase() + "%"
                : "%%";
        List<Object[]> transReconciledDataList = transReconciliationDataRepo.findTransReconciliationData(search.toLowerCase(), dateFrom, dateTo, accountCode, request.getPageNumber(), request.getPageSize(), unifiedSearch);
        final List<TransReconciliationDTO> transReconciliationDTOS = new ArrayList<>();

        long rowCount = 0L;
        if (!transReconciledDataList.isEmpty()) {
            rowCount = ((BigInteger) transReconciledDataList.get(0)[8]).intValue();
        }

        for (Object[] obj : transReconciledDataList) {
            TransReconciliationDTO transReconciliationDTO = new TransReconciliationDTO();
            transReconciliationDTO.setPolicyNumber((String) obj[0]);
            transReconciliationDTO.setClientName((String) obj[1]);
            transReconciliationDTO.setRiskNoteNumber((String) obj[2]);
            transReconciliationDTO.setUnderwriterPolicyNumber((String) obj[3]);
            transReconciliationDTO.setUnderwriterTransCode((String) obj[4]);
            transReconciliationDTO.setPremium((BigDecimal) obj[5]);
            transReconciliationDTO.setSettlement((BigDecimal) obj[6]);
            transReconciliationDTO.setBalance((BigDecimal) obj[7]);
            transReconciliationDTOS.add(transReconciliationDTO);
        }
        Page<TransReconciliationDTO> page = new PageImpl<>(transReconciliationDTOS, request, rowCount);
        return new DataTablesResult<>(request, page);
    }


    @Override
    @Transactional
    public List<TransReconciliation> transReconcileData(List<TransReconciliationDTO> transReconciliationDataList) throws BadRequestException {
        List<TransReconciliation> transReconciliationList = new ArrayList<>();
        for (TransReconciliationDTO transReconciliationData : transReconciliationDataList) {
            TransReconciliationData savedData = transReconciliationDataRepo.findByRiskNoteNumber(transReconciliationData.getRiskNoteNumber());
            if (savedData != null) {
                String policyNumber = policyTransRepo.findPolicyNumberWithRiskNote(savedData.getRiskNoteNumber());
                if (policyNumber != null) {
                    savedData.setStatus("Y");
                    TransReconciliation transReconciliation = new TransReconciliation();
                    transReconciliation.setTransReconciliationData(savedData);
                    transReconciliation.setReconDate(new Date());

                    List<BigInteger> mainTransList = transReconciliationRepo.getMainTrans();
                    System.out.println("MAIN TRANS LIST: " + mainTransList);

                    if (mainTransList != null && !mainTransList.isEmpty()) {
                        for (BigInteger trans : mainTransList) {
                            List<Object[]> childTransList = transReconciliationRepo.findChildTrans(trans.longValue());
                            for (Object[] obj : childTransList) {
                                String childPolicyNumber = obj[0] != null ? obj[0].toString() : "";
                                String firstName = obj[1] != null ? obj[1].toString() : "";
                                String lastName = obj[2] != null ? obj[2].toString() : "";
                                String underwriterPolNumber = obj[4] != null ? obj[4].toString() : "";
                                String underwriterTransCode = obj[5] != null ? obj[5].toString() : "";
                                String riskNoteNumber = obj[6] != null ? obj[6].toString() : "";
                                BigDecimal basicPremium = obj[7] != null ? (BigDecimal) obj[7] : BigDecimal.ZERO;
                                BigDecimal transSettlement = obj[8] != null ? (BigDecimal) obj[8] : BigDecimal.ZERO;
                                BigDecimal transBalance = obj[9] != null ? (BigDecimal) obj[9] : BigDecimal.ZERO;

                                String name = firstName.trim() + " " + lastName.trim();

                                if (!riskNoteNumber.equals(savedData.getRiskNoteNumber())) {
                                    continue;
                                }
                                String childNameLower = name.trim().toLowerCase().replaceAll("\\s+", " ");
                                String savedNameLower = savedData.getClientName().trim().toLowerCase().replaceAll("\\s+", " ");

                                if (basicPremium.compareTo(savedData.getPremium()) == 0
                                        && (childNameLower.contains(savedNameLower) || savedNameLower.contains(childNameLower))
                                        && (riskNoteNumber.isEmpty() || riskNoteNumber.equals(savedData.getRiskNoteNumber()))
                                        && (underwriterTransCode.isEmpty() || underwriterTransCode.equals(savedData.getUnderwriterTransCode()))
                                        && (underwriterPolNumber.isEmpty() || underwriterPolNumber.equals(savedData.getUnderwriterPolicyNumber()))
                                        && transSettlement.compareTo(savedData.getSettlement()) == 0
                                        && transBalance.compareTo(savedData.getBalance()) == 0) {
                                    transReconciliation.setStatus("Reconciled");
                                    transReconciliation.setUnmatchedEntries("None");

                                } else {
                                    transReconciliation.setStatus("Not Reconciled");
                                    List<String> unmatchedEntries = new ArrayList<>();

                                    if (basicPremium.compareTo(savedData.getPremium()) != 0) {
                                        unmatchedEntries.add("Premium");
                                    }
                                    if (!name.contains(savedData.getClientName())) {
                                        unmatchedEntries.add("Client");
                                    }
                                    if (!riskNoteNumber.equals(savedData.getRiskNoteNumber())) {
                                        unmatchedEntries.add("Risk Note Number");
                                    }
                                    if (!underwriterPolNumber.equals(savedData.getUnderwriterPolicyNumber())) {
                                        unmatchedEntries.add("Underwriter Policy Number");
                                    }
                                    if (!underwriterTransCode.equals(savedData.getUnderwriterTransCode())) {
                                        unmatchedEntries.add("Underwriter Trans Code");
                                    }
                                    if (transSettlement.compareTo(savedData.getSettlement()) != 0) {
                                        unmatchedEntries.add("Settlement");
                                    }
                                    if (transBalance.compareTo(savedData.getBalance()) != 0) {
                                        unmatchedEntries.add("Balance");
                                    }
                                    transReconciliation.setUnmatchedEntries(String.join(",", unmatchedEntries));
                                }
                            }
                        }

                        transReconciliationRepo.save(transReconciliation);
                        transReconciliationList.add(transReconciliation);
                    } else {
                        throw new BadRequestException("Transaction List is Empty");
                    }
                } else {
                    throw new BadRequestException("Policy Number Not Found for Risk Note: " + savedData.getRiskNoteNumber());
                }
            }
        }
        return transReconciliationList;
    }
    @Override
    @Transactional
    public void deleteTransReconciledData(Long accountCode, Date dateFrom, Date dateTo, String unifiedSearch) throws BadRequestException {
        try {
            List<Long> reconDataIds = transReconciliationRepo.getReconDataIdsForDeletion(accountCode, dateFrom, dateTo, unifiedSearch);

            if (reconDataIds.isEmpty()) {
                return;
            }
            transReconciliationRepo.deleteReconciliationByIds(reconDataIds);
            transReconciliationRepo.deleteReconciledDataByIds(reconDataIds);

        } catch (Exception e) {
            throw new BadRequestException("Failed: " + e.getMessage());
        }
    }



    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<TransReconciliationDTO> getTransReconciledData(DataTablesRequest request, Long accountCode, Date dateFrom, Date dateTo, String unifiedSearch) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null)
                ? "%" + request.getSearch().getValue().toLowerCase() + "%"
                : "%%";
        List<Object[]> transReconciledDataList = transReconciliationRepo.findTransReconciledData(search.toLowerCase(), dateFrom, dateTo, accountCode, request.getPageNumber(), request.getPageSize(), unifiedSearch);
        final List<TransReconciliationDTO> transReconciliationDTOS = new ArrayList<>();

        long rowCount = 0L;
        if (!transReconciledDataList.isEmpty()) {
            rowCount = ((BigInteger) transReconciledDataList.get(0)[10]).intValue();
        }

        for (Object[] obj : transReconciledDataList) {
            TransReconciliationDTO transReconciliationDTO = new TransReconciliationDTO();
            transReconciliationDTO.setPolicyNumber((String) obj[0]);
            transReconciliationDTO.setClientName((String) obj[1]);
            transReconciliationDTO.setRiskNoteNumber((String) obj[2]);
            transReconciliationDTO.setUnderwriterPolicyNumber((String) obj[3]);
            transReconciliationDTO.setUnderwriterTransCode((String) obj[4]);
            transReconciliationDTO.setPremium((BigDecimal) obj[5]);
            transReconciliationDTO.setSettlement((BigDecimal) obj[6]);
            transReconciliationDTO.setBalance((BigDecimal) obj[7]);
            transReconciliationDTO.setStatus((String) obj[8]);
            transReconciliationDTO.setUnmatchedEntries((String) obj[9]);
            transReconciliationDTOS.add(transReconciliationDTO);
        }
        Page<TransReconciliationDTO> page = new PageImpl<>(transReconciliationDTOS, request, rowCount);
        return new DataTablesResult<>(request, page);
    }
}
