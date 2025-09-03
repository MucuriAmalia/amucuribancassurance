package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.LifeQuestionsChoices;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LifeQuestionsChoicesRepo extends PagingAndSortingRepository<LifeQuestionsChoices, Long>, QueryDslPredicateExecutor<LifeQuestionsChoices> {



}
