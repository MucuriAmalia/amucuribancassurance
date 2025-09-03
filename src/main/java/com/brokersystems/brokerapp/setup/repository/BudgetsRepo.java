package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.Budgets;
import com.brokersystems.brokerapp.setup.model.Budgets;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BudgetsRepo extends PagingAndSortingRepository<Budgets, Long>, QueryDslPredicateExecutor<Budgets> {

}
