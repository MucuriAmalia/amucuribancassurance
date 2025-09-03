package com.brokersystems.brokerapp.bulktransactions.service.impl;

import com.brokersystems.brokerapp.bulktransactions.models.ClientMigration;
import com.brokersystems.brokerapp.bulktransactions.models.QClientMigration;
import com.brokersystems.brokerapp.bulktransactions.repositories.ClientMigrationRepository;
import com.brokersystems.brokerapp.bulktransactions.service.ClientMigrationService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.datatables.Search;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.repository.ClientRepository;
import com.brokersystems.brokerapp.setup.repository.ClientTypeRepo;
import com.brokersystems.brokerapp.setup.repository.OrgBranchRepository;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import com.brokersystems.brokerapp.setup.model.ClientTypes;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.Cell.*;

@Service
@Transactional
public class ClientMigrationServiceImpl implements ClientMigrationService {
    private static final Logger log = LoggerFactory.getLogger(ClientMigrationServiceImpl.class);

    @Autowired
    private ClientMigrationRepository clientMigrationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrgBranchRepository orgBranchRepository;

    @Autowired
    private ClientTypeRepo clientTypeRepo;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public Map<String, Object> uploadAndSaveClientData(MultipartFile file) throws BadRequestException {
        try {
            List<ClientMigration> clients = new ArrayList<>();
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            // Skip two header rows
            if (sheet.getLastRowNum() < 2) {
                throw new BadRequestException("File must contain data rows after headers");
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String username = getCurrentUsername();

            // Start from row 2 (index starts at 0, so row 2 is index 2)
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) continue;

                try {
                    ClientMigration client = new ClientMigration();

                    // Map the columns according to your Excel structure
                    client.setClientCode(getCellValueAsString(row.getCell(0)));  // Client_Code
                    client.setPrefferedPaymentMode(getCellValueAsString(row.getCell(1))); // Preferred_Paymode
                    client.setClientShortDesc(getCellValueAsString(row.getCell(2)));

                    // Combine surname and other names for client name
                    client.setClientSurname(getCellValueAsString(row.getCell(3))); // Surname
                    client.setClientOtherNames(getCellValueAsString(row.getCell(4))); // Other_Names

                    client.setClientType(getCellValueAsString(row.getCell(5)));  // Client_Type
                    client.setClientGender(getCellValueAsString(row.getCell(6))); // Gender
                    client.setIdNumber(getCellValueAsString(row.getCell(7)));    // ID_Number
                    client.setClientPassportNumber(getCellValueAsString(row.getCell(8)));  // Passport
                    client.setKraPin(getCellValueAsString(row.getCell(9)));      // Client_Pin
                    client.setClientTitle(getCellValueAsString(row.getCell(10)));  // Title
                    client.setPhysicalAddress(getCellValueAsString(row.getCell(11))); // Physical_Address
                    client.setClientCreditAllowed(getCellValueAsString(row.getCell(12))); // Credit_Allowed
                    client.setClientLimitAllowed(getCellValueAsString(row.getCell(13))); // Credit_Limit
                    client.setPhoneNumber(getCellValueAsString(row.getCell(14))); // Mobile_Number
                    client.setPostalAddress(getCellValueAsString(row.getCell(15))); // Postal_Address
                    client.setClientCountryCode(getCellValueAsString(row.getCell(16))); // Country_Code
                    client.setEmailAddress(getCellValueAsString(row.getCell(17))); // Email

                    // Handle date of birth
                    String dobString = getCellValueAsString(row.getCell(18)); // Date_Of_Birth
                    if (dobString != null && !dobString.isEmpty()) {
                        try {
                            client.setDateOfBirth(dateFormat.parse(dobString));
                        } catch (Exception e) {
                            // Try alternative date format if the first one fails
                            client.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse(dobString.split(" ")[0]));
                        }
                    }

                    client.setBranchCode(getCellValueAsString(row.getCell(19))); // Branch_Code
                    client.setClientMaritalStatus(getCellValueAsString(row.getCell(20))); // Marital_Status
                    client.setClientDefaultCommunication(getCellValueAsString(row.getCell(21))); // Default_Communication

                    // Set default values
                    client.setStatus("PENDING");
                    client.setProcessed(false);
                    client.setUploadedBy(username);

                    clients.add(client);
                } catch (Exception e) {
                    throw new BadRequestException("Error in row " + (i + 1) + ": " + e.getMessage());
                }
            }

            if (clients.isEmpty()) {
                throw new BadRequestException("No valid data found in file");
            }

            clientMigrationRepository.save(clients);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully uploaded " + clients.size() + " clients");
            response.put("count", clients.size());

