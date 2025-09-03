package com.brokersystems.brokerapp.life.repository;

import com.brokersystems.brokerapp.life.model.PolicyBenefitsDistribution;
import com.brokersystems.brokerapp.life.model.PolicyInstallments;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PolicyInstallmentsRepo extends PagingAndSortingRepository<PolicyInstallments, Long>, QueryDslPredicateExecutor<PolicyInstallments> {


    @Modifying
    @Query(value = "update sys_brk_life_installments set pi_installment_no = :installNo  where lrct_policy_id = :policyId", nativeQuery = true)
    void updatePolicyInstallment(@Param("installNo") Long installNo,
                                        @Param("policyId") Long policyId);

}
