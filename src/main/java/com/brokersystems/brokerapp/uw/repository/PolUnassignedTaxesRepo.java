package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.setup.model.TaxRates;
import com.brokersystems.brokerapp.uw.model.PolicyTaxes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by waititu on 01/10/2017.
 */
public interface PolUnassignedTaxesRepo extends PagingAndSortingRepository<TaxRates, Long>,QueryDslPredicateExecutor<TaxRates> {
    @Query(value = "select distinct tax_id,rev_code ,tax_rate_type,tax_level,tax_rate,tax_div_factor \n" +
            "from  sys_brk_tax_rates,sys_brk_revenue_items,sys_brk_pol_taxes\n" +
            "where rev_id = tax_rev_code\n" +
            "and tax_pro_id = :proId\n" +
            "and tax_sub_id = pol_tax_sub_id\n" +
            "and risk_pol_id = :polId\n" +
            "and tax_active = 'true'\n" +
            "and tax_rev_code not  in  (select pol_tax_rev_code from sys_brk_pol_taxes where risk_pol_id = :polId )",nativeQuery = true)
    public List<Object[]> getUnassignPolTaxes(@Param("polId") Long polId,  @Param("proId") Long proId);


    @Query(value = "select distinct tax_id,rev_code ,tax_rate_type,tax_level,tax_rate,tax_div_factor \n" +
            "from  sys_brk_tax_rates,sys_brk_revenue_items,sys_brk_mquot_taxes\n" +
            "where rev_id = tax_rev_code\n" +
            "and tax_pro_id = :proId\n" +
            "and tax_sub_id = mqt_tax_sub_id\n" +
            "and mqt_quot_id = :quotId\n" +
            "and tax_active = 'true'\n" +
            "and tax_rev_code not  in  (select mqt_tax_rev_code from sys_brk_mquot_taxes where mqt_quot_id = :quotId )",nativeQuery = true)
    public List<Object[]> getUnassignQuotTaxes(@Param("quotId") Long quotId,  @Param("proId") Long proId);
}
