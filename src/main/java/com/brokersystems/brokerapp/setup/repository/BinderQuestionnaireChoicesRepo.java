package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.BinderQuestionnaireChoices;
import org.hibernate.id.insert.Binder;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 01/06/2019.
 */
public interface BinderQuestionnaireChoicesRepo extends PagingAndSortingRepository<BinderQuestionnaireChoices, Long>, QueryDslPredicateExecutor<BinderQuestionnaireChoices> {
}
