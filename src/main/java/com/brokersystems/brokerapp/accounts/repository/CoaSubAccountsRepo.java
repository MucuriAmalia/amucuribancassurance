package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by peter on 4/7/2017.
 */
public interface CoaSubAccountsRepo extends PagingAndSortingRepository<CoaSubAccounts, Long>, QueryDslPredicateExecutor<CoaSubAccounts> {

    @Query(value = "select co_id,co_code,co_name,COUNT(*) OVER() as total_rows  from sys_brk_coa_sub\n" +
            "where (lower(co_code) like :search or lower(co_name) like :search)\n" +
            "order by co_name asc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findAllSubAccounts(@Param("search") String search,
                                   @Param("pageNo") int pageNo,
                                   @Param("limit") int limit);

    @Query(value = "select co_id,co_accounts_order,co_code,co_name,co_int_account,co_control_acc,co_acct_type_id," +
            "acc_name,co_main_acc_id,co_mapped_to_scl,sub_desc,sub_cl_code,co_sap_gl_account,co_sap_gl_account_business,co_sap_gl_account_corporate, COUNT(*) OVER() as total_rows from sys_brk_coa_sub\n" +
            "left join sys_brk_account_types sbat on co_acct_type_id =sbat.acc_id \n" +
            "left join sys_brk_subclasses sbs on co_sc_id =sbs.sub_id \n" +
            "where co_main_acc_id =:mainAccId\n" +
            "and (lower(co_name) like :search or lower(co_code) like :search or lower(coalesce(acc_name,'')) like :search)\n" +
            "order by co_id desc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findMainSubAccounts(@Param("mainAccId") Long mainAccId,
                                       @Param("search") String search,
                                       @Param("pageNo") int pageNo,
                                       @Param("limit") int limit);

    @Query(value = "select co_id  from sys_brk_coa_sub\n" +
            "where co_control_acc ='Y'\n" +
            "and co_acct_type_id =:accTypeId",nativeQuery = true)
    List<BigInteger> getControlAccounts(@Param("accTypeId") Long accTypeId);

    @Query(value = "select main.co_name from sys_brk_coa_sub sub,sys_brk_coa_main main\n" +
            "where co_main_acc_id = main.co_id \n" +
            "where sub.co_code =:acctNo",nativeQuery = true)
    String getMainAccountName(@Param("acctNo") String acctNo);

}
