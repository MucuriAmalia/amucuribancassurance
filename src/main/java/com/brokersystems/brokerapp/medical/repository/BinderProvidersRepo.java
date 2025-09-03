package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.BinderProviders;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 5/5/2017.
 */
public interface BinderProvidersRepo extends PagingAndSortingRepository<BinderProviders, Long>, QueryDslPredicateExecutor<BinderProviders> {

    @Query(value = "select msp_id,msp_name,msp_cont_person from sys_brk_med_serv_prvds where msp_id not \n" +
            "in (select bp_al_id from sys_brk_bin_providers where bp_bin_id=:binCode)",nativeQuery = true)
    public List<Object[]> getBinderProviders(@Param("binCode") Long binCode);
}
