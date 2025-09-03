package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.AccountYearPeriods;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountYearPeriodsRepo extends PagingAndSortingRepository<AccountYearPeriods, Long>, QueryDslPredicateExecutor<AccountYearPeriods> {

    @Query(value = "select yp_id , yp_period_name,yp_wef ,yp_wet,yp_state,yp_closed_date,sbu.user_username,yp_transacted,sbb.ob_name,COUNT(*) OVER() as total_rows \n" +
            "from sys_brk_account_yr_prds \n" +
            "left join sys_brk_users sbu on sbu.user_id =yp_closed_by\n" +
            "join sys_brk_branches sbb on sbb.ob_id =yp_ob_id\n" +
            "where yp_bay_id = :ypId " +
            " order by yp_wef asc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> searchAccountingYearsPeriods(@Param("ypId") Long ypId,
                                         @Param("pageNo") int pageNo,
                                         @Param("limit") int limit);

    @Query(value = "select yp_wef,yp_wet  from sys_brk_account_yr_prds,sys_brk_account_years sbay \n" +
            "where sbay.bay_id =yp_bay_id and yp_period_name=:period\n" +
            "and yp_ob_id =:obId \n" +
            "and sbay.bay_year =:yr",nativeQuery = true)
    List<Object[]> findMonthlyAcctPeriod(@Param("period") String period,
                                         @Param("obId") Long obId,
                                         @Param("yr") Long yr);

    @Query(value = "select yp_period_name  from sys_brk_account_yr_prds\n" +
            "join sys_brk_account_years sbay on  sbay.bay_id =yp_bay_id \n" +
            "where yp_ob_id =:obId \n" +
            "and sbay.bay_year =:yr\n" +
            "and yp_wef = sbay.bay_wef ",nativeQuery = true)
    String firstPeriodOfYr(@Param("obId") long branchId,@Param("yr") Long yr);

    @Query(value = "select yp_period_name  from sys_brk_account_yr_prds\n" +
            "join sys_brk_account_years sbay on  sbay.bay_id =yp_bay_id \n" +
            "where yp_ob_id =:obId \n" +
            "and sbay.bay_year =:yr\n" +
            "and yp_wet = sbay.bay_wet  ",nativeQuery = true)
    String lastPeriodOfYr(@Param("obId") long branchId,@Param("yr") Long yr);

}
