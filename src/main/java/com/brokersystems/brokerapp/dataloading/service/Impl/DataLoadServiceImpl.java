package com.brokersystems.brokerapp.dataloading.service.Impl;

import com.brokersystems.brokerapp.dataloading.model.*;
import com.brokersystems.brokerapp.dataloading.repository.PolicyDataRepo;
import com.brokersystems.brokerapp.dataloading.service.DataLoadService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.trans.model.QTransChecks;
import com.brokersystems.brokerapp.trans.model.TransChecks;
import com.brokersystems.brokerapp.trans.repository.TransChecksRepo;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;


@Service
public class DataLoadServiceImpl implements DataLoadService {
    @Autowired
    private PolicyTransService policyTransService;

    @Autowired
    private PolicyDataRepo policyDataRepo;

    @Autowired
    private ProductsRepo productsRepo;

    @Autowired
    private BindersRepo bindersRepo;

    @Autowired
    private OrgBranchRepository branchRepo;

    @Autowired
    private CurrencyRepository currencyRepo;

    @Autowired
    private PaymentModeRepo paymentModeRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private SubClassRepo subClassRepo;

    @Autowired
    private SubClassCoverRepo subClassCoverRepo;

    @Autowired
    private BinderDetRepo binderDetRepo;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private PremComputeService premiumService;

    @Autowired
    private PolicyAuthorization authService;

    @Autowired
    private TransChecksRepo transChecksRepo;

    @Autowired
    private UserUtils userUtils;


