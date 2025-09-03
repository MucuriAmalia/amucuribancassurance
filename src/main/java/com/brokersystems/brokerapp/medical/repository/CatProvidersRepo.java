package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.CatalogueProviders;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 5/5/2017.
 */
public interface CatProvidersRepo extends PagingAndSortingRepository<CatalogueProviders, Long>, QueryDslPredicateExecutor<CatalogueProviders> {

    @Query(value = "SELECT distinct msp_id,msp_name FROM sys_brk_med_serv_prvds a left outer join sys_brk_provd_contracts b\n" +
            "  on a.msp_id = b.spc_provider_id\n" +
            "where b.spc_bin_id=  :bindCode and a.msp_id not in\n" +
            "(select cp_provider_code from sys_brk_cat_providers where cp_cat_id=:catId)\n",nativeQuery =true)
    public List<Object[]> getUnassignedProviders(@Param("catId") Long catId, @Param("bindCode") Long bindCode);

}
