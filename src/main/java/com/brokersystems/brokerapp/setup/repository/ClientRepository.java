package com.brokersystems.brokerapp.setup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.ClientDef;
import org.springframework.data.repository.query.Param;


public interface ClientRepository extends  PagingAndSortingRepository<ClientDef, Long>, QueryDslPredicateExecutor<ClientDef> {

	Optional<ClientDef> findByTenId(Long tenId);

	@Query(value = "select client_sht_desc,client_fname ,nullif(client_onames,'') client_onames,client_idno,client_email,client_phone,cnt_type_desc,client_status,client_date_created,\n" +
			"client_id,nullif(sys_brk_users.user_username,'Converted'),case when nullif(client_authorized,'N') = 'Y' THEN 'Yes' else 'No' end auth,client_hash_code,client_cif,COUNT(*) OVER() as total_rows \n" +
			"from sys_brk_clients  \n" +
			" left join sys_brk_client_types sbct \n" +
			"on client_clnt_type = sbct.cnt_type_id \n" +
			"left join sys_brk_users on client_created_user = user_id\n" +
			"where (nullif(client_idno,'') like :search)\n" +
//			"or lower(nullif(client_passport_no,'')) like :search\n" +
//			"or lower(concat(client_fname ,' ',nullif(client_onames,''))) like :search\n" +
//			"or nullif(client_phone,'') like :search\n" +
//			"or nullif(client_sms_number,'') like :search)\n" +
			"order by client_date_created desc OFFSET :pageNo*:limit limit :limit",nativeQuery = true)
	List<Object[]> searchClientsList(@Param("search") String search, @Param("pageNo") int pageNo, @Param("limit") int limit);


	@Query(value = "SELECT   client_id                                 tenId,\n" +
			"         client_sht_desc                           tenantNumber,\n" +
			"         client_fname                              fname,\n" +
			"         client_onames                             otherNames,\n" +
			"         client_idno                             idNo,\n" +
			"		  client_pin                              pinNo,\n " +
			"         COUNT(*) OVER() as total_rows\n" +
			"  from sys_brk_clients sbc \n" +
			" JOIN sys_brk_client_types sbct ON sbc.client_clnt_type = sbct.cnt_type_id \n"+
			"  where client_authorized = 'Y'\n" +
			"    and client_status not in ('T')\n" +
			"    and (lower(concat(client_fname,' ',client_onames)) like lower(:search)\n" +
			"           or lower(concat(client_onames,' ',client_fname)) like lower(:search)\n" +
			"           or client_idno like :search\n" +
			"           or lower(client_sht_desc) like lower(:search)\n" +
			"           or client_cif like :search\n" +
			"           or client_phone like :search\n" +
			"			or client_pin like :search\n " +
			"            )\n" +
			"     order by  client_fname      OFFSET :pageNo*:limit limit :limit ",nativeQuery = true)
	List<Object[]> searchClientsLists(@Param("search") String search,
									  @Param("pageNo") int pageNo,
									  @Param("limit") int limit);


	List<ClientDef> findClientDefByPinNo(String pinNo);

	List<ClientDef> findClientDefByIdNo(String idNo);
	List<ClientDef> findByEmailAddress(String emailAddress);

	@Query(value = "SELECT client_id, client_address, client_authorized, client_cust_ref, comments, client_date_created, \n" +
			"client_datereg, client_datetermin, client_dob, client_email, client_fname, client_gender, client_idno, \n" +
			"client_tel_number, client_onames, client_passport_no, client_phone, client_pin, \n" +
			"client_sms_number, client_status, client_cif, client_auth_user, client_sector_id, client_title_id, \n" +
			"client_cou_code, client_created_user, client_occup_id, client_phone_prefix, client_post_code, client_brn_code, \n" +
			"client_sms_prefix, client_clnt_type, client_town_code,client_photo_url,sbb.ob_name,sbt.ct_name,sbmp.prefix_name phone_prefix,\n" +
			"sbmp2.prefix_name  sms_prefix,sbct.cnt_type ,sbo.occ_name,sbc2.cou_name,sbct2.cnt_title,sbs.sector_name,sbpc.p_zip_code,sbct.cnt_type_desc,client_hash_code,client_resident_status,sbs2.seg_id,sbs2.seg_name, sbc.client_segment \n" +
			"FROM sys_brk_clients sbc\n" +
			"left join sys_brk_branches sbb on sbb.ob_id  =sbc.client_brn_code \n" +
			"left join sys_brk_towns sbt on sbc.client_town_code =sbt.ct_code \n" +
			"left join sys_brk_mob_prefix sbmp on sbmp.prefix_id  = client_phone_prefix \n" +
			"left join sys_brk_mob_prefix sbmp2 on sbmp2.prefix_id  = client_sms_prefix \n" +
			"join sys_brk_client_types sbct on sbct.cnt_type_id  = client_clnt_type \n" +
			"left join sys_brk_occupation sbo on sbo.occ_id  = client_occup_id \n" +
			"left join sys_brk_countries sbc2 on sbc2.cou_code  = client_cou_code \n" +
			"left join sys_brk_client_titles sbct2 on sbct2.cnt_title_id  = client_title_id \n" +
			"left join sys_brk_sectors sbs on sbs.sector_id  = client_sector_id \n" +
			"left join sys_brk_postal_codes sbpc on sbpc.p_code = client_post_code \n" +
			"left join sys_brk_segments sbs2 on sbc.client_segment_code = sbs2.seg_id \n" +
			"where sbc.client_id=:clientId", nativeQuery = true)
	List<Object[]> findClientDetails(@Param("clientId") Long clientId);

