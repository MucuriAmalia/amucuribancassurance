package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.PremRatesDef;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;


public interface PremRatesRepo extends  PagingAndSortingRepository<PremRatesDef, Long>, QueryDslPredicateExecutor<PremRatesDef> {

    @Query("select p from PremRatesDef p where p.binderDet.detId = :detId and p.active=true and :rate between p.rangeFrom and p.rangeTo")
    public List<PremRatesDef> getPremiumRates(@Param("detId")Long detId, @Param("rate")BigDecimal rate);

    @Query("select p from PremRatesDef p where p.binderDet.detId = :detId and p.active=true and p.section.id=:secCode and :rate between p.rangeFrom and p.rangeTo")
    public List<PremRatesDef> getSectPremiumRates(@Param("detId")Long detId, @Param("rate")BigDecimal rate, @Param("secCode")Long secCode);


    @Query("select p from PremRatesDef p where p.binderDet.detId = :detId and p.active=true and p.section.id=:secCode")
    public List<PremRatesDef> getSectPremiumRates(@Param("detId")Long detId, @Param("secCode")Long secCode);
}
