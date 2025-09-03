package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.brokersystems.brokerapp.setup.model.Country;


@Repository
public interface CountryRepository extends PagingAndSortingRepository<Country, Long>, QueryDslPredicateExecutor<Country> {
	
	Page<Country> findByCouNameLikeIgnoreCase(String countryName,Pageable pageable);

}
