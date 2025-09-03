package com.brokersystems.brokerapp.reinsurance.repo;

import com.brokersystems.brokerapp.reinsurance.model.TreatyParticipants;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TreatyParticipantsRepository extends
        PagingAndSortingRepository<TreatyParticipants, Long>, QueryDslPredicateExecutor<TreatyParticipants> {


    @Query(value = "select tp_id,tp_broker_type,tp_rein_rate,tp_rec_rate,tp_tax_chargeable_type,\n" +
            "tp_tax_rate,tp_tax_rate_type,sba.acct_name broker, sba2.acct_name participant,\n" +
            "sbri.rev_code, COUNT(*) OVER() as total_rows from sys_brk_treaty_participants sbtp \n" +
            "left join sys_brk_accounts sba on sba.acct_id =tp_br_code\n" +
            "join sys_brk_accounts sba2 on sba2.acct_id =tp_ac_code\n" +
            "join sys_brk_revenue_items sbri on sbri.rev_id =tp_rev_code\n" +
            "where  (sba.acct_name like :search or sba2.acct_name like :search)\n" +
            "and tp_td_code = :treatyId\n" +
            " order by sba2.acct_name  ASC \n" +
            " OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findTreatiesParticipants(@Param("treatyId") Long treatyId,
                                            @Param("search") String search,
                                            @Param("pageNo") int pageNo,
                                            @Param("limit") int limit);

}
