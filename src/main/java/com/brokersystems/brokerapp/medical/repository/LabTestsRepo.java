package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.LabTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 4/21/2017.
 */
public interface LabTestsRepo extends PagingAndSortingRepository<LabTest, Long>, QueryDslPredicateExecutor<LabTest> {
    @Query(value = "select lab_id,lab_sht_desc,lab_desc, lab_cpt_codes,lab_upper_limit\n" +
            "from sys_brk_lab_tests \n" +
            "where (lower(lab_sht_desc) like %:subName% or lower(lab_desc) like %:subName%  or lower(lab_cpt_codes) like %:subName% ) and lab_id not in (select sps_lab_id from sys_brk_provd_services where sps_provider_id=:providerCode )",nativeQuery = true)
    public List<LabTest> getUnassignedServices(@Param("providerCode") Long providerCode,@Param("subName") String subName);
}
