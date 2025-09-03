package com.brokersystems.brokerapp.setup.service;


import com.brokersystems.brokerapp.claims.dtos.ClaimantsDTO;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.setup.model.SubAccountTypes;
import com.brokersystems.brokerapp.setup.model.UserAuthority;
import com.brokersystems.brokerapp.users.dto.ChangePasswordDTO;
import com.brokersystems.brokerapp.users.dto.PermissionDTO;
import com.brokersystems.brokerapp.users.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.setup.model.User;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


/**
 * User Service
 * Mainly used for crud service on user setups
 * @author Peter
 *
 */
public interface UserService {


    UserDTO findByUserName(String userName);
    UserDTO findByUserId(Long userId);
    UserDTO findUserSignatureDetails(Long userId);

    DataTablesResult<UserDTO> findDatatables(DataTablesRequest request,String userstatus);

    void saveOrUpdate(UserDTO user, HttpServletRequest request) throws BadRequestException;

    public Page<AccountTypesDTO> findSubAccountTypes(String searchValue, Pageable paramPageable);
    
    User findById(Long id);

    void deleteUser(Long userId) throws BadRequestException;

    DataTablesResult<RolesDef> findRoles(DataTablesRequest request) throws IllegalAccessException;

    DataTablesResult<ModulesDef> getModules(DataTablesRequest request) throws IllegalAccessException;

    DataTablesResult<PermissionDTO> findPermissions(DataTablesRequest request, Long moduleId,Long roleId);
    DataTablesResult<PermissionDTO> findAllPermissions(DataTablesRequest request);

    void grantPermission(PermissionBean permissionBean) throws BadRequestException;

    void revokePermission(PermissionBean permissionBean) throws BadRequestException;

    Long checkIfRoleExists(long roleId,long permId);

    BigDecimal getMinLimitAmount(long roleId,long permId);

    BigDecimal getMaxLimitAmount(long roleId,long permId);


    void createRole(RolesDef role) throws BadRequestException;

    void resetPassword(String password, Long userId);

    void deleteRole(Long roleId);

    DataTablesResult<RolesDef> findUnassignedUserRoles(DataTablesRequest request,Long userId) throws IllegalAccessException;

    DataTablesResult<RolesDef> findassignedUserRoles(DataTablesRequest request,Long userId) throws IllegalAccessException;

    void assignUserRoles(UserRolesBean rolesBean) throws BadRequestException;

    void unAssignUserRoles(UserRolesBean rolesBean) throws BadRequestException;

    void savePermLimits(PermLimitsBean limitsBean) throws BadRequestException;

    Set<UserAuthority> getUserAuthorities(Long userId);

    void updateUserIP(Long userId,String ip) throws BadRequestException;

    void addModule(ModulesDef modulesDef);

    ModulesDef findException(String exception);

    void setPermissionSetups(PermissionsDef permissionsDef) throws BadRequestException;

    ModulesDef findReportModule(String moduleName);

    Long findRepMod(String reports_module);

    String getPerm(Long permId);

    void createUserBranch(CreateUserBranchesDTO userBranchesDTO) throws BadRequestException;

    DataTablesResult<UserBranchesDTO> findUserBranches(Long userId, DataTablesRequest request);

    List<BranchDTO> findUnassignedBranches(Long userId, String search);

    void deleteUserBranch(Long userBranchId);

    String changePassword(ChangePasswordDTO changePasswordDTO) throws BadRequestException;

    void createPermission(PermissionsDef permissionsDef) throws BadRequestException;
}
