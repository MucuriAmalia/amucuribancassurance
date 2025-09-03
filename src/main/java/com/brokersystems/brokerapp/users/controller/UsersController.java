package com.brokersystems.brokerapp.users.controller;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.setup.model.EscalationLevel;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.repository.EscalationLevelRepository;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import com.brokersystems.brokerapp.setup.service.ClientService;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.users.dto.PermissionDTO;
import com.brokersystems.brokerapp.users.dto.RejectRequest;
import com.brokersystems.brokerapp.users.model.*;
import com.brokersystems.brokerapp.users.repository.RolesRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.workflow.docs.SysWfDocs;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Created by peter on 4/13/2017.
 */
@Controller
@RequestMapping({"/protected/users"})
public class UsersController {

    private static final String TEMP_FOLDER_PATH = System.getProperty("java.io.tmpdir");
    private static final String IMAGES_SYSTEM_PATH = TEMP_FOLDER_PATH + File.separator + "images";
    private static final File IMAGES_SYSTEM_DIR = new File(IMAGES_SYSTEM_PATH);
    private static final String IMAGES_SYSTEM_DIR_ABSOLUTE_PATH = IMAGES_SYSTEM_DIR.getAbsolutePath() + File.separator;
    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepo rolesRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private EscalationLevelRepository escalationLevelRepository;

