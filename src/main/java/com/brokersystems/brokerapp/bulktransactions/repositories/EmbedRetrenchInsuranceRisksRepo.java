package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.EmbedRetrenchInsurance;
import com.brokersystems.brokerapp.bulktransactions.models.EmbedRetrenchInsuranceRisks;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmbedRetrenchInsuranceRisksRepo extends PagingAndSortingRepository<EmbedRetrenchInsuranceRisks, Long>, QueryDslPredicateExecutor<EmbedRetrenchInsuranceRisks> {

    @Query(value = "SELECT * from sys_brk_embed_retrench_insurance_risks where retrench_package_id = :retrenchId", nativeQuery = true)
    List<EmbedRetrenchInsuranceRisks> findAllRiskByRetrenchInsurance(@Param("retrenchId") Long retrenchId);

    @Transactional
    @Modifying
    @Query("DELETE FROM EmbedRetrenchInsuranceRisks r WHERE r.retrenchInsurance = :retrenchIns")
    void deleteByPackage(@Param("retrenchIns") EmbedRetrenchInsurance retrenchInsurance);

}
