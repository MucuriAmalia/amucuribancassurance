package com.brokersystems.brokerapp.updatepolno.service.Impl;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.server.utils.ValidatorUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.AccountRepo;
import com.brokersystems.brokerapp.setup.repository.ClientRepository;
import com.brokersystems.brokerapp.setup.repository.ProductsRepo;
import com.brokersystems.brokerapp.updatepolno.dto.UpdateClientPolNoDTO;
import com.brokersystems.brokerapp.updatepolno.modal.UpdateClientPolNo;
import com.brokersystems.brokerapp.updatepolno.repository.UpdateClientPolNoRepo;
import com.brokersystems.brokerapp.updatepolno.service.UpdateClientPolNoService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class UpdateClientPolNoServiceImpl implements UpdateClientPolNoService {

    @Autowired
    private UserUtils userUtils;
    @Autowired
    private UpdateClientPolNoRepo updatedRepo;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private ProductsRepo productsRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public Map<String, Object> uploadClientPol(MultipartFile file) throws BadRequestException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BadRequestException("Upload files with .xlsx or .xls extension only");
        }

        Map<String, Object> response = new HashMap<>();
        List<String> invalidRecords = new ArrayList<>();
        List<UpdateClientPolNo> clientPolNos = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheetOne = workbook.getSheetAt(0);

            int startRowIndex = findDataStartRow(sheetOne);
            System.out.println("startRowIndex" + startRowIndex);
            System.out.println("Last Row Num: " + sheetOne.getLastRowNum());
            if (startRowIndex == -1) {
                throw new BadRequestException("Sheet One contains no data.");
            }

            for (int rowNum = startRowIndex; rowNum <= sheetOne.getLastRowNum(); rowNum++) {
                Row row = sheetOne.getRow(rowNum);
                if (isBlankRow(row)) continue;

                try {
                    UpdateClientPolNo clientPolNo = new UpdateClientPolNo();

                    String polNo = getCellStringValue(row.getCell(0));
                    String updatePol = getCellStringValue(row.getCell(1));
                    String underwriterTransCode = getCellStringValue(row.getCell(2));
                    String riskNote = getCellStringValue(row.getCell(3));

                    PolicyTrans policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.refNo.equalsIgnoreCase(riskNote)
                            .and(QPolicyTrans.policyTrans.authStatus.eq("A")));
                    if (policyTrans != null){
                        clientPolNo.setUpdatedPolicy(policyTrans);
                    } else {
                        throw new BadRequestException("No active policy transaction found for risk note number " + riskNote);
                    }
                    Long clientId = policyTrans.getClient().getTenId();
                    ClientDef clientDef = clientRepository.findOne(clientId);
                    AccountDef accountDef = accountRepo.findOne(policyTrans.getAgent().getAcctId());
                    ProductsDef productsDef = productsRepo.findOne(policyTrans.getProduct().getProCode());
                    boolean status = policyTrans.getPolnoUpdate();
                    if (!status) {

                        if (!policyTrans.getPolNo().equalsIgnoreCase(polNo)) {
                            throw new BadRequestException("Provided Insuremaster policy number " + polNo + " does not match system policy number for the risk note number " + riskNote);
                        }
                        clientPolNo.setInsuremasterPolNo(polNo);
                        clientPolNo.setUnderwriterPolNo(updatePol);
                        clientPolNo.setClientDef(clientDef);
                        clientPolNo.setInsurerId(accountDef);
                        clientPolNo.setProductId(productsDef);
                        clientPolNo.setRiskNote(riskNote);
                        clientPolNo.setUnderwriterTransCode(underwriterTransCode);
                        clientPolNo.setUploadedBy(userUtils.getCurrentUser());
                        clientPolNo.setUploadDate(new Date());
                        clientPolNo.setUpdateStatus("N");

                        clientPolNos.add(clientPolNo);
                    } else {
                        throw new BadRequestException("Risk note number " + riskNote + " already updated with underwriter policy number " + policyTrans.getClientPolNo());
                    }


                } catch (Exception e) {
                    throw new BadRequestException("Sheet One, Row " + (rowNum + 1) + ": " + e.getMessage());
                }

                int successfulPol = clientPolNos.size();
                int invalidPol = invalidRecords.size();

                if (!(invalidPol > 0) && successfulPol > 0) {
                    updatedRepo.save(clientPolNos);
                }
            }

            return response;

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
                if (cellValue.contains("insuremaster policy no") ||
                        cellValue.contains("underwriter policy no") ||
                        cellValue.contains("risk note No") ||
                        cellValue.contains("underwriter trans code")) {
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
    public DataTablesResult<UpdateClientPolNoDTO> findUnprocessedUpdates(DataTablesRequest request) {
        List<Object[]> unprossedUpdates = updatedRepo.findUnProcessedUpdates((request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%", request.getPageNumber(), request.getPageSize());
        final List<UpdateClientPolNoDTO> updateList= new ArrayList<>();
        long rowCount = 0L;
        if (!unprossedUpdates.isEmpty()) rowCount = ((BigInteger) unprossedUpdates.get(0)[10]).intValue();
        for (Object[] updatePol : unprossedUpdates) {
            UpdateClientPolNoDTO updateDTO = new UpdateClientPolNoDTO();
            updateDTO.setUpdateId(((BigInteger) updatePol[0]).longValue());
            updateDTO.setClientName((String) updatePol[1] + " " + (String) updatePol[2]);
            updateDTO.setPolNo((String) updatePol[3]);
            updateDTO.setClientPol((String) updatePol[4]);
            updateDTO.setRiskNote((String) updatePol[5]);
            updateDTO.setProductName((String) updatePol[6]);
            updateDTO.setInsurerCode((String) updatePol[7]);
            updateDTO.setUploadDate((Date) updatePol[8]);
            updateDTO.setUnderwriterTransCode((String) updatePol[9]);
            updateList.add(updateDTO);
        }

        Page<UpdateClientPolNoDTO> page = new PageImpl<>(updateList, request, rowCount);

        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<UpdateClientPolNoDTO> viewProcessedUpdates(DataTablesRequest request, Long agentCode, Boolean status, Date dateFrom, Date dateTo) {
        LocalDateTime wefLocal = dateTo.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .plusDays(1)
                .minusSeconds(1);

        Date wet = Date.from(wefLocal.atZone(ZoneId.systemDefault()).toInstant());

        List<Object[]> pages = updatedRepo.getPolicyNoStatus(dateFrom, wet, agentCode, status);
        List<UpdateClientPolNoDTO> clientPolNoDTOS = new ArrayList<>();
        long rowCount = 0L;
        if (!pages.isEmpty()) rowCount = ((BigInteger) pages.get(0)[8]).intValue();

        for (Object[] page : pages) {
        UpdateClientPolNoDTO viewDTO = new UpdateClientPolNoDTO();
        viewDTO.setRiskNote((String) page[0]);
        viewDTO.setClientName((String) page[1] + " " + (String) page[2]);
        viewDTO.setPolNo((String) page[3]);
        viewDTO.setClientPol((String) page[4]);
        viewDTO.setProductName((String) page[5]);
        viewDTO.setProcessedDate((Date) page[6]);
        viewDTO.setPolauthDate((Date) page[7]);

        clientPolNoDTOS.add(viewDTO);
            System.out.println("clientPolNoDTOS" +clientPolNoDTOS);
        }


        Page<UpdateClientPolNoDTO> pageResult = new PageImpl<>(clientPolNoDTOS, request, rowCount);

        return new DataTablesResult<>(request, pageResult);
    }

    @Override
    public String processPolNoUpdates(List<Long> updateIds) throws BadRequestException {
        if (updateIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long updateId : updateIds) {
            UpdateClientPolNo updatedpols =  updatedRepo.findByUpdateId(updateId); // updatedRepo.findOne(updateId);
            PolicyTrans policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(updatedpols.getUpdatedPolicy().getPolicyId()));
            policyTrans.setClientPolNo(updatedpols.getUnderwriterPolNo());
            policyTrans.setPolnoUpdate(true);
            policyTrans.setUnderwriterTransCode(updatedpols.getUnderwriterTransCode());
            updatedpols.setUpdateStatus("Y");
            updatedpols.setProcessedBy(userUtils.getCurrentUser());
            updatedpols.setProcessedDate(new Date());

            updatedRepo.save(updatedpols);
            policyTransRepo.save(policyTrans);

        }
        return "Insurer policy numbers updated successfully";
    }

    @Override
    public String deleteUploadedPolNoUpdates(List<Long> updateIds) throws BadRequestException {
        if (updateIds.size() <= 0) {
            throw new BadRequestException("Select At least One Transaction To Process");
        }
        for (Long updateId : updateIds) {
            UpdateClientPolNo updatedPol = updatedRepo.findOne(updateId);
            if("N".equalsIgnoreCase(updatedPol.getUpdateStatus())) {
                updatedRepo.delete(updatedPol);
            }
        }
        return "Deleted Successfully.";
    }
}
