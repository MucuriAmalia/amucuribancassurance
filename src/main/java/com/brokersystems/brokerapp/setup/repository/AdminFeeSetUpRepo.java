package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.AdminFeeSetUp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminFeeSetUpRepo extends PagingAndSortingRepository<AdminFeeSetUp, Long>, QueryDslPredicateExecutor<AdminFeeSetUp> {

    @Query(value = "select fc_id ,fc_excise_rate, fc_excise_rate_type,fc_vat_rate ,fc_vat_rate_type,fc_status,sys_brk_binders.bin_status,sys_brk_binders.bin_id,fc_admin_fee_rate,fc_admin_fee_rt_type,\n" +
            "COUNT(*) OVER() as total_rows  from sys_brk_admin_fee_config\n" +
            "join sys_brk_binders  on sys_brk_binders.bin_id  =sys_brk_admin_fee_config.fc_bin_code \n" +
            "where fc_bin_code = :binId\n" +
            "order by fc_excise_rate desc OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
    List<Object[]> findAllAdminFeeConfig( @Param("binId") Long binId,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);

}