    @PreAuthorize("hasAnyAuthority('ACCESS_USERS')")
    @RequestMapping(value = "usersHome", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String classHome(Model model, HttpServletRequest request) {
        auditTrailLogger.log("Accessed Users and Roles Screen ", request, "Users and Roles");
        return "users";
    }

    @RequestMapping(value = "roles", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String roles(Model model) {

        return "roles";
    }

    @RequestMapping(value = "permissions", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String permissions(Model model) {

        return "permissions";
    }


    @RequestMapping(value = {"usersList/{userstatus}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<UserDTO> getClasses(@DataTable DataTablesRequest pageable, @PathVariable String userstatus) {
        return userService.findDatatables(pageable, userstatus);
    }

    @RequestMapping(value = {"rolesList"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<RolesDef> rolesList(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return userService.findRoles(pageable);
    }

    @RequestMapping(value = "/usersignature/{id}")
    public void getImage(HttpServletResponse response, @PathVariable Long id) throws IOException, ServletException {
        UserDTO userDTO = userService.findUserSignatureDetails(id);
        if(userDTO.getSignature()!=null) {
            response.setContentType(userDTO.getSignatureContentType());
            response.getOutputStream().write(Files.readAllBytes(Paths.get(userDTO.getSignature())));
            response.getOutputStream().close();
        }
    }

    @PreAuthorize("hasAnyAuthority('CREATE_USER')")
    @RequestMapping(value = {"createUsers"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void createUsers(UserDTO user, HttpServletRequest request) throws IOException, BadRequestException {
        String message = "";
        final boolean newUser = user.getId() == null;
        User currentUser = user.getId() != null ? userRepository.findOne(user.getId()) : null;
        if (newUser) {
            String isMarketer ="No";
            String typeOfMarketer ="None";
            message = "Created a new User with the following attributes, Name: " + user.getName() + " UserName: " + user.getUsername()
                    + " Marketer: " + isMarketer + " Marketer Type: " + typeOfMarketer + " Email: " + user.getEmail() +
                    " Active " + user.getStatus() +  "Ab No: " +user.getAbsaNo();
        } else {
            String nameAudit = currentUser.getName().equalsIgnoreCase(user.getName()) ? "" : "Changed User Name From " + currentUser.getName() + " to " + user.getName();
            String userName = currentUser.getUsername().equalsIgnoreCase(user.getUsername()) ? "" : "Changed User UserName From " + currentUser.getUsername() + " to " + user.getUsername();
            String marketer = "";

            String email = currentUser.getEmail().equalsIgnoreCase(user.getEmail()) ? "" : "Changed User Email From " + (currentUser.getEmail().isEmpty()?"None ": currentUser.getEmail()) + " to " + user.getEmail();
            String currActive = (currentUser.getEnabled()!=null && currentUser.getEnabled().equalsIgnoreCase("1"))?"on":"off";
            String active = currActive.equalsIgnoreCase(user.getStatus()) ? "" : "Changed User Active Flag From " + currActive + " to " + user.getStatus();
            message = "Edited " + currentUser.getUsername() + nameAudit + " " + userName + " " + marketer  + " " + email + " " + active;

        }
        if ((user.getFile() != null) &&
                (!user.getFile().isEmpty())) {
            String uploadFolder = IMAGES_SYSTEM_DIR_ABSOLUTE_PATH;
            byte[] bytes = user.getFile().getBytes();
            String folderName = uploadFolder + "/USERS"+ "/"+ user.getUsername();
            File file = new File(folderName);
            if (!file.exists())
                FileUtils.forceMkdir(file);
            Path path = Paths.get(folderName  +"/"+ user.getFile().getOriginalFilename());
            Files.write(path, bytes);
            user.setSignatureContentType(user.getFile().getContentType());
            user.setSignature(path.toFile().getAbsolutePath());
        }
        else{
            if(user.getId()!=null) {
                UserDTO dto = userService.findUserSignatureDetails(user.getId());
                if (dto != null && dto.getSignature() != null) {
                    user.setSignatureContentType(dto.getSignatureContentType());
                    user.setSignature(dto.getSignature());
                }
            }
        }
        String resource = "Create User";
        userService.saveOrUpdate(user, request);
        auditTrailLogger.log(message, request,resource);
    }


    @RequestMapping(value = {"deleteUser/{userId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSequence(@PathVariable Long userId, HttpServletRequest request) throws BadRequestException {
        User user = userRepository.findOne(userId);
        String message = "De-activated User : " + user.getName() + " Of UserName: " + user.getUsername();
        String resource = "Delete User";
        userService.deleteUser(userId);
        auditTrailLogger.log(message, request, resource);

    }

    @RequestMapping(value = {"modulesList"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<ModulesDef> modulesList(@DataTable DataTablesRequest pageable)
            throws IllegalAccessException {
        return userService.getModules(pageable);
    }

    @RequestMapping(value = { "addPermission" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void selproducts(PermissionsDef permissionsDef)
            throws IllegalAccessException, BadRequestException {
        userService.setPermissionSetups(permissionsDef);
    }
    @RequestMapping(value = {"permissions/{roleId}/{moduleId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<PermissionDTO> getPermissions(@DataTable DataTablesRequest pageable,
                                                          @PathVariable("roleId") Long roleId,
                                                          @PathVariable("moduleId") Long moduleId) {
        return userService.findPermissions(pageable, moduleId,roleId);
    }

    @RequestMapping(value = "allPermissions", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<PermissionDTO> getAllPermissions(@DataTable DataTablesRequest pageable) {
        return userService.findAllPermissions(pageable);
    }


    @RequestMapping(value = {"grantPermission"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void grantPermission(PermissionBean permissionBean) throws IllegalAccessException, IOException, BadRequestException {
        userService.grantPermission(permissionBean);
    }

    @RequestMapping(value = {"addNewModule"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void addNewModule(@RequestParam("module_name") String moduleName,
                             @RequestParam("module_sht_desc") String moduleDesc) throws IllegalAccessException, IOException, BadRequestException {
        ModulesDef modulesDef = new ModulesDef();
        modulesDef.setModuleName(moduleName);
        modulesDef.setShtDesc(moduleDesc);
        userService.addModule(modulesDef);
    }

    @RequestMapping(value = {"addModule"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void addModule(ModulesDef modulesDef) throws IllegalAccessException, IOException, BadRequestException {
        userService.addModule(modulesDef);
    }

    @RequestMapping(value = {"revokePermission"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void revokePermission(PermissionBean permissionBean) throws IllegalAccessException, IOException, BadRequestException {
        userService.revokePermission(permissionBean);
    }

    @RequestMapping(value = {"checkPermissionExists"}, method = {RequestMethod.GET})
    @ResponseBody
    public Long getPermissionsCount(@RequestParam(value = "roleId", required = false) Long roleId, @RequestParam(value = "permId", required = false) Long permId)
            throws IllegalAccessException {
        return userService.checkIfRoleExists(roleId, permId);
    }

    @RequestMapping(value = {"minLimitAmt"}, method = {RequestMethod.GET})
    @ResponseBody
    public BigDecimal getMinLimitAmount(@RequestParam(value = "roleId", required = false) Long roleId, @RequestParam(value = "permId", required = false) Long permId)
            throws IllegalAccessException {
        return userService.getMinLimitAmount(roleId, permId);
    }

    @RequestMapping(value = {"maxLimitAmt"}, method = {RequestMethod.GET})
    @ResponseBody
    public BigDecimal getMaxLimitAmount(@RequestParam(value = "roleId", required = false) Long roleId, @RequestParam(value = "permId", required = false) Long permId)
            throws IllegalAccessException {
        return userService.getMaxLimitAmount(roleId, permId);
    }

    @RequestMapping(value = { "assignedLevel/{userId}" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<EscalationLevel> getAssignedLevel(@PathVariable Long userId) {
        User user = userRepository.findOne(userId);
        System.out.println("User from get request for level assignment: " + userId);
        System.out.println("User from get request for level assignment: " + user);
        if (user == null){
            throw new IllegalArgumentException("User not found");
        }
        if (user.getEscalationLevel() == null){
            return null;
        }
        return ResponseEntity.ok(user.getEscalationLevel());
    }

    @RequestMapping(value = { "getEscalationLevels" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<EscalationLevel>> getAllEscalationLevels() {
        System.out.println("Available escalation levels: " + escalationLevelRepository.findAll());
        return ResponseEntity.ok(escalationLevelRepository.findAll());
    }

    @RequestMapping(value = { "assignHierarchyLevel" }, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> assignHierarchyLevel(@RequestParam Long userId, @RequestParam Long levelId) {
        User user = userRepository.findOne(userId);
        if (user == null){
            throw new IllegalArgumentException("User not found");
        }
        EscalationLevel level = escalationLevelRepository.findOne(levelId);
        if (level == null){
            throw new IllegalArgumentException("Level not found");
        }

        user.setEscalationLevel(level);
        userRepository.save(user);
        System.out.println("Updated user: " + user);
        return ResponseEntity.ok("Hierarchy level assigned successfully");
    }

    @RequestMapping(value = {"createRoles"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void createRoles(RolesDef rolesDef) throws IllegalAccessException, IOException, BadRequestException {
        userService.createRole(rolesDef);
    }

    @RequestMapping(value = {"createPermissions"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void createPermissions(PermissionsDef permissionsDef) throws IllegalAccessException, IOException, BadRequestException {
        userService.createPermission(permissionsDef);
    }

    @RequestMapping(value = {"subaccounts"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<AccountTypesDTO> selectSubaccountTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
        return userService.findSubAccountTypes(term, pageable);
    }

    @RequestMapping(value = {"accountTypes"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<AccountTypesDTO> selectAccountTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
        return userService.findSubAccountTypes(term, pageable);
    }



    @RequestMapping(value = {"deleteRole/{roleId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable Long roleId)  {
        userService.deleteRole(roleId);
    }

    @RequestMapping(value = {"assignedRoles/{userId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<RolesDef> getAssignedRoles(@DataTable DataTablesRequest pageable, @PathVariable Long userId)
            throws IllegalAccessException {
        return userService.findassignedUserRoles(pageable, userId);
    }

    @RequestMapping(value = {"unAssignedRoles/{userId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<RolesDef> getUnAssignedRoles(@DataTable DataTablesRequest pageable, @PathVariable Long userId)
            throws IllegalAccessException {
        return userService.findUnassignedUserRoles(pageable, userId);
    }

    @RequestMapping(value = {"assignRoles"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> assignRoles(@RequestBody UserRolesBean rolesBean, HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        User user = userRepository.findOne(rolesBean.getUserId());
        String assignedRoles="";
        for(Long roleId:rolesBean.getRoles()){
            RolesDef role = rolesRepo.findOne(roleId);
            assignedRoles = assignedRoles+role.getRoleName()+" ,";
        }
        String message = "Assigned User : " + user.getName()+ " Of UserName: " + user.getUsername() + " The following roles: " + assignedRoles;
        String resource = "Assign Roles";
        userService.assignUserRoles(rolesBean);
        auditTrailLogger.log(message, request, resource);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = {"unAssignRoles"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> unAssignRoles(@RequestBody UserRolesBean rolesBean, HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        User user = userRepository.findOne(rolesBean.getUserId());
        String assignedRoles="";
        for(Long roleId:rolesBean.getRoles()){
            RolesDef role = rolesRepo.findOne(roleId);
            assignedRoles = assignedRoles+role.getRoleName()+" ,";
        }
        String message = "Un-Assigned User : " + user.getName()+ " Of UserName: " + user.getUsername() + " The following roles: " + assignedRoles;
        String resource = "Unassign roles";
        userService.unAssignUserRoles(rolesBean);
        auditTrailLogger.log(message, request, resource);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = {"updatePermLimits"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public void updatePermLimits(PermLimitsBean limitsBean) throws IllegalAccessException, IOException, BadRequestException {
        userService.savePermLimits(limitsBean);
    }

    @RequestMapping(value = {"assignUserBranches"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<String> assignUserBranches(@RequestBody CreateUserBranchesDTO userBranchesDTO, HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        String resource = "Assign User Branches";
        userService.createUserBranch(userBranchesDTO);
        auditTrailLogger.log(userBranchesDTO.toString(), request, resource);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = { "getUserAssignedBranches/{userId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<UserBranchesDTO> getUserAssignedBranches(@DataTable DataTablesRequest pageable,@PathVariable Long userId) throws IllegalAccessException {
        return userService.findUserBranches(userId, pageable);
    }

    @RequestMapping(value = { "userbranches" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<BranchDTO> userbranches(@RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "search", required = false) String search ) {
        return userService.findUnassignedBranches(userId,search);
    }

    @RequestMapping(value = {"deleteUserBranch/{userBranchId}"}, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserBranch(@PathVariable Long userBranchId)  {
        userService.deleteUserBranch(userBranchId);
    }

    @RequestMapping(value = "rejectTask", method = RequestMethod.POST)
    public ResponseEntity<?> rejectTask(@RequestBody RejectRequest rejectRequest) throws BadRequestException {
        makerCheckerService.rejectTask(
                rejectRequest.getTaskId(),
                rejectRequest.getReasonId(),
                rejectRequest.getReason());
        return ResponseEntity.ok("Task rejected successfully");
    }
    @RequestMapping(value = "approveTask", method = RequestMethod.POST)
    public ResponseEntity<?> approveTask(@RequestBody Long taskId) throws BadRequestException {

        makerCheckerService.approveTask(taskId);

        SysWfDocs sysWfDocs = new SysWfDocs();
        sysWfDocs.setDocId(taskId);
        System.out.println(sysWfDocs.getClientId());
        System.out.println(taskId);

        return ResponseEntity.ok("Task approved successfully");
    }
}
