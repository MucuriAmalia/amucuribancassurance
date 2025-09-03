package com.brokersystems.brokerapp.setup.service.impl;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.model.Budgets;
import com.brokersystems.brokerapp.setup.repository.AccountRepo;
import com.brokersystems.brokerapp.setup.repository.BudgetsRepo;
import com.brokersystems.brokerapp.setup.repository.UnloadedBudgetsRepo;
import com.brokersystems.brokerapp.setup.service.BudgetInterface;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetService implements BudgetInterface {

    @Autowired
    BudgetsRepo budgetsRepo;

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    UnloadedBudgetsRepo unloadedBudgetsRepo;

    @Override
    @Transactional(readOnly = false)
    public void deleteBudgets(Long id) {
        budgetsRepo.delete(id);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public void saveBudget(Budgets budget, String product, String agent, String branch) throws BadRequestException {
        //if(budget.getOrgBranch()==null) throw new BadRequestException("Please input a valid branch in the Excel sheer");
        //if(budget.getAccountDef()==null) throw new BadRequestException("Please input a valid User in the Excel sheet");
        if (budget.getYear() == null) {
            UnloadedBudgets unloadedBudgets = new UnloadedBudgets();
            unloadedBudgets.setName(budget.getName());
            unloadedBudgets.setAccountDef(agent);
            unloadedBudgets.setProductReportGroup(product);
            unloadedBudgets.setBranch(branch);
            unloadedBudgets.setYear(budget.getYear()==null?null:budget.getYear().toString());
            unloadedBudgets.setJanProd(budget.getJanProd()==null?null:budget.getJanProd().toString());
            unloadedBudgets.setFebProd(budget.getFebProd()==null?null:budget.getFebProd().toString());
            unloadedBudgets.setMarProd(budget.getMarProd()==null?null:budget.getMarProd().toString());
            unloadedBudgets.setAprProd(budget.getAprProd()==null?null:budget.getAprProd().toString());
            unloadedBudgets.setMayProd(budget.getMayProd()==null?null:budget.getMayProd().toString());
            unloadedBudgets.setJunProd(budget.getJunProd()==null?null:budget.getJunProd().toString());
            unloadedBudgets.setJulProd(budget.getJulProd()==null?null:budget.getJulProd().toString());
            unloadedBudgets.setAugProd(budget.getAugProd()==null?null:budget.getAugProd().toString());
            unloadedBudgets.setSepProd(budget.getAugProd()==null?null:budget.getSepProd().toString());
            unloadedBudgets.setOctProd(budget.getOctProd()==null?null:budget.getOctProd().toString());
            unloadedBudgets.setNovProd(budget.getNovProd()==null?null:budget.getNovProd().toString());
            unloadedBudgets.setDecProd(budget.getDecProd()==null?null:budget.getDecProd().toString());
            unloadedBudgets.setJanPol(budget.getJanPol()==null?null:budget.getJanPol().toString());
            unloadedBudgets.setFebPol(budget.getFebPol()==null?null:budget.getFebPol().toString());
            unloadedBudgets.setMarPol(budget.getMarPol()==null?null:budget.getMarPol().toString());
            unloadedBudgets.setAprPol(budget.getAprPol()==null?null:budget.getAprPol().toString());
            unloadedBudgets.setMayPol(budget.getMayPol()==null?null:budget.getMayPol().toString());
            unloadedBudgets.setJunPol(budget.getJunPol()==null?null:budget.getJunPol().toString());
            unloadedBudgets.setJulPol(budget.getJulPol()==null?null:budget.getJulPol().toString());
            unloadedBudgets.setAugPol(budget.getAugPol()==null?null:budget.getAugPol().toString());
            unloadedBudgets.setSepPol(budget.getSepPol()==null?null:budget.getSepPol().toString());
            unloadedBudgets.setOctPol(budget.getOctPol()==null?null:budget.getOctPol().toString());
            unloadedBudgets.setNovPol(budget.getNovPol()==null?null:budget.getNovPol().toString());
            unloadedBudgets.setDecPol(budget.getDecPol()==null?null:budget.getDecPol().toString());
            unloadedBudgets.setError("Input a valid year ensure no blanks in either field");
            unloadedBudgetsRepo.save(unloadedBudgets);
            throw new BadRequestException("Please input Year in year field");

        }
        // Predicate pred=QBudgets.budgets.orgBranch.obId.eq(budget.getOrgBranch().getObId()).
        //       and(QBudgets.budgets.productGroupDef.prgCode.eq(budget.getProductGroupDef().getPrgCode()));
        if (budget.getAccountDef() == null) {
            Predicate pred2 = QBudgets.budgets.orgBranch.obId.eq(budget.getOrgBranch().getObId()).and(QBudgets.budgets.productReportGroup.rptId.eq(budget.getProductReportGroup().getRptId()));
            Long bud2 = budgetsRepo.count(pred2);
            if (bud2 > 0) {
                UnloadedBudgets unloadedBudgets = new UnloadedBudgets();
                unloadedBudgets.setName(budget.getName());
                unloadedBudgets.setAccountDef(agent);
                unloadedBudgets.setProductReportGroup(product);
                unloadedBudgets.setBranch(branch);
                unloadedBudgets.setYear(budget.getYear()==null?null:budget.getYear().toString());
                unloadedBudgets.setJanProd(budget.getJanProd()==null?null:budget.getJanProd().toString());
                unloadedBudgets.setFebProd(budget.getFebProd()==null?null:budget.getFebProd().toString());
                unloadedBudgets.setMarProd(budget.getMarProd()==null?null:budget.getMarProd().toString());
                unloadedBudgets.setAprProd(budget.getAprProd()==null?null:budget.getAprProd().toString());
                unloadedBudgets.setMayProd(budget.getMayProd()==null?null:budget.getMayProd().toString());
                unloadedBudgets.setJunProd(budget.getJunProd()==null?null:budget.getJunProd().toString());
                unloadedBudgets.setJulProd(budget.getJulProd()==null?null:budget.getJulProd().toString());
                unloadedBudgets.setAugProd(budget.getAugProd()==null?null:budget.getAugProd().toString());
                unloadedBudgets.setSepProd(budget.getAugProd()==null?null:budget.getSepProd().toString());
                unloadedBudgets.setOctProd(budget.getOctProd()==null?null:budget.getOctProd().toString());
                unloadedBudgets.setNovProd(budget.getNovProd()==null?null:budget.getNovProd().toString());
                unloadedBudgets.setDecProd(budget.getDecProd()==null?null:budget.getDecProd().toString());
                unloadedBudgets.setJanPol(budget.getJanPol()==null?null:budget.getJanPol().toString());
                unloadedBudgets.setFebPol(budget.getFebPol()==null?null:budget.getFebPol().toString());
                unloadedBudgets.setMarPol(budget.getMarPol()==null?null:budget.getMarPol().toString());
                unloadedBudgets.setAprPol(budget.getAprPol()==null?null:budget.getAprPol().toString());
                unloadedBudgets.setMayPol(budget.getMayPol()==null?null:budget.getMayPol().toString());
                unloadedBudgets.setJunPol(budget.getJunPol()==null?null:budget.getJunPol().toString());
                unloadedBudgets.setJulPol(budget.getJulPol()==null?null:budget.getJulPol().toString());
                unloadedBudgets.setAugPol(budget.getAugPol()==null?null:budget.getAugPol().toString());
                unloadedBudgets.setSepPol(budget.getSepPol()==null?null:budget.getSepPol().toString());
                unloadedBudgets.setOctPol(budget.getOctPol()==null?null:budget.getOctPol().toString());
                unloadedBudgets.setNovPol(budget.getNovPol()==null?null:budget.getNovPol().toString());
                unloadedBudgets.setDecPol(budget.getDecPol()==null?null:budget.getDecPol().toString());
                unloadedBudgets.setError("A similar budget already exists");
                unloadedBudgetsRepo.save(unloadedBudgets);
                throw new BadRequestException("A similar budget already exists");
            } else {
                budgetsRepo.save(budget);
            }


        } else if (budget.getOrgBranch() == null) {
            Predicate pred3 = QBudgets.budgets.accountDef.acctId.eq(budget.getAccountDef().getAcctId()).and(QBudgets.budgets.productReportGroup.rptId.eq(budget.getProductReportGroup().getRptId()));
            Long bud = budgetsRepo.count(pred3);
            if (bud > 0) {
                UnloadedBudgets unloadedBudgets = new UnloadedBudgets();
                unloadedBudgets.setName(budget.getName());
                unloadedBudgets.setAccountDef(agent);
                unloadedBudgets.setProductReportGroup(product);
                unloadedBudgets.setBranch(branch);
                unloadedBudgets.setYear(budget.getYear()==null?null:budget.getYear().toString());
                unloadedBudgets.setJanProd(budget.getJanProd()==null?null:budget.getJanProd().toString());
                unloadedBudgets.setFebProd(budget.getFebProd()==null?null:budget.getFebProd().toString());
                unloadedBudgets.setMarProd(budget.getMarProd()==null?null:budget.getMarProd().toString());
                unloadedBudgets.setAprProd(budget.getAprProd()==null?null:budget.getAprProd().toString());
                unloadedBudgets.setMayProd(budget.getMayProd()==null?null:budget.getMayProd().toString());
                unloadedBudgets.setJunProd(budget.getJunProd()==null?null:budget.getJunProd().toString());
                unloadedBudgets.setJulProd(budget.getJulProd()==null?null:budget.getJulProd().toString());
                unloadedBudgets.setAugProd(budget.getAugProd()==null?null:budget.getAugProd().toString());
                unloadedBudgets.setSepProd(budget.getAugProd()==null?null:budget.getSepProd().toString());
                unloadedBudgets.setOctProd(budget.getOctProd()==null?null:budget.getOctProd().toString());
                unloadedBudgets.setNovProd(budget.getNovProd()==null?null:budget.getNovProd().toString());
                unloadedBudgets.setDecProd(budget.getDecProd()==null?null:budget.getDecProd().toString());
                unloadedBudgets.setJanPol(budget.getJanPol()==null?null:budget.getJanPol().toString());
                unloadedBudgets.setFebPol(budget.getFebPol()==null?null:budget.getFebPol().toString());
                unloadedBudgets.setMarPol(budget.getMarPol()==null?null:budget.getMarPol().toString());
                unloadedBudgets.setAprPol(budget.getAprPol()==null?null:budget.getAprPol().toString());
                unloadedBudgets.setMayPol(budget.getMayPol()==null?null:budget.getMayPol().toString());
                unloadedBudgets.setJunPol(budget.getJunPol()==null?null:budget.getJunPol().toString());
                unloadedBudgets.setJulPol(budget.getJulPol()==null?null:budget.getJulPol().toString());
                unloadedBudgets.setAugPol(budget.getAugPol()==null?null:budget.getAugPol().toString());
                unloadedBudgets.setSepPol(budget.getSepPol()==null?null:budget.getSepPol().toString());
                unloadedBudgets.setOctPol(budget.getOctPol()==null?null:budget.getOctPol().toString());
                unloadedBudgets.setNovPol(budget.getNovPol()==null?null:budget.getNovPol().toString());
                unloadedBudgets.setDecPol(budget.getDecPol()==null?null:budget.getDecPol().toString());
                unloadedBudgets.setError("A similar budget already exists");
                unloadedBudgetsRepo.save(unloadedBudgets);

                throw new BadRequestException("A similar budget already exists");
            } else {
                budgetsRepo.save(budget);
            }

        } else {
            budgetsRepo.save(budget);
        }


    }

    /*
    public void printFailures(List<Budgets> myBudgets) throws FileNotFoundException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Failed Budgets");
        int rowCount = 0;

        for (Budgets budgets: myBudgets) {
            Row row = sheet.createRow(++rowCount);

            int columnCount = 0;

if(budgets.getName()!=null) {
    Cell cell = row.createCell(1);
    cell.setCellValue(budgets.getName());
}else{
    Cell cell = row.createCell(1);

}
            if(budgets.getProductReportGroup()!=null) {
              Cell cell = row.createCell(2);
                cell.setCellValue(budgets.getProductReportGroup().getRepDesc());
            }
            else{
                Cell cell = row.createCell(2);

            }
            if(budgets.getAccountDef()!=null){
           Cell cell = row.createCell(3);
            cell.setCellValue(budgets.getAccountDef().getName());
            }
            if(budgets.getOrgBranch()!=null){
                Cell cell = row.createCell(3);
                cell.setCellValue(budgets.getOrgBranch().getObName());
            }
            else{
                Cell cell = row.createCell(3);

            }
            if(budgets.getYear()!=null){
                Cell cell = row.createCell(4);
                cell.setCellValue(budgets.getYear());
            }
            else{
                Cell cell = row.createCell(4);

            }
            if(budgets.getJanProd()!=null){
                Cell cell = row.createCell(5);
                cell.setCellValue((RichTextString) budgets.getJanProd());
            }
            else{
                Cell cell = row.createCell(5);

            }
            if(budgets.getJanPol()!=null){
                Cell cell = row.createCell(6);
                cell.setCellValue((RichTextString) budgets.getJanPol());
            }
            else{
                Cell cell = row.createCell(6);

            }
            if(budgets.getFebProd()!=null){
                Cell cell = row.createCell(7);
                cell.setCellValue((RichTextString) budgets.getFebProd());
            }
            else{
                Cell cell = row.createCell(7);

            }
            if(budgets.getFebPol()!=null){
                Cell cell = row.createCell(8);
                cell.setCellValue((RichTextString) budgets.getFebPol());
            }
            else{
                Cell cell = row.createCell(8);

            }
            if(budgets.getMarProd()!=null){
                Cell cell = row.createCell(9);
                cell.setCellValue((RichTextString) budgets.getMarProd());
            }
            else{
                Cell cell = row.createCell(9);

            }
            if(budgets.getMarPol()!=null){
                Cell cell = row.createCell(10);
                cell.setCellValue((RichTextString) budgets.getMarPol());
            }
            else{
                Cell cell = row.createCell(10);

            }
            if(budgets.getAprProd()!=null){
                Cell cell = row.createCell(11);
                cell.setCellValue((RichTextString) budgets.getAprProd());
            }
            else{
                Cell cell = row.createCell(11);

            } if(budgets.getAprPol()!=null){
                Cell cell = row.createCell(12);
                cell.setCellValue((RichTextString) budgets.getAprPol());
            }
            else{
                Cell cell = row.createCell(12);

            }
            if(budgets.getMayProd()!=null){
                Cell cell = row.createCell(13);
                cell.setCellValue((RichTextString) budgets.getMayProd());
            }
            else{
                Cell cell = row.createCell(13);

            }
            if(budgets.getMayPol()!=null){
                Cell cell = row.createCell(14);
                cell.setCellValue((RichTextString) budgets.getMayPol());
            }
            else{
                Cell cell = row.createCell(14);

            }
            if(budgets.getJunProd()!=null){
                Cell cell = row.createCell(15);
                cell.setCellValue((RichTextString) budgets.getJunProd());
            }
            else{
                Cell cell = row.createCell(15);

            }
            if(budgets.getJunPol()!=null){
                Cell cell = row.createCell(16);
                cell.setCellValue((RichTextString) budgets.getJunPol());
            }
            else{
                Cell cell = row.createCell(16);

            }
        }


        try (FileOutputStream outputStream = new FileOutputStream("Failed.xls")) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<Budgets> findBudgets(DataTablesRequest request, Long product) throws IllegalAccessException {
        if (product == null) {
            Page<Budgets> page = budgetsRepo.findAll(request.searchPredicate(QBudgets.budgets), request);
            return new DataTablesResult<>(request, page);
        } else {
            BooleanExpression predicate = QBudgets.budgets.productReportGroup.rptId.eq(product);
            Page<Budgets> page = budgetsRepo.findAll(predicate, request);
            return new DataTablesResult<>(request, page);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<Budgets> findBudgetsLife(DataTablesRequest request, Long product) throws IllegalAccessException {
        BooleanExpression pred = QBudgets.budgets.productReportGroup.rptId.eq(product);
        Page<Budgets> page = budgetsRepo.findAll(pred.and(request.searchPredicate(QBudgets.budgets)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public Budgets editBudget(Long id) {
        BooleanExpression pred = QBudgets.budgets.id.eq(id);
        Budgets budget = budgetsRepo.findOne(pred);
        return budget;
    }

    @Override
    public void editBudgets(Budgets budget) {
        Long myId = budget.getId();
        Predicate pred = QBudgets.budgets.id.eq(myId);
        Budgets budgets = budgetsRepo.findOne(pred);
        budgets.setName(budget.getName());
        budgets.setAccountDef(budget.getAccountDef());
        budgets.setOrgBranch(budget.getOrgBranch());
        budgets.setProductReportGroup(budget.getProductReportGroup());
        budgets.setYear(budget.getYear());
        budgets.setJanPol(budget.getJanPol());
        budgets.setJanProd(budget.getJanProd());
        budgets.setFebPol(budget.getFebPol());
        budgets.setFebProd(budget.getFebProd());
        budgets.setMarPol(budget.getMarPol());
        budgets.setMarProd(budget.getMarProd());
        budgets.setAprPol(budget.getAprPol());
        budgets.setAprProd(budget.getAprProd());
        budgets.setMayPol(budget.getMayPol());
        budgets.setMayProd(budget.getMayProd());
        budgets.setJunPol(budget.getJunPol());
        budgets.setJunProd(budget.getJunProd());
        budgets.setJulPol(budget.getJulPol());
        budgets.setJulProd(budget.getJulProd());
        budgets.setAugPol(budget.getAugPol());
        budgets.setAugProd(budget.getAugProd());
        budgets.setSepPol(budget.getSepPol());
        budgets.setSepProd(budget.getSepProd());
        budgets.setOctPol(budget.getOctPol());
        budgets.setOctProd(budget.getOctProd());
        budgets.setNovPol(budget.getNovPol());
        budgets.setNovProd(budget.getNovProd());
        budgets.setDecPol(budget.getDecPol());
        budgets.setDecProd(budget.getDecProd());
        budgetsRepo.save(budgets);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public void delBudgets(Long id) {
        budgetsRepo.delete(id);
    }

    @Override
    public DataTablesResult<Budgets> searchBudgets(DataTablesRequest request, String sbr) throws IllegalAccessException {
        BooleanExpression pred = QBudgets.budgets.orgBranch.obName.containsIgnoreCase(sbr);
        Page<Budgets> page = budgetsRepo.findAll(pred.and(request.searchPredicate(QBudgets.budgets)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public void saveBudgets(Budgets budget) {

    }

    @Override
    public DataTablesResult<UnloadedBudgets> findUnloaded(DataTablesRequest request) throws IllegalAccessException {
        Page<UnloadedBudgets> page=unloadedBudgetsRepo.findAll(request.searchPredicate(QUnloadedBudgets.unloadedBudgets),request);
        return new DataTablesResult<>(request,page);

    }

    @Override
    public List<UnloadedBudgets> findAll() {
        Iterable<UnloadedBudgets>  unloadedBudgets=unloadedBudgetsRepo.findAll();
        if(unloadedBudgets!=null) {
            List<UnloadedBudgets> myList=new ArrayList<>();
            UnloadedBudgets budgets=new UnloadedBudgets();
            budgets.setError("ERROR");
            budgets.setName("NAME");
            budgets.setProductReportGroup("PRODUCT");
            budgets.setBranch("BRANCH");
            budgets.setAccountDef("USER");
            budgets.setYear("YEAR");
            budgets.setJanProd("JANPROD");
            budgets.setJanPol("JANPOL");
            budgets.setFebProd("FEBPROD");
            budgets.setFebPol("FEBPOL");
            budgets.setMarProd("MARPROD");
            budgets.setMarPol("MARPOL");
            budgets.setAprProd("APRPROD");
            budgets.setAprPol("APRPOL");
            budgets.setMayProd("MAYPROD");
            budgets.setMayPol("MAYPOL");
            budgets.setJunProd("JUNPROD");
            budgets.setJunPol("JUNPOL");
            budgets.setJulProd("JULPROD");
            budgets.setJulPol("JULPOL");
            budgets.setAugProd("AUGPROD");
            budgets.setAugPol("AUGPOL");
            budgets.setSepProd("SEPPROD");
            budgets.setSepPol("SEPPOL");
            budgets.setOctProd("OCTPROD");
            budgets.setOctPol("OCTPOL");
            budgets.setNovProd("NOVPROD");
            budgets.setNovPol("NOVPOL");
            budgets.setDecProd("DECPROD");
            budgets.setDecPol("DECPOL");
            myList.add(budgets);
            unloadedBudgets.forEach(myList::add);
            return myList;
        }
        else{
            return null;
        }
    }
}