package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.ClientTypes;
import org.springframework.data.repository.query.Param;

public interface ClientTypeRepo extends  PagingAndSortingRepository<ClientTypes, Long>, QueryDslPredicateExecutor<ClientTypes> {
    @Query("SELECT p FROM ClientTypes p WHERE LOWER(REPLACE(p.typeDesc, ' ', '')) = LOWER(REPLACE(:name, ' ', ''))")
    ClientTypes findByNormalizeType(@Param("name") String name);

}
