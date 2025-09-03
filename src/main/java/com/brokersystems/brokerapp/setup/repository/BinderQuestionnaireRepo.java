package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.BinderQuestionnaire;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 01/06/2019.
 */
public interface BinderQuestionnaireRepo extends PagingAndSortingRepository<BinderQuestionnaire, Long>, QueryDslPredicateExecutor<BinderQuestionnaire>{
}
