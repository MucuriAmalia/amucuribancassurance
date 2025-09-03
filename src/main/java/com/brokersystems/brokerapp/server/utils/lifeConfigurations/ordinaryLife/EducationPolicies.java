package com.brokersystems.brokerapp.server.utils.lifeConfigurations.ordinaryLife;

import com.brokersystems.brokerapp.life.model.PolicyBenefitsDistribution;
import com.brokersystems.brokerapp.life.repository.PolicyBenefitsDistributionRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.MaturitiesBean;
import com.brokersystems.brokerapp.server.utils.PremiumItemsBean;
import com.brokersystems.brokerapp.server.utils.PremiumResultBean;
import com.brokersystems.brokerapp.uw.dtos.SectionTransDTO;
import com.brokersystems.brokerapp.uw.model.PolicySurrenderValues;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicySurrenderValuesRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.SectionTransRepo;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class EducationPolicies {

    @Autowired
    private SectionTransRepo sectionRepo;

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private PolicySurrenderValuesRepo surrenderValuesRepo;

    @Autowired
    private PolicyBenefitsDistributionRepo maturityRepo;

    public void getEducationPolicyPremium(List<PremiumItemsBean> premItems,
                                          Date dob, String gender, Integer policyTerm,
                                          String frequency, Long polCode, String sheetName,
                                          String computeType, Sheet sheet, BigDecimal totalPremium,
                                          Workbook workbook, PremiumResultBean premiumResultBean,
                                          List<MaturitiesBean> maturitiesBeanList) throws BadRequestException {

        if (sheet.getSheetName().equalsIgnoreCase(sheetName) && (sheetName.equalsIgnoreCase("SA-Education Policy Quote") || sheetName.equalsIgnoreCase("PRE-Education Policy Quote"))) {
            System.out.println("Sheet name " + sheet.getSheetName());
            System.out.println(sheetName);
            BigDecimal sumAssured =premItems.stream().filter(PremiumItemsBean::isMainSection).map(PremiumItemsBean::getSumAssured).findAny().isPresent()? premItems.stream().filter(PremiumItemsBean::isMainSection).map(PremiumItemsBean::getSumAssured).findAny().get():BigDecimal.ZERO;
            BigDecimal premium =premItems.stream().filter(PremiumItemsBean::isMainSection).map(PremiumItemsBean::getPremium).findAny().isPresent()? premItems.stream().filter(PremiumItemsBean::isMainSection).map(PremiumItemsBean::getPremium).findAny().get():BigDecimal.ZERO;
            if(computeType.equalsIgnoreCase("S")) {
                if (sumAssured.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BadRequestException("Sum Assured cannot be zero for this calculator...");
                }
            }
            else    if(computeType.equalsIgnoreCase("P")) {
                System.out.println("Compute with Premium..."+premium);
                if (premium.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BadRequestException("Premium cannot be zero for this calculator..."+premium);
                }
            }
            HSSFCreationHelper createHelper = (HSSFCreationHelper) workbook.getCreationHelper();
            HSSFCellStyle cellStyle         = (HSSFCellStyle) workbook.createCellStyle();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("d-MMM-yy"));
            CellReference dateReference = new CellReference("E6");
            int r = dateReference.getRow();
            int c = dateReference.getCol();
            Row row = sheet.getRow(r);
            Cell currentCell = row.getCell(c);
            currentCell.setCellValue(dob);
            currentCell.setCellStyle(cellStyle);

            System.out.println("DOB..."+dob);

            CellReference genderReference = new CellReference("E7");
            int rows = genderReference.getRow();
            int col = genderReference.getCol();
            Row genderRow = sheet.getRow(rows);
            Cell genderCell = genderRow.getCell(col);
            genderCell.setCellValue(gender);

            System.out.println("Gender..."+gender);

            CellReference dateChildReference = new CellReference("E10");
            int rChild = dateChildReference.getRow();
            int cChild = dateChildReference.getCol();
            Row rowChild = sheet.getRow(rChild);
            Cell currentCellChild = rowChild.getCell(cChild);
            currentCellChild.setCellValue(dob);
            currentCellChild.setCellStyle(cellStyle);

            System.out.println("DOB..."+dob);

            CellReference genderChildReference = new CellReference("E11");
            int rowsChild = genderChildReference.getRow();
            int colChild = genderChildReference.getCol();
            Row genderRowChild = sheet.getRow(rowsChild);
            Cell genderCellChild = genderRowChild.getCell(colChild);
            genderCellChild.setCellValue(gender);

            System.out.println("Gender..."+gender);

            CellReference termReference = new CellReference("E13");
            rows = termReference.getRow();
            col = termReference.getCol();
            Row termRow = sheet.getRow(rows);
            Cell termCell = termRow.getCell(col);
            termCell.setCellValue(policyTerm);

            System.out.println("Policy Term..."+gender);

            CellReference freqReference = new CellReference("E23");
            rows = freqReference.getRow();
            col = freqReference.getCol();
            Row freqRow = sheet.getRow(rows);
            Cell freqCell = freqRow.getCell(col);
            String freq = "";
            if(frequency==null){
                throw new BadRequestException("Frequency cannot be null");
            }
            if(frequency.equalsIgnoreCase("M")){
                freq = "Monthly";
            }
            else if(frequency.equalsIgnoreCase("Q")){
                freq = "Quarterly";
            }
            else if(frequency.equalsIgnoreCase("S")){
                freq = "Semi-annual";
            }
            else if(frequency.equalsIgnoreCase("A")){
                freq = "Annual";
            }
            else if(frequency.equalsIgnoreCase("SG")){
                freq = "Single";
            }
            freqCell.setCellValue(freq);

            CellReference assuredReference = new CellReference("E17");
            rows = assuredReference.getRow();
            col = assuredReference.getCol();
            Row assuredRow = sheet.getRow(rows);
            Cell assuredCell = assuredRow.getCell(col);
            if(computeType.equalsIgnoreCase("S")) {
                assuredCell.setCellValue(sumAssured.doubleValue());
            }


            CellReference monthlyReference = new CellReference("E18");
            rows = monthlyReference.getRow();
            col = monthlyReference.getCol();
            Row monthlyRow = sheet.getRow(rows);
            Cell monthlyCell = monthlyRow.getCell(col);
            if(computeType.equalsIgnoreCase("P")) {
                monthlyCell.setCellValue(premium.doubleValue());
            }

            CellReference quartelyReference = new CellReference("E19");
            rows = quartelyReference.getRow();
            col = quartelyReference.getCol();
            Row quartelyRow = sheet.getRow(rows);
            Cell quartelyCell = quartelyRow.getCell(col);

            CellReference semiAnnualReference = new CellReference("E20");
            rows = semiAnnualReference.getRow();
            col = semiAnnualReference.getCol();
            Row semiAnnualRow = sheet.getRow(rows);
            Cell semiCell = semiAnnualRow.getCell(col);

            CellReference annualReference = new CellReference("E21");
            rows = annualReference.getRow();
            col = annualReference.getCol();
            Row annualRow = sheet.getRow(rows);
            Cell annualCell = annualRow.getCell(col);

            CellReference singleReference = new CellReference("E22");
            rows = singleReference.getRow();
            col = singleReference.getCol();
            Row singleRow = sheet.getRow(rows);
            Cell singleCell = singleRow.getCell(col);

            CellReference taxReliefCell = new CellReference("E71");
            rows = taxReliefCell.getRow();
            col = taxReliefCell.getCol();
            Row taxRow = sheet.getRow(rows);
            if (taxRow == null) {
                taxRow = sheet.createRow(rows);
            }
            Cell taxCell = taxRow.getCell(col);
            if (taxCell == null) {
                taxCell = taxRow.createCell(col);
            }

            if(computeType.equalsIgnoreCase("S")) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(monthlyCell);
                if (cellValue.getNumberValue() != 0) {
                    premiumResultBean.setPremium(cellValue.getNumberValue());
                    premiumResultBean.setSumInsured(sumAssured.doubleValue());
                    premium = BigDecimal.valueOf(cellValue.getNumberValue());
                }
            }
            else if(computeType.equalsIgnoreCase("P")) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(assuredCell);
                if (cellValue.getNumberValue() != 0) {
                    premiumResultBean.setPremium(premium.doubleValue());
                    premiumResultBean.setSumInsured(cellValue.getNumberValue());
                    sumAssured = BigDecimal.valueOf(cellValue.getNumberValue());
                    System.out.println("Premium..."+premium);
                    System.out.println("Sum Assured..."+sumAssured);
                }
            }

            if (monthlyCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(monthlyCell);
                if (cellValue.getNumberValue() != 0) {
                    premiumResultBean.setPremium(cellValue.getNumberValue());
                    //premiumResultBean.setSumInsured(sumAssured.doubleValue());
                    totalPremium =  BigDecimal.valueOf(cellValue.getNumberValue());
                    System.out.println("Monthly premium...."+totalPremium);
                }
            }

            if (quartelyCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(quartelyCell);
                if (cellValue.getNumberValue() != 0) {
                    premiumResultBean.setQuarterlypremium(cellValue.getNumberValue());
                    // premiumResultBean.setSumInsured(sumAssured.doubleValue());
                    totalPremium =  BigDecimal.valueOf(cellValue.getNumberValue());
                    System.out.println("Quartely premium...."+totalPremium);
                }
            }

            if (semiCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(semiCell);
                if (cellValue.getNumberValue() != 0) {
                    premiumResultBean.setSemiAnnualpremium(cellValue.getNumberValue());
                    // premiumResultBean.setSumInsured(sumAssured.doubleValue());
                    totalPremium =  BigDecimal.valueOf(cellValue.getNumberValue());
                }
            }

            if (annualCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(annualCell);
                if (cellValue.getNumberValue() != 0) {
                    premiumResultBean.setAnnualpremium(cellValue.getNumberValue());
                    //  premiumResultBean.setSumInsured(sumAssured.doubleValue());
                    totalPremium =  BigDecimal.valueOf(cellValue.getNumberValue());
                }
            }

            if (singleCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(singleCell);
                if (cellValue.getNumberValue() != 0) {
                    premiumResultBean.setSinglepremium(cellValue.getNumberValue());
                    // premiumResultBean.setSumInsured(sumAssured.doubleValue());
                    totalPremium =  BigDecimal.valueOf(cellValue.getNumberValue());
                }
            }

            if (taxCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(taxCell);
                if (cellValue.getNumberValue() != 0) {
                    premiumResultBean.setTaxRelief(cellValue.getNumberValue());
                }
            }



            if(frequency.equalsIgnoreCase("M")){
                System.out.println("Passed here...."+premiumResultBean.getPremium());
                totalPremium = BigDecimal.valueOf(premiumResultBean.getPremium());
            }
            else if(frequency.equalsIgnoreCase("Q")){
                System.out.println("Passed here..2..");
                totalPremium = BigDecimal.valueOf(premiumResultBean.getQuarterlypremium());
            }
            else if(frequency.equalsIgnoreCase("S")){
                System.out.println("Passed here...3.");
                totalPremium = BigDecimal.valueOf(premiumResultBean.getSemiAnnualpremium());
            }
            else if(frequency.equalsIgnoreCase("A")){
                totalPremium = BigDecimal.valueOf(premiumResultBean.getAnnualpremium());
            }
            else if(frequency.equalsIgnoreCase("SG")){
                totalPremium = BigDecimal.valueOf(premiumResultBean.getSinglepremium());
            }

            for(PremiumItemsBean itemsBean: premItems){
                sectionRepo.updateSectionSumAssuredDetails(BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
                        itemsBean.getSectId());
            }



            for(PremiumItemsBean itemsBean: premItems){
                if(itemsBean.getPremiumId().equalsIgnoreCase("Main Benefit")){
                    SectionTransDTO section = new SectionTransDTO();
                    List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                    if(!sectionArr.isEmpty()) {
                        final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                        final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                        final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                        final Long sectId = ((BigInteger)sectionArr.get(0)[0]).longValue();
                        final Long sectSectionId = ((BigInteger)sectionArr.get(0)[8]).longValue();
                        final Long premRateId = ((BigInteger)sectionArr.get(0)[9]).longValue();
                        section.setPremRatesId(premRateId);
                        section.setRate(rate);
                        section.setFreeLimit(freeLimit);
                        section.setDivFactor(divFactor);
                        section.setSectId(sectId);
                        section.setSectionSectId(sectSectionId);
                    }

                    section.setAmount(BigDecimal.valueOf(premiumResultBean.getSumInsured()));
                    section.setPrem(BigDecimal.valueOf(premiumResultBean.getPremium()));
                    section.setCalcprem(BigDecimal.valueOf(premiumResultBean.getPremium()));
                    sectionRepo.updateSectionSumAssuredDetails((section.getPrem()!=null)?section.getPrem():BigDecimal.ZERO,
                            (section.getCalcprem()!=null)?section.getCalcprem():BigDecimal.ZERO,(section.getAmount()!=null)?section.getAmount():BigDecimal.ZERO,
                            section.getSectId());

                }
                if(itemsBean.getPremiumId().equalsIgnoreCase("Total & Permanent Disability")){
                    CellReference riderReference = new CellReference("E26");
                    rows = riderReference.getRow();
                    col = riderReference.getCol();
                    Row riderRow = sheet.getRow(rows);
                    Cell riderCell = riderRow.getCell(col);
                    riderCell.setCellValue("Yes");
                    CellReference riderSumAssuredReference = new CellReference("F26");
                    rows = riderSumAssuredReference.getRow();
                    col = riderSumAssuredReference.getCol();
                    Row riderSumAssuredRow = sheet.getRow(rows);
                    Cell riderSumAssuredRowCellCell = riderSumAssuredRow.getCell(col);
                    SectionTransDTO section = new SectionTransDTO();
                    List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                    if(!sectionArr.isEmpty()) {
                        final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                        final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                        final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                        final Long sectId = ((BigInteger)sectionArr.get(0)[0]).longValue();
                        final Long sectSectionId = ((BigInteger)sectionArr.get(0)[8]).longValue();
                        final Long premRateId = ((BigInteger)sectionArr.get(0)[9]).longValue();
                        section.setPremRatesId(premRateId);
                        section.setRate(rate);
                        section.setFreeLimit(freeLimit);
                        section.setDivFactor(divFactor);
                        section.setSectId(sectId);
                        section.setSectionSectId(sectSectionId);
                    }

                    if (riderSumAssuredRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderSumAssuredRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setAmount(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    CellReference riderpremReference = new CellReference("E34");
                    rows = riderpremReference.getRow();
                    col = riderpremReference.getCol();
                    Row riderPremRow = sheet.getRow(rows);
                    Cell riderpremRowCellCell = riderPremRow.getCell(col);

                    if (riderpremRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderpremRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setPrem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            section.setCalcprem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            totalPremium =  totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    sectionRepo.updateSectionSumAssuredDetails((section.getPrem()!=null)?section.getPrem():BigDecimal.ZERO,
                            (section.getCalcprem()!=null)?section.getCalcprem():BigDecimal.ZERO,(section.getAmount()!=null)?section.getAmount():BigDecimal.ZERO,
                            section.getSectId());

                }
                if(itemsBean.getPremiumId().equalsIgnoreCase("Waiver of Premium")){
                    CellReference riderReference = new CellReference("E27");
                    rows = riderReference.getRow();
                    col = riderReference.getCol();
                    Row riderRow = sheet.getRow(rows);
                    Cell riderCell = riderRow.getCell(col);
                    riderCell.setCellValue("Yes");
                    CellReference riderSumAssuredReference = new CellReference("F27");
                    rows = riderSumAssuredReference.getRow();
                    col = riderSumAssuredReference.getCol();
                    Row riderSumAssuredRow = sheet.getRow(rows);
                    Cell riderSumAssuredRowCellCell = riderSumAssuredRow.getCell(col);
                    SectionTransDTO section = new SectionTransDTO();
                    List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                    if(!sectionArr.isEmpty()) {
                        final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                        final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                        final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                        final Long sectId = ((BigInteger)sectionArr.get(0)[0]).longValue();
                        final Long sectSectionId = ((BigInteger)sectionArr.get(0)[8]).longValue();
                        final Long premRateId = ((BigInteger)sectionArr.get(0)[9]).longValue();
                        section.setPremRatesId(premRateId);
                        section.setRate(rate);
                        section.setFreeLimit(freeLimit);
                        section.setDivFactor(divFactor);
                        section.setSectId(sectId);
                        section.setSectionSectId(sectSectionId);
                    }

                    if (riderSumAssuredRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderSumAssuredRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setAmount(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    CellReference riderpremReference = new CellReference("E35");
                    rows = riderpremReference.getRow();
                    col = riderpremReference.getCol();
                    Row riderPremRow = sheet.getRow(rows);
                    Cell riderpremRowCellCell = riderPremRow.getCell(col);

                    if (riderpremRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderpremRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setPrem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            section.setCalcprem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            totalPremium =  totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    sectionRepo.updateSectionSumAssuredDetails((section.getPrem()!=null)?section.getPrem():BigDecimal.ZERO,
                            (section.getCalcprem()!=null)?section.getCalcprem():BigDecimal.ZERO,(section.getAmount()!=null)?section.getAmount():BigDecimal.ZERO,
                            section.getSectId());
                }

                if(itemsBean.getPremiumId().equalsIgnoreCase("Adult Medical Reimbursement")){
                    CellReference riderReference = new CellReference("E28");
                    rows = riderReference.getRow();
                    col = riderReference.getCol();
                    Row riderRow = sheet.getRow(rows);
                    Cell riderCell = riderRow.getCell(col);
                    riderCell.setCellValue("Yes");
                    CellReference riderSumAssuredReference = new CellReference("F28");
                    rows = riderSumAssuredReference.getRow();
                    col = riderSumAssuredReference.getCol();
                    Row riderSumAssuredRow = sheet.getRow(rows);
                    Cell riderSumAssuredRowCellCell = riderSumAssuredRow.getCell(col);
                    SectionTransDTO section = new SectionTransDTO();
                    List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                    if(!sectionArr.isEmpty()) {
                        final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                        final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                        final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                        final Long sectId = ((BigInteger)sectionArr.get(0)[0]).longValue();
                        final Long sectSectionId = ((BigInteger)sectionArr.get(0)[8]).longValue();
                        final Long premRateId = ((BigInteger)sectionArr.get(0)[9]).longValue();
                        section.setPremRatesId(premRateId);
                        section.setRate(rate);
                        section.setFreeLimit(freeLimit);
                        section.setDivFactor(divFactor);
                        section.setSectId(sectId);
                        section.setSectionSectId(sectSectionId);
                    }

                    if (riderSumAssuredRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderSumAssuredRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setAmount(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    CellReference riderpremReference = new CellReference("E36");
                    rows = riderpremReference.getRow();
                    col = riderpremReference.getCol();
                    Row riderPremRow = sheet.getRow(rows);
                    Cell riderpremRowCellCell = riderPremRow.getCell(col);

                    if (riderpremRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderpremRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setPrem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            section.setCalcprem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            totalPremium =  totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    sectionRepo.updateSectionSumAssuredDetails((section.getPrem()!=null)?section.getPrem():BigDecimal.ZERO,
                            (section.getCalcprem()!=null)?section.getCalcprem():BigDecimal.ZERO,(section.getAmount()!=null)?section.getAmount():BigDecimal.ZERO,
                            section.getSectId());
                }
                if(itemsBean.getPremiumId().equalsIgnoreCase("Child Medical Reimbursement")){
                    CellReference riderReference = new CellReference("E29");
                    rows = riderReference.getRow();
                    col = riderReference.getCol();
                    Row riderRow = sheet.getRow(rows);
                    Cell riderCell = riderRow.getCell(col);
                    riderCell.setCellValue("Yes");
                    CellReference riderSumAssuredReference = new CellReference("F29");
                    rows = riderSumAssuredReference.getRow();
                    col = riderSumAssuredReference.getCol();
                    Row riderSumAssuredRow = sheet.getRow(rows);
                    Cell riderSumAssuredRowCellCell = riderSumAssuredRow.getCell(col);
                    SectionTransDTO section = new SectionTransDTO();
                    List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                    if(!sectionArr.isEmpty()) {
                        final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                        final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                        final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                        final Long sectId = ((BigInteger)sectionArr.get(0)[0]).longValue();
                        final Long sectSectionId = ((BigInteger)sectionArr.get(0)[8]).longValue();
                        final Long premRateId = ((BigInteger)sectionArr.get(0)[9]).longValue();
                        section.setPremRatesId(premRateId);
                        section.setRate(rate);
                        section.setFreeLimit(freeLimit);
                        section.setDivFactor(divFactor);
                        section.setSectId(sectId);
                        section.setSectionSectId(sectSectionId);
                    }

                    if (riderSumAssuredRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderSumAssuredRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setAmount(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    CellReference riderpremReference = new CellReference("E37");
                    rows = riderpremReference.getRow();
                    col = riderpremReference.getCol();
                    Row riderPremRow = sheet.getRow(rows);
                    Cell riderpremRowCellCell = riderPremRow.getCell(col);

                    if (riderpremRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderpremRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setPrem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            section.setCalcprem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            totalPremium =  totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    sectionRepo.updateSectionSumAssuredDetails((section.getPrem()!=null)?section.getPrem():BigDecimal.ZERO,
                            (section.getCalcprem()!=null)?section.getCalcprem():BigDecimal.ZERO,(section.getAmount()!=null)?section.getAmount():BigDecimal.ZERO,
                            section.getSectId());
                }
                if(itemsBean.getPremiumId().equalsIgnoreCase("Critical illness")){
                    CellReference riderReference = new CellReference("E30");
                    rows = riderReference.getRow();
                    col = riderReference.getCol();
                    Row riderRow = sheet.getRow(rows);
                    Cell riderCell = riderRow.getCell(col);
                    riderCell.setCellValue("Yes");
                    CellReference riderSumAssuredReference = new CellReference("F30");
                    rows = riderSumAssuredReference.getRow();
                    col = riderSumAssuredReference.getCol();
                    Row riderSumAssuredRow = sheet.getRow(rows);
                    Cell riderSumAssuredRowCellCell = riderSumAssuredRow.getCell(col);
                    SectionTransDTO section = new SectionTransDTO();
                    List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                    if(!sectionArr.isEmpty()) {
                        final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                        final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                        final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                        final Long sectId = ((BigInteger)sectionArr.get(0)[0]).longValue();
                        final Long sectSectionId = ((BigInteger)sectionArr.get(0)[8]).longValue();
                        final Long premRateId = ((BigInteger)sectionArr.get(0)[9]).longValue();
                        section.setPremRatesId(premRateId);
                        section.setRate(rate);
                        section.setFreeLimit(freeLimit);
                        section.setDivFactor(divFactor);
                        section.setSectId(sectId);
                        section.setSectionSectId(sectSectionId);
                    }

                    if (riderSumAssuredRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderSumAssuredRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setAmount(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    CellReference riderpremReference = new CellReference("E38");
                    rows = riderpremReference.getRow();
                    col = riderpremReference.getCol();
                    Row riderPremRow = sheet.getRow(rows);
                    Cell riderpremRowCellCell = riderPremRow.getCell(col);

                    if (riderpremRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderpremRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setPrem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            section.setCalcprem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            totalPremium =  totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    sectionRepo.updateSectionSumAssuredDetails((section.getPrem()!=null)?section.getPrem():BigDecimal.ZERO,
                            (section.getCalcprem()!=null)?section.getCalcprem():BigDecimal.ZERO,(section.getAmount()!=null)?section.getAmount():BigDecimal.ZERO,
                            section.getSectId());
                }
                if(itemsBean.getPremiumId().equalsIgnoreCase("Retrenchment")){
                    CellReference riderReference = new CellReference("E31");
                    rows = riderReference.getRow();
                    col = riderReference.getCol();
                    Row riderRow = sheet.getRow(rows);
                    Cell riderCell = riderRow.getCell(col);
                    riderCell.setCellValue("Yes");
                    CellReference riderSumAssuredReference = new CellReference("F31");
                    rows = riderSumAssuredReference.getRow();
                    col = riderSumAssuredReference.getCol();
                    Row riderSumAssuredRow = sheet.getRow(rows);
                    Cell riderSumAssuredRowCellCell = riderSumAssuredRow.getCell(col);
                    SectionTransDTO section = new SectionTransDTO();
                    List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                    if(!sectionArr.isEmpty()) {
                        final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                        final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                        final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                        final Long sectId = ((BigInteger)sectionArr.get(0)[0]).longValue();
                        final Long sectSectionId = ((BigInteger)sectionArr.get(0)[8]).longValue();
                        final Long premRateId = ((BigInteger)sectionArr.get(0)[9]).longValue();
                        section.setPremRatesId(premRateId);
                        section.setRate(rate);
                        section.setFreeLimit(freeLimit);
                        section.setDivFactor(divFactor);
                        section.setSectId(sectId);
                        section.setSectionSectId(sectSectionId);
                    }

                    if (riderSumAssuredRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderSumAssuredRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setAmount(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    CellReference riderpremReference = new CellReference("E39");
                    rows = riderpremReference.getRow();
                    col = riderpremReference.getCol();
                    Row riderPremRow = sheet.getRow(rows);
                    Cell riderpremRowCellCell = riderPremRow.getCell(col);

                    if (riderpremRowCellCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(riderpremRowCellCell);
                        if (cellValue.getNumberValue() != 0) {
                            section.setPrem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            section.setCalcprem(BigDecimal.valueOf(cellValue.getNumberValue()));
                            totalPremium =  totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                        }
                    }
                    sectionRepo.updateSectionSumAssuredDetails((section.getPrem()!=null)?section.getPrem():BigDecimal.ZERO,
                            (section.getCalcprem()!=null)?section.getCalcprem():BigDecimal.ZERO,(section.getAmount()!=null)?section.getAmount():BigDecimal.ZERO,
                            section.getSectId());
                }
            }
            System.out.println(">>>>>>>premium at sheet validation class"+totalPremium);
            final PolicyTrans policyTrans = policyRepo.findOne(polCode);

            CellReference revBonusReference = new CellReference("E45");
            rows = revBonusReference.getRow();
            col = revBonusReference.getCol();
            Row revBonusPremRow = sheet.getRow(rows);
            Cell revBonusPremRowCell = revBonusPremRow.getCell(col);

            if (revBonusPremRowCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(revBonusPremRowCell);
                if (cellValue.getNumberValue() != 0) {
                    policyTrans.setRevBonus(BigDecimal.valueOf(cellValue.getNumberValue()));
                }
            }

            CellReference taxReliefReference = new CellReference("E71");
            rows = taxReliefReference.getRow();
            col = taxReliefReference.getCol();
            Row taxReliefPremRow = sheet.getRow(rows);
            Cell taxReliefPremRowCell = taxReliefPremRow.getCell(col);




            if (taxReliefPremRowCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(taxReliefPremRowCell);
                if (cellValue.getNumberValue() != 0) {
                    policyTrans.setTaxRelief(BigDecimal.valueOf(cellValue.getNumberValue()));
                }
            }

            CellReference termBonusReference = new CellReference("E46");
            rows = termBonusReference.getRow();
            col = termBonusReference.getCol();
            Row termBonusPremRow = sheet.getRow(rows);
            Cell termBonusPremRowCell = termBonusPremRow.getCell(col);

            if (termBonusPremRowCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(termBonusPremRowCell);
                if (cellValue.getNumberValue() != 0) {
                    policyTrans.setTerminalBonus(BigDecimal.valueOf(cellValue.getNumberValue()));
                }
            }

            CellReference year1MaturityReference = new CellReference("K24");
            rows = year1MaturityReference.getRow();
            col = year1MaturityReference.getCol();
            Row year1MaturityPremRow = sheet.getRow(rows);
            Cell year1MaturityPremRowCell = year1MaturityPremRow.getCell(col);

            if (year1MaturityPremRowCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(year1MaturityPremRowCell);
                if (cellValue.getNumberValue() != 0) {
                    MaturitiesBean maturitiesBean = new MaturitiesBean();
                    maturitiesBean.setBenefitYear("Year 1");
                    maturitiesBean.setEstBenefit(cellValue.getNumberValue());
                    maturitiesBeanList.add(maturitiesBean);
                }
            }

            CellReference year2MaturityReference = new CellReference("K25");
            rows = year2MaturityReference.getRow();
            col = year2MaturityReference.getCol();
            Row year2MaturityPremRow = sheet.getRow(rows);
            Cell year2MaturityPremRowCell = year2MaturityPremRow.getCell(col);

            if (year2MaturityPremRowCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(year2MaturityPremRowCell);
                if (cellValue.getNumberValue() != 0) {
                    MaturitiesBean maturitiesBean = new MaturitiesBean();
                    maturitiesBean.setBenefitYear("Year 2");
                    maturitiesBean.setEstBenefit(cellValue.getNumberValue());
                    maturitiesBeanList.add(maturitiesBean);
                }
            }

            CellReference year3MaturityReference = new CellReference("K26");
            rows = year3MaturityReference.getRow();
            col = year3MaturityReference.getCol();
            Row year3MaturityPremRow = sheet.getRow(rows);
            Cell year3MaturityPremRowCell = year3MaturityPremRow.getCell(col);

            if (year3MaturityPremRowCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(year3MaturityPremRowCell);
                if (cellValue.getNumberValue() != 0) {
                    MaturitiesBean maturitiesBean = new MaturitiesBean();
                    maturitiesBean.setBenefitYear("Year 3");
                    maturitiesBean.setEstBenefit(cellValue.getNumberValue());
                    maturitiesBeanList.add(maturitiesBean);
                }
            }

            CellReference year4MaturityReference = new CellReference("K27");
            rows = year4MaturityReference.getRow();
            col = year4MaturityReference.getCol();
            Row year4MaturityPremRow = sheet.getRow(rows);
            Cell year4MaturityPremRowCell = year4MaturityPremRow.getCell(col);

            if (year4MaturityPremRowCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(year4MaturityPremRowCell);
                if (cellValue.getNumberValue() != 0) {
                    MaturitiesBean maturitiesBean = new MaturitiesBean();
                    maturitiesBean.setBenefitYear("Year 4");
                    maturitiesBean.setEstBenefit(cellValue.getNumberValue());
                    maturitiesBeanList.add(maturitiesBean);
                }
            }

            CellReference year5MaturityReference = new CellReference("K28");
            rows = year5MaturityReference.getRow();
            col = year5MaturityReference.getCol();
            Row year5MaturityPremRow = sheet.getRow(rows);
            Cell year5MaturityPremRowCell = year5MaturityPremRow.getCell(col);

            if (year5MaturityPremRowCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(year5MaturityPremRowCell);
                if (cellValue.getNumberValue() != 0) {
                    MaturitiesBean maturitiesBean = new MaturitiesBean();
                    maturitiesBean.setBenefitYear("Year 5 and Above");
                    maturitiesBean.setEstBenefit(cellValue.getNumberValue());
                    maturitiesBeanList.add(maturitiesBean);
                }
            }
            int initial = 50;
            final List<PolicySurrenderValues> surrenderValuesList = new ArrayList<>();
            int start = 2;
            for(int x =0 ; x < 18; x++){
                initial++;
                start++;
                CellReference surrenderReference = new CellReference("E"+initial);
                rows = surrenderReference.getRow();
                col = surrenderReference.getCol();
                Row surrenderRow = sheet.getRow(rows);
                if (surrenderRow == null) {
                    surrenderRow = sheet.createRow(rows);
                }
                Cell surrenderRowCell = surrenderRow.getCell(col);
                if (surrenderRowCell == null) {
                    surrenderRowCell = surrenderRow.createCell(col);
                }

                if (surrenderRowCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(surrenderRowCell);
                    if (cellValue.getNumberValue() != 0) {
                        PolicySurrenderValues surrenderValues = new PolicySurrenderValues();
                        surrenderValues.setSurrenderYear("Year "+start);
                        surrenderValues.setSurrenderValue(BigDecimal.valueOf(cellValue.getNumberValue()));
                        surrenderValues.setPolicyId(policyTrans);
                        surrenderValuesList.add(surrenderValues);
                    }
                }
            }
            surrenderValuesRepo.save(surrenderValuesList);
        }
        //add another sheet validation
        ArrayList<PolicyBenefitsDistribution> newMaturities = new ArrayList<>();
        PolicyTrans policy = policyRepo.findOne(polCode);
        for(MaturitiesBean bean:maturitiesBeanList){
            PolicyBenefitsDistribution newMat = new PolicyBenefitsDistribution();
            if(bean.getEstBenefit()!=0) {
                String yr =  bean.getBenefitYear();
                newMat.setEstBenefit(bean.getEstBenefit());
                newMat.setMaturityYear(yr);
                newMat.setPolicyId(policy);
                newMaturities.add(newMat);
            }
        }
        maturityRepo.save(newMaturities);
        premiumResultBean.setPremium(totalPremium.doubleValue());
    }

}
