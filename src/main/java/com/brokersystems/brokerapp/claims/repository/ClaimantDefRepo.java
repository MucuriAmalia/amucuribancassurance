package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.model.ClaimantsDef;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/5/2017.
 */
public interface ClaimantDefRepo extends PagingAndSortingRepository<ClaimantsDef, Long>, QueryDslPredicateExecutor<ClaimantsDef> {

    @Query(value = "select clmnt_id, clmnt_address, clmnt_idno, clmnt_mob_no, clmnt_othernames, clmnt_surname, " +
            "sbo.occ_name, sbo.occ_id, sys_brk_claimants.created_date, sbu.user_username, clmnt_email, " +
            "COUNT(*) OVER() as total_rows " +
            "from sys_brk_claimants " +
            "left join sys_brk_occupation sbo on sbo.occ_id = clmnt_occup " +
            "left join sys_brk_users sbu on sbu.user_id = sys_brk_claimants.created_user " +
            "where (lower(clmnt_idno) like :search or lower(clmnt_mob_no) like :search " +
            "or lower(coalesce(clmnt_othernames, '')) like :search or lower(clmnt_surname) like :search) " +
            "order by sys_brk_claimants.created_date desc OFFSET :pageNo * :limit LIMIT :limit",
            nativeQuery = true)
    List<Object[]> findClmants(@Param("search") String search,
                               @Param("pageNo") int pageNo,
                               @Param("limit") int limit);



}
