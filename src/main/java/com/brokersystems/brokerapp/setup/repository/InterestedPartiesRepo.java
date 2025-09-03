package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.InterestedParties;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by HP on 9/13/2017.
 */
public interface InterestedPartiesRepo extends PagingAndSortingRepository<InterestedParties, Long>, QueryDslPredicateExecutor<InterestedParties> {


    @Query(value = "select * from sys_brk_int_parties\n" +
            "where part_code not in (select rid_ip_id from sys_brk_rsk_ips where rid_risk_id=:riskId)",nativeQuery = true)
    List<InterestedParties> searchRiskInterestedParties(@Param("riskId") Long riskId);

}
