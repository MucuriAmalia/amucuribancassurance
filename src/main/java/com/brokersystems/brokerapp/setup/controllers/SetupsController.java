package com.brokersystems.brokerapp.setup.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.brokersystems.brokerapp.accounts.dtos.BankBranchDTO;
import com.brokersystems.brokerapp.accounts.model.Banks;
import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.dms.model.UploadBean;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.setup.model.RejectedReasons;
import com.brokersystems.brokerapp.setup.model.PublicHolidays;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.*;
import com.brokersystems.brokerapp.setup.service.impl.EscalationRecordService;
import com.brokersystems.brokerapp.trans.model.TransactionMapping;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping({"/protected/setups"})
public class SetupsController {
    private static final Logger logger = LoggerFactory.getLogger(SetupsController.class);

    @Autowired
    private ClassesService classService;

    @Autowired
    private ProductGroupRepo productGroupRepo;

    @Autowired
    ProductsRepo productsRepo;

    @Autowired
    private SetupsService setupsService;

    @Autowired
    LocationUtils locationUtils;

    @Autowired
    private ParamService paramService;

    @Autowired
    private ValidatorUtils validator;

    @Autowired
    BudgetInterface budgetInterface;

    @Autowired
    UploadService uploadService;

    @Autowired
    private UnloadedBudgetsRepo unloadedBudgetsRepo;

    @Autowired
    AuditTrailLogger auditTrailLogger;

    @Autowired
    private TaskEscalationConfigRepository configRepository;

    @Autowired
    private EscalationLevelRepository escalationLevelRepository;

    @Autowired
    private EscalationRecordService escalationRecordService;

    @Autowired
    private EscalationActivityLoggerRepository loggerRepository;

    @Autowired
    private SubAgentMarketerImportService subAgentMarketerImportService;
    @Autowired
    private AccountDocsRepo accountDocsRepo;
    @Autowired
    private FormLockManager formLockManager;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;

    @InitBinder({"account"})
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @ModelAttribute
    public ModelHelperForm createHelperForm() {
        return new ModelHelperForm();
    }

    @RequestMapping(value = "currency", method = RequestMethod.GET)
    public String currencyHome(Model model, HttpServletRequest request) {

        String message = "Accessed Currency Home Screen";
        String resource = "Currencies";
        auditTrailLogger.log(message, request, resource);
        return "currency";
    }

    @RequestMapping(value = "budgetRep", method = RequestMethod.GET)
    public String budRep(Model model, HttpServletRequest request) {

        String message = "Accessed Budget Reports Setup Screen";
        String resource = "Budget Reports";
        auditTrailLogger.log(message, request, resource);
        return "budgetRep";
    }

    @RequestMapping(value = "contract", method = RequestMethod.GET)
    public String contracts(Model model, HttpServletRequest request) {

        String message = "Accessed Contracts Setup Screen";
        String resource = "Contracts";
        auditTrailLogger.log(message, request, resource);
        return "binderList";
    }

