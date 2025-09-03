package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.AdminFeePolicies;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 6/6/2017.
 */
public interface AdminFeePolRepo extends PagingAndSortingRepository<AdminFeePolicies, Long>, QueryDslPredicateExecutor<AdminFeePolicies> {

    @Query(value = "select pol_id, pol_no,pol_rev_no,pol_ref_no from sys_brk_policies inner join sys_brk_binders on pol_binder_id = bin_id where pol_client_id = :clientId and pol_id \n" +
            "not in (select afp_pol_id from sys_brk_admin_policies where afp_af_id = :admiFeeid)\n" +
            "and pol_current_status='A' and pol_auth_status='A'",nativeQuery = true)
    public List<Object[]> getAdminFeePolicies(@Param("clientId") Long clientId, @Param("admiFeeid") Long admiFeeid);
}
