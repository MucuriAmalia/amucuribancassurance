package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.EmbedPackageInsurance;
import com.brokersystems.brokerapp.bulktransactions.models.EmbedPackageInsuranceRisks;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EmbedPackageInsuranceRiskRepo extends PagingAndSortingRepository<EmbedPackageInsuranceRisks, Long>, QueryDslPredicateExecutor<EmbedPackageInsuranceRisks> {

    @Transactional
    @Modifying
    @Query("DELETE FROM EmbedPackageInsuranceRisks r WHERE r.embedPackageInsurance = :embedPackage")
    void deleteByPackage(@Param("embedPackage") EmbedPackageInsurance embedPackage);

}
