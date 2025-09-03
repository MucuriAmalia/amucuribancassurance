package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.LifeQuestions;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LifeQuestionsRepo extends PagingAndSortingRepository<LifeQuestions, Long>, QueryDslPredicateExecutor<LifeQuestions> {
}