    @Override
    public void removeUnloadedPolicies() throws BadRequestException {
        Long polCnt = policyDataRepo.count(QPolicyData.policyData.loaded.equalsIgnoreCase("N").and(QPolicyData.policyData.importedby.eq(userUtils.getCurrentUser())));
        if (polCnt==0)
        {
            throw new BadRequestException("No policies to remove");

        }
        Iterable<PolicyData> polData = policyDataRepo.findAll(QPolicyData.policyData.loaded.equalsIgnoreCase("N").and(QPolicyData.policyData.importedby.eq(userUtils.getCurrentUser())));

        for (PolicyData policyData:polData){
            policyDataRepo.delete(policyData);
        }

    }
    @Override
    public void loadPolicies() throws BadRequestException {

        Long polCnt = policyDataRepo.count(QPolicyData.policyData.loaded.equalsIgnoreCase("N").and(QPolicyData.policyData.importedby.eq(userUtils.getCurrentUser())));
        if (polCnt==0)
        {
            throw new BadRequestException("No policies to load");

        }
        Iterable<PolicyData> polData = policyDataRepo.findAll(QPolicyData.policyData.loaded.equalsIgnoreCase("N").and(QPolicyData.policyData.importedby.eq(userUtils.getCurrentUser())));

        for (PolicyData policyData:polData){
            try {
                PolicyCreateDTO policy = new PolicyCreateDTO();
//            ProductsDef product = productsRepo.findOne(QProductsDef.productsDef.proDesc.upper().equalsIgnoreCase(policyData.getProduct()));
//            policy.setProduct(product);
//            policy.setProdId(product.getProCode());
//            policy.setProductName(product.getProDesc());
                if (policyData.getBinder()==null)
                    throw new  BadRequestException("Provide Binder name");
                BindersDef binder = bindersRepo.findOne(QBindersDef.bindersDef.binShtDesc.upper().equalsIgnoreCase(policyData.getBinder())
                .or(QBindersDef.bindersDef.binName.upper().equalsIgnoreCase(policyData.getBinder())));
                if (binder==null)
                    throw new BadRequestException("binder "+policyData.getBinder() +" not found");
//                policy.setBinder(binder);
                policy.setBindName(binder.getBinName());
                policy.setBindCode(binder.getBinId());
                policy.setAgentId(binder.getAccount().getAcctId());
                policy.setBusinessType("N");
                //policy.setProduct(binder.getProduct());
                policy.setProdId(binder.getProduct().getProCode());
                policy.setProductName(binder.getProduct().getProDesc());

                // client
                if (policyData.getCid()==null)
                    throw new  BadRequestException("Provide Client Reference");
                if (clientRepo.count(QClientDef.clientDef.clientRef.equalsIgnoreCase(policyData.getCid()))==0)
                    throw new  BadRequestException("Customer reference not found");
                if (clientRepo.count(QClientDef.clientDef.clientRef.equalsIgnoreCase(policyData.getCid()))>1)
                    throw new  BadRequestException("Customer reference is used by more than one customer");
                ClientDef client = clientRepo.findOne(QClientDef.clientDef.clientRef.equalsIgnoreCase(policyData.getCid()));
                if (client==null)
                    throw new BadRequestException("client ref "+policyData.getCid() +" not found");
//                policy.setClient(client);
                policy.setClientId(client.getTenId());


                // get branch
                if (policyData.getBranch()==null)
                    throw new  BadRequestException("Provide policy Branch");
                OrgBranch branch = branchRepo.findOne(QOrgBranch.orgBranch.obName.upper().equalsIgnoreCase(policyData.getBranch()));
                if (branch==null)
                    throw new BadRequestException("Branch "+policyData.getBranch() +" not found");
//                policy.setBranch(branch);
                policy.setBranchId(branch.getObId());

                // currency
                if (policyData.getCurrency()==null)
                    throw new  BadRequestException("Provide Currency");
                Currencies currency = currencyRepo.findOne(QCurrencies.currencies.curIsoCode.upper().equalsIgnoreCase(policyData.getCurrency()));
                if (currency==null)
                    throw new BadRequestException("Currency "+policyData.getCurrency() +" not found");
//                policy.setTransCurrency(currency);
                policy.setCurrencyId(currency.getCurCode());

                policy.setWefDate(policyData.getEffdate());
                policy.setWetDate(policyData.getExpdate());
                policy.setInterfaceType("A");

                //pay method

                PaymentModes paymentMode = paymentModeRepo.findOne(QPaymentModes.paymentModes.pmShtDesc.upper().equalsIgnoreCase("CASH"));

                //policy.setPaymentMode(paymentMode);
                policy.setPaymentId(paymentMode.getPmId());

                policy.setFrequency("A");


                // risk details creation
                RiskTransBean riskTrans = new RiskTransBean();
                riskTrans.setInsuredCode(client.getTenId());
                if (policyData.getCovertype()==null)
                    throw new  BadRequestException("Provide Cover Type");
                BinderDetails det = binderDetRepo.findOne(QBinderDetails.binderDetails.binder.binId.eq(binder.getBinId()).and(QBinderDetails.binderDetails.isNotNull())
                        .and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covName.containsIgnoreCase(policyData.getCovertype())
                                .or(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covShtDesc.containsIgnoreCase(policyData.getCovertype()))));
                if (det==null)
                    throw new BadRequestException("Cover Type "+policyData.getCovertype() +" not found");
                SubClassDef subClass = det.getSubCoverTypes().getSubclass();
                riskTrans.setSclCode(subClass.getSubId());


                BinderDetails bindeDetails = binderDetRepo.findOne(QBinderDetails.binderDetails.subCoverTypes.subclass.subId.eq(subClass.getSubId()).and(QBinderDetails.binderDetails.binder.binId.eq(binder.getBinId()))
                        .and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covName.containsIgnoreCase(policyData.getCovertype())
                                .or(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covShtDesc.containsIgnoreCase(policyData.getCovertype()))));
                if (bindeDetails==null)
                    throw new BadRequestException("Cover Type "+policyData.getCovertype() +" not found");
                CoverTypesDef coverType = bindeDetails.getSubCoverTypes().getCoverTypes();
                riskTrans.setCoverCode(coverType.getCovId());
                riskTrans.setRiskShtDesc(policyData.getRiskId());
                riskTrans.setRiskDesc(policyData.getRiskDescription());
                riskTrans.setWefDate(policyData.getEffdate());
                riskTrans.setWetDate(policyData.getExpdate());
                riskTrans.setProrata("F");
                riskTrans.setCommRate(policyTransService.getCommissionRate(binder.getBinId()));
                riskTrans.setButchargePrem(policyData.getPremium());
                riskTrans.setInstallmentNo(new Long(1));
                riskTrans.setBinderDet(bindeDetails.getDetId());
                riskTrans.setBindCode(binder.getBinId());
                if (riskTrans == null)
                    throw new BadRequestException("No risks to be created");

                policy.setRiskBean(riskTrans);
                // premium items
                Set<RiskSectionBean> rates = null;
                if (binder.getProduct().getAgeApplicable() != null && "Y".equalsIgnoreCase(binder.getProduct().getAgeApplicable())) {
                    Integer age = dateUtils.getAge(client.getDob());
                    if (binder.getPremiumAgeType() != null && "N".equalsIgnoreCase(binder.getPremiumAgeType()))
                        age = age + 1;

                    System.out.println(bindeDetails.getDetId() + ";age=" + age + ";" + age.longValue());
                    rates = policyTransService.getBinderClientPremRates(bindeDetails.getDetId(), age.longValue());
                } else {
                    rates = policyTransService.getBinderPremRates(bindeDetails.getDetId());
                }
                if (rates == null || rates.isEmpty())
                    throw new BadRequestException("No risk section to be created");
                System.out.println("rates=" + rates.toString());
                List<RiskSectionBean> riskSectionBeans = new ArrayList<>();
                for (RiskSectionBean sectionBean : rates) {
                    //  if ("Y".equalsIgnoreCase(sectionBean.getMandatory())) {
                    RiskSectionBean riskSectionBean = new RiskSectionBean();
                    riskSectionBean.setMandatory(sectionBean.getMandatory());
                    riskSectionBean.setAmount(policyData.getBenefitvalue());
                    riskSectionBean.setSection(sectionBean.getPremId());
                    riskSectionBean.setRatesApplicable(sectionBean.getRatesApplicable());
                    riskSectionBean.setRate(sectionBean.getRate());
                    riskSectionBean.setDivFactor(sectionBean.getDivFactor());
                    riskSectionBean.setFreeLimit(sectionBean.getFreeLimit());
                    riskSectionBeans.add(riskSectionBean);
                    //   }
                }
                if (riskSectionBeans.isEmpty())
                    throw new BadRequestException("No premium items selected");

                policy.setSections(riskSectionBeans);

                PolicyTrans createdPolicy = policyTransService.createPolicy(policy, false);
                policyData.setPolid(createdPolicy);
                policyData.setLoaded("Y");
                policyDataRepo.save(policyData);
                // premium computation
                if ("NB".equalsIgnoreCase(createdPolicy.getTransType()) || "SP".equalsIgnoreCase(createdPolicy.getTransType()) || "EX".equalsIgnoreCase(createdPolicy.getTransType()) || "RN".equalsIgnoreCase(createdPolicy.getTransType()))
                    try {
                        premiumService.computePrem(createdPolicy.getPolicyId());
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new BadRequestException(e.getMessage());
                    }
                else if ("EN".equalsIgnoreCase(createdPolicy.getTransType())) {
                    try {
                        premiumService.computeEndorsePremium(createdPolicy.getPolicyId());
                    } catch (IOException e) {
                        throw new BadRequestException(e.getMessage());
                    }
                }


                // make ready

                String response = policyTransService.makeReady(createdPolicy.getPolicyId());
                policy.setStatus(response);


                // authorize checks
                Iterable<TransChecks> checks = transChecksRepo.findAll(QTransChecks.transChecks.policyTrans.policyId.eq(createdPolicy.getPolicyId()));
                for (TransChecks check : checks) {
                    policyTransService.approveException(check.getTcNo(), createdPolicy.getPolicyId());
                }


                // authorize policy
                //authService.authorizePolicy(createdPolicy.getPolicyId(), BigDecimal.ZERO);
                authService.authorizePolicy(createdPolicy.getPolicyId(), BigDecimal.ZERO, false);
                policyTransService.dispatchDocuments(createdPolicy.getPolicyId());

            } catch (Exception e){
                policyData.setComments(e.toString().substring(e.toString().indexOf(":")+1).trim());
                policyDataRepo.save(policyData);


            }

        }


    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PolicyData> getPoliciesToLoad(DataTablesRequest request, String status) throws IllegalAccessException {
        BooleanExpression pred = QPolicyData.policyData.loaded.eq(status).and(QPolicyData.policyData.importedby.eq(userUtils.getCurrentUser()));
        Page<PolicyData> page = policyDataRepo.findAll(pred.and(request.searchPredicate(QPolicyData.policyData)),request);
        return new DataTablesResult<>(request, page);
    }


    @Override
    public void importPolicies(MultipartFile file) throws IOException {
        List<PolicyData> polsList = new ArrayList<>();
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
                        PolicyData polTrans = new PolicyData();
                        Iterator<Cell> cellIterator = currentRow.iterator();
                        while (cellIterator.hasNext()) {
                            Cell currentCell = cellIterator.next();
                            int columnIndex = currentCell.getColumnIndex();
                            switch (columnIndex) {
                                case 0:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setProduct(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 1:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setBinder(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 2:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setUnderwriter(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 3:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setBranch(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 4:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setCurrency(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 5:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setEffdate(currentCell.getDateCellValue());
                                    } else
                                    if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                                        polTrans.setEffdate(currentCell.getDateCellValue());
                                    }
                                    break;
                                case 6:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setExpdate(currentCell.getDateCellValue());
                                    } else
                                    if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                                        polTrans.setExpdate(currentCell.getDateCellValue());
                                    }
                                    break;
                                case 7:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setDebitdate(currentCell.getDateCellValue());
                                    }else
                                    if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                                        polTrans.setDebitdate(currentCell.getDateCellValue());
                                    }
                                    break;
                                case 8:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setRisknoteno(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 9:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setNegotiatedPrem(new BigDecimal(currentCell.getStringCellValue()));
                                    }
                                    else  if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        polTrans.setNegotiatedPrem(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                    }
                                    break;
                                case 10:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setRiskId(currentCell.getStringCellValue());
                                    }
                                    break;
                                case 11:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setRiskDescription(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 12:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setCovertype(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 13:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setBenefitvalue(new BigDecimal(currentCell.getStringCellValue()));
                                    }
                                    else  if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        polTrans.setBenefitvalue(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                    }
                                    break;
                                case 14:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setAccountNumber(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 15:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setCid(currentCell.getStringCellValue());
                                    }else
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        polTrans.setCid(String.valueOf((int)currentCell.getNumericCellValue()));
                                    }
                                    break;
                                case 16:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setDebitRef(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 17:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setAccountName(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                                case 18:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setAmount(new BigDecimal(currentCell.getStringCellValue()));
                                    }
                                    else  if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        polTrans.setAmount(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                    }
                                    break;
                                case 19:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setPremium(new BigDecimal(currentCell.getStringCellValue()));
                                    }
                                    else  if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        polTrans.setPremium(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                                    }
                                    break;
                                case 20:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setFamilysize(currentCell.getStringCellValue());
                                    }
                                    break;
                                case 21:
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                        polTrans.setPfno(currentCell.getStringCellValue().trim());
                                    }
                                    break;
                            }
                        }
                        polTrans.setLoaded("N");
                        polTrans.setDateImported(new Date());
                        polTrans.setImportedby(userUtils.getCurrentUser());
                        polsList.add(polTrans);
                    }

                }

            }
        }
        policyDataRepo.save(polsList);

    }






}
