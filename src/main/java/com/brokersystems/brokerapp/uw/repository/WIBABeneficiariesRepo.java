package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.WIBABeneficiaries;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WIBABeneficiariesRepo extends PagingAndSortingRepository<WIBABeneficiaries, Long>, QueryDslPredicateExecutor<WIBABeneficiaries> {

    @Query(value = "select bwb_id,bwb_emp_name,bwb_occupation,bwb_salary,COUNT(*) OVER() AS total_rows from sys_brk_wba_beneficiaries\n" +
            "where bwb_risk_id =:riskId\n" +
            "and (LOWER(bwb_emp_name) like :search or lower(bwb_occupation) like :search) \n" +
            "order by bwb_emp_name \n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findBeneficiaries(@Param("search") String search,
                               @Param("riskId") Long riskId,
                               @Param("pageNo") int pageNo,
                               @Param("limit") int limit);

}
