package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.BindersDef;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;


public interface BindersRepo extends PagingAndSortingRepository<BindersDef, Long>, QueryDslPredicateExecutor<BindersDef> {

    @Query(value = "select bin_id, sba.acct_name, sbp.pr_desc, sba.acct_id, sbp.pr_code, sbb.bin_pol_no, sbb.bin_name, sbp.pr_age_appli, sbp.pr_motor_product, " +
            "COUNT(*) OVER() as total_rows " +
            "from sys_brk_binders sbb " +
            "join sys_brk_products sbp on sbp.pr_code = sbb.bin_pr_code " +
            "join sys_brk_product_grp sbpg on sbpg.bpg_code = sbp.pr_bpg_code " +
            "join sys_brk_accounts sba on sba.acct_id = sbb.bin_acct_code " +
            "where sbb.bin_auth_status = 'Authorised' " +
            "and sbb.bin_status = true " +
            "and sbpg.bpg_type in ('MD') " +
            "order by sbp.pr_desc OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchMedicalProductBinders(@Param("pageNo") int pageNo,
                                               @Param("limit") int limit);

    @Query(value = "select count(1) from sys_brk_binders where bin_acct_code=:acctId and bin_pr_code=:prodId",nativeQuery = true)
    Long countCheckIfProductExists(@Param("acctId") Long acctId, @Param("prodId") Long prodId);

    @Query(value = "select bin_id,sba.acct_name,sbp.pr_desc,sba.acct_id,sbp.pr_code,sbb.bin_pol_no,sbb.bin_name,sbp.pr_age_appli,sbp.pr_motor_product,\n" +
            "COUNT(*) OVER() as total_rows\n" +
            "from sys_brk_binders sbb \n" +
            "join sys_brk_products sbp on sbp.pr_code =sbb.bin_pr_code \n" +
            "join sys_brk_product_grp sbpg on sbpg.bpg_code  = sbp.pr_bpg_code  \n" +
            "join sys_brk_accounts sba on sba.acct_id  = sbb.bin_acct_code \n" +
            "where sbb.bin_auth_status ='Authorised'\n" +
            "and sbb.bin_status  = true\n" +
            "and sbb.bin_pr_code = :productId\n" +
            "and sbpg.bpg_type not in ('MD','L')\n" +
            "AND (sbb.bin_name like :search or sbp.pr_desc like :search)\n" +
            "order by sbp.pr_desc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchProductBinders(@Param("search") String search,
                                        @Param("pageNo") int pageNo,
                                        @Param("limit") int limit,
                                        @Param("productId") Long productId);

    @Query(value = "select bin_id,sba.acct_name,sbp.pr_desc,sba.acct_id,sbp.pr_code,sbb.bin_pol_no,sbb.bin_name,sbp.pr_age_appli,sbp.pr_motor_product,\n" +
            "COUNT(*) OVER() as total_rows\n" +
            "from sys_brk_binders sbb \n" +
            "join sys_brk_products sbp on sbp.pr_code =sbb.bin_pr_code \n" +
            "join sys_brk_product_grp sbpg on sbpg.bpg_code  = sbp.pr_bpg_code  \n" +
            "join sys_brk_accounts sba on sba.acct_id  = sbb.bin_acct_code \n" +
            "where sbb.bin_auth_status ='Authorised'\n" +
            "and sbb.bin_status  = true\n" +
            "and sbpg.bpg_type  in ('L')\n" +
            "AND (sbb.bin_name like :search or sbp.pr_desc like :search)\n" +
            "order by sbp.pr_desc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchLifeProductBinders(@Param("search") String search,
                                            @Param("pageNo") int pageNo,
                                            @Param("limit") int limit);

    @Query(value = "select bin_id,sba.acct_name,sbp.pr_desc,sba.acct_id,sbp.pr_code,sbb.bin_pol_no,sbb.bin_name,sbp.pr_age_appli,sbp.pr_motor_product,\n" +
            "COUNT(*) OVER() as total_rows\n" +
            "from sys_brk_binders sbb\n" +
            "join sys_brk_products sbp on sbp.pr_code =sbb.bin_pr_code\n" +
            "join sys_brk_product_grp sbpg on sbpg.bpg_code  = sbp.pr_bpg_code\n" +
            "join sys_brk_accounts sba on sba.acct_id  = sbb.bin_acct_code\n" +
            "where sbb.bin_auth_status ='Authorised'\n" +
            "and sbp.pr_code  in  (select distinct quot_pr_pro_id  from sys_brk_quot_products sbqp where quot_pr_quot_id =:quotId)\n" +
            "and sbb.bin_id not in (select quot_pr_bind_id  from sys_brk_quot_products sbqp where quot_pr_quot_id =:quotId)\n" +
            "and sbb.bin_status  = true\n" +
            "and sbpg.bpg_type not in ('MD','L')\n" +
            "AND (sbb.bin_name like :search or sbp.pr_desc like :search)\n" +
            "order by sbp.pr_desc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchCompBinders(@Param("search") String search,
                                     @Param("pageNo") int pageNo,
                                     @Param("limit") int limit,
                                     @Param("quotId") Long quoteId);

