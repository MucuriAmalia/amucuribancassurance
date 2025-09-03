package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.RiskInterestedParties;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by HP on 9/13/2017.
 */
public interface RiskIntPartiesRepo extends PagingAndSortingRepository<RiskInterestedParties, Long>, QueryDslPredicateExecutor<RiskInterestedParties> {

    @Query(value = "select sbri.rid_id , part_name, case when sbip.part_type = 'P' then 'Premium Financier' when sbip.part_type = 'B' then 'Beneficiary' \n" +
            "when sbip.part_type = 'I' then 'Interested Party' end part_type,sbip.part_pin,sbip.part_reg_no,sbp.pol_auth_status,sbip.part_email_address,COUNT(*) OVER() as total_rows  \n" +
            "from sys_brk_rsk_ips sbri join sys_brk_int_parties sbip on sbri.rid_ip_id  = sbip.part_code \n" +
            "join sys_brk_risks sbr on sbr.risk_id  = sbri.rid_risk_id \n" +
            "join sys_brk_policies sbp on sbp.pol_id  = sbr.risk_pol_id \n" +
            "where  sbri.rid_risk_id =:riskId\n" +
            "and (lower(part_name) like :search)\n" +
            "order by part_name\n" +
            "OFFSET :pageNo*:limit limit :limit", nativeQuery = true)
    List<Object[]> findRiskAllInterestedParties(@Param("search") String search,
                                   @Param("riskId") Long riskId,
                                   @Param("pageNo") int pageNo,
                                   @Param("limit") int limit);

}
