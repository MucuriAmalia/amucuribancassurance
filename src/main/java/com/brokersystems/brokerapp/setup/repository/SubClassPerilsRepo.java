package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.PerilsDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.setup.model.SubclassPerils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 2/27/2017.
 */
public interface SubClassPerilsRepo  extends PagingAndSortingRepository<SubclassPerils, Long>, QueryDslPredicateExecutor<SubclassPerils> {

    @Query("select s from PerilsDef s where (lower(s.perilDesc) like %:subName% or lower(s.perilShtDesc) like %:subName%) and NOT EXISTS(select p from s.perils p where p.subclass.subId=:subCode)")
    public Page<PerilsDef> getUnassignedPerils(@Param("subCode")Long subCode, @Param("subName")String subName, Pageable pageable);

}
