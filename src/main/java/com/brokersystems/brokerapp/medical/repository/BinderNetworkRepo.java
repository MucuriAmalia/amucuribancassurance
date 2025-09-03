package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedicalNetworks;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by waititu on 05/06/2017.
 */
public interface BinderNetworkRepo extends PagingAndSortingRepository<MedicalNetworks, Long>, QueryDslPredicateExecutor<MedicalNetworks> {
    @Query(value = "select ben_id,ben_desc,ben_sht_desc from sys_brk_networks where (lower(ben_desc) like %:subName% or lower(ben_sht_desc) like %:subName%) and ben_id " +
            "not in (select be_ben_id from sys_brk_bin_exclusions where be_bin_id=:binCode  and be_ben_id is  not null)",nativeQuery = true)
    public List<Object[]> getBinderNetworks(@Param("binCode") Long binCode,@Param("subName")String subName);
}