            return response;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Failed to process file: " + e.getMessage());
        }
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        for (int cellNum = 0; cellNum < 21; cellNum++) { // Check first 20 cells
            if (!StringUtils.isEmpty(getCellValueAsString(row.getCell(cellNum)))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public DataTablesResult<ClientMigration> findUnprocessedClients(DataTablesRequest request) {
        if (request.getSearch() == null) {
            request.setSearch(new Search());
        }

        QClientMigration qClient = QClientMigration.clientMigration;
        BooleanExpression predicate = qClient.processed.isNull()
                .or(qClient.processed.eq(false));

        Page<ClientMigration> page = clientMigrationRepository.findAll(predicate, request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional
    public String processSingleClient(Long clientId) throws BadRequestException {
        ClientMigration migration = clientMigrationRepository.getOne(clientId);
        if (migration == null) {
            throw new BadRequestException("Client migration record not found");
        }

        if (Boolean.TRUE.equals(migration.getProcessed())) {
            throw new BadRequestException("Client already processed");
        }

        // Check if client with same ID number already exists
        if (!StringUtils.isEmpty(migration.getIdNumber()) &&
                clientRepository.findByIdNo(migration.getIdNumber()) != null) {
            throw new BadRequestException("Client with ID Number " + migration.getIdNumber() + " already exists");
        }

        if (!StringUtils.isEmpty(migration.getKraPin())) {
            ClientDef existingClient = clientRepository.findByPinNoIgnoreCase(migration.getKraPin());
            log.info("Checking KRA PIN: {} - Existing client: {}", migration.getKraPin(), existingClient);
            if (existingClient != null) {
                throw new BadRequestException("Client with KRA PIN " + migration.getKraPin() + " already exists");
            }
        }

        // Validate unique tenant number
        if (clientRepository.findByTenantNumber(migration.getClientShortDesc()) != null) {
            throw new BadRequestException("Client with short description " + migration.getClientShortDesc() + " already exists");
        }

        // Required field validations
        if (StringUtils.isEmpty(migration.getIdNumber())) {
            String message = String.format("The ID number for client %s %s is missing",
                    migration.getClientSurname(),
                    migration.getClientOtherNames());
            throw new BadRequestException(message);
        }

        if (StringUtils.isEmpty(migration.getKraPin())) {
            String message = String.format("The KRA PIN for Client %s %s with ID No: %s is missing",
                    migration.getClientSurname(),
                    migration.getClientOtherNames(),
                    migration.getIdNumber());
            log.warn(message);
            throw new BadRequestException(message);
        }

        if (StringUtils.isEmpty(migration.getClientSurname())) {
            String message = String.format("The client Surname of client %s with Id No: %s is missing",
                    migration.getClientOtherNames(),
                    migration.getIdNumber());
            throw new BadRequestException(message);
        }
        if (StringUtils.isEmpty(migration.getClientOtherNames())) {
            String message = String.format("The Other Names of client %s with ID No: %s is missing",
                    migration.getClientSurname(),
                    migration.getIdNumber());
            throw new BadRequestException(message);
        }
        if (StringUtils.isEmpty(migration.getPhoneNumber())) {
            String message = String.format("The Phone number for client %s %s with ID No: %s is missing",
                    migration.getClientSurname(),
                    migration.getClientOtherNames(),
                    migration.getIdNumber());
            throw new BadRequestException(message);
        }

        // Extract and validate client type
        String rawClientType = migration.getClientType();
        String message = String.format("The Client type for client %s %s with ID No: %s is missing",
                migration.getClientSurname(),
                migration.getClientOtherNames(),
                migration.getIdNumber());
        if (StringUtils.isEmpty(rawClientType)) {
            throw new BadRequestException(message);
        }

        // Extract last character (I or C) and map to full type description
        String clientType = rawClientType.substring(rawClientType.length() - 1);
        String typeDesc;
        switch (clientType) {
            case "I":
                typeDesc = "Individual";
                break;
            case "C":
                typeDesc = "Corporate";
                break;
            default:
                throw new BadRequestException("Invalid client type: " + clientType + ". Must be either 'I' or 'C'");
        }

        // Use existing query with mapped type description
        ClientTypes type = clientTypeRepo.findByNormalizeType(typeDesc);
        if (type == null) {
            throw new BadRequestException("Client type not found for: " + typeDesc);
        }

        ClientDef client = new ClientDef();

        // Keep the phone number exactly as it is in the migration data
        String phoneNumber = migration.getPhoneNumber();
        if (!StringUtils.isEmpty(phoneNumber)) {
            client.setPhoneNo(phoneNumber); // This will preserve the format '+***********2'
        }

        // Map the fields
        client.setTenantNumber(migration.getClientShortDesc());
        client.setFname(migration.getClientSurname());
        client.setOtherNames(migration.getClientOtherNames());
        client.setEmailAddress(migration.getEmailAddress());
        client.setDob(migration.getDateOfBirth());
        client.setPinNo(migration.getKraPin());
        client.setIdNo(migration.getIdNumber());
        client.setPassportNo(migration.getClientPassportNumber());
        client.setTenantType(type);
        client.setStatus("A");
        client.setGender(migration.getClientGender());
        client.setAddress(migration.getPhysicalAddress());
        client.setDateregistered(new Date());
        client.setDateCreated(new Date());
        client.setAuthStatus("N");
        // Update branch lookup using ob_sht_desc
        OrgBranch branch = orgBranchRepository.findByObShtDescLikeIgnoreCase(migration.getBranchCode(), null)
                .getContent()
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Branch with code " + migration.getBranchCode() + " not found"));

        // Set the branch
        client.setRegisteredbrn(branch);

        // Set the created by user
        client.setCreatedBy(userService.findById(userService.findByUserName(getCurrentUsername()).getId()));

        // Save the client
        try {
            clientRepository.save(client);
        } catch (Exception e) {
            throw new BadRequestException("Failed to save client: " + e.getMessage());
        }

        // Update migration record
        migration.setProcessed(true);
        migration.setProcessedDate(new Date());
        migration.setProcessedDate(new Date());
        migration.setProcessedBy(getCurrentUsername());
        migration.setStatus("PROCESSED");
        clientMigrationRepository.save(migration);

        return "Client processed successfully";
    }

    @Override
    @Transactional
    public String bulkProcessClients(List<Long> clientIds) throws BadRequestException {
        if (clientIds == null || clientIds.isEmpty()) {
            throw new BadRequestException("No clients selected for processing");
        }

        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (Long clientId : clientIds) {
            try {
                processSingleClient(clientId);
                successCount++;
            } catch (BadRequestException e) {
                // Collect error messages with client details
                ClientMigration client = clientMigrationRepository.getOne(clientId);
                String clientInfo = String.format("Client %s %s (ID No: %s)",
                        client.getClientSurname(),
                        client.getClientOtherNames(),
                        client.getIdNumber());
                errors.add(clientInfo + " - " + e.getMessage());
            }
        }

        // Prepare response message
        if (errors.isEmpty()) {
            return String.format("Successfully processed all %d clients", successCount);
        } else {
            String errorMessage = String.format("Processed %d out of %d clients. Errors:\n%s",
                    successCount,
                    clientIds.size(),
                    String.join("\n", errors));
            throw new BadRequestException(errorMessage);
        }
    }

    @Override
    @Transactional
    public String deleteBulkClients(List<Long> clientIds) throws BadRequestException {
        if (clientIds == null || clientIds.isEmpty()) {
            throw new BadRequestException("No clients selected for deletion");
        }

        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (Long clientId : clientIds) {
            try {
                ClientMigration client = clientMigrationRepository.findOne(clientId);
                if (client == null) {
                    errors.add("Client with ID " + clientId + " not found");
                    continue;
                }

                // Check if client is already processed
                if (Boolean.TRUE.equals(client.getProcessed())) {
                    errors.add("Client " + client.getClientSurname() + " " +
                            client.getClientOtherNames() + " is already processed and cannot be deleted");
                    continue;
                }

                // Delete the client
                clientMigrationRepository.delete(client);
                successCount++;

            } catch (Exception e) {
                ClientMigration client = clientMigrationRepository.findOne(clientId);
                String clientInfo = client != null ?
                        String.format("Client %s %s (ID: %s)",
                                client.getClientSurname(),
                                client.getClientOtherNames(),
                                client.getIdNumber()) :
                        "Client ID: " + clientId;
                errors.add(clientInfo + " - " + e.getMessage());
            }
        }

        // Prepare response message
        if (errors.isEmpty()) {
            return String.format("Successfully deleted %d clients", successCount);
        } else {
            String errorMessage = String.format("Deleted %d out of %d clients. Errors:\n%s",
                    successCount,
                    clientIds.size(),
                    String.join("\n", errors));
            throw new BadRequestException(errorMessage);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
            case CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                }
                // Check if it's a whole number
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num)) {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);
            case CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case CELL_TYPE_FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case CELL_TYPE_STRING:
                        return cell.getStringCellValue();
                    case CELL_TYPE_NUMERIC:
                        return String.valueOf(cell.getNumericCellValue());
                    case CELL_TYPE_BOOLEAN:
                        return String.valueOf(cell.getBooleanCellValue());
                    default:
                        return "";
                }
            default:
                return "";
        }
    }
}