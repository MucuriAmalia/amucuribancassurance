package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.BinderLoadings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 5/4/2017.
 */
public interface BinderLoadingsRepo extends PagingAndSortingRepository<BinderLoadings, Long>, QueryDslPredicateExecutor<BinderLoadings> {

    @Query(value = "select ba_id,ba_desc,ba_sht_desc from sys_brk_ailments where ba_id not \n" +
            "in (select bl_al_id from sys_brk_bin_loadings where bl_bn_id=:binCode)",nativeQuery = true)
    public List<Object[]> getBinderAilments(@Param("binCode") Long binCode);
}
