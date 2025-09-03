package com.brokersystems.brokerapp.dms.repo;

import com.brokersystems.brokerapp.dms.model.IntegrationUw;
import com.brokersystems.brokerapp.dms.model.SybrinCases;
import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import org.jboss.logging.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

public interface IntegrationUwRepo extends PagingAndSortingRepository<IntegrationUw, Long>, QueryDslPredicateExecutor<IntegrationUw> {

    //    @Query(value = "select  * from uw_doc_client_pol_no where pol_id = :polId", nativeQuery = true)
    //    IntegrationUw findByPolicyTransCode(@Param("polId") Long polId);

//    @Query(value = "SELECT * FROM uw_doc_client_pol_no WHERE uw_pol_id = :polId", nativeQuery = true)
//    IntegrationUw findByPolicyTransCode(@Param("polId") Long polId);
}
