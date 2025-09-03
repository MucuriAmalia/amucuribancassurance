package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.AccountTypes;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AccountTypeRepo extends  PagingAndSortingRepository<AccountTypes, Long>, QueryDslPredicateExecutor<AccountTypes>{


    @Query(value = "select acc_id,acc_name,COUNT(*) OVER() as total_rows  from sys_brk_account_types sbat where acc_type ='SUB'\n" +
            "and  lower(acc_name) like :search\n" +
            "order by acc_name \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findIAAccountTypes( @Param("search") String search,
                                        @Param("pageNo") int pageNo,
                                        @Param("limit") int limit);

    @Query(value = "select acc_id,acc_name,acc_type,COUNT(*) OVER() AS total_rows  from sys_brk_account_types sbat \n" +
            " where lower(acc_name) like :search\n" +
            "order by acc_name \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllAccountTypes( @Param("search") String search,
                                       @Param("pageNo") int pageNo,
                                       @Param("limit") int limit);

    @Query(value = "select acc_id,acc_name,acc_type,COUNT(*) OVER() AS total_rows  from sys_brk_account_types sbat \n" +
            " where lower(acc_name) like :search\n" +
            "and acc_type IN ('INT', 'SUB', 'MRK')" +
            "order by acc_name \n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllsubComAccounts( @Param("search") String search,
                                    @Param("pageNo") int pageNo,
                                    @Param("limit") int limit);



}
