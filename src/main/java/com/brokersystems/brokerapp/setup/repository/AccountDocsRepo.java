package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.AccountsDocs;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 10/7/2017.
 */
public interface AccountDocsRepo extends PagingAndSortingRepository<AccountsDocs, Long>, QueryDslPredicateExecutor<AccountsDocs> {
}
