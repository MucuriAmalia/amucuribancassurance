package com.brokersystems.brokerapp.reconciliation.controller;

import com.brokersystems.brokerapp.reconciliation.Reconciliation;
import com.brokersystems.brokerapp.reconciliation.ReconciliationDTO;
import com.brokersystems.brokerapp.reconciliation.TransReconciliation;
import com.brokersystems.brokerapp.reconciliation.TransReconciliationDTO;
import com.brokersystems.brokerapp.reconciliation.service.ReconciliationService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("protected/reconciliation")
public class ReconciliationController {
    @Autowired
    private ReconciliationService reconciliationService;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private DataSource datasource;

    @RequestMapping(value = "uploadExcel",method = RequestMethod.POST)
    @ResponseBody
    public String uploadExcel(@RequestParam MultipartFile file) throws IOException {
        reconciliationService.processReconExcel(file);
        return "File uploaded successfully";
    }

    @RequestMapping(value = "reconciliationPage",method = RequestMethod.GET)
    public String reconciliationPage(){
        return "reconciliation";
    }

    @RequestMapping(value = "reconciliationData", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<ReconciliationDTO> getReconciliationData(@DataTable DataTablesRequest request, @RequestParam(required = false) Long accountCode, @RequestParam(required = false) Date dateFrom, @RequestParam(required = false) Date dateTo){
        return reconciliationService.getReconciliationData(request, accountCode,dateFrom,dateTo);
    }

    @RequestMapping(value = "reconcile",method = RequestMethod.POST)
    @ResponseBody
    public List<Reconciliation> reconcile(@RequestBody List<ReconciliationDTO> reconciliationData) throws BadRequestException {
        return reconciliationService.reconcileData(reconciliationData);
    }

    @RequestMapping(value = "reconciledData", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<ReconciliationDTO> getReconciledData(@DataTable DataTablesRequest request, @RequestParam(required = false) Long accountCode, @RequestParam(required = false) Date dateFrom, @RequestParam(required = false) Date dateTo) {
        return reconciliationService.getReconciledData(request, accountCode,dateFrom,dateTo);
    }
    @RequestMapping(value = "rpt_reconciliation.pdf", method = RequestMethod.GET)
    public ModelAndView creditorCommissionsRpt(@RequestParam String accountCode,
                                               @RequestParam Date dateFrom,
                                               @RequestParam Date dateTo,
                                               ModelMap modelMap)
            throws BadRequestException, IOException {
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);

        modelMap.put("logo", image);
        modelMap.put("accountCode", accountCode);
        modelMap.put("dateFrom", dateFrom);
        modelMap.put("dateTo", dateTo);
        modelMap.put("format", "pdf");
        modelMap.put("datasource", datasource);

        return new ModelAndView("rpt_reconciliation", modelMap);
    }

    @RequestMapping(value = {"bulkreconciletemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-reconcile-upload-template.xls");

        String[] sheet1Headers = {
                "POLICY", "CLIENT", "RISK NOTE NUMBER", "UNDERWRITER POLICY NUMBER", "PREMIUM", "COMMISSION", "TOTAL COMMISSION PAYABLE", "WITHHOLDING TAX"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("RECONCILE COM");
        // Create the header row
        Row headerRow = sheet1.createRow(0);
        for (int i = 0; i < sheet1Headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(sheet1Headers[i]);
            sheet1.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        //workbook.close();
    }

    @RequestMapping(value = "transReconciliationPage",method = RequestMethod.GET)
    public String transReconciliationPage(){
        return "transReconciliation";
    }

    @RequestMapping(value = "uploadExcelFile",method = RequestMethod.POST)
    @ResponseBody
    public String uploadExcelFile(@RequestParam MultipartFile file) throws IOException {
        reconciliationService.processTransReconExcel(file);
        return "File uploaded successfully";
    }

    @RequestMapping(value = "transReconciliationData", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<TransReconciliationDTO> getTransReconciliationData(@DataTable DataTablesRequest request, @RequestParam(required = false) Long accountCode, @RequestParam(required = false) Date dateFrom, @RequestParam(required = false) Date dateTo, @RequestParam(required = false) String unifiedSearch){
        return reconciliationService.getTransReconciliationData(request, accountCode,dateFrom,dateTo,unifiedSearch);
    }

    @RequestMapping(value = "transReconcile",method = RequestMethod.POST)
    @ResponseBody
    public List<TransReconciliation> transReconcile(@RequestBody List<TransReconciliationDTO> transReconciliationData) throws BadRequestException {
        return reconciliationService.transReconcileData(transReconciliationData);
    }

    @RequestMapping(value = "transReconciledData", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<TransReconciliationDTO> getTransReconciledData(@DataTable DataTablesRequest request, @RequestParam(required = false) Long accountCode, @RequestParam(required = false) Date dateFrom, @RequestParam(required = false) Date dateTo, @RequestParam(required = false) String unifiedSearch) {
        return reconciliationService.getTransReconciledData(request, accountCode,dateFrom,dateTo, unifiedSearch);
    }

    @RequestMapping(value = "deleteTransReconciledData", method = RequestMethod.POST)
    @ResponseBody
    public String deleteTransReconciledData(@RequestParam(required = false) Long accountCode,
                                            @RequestParam(required = false) Date dateFrom,
                                            @RequestParam(required = false) Date dateTo,
                                            @RequestParam(required = false) String unifiedSearch) throws BadRequestException {
        reconciliationService.deleteTransReconciledData(accountCode,dateFrom, dateTo, unifiedSearch);
        return "Transaction reconciled data deleted successfully";
    }
    @RequestMapping(value = "rpt_trans_reconciliation", method = RequestMethod.POST)
    public ModelAndView transactionReconciliationRpt(@RequestParam String accountCode,
                                               @RequestParam Date dateFrom,
                                               @RequestParam Date dateTo,
                                               @RequestParam(required = false, defaultValue = "pdf") String format,
                                               @RequestParam(required = false) String RiskNoteNumber,
                                               @RequestParam(required = false) String ReconStatus,
                                               ModelMap modelMap)
            throws BadRequestException, IOException {
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);

        String riskNoteParam = (RiskNoteNumber != null && RiskNoteNumber.trim().isEmpty()) ? null : RiskNoteNumber;
        String reconStatusParam = (ReconStatus != null && ReconStatus.trim().isEmpty()) ? null : ReconStatus;

        if (RiskNoteNumber != null && !RiskNoteNumber.trim().isEmpty()) {
            String searchValue = RiskNoteNumber.trim().toLowerCase();

            if (searchValue.equals("reconciled") || searchValue.equals("not reconciled") ||
                    searchValue.equals("unreconciled")) {
                reconStatusParam = RiskNoteNumber;
                riskNoteParam = null;
            } else {
                riskNoteParam = RiskNoteNumber;
                reconStatusParam = null;
            }
        }

        modelMap.put("logo", image);
        modelMap.put("Agent", accountCode);
        modelMap.put("dateFrom", dateFrom);
        modelMap.put("dateTo", dateTo);
        modelMap.put("format", format);
        modelMap.put("riskNoteSearch", riskNoteParam);
        modelMap.put("reconStatus", reconStatusParam);
        modelMap.put("datasource", datasource);

        return new ModelAndView("rpt_trans_reconciliation", modelMap);
    }

    @RequestMapping(value = {"bulktransreconciletemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcelFile(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=bulk-reconcile-upload-template.xls");

        String[] sheet1Headers = {
                "POLICY", "CLIENT", "RISK NOTE NUMBER", "UNDERWRITER POLICY NUMBER", "UNDERWRITER TRANS CODE", "PREMIUM", "SETTLEMENT", "BALANCE"
        };

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("RECONCILE TRANSACTIONS");
        // Create the header row
        Row headerRow = sheet1.createRow(0);
        for (int i = 0; i < sheet1Headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(sheet1Headers[i]);
            sheet1.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        //workbook.close();
    }
}
