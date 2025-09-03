package com.brokersystems.brokerapp.customscreens.repository;

import com.brokersystems.brokerapp.customscreens.model.RegisteredTableModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegisteredTableModelRepo extends PagingAndSortingRepository<RegisteredTableModel, String>, QueryDslPredicateExecutor<RegisteredTableModel> {


    @Query(value = " select registered_table_name,application_table_name,category,app_table_key_value from x_registered_table " +
            "         where app_table_key_value =:subId",nativeQuery = true)
    List<RegisteredTableModel> getAllSchedules(@Param("subId") Long subId);

    @Query(value = "select registered_table_name,application_table_name,category,app_table_key_value,COUNT(*) OVER() AS total_rows " +
            "from x_registered_table where  app_table_key_value = :subId " +
            "AND (UPPER(registered_table_name) like UPPER(:search)) " +
            "ORDER BY registered_table_name " +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findRegTables(@Param("search") String search,
                                     @Param("subId") Long subId,
                                     @Param("pageNo") int pageNo,
                                     @Param("limit") int limit);

    RegisteredTableModel findByTableName(String tableName);

}
