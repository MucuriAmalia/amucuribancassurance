package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.EscalationLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brokersystems.brokerapp.setup.model.User;

import java.util.List;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, QueryDslPredicateExecutor<User>{
	User findByUsername(String username);

	List<User> findByEscalationLevel(EscalationLevel escalationLevel);

	@Query(value = "select user_username,user_password,user_status,user_id,user_name,user_email,user_reset_pwd,user_send_email  from sys_brk_users \n" +
			"where user_username =:username",nativeQuery = true)
	List<Object[]> findSearchByUsername(@Param("username") String username);

	@Query(value = "select user_username,user_password,user_status,user_id,user_name,user_email,sba.acct_id,sbat.acc_id  from sys_brk_users \n" +
			" left join sys_brk_accounts sba on user_sub_agent = sba.acct_id \n" +
			" left join sys_brk_account_types sbat on user_sub_account = sbat.acc_id \n" +
			" where user_id =:userId",nativeQuery = true)
	List<Object[]> findSearchByUserId(@Param("userId") Long userId);

	@Query(value = "select user_signature,user_content_type  from sys_brk_users \n" +
			"where user_id =:userId",nativeQuery = true)
	List<Object[]> findUserSignatureDetails(@Param("userId") Long userId);


	@Query(value = "select user_username,user_password,user_status,user_id,user_name,user_email,sba.acct_id,sbat.acc_id,sbat.acc_name ,\n" +
			"user_signature,user_content_type,NULLIF(sbat.acc_name,'System'),user_last_login,user_absa_no,COUNT(*) OVER() AS total_rows\n" +
			"from sys_brk_users\n" +
			"left join sys_brk_accounts sba on user_sub_agent = sba.acct_id \n" +
			"left join sys_brk_account_types sbat on user_sub_account = sbat.acc_id \n" +
			"where NULLIF(user_status,'0') =:userstatus\n" +
			"and (lower(user_email) like :search " +
			"or LOWER(user_name) like :search " +
			"or LOWER(user_absa_no) like :search " +
			"or LOWER(user_email) like :search " +
			"or LOWER(user_username) like :search) \n" +
			"order by user_name \n" +
			"OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
	List<Object[]> findAllUsers(@Param("userstatus") String userstatus,
			                   @Param("search") String search,
							   @Param("pageNo") int pageNo,
							   @Param("limit") int limit);
	
	Page<User> findByUsernameLikeIgnoreCaseAndEnabled(String username, Pageable paramPageable, String enabled);

    @Query(value = "select user_id,user_name,user_username,user_absa_no from sys_brk_users sbu \n" +
			" left join sys_brk_user_roles sbur on sbu.user_id = sbur.userid \n" +
			" left join sys_brk_roles sbr on sbur.role_name = sbr.role_id \n" +
			" left join sys_brk_role_permissions sbrp on sbr.role_id = sbrp.rp_role \n" +
			" left join sys_brk_permissions sbp on sbrp.rp_perm = sbp.perm_id \n" +
			" where sbp.perm_name = :permissionName \n",nativeQuery = true)
	List<Object[]> findByRoleAndPermission(@Param("permissionName") String permissionName);

	@Query(value = "select u from User u where u.id in :checkerIds")
	List<User> findUsersByIds(@Param("checkerIds") List<Long> checkerIds);

	@Query(value = "SELECT * FROM sys_brk_users WHERE LOWER(user_email) = LOWER(:email) limit 1", nativeQuery = true)
	User findUserByEmail(@Param("email") String email);

	@Query(value = "SELECT u.user_id, u.user_name, u.user_username, u.user_absa_no, " +
			"COUNT(*) OVER() as total_rows " +
			"FROM sys_brk_users u " +
			"WHERE u.user_status = '1' " +
			"AND (lower(u.user_name) LIKE :search " +
			"    OR lower(u.user_username) LIKE :search " +
			"    OR lower(u.user_absa_no) LIKE :search) " +
			"ORDER BY u.user_name " +
			"OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
	List<Object[]> findAllLeadsMan(@Param("search") String search,
								   @Param("pageNo") int pageNo,
								   @Param("limit") int limit);

	@Query(value = "SELECT u.user_id, u.user_name, u.user_username, u.user_absa_no, " +
			"COUNT(*) OVER() as total_rows " +
			"FROM sys_brk_users u " +
			"WHERE u.user_status = '1' " +
			"AND lower(u.user_username) LIKE :search " +
			"ORDER BY u.user_username " +
			"OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
	List<Object[]> findUsersForSelect(@Param("search") String search,
									  @Param("pageNo") int pageNo,
									  @Param("limit") int limit);
}
