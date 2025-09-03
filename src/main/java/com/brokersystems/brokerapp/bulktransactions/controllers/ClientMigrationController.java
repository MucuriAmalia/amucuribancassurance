package com.brokersystems.brokerapp.bulktransactions.controllers;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.bulktransactions.models.ClientMigration;
import com.brokersystems.brokerapp.bulktransactions.service.ClientMigrationService;
import com.brokersystems.brokerapp.bulktransactions.repositories.ClientMigrationRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/protected/clientmigration")
public class ClientMigrationController {

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private ClientMigrationService clientMigrationService;

    @Autowired
    private ClientMigrationRepository clientMigrationRepository;

    @RequestMapping(value = "clientHomeMigration", method = RequestMethod.GET)
    public String clientHome(HttpServletRequest request) {
        auditTrailLogger.log("Accessed Client Migration Home", request, "Client Migration");
        return "bulkClientMigration";
    }

    @RequestMapping(value = "uploadClients", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadClients(@RequestParam("file") MultipartFile file, HttpServletRequest request)
            throws BadRequestException {
        auditTrailLogger.log("Uploaded bulk client file: " + file.getOriginalFilename(), request, "Bulk Client Upload");
        return clientMigrationService.uploadAndSaveClientData(file);
    }

    @RequestMapping(value = "unprocessedClients", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<ClientMigration> getUnprocessedClients(@DataTable DataTablesRequest pageable) {
        System.out.println("DataTables Request: " + pageable);
        DataTablesResult<ClientMigration> result = clientMigrationService.findUnprocessedClients(pageable);
        System.out.println("Records found: " + result.getRecordsTotal());
        return result;
    }

    @RequestMapping(value = "processSingleClient/{clientId}", method = RequestMethod.POST)
    @ResponseBody
    public String processClient(@PathVariable("clientId") Long clientId, HttpServletRequest request)
            throws BadRequestException {
        auditTrailLogger.log("Processed single client with ID: " + clientId, request, "Process Single Client");
        return clientMigrationService.processSingleClient(clientId);
    }

    @RequestMapping(value = "processBulkClients", method = RequestMethod.POST)
    @ResponseBody
    public String bulkClientProcessing(@RequestBody List<Long> clientIds, HttpServletRequest request)
            throws BadRequestException {
        auditTrailLogger.log("Processed bulk clients with IDs: " + clientIds, request, "Process Bulk Clients");
        return clientMigrationService.bulkProcessClients(clientIds);
    }

    @RequestMapping(value = "getAllClientIds", method = RequestMethod.GET)
    @ResponseBody
    public List<Long> getAllClientIds() {
        return clientMigrationRepository.findAllUnprocessedClientIds();
    }

    @RequestMapping(value = "deleteBulkClients", method = RequestMethod.POST)
    @ResponseBody
    public String deleteBulkClients(@RequestBody List<Long> clientIds, HttpServletRequest request)
            throws BadRequestException {
        auditTrailLogger.log("Delete bulk clients with IDs: " + clientIds, request, "Delete Bulk Clients");
        return clientMigrationService.deleteBulkClients(clientIds);
    }

    @RequestMapping(value = "clientMigrationTemplate", method = RequestMethod.GET)
    @ResponseBody
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Client-Migration-Template.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Client Details");

        String[] headers = {
                "Client Name", "ID Number", "KRA PIN", "Phone Number", "Email Address",
                "Date of Birth", "Branch Code", "Client Type", "Postal Address",
                "Physical Address", "Next of Kin Name", "Next of Kin Phone"
        };

        Row headerRow = ((Sheet) sheet).createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
    }
}