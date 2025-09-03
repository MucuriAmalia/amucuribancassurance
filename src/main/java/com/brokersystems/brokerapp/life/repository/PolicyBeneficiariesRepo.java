package com.brokersystems.brokerapp.life.repository;

import com.brokersystems.brokerapp.life.model.PolicyBeneficiaries;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 */
public interface PolicyBeneficiariesRepo extends PagingAndSortingRepository<PolicyBeneficiaries,Long>,QueryDslPredicateExecutor<PolicyBeneficiaries> {

    @Query(value = "select count(1),coalesce(sum(pben_allocation),0) from sys_brk_pol_beneficiaries\n" +
            "where pben_pol_id =:polId",nativeQuery = true)
    List<Object[]> countBeneficiaries(@Param("polId") Long policyId);

}
