package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.RelationshipTypes;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 25/04/2019.
 */
public interface RelationshipTypesRepo extends PagingAndSortingRepository<RelationshipTypes,Long>, QueryDslPredicateExecutor<RelationshipTypes> {
}