    @Query(value = "select bin_id , bin_name,bin_sht_desc, bin_pol_no,sbc.cur_name,sba.acct_name,sbat.acc_name,\n" +
            "case when bin_status=true then 'Active' else 'Inactive' end status,\n" +
            "isnull(bin_auth_status,'Draft') bin_auth_status,COUNT(*) OVER() as total_rows  from sys_brk_binders sbb \n" +
            "join sys_brk_currencies sbc on sbb.bin_cur_code = sbc.cur_code \n" +
            "join sys_brk_accounts sba  on sbb.bin_acct_code  = sba.acct_id \n" +
            "join sys_brk_account_types sbat on sbat.acc_id  = sba.acct_acc_code \n" +
            "where (sbb.bin_name like :search)\n" +
            "order by sbb.bin_name desc\n" +
            "OFFSET :pageNo*:limit limit :limit", nativeQuery = true)
    List<Object[]> searchAllBinders(@Param("search") String search,
                                    @Param("pageNo") int pageNo,
                                    @Param("limit") int limit);

//    @Query(value = "SELECT bin_id, bin_status, bin_default,\n" +
//            " bin_name, bin_pol_no, bin_remarks, bin_sht_desc, bin_auth_status, bin_type, bin_fund_binder, \n" +
//            "bin_max_exposure, bin_max_term, bin_medical_type, bin_min_prem, bin_min_term, bin_prem_age_type, \n" +
//            "sba.acct_id, sbc.cur_name, sbp.pr_desc\n" +
//            "FROM public.sys_brk_binders sbb \n"+
//            "left join sys_brk_accounts sba on sba.acct_id = sbb.bin_acct_code\n" +
//            "left join sys_brk_products sbp on sbp.pr_code =sbb.bin_pr_code\n" +
//            "left join sys_brk_currencies sbc on sbc.cur_code = sbb.bin_cur_code\n" +
//            "where sbb.bin_id = :binId",nativeQuery = true)
//    List<Object[]> findBinderDetails(@Param("binId") Long binId);

//    @Query(value = "SELECT sbb.bin_id, sbb.created_by, sbb.created_date, sbb.modified_by, sbb.modified_date, sbb.bin_status, \n" +
//            "sbb.bin_default, sbb.bin_name, sbb.bin_pol_no, sbb.bin_remarks, sbb.bin_sht_desc, sbb.bin_auth_status, \n" +
//            "sbb.bin_type, sbb.bin_fund_binder, sbb.bin_max_exposure, sbb.bin_max_term, sbb.bin_medical_type, \n" +
//            "sbb.bin_min_prem, sbb.bin_min_term, sbb.bin_prem_age_type, sbb.bin_acct_code, sbb.bin_cur_code, \n" +
//            "sbb.bin_pr_code,sbb.bin_calc_type, sbb.bin_policy_url, sbb.bin_premium_url \n " +
//            "FROM sys_brk_binders sbb WHERE sbb.bin_pr_code = :proCode AND sbb.bin_acct_code = :accId", nativeQuery = true)
//    List<Object[]> findBinder(@Param("proCode") Long proCode,
//                          @Param("accId") Long accId);

    @Query(value = "SELECT sbb.bin_id, sbb.created_by, sbb.created_date, sbb.modified_by, sbb.modified_date, sbb.bin_status,\n" +
            "sbb.bin_default, sbb.bin_name, sbb.bin_pol_no, sbb.bin_remarks, sbb.bin_sht_desc, sbb.bin_auth_status,\n" +
            "sbb.bin_type, sbb.bin_fund_binder, sbb.bin_max_exposure, sbb.bin_max_term, sbb.bin_medical_type, \n " +
            "sbb.bin_min_prem, sbb.bin_min_term, sbb.bin_prem_age_type, sbb.bin_acct_code, sbb.bin_cur_code,sbb.bin_pr_code, \n" +
            "sbb.bin_calc_type, sbb.bin_policy_url, sbb.bin_premium_url, sbb.bin_admin_fee_status \n" +
            " FROM sys_brk_binders sbb WHERE sbb.bin_type = 'B' AND sbb.bin_acct_code = :accId AND sbb.bin_pr_code = :proCode", nativeQuery = true)
    BindersDef findBinderByAccId(@Param("accId") Long accId, @Param("proCode") Long proCode);

