package com.brokersystems.brokerapp.life.repository;

import com.brokersystems.brokerapp.life.model.LifeReceiptAllocations;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * Created by waititu on 18/03/2019.
 */
public interface LifeReceiptAllocationsRepo extends PagingAndSortingRepository<LifeReceiptAllocations, Long>, QueryDslPredicateExecutor<LifeReceiptAllocations> {

    @Query(value = "select coalesce(max(due_date),CURRENT_DATE)  from sys_brk_life_installments sbli where lrct_policy_id=:policyId and " +
            "pi_installment_no in (select coalesce(sbli.pi_installment_no,-2000)+1   from sys_brk_life_installments sbli where install_paid ='Y' and lrct_policy_id=:policyId) ", nativeQuery = true)
    Date getPaidToDate(@Param("policyId") Long policyId);





}
