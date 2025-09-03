package com.brokersystems.brokerapp.uw.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.uw.model.PolicyTaxes;
import org.springframework.data.repository.query.Param;

public interface PolTaxesRepo extends  PagingAndSortingRepository<PolicyTaxes, Long>, QueryDslPredicateExecutor<PolicyTaxes> {


    @Query(value = "select count(1) from sys_brk_pol_taxes where risk_pol_id=:polId", nativeQuery = true)
    Integer countTotalTaxes(@Param("polId")Long policyId);

    @Modifying
    @Query(value = "delete from sys_brk_pol_taxes where risk_pol_id=:polId",nativeQuery = true)
    void deletePolTaxes(@Param("polId") Long policyId);

    @Query(value = "select count(1) from sys_brk_pol_taxes where risk_pol_id=:polId and pol_tax_sub_id=:sclId and pol_tax_rev_code=:revId", nativeQuery = true)
    Integer countIfPolicyTaxExist(@Param("polId")Long policyId,@Param("sclId")Long sclId,@Param("revId")Long revId);

}
