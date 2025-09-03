package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyCreation;
import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyRisk;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BulkPolicyRiskRepository extends PagingAndSortingRepository<BulkPolicyRisk, Long>, QueryDslPredicateExecutor<BulkPolicyRisk> {

    @Query(value = "SELECT * FROM sys_brk_create_bulk_policy_risks WHERE bulk_policy_id = :bulkPolicy", nativeQuery = true)
    BulkPolicyRisk findByBulkPolicy(@Param("bulkPolicy") Long bulkPolicy);

    List<BulkPolicyRisk> findAllByBulkPolicy(BulkPolicyCreation bulkPolicy);
}
