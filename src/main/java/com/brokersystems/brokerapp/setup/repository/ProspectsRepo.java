package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.ProspectDef;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 4/9/2017.
 */
public interface ProspectsRepo extends PagingAndSortingRepository<ProspectDef, Long>, QueryDslPredicateExecutor<ProspectDef> {

    @Query(value = "select prs_id,prs_fname,prs_onames,prs_phone, COUNT(*) OVER() AS total_rows from sys_brk_prospects\n" +
            "where prs_status = 'A' and  (lower(prs_fname) like lower(:search) or lower(prs_onames) like lower(:search)\n" +
            "or prs_phone like :search)\n" +
            "AND prs_clnt_code IS NULL\n" +
            "order by prs_fname asc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findProspcts(@Param("search") String search,
                               @Param("pageNo") int pageNo,
                               @Param("limit") int limit);


    @Query(value = "select prs_id,prs_fname,prs_onames,prs_phone,prs_dob,  sbc.cnt_type_desc,sbc.cnt_type_id,prs_status,prs_category,sbb.ob_id,\n" +
            "sbb.ob_name,sba.acct_id,sba.acct_name,prs_comment,prs_sht_desc,prs_email,prs_gender,sbu.user_username,TO_CHAR(prs_dob, 'dd/mm/yyyy') date_ofBirth, COUNT(*) OVER() AS total_rows  from sys_brk_prospects sbp\n" +
            "join sys_brk_client_types sbc on sbp.prs_clnt_type  = sbc.cnt_type_id \n" +
            "left join sys_brk_branches sbb on sbb.ob_id  = sbp.prs_branch \n" +
            "left join sys_brk_accounts sba on sba.acct_id  = sbp.prs_sub_agent \n" +
            "join sys_brk_users sbu on sbu.user_id = sbp.prs_created_by \n" +
            "where (lower(prs_fname) like :search or lower(prs_onames) like :search)\n" +
            "and (:hasPermission = true or sbp.prs_created_by = :createdBy) \n" +
            "order by prs_fname asc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findProspctsListing(@Param("search") String search,
                                @Param("pageNo") int pageNo,
                                @Param("limit") int limit,
                                @Param("hasPermission")boolean hasPermission,
                                @Param("createdBy") Long createdBy);

    @Query(value = "select prs_id,prs_fname,prs_onames,prs_phone,prs_dob,  sbc.cnt_type_desc,sbc.cnt_type_id,prs_status,prs_category,sbb.ob_id,\n" +
            "sbb.ob_name,sba.acct_id,sba.acct_name,prs_comment,prs_sht_desc,prs_email,prs_gender,sbu.user_username  from sys_brk_prospects sbp\n" +
            "join sys_brk_client_types sbc on sbp.prs_clnt_type  = sbc.cnt_type_id \n" +
            "join sys_brk_users sbu on sbu.user_id = sbp.prs_created_by \n" +

            "left join sys_brk_branches sbb on sbb.ob_id  = sbp.prs_branch \n" +
            "left join sys_brk_accounts sba on sba.acct_id  = sbp.prs_sub_agent where prs_id=:prsId",nativeQuery = true)
    List<Object[]> findProspctsId(@Param("prsId") long prsId);

    @Query(value = "SELECT   prs_id                                 tenId,\n" +
            "         prs_sht_desc                           prospShtDesc,\n" +
            "         prs_fname                              fname,\n" +
            "         prs_onames                             otherNames,\n" +
            "         prs_idno                             idNo,\n" +
            "         COUNT(*) OVER() as total_rows\n" +
            "  from sys_brk_prospects\n" +
            "  where prs_status not in ('T')\n" +
            "    and (lower(concat(prs_fname,' ',prs_onames)) like lower(:search)\n" +
            "           or lower(concat(prs_onames,' ',prs_fname)) like lower(:search)\n" +
            "           or prs_idno like :search\n" +
            "           or lower(prs_sht_desc) like lower(:search)\n" +
            "            )\n" +
            "     order by  prs_fname      OFFSET :pageNo*:limit limit :limit ",nativeQuery = true)
    List<Object[]> searchProspectLists(@Param("search") String search,
                                      @Param("pageNo") int pageNo,
                                      @Param("limit") int limit);

}
