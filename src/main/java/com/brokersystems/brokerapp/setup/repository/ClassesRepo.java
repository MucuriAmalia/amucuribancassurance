package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.ClassesDef;
import com.brokersystems.brokerapp.setup.model.Country;


public interface ClassesRepo extends  PagingAndSortingRepository<ClassesDef, Long>, QueryDslPredicateExecutor<ClassesDef> {
	
	Page<ClassesDef> findByClDescLikeIgnoreCase(String countryName,Pageable pageable);

}
