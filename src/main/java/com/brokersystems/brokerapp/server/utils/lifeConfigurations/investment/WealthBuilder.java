package com.brokersystems.brokerapp.server.utils.lifeConfigurations.investment;

import com.brokersystems.brokerapp.life.repository.MaturityPayoutScheduleRepo;
import com.brokersystems.brokerapp.life.repository.PolicyBenefitsDistributionRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.MaturitiesBean;
import com.brokersystems.brokerapp.server.utils.PremiumItemsBean;
import com.brokersystems.brokerapp.server.utils.PremiumResultBean;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicySurrenderValuesRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.SectionTransRepo;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class WealthBuilder {
    @Autowired
    private SectionTransRepo sectionRepo;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private PolicyBenefitsDistributionRepo maturityRepo;

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private PolicySurrenderValuesRepo surrenderValuesRepo;
    @Autowired
    private MaturityPayoutScheduleRepo maturityPayoutScheduleRepo;


    public void getLifePremium(Sheet sheet, Workbook workbook, PremiumResultBean premiumResultBean, List<PremiumItemsBean> premItems,
                                            final Date dob,final Integer policyTerm, final Long polCode,
                                            final boolean isAllowTopUps, final String topUpFrequency, final Date firstTopUpDate, final Date lastTopUpDate,final String investmentFreq) throws BadRequestException {

        List<MaturitiesBean> maturitiesBeanList = new ArrayList<>();
        BigDecimal investment = premItems.stream().filter(PremiumItemsBean::isMainSection).map(PremiumItemsBean::getInvestment).findAny().orElse(BigDecimal.ZERO);
        BigDecimal topUp = premItems.stream().filter(PremiumItemsBean::isMainSection).map(PremiumItemsBean::getTopUp).findAny().orElse(BigDecimal.ZERO);



        if (investment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Investment cannot be zero for this calculator...");
        }

        System.out.println("Investment: " + investment);

        // Retrieve the Monthly Top-Up Amount dynamically from PremiumResultBean
        if (isAllowTopUps) {
            if (topUp.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Top-up amount cannot be zero or null for this calculator...");
            }
        }

        // Set DOB
        HSSFCreationHelper createHelper = (HSSFCreationHelper) workbook.getCreationHelper();
        HSSFCellStyle cellStyle = (HSSFCellStyle) workbook.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("d-MMM-yy"));
        CellReference dateReference = new CellReference("E7");
        int r = dateReference.getRow();
        int c = dateReference.getCol();
        Row row = sheet.getRow(r);
        Cell currentCell = row.getCell(c);
        currentCell.setCellValue(dob);
        currentCell.setCellStyle(cellStyle);

        System.out.println("DOB: " + dob);

        // Set Policy Term
        CellReference termReference = new CellReference("E9");
        r = termReference.getRow();
        c = termReference.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);
        currentCell.setCellValue(policyTerm);

        System.out.println("Policy Term: " + policyTerm);

        // Set Frequency (Annual/Single)
        CellReference freqReference = new CellReference("E10");
        r = freqReference.getRow();
        c = freqReference.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);
        String freq = "";

        if (investmentFreq == null) {
            throw new BadRequestException("Frequency cannot be null");
        }

        switch (investmentFreq.toUpperCase()) {
            case "SG":
                freq = "Single";
                break;
            case "A":
                freq = "Annual";
                break;
            default:
                throw new BadRequestException("Invalid frequency type. Must be 'Single' or 'Annual'.");
        }

        currentCell.setCellValue(freq);

        // Set the Investment Value based on the frequency (Single or Annual) - sum assured
        CellReference investmentReference = new CellReference("E11");
        r = investmentReference.getRow();
        c = investmentReference.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);
        currentCell.setCellValue(investment.doubleValue());

        // Retrieve the 'Allow Top-ups' flag dynamically
        String allowTopUpsText = isAllowTopUps ? "Yes" : "No";

        // Set the 'Allow Top-ups?' field in the Excel sheet
        CellReference allowTopUpsReference = new CellReference("E12");
        r = allowTopUpsReference.getRow();
        c = allowTopUpsReference.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);
        currentCell.setCellValue(allowTopUpsText);

        // Set Top-Up Amount (only if top-ups are allowed and a valid amount is set)
        CellReference topUpAmountReference = new CellReference("E13");  // Cell for top-up amount
        r = topUpAmountReference.getRow();
        c = topUpAmountReference.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);
        currentCell.setCellValue(topUp.doubleValue());  // Set the top-up amount

        // Set Top-Up Frequency (Single/Semi-Annual/Monthly/Quarterly/Annual)
        CellReference topUpFreqReference = new CellReference("E14");
        r = topUpFreqReference.getRow();
        c = topUpFreqReference.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);
        String topUpFreq = "";

        if (topUpFrequency == null) {
            throw new BadRequestException("Top-Up Frequency cannot be null");
        }

        switch (topUpFrequency.toUpperCase()) {
            case "SG":
                topUpFreq = "Single";
                break;
            case "S":
                topUpFreq = "Semi-Annual";
                break;
            case "M":
                topUpFreq = "Monthly";
                break;
            case "Q":
                topUpFreq = "Quarterly";
                break;
            case "A":
                topUpFreq = "Annual";
                break;
            default:
                throw new BadRequestException("Invalid Top-Up Frequency. Must be 'Monthly', 'Quarterly', or 'Annual'.");
        }

        currentCell.setCellValue(topUpFreq);

        // Retrieve the 'Expected Date of First Top-Up Payment'
        CellReference firstTopUpDateReference = new CellReference("E15");
        r = firstTopUpDateReference.getRow();
        c = firstTopUpDateReference.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);
        currentCell.setCellValue(firstTopUpDate != null ? firstTopUpDate.toString() : "N/A");

        // Retrieve the 'Expected Date of Last Top-Up Payment'
        CellReference lastTopUpDateReference = new CellReference("E16");
        r = lastTopUpDateReference.getRow();
        c = lastTopUpDateReference.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);
        currentCell.setCellValue(lastTopUpDate != null ? lastTopUpDate.toString() : "N/A");


            // Initialize FormulaEvaluator once and reuse it
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        final PolicyTrans policyTrans = policyRepo.findOne(polCode);

        CellReference maturityAgeReference = new CellReference("E17");
        r = maturityAgeReference.getRow();
        c = maturityAgeReference.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);
        if(currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            CellValue maturityAge = evaluator.evaluate(currentCell);
            currentCell.setCellValue(maturityAge.getNumberValue());
        }

        // Set Projected Life cover Value
        CellReference lifeCoverReference = new CellReference("E24");
        r = lifeCoverReference.getRow();
        c = lifeCoverReference.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);

        if(currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            CellValue lifeCover = evaluator.evaluate(currentCell);
            if (lifeCover.getNumberValue() != 0) {
                policyTrans.setLifeCoverAmt(BigDecimal.valueOf(lifeCover.getNumberValue()));
            }

        }

        // Set Projected Maturity Value
