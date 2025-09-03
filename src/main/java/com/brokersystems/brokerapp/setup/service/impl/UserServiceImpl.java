
package com.brokersystems.brokerapp.setup.service.impl;

import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.mail.service.Mailer;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.TemplateMerger;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.users.dto.ChangePasswordDTO;
import com.brokersystems.brokerapp.users.dto.PermissionDTO;
import com.brokersystems.brokerapp.users.model.*;
import com.brokersystems.brokerapp.users.repository.*;
import com.brokersystems.brokerapp.users.utils.PasswordValidatorUtils;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RolesRepo rolesRepo;

	@Autowired
	private OrgBranchRepository branchRepository;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private ModulesRepo modulesRepo;

	@Autowired
	private PermissionsRepo permissionsRepo;

	@Autowired
	private RolePermissionsRepo rolePermissionsRepo;

	@Autowired
	private UserRolesRepo userRolesRepo;
	
	@Autowired
	private BCryptPasswordEncoder encoder;

    @Autowired
	private ParamService paramService;

	@Autowired
	private PasswordResetTokenRepo passwordResetTokenRepo;

	@Autowired
	private TemplateMerger templateMerger;

	@Autowired
	private SubAccountTypesRepo subAccountTypesRepo;

	@Autowired
	private SequenceRepository sequenceRepo;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private AccountTypeRepo accountTypeRepo;

	@Autowired
	private UserBranchesRepository userBranchesRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;


	@Autowired
	private PasswordValidatorUtils passwordValidatorUtils;

    @Override
	//@Cacheable(value = "users", key="#userName")
	public UserDTO findByUserName(String userName) {
		List<Object[]> users = userRepository.findSearchByUsername(userName);
		if(users.size() != 1) return null;
		Object[] arr = users.get(0);
		UserDTO user = new UserDTO();
		user.setUsername((String)arr[0]);
		user.setPassword((String)arr[1]);
		user.setStatus((String)arr[2]);
		user.setId(((BigInteger)arr[3]).longValue());
		user.setName((String)arr[4]);
		user.setEmail((String)arr[5]);
		user.setResetPass((String)arr[6]);
		user.setSendEmail((String)arr[7]);
		return user;
	}

	@Override
	public UserDTO findByUserId(Long userId) {
		List<Object[]> users = userRepository.findSearchByUserId(userId);
		if(users.size() != 1) return null;
		Object[] arr = users.get(0);
		UserDTO user = new UserDTO();
		user.setUsername((String)arr[0]);
		user.setPassword((String)arr[1]);
		user.setStatus((String)arr[2]);
		user.setId(((BigInteger)arr[3]).longValue());
		user.setName((String)arr[4]);
		user.setEmail((String)arr[5]);
		if(arr[6]!=null){
			user.setAcctId(((BigInteger)arr[6]).longValue());
			user.setAcctTypeId(((BigInteger)arr[7]).longValue());
		}
		return user;
	}

	@Override
	public UserDTO findUserSignatureDetails(Long userId) {
		List<Object[]> users = userRepository.findUserSignatureDetails(userId);
		if(users.size() != 1) return null;
		Object[] arr = users.get(0);
		UserDTO user = new UserDTO();
		user.setSignature((String)arr[0]);
		user.setSignatureContentType((String)arr[1]);
		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<UserDTO> findDatatables(DataTablesRequest request, String userstatus) {
		final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";
		List<Object[]> usersList = userRepository.findAllUsers(userstatus, search.toLowerCase(),request.getPageNumber(), request.getPageSize());
		final List<UserDTO> users = new ArrayList<>();
		long rowCount = 0L;
		if(!usersList.isEmpty()) rowCount = ((BigInteger)usersList.get(0)[14]).intValue();
		for(Object[] arr: usersList){
			UserDTO user = new UserDTO();
			user.setUsername((String)arr[0]);
			user.setPassword((String)arr[1]);
			user.setStatus((String)arr[2]);
			user.setId(((BigInteger)arr[3]).longValue());
			user.setName((String)arr[4]);
			user.setEmail((String)arr[5]);
			if(arr[6]!=null){
				user.setAcctId(((BigInteger)arr[6]).longValue());
				user.setAcctTypeId(((BigInteger)arr[7]).longValue());
				user.setAccountType((String)arr[8]);
			}
			user.setSignature((String)arr[9]);
			user.setSignatureContentType((String)arr[10]);
			user.setAccType((String)arr[11]);
			user.setLastLogin((Timestamp)arr[12]);
			user.setAbsaNo((String)arr[13]);
			users.add(user);
		}
		Page<UserDTO>  page = new PageImpl<>(users,request, rowCount);
		return new DataTablesResult<>(request, page);
	}


	@Override
	public Page<AccountTypesDTO> findSubAccountTypes(String searchValue, Pageable paramPageable) {
		if(searchValue==null) searchValue="%%";
		else searchValue = "%"+searchValue+"%";
		List<Object[]> accountTypesList = accountTypeRepo.findIAAccountTypes(searchValue, paramPageable.getPageNumber(), paramPageable.getPageSize());
		long rowCount = 0L;
		if(!accountTypesList.isEmpty()) rowCount = ((BigInteger)accountTypesList.get(0)[2]).intValue();
		List<AccountTypesDTO> accountTypes = new ArrayList<>();
		for(Object[] accountType:accountTypesList){
			AccountTypesDTO accountTypesDTO = new AccountTypesDTO();
			accountTypesDTO.setAccId(((BigInteger)accountType[0]).longValue());
			accountTypesDTO.setAccName((String) accountType[1]);
			accountTypes.add(accountTypesDTO);
		}
		return new PageImpl<>(accountTypes,paramPageable, rowCount);
	}

	private boolean isValid(String stringToValidate) throws BadRequestException {
		final String regexParam = paramService.getParameterString("PASSWORD_REGEX");
		return Pattern.compile(regexParam).matcher(stringToValidate).find();
	}

//	@Modifying
//	@Transactional(readOnly = false)
//	@CacheEvict(value = "users",key = "#saveUser.username")
//	public void saveOrUpdate(UserDTO saveUser, HttpServletRequest request) throws BadRequestException{
//		final boolean newUser = saveUser.getId()==null;
//		final User user = new User();
//		 boolean userHasAccount = false;
//		UserDTO currentUser = null;
//		BeanUtils.copyProperties(saveUser,user);
//		String password = null;
//		if(newUser){
//			 password = RandomStringUtils.random(10, true, true);
//			if(!isValid(saveUser.getPassword())){
//				throw new BadRequestException("The Password must be at least 8 characters with at least a letter and a number");
//			}
//			if(!StringUtils.equals(saveUser.getPassword(),saveUser.getConfirmPassword()))
//				throw new BadRequestException("Password do not match...");
//			if(userRepository.count(QUser.user.username.endsWithIgnoreCase(user.getUsername())) > 0)
//				throw new BadRequestException("User already exists...");
//			user.setEnabled("1");
//			user.setPassword(encoder.encode(user.getPassword()));
//		}else{
//			currentUser = this.findByUserId(saveUser.getId());
//			user.setPassword(currentUser.getPassword());
//			if(saveUser.getStatus()==null){
//				user.setEnabled("0");
//			}
//			else if(saveUser.getStatus().equalsIgnoreCase("on")){
//				user.setEnabled("1");
//			}
//			else
//				user.setEnabled("0");
//			userHasAccount = currentUser.getAcctTypeId()!=null;
//		}
//		if("on".equalsIgnoreCase(saveUser.getMarketer()) && !userHasAccount){
//				if(saveUser.getAcctTypeId()==null)
//					throw new BadRequestException("Select Marketer Type to continue");
//				AccountDef accountDef = new AccountDef();
//				accountDef.setAccountType(accountTypeRepo.findOne(saveUser.getAcctTypeId()));
//				accountDef.setAddress("N/A");
//				accountDef.setPhoneNo("N/A");
//				accountDef.setName(user.getName());
//				accountDef.setWef(new Date());
//				accountDef.setStatus("A");
//				Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("A");
//				if (sequenceRepo.count(seqPredicate) == 0)
//					throw new BadRequestException("Sequence for Account Definition has not been setup");
//				SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
//				Long seqNumber = sequence.getNextNumber();
//				final String accountNumber = sequence.getSeqPrefix() + String.format("%03d", seqNumber);
//				sequence.setLastNumber(seqNumber);
//				sequence.setNextNumber(seqNumber + 1);
//				sequenceRepo.save(sequence);
//				accountDef.setShtDesc(accountNumber);
//				accountDef.setEmail(user.getEmail());
//				accountRepo.save(accountDef);
//				user.setAccountDef(accountDef);
//				user.setSubAccountTypes(accountTypeRepo.findOne(saveUser.getAcctTypeId()));
//		}
//		if(user.getId()==null) {
//			user.setResetPass("Y");
////			user.setSendEmail("N");
//			user.setSendEmail("Y");
//			user.setPassword(encoder.encode(password));
//			User savedUser = userRepository.save(user);
//			Iterable<PasswordResetToken> passwordResetTokens = passwordResetTokenRepo.findAll(QPasswordResetToken.passwordResetToken.user.id.eq(savedUser.getId()));
//			passwordResetTokenRepo.delete(passwordResetTokens);
//			PasswordResetToken token = new PasswordResetToken();
//			token.setToken(UUID.randomUUID().toString());
//			token.setUser(savedUser);
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(new Date());
//			cal.add(Calendar.HOUR, 24);
//			java.util.Date expirationDate = cal.getTime();
//			token.setExpiryDate(expirationDate);
//			passwordResetTokenRepo.save(token);
//			templateMerger.sendPasswordResetEmail(savedUser.getUsername(), password, request);
//		}
//	}

	@Modifying
	@Transactional(readOnly = false)
	@CacheEvict(value = "users", key = "#saveUser.username")
	public void saveOrUpdate(UserDTO saveUser, HttpServletRequest request) throws BadRequestException {
		final boolean newUser = saveUser.getId() == null;
		final User user = new User();
		boolean userHasAccount = false;
		UserDTO currentUser = null;
		BeanUtils.copyProperties(saveUser, user);

		if (newUser) {
			// Validate password requirements
			if (!isValid(saveUser.getPassword())) {
				throw new BadRequestException("The Password must be at least 8 characters with at least a letter and a number");
			}
			if (!StringUtils.equals(saveUser.getPassword(), saveUser.getConfirmPassword())) {
				throw new BadRequestException("Password do not match...");
			}
			if (userRepository.count(QUser.user.username.endsWithIgnoreCase(user.getUsername())) > 0) {
				throw new BadRequestException("User already exists...");
			}
			user.setEnabled("1");
			user.setPassword(encoder.encode(saveUser.getPassword()));
		} else {
			currentUser = this.findByUserId(saveUser.getId());
			user.setPassword(currentUser.getPassword());
			if (saveUser.getStatus() == null) {
				user.setEnabled("0");
			} else {
				user.setEnabled(saveUser.getStatus().equalsIgnoreCase("on") ? "1" : "0");
			}
			userHasAccount = currentUser.getAcctTypeId() != null;
		}

		if ("on".equalsIgnoreCase(saveUser.getMarketer()) && !userHasAccount) {
			if (saveUser.getAcctTypeId() == null) {
				throw new BadRequestException("Select Marketer Type to continue");
			}
			AccountDef accountDef = createAccountDefinition(user, saveUser.getAcctTypeId());
			user.setAccountDef(accountDef);
			user.setSubAccountTypes(accountTypeRepo.findOne(saveUser.getAcctTypeId()));
		}

		if (user.getId() == null) {
			user.setResetPass("N"); // User already set their password during registration
			user.setSendEmail("Y");
			User savedUser = userRepository.save(user);

			// Generate and save password reset token
			createPasswordResetToken(savedUser);

			// Send welcome email with account details
			templateMerger.sendPasswordResetEmail(savedUser.getUsername(), saveUser.getPassword(), request);
		} else {
			userRepository.save(user);
		}
	}

	private AccountDef createAccountDefinition(User user, Long acctTypeId) throws BadRequestException {
		AccountDef accountDef = new AccountDef();
		accountDef.setAccountType(accountTypeRepo.findOne(acctTypeId));
		accountDef.setAddress("N/A");
		accountDef.setPhoneNo("N/A");
		accountDef.setName(user.getName());
		accountDef.setWef(new Date());
		accountDef.setStatus("A");

		// Generate account number
		Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("A");
		if (sequenceRepo.count(seqPredicate) == 0) {
			throw new BadRequestException("Sequence for Account Definition has not been setup");
		}

		SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
		Long seqNumber = sequence.getNextNumber();
		final String accountNumber = sequence.getSeqPrefix() + String.format("%03d", seqNumber);
		sequence.setLastNumber(seqNumber);
		sequence.setNextNumber(seqNumber + 1);
		sequenceRepo.save(sequence);

		accountDef.setShtDesc(accountNumber);
		accountDef.setEmail(user.getEmail());
		return accountRepo.save(accountDef);
	}

	private void createPasswordResetToken(User user) {
		// Delete any existing tokens for this user
		passwordResetTokenRepo.delete(
				passwordResetTokenRepo.findAll(
						QPasswordResetToken.passwordResetToken.user.id.eq(user.getId())
				)
		);

		// Create new token
		PasswordResetToken token = new PasswordResetToken();
		token.setToken(UUID.randomUUID().toString());
		token.setUser(user);

		// Set expiration to 24 hours
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, 24);
		token.setExpiryDate(cal.getTime());

		passwordResetTokenRepo.save(token);
	}


	@Override
	@Transactional(readOnly = true)
	public User findById(Long id) {
		return userRepository.findOne(id);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	@CacheEvict(value = "users")
	public void deleteUser(Long userId) throws BadRequestException {
		if(userId==null) throw new BadRequestException("No user selected to deactivate");
		User user = userRepository.findOne(userId);
		final String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
		if(StringUtils.equals(user.getUsername(),loggedInUser)){
			throw new BadRequestException("Cannot deactivate currently logged in user...");
		}
		Iterable<OrgBranch> branches = branchRepository.findAll(QOrgBranch.orgBranch.branchManager.eq(user));

		for(OrgBranch branch:branches){
			branch.setBranchManager(null);
		}
		branchRepository.save(branches);
		user.setEnabled("0");
		userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<RolesDef> findRoles(DataTablesRequest request) throws IllegalAccessException {
		Page<RolesDef> page =rolesRepo.findAll(request.searchPredicate(QRolesDef.rolesDef),request);
		return new DataTablesResult<RolesDef>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<ModulesDef> getModules(DataTablesRequest request) throws IllegalAccessException {
		Page<ModulesDef> page =modulesRepo.findAll(request.searchPredicate(QModulesDef.modulesDef),request);
		return new DataTablesResult<ModulesDef>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<PermissionDTO> findPermissions(DataTablesRequest request, Long moduleId,Long roleId)  {
		final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%";
		List<PermissionDTO> permissionDTOS = new ArrayList<>();
		List<Object[]> permissionsList = permissionsRepo.findRolePermissions(moduleId,roleId,search,request.getPageNumber(), request.getPageSize());
		for(Object[] obj:permissionsList){
			PermissionDTO permissionDTO = new PermissionDTO();
			permissionDTO.setPermId(((BigInteger)obj[0] ).longValue());
			permissionDTO.setAccessType((String)obj[1]);
			permissionDTO.setPermDesc((String)obj[2]);
			permissionDTO.setPermName((String)obj[3]);
			permissionDTO.setMinAmount((BigDecimal) obj[4]);
			permissionDTO.setMaxAmount((BigDecimal) obj[5]);
			permissionDTO.setCount((Integer) obj[6] );
			permissionDTOS.add(permissionDTO);
		}
		Page<PermissionDTO>  page = new PageImpl<>(permissionDTOS,request, permissionsRepo.countRolePermissions(moduleId,roleId,search));
		return new DataTablesResult<>(request, page);
	}
	
    @Override
	@Transactional(readOnly = true)
	public DataTablesResult<PermissionDTO> findAllPermissions(DataTablesRequest request) {
		final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";

		List<Object[]> permissionsList = permissionsRepo.findAllPermissions(search.toLowerCase(), request.getPageNumber(), request.getPageSize());
		final List<PermissionDTO> permissionDTOS = new ArrayList<>();
		long rowCount = 0L;
		if(!permissionsList.isEmpty()){
			rowCount = ((BigInteger)permissionsList.get(0)[5]).intValue();
		}
		for (Object[] obj : permissionsList) {
			PermissionDTO permissionDTO = new PermissionDTO();
			permissionDTO.setPermName((String) obj[0]);
			permissionDTO.setPermDesc((String) obj[1]);
			permissionDTO.setAccessType((String) obj[2]);
			permissionDTO.setModuleName((String) obj[3]);
			permissionDTO.setMakerCheckerRequired((String) obj[4]);
			permissionDTOS.add(permissionDTO);
		}

		Page<PermissionDTO> page = new PageImpl<>(permissionDTOS, request, rowCount);
		return new DataTablesResult<>(request, page);
	}


	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void grantPermission(PermissionBean permissionBean) throws BadRequestException {
        long count = rolePermissionsRepo.count(QRolePermissions.rolePermissions.roles.roleId.eq(permissionBean.getRoleId()).and(QRolePermissions.rolePermissions.permission.permId.eq(permissionBean.getPermissionId())));
		if(count > 0) throw new BadRequestException("Permission Already Exist..Cannot Grant");
		RolePermissions rolePermission = new RolePermissions();
		rolePermission.setPermission(permissionsRepo.findOne(permissionBean.getPermissionId()));
		rolePermission.setRoles(rolesRepo.findOne(permissionBean.getRoleId()));
		rolePermissionsRepo.save(rolePermission);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void revokePermission(PermissionBean permissionBean) throws BadRequestException {
		Long count = rolePermissionsRepo.count(QRolePermissions.rolePermissions.roles.roleId.eq(permissionBean.getRoleId()).and(QRolePermissions.rolePermissions.permission.permId.eq(permissionBean.getPermissionId())));
		if(count == 0) throw new BadRequestException("Permission does not exist..Cannot Revoke");
		Iterable<RolePermissions> permissions = rolePermissionsRepo.findAll(QRolePermissions.rolePermissions.roles.roleId.eq(permissionBean.getRoleId()).and(QRolePermissions.rolePermissions.permission.permId.eq(permissionBean.getPermissionId())));
		rolePermissionsRepo.delete(permissions);
	}

	@Override
	@Transactional(readOnly = true)
	public Long checkIfRoleExists(long roleId, long permId) {
		return rolePermissionsRepo.count(QRolePermissions.rolePermissions.roles.roleId.eq(roleId).and(QRolePermissions.rolePermissions.permission.permId.eq(permId)));
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void createRole(RolesDef role) throws BadRequestException {
		if(StringUtils.isBlank(StringUtils.trim(role.getRoleName())))
			throw new BadRequestException("Role Name cannot be Blank");
		if(role.getRoleId()==null) {
			long count = rolesRepo.count(QRolesDef.rolesDef.roleName.equalsIgnoreCase(StringUtils.trim(role.getRoleName())));
			if (count > 0) throw new BadRequestException("Role already exists...");
		}
       rolesRepo.save(role);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void createPermission(PermissionsDef permission) throws BadRequestException {
		if(StringUtils.isBlank(StringUtils.trim(permission.getPermName())))
			throw new BadRequestException("Permission Name cannot be Blank");
		if(permission.getPermId()==null) {
			long count = permissionsRepo.count(QPermissionsDef.permissionsDef.permName.equalsIgnoreCase(StringUtils.trim(permission.getPermName())));
			if (count > 0) throw new BadRequestException("Permission already exists...");
		}
		RolePermissions rolePermissions = new RolePermissions();
		RolesDef rolesDef = rolesRepo.findOne(QRolesDef.rolesDef.roleId.eq(permission.getRoleId()));
		rolePermissions.setRoles(rolesDef);
		rolePermissions.setPermission(permission);
		rolePermissionsRepo.save(rolePermissions);
		permissionsRepo.save(permission);
	}

	@CacheEvict(value = "users")
	public void resetPassword(String password, Long userId) {
		User user = userRepository.findOne(userId);
		user.setPassword(password);
		user.setResetPass("N");
		userRepository.save(user);
	}



	@Override
	@Transactional(readOnly = false)
	public void deleteRole(Long roleId) {
       rolesRepo.delete(roleId);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<RolesDef> findUnassignedUserRoles(DataTablesRequest request, Long userId) throws IllegalAccessException {
		Page<RolesDef> page =rolesRepo.getUnassignedRoles(userId,request);
		return new DataTablesResult<RolesDef>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<RolesDef> findassignedUserRoles(DataTablesRequest request, Long userId) throws IllegalAccessException {
		Page<RolesDef> page =rolesRepo.getAssignedRoles(userId,request);
		return new DataTablesResult<RolesDef>(request, page);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void assignUserRoles(UserRolesBean rolesBean) throws BadRequestException {
		if(rolesBean.getUserId()==null) throw new BadRequestException("User Id cannot be null..");
		if(rolesBean.getRoles() == null || rolesBean.getRoles().size()==0)
			throw new BadRequestException("No roles to assign");
		User user = userRepository.findOne(rolesBean.getUserId());
		List<UserRole> userRoles = new ArrayList<>();
		for(Long roleId:rolesBean.getRoles()){
			UserRole userRole = new UserRole();
			userRole.setUser(user);
			userRole.setRoles(rolesRepo.findOne(roleId));
			userRoles.add(userRole);
		}
		userRolesRepo.save(userRoles);

	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void unAssignUserRoles(UserRolesBean rolesBean) throws BadRequestException {
		if(rolesBean.getUserId()==null) throw new BadRequestException("User Id cannot be null..");
		if(rolesBean.getRoles() == null || rolesBean.getRoles().size()==0)
			throw new BadRequestException("No roles to assign");
		User user = userRepository.findOne(rolesBean.getUserId());
		List<UserRole> userRoles = new ArrayList<>();
		for(Long roleId:rolesBean.getRoles()){
			Iterable<UserRole> roles = userRolesRepo.findAll(QUserRole.userRole.roles.roleId.eq(roleId).and(QUserRole.userRole.user.id.eq(rolesBean.getUserId())));
			roles.forEach(a -> userRoles.add(a));
		}
		userRolesRepo.delete(userRoles);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal getMinLimitAmount(long roleId, long permId) {
		Long count = rolePermissionsRepo.count(QRolePermissions.rolePermissions.roles.roleId.eq(roleId).and(QRolePermissions.rolePermissions.permission.permId.eq(permId)));
        if(count ==1){
			RolePermissions permissions = rolePermissionsRepo.findOne(QRolePermissions.rolePermissions.roles.roleId.eq(roleId).and(QRolePermissions.rolePermissions.permission.permId.eq(permId)));
			return permissions.getMinAmount();
		}
		return BigDecimal.ZERO;
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal getMaxLimitAmount(long roleId, long permId) {
		Long count = rolePermissionsRepo.count(QRolePermissions.rolePermissions.roles.roleId.eq(roleId).and(QRolePermissions.rolePermissions.permission.permId.eq(permId)));
		if(count ==1){
			RolePermissions permissions = rolePermissionsRepo.findOne(QRolePermissions.rolePermissions.roles.roleId.eq(roleId).and(QRolePermissions.rolePermissions.permission.permId.eq(permId)));
			return permissions.getMaxAmount();
		}
		return BigDecimal.ZERO;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void savePermLimits(PermLimitsBean limitsBean) throws BadRequestException {
		if(limitsBean.getRoleId()==null) throw new BadRequestException("Role is Mandatory");
		if(limitsBean.getPermId()==null) throw new BadRequestException("Permission is Mandatory");
		if(limitsBean.getMinAmount().compareTo(BigDecimal.ZERO) ==-1)
			throw new BadRequestException("Min Limit Amount cannot be less than zero");
		if(limitsBean.getMaxAmount().compareTo(BigDecimal.ZERO) ==-1)
			throw new BadRequestException("Max Limit Amount cannot be less than zero");
		if(limitsBean.getMinAmount().compareTo(limitsBean.getMaxAmount()) ==1)
			throw new BadRequestException("Min Limit Amount cannot be greater than Max Limit Amount");
		Long count = rolePermissionsRepo.count(QRolePermissions.rolePermissions.roles.roleId.eq(limitsBean.getRoleId()).and(QRolePermissions.rolePermissions.permission.permId.eq(limitsBean.getPermId())));
		if(count !=1) throw new BadRequestException("Error getting Permission and Role Details...Contact System Admin");
		RolePermissions permissions = rolePermissionsRepo.findOne(QRolePermissions.rolePermissions.roles.roleId.eq(limitsBean.getRoleId()).and(QRolePermissions.rolePermissions.permission.permId.eq(limitsBean.getPermId())));
		permissions.setMaxAmount(limitsBean.getMaxAmount());
		permissions.setMinAmount(limitsBean.getMinAmount());
		rolePermissionsRepo.save(permissions);
	}

	@Override
	public Set<UserAuthority> getUserAuthorities(Long userId) {
		Set<UserAuthority> authorities = new HashSet<>();
		List<Object[]> userRoles = rolePermissionsRepo.getUserPerms(userId);
		for (Object[] object : userRoles) {
			authorities.add(new UserAuthority((String)object[0],(BigDecimal)object[2],(BigDecimal)object[1]));
		}
		return authorities;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void updateUserIP(Long userId,String ip) throws BadRequestException {
		if(userId==null) throw new BadRequestException("User not authenticated");
		User user = userRepository.findOne(userId);
		user.setLastIP(ip);
		userRepository.save(user);
	}

	@Override
	public void addModule(ModulesDef modulesDef) {
		modulesRepo.save(modulesDef);
	}

	@Override
	public ModulesDef findException(String exception) {
		Predicate pred=QModulesDef.modulesDef.moduleName.eq(exception);
		return modulesRepo.findOne(pred);
	}

	@Override
	public void setPermissionSetups(PermissionsDef permissionsDef) throws BadRequestException {
		Predicate pred2=QPermissionsDef.permissionsDef.permName.equalsIgnoreCase(permissionsDef.getPermName());
		Long bud2=permissionsRepo.count(pred2);
      if(bud2>0)throw new BadRequestException("A Permission already exists");
		permissionsRepo.save(permissionsDef);
	}


	@Override
	public ModulesDef findReportModule(String moduleName) {
		Predicate pred= QModulesDef.modulesDef.moduleName.equalsIgnoreCase(moduleName);
		return modulesRepo.findOne(pred);
	}

	@Override
	public Long findRepMod(String reports_module) {
		Predicate predicate=QModulesDef.modulesDef.moduleName.equalsIgnoreCase(reports_module);
		ModulesDef module=modulesRepo.findOne(predicate);
		return module.getModuleId();
	}

	@Override
	public String getPerm(Long permId) {
		Predicate predicate=QPermissionsDef.permissionsDef.permId.eq(permId);
		PermissionsDef permissionsDef=permissionsRepo.findOne(predicate);
		return permissionsDef.getPermName();
		}

	@Override
	@Transactional(readOnly = false)
	public void createUserBranch(CreateUserBranchesDTO userBranchesDTO) throws BadRequestException {
		if(userBranchesDTO.getUserId()==null)
			throw new BadRequestException("Select User to continue");
		if(userBranchesDTO.getBranches().isEmpty()){
			throw new BadRequestException("No Branches Selected..");
		}
		final User user = userRepository.findOne(userBranchesDTO.getUserId());
		if(user==null || user.getEnabled()==null || !user.getEnabled().equalsIgnoreCase("1")){
			throw new BadRequestException("Select A valid user....");
		}
		final List<UserBranches> branchList = new ArrayList<>();
		for(Long brn: userBranchesDTO.getBranches()){
			if(userBranchesRepository.countBranchesUser(userBranchesDTO.getUserId(),brn) ==0) {
				final OrgBranch branch = branchRepository.findOne(brn);
				UserBranches branches = new UserBranches();
				branches.setBranch(branch);
				branches.setUser(user);
				branches.setUserAssigned(userUtils.getCurrentUser());
				branches.setDateAssigned(new Date());
				branchList.add(branches);
			}
		}
		userBranchesRepository.save(branchList);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<UserBranchesDTO> findUserBranches(Long userId, DataTablesRequest request) {
		final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";
		List<Object[]> userBranches = userBranchesRepository.findUsrBranches(search.toLowerCase(),userId, request.getPageNumber(), request.getPageSize());
		final List<UserBranchesDTO> branchesDTOList = new ArrayList<>();
		long rowCount = 0l;
		if(!userBranches.isEmpty()) rowCount = ((BigInteger)userBranches.get(0)[5]).intValue();
		for(Object[] branch:userBranches){
			UserBranchesDTO branchesDTO = new UserBranchesDTO();
			branchesDTO.setUserBranchId(((BigInteger)branch[0]).longValue());
			branchesDTO.setDateAssigned((Date) branch[1]);
			branchesDTO.setBranchName((String) branch[2]);
			branchesDTO.setUsername((String) branch[3]);
			branchesDTO.setUserAssigned((String) branch[4]);
			branchesDTOList.add(branchesDTO);
		}
		Page<UserBranchesDTO>  page = new PageImpl<>(branchesDTOList,request, rowCount);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BranchDTO> findUnassignedBranches(Long userId, String search) {
		if(search==null){
			search="%%";
		}
		else {
			search = "%"+search+"%";
		}
		List<Object[]> userBranches = userBranchesRepository.findunassignedUsrBranches(search.toLowerCase(),userId);
		final List<BranchDTO> branchesDTOList = new ArrayList<>();
		for(Object[] branch:userBranches){
			BranchDTO branchDTO = new BranchDTO();
			branchDTO.setObId(((BigInteger)branch[0]).longValue());
			branchDTO.setObName((String) branch[1]);
			branchesDTOList.add(branchDTO);
		}
		return branchesDTOList;
	}


	@Override
	public void deleteUserBranch(Long userBranchId) {
		userBranchesRepository.delete(userBranchId);
	}

	@Override
	public String changePassword(ChangePasswordDTO changePasswordDTO) throws BadRequestException {
		if(changePasswordDTO.getUsername()==null){
			throw new BadRequestException("Invalid Username. Cannot change Password for this user");
		}
		final UserDTO user = findByUserName(changePasswordDTO.getUsername());
		final boolean enabled = user.getStatus()!=null && user.getStatus().equals("1");
		if(!enabled){
			throw new BadRequestException("The user is disabled and cannot change Password. Contact Administrator...");
		}
		if(changePasswordDTO.getNewPass()==null){
			throw new BadRequestException("The Password Cannot be null");
		}
		if(changePasswordDTO.getNewPass().length() < 7){
			throw new BadRequestException("The Password is too short....");
		}
		if(!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(),user.getPassword())){
			throw new BadRequestException("The current user password is wrong..Cannot continue..");
		}
//		if(!passwordValidatorUtils.validatePassword(changePasswordDTO.getNewPass())){
//			throw new BadRequestException("Your new password should contain numbers capital letters small letters and special characters..If this persists contact system administrator");
//		}
		if(changePasswordDTO.getConfirmPass()==null || StringUtils.isBlank(changePasswordDTO.getConfirmPass())){
			throw new BadRequestException("Confirm Password cannot be null");
		}
		if(!StringUtils.equals(changePasswordDTO.getNewPass(),changePasswordDTO.getConfirmPass())){
			throw new BadRequestException("New Password and Confirm Password do not match...");
		}

		final User currentUser = userRepository.findOne(user.getId());
		currentUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPass()));
		currentUser.setSendEmail("N");
		currentUser.setResetPass("N");
		userRepository.save(currentUser);
		return "Password Changed Successfully..";
	}
}

