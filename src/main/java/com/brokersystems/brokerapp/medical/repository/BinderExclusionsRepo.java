package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.BinderExclusions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 5/4/2017.
 */
public interface BinderExclusionsRepo  extends PagingAndSortingRepository<BinderExclusions, Long>, QueryDslPredicateExecutor<BinderExclusions> {

    @Query(value = "select ba_id,ba_desc,ba_sht_desc from sys_brk_ailments where (lower(ba_desc) like %:subName% or lower(ba_sht_desc) like %:subName%) and  ba_id not \n" +
            "in (select be_al_id from sys_brk_bin_exclusions where be_bin_id=:binCode and be_al_id is not  null)",nativeQuery = true)
    public List<Object[]> getBinderAilments(@Param("binCode") Long binCode,@Param("subName")String subName);
}
