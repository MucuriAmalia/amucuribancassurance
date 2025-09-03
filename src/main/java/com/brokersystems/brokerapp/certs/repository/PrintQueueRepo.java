package com.brokersystems.brokerapp.certs.repository;

import com.brokersystems.brokerapp.certs.model.PrintCertificateQueue;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
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
public interface PrintQueueRepo extends PagingAndSortingRepository<PrintCertificateQueue, Long>, QueryDslPredicateExecutor<PrintCertificateQueue> {
    @Query("select t from PrintCertificateQueue t where (t.policyCerts.risk.policy.branch.obId=:brnCode) " +
            "and (t.subclassCertTypes.subclasscertId=:certCode) " +
            "and (t.policyCerts.risk.policy.binder.account.acctId=:acctCode or :acctCode is null )"+
            "and (t.status=:certStatus or :certStatus is null )"+
            "and (upper(t.policyCerts.risk.policy.polNo) like %:polNo% or :polNo is null )"+
            "and (upper(t.policyCerts.risk.riskShtDesc) like %:riskId% or :riskId is null )")
    public Page<PrintCertificateQueue> getPrintCertificateQueue(@Param("brnCode") Long brnCode,
                                                                @Param("certCode") Long certCode,
                                                                @Param("acctCode") Long acctCode,
                                                                @Param("certStatus") String certStatus,
                                                                @Param("polNo") String polNo,
                                                                @Param("riskId")String riskId,
                                                                Pageable pageable);

    @Query(value = "select cq_id,cq_date_time,sbr.risk_sht_desc,sbpc.pc_wef,sbpc.pc_wet,cq_status,sbu.user_username,cq_cert_no,COUNT(*) OVER() as total_rows  from sys_brk_cert_queue\n" +
            "join sys_brk_risks sbr \n" +
            "on cq_rsk_id = sbr.risk_id \n" +
            "join sys_brk_policy_certs sbpc on cq_pc_id = sbpc.pc_id \n" +
            "left join sys_brk_users sbu on cq_alloc_by = sbu.user_id \n" +
            "where sbr.risk_pol_id  = :polId order by cq_date_time asc  OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    public List<Object[]> getPolCertToPrint(@Param("polId") Long polId,
                                            @Param("pageNo") int pageNo,
                                            @Param("limit") int limit);


    @Query(value = "select cq_status,cq_cert_no from sys_brk_cert_queue where cq_id=:certId",nativeQuery = true)
    List<Object[]> SearchCertsById(@Param("certId") Long certId);

    @Query(value = "select cq_aki_cert_no from sys_brk_cert_queue where cq_rsk_id=:riskId",nativeQuery = true)
    String getCertNo(@Param("riskId") Long riskId);

    List<PrintCertificateQueue> findAllByRisk(RiskTrans riskTrans);

}
