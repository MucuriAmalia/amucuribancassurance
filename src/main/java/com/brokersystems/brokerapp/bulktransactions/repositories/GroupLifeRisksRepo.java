package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.GroupLife;
import com.brokersystems.brokerapp.bulktransactions.models.GroupLifeRisks;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GroupLifeRisksRepo extends PagingAndSortingRepository<GroupLifeRisks, Long>, QueryDslPredicateExecutor<GroupLifeRisks> {
    @Transactional
    @Modifying
    @Query("DELETE FROM GroupLifeRisks r WHERE r.groupLife = :groupLife")
    void deleteByPackage(@Param("groupLife") GroupLife groupLife);
}
