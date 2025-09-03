package com.brokersystems.brokerapp.users.repository;

import com.brokersystems.brokerapp.users.model.ModulesDef;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/17/2017.
 */
public interface ModulesRepo extends PagingAndSortingRepository<ModulesDef, Long>, QueryDslPredicateExecutor<ModulesDef> {
}
