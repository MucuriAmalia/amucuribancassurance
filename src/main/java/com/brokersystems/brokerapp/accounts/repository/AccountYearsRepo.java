package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.AccountYears;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountYearsRepo extends PagingAndSortingRepository<AccountYears, Long>, QueryDslPredicateExecutor<AccountYears> {

    @Query(value = "select coalesce (max(bay_year)+1,cast( to_char(current_date, 'YYYY') as numeric)) from sys_brk_account_years",nativeQuery = true)
   Long getCurrentAccountingYear();

    @Query(value = "select count(1) from sys_brk_account_years where bay_state='O'",nativeQuery = true)
    Long countCurrentAccountingYear();

    @Query(value = "select bay_id,bay_state,bay_year,bay_wef,bay_wet,bay_prds,COUNT(*) OVER() as total_rows from sys_brk_account_years\n" +
            "where bay_ob_id =:branchId\n" +
            "and bay_year =:yr" +
            " order by bay_year desc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchAccountingYears(@Param("branchId") Long branchId,
                                         @Param("yr") Long yr,
                                         @Param("pageNo") int pageNo,
                                         @Param("limit") int limit);

    @Query(value = "select bay_id,bay_state,bay_year,bay_wef,bay_wet,bay_prds,COUNT(*) OVER() as total_rows from sys_brk_account_years\n" +
            "where bay_ob_id =:branchId\n" +
            " order by bay_year desc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> searchAccountingYearsNone(@Param("branchId") Long branchId,
                                         @Param("pageNo") int pageNo,
                                         @Param("limit") int limit);

}
