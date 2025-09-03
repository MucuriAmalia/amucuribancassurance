package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.model.ClaimUploads;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/11/2017.
 */
public interface ClaimUploadRepo extends PagingAndSortingRepository<ClaimUploads, Long>, QueryDslPredicateExecutor<ClaimUploads> {

    @Query(value = "select cu_id,cu_file_id,cu_file_name,cu_uploaded_dt,cu_comment,sbu.user_name,sbcb.clm_status, COUNT(*) OVER() AS total_rows\n" +
            "from sys_brk_clm_uploads\n" +
            "join sys_brk_users sbu on sbu.user_id  = cu_uploaded_by\n" +
            "join sys_brk_clm_bookings sbcb on sbcb.clm_id  =cu_clm_id \n" +
            "where cu_clm_id = :clmId\n" +
            "and cu_file_id like :search\n" +
            "order by cu_id\n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findSearchClaimUploads(@Param("clmId") Long clmId,
                                          @Param("search") String search,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit);

}
