package com.brokersystems.brokerapp.server.utils.lifeConfigurations.life;

import com.brokersystems.brokerapp.life.model.PolicyBenefitsDistribution;
import com.brokersystems.brokerapp.life.repository.PolicyBenefitsDistributionRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.MaturitiesBean;
import com.brokersystems.brokerapp.server.utils.PremiumItemsBean;
import com.brokersystems.brokerapp.server.utils.PremiumResultBean;
import com.brokersystems.brokerapp.uw.dtos.SectionTransDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
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
public class WholeLifeExcelUtils {

    @Autowired
    private SectionTransRepo sectionRepo;

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private PolicyBenefitsDistributionRepo maturityRepo;

    public PremiumResultBean getWholeLifePremium(List<PremiumItemsBean> premItems,
                                                 final Date dob, final String gender, final Integer policyTerm,
                                                 final String frequency, final Long polCode, final String computeType, final Sheet sheet,
                                                 final Workbook workbook, final PremiumResultBean premiumResultBean) throws IOException, BadRequestException {

        BigDecimal totalPremium = BigDecimal.ZERO;
        List<MaturitiesBean> maturitiesBeanList = new ArrayList<>();
        BigDecimal sumAssured = premItems.stream().filter(PremiumItemsBean::isMainSection).map(PremiumItemsBean::getSumAssured).findAny().isPresent() ? premItems.stream().filter(PremiumItemsBean::isMainSection).map(PremiumItemsBean::getSumAssured).findAny().get() : BigDecimal.ZERO;
        BigDecimal premium = premItems.stream().filter(PremiumItemsBean::isMainSection).map(PremiumItemsBean::getPremium).findAny().isPresent() ? premItems.stream().filter(PremiumItemsBean::isMainSection).map(PremiumItemsBean::getPremium).findAny().get() : BigDecimal.ZERO;
        if (computeType.equalsIgnoreCase("S")) {
            if (sumAssured.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Sum Assured cannot be zero for this calculator...");
            }
        } else if (computeType.equalsIgnoreCase("P")) {
            System.out.println("Compute with Premium..." + premium);
            if (premium.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Premium cannot be zero for this calculator..." + premium);
            }
        }
        HSSFCreationHelper createHelper = (HSSFCreationHelper) workbook.getCreationHelper();
        HSSFCellStyle cellStyle = (HSSFCellStyle) workbook.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("d-MMM-yy"));
        CellReference dateReference = new CellReference("E6");
        int r = dateReference.getRow();
        int c = dateReference.getCol();
        Row row = sheet.getRow(r);
        Cell currentCell = row.getCell(c);
        currentCell.setCellValue(dob);
        currentCell.setCellStyle(cellStyle);

        System.out.println("DOB..." + dob);

        CellReference genderReference = new CellReference("E7");
        int rows = genderReference.getRow();
        int col = genderReference.getCol();
        Row genderRow = sheet.getRow(rows);
        Cell genderCell = genderRow.getCell(col);
        genderCell.setCellValue(gender);

        System.out.println("Gender..." + gender);

        CellReference termReference = new CellReference("E10");
        rows = termReference.getRow();
        col = termReference.getCol();
        Row termRow = sheet.getRow(rows);
        Cell termCell = termRow.getCell(col);
        termCell.setCellValue(policyTerm);

        System.out.println("Policy Term..." + policyTerm);

        CellReference freqReference = new CellReference("E19");
        rows = freqReference.getRow();
        col = freqReference.getCol();
        Row freqRow = sheet.getRow(rows);
        Cell freqCell = freqRow.getCell(col);
        String freq = "";
        if (frequency == null) {
            throw new BadRequestException("Frequency cannot be null");
        }
        if (frequency.equalsIgnoreCase("M")) {
            freq = "Monthly";
        } else if (frequency.equalsIgnoreCase("Q")) {
            freq = "Quarterly";
        } else if (frequency.equalsIgnoreCase("S")) {
            freq = "Semi-annual";
        } else if (frequency.equalsIgnoreCase("A")) {
            freq = "Annual";
        }
        freqCell.setCellValue(freq);

        CellReference assuredReference = new CellReference("E13");
        rows = assuredReference.getRow();
        col = assuredReference.getCol();
        Row assuredRow = sheet.getRow(rows);
        Cell assuredCell = assuredRow.getCell(col);
        if (computeType.equalsIgnoreCase("S")) {
            assuredCell.setCellValue(sumAssured.doubleValue());
        }


        CellReference monthlyReference = new CellReference("E14");
        rows = monthlyReference.getRow();
        col = monthlyReference.getCol();
        Row monthlyRow = sheet.getRow(rows);
        Cell monthlyCell = monthlyRow.getCell(col);
        if (computeType.equalsIgnoreCase("P")) {
            monthlyCell.setCellValue(premium.doubleValue());
        }

        CellReference quartelyReference = new CellReference("E15");
        rows = quartelyReference.getRow();
        col = quartelyReference.getCol();
        Row quartelyRow = sheet.getRow(rows);
        Cell quartelyCell = quartelyRow.getCell(col);

