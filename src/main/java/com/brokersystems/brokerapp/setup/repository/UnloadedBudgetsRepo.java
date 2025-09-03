package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.TaxRates;
import com.brokersystems.brokerapp.setup.model.UnloadedBudgets;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UnloadedBudgetsRepo extends PagingAndSortingRepository<UnloadedBudgets, Long>,QueryDslPredicateExecutor<UnloadedBudgets> {
}
