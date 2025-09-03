package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ServiceProviderTypes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GIBServiceProviderTypesRepo extends PagingAndSortingRepository<ServiceProviderTypes, Long>, QueryDslPredicateExecutor<ServiceProviderTypes> {


    @Query(value = "select spr_tp_id,spr_type,COUNT(*) OVER() AS total_rows from sys_brk_provider_types \n" +
            "where (lower(spr_type) like :search)\n" +
            "order by spr_type asc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findServProviderTypes(@Param("search") String search,
                               @Param("pageNo") int pageNo,
                               @Param("limit") int limit);


    @Query(value = "select count(1)\n" +
            "from sys_brk_provider_types\n" +
            "where (lower(spr_type) = :search)",nativeQuery = true)
    long countExactServProviderTypes(@Param("search") String search);



}
