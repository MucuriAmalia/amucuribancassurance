package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.ProductGroupDef;
import org.springframework.data.repository.query.Param;


public interface ProductGroupRepo extends  PagingAndSortingRepository<ProductGroupDef, Long>, QueryDslPredicateExecutor<ProductGroupDef> {

    @Query("SELECT p FROM ProductGroupDef p WHERE LOWER(REPLACE(p.prgDesc, ' ', '')) = LOWER(REPLACE(:name, ' ', ''))")
    ProductGroupDef findByNormalizedName(@Param("name") String name);

}
