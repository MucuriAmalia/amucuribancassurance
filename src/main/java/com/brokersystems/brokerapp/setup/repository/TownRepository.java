package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.brokersystems.brokerapp.setup.model.County;
import com.brokersystems.brokerapp.setup.model.Town;

public abstract interface TownRepository
  extends JpaRepository<Town, Long>, QueryDslPredicateExecutor<Town>
{
  public  Page<Town> findByCtNameLikeIgnoreCaseAndCounty(String paramString, Pageable paramPageable, County paramCounty);
}