    @RequestMapping(value = {"classesList"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ClassesDef> getClasses(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return classService.findAllClasses(pageable);
    }

    @RequestMapping(value = "escalation", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ACCESS_ESCALATION_MATRIX')")
    public String getEscalationPage(Model model, HttpServletRequest request) {
        String message = "Accessed Escalation Setup Screen";
        String resource = "Escalation Matrix";
        auditTrailLogger.log(message, request, resource);
        return "escalation";
    }

    @RequestMapping(value = {"getEscalationLevels"}, method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<List<EscalationLevel>> getEscalationLevels() {
        List<EscalationLevel> roles = escalationLevelRepository.findAll(); // Use your repository to fetch roles
        return ResponseEntity.ok(roles);
    }

    @RequestMapping(value = {"getEscalationRecordsByBranch"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<EscalationRecord> getEscalationRecordsByBranch(
            @RequestParam Long branchId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDate
    ) {
        System.out.println("From controller Branch: " + branchId + " start: " + startDate + " end: " + endDate);
        return escalationRecordService.getEscalationRecordsByBranchAndDateRange(branchId, startDate, endDate);
    }


    @RequestMapping(value = {"getActivityRecordsByTask"}, method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<List<EscalationActivityLogger>> getActivityRecordsByTask(
            @RequestParam Long taskId
    ) {
        List<EscalationActivityLogger> activityRecords = loggerRepository.findByTaskId(taskId);
        return ResponseEntity.ok(activityRecords);
    }

    @RequestMapping(value = {"getEscalationList"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<TaskEscalationConfig> getEscalationList() {
        return configRepository.findAll();
    }

    @RequestMapping(value = {"saveEscalationList"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<String> saveEscalationList(@RequestBody TaskEscalationConfig config) {
        // Check if the task type already exists
        TaskEscalationConfig existingConfig = configRepository.findByTaskType(config.getTaskType());
        if (existingConfig != null && !existingConfig.getEscConfigId().equals(config.getEscConfigId())) {
            // If the taskType exists and it's not the current record, throw an exception
            throw new IllegalArgumentException("Configurations for this task already exists. Check the table to edit.");
        }

        // Save or update the configuration
        configRepository.save(config);
        return ResponseEntity.ok("Configuration saved successfully");
    }

    @RequestMapping(value = "createBudget", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void saveBudget(Budgets budget) throws BadRequestException, FileNotFoundException {
        budgetInterface.saveBudgets(budget);
    }

    @RequestMapping(value = "saveExcel", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void saveBudgetExcel(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String product,
                                @RequestParam(required = false) String agent,
                                @RequestParam(required = false) String branch,
                                @RequestParam(required = false) Integer year,
                                @RequestParam(required = false) BigDecimal janProd,
                                @RequestParam(required = false) BigDecimal febProd,
                                @RequestParam(required = false) BigDecimal marProd,
                                @RequestParam(required = false) BigDecimal aprProd,
                                @RequestParam(required = false) BigDecimal mayProd,
                                @RequestParam(required = false) BigDecimal junProd,
                                @RequestParam(required = false) BigDecimal julProd,
                                @RequestParam(required = false) BigDecimal augProd,
                                @RequestParam(required = false) BigDecimal sepProd,
                                @RequestParam(required = false) BigDecimal octProd,
                                @RequestParam(required = false) BigDecimal novProd,
                                @RequestParam(required = false) BigDecimal decProd,
                                @RequestParam(required = false) BigDecimal janPol,
                                @RequestParam(required = false) BigDecimal febPol,
                                @RequestParam(required = false) BigDecimal marPol,
                                @RequestParam(required = false) BigDecimal aprPol,
                                @RequestParam(required = false) BigDecimal mayPol,
                                @RequestParam(required = false) BigDecimal junPol,
                                @RequestParam(required = false) BigDecimal julPol,
                                @RequestParam(required = false) BigDecimal augPol,
                                @RequestParam(required = false) BigDecimal sepPol,
                                @RequestParam(required = false) BigDecimal octPol,
                                @RequestParam(required = false) BigDecimal novPol,
                                @RequestParam(required = false) BigDecimal decPol
    ) throws BadRequestException, FileNotFoundException {

        ProductReportGroup productGroupDef = null;
        OrgBranch orgBranch = null;
        AccountDef accountDef = null;
        Budgets budgets = new Budgets();
        if (product != null && branch == null) {
            productGroupDef = setupsService.findThisProduct(product, name, branch, agent, year,
                    janPol, janProd, febProd, febPol, marProd, marPol, aprProd, aprPol, mayProd, mayPol,
                    junProd, junPol, julProd, julPol, augProd, augPol, sepProd, sepPol,
                    octProd, octPol, novProd, novPol, decProd, decPol);
            accountDef = setupsService.findThisAgent(product, name, branch, agent, year,
                    janPol, janProd, febProd, febPol, marProd, marPol, aprProd, aprPol, mayProd, mayPol,
                    junProd, junPol, julProd, julPol, augProd, augPol, sepProd, sepPol,
                    octProd, octPol, novProd, novPol, decProd, decPol);

            budgets.setProductReportGroup(productGroupDef);
            budgets.setAccountDef(accountDef);

            budgets.setName(name);
            budgets.setYear(year);
            budgets.setJanProd(janProd);
            budgets.setFebProd(febProd);
            budgets.setMarProd(marProd);
            budgets.setAprProd(aprProd);
            budgets.setMayProd(mayProd);
            budgets.setJunProd(junProd);
            budgets.setJulProd(julProd);
            budgets.setAugProd(augProd);
            budgets.setSepProd(sepProd);
            budgets.setOctProd(octProd);
            budgets.setNovProd(novProd);
            budgets.setDecProd(decProd);
            budgets.setJanPol(janPol);
            budgets.setFebPol(febPol);
            budgets.setMarPol(marPol);
            budgets.setAprPol(aprPol);
            budgets.setMayPol(mayPol);
            budgets.setJunPol(junPol);
            budgets.setJulPol(julPol);
            budgets.setAugPol(augPol);
            budgets.setSepPol(sepPol);
            budgets.setOctPol(octPol);
            budgets.setNovPol(novPol);
            budgets.setDecPol(decPol);
            budgetInterface.saveBudget(budgets, product, agent, branch);
        } else if (product != null && agent == null) {

            productGroupDef = setupsService.findThisProduct(product, name, branch, agent, year,
                    janPol, janProd, febProd, febPol, marProd, marPol, aprProd, aprPol, mayProd, mayPol,
                    junProd, junPol, julProd, julPol, augProd, augPol, sepProd, sepPol,
                    octProd, octPol, novProd, novPol, decProd, decPol);
            orgBranch = setupsService.findThisBranch(product, name, branch, agent, year,
                    janPol, janProd, febProd, febPol, marProd, marPol, aprProd, aprPol, mayProd, mayPol,
                    junProd, junPol, julProd, julPol, augProd, augPol, sepProd, sepPol,
                    octProd, octPol, novProd, novPol, decProd, decPol);

            budgets.setProductReportGroup(productGroupDef);
            budgets.setOrgBranch(orgBranch);

            budgets.setName(name);
            budgets.setYear(year);
            budgets.setJanProd(janProd);
            budgets.setFebProd(febProd);
            budgets.setMarProd(marProd);
            budgets.setAprProd(aprProd);
            budgets.setMayProd(mayProd);
            budgets.setJunProd(junProd);
            budgets.setJulProd(julProd);
            budgets.setAugProd(augProd);
            budgets.setSepProd(sepProd);
            budgets.setOctProd(octProd);
            budgets.setNovProd(novProd);
            budgets.setDecProd(decProd);
            budgets.setJanPol(janPol);
            budgets.setFebPol(febPol);
            budgets.setMarPol(marPol);
            budgets.setAprPol(aprPol);
            budgets.setMayPol(mayPol);
            budgets.setJunPol(junPol);
            budgets.setJulPol(julPol);
            budgets.setAugPol(augPol);
            budgets.setSepPol(sepPol);
            budgets.setOctPol(octPol);
            budgets.setNovPol(novPol);
            budgets.setDecPol(decPol);
            budgetInterface.saveBudget(budgets, product, agent, branch);
        } else if (product != null && agent != null && branch != null) {
            productGroupDef = setupsService.findThisProduct(product, name, branch, agent, year,
                    janPol, janProd, febProd, febPol, marProd, marPol, aprProd, aprPol, mayProd, mayPol,
                    junProd, junPol, julProd, julPol, augProd, augPol, sepProd, sepPol,
                    octProd, octPol, novProd, novPol, decProd, decPol);
            accountDef = setupsService.findThisAgent(product, name, branch, agent, year,
                    janPol, janProd, febProd, febPol, marProd, marPol, aprProd, aprPol, mayProd, mayPol,
                    junProd, junPol, julProd, julPol, augProd, augPol, sepProd, sepPol,
                    octProd, octPol, novProd, novPol, decProd, decPol);
            orgBranch = setupsService.findThisBranch(product, name, branch, agent, year,
                    janPol, janProd, febProd, febPol, marProd, marPol, aprProd, aprPol, mayProd, mayPol,
                    junProd, junPol, julProd, julPol, augProd, augPol, sepProd, sepPol,
                    octProd, octPol, novProd, novPol, decProd, decPol);

            budgets.setProductReportGroup(productGroupDef);
            budgets.setAccountDef(accountDef);
            budgets.setOrgBranch(orgBranch);

            budgets.setName(name);
            budgets.setYear(year);
            budgets.setJanProd(janProd);
            budgets.setFebProd(febProd);
            budgets.setMarProd(marProd);
            budgets.setAprProd(aprProd);
            budgets.setMayProd(mayProd);
            budgets.setJunProd(junProd);
            budgets.setJulProd(julProd);
            budgets.setAugProd(augProd);
            budgets.setSepProd(sepProd);
            budgets.setOctProd(octProd);
            budgets.setNovProd(novProd);
            budgets.setDecProd(decProd);
            budgets.setJanPol(janPol);
            budgets.setFebPol(febPol);
            budgets.setMarPol(marPol);
            budgets.setAprPol(aprPol);
            budgets.setMayPol(mayPol);
            budgets.setJunPol(junPol);
            budgets.setJulPol(julPol);
            budgets.setAugPol(augPol);
            budgets.setSepPol(sepPol);
            budgets.setOctPol(octPol);
            budgets.setNovPol(novPol);
            budgets.setDecPol(decPol);
            budgetInterface.saveBudget(budgets, product, agent, branch);

        } else if (product == null) {
            accountDef = setupsService.findThisAgent(product, name, branch, agent, year,
                    janPol, janProd, febProd, febPol, marProd, marPol, aprProd, aprPol, mayProd, mayPol,
                    junProd, junPol, julProd, julPol, augProd, augPol, sepProd, sepPol,
                    octProd, octPol, novProd, novPol, decProd, decPol);
            orgBranch = setupsService.findThisBranch(product, name, branch, agent, year,
                    janPol, janProd, febProd, febPol, marProd, marPol, aprProd, aprPol, mayProd, mayPol,
                    junProd, junPol, julProd, julPol, augProd, augPol, sepProd, sepPol,
                    octProd, octPol, novProd, novPol, decProd, decPol);

            budgets.setAccountDef(accountDef);
            budgets.setOrgBranch(orgBranch);

            budgets.setName(name);
            budgets.setYear(year);
            budgets.setJanProd(janProd);
            budgets.setFebProd(febProd);
            budgets.setMarProd(marProd);
            budgets.setAprProd(aprProd);
            budgets.setMayProd(mayProd);
            budgets.setJunProd(junProd);
            budgets.setJulProd(julProd);
            budgets.setAugProd(augProd);
            budgets.setSepProd(sepProd);
            budgets.setOctProd(octProd);
            budgets.setNovProd(novProd);
            budgets.setDecProd(decProd);
            budgets.setJanPol(janPol);
            budgets.setFebPol(febPol);
            budgets.setMarPol(marPol);
            budgets.setAprPol(aprPol);
            budgets.setMayPol(mayPol);
            budgets.setJunPol(junPol);
            budgets.setJulPol(julPol);
            budgets.setAugPol(augPol);
            budgets.setSepPol(sepPol);
            budgets.setOctPol(octPol);
            budgets.setNovPol(novPol);
            budgets.setDecPol(decPol);

            UnloadedBudgets unloadedBudgets = new UnloadedBudgets();
            unloadedBudgets.setName(budgets.getName());
            unloadedBudgets.setAccountDef(agent);
            unloadedBudgets.setBranch(branch);
            unloadedBudgets.setYear(budgets.getYear().toString());
            unloadedBudgets.setJanProd(budgets.getJanProd().toString());
            unloadedBudgets.setFebProd(budgets.getFebProd().toString());
            unloadedBudgets.setMarProd(budgets.getMarProd().toString());
            unloadedBudgets.setAprProd(budgets.getAprProd().toString());
            unloadedBudgets.setMayProd(budgets.getMayProd().toString());
            unloadedBudgets.setJunProd(budgets.getJunProd().toString());
            unloadedBudgets.setJulProd(budgets.getJulProd().toString());
            unloadedBudgets.setAugProd(budgets.getAugProd().toString());
            unloadedBudgets.setSepProd(budgets.getSepProd().toString());
            unloadedBudgets.setOctProd(budgets.getOctProd().toString());
            unloadedBudgets.setNovProd(budgets.getNovProd().toString());
            unloadedBudgets.setDecProd(budgets.getDecProd().toString());
            unloadedBudgets.setJanPol(budgets.getJanPol().toString());
            unloadedBudgets.setFebPol(budgets.getFebPol().toString());
            unloadedBudgets.setMarPol(budgets.getMarPol().toString());
            unloadedBudgets.setAprPol(budgets.getAprPol().toString());
            unloadedBudgets.setMayPol(budgets.getMayPol().toString());
            unloadedBudgets.setJunPol(budgets.getJunPol().toString());
            unloadedBudgets.setJulPol(budgets.getJulPol().toString());
            unloadedBudgets.setAugPol(budgets.getAugPol().toString());
            unloadedBudgets.setSepPol(budgets.getSepPol().toString());
            unloadedBudgets.setOctPol(budgets.getOctPol().toString());
            unloadedBudgets.setNovPol(budgets.getNovPol().toString());
            unloadedBudgets.setDecPol(budgets.getDecPol().toString());
            unloadedBudgets.setError("Input a product and ensure no blanks in the field");
            unloadedBudgetsRepo.save(unloadedBudgets);
            throw new BadRequestException("Ensure Product Group is not blank");
        }
    }

    @RequestMapping(value = {"getUnloadedBudgets"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<UnloadedBudgets> getUnloadedBudgets(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return budgetInterface.findUnloaded(pageable);
    }

    @RequestMapping(value = {"checkUnloaded"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    Iterable<UnloadedBudgets> myBudgets() {
        return unloadedBudgetsRepo.findAll();
    }

    @RequestMapping(value = {"drawUnloads"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    List<UnloadedBudgets> unLoads() {
        return budgetInterface.findAll();
    }

    @RequestMapping(value = {"deleteAll"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public void deleteUnloaded() {
        unloadedBudgetsRepo.deleteAll();
    }

    @RequestMapping(value = {"createGroup"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void createClass(ProductGroupDef group) throws IllegalAccessException, IOException, BadRequestException {
        classService.createProductGroup(group);
    }

    @RequestMapping(value = {"products"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ProductsDef> getPoducts(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return classService.findAllProduct(pageable);
    }

    @RequestMapping(value = {"products/{prg}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ProductsDef> getPoductsPrg(@DataTable DataTablesRequest pageable, @PathVariable Long prg)
            throws IllegalAccessException {
        return classService.findAllProductPrg(pageable, prg);
    }

    @RequestMapping(value = "editBudget", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void editBudget(Budgets budget) {
        budgetInterface.editBudgets(budget);
    }

    @RequestMapping(value = {"assignProducts"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> assignRoles(@RequestBody ProductAssignBean productAssignBean, HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        ProductGroupDef productGroupDef = productGroupRepo.findOne(productAssignBean.getPrgCode());
        String assignedRoles = "";
        for (Long prId : productAssignBean.getProducts()) {
            ProductsDef productsDef = productsRepo.findOne(prId);
            productsDef.setProGroup(productGroupDef);
        }

        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = {"deleteProductGroup/{prodId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductGroup(@PathVariable Long prodId) {
        classService.deleteProductGroup(prodId);
    }

    @RequestMapping(value = {"addProducts"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProductG(@RequestParam Long product, @RequestParam Long group) {
        classService.addPrgProducts(product, group);
    }

    @RequestMapping(value = {"remProducts"}, method = {
            RequestMethod.POST})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remProductGroup(@RequestParam Long product, @RequestParam Long group) {
        classService.remPrgProducts(product, group);
    }

    @RequestMapping(value = "deleteBudget/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBudget(@PathVariable Long id) {
        budgetInterface.delBudgets(id);
    }

    @RequestMapping(value = {"getBudgets"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<Budgets> getBudgets(@DataTable DataTablesRequest pageable, @RequestParam(required = false) Long product)
            throws IllegalAccessException {
        return budgetInterface.findBudgets(pageable, product);
    }

    @RequestMapping(value = {"searchBud/{sbr}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<Budgets> searchBudgets(@DataTable DataTablesRequest pageable, String sbr)
            throws IllegalAccessException {
        return budgetInterface.searchBudgets(pageable, sbr);
    }

    @RequestMapping(value = {"getEdits/{id}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Budgets getBudgets(@PathVariable Long id) {
        return budgetInterface.editBudget(id);
    }

    @RequestMapping(value = {"getBudgetsLife/{product}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<Budgets> getBudgetsLife(@DataTable DataTablesRequest pageable, @PathVariable Long product)
            throws IllegalAccessException {
        return budgetInterface.findBudgetsLife(pageable, product);
    }

    @RequestMapping(value = "shtprdrates", method = RequestMethod.GET)
    public String shtprdrates(Model model, HttpServletRequest request) {
        String message = "Accessed Short Period Rates Screen";
        String resource = "Short Period Rates";
        auditTrailLogger.log(message, request, resource);
        return "shtprdrates";
    }

    @RequestMapping(value = "syssequences", method = RequestMethod.GET)
    public String sequencesHome(Model model, HttpServletRequest request) {
        String message = "Accessed System Sequence Screen";
        String resource = "System Sequences";
        auditTrailLogger.log(message, request, resource);
        return "syssequences";
    }

    @RequestMapping(value = "countries", method = RequestMethod.GET)
    public String countryHome(Model model, HttpServletRequest request) {
        String message = "Accessed Countries Screen";
        String resource = "Countries";
        auditTrailLogger.log(message, request, resource);
        return "countries";
    }

    @RequestMapping(value = "businesssources", method = RequestMethod.GET)
    public String businessSourcesHome(Model model, HttpServletRequest request) {
        String message = "Accessed Business Sources Screen";
        String resource = "Business Sources";
        auditTrailLogger.log(message, request, resource);
        return "businesssources";
    }


    @RequestMapping(value = "paymentmodes", method = RequestMethod.GET)
    public String paymentModesHome(Model model, HttpServletRequest request) {
        String message = "Accessed Payment Modes Screen";
        String resource = "Payment Modes";
        auditTrailLogger.log(message, request, resource);
        return "paymodesList";
    }

    @RequestMapping(value = "parameterlist", method = RequestMethod.GET)
    public String parametersHome(Model model, HttpServletRequest request) {
        String message = "Accessed Parameters Screen Screen";
        String resource = "Parameters";
        auditTrailLogger.log(message, request, resource);
        return "paramlist";
    }

    @RequestMapping(value = "mobprefixlist", method = RequestMethod.GET)
    public String mobPrefixes(Model model, HttpServletRequest request) {
        String message = "Accessed Mobile Prefixes Screen";
        String resource = "Mobile Prefixes";
        auditTrailLogger.log(message, request, resource);
        return "mobprefix";
    }

    @RequestMapping(value = "clienttypeshome", method = RequestMethod.GET)
    public String clientTypes(Model model, HttpServletRequest request) {
        String message = "Accessed Client Types Screen";
        String resource = "Client Types";
        auditTrailLogger.log(message, request, resource);

        return "clienttypes";
    }

    @RequestMapping(value = "relationtypeshome", method = RequestMethod.GET)
    public String relationTypes(Model model, HttpServletRequest request) {
        String message = "Accessed Relation Types Screen";
        String resource = "Relation Types";
        auditTrailLogger.log(message, request, resource);
        return "relationtypes";
    }

    @RequestMapping(value = "segments", method = RequestMethod.GET)
    public String segments(Model model, HttpServletRequest request) {
        String message = "Accessed Segments Screen";
        String resource = "Segments";
        auditTrailLogger.log(message, request, resource);
        return "segments";
    }


    @RequestMapping(value = "segments", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateSegment(@Valid @ModelAttribute Segments segment) throws BadRequestException, IllegalAccessException {
        if (segment.getSegId() != null) {
            // Update existing segment
            Segments existingSegment = setupsService.findSegmentById(segment.getSegId());
            existingSegment.setSegName(segment.getSegName());
            existingSegment.setSegDescription(segment.getSegDescription());
            existingSegment.setSegSegment(segment.getSegSegment());
            existingSegment.setSapSegment(segment.getSapSegment());
            existingSegment.setSegCode(segment.getSegCode());
            existingSegment.setSegCompCode(segment.getSegCompCode());

            // Add these lines for main bank segment
            existingSegment.setSegMainBankSegmentId(segment.getSegMainBankSegmentId());
            existingSegment.setSegMainBankSegment(segment.getSegMainBankSegment());

            setupsService.saveSegment(existingSegment);
        } else {
            // Create new segment
            setupsService.saveSegment(segment);
        }
    }

    @RequestMapping(value = "segmentsList", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<Segments> getSegments(@DataTable DataTablesRequest pageable) throws IllegalAccessException {
        return setupsService.findAllSegments(pageable);
    }

    @RequestMapping(value = "mainBankSegments", method = RequestMethod.GET)
    public String mainBankSegments(Model model, HttpServletRequest request) {
        String message = "Accessed Main Bank Segments Screen";
        String resource = "Main Bank Segments";
        auditTrailLogger.log(message, request, resource);
        return "mainBankSegments";
    }

    @RequestMapping(value = "mainBankSegments", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateMainBankSegment(@Valid @ModelAttribute MainBankSegments mainBankSegment) throws BadRequestException, IllegalAccessException {
        if (mainBankSegment.getMainBankSegId() != null) {
            // Update existing main bank segment
            MainBankSegments existingMainBankSegment = setupsService.findMainBankSegmentById(mainBankSegment.getMainBankSegId());
            existingMainBankSegment.setMainBankSegName(mainBankSegment.getMainBankSegName());
            setupsService.saveMainBankSegment(existingMainBankSegment);
        } else {
            // Create new main bank segment
            setupsService.saveMainBankSegment(mainBankSegment);
        }
    }

    @RequestMapping(value = "mainBankSegmentsList", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<MainBankSegments> getMainBankSegments(@DataTable DataTablesRequest pageable) throws IllegalAccessException {
        return setupsService.findAllMainBankSegments(pageable);
    }

    @RequestMapping(value = "selMainBankSegments", method = RequestMethod.GET)
    @ResponseBody
    public Page<MainBankSegments> selMainBankSegments(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.findMainBankSegments(term, pageable);
    }

    @RequestMapping(value = "occupations", method = RequestMethod.GET)
    public String sectors(Model model, HttpServletRequest request) {
        String message = "Accessed Occupations Screen";
        String resource = "Occupations";
        auditTrailLogger.log(message, request, resource);
        return "occupations";
    }

    @RequestMapping(value = "budgetsdef", method = RequestMethod.GET)
    public String budgets(Model model, HttpServletRequest request) {
        String message = "Accessed Budgets Screen";
        String resource = "Budgets";
        auditTrailLogger.log(message, request, resource);
        return "budgets";
    }

    @RequestMapping(value = "mapping", method = RequestMethod.GET)
    public String mapping(Model model, HttpServletRequest request) {
        String message = "Accessed Transaction Mapping Screen";
        String resource = "Transaction Mapping";
        auditTrailLogger.log(message, request, resource);

        return "transmapping";
    }

    @RequestMapping(value = "intparties", method = RequestMethod.GET)
    public String intparties(Model model, HttpServletRequest request) {
        String message = "Accessed Interested Parties Screen";
        String resource = "Interested Parties";
        auditTrailLogger.log(message, request, resource);
        return "intparties";
    }

    @RequestMapping(value = "questionsdef", method = RequestMethod.GET)
    public String questions(Model model, HttpServletRequest request) {
        String message = "Accessed Questions Screen";
        String resource = "Questions";
        auditTrailLogger.log(message, request, resource);
        return "questions";
    }

    @RequestMapping(value = "testquestion", method = RequestMethod.GET)
    public String testquiz(Model model, HttpServletRequest request) {
        String message = "Accessed Test Questions Screen";
        String resource = "Test Questions";
        auditTrailLogger.log(message, request, resource);
        return "testquiz";
    }

    @RequestMapping(value = {"paramsList"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ParametersDef> getParameterList(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return paramService.findAllParameters(pageable);
    }


    @RequestMapping(value = {"createParameter"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createParameter(ParametersDef parameter) throws IllegalAccessException, BadRequestException {
        paramService.createParameter(parameter);
    }

    @RequestMapping(value = {"createExchangeRate"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createExchangeRate(CurrencyExchangeRatesDTO exchangeRates) throws IllegalAccessException, BadRequestException {
        System.out.println(exchangeRates.getExchangeDate());
        setupsService.createCurExchangeRate(exchangeRates);
    }

    @RequestMapping(value = {"currencies"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<Currencies> getCurrencies(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllCurrencies(pageable);
    }

    @RequestMapping(value = {"currencyRates/{curCode}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<CurrencyExchangeRatesDTO> getCurrencyRates(@PathVariable Long curCode, @DataTable DataTablesRequest pageable) throws IllegalAccessException {
        return setupsService.findCurrencyExchangeRates(pageable, curCode);
    }

    @RequestMapping(value = {"SourceGroups"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BusinessSourceGroups> getSourceGroups(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllBusSrcGroups(pageable);
    }

    @RequestMapping(value = {"createSourceGroup"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createSourceGroup(BusinessSourceGroups businessSourceGroups) throws IllegalAccessException, BadRequestException {
        setupsService.defineBusSrcGroup(businessSourceGroups);
    }

    @RequestMapping(value = {"deleteSourceGroup/{srcGroupId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSourceGroup(@PathVariable Long srcGroupId) {
        setupsService.deleteBusSrcGroup(srcGroupId);
    }

    @RequestMapping(value = {"BusinessSources/{srcGroupId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<BusinessSources> getBusinessSources(@DataTable DataTablesRequest pageable, @PathVariable Long srcGroupId)
            throws IllegalAccessException {
        return setupsService.findAllBusinessSources(srcGroupId, pageable);
    }

    @RequestMapping(value = {"createBusinessSource"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createBusinessSource(BusinessSources businessSources) throws IllegalAccessException, BadRequestException {
        setupsService.defineBusSource(businessSources);
    }

    @RequestMapping(value = {"deleteBusinessSource/{srcId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBusinessSource(@PathVariable Long srcId) {
        setupsService.deleteBusSrc(srcId);
    }


    @RequestMapping(value = {"sequences"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<SystemSequence> getSequences(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllSequences(pageable);
    }

    @RequestMapping(value = {"createSequence"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOrUpdateSequence(SystemSequence sequence) throws IllegalAccessException, BadRequestException {
        setupsService.defineSequence(sequence);
    }

    @RequestMapping(value = {"deleteSequence/{seqCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSequence(@PathVariable Long seqCode) {
        setupsService.deleteSequence(seqCode);
    }

    @RequestMapping(value = {"createCurrency"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOrUpdateCurrency(Currencies currency) throws IllegalAccessException {
        setupsService.defineCurrency(currency);
    }

    @RequestMapping(value = {"deleteCurrency/{currCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrency(@PathVariable Long currCode) {
        setupsService.deleteCurrency(currCode);
    }

    @RequestMapping(value = {"allCountries"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<Country> getCountries(@DataTable DataTablesRequest pageable) throws IllegalAccessException {
        return setupsService.findAllCountries(pageable);
    }

    @RequestMapping(value = {"createCountry"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOrUpdateCountry(Country country) throws IllegalAccessException {
        setupsService.defineCountry(country);
    }

    @RequestMapping(value = {"deleteCountry/{couCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCountry(@PathVariable Long couCode) {
        setupsService.deleteCountry(couCode);
    }

    @RequestMapping(value = {"allCounties/{couCode}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<County> getCounties(@DataTable DataTablesRequest pageable, @PathVariable Long couCode)
            throws IllegalAccessException {
        return setupsService.findCountiesByCountry(couCode, pageable);
    }

    @RequestMapping(value = {"createCounty"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOrUpdateCounty(County county) throws IllegalAccessException {
        setupsService.defineCounty(county);
    }

    @RequestMapping(value = {"deleteCounty/{countyCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCounty(@PathVariable Long countyCode) {
        setupsService.deleteCounty(countyCode);
    }

    @RequestMapping(value = {"allTowns/{countyCode}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<Town> getTowns(@DataTable DataTablesRequest pageable, @PathVariable Long countyCode)
            throws IllegalAccessException {
        return setupsService.findTownsByCounty(countyCode, pageable);
    }

    @RequestMapping(value = {"createTown"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOrUpdateTown(Town town) throws IllegalAccessException {
        setupsService.defineTown(town);
    }

    @RequestMapping(value = {"deleteTown/{townCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTown(@PathVariable Long townCode) {
        setupsService.deleteTown(townCode);
    }

    @RequestMapping(value = {"allpaymentModes"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<PaymentModes> getPayemtModes(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllPaymentModes(pageable);
    }

    @RequestMapping(value = {"createPaymentModes"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOrUpdatePaymentModes(PaymentModes mode) throws BadRequestException {
        setupsService.definePaymentMode(mode);
    }

    @RequestMapping(value = {"deletePayMode/{pmId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePaymentMode(@PathVariable Long pmId) {
        setupsService.deletePaymentMode(pmId);
    }

    @RequestMapping(value = {"acctypes"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<AccountTypes> getAccountTypes(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllAccountTypes(pageable);
    }

    @RequestMapping(value = {"subacctypes/{acctypeId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<SubAccountTypes> getSubAccountTypes(@DataTable DataTablesRequest pageable, @PathVariable Long acctypeId)
            throws IllegalAccessException {
        return setupsService.findAllSubAccountTypes(pageable, acctypeId);
    }

    @RequestMapping(value = {"createsubacctypes"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createSubacctypes(SubAccountTypes acctypes) throws BadRequestException {
        setupsService.defineSubAccountType(acctypes);
    }

    @RequestMapping(value = {"deletesubacctypes/{accId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletesubacctypes(@PathVariable Long accId) {
        setupsService.deleteSubAccountType(accId);
    }


    @RequestMapping(value = "accttypes", method = RequestMethod.GET)
    public String accountTypesHome(Model model) {
        return "accounttypes";
    }

    @RequestMapping(value = {"createAcctTypes"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOrUpdateAcctTypes(AccountTypes acctypes) throws BadRequestException {
        setupsService.defineAccountType(acctypes);
    }

    @RequestMapping(value = {"deleteAcctype/{accId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccType(@PathVariable Long accId) {
        setupsService.deleteAccountType(accId);
    }

    @RequestMapping(value = {"rejectAccount/{accId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectAccount(
            @PathVariable Long accId,
            @RequestParam Long reasonId,
            @RequestParam(required = false) String reason) throws BadRequestException {
        setupsService.rejectAccount(accId, reasonId, reason);
    }

    @RequestMapping(value = {"approveAccount/{accId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveAccount(@PathVariable Long accId) throws BadRequestException {
        MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.acctId.eq(accId).and(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("IM")));
        if (makerChecker != null) {
            makerCheckerService.approveTask(makerChecker.getId());
        }
    }

    @RequestMapping(value = {"deactivateAccount/{accId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateAccount(@PathVariable Long accId) throws BadRequestException {
        setupsService.deactivateAccount(accId);
    }

    @RequestMapping(value = "accts", method = RequestMethod.GET)
    public String accountsHome(Model model, HttpServletRequest request) {
        String message = "Accessed Accounts Home Screen";
        String resource = "Accounts";
        auditTrailLogger.log(message, request, resource);
        return "accounts";
    }

    @RequestMapping(value = {"selAcctTypes"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<AccountTypesDTO> selAccountTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.findAccountTypesforSelect(term, pageable);
    }

    @RequestMapping(value = {"subComAcctTypes"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<AccountTypesDTO> findsubComAccountTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.findsubComAccountTypes(term, pageable);
    }

    @RequestMapping(value = {"selSubclasses"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<SubclassDTO> selSubclasses(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.selectSubclasses(term, pageable);
    }

    @RequestMapping(value = {"selAllRevItems"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<RevenueItemsDTO> selAllRevItems(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.selectAllRevenueItems(term, pageable);
    }

    @RequestMapping(value = {"selParentAccts"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<AccountsDTO> selParentAccts(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
        return setupsService.findParentAccountsforSelect(term, pageable);
    }

    @RequestMapping(value = {"selBrokers"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<AccountsDTO> selBrokers(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
        return setupsService.findBrokersforSelect(term, pageable);
    }

    @RequestMapping(value = {"selReinsurers"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<AccountsDTO> selReinsurers(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
        return setupsService.findReinsuranceforSelect(term, pageable);
    }


    @RequestMapping(value = {"allaccounts/{accId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<AccountsDTO> getAccounts(@DataTable DataTablesRequest pageable, @PathVariable Long accId) {
        return setupsService.findAllAccounts(pageable, accId);
    }

    @RequestMapping(value = "acctsform", method = RequestMethod.GET)
    public String accountsform(Model model) {
        model.addAttribute("accId", -2000);
        return "acctsform";
    }

    @RequestMapping(value = "/accountImage/{acctId}")
    public void getImage(HttpServletResponse response, @PathVariable Long acctId)
            throws IOException, ServletException {
        AccountsDTO account = setupsService.getAccountDetails(acctId);
        if (account != null && account.getLogo() != null) {
            File file = new File(account.getLogo());
            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
            response.getOutputStream().write(Files.readAllBytes(file.toPath()));
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = {"createAccount"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    @Transactional(rollbackFor = BadRequestException.class)
    public Long saveOrUpdateAccount(AccountsDTO account) throws BadRequestException {

        System.out.println("Received commissionEarning: " + account.getCommissionEarning());

        if ((account.getFile() != null) && (!account.getFile().isEmpty())) {
            if (account.getFile().getSize() != 0) {
                try {
                    UploadDocumentForm uploadDocumentForm = new UploadDocumentForm();
                    uploadDocumentForm.setFile(account.getFile());
                    uploadDocumentForm.setEntityId(100L);
                    uploadDocumentForm.setEntityType("accounts_logo");
                    final String loc = locationUtils.saveFile(uploadDocumentForm);
                    account.setLogo(loc);
                } catch (IOException ex) {
                    throw new BadRequestException(ex.getMessage());
                }
            } else {
                account.setLogo(setupsService.getAccountDetails(account.getAcctId()).getLogo());
            }
        } else {
            if (account.getAcctId() != null) {
                account.setLogo(setupsService.getAccountDetails(account.getAcctId()).getLogo());
            }
        }
        return setupsService.defineAccount(account, false);
    }

    @RequestMapping(value = "uploadSubAgent", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadExcel(@RequestParam MultipartFile file, HttpServletRequest request) throws BadRequestException {
        auditTrailLogger.log("Uploaded sub-Agent / Marketer file: " + file, request, "upload sub-Agent / Marketer file");
        return subAgentMarketerImportService.importSubAgentMarketer(file);
    }

    @RequestMapping(value = {"selBankBranches"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<BankBranchDTO> selBankBranches(@RequestParam(value = "term", required = false) String term, @RequestParam("bankCode") Long bankCode, Pageable pageable) {
        return setupsService.findBankBranchesForSel(term, pageable, bankCode);
    }

    @RequestMapping(value = {"selBanks"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<Banks> selBanks(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.findBanksForSelect(term, pageable);
    }

    @RequestMapping(value = "/editAcctForm", method = RequestMethod.POST)
    public String editRentalForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model) throws BadRequestException {
        model.addAttribute("accId", helperForm.getId());
        formLockManager.openForm("INSURER/AGENT",String.valueOf(helperForm.getId()));
        return "acctsform";
    }

    @RequestMapping(value = "/viewAcctForm", method = RequestMethod.POST)
    public String viewAcctForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model) throws BadRequestException {
        model.addAttribute("accIdView", helperForm.getId());
        formLockManager.openForm("INSURER/AGENT",String.valueOf(helperForm.getId()));
        return "viewAccountForm";
    }

    @RequestMapping(value = {"accounts/{acctId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public AccountsDTO getAccountDetails(@PathVariable Long acctId) {
        return setupsService.getAccountDetails(acctId);
    }

    @RequestMapping(value = {"deleteAccount/{acctId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long acctId) {
        setupsService.deleteAccount(acctId);
    }

    @RequestMapping(value = {"branches"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<OrgBranch> orgBranches(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.findBranchForSelect(term, pageable);
    }

    @RequestMapping(value = {"year"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<Year> year(@RequestParam(value = "term", required = false) String term, Pageable pageable) throws BadRequestException {
        return setupsService.findYearForSelect(term, pageable);
    }

    /*
    @RequestMapping(value = { "getBranchId/{branch}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public OrgBranch branc(@PathVariable String branch) throws BadRequestException{
        return setupsService.findThisBranch(branch);
    }
    @RequestMapping(value = { "getUserId/{user}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public AccountDef getUs(@PathVariable String user) throws BadRequestException{
        return setupsService.findThisAgent(user);
    }
    @RequestMapping(value = { "getProductId/{product}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public ProductGroupDef getPr(@PathVariable String product) throws BadRequestException{
        return setupsService.findThisProduct(product);
    }
     */
    @RequestMapping(value = {"mobprefixes/{couCode}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<MobilePrefixDef> getMobPrefixes(@DataTable DataTablesRequest pageable, @PathVariable Long couCode)
            throws IllegalAccessException {
        return setupsService.findAllPrefixes(couCode, pageable);
    }

    @RequestMapping(value = {"contracts"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ContractDTO> getContracts(@DataTable DataTablesRequest pageable) {
        return setupsService.findAllContracts(pageable);
    }

    @RequestMapping(value = {"createPrefix"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createPrefix(MobilePrefixDef prefix) throws IllegalAccessException, BadRequestException {
        setupsService.definePrefix(prefix);
    }

    @RequestMapping(value = {"deletePrefix/{prefId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePrefix(@PathVariable Long prefId) {
        setupsService.deletePrefix(prefId);
    }

    @RequestMapping(value = {"clienttypeslist"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ClientTypes> getClientTypes(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllClientTypes(pageable);
    }

    @RequestMapping(value = {"relationtypeslist"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<RelationshipTypes> getRelationTypes(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllRelationshipTypes(pageable);
    }

    @RequestMapping(value = {"creatClientType"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void creatClientType(ClientTypes clntType) throws IllegalAccessException, BadRequestException {
        setupsService.defineClientType(clntType);
    }

    @RequestMapping(value = {"createRelationType"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createRelationType(RelationshipTypes relationshipType) throws IllegalAccessException, BadRequestException {
        setupsService.defineRelationshipType(relationshipType);
    }

    @RequestMapping(value = {"deleteClientType/{typeId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClientType(@PathVariable Long typeId) {
        setupsService.deleteClientType(typeId);
    }

    @RequestMapping(value = {"deleteRelationType/{typeId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRelationType(@PathVariable Long typeId) {
        setupsService.deleteRelationshipType(typeId);
    }

    @RequestMapping(value = "clienttitleshome", method = RequestMethod.GET)
    public String clientTitles(Model model, HttpServletRequest request) {
        String message = "Accessed Client Titles Screen";
        String resource = "Client Titles";
        auditTrailLogger.log(message, request, resource);
        return "clienttitles";
    }

    @RequestMapping(value = {"clienttitleslist"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ClientTitle> getClientTitles(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllClientTitles(pageable);
    }

    @RequestMapping(value = {"creatClientTitle"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void creatClientTitle(ClientTitle clientTitle) throws IllegalAccessException, BadRequestException {
        setupsService.defineClientTitle(clientTitle);
    }

    @RequestMapping(value = {"deleteClientTitle/{titleId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClientTitle(@PathVariable Long titleId) {
        setupsService.deleteClientTitle(titleId);
    }

    @RequestMapping(value = {"postalcodes/{townCode}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<PostalCodesDef> getTownPostalCodes(@DataTable DataTablesRequest pageable, @PathVariable Long townCode)
            throws IllegalAccessException {
        return setupsService.findPostalCodesByTown(townCode, pageable);
    }

    @RequestMapping(value = {"createPostalCode"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createPostalCode(PostalCodesDef postalCode) throws IllegalAccessException, BadRequestException {
        setupsService.definePostalCode(postalCode);
    }

    @RequestMapping(value = {"deletePostalCode/{pCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostalCode(@PathVariable Long pCode) {
        setupsService.deletePostalCode(pCode);
    }

    @RequestMapping(value = {"selSectors"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<SectorDef> selSectors(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.findSectorForSelect(term, pageable);
    }


    @RequestMapping(value = {"selOccupations"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<Occupation> selectCounties(@RequestParam(value = "term", required = false) String term, @RequestParam("sectCode") Long sectCode, Pageable pageable)
            throws IllegalAccessException, BadRequestException {
        return this.setupsService.findSectorOccupations(term, pageable, sectCode);
    }


    @RequestMapping(value = {"occupations/{sectCode}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<Occupation> getSectorOccupations(@DataTable DataTablesRequest pageable, @PathVariable Long sectCode)
            throws IllegalAccessException {
        return setupsService.findOccupationBySector(sectCode, pageable);
    }

    @RequestMapping(value = {"createSector"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createSector(SectorDef sector) throws IllegalAccessException, BadRequestException {
        setupsService.defineSector(sector);
    }


    @RequestMapping(value = {"createOccupation"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createOccupation(Occupation occ) throws IllegalAccessException, BadRequestException {
        setupsService.defineOccupation(occ);
    }


    @RequestMapping(value = {"deleteSector/{secCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSector(@PathVariable Long secCode) {
        setupsService.deleteSector(secCode);
    }

    @RequestMapping(value = {"deleteOccupation/{occCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOccupation(@PathVariable Long occCode) {
        setupsService.deleteOccupation(occCode);
    }


    @RequestMapping(value = {"shortperiods"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ShortPeriodRates> getShortperiods(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findShortPeriodRates(pageable);
    }

    @RequestMapping(value = {"createShortPeriod"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createShortPeriod(ShortPeriodRates shortPeriod) throws IllegalAccessException, BadRequestException {
        setupsService.defineShortPeriodRates(shortPeriod);
    }

    @RequestMapping(value = {"deleteShortPeriod/{shortPeriodCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShortPeriod(@PathVariable Long shortPeriodCode) {
        setupsService.deleteShortPrdRates(shortPeriodCode);
    }

    @RequestMapping(value = {"selSubclassSections"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<SubclassSections> selSubClassSections(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.findAllSubClassSections(term, pageable);
    }


    @RequestMapping(value = {"secshortperiods/{ssCode}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<SecShortPeriodRates> getSectionShortPeriods(@DataTable DataTablesRequest pageable, @PathVariable Long ssCode)
            throws IllegalAccessException {
        return setupsService.findSecShortPeriodRates(ssCode, pageable);
    }


    @RequestMapping(value = {"createSecShortPeriod"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createSecShortPeriod(SecShortPeriodRates shortPeriod) throws IllegalAccessException, BadRequestException {
        setupsService.defineSecShortPeriodRts(shortPeriod);
    }

    @RequestMapping(value = {"deleteSecShortPeriod/{shortPeriodCode}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSecShortPeriod(@PathVariable Long shortPeriodCode) {
        setupsService.deleteSecShortPrdRts(shortPeriodCode);
    }


    @RequestMapping(value = "endorseremarks", method = RequestMethod.GET)
    public String endorseremarks(Model model, HttpServletRequest request) {
        String message = "Accessed Client Titles Home Screen";
        String resource = "Client Titles";
        auditTrailLogger.log(message, request, resource);
        return "endorseremarks";
    }


    @RequestMapping(value = {"createEndRemarks"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createEndRemarks(EndorsementRemarks remarks) throws IllegalAccessException, BadRequestException {
        setupsService.defineEndorseRemarks(remarks);
    }

    @RequestMapping(value = {"deleteEndRemarks/{remarkId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEndRemarks(@PathVariable Long remarkId) {
        setupsService.deleteEndorseRemarks(remarkId);
    }


    @RequestMapping(value = {"endRemarks"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<EndorsementRemarks> getEndRemarks(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllEndorseRemarks(pageable);
    }

    @RequestMapping(value = "clmactivity", method = RequestMethod.GET)
    public String claimActivityHome(Model model, HttpServletRequest request) {
        String message = "Accessed Claim Activity Home Screen";
        String resource = "Claim Activity";
        auditTrailLogger.log(message, request, resource);
        return "clmactivities";
    }

    @RequestMapping(value = {"clmActivities"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ClmCausations> getClmActivities(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllActivities(pageable);
    }

    @RequestMapping(value = {"createClmActivity"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createClmActivity(ClmCausations clmStatus) throws IllegalAccessException, BadRequestException {
        setupsService.defineActivity(clmStatus);
    }

    @RequestMapping(value = {"deleteClmActivity/{actId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClmActivity(@PathVariable Long actId) {
        setupsService.deleteActivity(actId);
    }

    @RequestMapping(value = {"mappingList"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<TransactionMapping> getMappingList(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findTransMapping(pageable);
    }

    @RequestMapping(value = {"createTransMapping"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createTransMapping(TransactionMapping mapping) throws IllegalAccessException, BadRequestException {
        setupsService.defineTransMapping(mapping);
    }

    @RequestMapping(value = {"deleteTransMapping/{mappingId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransMapping(@PathVariable Long mappingId) {
        setupsService.deleteMapping(mappingId);
    }

    @RequestMapping(value = {"validatePin"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public String validatePin(@RequestParam(value = "pinNo", required = false) String pinNo) throws BadRequestException {
        validator.validatePinNo(pinNo);
        return "Y";
    }

    @RequestMapping(value = {"interestedparties"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<InterestedParties> getInterestedparties(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findInterestedParties(pageable);
    }

    @RequestMapping(value = {"createIntParties"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createIntParties(InterestedPartiesDto interestedParties) throws IllegalAccessException, BadRequestException {
        setupsService.defineInterestedParties(interestedParties);
    }

    @RequestMapping(value = {"deleteIntParties/{intId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIntParties(@PathVariable Long intId) {
        setupsService.deletInterestedParties(intId);
    }

    @RequestMapping(value = {"accountDocs/{accountId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<AccountsDocs> getClientDocs(@DataTable DataTablesRequest pageable, @PathVariable Long accountId)
            throws IllegalAccessException {
        return setupsService.findAccountDocs(pageable, accountId);
    }

    @RequestMapping(value = {"uploadAccountDocs"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<String> uploadAccountDocs(UploadBean uploadBean) throws BadRequestException {
        try {
            uploadService.sybrinCreateCase(uploadBean, "Account");
            return ResponseEntity.status(HttpStatus.CREATED).body("File Uploaded successful");
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during upload");
        }
    }

    @RequestMapping(value = {"acctreqdocs"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<RequiredDocs> getUnassignedDocs(@RequestParam(value = "acctCode", required = false) Long acctCode, @RequestParam(value = "docName", required = false) String docName)
            throws IllegalAccessException {
        return setupsService.findUnassignedAcctDocs(acctCode, docName);
    }

    @RequestMapping(value = {"createAcctDocs"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> createAcctDocs(@RequestBody RequiredDocBean requiredDocBean) throws IllegalAccessException, IOException, BadRequestException {
        setupsService.createAcctRequiredDocs(requiredDocBean);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = {"selRelationTypes"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<RelationshipTypes> selRelationTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException, BadRequestException {
        return setupsService.findSelRelationTypes(term, pageable);
    }

    @RequestMapping(value = "/acctDocument/{adId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> thumbnail(@PathVariable Long adId) throws BadRequestException {
        AccountsDocs accountsDoc = accountDocsRepo.findOne(adId);
        AccountDef accountDef = accountsDoc.getAccountDef();
        Long accId = accountDef.getAcctId();
        byte[] content = uploadService.sybrinDocumentDetails("Account", adId, accId);
        if (content.length > 0) {
            String contentType = uploadService.getAcctDocumentType(adId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(content.length);
            return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = {"deleteAcctDoc/{adId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAcctDoc(@PathVariable Long adId) throws BadRequestException {
        uploadService.deleteAcctDocument(adId);
    }

    @RequestMapping(value = {"createQuestions"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createQuestions(LifeQuestions lifeQuestions) throws IllegalAccessException, BadRequestException {
        setupsService.defineQuestions(lifeQuestions);
    }

    @RequestMapping(value = {"createQuestionsChoices"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createQuestionsChoices(LifeQuestionsChoices lifeQuestionsChoices) throws IllegalAccessException, BadRequestException {
        setupsService.defineQuestionsChoices(lifeQuestionsChoices);
    }

    @RequestMapping(value = {"questions"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<LifeQuestions> getQuestions(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findQuestions(pageable);
    }

    @RequestMapping(value = {"questions/{questionId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<LifeQuestionsChoices> getQuestionsChoices(@DataTable DataTablesRequest pageable, @PathVariable Long questionId)
            throws IllegalAccessException {
        return setupsService.findQuestionsChoices(pageable, questionId);
    }


    @RequestMapping(value = {"allquestions"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<SingleQuizModel> getAllQuestions()
            throws IllegalAccessException {
        return setupsService.findAllQuestions();
    }

    @RequestMapping(value = {"prdrptgroups"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<ProductReportGroup> selRptGrps(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return setupsService.selRptGrps(term, pageable);
    }

    @RequestMapping(value = {"createRptGroup"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createRptGroup(ProductReportGroup productReportGroup) throws IllegalAccessException, BadRequestException {
        setupsService.createRptGroup(productReportGroup);
    }

    @RequestMapping(value = "rejectedreasons", method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public String rejectedReasons(Model model, HttpServletRequest request) {
        String message = "Accessed Rejected Reasons Setup Screen";
        String resource = "REJECTED REASONS";
        auditTrailLogger.log(message, request, resource);
        return "rejectedReasons";
    }

    @RequestMapping(value = {"rjctReasons"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<RejectedReasons> getRjctReasons(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return setupsService.findAllRejectedReasons(pageable);
    }

    @RequestMapping(value = {"createRejectedReasons"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseStatus(HttpStatus.CREATED)
    public void createRejectedReasons(@Valid RejectedReasons rejectedStatus) throws IllegalAccessException, BadRequestException {
        if (rejectedStatus.getReasonDesc() == null || rejectedStatus.getReasonDesc().trim().isEmpty()) {
            throw new BadRequestException("Reason description cannot be empty");
        }
        setupsService.defineRejectedReasons(rejectedStatus);
    }

    @RequestMapping(value = {"deleteRejectedReasons/{rjctId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRejectedReasons(@PathVariable Long rjctId) {
        setupsService.deleteRejectedReasons(rjctId);
    }


    @RequestMapping(value = "publicholidays", method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    public String publicHolidays(Model model, HttpServletRequest request) {
        String message = "Accessed Public Holidays Setup Screen";
        String resource = "PUBLIC HOLIDAYS";
        auditTrailLogger.log(message, request, resource);
        return "publicHolidays";
    }


    @RequestMapping(value = {"createPublicHolidays"}, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createPublicHolidays(@Valid PublicHolidaysDTO dto) throws IllegalAccessException, BadRequestException {
        if (dto.getHolidayName() == null || dto.getHolidayName().trim().isEmpty()) {
            throw new BadRequestException("Holiday name cannot be empty");
        }
        if (dto.getHolidayDate() == null) {
            throw new BadRequestException("Holiday date cannot be empty");
        }

        PublicHolidays holiday = new PublicHolidays();
        holiday.setHolidayName(dto.getHolidayName());
        holiday.setIsRecurring(dto.getIsRecurring());

        // Parse the date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            holiday.setHolidayDate(sdf.parse(dto.getHolidayDate()));
        } catch (ParseException e) {
            throw new BadRequestException("Invalid date format");
        }

        setupsService.createPublicHoliday(holiday);
        // Invalidate cache for the year of this holiday
        LocalDate holidayDate = LocalDate.parse(dto.getHolidayDate());
        HolidayUtils.invalidateCacheForYear(holidayDate.getYear());
    }



    @RequestMapping(value = "/loadPublicHolidays", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<PublicHolidays> loadPublicHolidays(@DataTable DataTablesRequest request) throws IllegalAccessException {
        return setupsService.findAllPublicHolidays(request);
    }
    @RequestMapping(value = {"deletePublicHoliday/{holidayId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePublicHoliday(@PathVariable Long holidayId) {
        setupsService.deletePublicHoliday(holidayId);
    }


}
