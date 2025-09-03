package com.brokersystems.brokerapp.setup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.brokersystems.brokerapp.setup.model.ShortPeriodRates;
import com.brokersystems.brokerapp.setup.model.SubClassDef;

public interface ShortPeriodRepo extends  PagingAndSortingRepository<ShortPeriodRates, Long>, QueryDslPredicateExecutor<ShortPeriodRates>{
	
	@Query("select s from ShortPeriodRates s where :coverDays between s.periodFrom and s.periodTo")
	public List<ShortPeriodRates> getShortPeriodRates(@Param("coverDays")Long coverDays);
	

}
