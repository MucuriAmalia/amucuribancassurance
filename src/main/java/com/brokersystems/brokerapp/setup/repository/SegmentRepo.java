package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.Occupation;
import com.brokersystems.brokerapp.setup.model.Segments;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.ldap.repository.Query;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SegmentRepo extends  PagingAndSortingRepository<Segments, Long>, QueryDslPredicateExecutor<Segments> {
    Optional<Segments> findBySegId(Long segId);

    @Query(value = "SELECT * FROM sys_brk_segments WHERE seg_segment = ?1", nativeQuery = true)
    Segments findbySegName(String seg);
}