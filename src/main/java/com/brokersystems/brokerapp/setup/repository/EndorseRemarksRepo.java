package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.brokersystems.brokerapp.setup.model.EndorsementRemarks;

public interface EndorseRemarksRepo extends JpaRepository<EndorsementRemarks, Long>, QueryDslPredicateExecutor<EndorsementRemarks> {

}
