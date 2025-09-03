package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.PolicyQuestionnaire;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 07/06/2019.
 */
public interface PolicyQuestionnaireRepo extends PagingAndSortingRepository<PolicyQuestionnaire, Long>, QueryDslPredicateExecutor<PolicyQuestionnaire> {
}
