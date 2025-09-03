package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.TrialBalanceTrans;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrialBalanceTransRepo extends PagingAndSortingRepository<TrialBalanceTrans,Long>, QueryDslPredicateExecutor<TrialBalanceTrans> {

    @Query(value = "select tbt_id from sys_brk_trial_bals where tbt_usr_id=:userId",nativeQuery = true)
    List<Long> findRecordsToDelete(@Param("userId") Long userId);

}
