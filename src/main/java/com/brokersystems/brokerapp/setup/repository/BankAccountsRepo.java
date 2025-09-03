package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.BankAccounts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BankAccountsRepo extends  PagingAndSortingRepository<BankAccounts, Long>, QueryDslPredicateExecutor<BankAccounts> {

    @Query(value = "select ba_id,ba_acct_name,ba_acct_no,ba_check_no ,ba_max_amount,ba_min_amount,ba_status,sbb.ob_name,sbb.ob_id,\n" +
            "sbbb.bb_name,sbbb.bb_id,sub.co_id,sub.co_code,sub.co_name, COUNT(*) OVER() as total_rows\n" +
            "from sys_brk_bnk_accounts\n" +
            "join sys_brk_branches sbb on sbb.ob_id =ba_sys_br_code\n" +
            "left join sys_brk_bank_branches sbbb on sbbb.bb_id =ba_branch_code\n" +
            "join sys_brk_coa_sub sub on sub.co_id  = ba_gl_acc \n" +
            "where ba_type = 'B' and  (lower(ba_acct_name) like :search or lower(ba_acct_no) like :search)\n" +
            "order by ba_acct_name desc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findBankAccnts(@Param("search") String search,
                               @Param("pageNo") int pageNo,
                               @Param("limit") int limit);

    @Query(value = "select ba_id,concat(ba_acct_name,' - ',ba_acct_no)acctno, COUNT(*) OVER() as total_rows\n" +
            "from sys_brk_bnk_accounts\n" +
            "where ba_type = 'B' and  (lower(ba_acct_name) like :search or lower(ba_acct_no) like :search)\n" +
            "order by ba_acct_name desc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findBankAccntsLov(@Param("search") String search,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);

    @Query(value = "select ba_gl_acc from sys_brk_bnk_accounts where ba_id=:baId",nativeQuery = true)
    Long getGlAcct(@Param("baId") Long baId);
}
