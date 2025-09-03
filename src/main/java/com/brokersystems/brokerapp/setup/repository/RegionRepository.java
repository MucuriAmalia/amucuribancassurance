package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.brokersystems.brokerapp.setup.model.OrgRegions;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RegionRepository extends JpaRepository<OrgRegions, Long>, QueryDslPredicateExecutor<OrgRegions> {

    @Query(value = "select reg_code, reg_desc ,COUNT(*) OVER() as total_rows  from sys_brk_regions \n" +
            " where lower(reg_desc) like :search" +
            " order by reg_desc desc OFFSET :pageNo*:limit limit :limit",nativeQuery = true)
    List<Object[]> findAllRegions(@Param("search") String search,
                                   @Param("pageNo") int pageNo,
                                   @Param("limit") int limit);
}
