
package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.life.model.PolicyBenefitsDistribution;
import com.brokersystems.brokerapp.life.repository.PolicyBenefitsDistributionRepo;
import com.brokersystems.brokerapp.quotes.model.QuotRiskLimits;
import com.brokersystems.brokerapp.quotes.repository.QuotRiskLimitsRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.lifeConfigurations.investment.AkibaPolicies;
import com.brokersystems.brokerapp.server.utils.lifeConfigurations.investment.WealthBuilder;
import com.brokersystems.brokerapp.server.utils.lifeConfigurations.life.TermPolicies;
import com.brokersystems.brokerapp.server.utils.lifeConfigurations.life.WholeLifeExcelUtils;
import com.brokersystems.brokerapp.server.utils.lifeConfigurations.ordinaryLife.EducationPolicies;
import com.brokersystems.brokerapp.server.utils.lifeConfigurations.ordinaryLife.EndowmentPolicies;
import com.brokersystems.brokerapp.setup.model.PremRatesDef;
import com.brokersystems.brokerapp.setup.model.QSubclassSections;
import com.brokersystems.brokerapp.setup.model.SubclassSections;
import com.brokersystems.brokerapp.setup.repository.PremRatesRepo;
import com.brokersystems.brokerapp.setup.repository.SubSectionRepo;
import com.brokersystems.brokerapp.uw.dtos.SectionTransDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.lowagie.text.exceptions.BadPasswordException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by HP on 9/20/2017.
 */
@Component
public class PremExcelUtils {

    @Autowired
    private SectionTransRepo sectionRepo;

    @Autowired
    private QuotRiskLimitsRepo quotRiskLimitsRepo;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private DataSource dataSource;


    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private PolicyQuestionnaireRepo policyQuestionnaireRepo;
    @Autowired
    private PremRatesRepo premRatesRepo;

    @Autowired
    private SubSectionRepo subclassRepo;

    @Autowired
    private PolicySurrenderValuesRepo surrenderValuesRepo;

    @Autowired
    private EducationPolicies educationPolicies;
    @Autowired
    private TermPolicies termPolicies;
    @Autowired
    private EndowmentPolicies endowmentPolicies;
    @Autowired
    private AkibaPolicies akibaPolicies;
    @Autowired
    private WholeLifeExcelUtils wholeLifeExcelUtils;

    @Autowired
    private WealthBuilder wealthBuilder;


