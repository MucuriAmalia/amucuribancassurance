package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.LabTest;
import com.brokersystems.brokerapp.medical.model.MedActivities;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 4/21/2017.
 */
public interface MedActivitiesRepo extends PagingAndSortingRepository<MedActivities, Long>, QueryDslPredicateExecutor<MedActivities> {
    @Query(value = "select ben_id,ben_sht_desc ,ben_desc,ben_act_type,ben_pre_authored,ba_ail_id,ba_sec_code  from sys_brk_med_activities  where (lower(ben_desc) like %:subName% or lower(ben_sht_desc) like %:subName%)  and ben_id not in (select spa_act_id from sys_brk_provider_activities where spa_sps_id=:serviceId )",nativeQuery = true)
    public List<MedActivities> getUnassignedActivities(@Param("serviceId") Long serviceId, @Param("subName") String subName);
}
