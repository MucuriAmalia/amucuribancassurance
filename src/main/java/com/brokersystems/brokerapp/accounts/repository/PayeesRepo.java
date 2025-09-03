package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.Payees;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PayeesRepo extends PagingAndSortingRepository<Payees, Long>, QueryDslPredicateExecutor<Payees> {

    @Query(value = "select pay_id,pay_created_dt,pay_email,pay_full_name,pay_mobile,pay_tel_no,sbu.user_username,pay_status,COUNT(*) OVER() AS total_rows  from sys_brk_payees\n" +
            "join sys_brk_users sbu on sbu.user_id =pay_created_by\n" +
            "where (pay_full_name like :search or pay_email like :search or pay_mobile like :search)\n" +
            "order by pay_created_dt desc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchAllPayes(@Param("search") String search,
                                    @Param("pageNo") int pageNo,
                                    @Param("limit") int limit);

    @Query(value = "select pay_id,concat(pay_full_name,'-',payc_account_no)acct,COUNT(*) OVER() AS total_rows  from sys_brk_payees\n" +
            "join sys_brk_payee_accounts sbu on sbu.payc_pay_id =pay_id\n" +
            "where payc_status='A' and  (pay_full_name like :search or payc_account_no like :search)\n" +
            "order by pay_full_name desc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchAllPayesLov(@Param("search") String search,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);

    @Query(value = "select count(1) from sys_brk_payees where pay_email=:search", nativeQuery = true)
    int countPayesByEmail(@Param("search") String search);

}