	@Query(value = "SELECT client_id, client_address, client_authorized, client_cust_ref, comments, client_date_created, \n" +
			"client_datereg, client_datetermin, client_dob, client_email, client_fname, client_gender, client_idno, \n" +
			"client_tel_number, client_onames, client_passport_no, client_phone, client_pin, \n" +
			"client_sms_number, client_status, client_cif, client_auth_user, client_sector_id, client_title_id, \n" +
			"client_cou_code, client_created_user, client_occup_id, client_phone_prefix, client_post_code, client_brn_code, \n" +
			"client_sms_prefix, client_clnt_type, client_town_code,client_photo_url,sbb.ob_name,sbt.ct_name,sbmp.prefix_name phone_prefix,\n" +
			"sbmp2.prefix_name  sms_prefix,sbct.cnt_type ,sbo.occ_name,sbc2.cou_name,sbct2.cnt_title,sbs.sector_name,sbpc.p_zip_code,sbct.cnt_type_desc,client_hash_code,client_resident_status   \n" +
			"FROM sys_brk_clients sbc\n" +
			"left join sys_brk_branches sbb on sbb.ob_id  =sbc.client_brn_code \n" +
			"left join sys_brk_towns sbt on sbc.client_town_code =sbt.ct_code \n" +
			"left join sys_brk_mob_prefix sbmp on sbmp.prefix_id  = client_phone_prefix \n" +
			"left join sys_brk_mob_prefix sbmp2 on sbmp2.prefix_id  = client_sms_prefix \n" +
			"join sys_brk_client_types sbct on sbct.cnt_type_id  = client_clnt_type \n" +
			"left join sys_brk_occupation sbo on sbo.occ_id  = client_occup_id \n" +
			"left join sys_brk_countries sbc2 on sbc2.cou_code  = client_cou_code \n" +
			"left join sys_brk_client_titles sbct2 on sbct2.cnt_title_id  = client_title_id \n" +
			"left join sys_brk_sectors sbs on sbs.sector_id  = client_sector_id \n" +
			"left join sys_brk_postal_codes sbpc on sbpc.p_code = client_post_code \n" +
			"where sbc.client_hash_code=:clientId", nativeQuery = true)
	List<Object[]> findClientDetailsByHash(@Param("clientId") String clientId);

	@Query(value = "select trans_amount,trans_date,trans_net_amt,trans_balance,trans_ref_no,\n" +
			"case when  trans_type='NBD'  then 'New Transaction'\n" +
			"when trans_type ='RC' THEN 'Receipt'\n" +
			"when trans_type = 'NBC' THEN 'New Transaction Reversal'\n" +
			"when trans_type = 'RNC' THEN 'Renewal Reversal'\n" +
			"when trans_type ='CN' THEN 'Cancelled'\n" +
			"when trans_type ='RND' then 'Renewal'\n" +
			"else 'Other' end trans_type,sbp.pol_wef_date,sbp.pol_wet_date ,\n" +
			"sbp.pol_no,sbp2.pr_desc , COUNT(*) OVER() as total_rows\n" +
			"from sys_brk_main_transactions sbmt  \n" +
			"join sys_brk_policies sbp on sbp.pol_id  = trans_pol_id \n" +
			"join sys_brk_products sbp2 on sbp2.pr_code  = sbp.pol_prod_id \n" +
			"where trans_clnt_type ='C' and trans_authorised ='Y'\n" +
			"and trans_clnt_code=:clntCode and sbp.pol_no like :search\n" +
			"order by trans_date desc OFFSET :pageNo*:limit limit :limit", nativeQuery = true)
	List<Object[]> getClientTransactions(@Param("clntCode") Long clntCode,
										 @Param("search") String search,
										 @Param("pageNo") int pageNo,
										 @Param("limit") int limit);

	@Query("SELECT c FROM ClientDef c WHERE UPPER(c.pinNo) = UPPER(:pinNo)")
	ClientDef findByPinNoIgnoreCase(@Param("pinNo") String pinNo);

	@Query("SELECT c FROM ClientDef c WHERE UPPER(c.clientCIF) = UPPER(:clientCIF) and UPPER(c.idNo) = UPPER(:idNo)")
	ClientDef findByCifandIdNoIgnoreCase(@Param("clientCIF") String clientCIF, @Param("idNo") String idNo);

	ClientDef findByIdNo(String idNo);
	ClientDef findByTenantNumber(String tenantNumber);

}
