package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimActivities;
import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 4/7/2017.
 */
public interface ClmActivitiesRepo extends PagingAndSortingRepository<ClaimActivities, Long>, QueryDslPredicateExecutor<ClaimActivities> {


    @Query(value = "select sbca.clm_act_id , sbc.ca_desc,sbu.user_username,sbca.clm_act_dt,sbca.clm_act_status,sbca.clm_act_rem_dt,clm_act_notes,COUNT(*) OVER() as total_rows from sys_brk_clm_activities sbca \n" +
            "join sys_brk_causations sbc on sbc.ca_id =sbca.clm_act_status_id \n" +
            "join sys_brk_users sbu on sbu.user_id  = sbca.clm_act_user_id \n" +
            "where sbca.clm_act_clm_id  = :clmId\n" +
            "and sbc.ca_desc like :search \n" +
            "order by sbca.clm_act_id asc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> getClmActivities(@Param("clmId") Long clmId,
                                    @Param("pageNo") int pageNo,
                                    @Param("search") String search,
                                    @Param("limit") int limit);


    ClaimActivities findTopByClaimBookingsOrderByActivityDateDesc(ClaimBookings claimBookings);

    List<ClaimActivities> findByClaimBookingsOrderByActivityDateDesc(ClaimBookings claimBookings);


}
