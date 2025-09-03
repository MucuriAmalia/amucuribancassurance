package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.setup.dto.AccountsDTO;
import com.brokersystems.brokerapp.setup.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.AccountDef;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AccountRepo extends  PagingAndSortingRepository<AccountDef, Long>, QueryDslPredicateExecutor<AccountDef> {
    AccountDef findByAcctId(Long agent);

    List<AccountsDTO> findAccountDefByPinNo(String pinNo);

    @Query(value = "select acct_id,acct_sht_desc,acct_name,acct_phone,acct_pin,acct_status,acct_email,sys_brk_accounts.created_by,\n" +
            "     sys_brk_accounts.modified_by,sbat.acc_name , COUNT(*) OVER() as total_rows\n" +
            "            from sys_brk_accounts\n" +
            "            join sys_brk_account_types sbat on sbat.acc_id =acct_acc_code\n" +
            "            where  (lower(acct_name) like :search or lower(acct_sht_desc) like :search or lower(acc_name) like :search)\n" +
            "            and sbat.acc_id = :accId \n" +
            "            order by acct_name \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllAccounts(@Param("accId") Long accId,
                                   @Param("search") String search,
                                   @Param("pageNo") int pageNo,
                                   @Param("limit") int limit);

    @Query(value = "select acct_id,acct_name,COUNT(*) OVER() AS total_rows\n" +
            "from sys_brk_accounts  \n" +
            "where acct_acc_code in (select sbat.acc_id  from sys_brk_account_types sbat where sbat.acc_type in ('INS','IA'))\n" +
            "and  (lower(acct_name) like :search or lower(acct_sht_desc) like :search) AND acct_status='A' \n" +
            "order by acct_name \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findParentAccountTypes( @Param("search") String search,
                                           @Param("pageNo") int pageNo,
                                           @Param("limit") int limit);

    @Query(value = "select acct_id,acct_name,COUNT(*) OVER() as total_rows\n" +
            "from sys_brk_accounts  \n" +
            "where acct_acc_code in (select sbat.acc_id  from sys_brk_account_types sbat where sbat.acc_type in ('BRK'))\n" +
            "and  (lower(acct_name) like :search or lower(acct_sht_desc) like :search)\n" +
            "order by acct_name \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findBrokerAccountTypes( @Param("search") String search,
                                           @Param("pageNo") int pageNo,
                                           @Param("limit") int limit);

    @Query(value = "select acct_id,acct_name,COUNT(*) OVER() as total_rows\n" +
            "from sys_brk_accounts  \n" +
            "where acct_acc_code in (select sbat.acc_id  from sys_brk_account_types sbat where sbat.acc_type in ('RN'))\n" +
            "and  (lower(acct_name) like :search or lower(acct_sht_desc) like :search)\n" +
            "order by acct_name \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findReinsuranceAccountTypes( @Param("search") String search,
                                           @Param("pageNo") int pageNo,
                                           @Param("limit") int limit);

    @Query(value = "SELECT sys_brk_accounts.acct_id, sys_brk_accounts.created_by, sys_brk_accounts.created_date, sys_brk_accounts.modified_by, sys_brk_accounts.modified_date, \n" +
            "sys_brk_accounts.acct_address, sys_brk_accounts.acct_bnk_account, \n" +
            "sys_brk_accounts.acct_contact_person, sys_brk_accounts.acct_contact_title, sys_brk_accounts.acct_dob, sys_brk_accounts.acct_email, \n" +
            "sys_brk_accounts.acct_license_no, sys_brk_accounts.acct_name, sys_brk_accounts.acct_pay_tel_no, \n" +
            "sys_brk_accounts.acct_paybill, sys_brk_accounts.acct_phone, sys_brk_accounts.acct_phys_address,sys_brk_accounts.acct_pin,\n" +
            "sys_brk_accounts.acct_sht_desc, sys_brk_accounts.acct_status, sys_brk_accounts.acct_wef, \n" +
            "sys_brk_accounts.acct_wet, sys_brk_accounts.acct_acc_code, sys_brk_accounts.acct_branch_code, sys_brk_accounts.acct_bnk_code,\n" +
            "sys_brk_accounts.acct_brn_code, sys_brk_accounts.acct_pmode_id, sys_brk_accounts.acct_logo_url, sys_brk_accounts.acct_parent_acct_id,\n" +
            "sbb.bn_name,sbbb.bb_name,sbpm.pm_desc,sbb2.ob_name ,sbat.acc_name,sbat.acc_type ,sba.acct_name acctypeName,\n" +
            "    coa_sub_receivable.co_code AS receivable_co_code, \n" +
            "    coa_sub_receivable.co_name AS receivable_co_name, \n" +
            "    coa_sub_payable.co_code AS payable_co_code, \n" +
            "    coa_sub_payable.co_name AS payable_co_name,\n" +
            "sys_brk_accounts.acct_receivable_acct, sys_brk_accounts.acct_payable_acct,\n" +
            "    coa_sub_whtx.co_code AS whtx_co_code, \n" +
            "    coa_sub_whtx.co_name AS whtx_co_name, \n" +
            "    coa_sub_comm.co_code AS comm_co_code, \n" +
            "    coa_sub_comm.co_name AS comm_co_name,\n" +
            "    coa_sub_admin.co_code AS admin_co_code, \n" +
            "    coa_sub_admin.co_name AS admin_co_name,\n" +
            "sys_brk_accounts.acct_whtx_acct,sys_brk_accounts.acct_comm_acct,sys_brk_accounts.acct_admin_acct\n," +
            "sys_brk_accounts.acct_comm_earning, sys_brk_accounts.acct_absa_no, sys_brk_accounts.acct_id_number, \n" +
            "sys_brk_accounts.insurance_type, \n" +
            "coa_sub_whtx_admin.co_code AS admin_whtx_co_code, \n" +
            "coa_sub_whtx_admin.co_name AS admin_whtx_co_name\n" +
            "FROM sys_brk_accounts\n" +
            "left join sys_brk_banks sbb ON sbb.bn_id  = sys_brk_accounts.acct_bnk_code\n" +
            "left join sys_brk_bank_branches sbbb on sbbb.bb_id  = sys_brk_accounts.acct_branch_code\n" +
            "left join sys_brk_payment_modes sbpm on sbpm.pm_id  = sys_brk_accounts.acct_pmode_id\n" +
            "left join sys_brk_branches sbb2 on sbb2.ob_id  = sys_brk_accounts.acct_brn_code\n" +
            "left join sys_brk_account_types sbat on sbat.acc_id  = sys_brk_accounts.acct_acc_code\n" +
            "left join sys_brk_accounts sba on sba.acct_id  = sys_brk_accounts.acct_parent_acct_id\n" +
            "LEFT JOIN sys_brk_coa_sub AS coa_sub_receivable \n" +
            "    ON coa_sub_receivable.co_id = sys_brk_accounts.acct_receivable_acct\n" +
            "LEFT JOIN sys_brk_coa_sub AS coa_sub_payable \n" +
            "    ON coa_sub_payable.co_id = sys_brk_accounts.acct_payable_acct \n" +
            "LEFT JOIN sys_brk_coa_sub AS coa_sub_whtx \n" +
            "    ON coa_sub_whtx.co_id = sys_brk_accounts.acct_whtx_acct\n" +
            "LEFT JOIN sys_brk_coa_sub AS coa_sub_comm \n" +
            "    ON coa_sub_comm.co_id = sys_brk_accounts.acct_comm_acct \n" +
            "LEFT JOIN sys_brk_coa_sub AS coa_sub_admin \n" +
            "    ON coa_sub_admin.co_id = sys_brk_accounts.acct_admin_acct \n" +
            "LEFT JOIN sys_brk_coa_sub AS coa_sub_whtx_admin \n" +
            "    ON coa_sub_whtx_admin.co_id = sys_brk_accounts.acct_admin_whtx_acct \n" +
            "where sys_brk_accounts.acct_id =:acctId", nativeQuery = true)
    List<Object[]> findOneAccount( @Param("acctId") Long acctId);

    @Query("SELECT a FROM AccountDef a WHERE UPPER(a.shtDesc) =UPPER(:name)")
    AccountDef findByNormalizedName(@Param("name") String name);

    @Query("SELECT a.acctId FROM AccountDef a WHERE UPPER(a.absaNo) =UPPER(:abno) AND UPPER(a.accountType.accountType) = 'MRK'")
    AccountDef findByMarketerABNo(@Param("abno") String abno);

    @Query("SELECT a.acctId FROM AccountDef a WHERE UPPER(a.absaNo) =UPPER(:abno) AND UPPER(a.accountType.accountType) ='SUB'")
    AccountDef findBySubAgentABNo(@Param("abno") String abno);

    @Query("SELECT a FROM AccountDef a WHERE UPPER(a.absaNo) =UPPER(:abno) AND UPPER(a.accountType.accountType) ='SUB'")
    AccountDef findBulkSubAgentBySubAgentABNo(@Param("abno") String abno);

    @Query("SELECT a.id FROM User a WHERE UPPER(a.absaNo) =UPPER(:abno)")
    User findByLeadsABNO(@Param("abno") String abno);

    // native query for introducer agents
    @Query(value = "SELECT a.acct_id, a.acct_name, a.acct_sht_desc, a.acct_phone, a.acct_email, " +
            "a.acct_pin, a.acct_status, a.acct_absa_no, sbat.acc_name, sbat.acc_id, " +
            "COUNT(*) OVER() as total_rows " +
            "FROM sys_brk_accounts a " +
            "JOIN sys_brk_account_types sbat ON sbat.acc_id = a.acct_acc_code " +
            "WHERE sbat.acc_type = 'SUB' " +
            "AND (lower(a.acct_name) LIKE :search " +
            "    OR lower(a.acct_sht_desc) LIKE :search " +
            "    OR lower(a.acct_absa_no) LIKE :search) " +
            "ORDER BY a.acct_name " +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllInhouseAgents(@Param("search") String search,
                                        @Param("pageNo") int pageNo,
                                        @Param("limit") int limit);

    // native query for introducer agents
    @Query(value = "SELECT a.acct_id, a.acct_name, a.acct_sht_desc, a.acct_phone, a.acct_email, " +
            "a.acct_pin, a.acct_status, a.acct_absa_no, sbat.acc_name, " +
            "COUNT(*) OVER() as total_rows " +
            "FROM sys_brk_accounts a " +
            "JOIN sys_brk_account_types sbat ON sbat.acc_id = a.acct_acc_code " +
            "WHERE sbat.acc_type = 'INT' " +
            "AND (lower(a.acct_name) LIKE :search " +
            "    OR lower(a.acct_sht_desc) LIKE :search " +
            "    OR lower(a.acct_absa_no) LIKE :search) " +
            "ORDER BY a.acct_name " +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllIntroducerAgents(@Param("search") String search,
                                           @Param("pageNo") int pageNo,
                                           @Param("limit") int limit);


    @Query(value = "SELECT a.acct_id, a.acct_name, a.acct_sht_desc, a.acct_absa_no, " +
            "COUNT(*) OVER() as total_rows " +
            "FROM sys_brk_accounts a " +
            "JOIN sys_brk_account_types sbat ON sbat.acc_id = a.acct_acc_code " +
            "WHERE sbat.acc_type = 'INS' " +
            "AND (:search IS NULL OR lower(a.acct_name) LIKE CAST(:search AS text)) " +
            "AND (:insuranceType IS NULL OR a.insurance_type = CAST(:insuranceType AS text)) " +
            "ORDER BY a.acct_name " +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findInsuranceAccounts(@Param("search") String search,
                                         @Param("insuranceType") String insuranceType,
                                         @Param("pageNo") int pageNo,
                                         @Param("limit") int limit);

}
