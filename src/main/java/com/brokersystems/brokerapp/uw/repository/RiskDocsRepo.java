package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.RiskDocs;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by HP on 8/14/2017.
 */
public interface RiskDocsRepo extends PagingAndSortingRepository<RiskDocs, Long>, QueryDslPredicateExecutor<RiskDocs> {


    @Query("select d from RiskDocs d where d.rdId =:docId")
    public RiskDocs getRiskDocsVal(@Param("docId")Long docId);


    @Query(value = "select a.rd_id,a.rd_member_id,a.rd_risk_id,c.pol_id,a.rd_req_id ,a.rd_loc_name, a.rd_content_type,b.risk_binder_det_id " +
            "from sys_brk_rsk_docs a ,sys_brk_risks b,sys_brk_policies c " +
            "where a.rd_id =:docId and b.risk_id= a.rd_risk_id " +
            "and b.risk_pol_id = c.pol_id",nativeQuery = true)
    List<Object[]>  getRiskDocsVal2(@Param("docId")Long docId);

    @Modifying
    @Query(value = " delete from sys_brk_rsk_docs   where rd_id =:docId ",nativeQuery = true)
    void   deleteRiskDocs(@Param("docId")Long docId);

    @Query(value = "select a.rd_id,a.rd_req_id,a.rd_verifier,c.pol_rev_no,a.rd_loc_name " +
            "from sys_brk_rsk_docs a ,sys_brk_risks b,sys_brk_policies c " +
            "where a.rd_risk_id =:riskId and b.risk_id= a.rd_risk_id " +
            "and b.risk_pol_id = c.pol_id",nativeQuery = true)
    List<Object[]> getRiskDocs(@Param("riskId")Long riskId);

    @Query(value = "select sbp.pol_rev_no , sbrd.rd_id,sbrd.rd_verifier,sbrd.rd_content_type,sbrd.rd_loc_name,sbrd.rd_url,\n" +
            "sbrd2.req_sht_desc,sbrd2.req_desc,sbp.pol_id,sbp.pol_auth_status,rd_initiator,rd_approver,rd_approval_date, rd_creation_date,sbrd.rd_comments,sbrd.rd_verified_by, sbrd.rd_verified_date,sbrd.document_source, COUNT(*) OVER() AS total_rows \n" +
            "from sys_brk_rsk_docs sbrd \n" +
            "join sys_brk_subclass_req_docs sbsrd on sbsrd.srq_id  = sbrd.rd_req_id \n" +
            "join sys_brk_req_docs sbrd2 on sbrd2.req_id = sbsrd.srq_req_code \n" +
            "join sys_brk_risks sbr on sbr.risk_id = sbrd.rd_risk_id \n" +
            "join sys_brk_policies sbp on sbp.pol_id  = sbr.risk_pol_id \n" +
            "where sbrd.rd_risk_id  = :riskId\n" +
            "and (sbrd.document_source is null or sbrd.document_source <> 'REFUND')" +
            "order by sbrd2.req_desc asc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> getAllRiskDocs(@Param("riskId") Long riskId,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);

    @Query(value = "select sbp.pol_rev_no , sbrd.rd_id,sbrd.rd_verifier,sbrd.rd_content_type,sbrd.rd_loc_name,sbrd.rd_url,\n" +
            "sbrd2.req_sht_desc,sbrd2.req_desc,sbp.pol_id,sbp.pol_auth_status,rd_initiator,rd_approver,rd_approval_date, rd_creation_date,sbrd.rd_comments,sbrd.rd_verified_by, sbrd.rd_verified_date,sbrd.document_source, COUNT(*) OVER() AS total_rows \n" +
            "from sys_brk_rsk_docs sbrd \n" +
            "join sys_brk_subclass_req_docs sbsrd on sbsrd.srq_id  = sbrd.rd_req_id \n" +
            "join sys_brk_req_docs sbrd2 on sbrd2.req_id = sbsrd.srq_req_code \n" +
            "join sys_brk_risks sbr on sbr.risk_id = sbrd.rd_risk_id \n" +
            "join sys_brk_policies sbp on sbp.pol_id  = sbr.risk_pol_id \n" +
            "where sbrd.rd_risk_id  = :riskId\n" +
            "and sbrd.document_source = 'REFUND' " +
            "order by sbrd2.req_desc asc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> getAllRiskRefundDocs(@Param("riskId") Long riskId,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);

    @Query(value = "select sbp.pol_rev_no , sbrd.rd_id,sbrd.rd_verifier,sbrd.rd_content_type,sbrd.rd_loc_name,sbrd.rd_url,\n" +
            "sbrd2.req_sht_desc,sbrd2.req_desc,sbp.pol_id,sbp.pol_auth_status,rd_initiator,rd_approver,rd_approval_date, rd_creation_date,sbrd.rd_comments,sbrd.rd_verified_by, sbrd.rd_verified_date, COUNT(*) OVER() AS total_rows \n" +
            "from sys_brk_rsk_docs sbrd \n" +
            "join sys_brk_subclass_req_docs sbsrd on sbsrd.srq_id  = sbrd.rd_req_id \n" +
            "join sys_brk_req_docs sbrd2 on sbrd2.req_id = sbsrd.srq_req_code \n" +
            "join sys_brk_risks sbr on sbr.risk_id = sbrd.rd_risk_id \n" +
            "join sys_brk_policies sbp on sbp.pol_id  = sbr.risk_pol_id \n" +
            "where sbrd.rd_risk_id  = :riskId\n" +
            "order by sbrd2.req_desc asc", nativeQuery = true)
    List<Object[]> getAllRiskDocsAttachment(@Param("riskId") Long riskId);

    @Query("select r from RiskDocs r where r.risk.riskId= :riskId")
    Iterable<RiskDocs> findRiskDocsById(@Param("riskId") Long riskId);
}
