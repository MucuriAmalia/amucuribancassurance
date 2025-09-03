package com.brokersystems.brokerapp.setup.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.brokersystems.brokerapp.setup.model.Country;
import com.brokersystems.brokerapp.setup.model.County;

public abstract interface CountyRepository
  extends JpaRepository<County, Long>, QueryDslPredicateExecutor<County>
{
  public  Page<County> findByCountyNameLikeIgnoreCaseAndCountry(String paramString, Pageable paramPageable, Country paramCountry);
  
  
}
