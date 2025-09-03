package com.brokersystems.brokerapp.workflow.repository;

import com.brokersystems.brokerapp.workflow.docs.SysWfDocs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by HP on 7/30/2017.
 */
public interface SysWfDocsRepo extends PagingAndSortingRepository<SysWfDocs, Long>, QueryDslPredicateExecutor<SysWfDocs> {

    @Query(value = "select x.*, count(*) over() as total_count from (\n" +
            " select bwd_id, bwd_doc_active_process, case when sbp.pol_no = '' then sbp.pol_proposal_no else pol_no end pol_no, " +
            " sbc.client_fname, sbc.client_onames, sbu.user_username, bwd_created_dt, pol_id, 'P' as pol_type, " +
            " sbp.auth_comments as auth_comments\n" +
            " from sys_brk_wf_docs join sys_brk_policies sbp on bwd_pol_id = sbp.pol_id \n" +
            " left join sys_brk_clients sbc on sbc.client_id = sbp.pol_client_id \n" +
            " left join sys_brk_users sbu on sbu.user_id = sbp.pol_created_user \n" +
            " where bwd_active = true and bwd_user_id = :userid and (\n" +
            "   cast(bwd_id as text) like :search or \n" +
            "   lower(coalesce(bwd_doc_active_process, '')) like :search or \n" +
            "   lower(coalesce(pol_no, '')) like :search or \n" +
            "   lower(coalesce(sbp.pol_proposal_no, '')) like :search or \n" +
            "   lower(concat(coalesce(client_fname, ''), ' ', coalesce(nullif(client_onames, ''), ''))) like :search or \n" +
            "   lower(coalesce(sbu.user_username, '')) like :search or \n" +
            "   lower(coalesce(sbp.auth_comments, '')) like :search or \n" +
            "   cast(bwd_created_dt as text) like :search)\n" +
            " and sbp.pol_auth_status not in ('A')\n" +
            " union all \n" +
            " select bwd_id, bwd_doc_active_process, client_sht_desc, sbc.client_fname, sbc.client_onames, " +
            " sbu.user_username, bwd_created_dt, client_id, 'C' as client_clnt_type, null as auth_comments\n" +
            " from sys_brk_wf_docs join sys_brk_clients sbc on bwd_client_id = sbc.client_id\n" +
            " left join sys_brk_users sbu on sbu.user_id = sbc.client_auth_user\n" +
            " where bwd_active = true and bwd_user_id = :userid and (\n" +
            "   cast(bwd_id as text) like :search or \n" +
            "   lower(coalesce(bwd_doc_active_process, '')) like :search or \n" +
            "   lower(coalesce(client_sht_desc, '')) like :search or \n" +
            "   lower(concat(coalesce(client_fname, ''), ' ', coalesce(nullif(client_onames, ''), ''))) like :search or \n" +
            "   lower(coalesce(sbu.user_username, '')) like :search or \n" +
            "   cast(bwd_created_dt as text) like :search)\n" +
            " union all \n" +
            " select distinct bwd_id, bwd_doc_active_process, quot_no pol_no, " +
            " case when quot_clnt_type = 'C' then sbc.client_fname else sbcp.prs_fname end fname, " +
            " case when quot_clnt_type = 'C' then sbc.client_onames else sbcp.prs_onames end onames, " +
            " sbu.user_username, bwd_created_dt, quot_id pol_id, 'Q' as pol_type, null as auth_comments\n" +
            " from sys_brk_wf_docs join sys_brk_quotations sbp on bwd_quot_id = sbp.quot_id \n" +
            " left join sys_brk_clients sbc on sbc.client_id = sbp.quot_client_id \n" +
            " left join sys_brk_prospects sbcp on sbcp.prs_id = sbp.quot_prs_id \n" +
            " left join sys_brk_users sbu on sbu.user_id = sbp.quot_prep_user \n" +
            " left join sys_brk_quot_products sbqp on sbqp.quot_pr_quot_id = sbp.quot_id \n" +
            " where bwd_active = true \n" +
            " and ((sbp.quot_type = 'combined' and sbp.quot_id in(\n" +
            "   select quot_pr_quot_id from sys_brk_quot_products\n" +
            "   group by quot_pr_quot_id\n" +
            "   having sum(case when quot_pr_converted = 'Y' then 1 else 0 end) < count(*)))\n" +
            " or (sbp.quot_type = 'comparison' and not exists (\n" +
            "   select 1 from sys_brk_quot_products\n" +
            "   where quot_pr_quot_id = sbp.quot_id and quot_pr_converted = 'Y')))\n" +
            " and sbp.quot_status not in ('CL') \n" +
            " and bwd_user_id = :userid \n" +
            " and sbcp.prs_clnt_code is null \n" +
            " and (cast(bwd_id as text) like :search or \n" +
            "   lower(coalesce(bwd_doc_active_process, '')) like :search or \n" +
            "   lower(coalesce(quot_no, '')) like :search or \n" +
            "   lower(concat(\n" +
            "     case when quot_clnt_type = 'C' then coalesce(sbc.client_fname, '') else coalesce(sbcp.prs_fname, '') end, ' ', \n" +
            "     coalesce(nullif(case when quot_clnt_type = 'C' then sbc.client_onames else sbcp.prs_onames end, ''), ''))) like :search or \n" +
            "   lower(coalesce(sbu.user_username, '')) like :search or \n" +
            "   cast(bwd_created_dt as text) like :search)\n" +
            ") x order by bwd_created_dt desc offset :pageNo * :limit limit :limit", nativeQuery = true)
    List<Object[]> getDashBoardTickets(
            @Param("search") String search,
            @Param("userid") Long userid,
            @Param("pageNo") int pageNo,
            @Param("limit") int limit);

}
