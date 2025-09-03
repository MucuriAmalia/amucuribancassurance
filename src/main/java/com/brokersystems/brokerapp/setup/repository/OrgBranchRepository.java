package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.OrgBranch;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;


public interface OrgBranchRepository extends  PagingAndSortingRepository<OrgBranch, Long>, QueryDslPredicateExecutor<OrgBranch> {
	
	public  Page<OrgBranch> findByObShtDescLikeIgnoreCase(String paramString, Pageable paramPageable);

	@Query(value = "select ob_id,ob_name,COUNT(*) OVER() AS total_rows  from sys_brk_branches \n" +
			"where ob_id  in (select bub_ob_id from sys_brk_user_branches where bub_user_id=:userId)\n" +
			"and lower(ob_name) like :search" +
			" order by ob_name desc OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
	List<Object[]> findBranches(@Param("search") String search,
								@Param("userId") Long userId,
							   @Param("pageNo") int pageNo,
							   @Param("limit") int limit);

	@Query(value = "select ob_id  from sys_brk_branches \n" +
			"where ob_id  in (select bub_ob_id from sys_brk_user_branches where bub_user_id=:userId)\n" ,nativeQuery = true)
	List<BigInteger> findUserBranches(@Param("userId") Long userId);


	@Query(value = "select ob_id,ob_name,COUNT(*) OVER() as total_rows  from sys_brk_branches \n" +
			" where lower(ob_name) like :search" +
			" order by ob_name desc OFFSET :pageNo*:limit limit :limit",nativeQuery = true)
	List<Object[]> findAllBranches(@Param("search") String search,
								@Param("pageNo") int pageNo,
								@Param("limit") int limit);


	@Query(value = "select ob_id,ob_sht_desc ,ob_name,ob_address,ob_tel_number,sbu.user_name," +
			"nullif(ob_head_office,'N') ob_head_office,ob_user_code,COUNT(*) OVER() AS total_rows  \n" +
			"from sys_brk_branches  \n" +
			"left join sys_brk_users sbu on sbu.user_id =ob_user_code " +
			" where ob_reg_code =:regcode and  lower(ob_name) like :search " +
			"order by ob_name desc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
	List<Object[]> findRegionBranches(@Param("regcode")Long regcode,
									  @Param("search") String search,
									  @Param("pageNo") int pageNo,
									  @Param("limit") int limit);

	@Query(value = "select count(1) from sys_brk_branches where nullif(ob_head_office,'N')='Y' and ob_id not in (:obId)",nativeQuery = true)
	long countHeadOffice(@Param("obId") Long obId);

	@Query(value = "select ob_id from sys_brk_branches where nullif(ob_head_office,'N')='Y'",nativeQuery = true)
	Long findHeadOffice();

	@Query("SELECT p FROM OrgBranch p WHERE UPPER(p.headOffice) = 'Y'")
	OrgBranch findHeadOfficeID();

	@Query(value = "select  * from sys_brk_branches where ob_sht_desc = :code", nativeQuery = true)
	OrgBranch findBranch(@Param("code") String code);
}
 