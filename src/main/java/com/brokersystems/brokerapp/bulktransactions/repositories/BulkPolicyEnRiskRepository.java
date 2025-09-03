package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkPolEnCreation;
import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyCreation;
import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyENRisk;
import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyRisk;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BulkPolicyEnRiskRepository extends PagingAndSortingRepository<BulkPolicyENRisk, Long>, QueryDslPredicateExecutor<BulkPolicyENRisk> {

    @Query(value = "SELECT * FROM sys_brk_create_bulk_policy_en_risks WHERE bulk_policy_id = :bulkPolicy", nativeQuery = true)
    BulkPolicyENRisk findByBulkPolicy(@Param("bulkPolicy") Long bulkPolicy);

    List<BulkPolicyENRisk> findAllByBulkPolicy(BulkPolEnCreation bulkPolicy);
}
