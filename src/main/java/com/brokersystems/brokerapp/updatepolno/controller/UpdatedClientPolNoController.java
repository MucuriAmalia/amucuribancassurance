package com.brokersystems.brokerapp.updatepolno.controller;

import com.brokersystems.brokerapp.accounts.dtos.SettlementDTO;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.repository.AccountRepo;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.updatepolno.dto.UpdateClientPolNoDTO;
import com.brokersystems.brokerapp.updatepolno.service.UpdateClientPolNoService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/protected/update/insurerpolno")
public class UpdatedClientPolNoController {

    @Autowired
    private UpdateClientPolNoService updateClientPolNoService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private DataSource datasource;

    @RequestMapping(value = "updatecreation", method = {RequestMethod.GET})
    public String transHome(HttpServletRequest request){
        auditTrailLogger.log("Accessed Updated Policy Number Processing Home", request, "Update policy number screen");
        return "updatecreation";
    }

    @RequestMapping(value = "viewupdates", method = {RequestMethod.GET})
    public String authorizedPolicy(HttpServletRequest request){
        auditTrailLogger.log("Accessed view update policy number Home", request, "view update policy number screen");
        return "viewupdates";
    }

    @RequestMapping(value = "uploadpolicies", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadUpdateExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Uploaded policy updates file: " + file, request, "Policy update file");
        return updateClientPolNoService.uploadClientPol(file);
    }

    @RequestMapping(value = {"unprocessedupdate"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<UpdateClientPolNoDTO> getUnProcessedUpdates(@DataTable DataTablesRequest pageable) {
        return updateClientPolNoService.findUnprocessedUpdates(pageable);
    }

//    @RequestMapping(value = {"viewupdated"}, method = {RequestMethod.GET})
//    @ResponseBody
//    public DataTablesResult<UpdateClientPolNoDTO> getprocessedPolUpdates(@DataTable DataTablesRequest pageable) {
//        return updateClientPolNoService.viewProcessedUpdates(pageable);
//    }

    @RequestMapping(value = { "viewupdated" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<UpdateClientPolNoDTO> getInsurancePaymentTransComm(@DataTable DataTablesRequest pageable,
                                                            @RequestParam(value = "status", required = true) Boolean status,
                                                            @RequestParam(value = "agentCode", required = false) Long agentCode,
                                                            @RequestParam(value = "wefDate", required = false) Date wefDate,
                                                            @RequestParam(value = "wetDate", required = false) Date wetDate,
                                                            HttpServletRequest request)
            throws IllegalAccessException {
        auditTrailLogger.log("3. Searched for insurance payments using the paramaters Insurance Co. "+
                ((agentCode!=null)? accountRepo.findOne(agentCode).getShtDesc():"") +" Wef Date: "+
                new SimpleDateFormat("yyyy-MM-dd").format(wefDate)+" Wet Date: "+new SimpleDateFormat("yyyy-MM-dd").format(wetDate)+
                " in "+ status,request, "Insurance Payments");
        request.getSession().setAttribute("status",status);
        request.getSession().setAttribute("agentCode",agentCode);
        request.getSession().setAttribute("wefDate",wefDate);
        request.getSession().setAttribute("wetDate",wetDate);
        return updateClientPolNoService.viewProcessedUpdates(pageable, agentCode,status,wefDate,wetDate);
    }

    @RequestMapping(value = "rpt_updated_policies.pdf", method = RequestMethod.GET)
    public ModelAndView creditorCommissionsRpt(ModelMap modelMap,
                                               HttpServletRequest request,
                                               ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long agentCode =  (Long)request.getSession().getAttribute("agentCode");
        Date wefDate = (Date)  request.getSession().getAttribute("wefDate");
        Date wetDate = (Date) request.getSession().getAttribute("wetDate");
        Boolean status = (Boolean) request.getSession().getAttribute("status");

        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("agentCode", agentCode);
        modelMap.put("dateFrom", wefDate);
        modelMap.put("dateTo", wetDate);
        modelMap.put("status", status);
        System.out.println(modelMap);
        modelAndView = new ModelAndView("rpt_updated_policies", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = {"approvepolupdates"}, method = {RequestMethod.POST})
    @ResponseBody
    public String approveProcessPolNoUpdates(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Approved updating of underwriter policy numbers with IDs: " + policyIds, request, "Approved update for underwriter policy numbers");
        return updateClientPolNoService.processPolNoUpdates(policyIds);
    }

    @RequestMapping(value = {"deleteUploadedPolNo"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteUploadedPolNoUpdates(@RequestBody List<Long> policyIds, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Delete updating of underwriter policy numbers with IDs: " + policyIds, request, "Delete update for underwriter policy numbers");
        return updateClientPolNoService.deleteUploadedPolNoUpdates(policyIds);
    }

    @RequestMapping(value = {"bulkUpdateUnderwriterTemplate"} , method = {RequestMethod.GET})
    @ResponseBody
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Bulk-upload-update-underwriter-template.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Underwriter Update");

        String[] headers = { "InsureMaster Policy No","underwriter Policy No", "Underwriter Trans Code", "Risk Note No"};

        // Create Header Row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            sheet.autoSizeColumn(i);
        }

        // Write the file to the response output stream
        workbook.write(response.getOutputStream());
        //workbook.close();
    }
}
