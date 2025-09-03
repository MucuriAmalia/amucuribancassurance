package com.brokersystems.brokerapp.certs.repository;

import com.brokersystems.brokerapp.certs.model.PolicyCerts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by usert1 on 2/1/2017.
 */
public interface PolicyCertsRepo  extends PagingAndSortingRepository<PolicyCerts, Long>, QueryDslPredicateExecutor<PolicyCerts> {

    @Query(value = "select c.pc_id,c.pc_sbccert_id,c.pc_status,c.pc_print_status,c.pc_wef,c.pc_wet,c.pc_cert_no,c.pc_reason_cancelled from sys_brk_policy_certs  c where c.pc_rsk_id=:riskId",nativeQuery = true)
    List<Object[]> QueryPolicyCert(@Param("riskId") Long riskId);
}