//        CellReference maturityReference = new CellReference("E25");
//        r = maturityReference.getRow();
//        c = maturityReference.getCol();
//        row = sheet.getRow(r);
//        currentCell = row.getCell(c);
//        System.out.println("MAT CELL TYPE: " + currentCell.getCellType());
//        if(currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
//            CellValue maturityValue = evaluator.evaluate(currentCell);
//            if (maturityValue.getNumberValue() != 0) {
//               policyTrans.setMaturityValue(BigDecimal.valueOf(maturityValue.getNumberValue()));
//            }
//
//        }

        // Set Projected Maturity Value
        try {
            CellReference maturityReference = new CellReference("E25");
            r = maturityReference.getRow();
            c = maturityReference.getCol();
            row = sheet.getRow(r);
            currentCell = (row != null) ? row.getCell(c) : null;

            if (currentCell != null) {
                // Read the cell value based on its type without evaluating the formula
                switch (currentCell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        double numericValue = currentCell.getNumericCellValue();
                        policyTrans.setMaturityValue(BigDecimal.valueOf(numericValue));
                        System.out.println("Cell E25 numeric value: " + numericValue);
                        break;

                    case Cell.CELL_TYPE_STRING:
                        try {
                            String stringValue = currentCell.getStringCellValue();
                            BigDecimal value = new BigDecimal(stringValue);
                            policyTrans.setMaturityValue(value);
                            System.out.println("Cell E25 string value parsed to numeric: " + value);
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing cell E25 string to number: " + e.getMessage());
                        }
                        break;

                    case Cell.CELL_TYPE_FORMULA:
                        // Fetch the cached value in case of a formula
                        switch (currentCell.getCachedFormulaResultType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                double cachedValue = currentCell.getNumericCellValue();
                                policyTrans.setMaturityValue(BigDecimal.valueOf(cachedValue));
                                System.out.println("Cell E25 formula cached value: " + cachedValue);
                                break;

                            case Cell.CELL_TYPE_STRING:
                                try {
                                    String cachedStringValue = currentCell.getStringCellValue();
                                    BigDecimal value = new BigDecimal(cachedStringValue);
                                    policyTrans.setMaturityValue(value);
                                    System.out.println("Cell E25 formula cached string value parsed to numeric: " + value);
                                } catch (NumberFormatException e) {
                                    System.err.println("Error parsing cell E25 cached formula string to number: " + e.getMessage());
                                }
                                break;

                            case Cell.CELL_TYPE_ERROR:
                                byte errorCode = currentCell.getErrorCellValue();
                                System.err.println("Cell E25 contains an error. Error code: " + errorCode);
                                break;

                            default:
                                System.err.println("Cell E25 formula cached contains unsupported type.");
                                break;
                        }
                        break;

                    case Cell.CELL_TYPE_ERROR:
                        byte errorCode = currentCell.getErrorCellValue();
                        System.err.println("Cell E25 contains an error. Error code: " + errorCode);
                        break;

                    default:
                        System.err.println("Cell E25 contains unsupported type: " + currentCell.getCellType());
                        break;
                }
            } else {
                System.err.println("Cell E25 is null.");
            }
        } catch (Exception e) {
            System.err.println("Error processing cell E25: " + e.getMessage());
        }

        // Tax Relief
        CellReference taxReliefCell = new CellReference("E52");
        r = taxReliefCell.getRow();
        c = taxReliefCell.getCol();
        row = sheet.getRow(r);
        currentCell = row.getCell(c);
        if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            CellValue cellValue = evaluator.evaluate(currentCell);
            if (cellValue.getNumberValue() != 0) {
                policyTrans.setTaxRelief(BigDecimal.valueOf(cellValue.getNumberValue()));
            }

        }

        // Handle yearly maturity projections (Year 1 to Year 10)
        // Similar to maturity values
        int initial = 29;
        int start = 0;

        for (int year = 1; year <= 10; year++) {
            initial++;
            start++;
            String cellReferenceString = "E" + initial;
            CellReference yearMaturityReference = new CellReference(cellReferenceString);
            r = yearMaturityReference.getRow();
            c = yearMaturityReference.getCol();
            row = sheet.getRow(r);
            currentCell = (row != null) ? row.getCell(c) : null;

            if (currentCell != null) {
                try {
                    switch (currentCell.getCellType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                            double numericValue = currentCell.getNumericCellValue();
                            if (numericValue != 0) {
                                MaturitiesBean maturitiesBean = new MaturitiesBean();
                                maturitiesBean.setBenefitYear("Year " + start);
                                maturitiesBean.setEstBenefit(numericValue);
                                maturitiesBeanList.add(maturitiesBean);
                                System.out.println("Year " + start + " numeric value: " + numericValue);
                            }
                            break;

                        case Cell.CELL_TYPE_STRING:
                            try {
                                String stringValue = currentCell.getStringCellValue();
                                BigDecimal value = new BigDecimal(stringValue);
                                if (value.compareTo(BigDecimal.ZERO) != 0) {
                                    MaturitiesBean maturitiesBean = new MaturitiesBean();
                                    maturitiesBean.setBenefitYear("Year " + start);
                                    maturitiesBean.setEstBenefit(value.doubleValue());
                                    maturitiesBeanList.add(maturitiesBean);
                                    System.out.println("Year " + start + " string value parsed to numeric: " + value);
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing cell " + cellReferenceString + " string to number: " + e.getMessage());
                            }
                            break;

                        case Cell.CELL_TYPE_FORMULA:
                            switch (currentCell.getCachedFormulaResultType()) {
                                case Cell.CELL_TYPE_NUMERIC:
                                    double cachedValue = currentCell.getNumericCellValue();
                                    if (cachedValue != 0) {
                                        MaturitiesBean maturitiesBean = new MaturitiesBean();
                                        maturitiesBean.setBenefitYear("Year " + start);
                                        maturitiesBean.setEstBenefit(cachedValue);
                                        maturitiesBeanList.add(maturitiesBean);
                                        System.out.println("Year " + start + " formula cached value: " + cachedValue);
                                    }
                                    break;

                                case Cell.CELL_TYPE_STRING:
                                    try {
                                        String cachedStringValue = currentCell.getStringCellValue();
                                        BigDecimal value = new BigDecimal(cachedStringValue);
                                        if (value.compareTo(BigDecimal.ZERO) != 0) {
                                            MaturitiesBean maturitiesBean = new MaturitiesBean();
                                            maturitiesBean.setBenefitYear("Year " + start);
                                            maturitiesBean.setEstBenefit(value.doubleValue());
                                            maturitiesBeanList.add(maturitiesBean);
                                            System.out.println("Year " + start + " formula cached string value parsed to numeric: " + value);
                                        }
                                    } catch (NumberFormatException e) {
                                        System.err.println("Error parsing cell " + cellReferenceString + " cached formula string to number: " + e.getMessage());
                                    }
                                    break;

                                case Cell.CELL_TYPE_ERROR:
                                    System.err.println("Cell " + cellReferenceString + " contains an error. Error code: " + currentCell.getErrorCellValue());
                                    break;

                                default:
                                    System.err.println("Cell " + cellReferenceString + " cached formula contains unsupported type.");
                                    break;
                            }
                            break;

                        case Cell.CELL_TYPE_ERROR:
                            System.err.println("Cell " + cellReferenceString + " contains an error. Error code: " + currentCell.getErrorCellValue());
                            break;

                        default:
                            System.err.println("Cell " + cellReferenceString + " contains unsupported type: " + currentCell.getCellType());
                            break;
                    }
                } catch (Exception e) {
                    System.err.println("Error processing cell " + cellReferenceString + ": " + e.getMessage());
                }
            } else {
                System.err.println("Cell " + cellReferenceString + " is null.");
            }
        }






//        // Set Projected Fund Value for Each Year
        for (int year = 1; year <= 10; year++) {
            initial++;
            start++;
            String projectedValueRef = "K" + (23 + year);  // Reference for each year's Projected Fund Value
            CellReference projectedValueReference = new CellReference(projectedValueRef);
            r = projectedValueReference.getRow();
            c = projectedValueReference.getCol();
            row = sheet.getRow(r);
            currentCell = row.getCell(c);
            CellValue projectedValue = evaluator.evaluate(currentCell);

            if (projectedValue != null && projectedValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                System.out.println("Projected Fund Value for Year " + year + ": " + projectedValue.getNumberValue());
            }
        }

        // Set Pay-out on Early Encashment for Each Year (where available)
        for (int year = 3; year <= 10; year++) {  // Starts from Year 3 for Early Encashment
            String earlyEncashmentRef = "L" + (23 + year);
            CellReference earlyEncashmentReference = new CellReference(earlyEncashmentRef);
            r = earlyEncashmentReference.getRow();
            c = earlyEncashmentReference.getCol();
            row = sheet.getRow(r);
            currentCell = row.getCell(c);
            CellValue earlyEncashmentValue = evaluator.evaluate(currentCell);

            if (earlyEncashmentValue != null && earlyEncashmentValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                System.out.println("Projected Early Encashment Value for Year " + year + ": " + earlyEncashmentValue.getNumberValue());
            }
        }

        // Set Amount Payable in Case of Death for Each Year
        for (int year = 1; year <= 10; year++) {
            String amountPayableRef = "M" + (23 + year);
            CellReference amountPayableReference = new CellReference(amountPayableRef);
            r = amountPayableReference.getRow();
            c = amountPayableReference.getCol();
            row = sheet.getRow(r);
            currentCell = row.getCell(c);
            CellValue amountPayableValue = evaluator.evaluate(currentCell);

            if (amountPayableValue != null && amountPayableValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                System.out.println("Amount Payable in Case of Death for Year " + year + ": " + amountPayableValue.getNumberValue());
            }
        }
    }
}

