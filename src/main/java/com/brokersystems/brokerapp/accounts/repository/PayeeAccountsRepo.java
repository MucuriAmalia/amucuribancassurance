package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.PayeeAccounts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PayeeAccountsRepo extends PagingAndSortingRepository<PayeeAccounts, Long>, QueryDslPredicateExecutor<PayeeAccounts> {

    @Query(value = "select payc_id,payc_account_no,bb_name,payc_bb_id,sbb.bn_name,payc_status,COUNT(*) OVER() AS total_rows  from sys_brk_payee_accounts \n" +
            "            join sys_brk_bank_branches sbbb on sbbb.bb_id = payc_bb_id\n" +
            "            JOIN sys_brk_banks sbb on sbb.bn_id  = sbbb.bb_bn_id \n" +
            "            where payc_pay_id = :payeeId and  (payc_account_no like :search or bb_name like :search)\n" +
            "            order by payc_id desc \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchAllPayesAccounts( @Param("payeeId") Long payeeId,
                                   @Param("search") String search,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);

    @Query(value = "select c from PayeeAccounts c left join c.payees p where p.payId=:payId")
    List<PayeeAccounts> findOtherAccounts(@Param("payId") Long payId);

    @Query(value = "select payc_id from sys_brk_payee_accounts where payc_pay_id=:payId and payc_status='A'",nativeQuery = true)
    Long findActiveAccounts(@Param("payId") Long payId);
    @Query(value = "select count(1) from sys_brk_payee_accounts where payc_pay_id=:payId and payc_account_no=:accountNo",nativeQuery = true)
    int countAccountExists(@Param("payId") Long payId, @Param("accountNo") String accountNo);

    @Query(value = "select count(1) from sys_brk_payee_accounts where payc_pay_id=:payId and payc_status='A'",nativeQuery = true)
    int countActiveAccount(@Param("payId") Long payId);

}