    public   PremiumResultBean getPremium(List<PremiumItemsBean> premItems,final String path) throws IOException {
        Workbook workbook = new HSSFWorkbook(Files.newInputStream(Paths.get(path)));
        System.out.println(premItems);
        System.out.println(path);
//        FunctionEval.registerFunction("DATEDIF", new DateIfFunction());
        List<SectionTransDTO> sections = new ArrayList<>();
        double sumInsured = 0;
        double totalPrem = 0;
        double fullPrem = 0;
        double commissionPrem = 0;
        Long polCode=null;
        for(int i=0;i<workbook.getNumberOfSheets();i++){
            Sheet sheet = workbook.getSheetAt(i);

            Optional<PremiumItemsBean> premItem = premItems.stream().filter(a -> a.getPremiumId().equals(StringUtils.trim(sheet.getSheetName()))).findAny();
            System.out.println(sheet.getSheetName()+" Item present ..."+premItem);
            PremiumItemsBean item = new PremiumItemsBean(sheet.getSheetName(), 0, 0, 0, 0,0l,0,"SI",0, new ArrayList<>(),null);
            SectionTransDTO section = null;
            if(premItem.isPresent()) {
                item = premItem.get();
                System.out.println("item "+item.getSectId());
                List<Object[]> sectionArr = sectionRepo.findSectionTransById(item.getSectId());
                if(!sectionArr.isEmpty()) {
                    section = new SectionTransDTO();
                    final BigDecimal rate = (BigDecimal) sectionArr.get(0)[7];
                    final BigDecimal freeLimit = (BigDecimal) sectionArr.get(0)[4];
                    final BigDecimal amount = (BigDecimal) sectionArr.get(0)[1];
                    final BigDecimal divFactor = (BigDecimal) sectionArr.get(0)[3];
                    final Long sectId = ((BigInteger)sectionArr.get(0)[0]).longValue();
                    final Long sectSectionId = ((BigInteger)sectionArr.get(0)[8]).longValue();
                    final Long premRateId = ((BigInteger)sectionArr.get(0)[9]).longValue();
                    final Long sclId = ((BigInteger)sectionArr.get(0)[15]).longValue();
                    section.setPremRatesId(premRateId);
                    section.setRate(rate);
                    section.setFreeLimit(freeLimit);
                    section.setAmount(amount);
                    section.setDivFactor(divFactor);
                    section.setSectId(sectId);
                    section.setSclCode(sclId);
                    section.setSectionSectId(sectSectionId);
                }
                polCode =item.getPolId();
            }
            if (polCode!=null){
                if (i==0 && policyQuestionnaireRepo.count(QPolicyQuestionnaire.policyQuestionnaire.policy.policyId.eq(polCode))>0 ) {
                    String sheetName ="QUESTIONNAIRE";
                    sheetName = sheetName.toUpperCase();
                    Sheet quizsheet = workbook.getSheet(sheetName);
                    if(quizsheet!=null && policyQuestionnaireRepo.count(QPolicyQuestionnaire.policyQuestionnaire.policy.policyId.eq(polCode))>0){
                        Iterable<PolicyQuestionnaire> questions = policyQuestionnaireRepo.findAll(QPolicyQuestionnaire.policyQuestionnaire.policy.policyId.eq(polCode));

                        // Iterator<Row> iterator = quizsheet.iterator();
                        int quizcount=1;
                        for (PolicyQuestionnaire quiz : questions) {
                            Row currentRow = quizsheet.getRow(quizcount);
                            System.out.println("currentRow="+currentRow);
                            Cell currentCell=null;
                            if (currentRow==null) {
                                currentRow = quizsheet.createRow(quizcount);
                            }
                            currentCell = currentRow.getCell(0,Row.CREATE_NULL_AS_BLANK);


                            System.out.println("quiz id="+quiz.getQuestion().getQuestionShtDesc());
                            currentCell.setCellValue(quiz.getQuestion().getQuestionShtDesc());

                            currentCell = currentRow.getCell(1,Row.CREATE_NULL_AS_BLANK);
                            System.out.println("quiz ="+quiz.getQuestion().getQuestionname());
                            currentCell.setCellValue(quiz.getQuestion().getQuestionname());

                            StringTokenizer tokenizer = new StringTokenizer(quiz.getChoice(),",");
                            int column = 2;
                            while(tokenizer.hasMoreTokens()){
                                String token  = tokenizer.nextToken();
                                currentCell = currentRow.getCell(column,Row.CREATE_NULL_AS_BLANK);
                                System.out.println("column="+column+";token ="+token);
                                currentCell.setCellValue(token);
                                column++;
                            }

                            quizcount++;
                        }
                    }
                }
            }
            Iterator<Row> iterator = sheet.iterator();
            int count=0;
            while (iterator.hasNext()) {
                if(count==2)
                    break;
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                if(count==0) {
                    count++;
                    continue;
                }
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    int columnIndex = currentCell.getColumnIndex();
                    switch (columnIndex) {
                        case 1:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC ||currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    if ("SI".equalsIgnoreCase(item.getSectType())) {
                                        sumInsured += item.getValue();
                                    }
                                    currentCell.setCellValue(item.getValue());//Setting Value
                                }
                                else{
                                    if ("SI".equalsIgnoreCase(item.getSectType())) {
                                        sumInsured += item.getValue();
                                    }
                                    currentCell.setCellValue((Double)item.getValue());//Setting Value;
                                }

                            }
                            break;

                        case 2:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC ||currentCell.getCellType() == Cell.CELL_TYPE_STRING ) {
                                if(currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                    currentCell.setCellValue(item.getRate());
                                else{
                                    currentCell.setCellValue( (Double)item.getRate());
                                    //Setting Rate
                                }


                            }
                            break;

                        case 3:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC ||currentCell.getCellType() == Cell.CELL_TYPE_STRING ) {
                                currentCell.setCellValue((item.getDivFactor() <= 0) ? 1 : item.getDivFactor()); //Setting Division Factor
                            }
                            break;

                        case 4:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC||currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                currentCell.setCellValue(item.getFreeLimit()); //Setting Free Limit

                            }
                            break;
                        case 6:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC||currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                currentCell.setCellValue(item.getMinPrem()); //Setting Minimum Premium

                            }
                            break;
                        case 8:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC||currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                currentCell.setCellValue(item.getAge()); //Setting Minimum Premium

                            }
                            break;
                        case 9:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC||currentCell.getCellType() == Cell.CELL_TYPE_STRING) {

                                if(section!=null) {
                                    currentCell.setCellValue(riskTransRepo.count(QRiskTrans.riskTrans.policy.policyId.eq(polCode))); //Setting Minimum Premium
                                }
                                if(section!=null)
                                    currentCell.setCellValue(riskTransRepo.count(QRiskTrans.riskTrans.policy.policyId.eq(polCode))); //Setting Minimum Premium

                            }
                            break;
                        case 10:

                            if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                if (item.getScheduleItems().size() > 0) {
                                    currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(0)));
                                } else {
                                    currentCell.setCellValue(0);
                                }
                            }
                            else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                if (item.getScheduleItems().size() > 0) {
                                    currentCell.setCellValue(item.getScheduleItems().get(0));
                                } else {
                                    currentCell.setCellValue("");
                                }
                            }
                            break;
                        case 11:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                if (item.getScheduleItems().size() > 1) {
                                    currentCell.setCellValue(item.getScheduleItems().get(1));
                                } else {
                                    currentCell.setCellValue("");
                                }
                            }
                            else   if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                if (item.getScheduleItems().size() > 1) {
                                    currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(1)));
                                } else {
                                    currentCell.setCellValue(0);
                                }
                            }
                            break;
                        case 12:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                if (item.getScheduleItems().size() > 2) {
                                    currentCell.setCellValue(item.getScheduleItems().get(2));
                                } else {
                                    currentCell.setCellValue("");
                                }
                            }
                            else   if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                if (item.getScheduleItems().size() > 2) {
                                    currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(2)));
                                } else {
                                    currentCell.setCellValue(0);
                                }
                            }
                            break;
                        case 13:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                if (item.getScheduleItems().size() > 3) {
                                    currentCell.setCellValue(item.getScheduleItems().get(3));
                                } else {
                                    currentCell.setCellValue("");
                                }
                            }
                            else   if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                if (item.getScheduleItems().size() > 3) {
                                    currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(3)));
                                } else {
                                    currentCell.setCellValue(0);
                                }
                            }
                            break;
                        case 14:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                if (item.getScheduleItems().size() > 4) {
                                    currentCell.setCellValue(item.getScheduleItems().get(4));
                                } else {
                                    currentCell.setCellValue("");
                                }
                            }
                            else   if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                if (item.getScheduleItems().size() > 4) {
                                    currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(4)));
                                } else {
                                    currentCell.setCellValue(0);
                                }
                            }
                            break;
                        case 15:
                            if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                if (item.getScheduleItems().size() > 5) {
                                    currentCell.setCellValue(item.getScheduleItems().get(5));
                                } else {
                                    currentCell.setCellValue("");
                                }
                            }
                            else   if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                if (item.getScheduleItems().size() > 5) {
                                    currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(5)));
                                } else {
                                    currentCell.setCellValue(0);
                                }
                            }
                            break;
                        default:
                    }

                }
                count++;
            }

            Iterator<Row> resultiterator = sheet.iterator();
            int counter = 0;
            while (resultiterator.hasNext()) {
                Row currentRow = resultiterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    int columnIndex = currentCell.getColumnIndex();
                    if(columnIndex== 7) {
                        if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                            evaluator.evaluateFormulaCell(currentCell);
                            CellValue cellValue = evaluator.evaluate(currentCell);
                            if (cellValue.getNumberValue() != 0) {
                                if (section != null) {
                                    final PremRatesDef premRatesDef = premRatesRepo.findOne(section.getPremRatesId());
                                    final Iterable<SubclassSections> subclassSections = subclassRepo.findAll(QSubclassSections.subclassSections.subclass.subId.eq(section.getSclCode())
                                            .and(QSubclassSections.subclassSections.section.id.eq(premRatesDef.getSection().getId())));
                                    if(subclassSections.spliterator().getExactSizeIfKnown()!=1){
                                        throw new BadPasswordException("There is an issue with mapping with Premium Item Sub Class Mapping contact Administrator");
                                    }
                                    final SubclassSections subclassSection = subclassSections.iterator().next();
                                     BigDecimal calcPrem = BigDecimal.valueOf(cellValue.getNumberValue());
                                     if(premRatesDef.getMinPremium()!=null && premRatesDef.getMinPremium().compareTo(calcPrem) > 0){
                                         calcPrem = premRatesDef.getMinPremium();
                                     }
                                    if(premRatesDef.getProratedFull()!=null && premRatesDef.getProratedFull().equalsIgnoreCase("P")) {
                                        section.setPrem(calcPrem);
                                        totalPrem += calcPrem.doubleValue();
                                    }else{
                                        section.setPrem(calcPrem);
                                        fullPrem += calcPrem.doubleValue();
                                    }
                                    if(subclassSection.getComputeCommission()!=null && subclassSection.getComputeCommission().equalsIgnoreCase("Y")){
                                        commissionPrem+=cellValue.getNumberValue();
                                    }
                                    if(premRatesDef.getSubSections()!=null && premRatesDef.getSubSections().getSubSections()!=null
                                    && premRatesDef.getSubSections().getSubSections().getComputeCommission()!=null
                                    && premRatesDef.getSubSections().getSubSections().getComputeCommission().equalsIgnoreCase("Y")){
                                        commissionPrem+=cellValue.getNumberValue();
                                    }
                                }
                            }
                        }
                    }
                    if(columnIndex== 5) {
                        if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                            evaluator.evaluateFormulaCell(currentCell);

                            if (currentCell.getNumericCellValue() != 0) {
                                if (section != null) {
                                    section.setCalcprem(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                }
                            }
                        }
                    }
                }
                counter++;
            }

            if(section!=null)
                sections.add(section);
        }
        for(SectionTransDTO transDTO: sections){
            sectionRepo.updateSectionDetails((transDTO.getPrem()!=null)?transDTO.getPrem():BigDecimal.ZERO,
                    (transDTO.getCalcprem()!=null)?transDTO.getCalcprem():BigDecimal.ZERO,transDTO.getSectId());
        }
        return new PremiumResultBean(totalPrem,fullPrem,sumInsured,commissionPrem);
    }

    public   PremiumResultBean getLifePremium(List<PremiumItemsBean> premItems,final String path,
                                              final Date dob, final String gender, final Integer policyTerm,
                                              final String frequency, final Long polCode, final String sheetName,
                                              final String computeType, final String topUpFrequency, final Boolean allowTopUps, final Date firstTopUpDate, final Date lastTopUpDate, final String investmentFrequency) throws IOException, BadRequestException{
    Workbook workbook;
        try {
            String fileExtension = path.substring(path.lastIndexOf(".") + 1);
            if ("xls".equalsIgnoreCase(fileExtension)) {
                workbook = new HSSFWorkbook(Files.newInputStream(Paths.get(path)));
            } else if ("xlsx".equalsIgnoreCase(fileExtension)) {
                workbook = new XSSFWorkbook(Files.newInputStream(Paths.get(path)));
            } else {
                throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
            }
        } catch (IOException e) {
            throw new BadRequestException("Workbook could not be accessed or is corrupted. Please check the file.");
        }

        double sumInsured = 0;
        double premiumVal = 0;
        BigDecimal totalPremium = BigDecimal.ZERO;

        final PremiumResultBean premiumResultBean = new PremiumResultBean();
        System.out.println(premItems);
        List<MaturitiesBean> maturitiesBeanList = new ArrayList<>();
        for(int i=0;i<workbook.getNumberOfSheets();i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet == null) {
                throw new BadRequestException("Sheet " + (i + 1) + " is inaccessible or does not exist.");
            }
//            try {
                if (sheet.getSheetName().equalsIgnoreCase(sheetName) && (sheetName.equalsIgnoreCase("SA-Education Policy Quote") || sheetName.equalsIgnoreCase("PRE-Education Policy Quote"))) {
                    educationPolicies.getEducationPolicyPremium(
                            premItems,dob,gender,policyTerm,frequency,polCode,sheetName,computeType,
                            sheet, totalPremium,workbook,premiumResultBean,
                            maturitiesBeanList
                    );
                    System.out.println(">>>>>>>premium at method call"+totalPremium);
                }
                if (sheet.getSheetName().equalsIgnoreCase(sheetName) && (sheetName.equalsIgnoreCase("SA-Term Assurance Policy Quote") || sheetName.equalsIgnoreCase("PRE-Term Assurance Policy Quote"))) {
                    termPolicies.getTermPolicyPremium(
                            premItems,dob,gender,policyTerm,frequency,polCode,sheetName,computeType,
                            sheet, totalPremium,workbook,premiumResultBean,
                            maturitiesBeanList
                    );
                }
                if (sheet.getSheetName().equalsIgnoreCase(sheetName) && (sheetName.equalsIgnoreCase("SA-Endowment Policy Quote") ||
                                                                         sheetName.equalsIgnoreCase("PRE-Endowment Policy Quote")  ||
                                                                         sheetName.equalsIgnoreCase("EndowmentInputs"))) {
                    System.out.println("sheetname at validation in utils: "+ sheetName+" "+premItems+" "+dob+" "+ computeType);
                    endowmentPolicies.getEndowmentPolicyPremium(
                            premItems,dob,gender,policyTerm,frequency,polCode,sheetName,computeType,
                            sheet, totalPremium,workbook,premiumResultBean,
                            maturitiesBeanList
                    );
                    System.out.println("sheetname at validation in utils: "+ sheetName);
                }
                if (sheet.getSheetName().equalsIgnoreCase(sheetName) && (sheetName.equalsIgnoreCase("SA-Anticipated Endowment Quote") || sheetName.equalsIgnoreCase("PRE-Anticipated Endowment Quote"))) {
                    akibaPolicies.getLifePremium(sheet,workbook,premiumResultBean,premItems,dob,gender,policyTerm,frequency,polCode,computeType);
                }
                if (sheet.getSheetName().equalsIgnoreCase(sheetName) &&
                        (sheetName.equalsIgnoreCase("SA-Whole Life Policy Quote") || sheetName.equalsIgnoreCase("PRE-Whole Life Policy Quote"))) {
                    System.out.println("sheet name" + sheet.getSheetName());
                    wholeLifeExcelUtils.getWholeLifePremium(premItems, dob, gender, policyTerm, frequency, polCode, computeType, sheet,
                            workbook, premiumResultBean);
                }
                //add another sheet validation
                if (sheet.getSheetName().equalsIgnoreCase(sheetName) && (sheet.getSheetName().equalsIgnoreCase("WealthBuilder Quote"))) {
                    wealthBuilder.getLifePremium(
                            sheet, workbook, premiumResultBean, premItems, dob, policyTerm,
                            polCode, allowTopUps, topUpFrequency,
                            firstTopUpDate, lastTopUpDate,investmentFrequency);
                }
            if (sheet.getSheetName().equalsIgnoreCase(sheetName) && sheetName.equalsIgnoreCase("SUM ASSURED")) {
                System.out.println("Processing SUM ASSURED calculation...");

                for (PremiumItemsBean item : premItems) {
                    if (item.isMainSection() && "SI".equals(item.getSectType())) {
                        System.out.println("Processing item: " + item);

                        BigDecimal sumAssured = item.getSumAssured();
                        BigDecimal rate = BigDecimal.valueOf(item.getRate());
                        BigDecimal divFactor = BigDecimal.valueOf(item.getDivFactor());

                        System.out.println("Sum Assured: " + sumAssured);
                        System.out.println("Rate: " + rate);
                        System.out.println("Division Factor: " + divFactor);

                        BigDecimal premium = sumAssured.multiply(rate).divide(divFactor, RoundingMode.CEILING);
                        System.out.println("Calculated Premium: " + premium);

                        premiumResultBean.setSumInsured(sumAssured.doubleValue());
                        premiumResultBean.setPremium(premium.doubleValue());

                        // Update the item's premium
                        item.setPremium(BigDecimal.valueOf(premium.doubleValue()));
                    }
                }
            }

//            }catch (Exception e) {
//                throw new BadRequestException("Could not resolve external workbook name. Computation Workbook environment has not been set up.");
//            }

        }

        // Before returning result
        System.out.println("\nFinal calculation results:");
        System.out.println("Total Premium: " + totalPremium);
        System.out.println("Premium Result Bean: " + premiumResultBean);

        return premiumResultBean;
    }

    public   PremiumResultBean getQuotPremium(List<PremiumItemsBean> premItems,final String path) throws IOException {
        System.out.println(path);
        Workbook workbook = new HSSFWorkbook(Files.newInputStream(Paths.get(path)));
        List<QuotRiskLimits> sectionTrans = new ArrayList<>();
        double sumInsured = 0;
        double totalPrem = 0;
        //System.out.println(premItems);
        for(int i=0;i<workbook.getNumberOfSheets();i++){
            Sheet sheet = workbook.getSheetAt(i);
            Optional<PremiumItemsBean> premItem = premItems.stream().filter(a -> a.getPremiumId().equals(StringUtils.trim(sheet.getSheetName()))).findAny();
            PremiumItemsBean item = new PremiumItemsBean(sheet.getSheetName(), 0, 0, 0, 0,0l,0,"SI",0, new ArrayList<>(),null);
            QuotRiskLimits section = null;
            boolean present = false;
            if(premItem.isPresent()) {
                item = premItem.get();
                section = quotRiskLimitsRepo.findOne(item.getSectId());
                present =true;
            }
            // System.out.println(section);
            if(present) {
                Iterator<Row> iterator = sheet.iterator();
                while (iterator.hasNext()) {
                    Row currentRow = iterator.next();
                    Iterator<Cell> cellIterator = currentRow.iterator();
                    while (cellIterator.hasNext()) {
                        Cell currentCell = cellIterator.next();
                        int columnIndex = currentCell.getColumnIndex();
                        switch (columnIndex) {
                            case 1:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    if ("SI".equalsIgnoreCase(item.getSectType())) {
                                        sumInsured += item.getValue();
                                    }
                                    currentCell.setCellValue(item.getValue());//Setting Value

                                }
                                break;

                            case 2:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    currentCell.setCellValue(item.getRate()); //Setting Rate

                                }
                                break;

                            case 3:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    currentCell.setCellValue((item.getDivFactor() <= 0) ? 1 : item.getDivFactor()); //Setting Division Factor

                                }
                                break;

                            case 4:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    currentCell.setCellValue(item.getFreeLimit()); //Setting Free Limit


                                }
                                break;
                            case 6:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    currentCell.setCellValue(item.getMinPrem()); //Setting Minimum Premium


                                }
                                break;
                            case 8:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    if (item.getScheduleItems().size() > 0) {
                                        currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(0)));
                                    } else {
                                        currentCell.setCellValue(0);
                                    }
                                } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    if (item.getScheduleItems().size() > 0) {
                                        currentCell.setCellValue(item.getScheduleItems().get(0));
                                    } else {
                                        currentCell.setCellValue("");
                                    }
                                }
                                break;
                            case 9:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    if (item.getScheduleItems().size() > 1) {
                                        currentCell.setCellValue(item.getScheduleItems().get(1));
                                    } else {
                                        currentCell.setCellValue("");
                                    }
                                } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    if (item.getScheduleItems().size() > 1) {
                                        currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(1)));
                                    } else {
                                        currentCell.setCellValue(0);
                                    }
                                }
                                break;
                            case 10:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    if (item.getScheduleItems().size() > 2) {
                                        currentCell.setCellValue(item.getScheduleItems().get(2));
                                    } else {
                                        currentCell.setCellValue("");
                                    }
                                } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    if (item.getScheduleItems().size() > 2) {
                                        currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(2)));
                                    } else {
                                        currentCell.setCellValue(0);
                                    }
                                }
                                break;
                            case 11:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    if (item.getScheduleItems().size() > 3) {
                                        currentCell.setCellValue(item.getScheduleItems().get(3));
                                    } else {
                                        currentCell.setCellValue("");
                                    }
                                } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    if (item.getScheduleItems().size() > 3) {
                                        currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(3)));
                                    } else {
                                        currentCell.setCellValue(0);
                                    }
                                }
                                break;
                            case 12:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    if (item.getScheduleItems().size() > 4) {
                                        currentCell.setCellValue(item.getScheduleItems().get(4));
                                    } else {
                                        currentCell.setCellValue("");
                                    }
                                } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    if (item.getScheduleItems().size() > 4) {
                                        currentCell.setCellValue(Double.valueOf(item.getScheduleItems().get(4)));
                                    } else {
                                        currentCell.setCellValue(0);
                                    }
                                }
                                break;
                            default:
                        }

                    }
                }

                Iterator<Row> resultiterator = sheet.iterator();
                while (resultiterator.hasNext()) {
                    Row currentRow = resultiterator.next();
                    Iterator<Cell> cellIterator = currentRow.iterator();
                    while (cellIterator.hasNext()) {
                        Cell currentCell = cellIterator.next();
                        int columnIndex = currentCell.getColumnIndex();
                        if (columnIndex == 7) {
                            if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                CellValue cellValue = evaluator.evaluate(currentCell);
                                if (cellValue.getNumberValue() != 0) {
                                    if (section != null) {
                                        section.setPrem(BigDecimal.valueOf(cellValue.getNumberValue()));
                                        totalPrem += cellValue.getNumberValue();
                                    }
                                }
                            }
                        }
                        if (columnIndex == 5) {
                            if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                evaluator.evaluateFormulaCell(currentCell);
                                if (currentCell.getNumericCellValue() != 0) {
                                    if (section.getSectId() == 477) {
                                        System.out.println(" prem..." + currentCell.getNumericCellValue());
                                    }
                                    if (section != null) {
                                        section.setCalcprem(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                    }
                                }
                            }
                        }
                    }
                }

                if (section != null)
                    sectionTrans.add(section);
            }
        }

        quotRiskLimitsRepo.save(sectionTrans);
        return new PremiumResultBean(sumInsured,totalPrem,0d);
    }
}