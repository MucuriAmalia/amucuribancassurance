package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedServiceProviderTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 4/26/2017.
 */
public interface ServiceProviderTypesRepo  extends PagingAndSortingRepository<MedServiceProviderTypes, Long>, QueryDslPredicateExecutor<MedServiceProviderTypes> {

    @Query(value = "select s from MedServiceProviderTypes s where s.id \n" +
            "in (select a.serviceProviders.serviceProviderTypes.id from ServiceProviderContracts a where a.binder.binId=:binCode )")
    public Page<MedServiceProviderTypes> getBinderProviderTypes(@Param("binCode") Long binCode, Pageable pageable);
}
