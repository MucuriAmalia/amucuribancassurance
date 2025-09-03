package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.SystemTransactionsTemp;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SystemTransactionsTempRepo extends PagingAndSortingRepository<SystemTransactionsTemp, Long>, QueryDslPredicateExecutor<SystemTransactionsTemp> {
    @Query("SELECT st.policy FROM SystemTransactionsTemp st WHERE st.tempTransno = :transno")
    PolicyTrans findPolicyByTransTempNo(@Param("transno") Long transno);

//    @Query("SELECT stt FROM SystemTransactionsTemp stt WHERE stt.policy.policyId = :policyId AND stt.clientType = 'C' AND stt.transdc = 'D'")
//    Optional<SystemTransactionsTemp> findByPolicyIdAndClientTypeAndTransdc(@Param("policyId") Long policyId);

    @Query(value = "SELECT stt FROM sys_brk_temp_transactions stt " +
            "WHERE stt.trans_pol_id = (" +
            "    SELECT sp.pol_id FROM sys_brk_policies sp " +
            "    WHERE sp.pol_id = :policyId" +
            ") " +
            "AND stt.trans_clnt_type = 'C' " +
            "AND stt.trans_dc = 'D'",
            nativeQuery = true)
    Optional<SystemTransactionsTemp> findByPolicyIdAndClientTypeAndTransdc(@Param("policyId") Long policyId);

}
