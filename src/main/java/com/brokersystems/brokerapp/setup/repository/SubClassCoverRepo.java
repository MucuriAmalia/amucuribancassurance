package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.SubclassCoverTypes;
import org.springframework.data.repository.query.Param;

public interface SubClassCoverRepo extends  PagingAndSortingRepository<SubclassCoverTypes, Long>, QueryDslPredicateExecutor<SubclassCoverTypes> {

    @Query(value = "select count(1) from sys_brk_sub_covertypes sbsc where sc_sub_code=:subCode and sc_cov_code=:covtCode and sc_def_cover is true",nativeQuery = true)
    Long countDefault(@Param("subCode") Long subCode, @Param("covtCode") Long covtCode);

}