        CellReference semiAnnualReference = new CellReference("E16");
        rows = semiAnnualReference.getRow();
        col = semiAnnualReference.getCol();
        Row semiAnnualRow = sheet.getRow(rows);
        Cell semiCell = semiAnnualRow.getCell(col);

        CellReference annualReference = new CellReference("E17");
        rows = annualReference.getRow();
        col = annualReference.getCol();
        Row annualRow = sheet.getRow(rows);
        Cell annualCell = annualRow.getCell(col);

        CellReference taxReliefCell = new CellReference("E47");
        rows = taxReliefCell.getRow();
        col = taxReliefCell.getCol();
        Row taxRow = sheet.getRow(rows);
        Cell taxCell = taxRow.getCell(col);

        if (computeType.equalsIgnoreCase("S")) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(monthlyCell);
            if (cellValue.getNumberValue() != 0) {
                premiumResultBean.setPremium(cellValue.getNumberValue());
                premiumResultBean.setSumInsured(sumAssured.doubleValue());
                premium = BigDecimal.valueOf(cellValue.getNumberValue());
                System.out.println("Premium..." + premium);
                System.out.println("Sum Assured..." + sumAssured);
            }
        } else if (computeType.equalsIgnoreCase("P")) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(assuredCell);
            if (cellValue.getNumberValue() != 0) {
                premiumResultBean.setPremium(premium.doubleValue());
                premiumResultBean.setSumInsured(cellValue.getNumberValue());
                sumAssured = BigDecimal.valueOf(cellValue.getNumberValue());
                System.out.println("Premium..." + premium);
                System.out.println("Sum Assured..." + sumAssured);
            }
        }
        if (monthlyCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(monthlyCell);
            if (cellValue.getNumberValue() != 0) {
                premiumResultBean.setPremium(cellValue.getNumberValue());
                // premiumResultBean.setSumInsured(sumAssured.doubleValue());
                totalPremium = BigDecimal.valueOf(cellValue.getNumberValue());
                System.out.println("Monthly premium...." + totalPremium);
            }
        }

        if (quartelyCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(quartelyCell);
            if (cellValue.getNumberValue() != 0) {
                premiumResultBean.setQuarterlypremium(cellValue.getNumberValue());
                // premiumResultBean.setSumInsured(sumAssured.doubleValue());
                totalPremium = BigDecimal.valueOf(cellValue.getNumberValue());
                System.out.println("Quartely premium...." + totalPremium);
            }
        }

        if (semiCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(semiCell);
            if (cellValue.getNumberValue() != 0) {
                premiumResultBean.setSemiAnnualpremium(cellValue.getNumberValue());
                // premiumResultBean.setSumInsured(sumAssured.doubleValue());
                totalPremium = BigDecimal.valueOf(cellValue.getNumberValue());
            }
        }

        if (annualCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(annualCell);
            if (cellValue.getNumberValue() != 0) {
                premiumResultBean.setAnnualpremium(cellValue.getNumberValue());
                //  premiumResultBean.setSumInsured(sumAssured.doubleValue());
                totalPremium = BigDecimal.valueOf(cellValue.getNumberValue());
            }
        }
        if (taxCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(taxCell);
            if (cellValue.getNumberValue() != 0) {
                premiumResultBean.setTaxRelief(cellValue.getNumberValue());
            }
        }

        if (frequency.equalsIgnoreCase("M")) {
            totalPremium = BigDecimal.valueOf(premiumResultBean.getPremium());
        } else if (frequency.equalsIgnoreCase("Q")) {
            totalPremium = BigDecimal.valueOf(premiumResultBean.getQuarterlypremium());
        } else if (frequency.equalsIgnoreCase("S")) {
            totalPremium = BigDecimal.valueOf(premiumResultBean.getSemiAnnualpremium());
        } else if (frequency.equalsIgnoreCase("A")) {
            totalPremium = BigDecimal.valueOf(premiumResultBean.getAnnualpremium());
        }
        for (PremiumItemsBean itemsBean : premItems) {
            sectionRepo.updateSectionSumAssuredDetails(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    itemsBean.getSectId());
        }
        for (PremiumItemsBean itemsBean : premItems) {
            if (itemsBean.getPremiumId().equalsIgnoreCase("Main Benefit")) {
                SectionTransDTO section = new SectionTransDTO();
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
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
                sectionRepo.updateSectionSumAssuredDetails((section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                        (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO, (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                        section.getSectId());

            }
            if (itemsBean.getPremiumId().equalsIgnoreCase("Total & Permanent Disability")) {
                CellReference riderReference = new CellReference("E22");
                rows = riderReference.getRow();
                col = riderReference.getCol();
                Row riderRow = sheet.getRow(rows);
                Cell riderCell = riderRow.getCell(col);
                riderCell.setCellValue("Yes");
                CellReference riderSumAssuredReference = new CellReference("F22");
                rows = riderSumAssuredReference.getRow();
                col = riderSumAssuredReference.getCol();
                Row riderSumAssuredRow = sheet.getRow(rows);
                Cell riderSumAssuredRowCellCell = riderSumAssuredRow.getCell(col);
                SectionTransDTO section = new SectionTransDTO();
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
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
                CellReference riderpremReference = new CellReference("E31");
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
                        totalPremium = totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                    }
                }
                sectionRepo.updateSectionSumAssuredDetails((section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                        (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO, (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                        section.getSectId());

            }
            if (itemsBean.getPremiumId().equalsIgnoreCase("Waiver of Premium")) {
                CellReference riderReference = new CellReference("E23");
                rows = riderReference.getRow();
                col = riderReference.getCol();
                Row riderRow = sheet.getRow(rows);
                Cell riderCell = riderRow.getCell(col);
                riderCell.setCellValue("Yes");
                CellReference riderSumAssuredReference = new CellReference("F23");
                rows = riderSumAssuredReference.getRow();
                col = riderSumAssuredReference.getCol();
                Row riderSumAssuredRow = sheet.getRow(rows);
                Cell riderSumAssuredRowCellCell = riderSumAssuredRow.getCell(col);
                SectionTransDTO section = new SectionTransDTO();
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
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
                CellReference riderpremReference = new CellReference("E32");
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
                        totalPremium = totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                    }
                }
                sectionRepo.updateSectionSumAssuredDetails((section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                        (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO, (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                        section.getSectId());
            }
            if (itemsBean.getPremiumId().equalsIgnoreCase("Accidental death")) {
                CellReference riderReference = new CellReference("E24");
                rows = riderReference.getRow();
                col = riderReference.getCol();
                Row riderRow = sheet.getRow(rows);
                Cell riderCell = riderRow.getCell(col);
                riderCell.setCellValue("Yes");
                CellReference riderSumAssuredReference = new CellReference("F24");
                rows = riderSumAssuredReference.getRow();
                col = riderSumAssuredReference.getCol();
                Row riderSumAssuredRow = sheet.getRow(rows);
                Cell riderSumAssuredRowCellCell = riderSumAssuredRow.getCell(col);
                SectionTransDTO section = new SectionTransDTO();
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
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
                CellReference riderpremReference = new CellReference("E33");
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
                        totalPremium = totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                    }
                }
                sectionRepo.updateSectionSumAssuredDetails((section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                        (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO, (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                        section.getSectId());
            }
            if (itemsBean.getPremiumId().equalsIgnoreCase("Adult Medical Reimbursement")) {
                CellReference riderReference = new CellReference("E25");
                rows = riderReference.getRow();
                col = riderReference.getCol();
                Row riderRow = sheet.getRow(rows);
                Cell riderCell = riderRow.getCell(col);
                riderCell.setCellValue("Yes");
                CellReference riderSumAssuredReference = new CellReference("F25");
                rows = riderSumAssuredReference.getRow();
                col = riderSumAssuredReference.getCol();
                Row riderSumAssuredRow = sheet.getRow(rows);
                Cell riderSumAssuredRowCellCell = riderSumAssuredRow.getCell(col);
                SectionTransDTO section = new SectionTransDTO();
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(itemsBean.getSectId());
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
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
                        totalPremium = totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                    }
                }
                sectionRepo.updateSectionSumAssuredDetails((section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                        (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO, (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                        section.getSectId());
            }
            if (itemsBean.getPremiumId().equalsIgnoreCase("Critical illness")) {
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
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
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
                        totalPremium = totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                    }
                }
                sectionRepo.updateSectionSumAssuredDetails((section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                        (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO, (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                        section.getSectId());
            }
            if (itemsBean.getPremiumId().equalsIgnoreCase("Retrenchment")) {
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
                if (!sectionArr.isEmpty()) {
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger) sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger) sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger) sectionArr.get(0)[9]).longValue();
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
                        totalPremium = totalPremium.add(BigDecimal.valueOf(cellValue.getNumberValue()));
                    }
                }
                sectionRepo.updateSectionSumAssuredDetails((section.getPrem() != null) ? section.getPrem() : BigDecimal.ZERO,
                        (section.getCalcprem() != null) ? section.getCalcprem() : BigDecimal.ZERO, (section.getAmount() != null) ? section.getAmount() : BigDecimal.ZERO,
                        section.getSectId());
            }
        }

        final PolicyTrans policyTrans = policyRepo.findOne(polCode);

        CellReference taxReliefReference = new CellReference("E67");
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


        ArrayList<PolicyBenefitsDistribution> newMaturities = new ArrayList<>();
        PolicyTrans policy = policyRepo.findOne(polCode);
        for (
                MaturitiesBean bean : maturitiesBeanList) {
            PolicyBenefitsDistribution newMat = new PolicyBenefitsDistribution();
            if (bean.getEstBenefit() != 0) {
                String yr = bean.getBenefitYear();
                newMat.setEstBenefit(bean.getEstBenefit());
                newMat.setMaturityYear(yr);
                newMat.setPolicyId(policy);
                newMaturities.add(newMat);
            }
        }
        maturityRepo.save(newMaturities);

        premiumResultBean.setPremium(totalPremium.doubleValue());

        return premiumResultBean;
    }
}