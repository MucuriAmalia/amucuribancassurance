package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.TrialBalances;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrialBalancesRepo extends PagingAndSortingRepository<TrialBalances,Long>, QueryDslPredicateExecutor<TrialBalances> {

    @Query(value = "select tb_id from sys_brk_trial_bals where tb_usr_id=:userId",nativeQuery = true)
    List<Long> findRecordsToDelete(@Param("userId") Long userId);

}
