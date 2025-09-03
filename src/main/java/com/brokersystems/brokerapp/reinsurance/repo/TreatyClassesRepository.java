package com.brokersystems.brokerapp.reinsurance.repo;

import com.brokersystems.brokerapp.reinsurance.model.TreatyClasses;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TreatyClassesRepository extends
        PagingAndSortingRepository<TreatyClasses, Long>, QueryDslPredicateExecutor<TreatyClasses> {

    @Query(value = "select tc_id, sbs.sub_desc ,tc_ret_limit,tc_fa_cede_rate,tc_min_eml,tc_ri_prem_tax_rate,tc_claim_limit,tc_insured_limit,\n" +
            "COUNT(*) OVER() AS total_rows from sys_brk_treaty_classes sbtp \n" +
            "join sys_brk_subclasses sbs  on sbs.sub_id  =tc_sc_code\n" +
            "where  (sbs.sub_desc like :search)\n" +
            "and tc_td_code = :treatyId\n" +
            " order by sbs.sub_desc  ASC \n" +
            " OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findTreatiesClasses(@Param("treatyId") Long treatyId,
                                       @Param("search") String search,
                                       @Param("pageNo") int pageNo,
                                       @Param("limit") int limit);
}