    @Query(value = "SELECT * FROM sys_brk_binders sbb WHERE sbb.bin_type = 'B' AND sbb.bin_acct_code = :accId AND sbb.bin_pr_code = :proCode", nativeQuery = true)
    List<BindersDef> listFindBinderByAccId(@Param("accId") Long accId, @Param("proCode") Long proCode);

    @Query(value = "SELECT sbb.bin_id, sbb.created_by, sbb.created_date, sbb.modified_by, sbb.modified_date, sbb.bin_status,\n" +
            "sbb.bin_default, sbb.bin_name, sbb.bin_pol_no, sbb.bin_remarks, sbb.bin_sht_desc, sbb.bin_auth_status,\n" +
            "sbb.bin_type, sbb.bin_fund_binder, sbb.bin_max_exposure, sbb.bin_max_term, sbb.bin_medical_type, \n " +
            "sbb.bin_min_prem, sbb.bin_min_term, sbb.bin_prem_age_type, sbb.bin_acct_code, sbb.bin_cur_code,sbb.bin_pr_code, \n" +
            "sbb.bin_calc_type, sbb.bin_policy_url, sbb.bin_premium_url, sbb.bin_admin_fee_status \n" +
            " FROM sys_brk_binders sbb WHERE sbb.bin_type = 'B' AND sbb.bin_acct_code = :accId " +
            "AND sbb.bin_pr_code = :proCode AND " +
            "bin_default is true and sbb.bin_status is true", nativeQuery = true)
    List<BindersDef> findBinderByAccIdAndBinName(@Param("accId") Long accId, @Param("proCode") Long proCode);

    @Query(value = "SELECT sbb.bin_id, sbb.created_by, sbb.created_date, sbb.modified_by, sbb.modified_date, sbb.bin_status,\n" +
            "sbb.bin_default, sbb.bin_name, sbb.bin_pol_no, sbb.bin_remarks, sbb.bin_sht_desc, sbb.bin_auth_status,\n" +
            "sbb.bin_type, sbb.bin_fund_binder, sbb.bin_max_exposure, sbb.bin_max_term, sbb.bin_medical_type, \n " +
            "sbb.bin_min_prem, sbb.bin_min_term, sbb.bin_prem_age_type, sbb.bin_acct_code, sbb.bin_cur_code,sbb.bin_pr_code, \n" +
            "sbb.bin_calc_type, sbb.bin_policy_url, sbb.bin_premium_url, sbb.bin_admin_fee_status \n" +
            " FROM sys_brk_binders sbb WHERE sbb.bin_type = 'B'" +
            "AND sbb.bin_pr_code = :proCode AND sbb.bin_name = :binName and sbb.bin_status is true", nativeQuery = true)
    BindersDef findBinderByAccIdAndContractBinName(@Param("proCode") Long proCode, @Param("binName") String binName);

    @Query(value = "SELECT sbb.bin_id, sbb.created_by, sbb.created_date, sbb.modified_by, sbb.modified_date, sbb.bin_status,\n" +
            "sbb.bin_default, sbb.bin_name, sbb.bin_pol_no, sbb.bin_remarks, sbb.bin_sht_desc, sbb.bin_auth_status,\n" +
            "sbb.bin_type, sbb.bin_fund_binder, sbb.bin_max_exposure, sbb.bin_max_term, sbb.bin_medical_type, \n " +
            "sbb.bin_min_prem, sbb.bin_min_term, sbb.bin_prem_age_type, sbb.bin_acct_code, sbb.bin_cur_code,sbb.bin_pr_code, \n" +
            "sbb.bin_calc_type, sbb.bin_policy_url, sbb.bin_premium_url, sbb.bin_admin_fee_status \n" +
            "FROM sys_brk_binders sbb WHERE sbb.bin_type = 'B' AND sbb.bin_acct_code = :accId " +
            "AND sbb.bin_pr_code = :proCode AND LOWER(sbb.bin_name) = LOWER(:binName) and sbb.bin_status is true", nativeQuery = true)
    BindersDef findBinderByAccIdProDCodeActive(@Param("accId") Long accId, @Param("proCode") Long proCode, @Param("binName") String binName);

    @Query(value = "SELECT COUNT(*) FROM sys_brk_binders sbb WHERE sbb.bin_type = 'B' AND sbb.bin_acct_code = :accId AND sbb.bin_pr_code = :proCode", nativeQuery = true)
    int countBindersByAccId(@Param("accId") Long accId, @Param("proCode") Long proCode);

    @Query(value = "SELECT * FROM sys_brk_binders sbb WHERE sbb.bin_type = 'B' AND sbb.bin_id = :binId", nativeQuery = true)
    BindersDef findByBindersBybinid(@Param("binId") Long binId);
}
