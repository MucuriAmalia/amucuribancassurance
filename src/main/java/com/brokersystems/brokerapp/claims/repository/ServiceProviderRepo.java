package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ServiceProviderDef;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceProviderRepo extends PagingAndSortingRepository<ServiceProviderDef, Long>, QueryDslPredicateExecutor<ServiceProviderDef> {

    @Query(value = "select provd_id,provd_email,provd_name,provd_mobile,a.created_date,provd_type_id,sbu.user_username,COUNT(*) OVER() AS total_rows from sys_brk_serv_providers a  \n" +
            " join sys_brk_users sbu on sbu.user_id =a.created_user where provd_type_id = :typeId and  (lower(provd_name) like :search)\n" +
            "order by provd_name asc OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findServProviders(@Param("search") String search,
                                     @Param("typeId") Long typeId,
                                     @Param("pageNo") int pageNo,
                                     @Param("limit") int limit);


    @Query(value = "select provd_id,provd_name,COUNT(*) OVER() AS total_rows from sys_brk_serv_providers  \n" +
            " where  (lower(provd_name) like :search)\n" +
            "order by provd_name asc OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
    List<Object[]> findServProvidersLov(@Param("search") String search,
                                     @Param("pageNo") int pageNo,
                                     @Param("limit") int limit);


}
