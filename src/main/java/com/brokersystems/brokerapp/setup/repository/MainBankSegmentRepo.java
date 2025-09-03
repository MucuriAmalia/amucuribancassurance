package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.MainBankSegments;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MainBankSegmentRepo extends PagingAndSortingRepository<MainBankSegments, Long>, QueryDslPredicateExecutor<MainBankSegments> {

    Optional<MainBankSegments> findByMainBankSegId(Long mainBankSegId);

    @Query(value = "SELECT * FROM sys_brk_main_bank_segments WHERE main_bank_seg_name = ?1", nativeQuery = true)
    MainBankSegments findByMainBankSegName(String mainBankSegName);
}