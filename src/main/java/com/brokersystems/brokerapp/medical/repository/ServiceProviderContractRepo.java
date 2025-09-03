package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedServiceProviderTypes;
import com.brokersystems.brokerapp.medical.model.ServiceProviderContracts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by peter on 5/12/2017.
 */
public interface ServiceProviderContractRepo extends PagingAndSortingRepository<ServiceProviderContracts, Long>, QueryDslPredicateExecutor<ServiceProviderContracts> {
    @Query(value = "select a  from ServiceProviderContracts a where a.binder.binId=:binCode and a.serviceProviders.serviceProviderTypes.id= :typeCode ")
    public Page<ServiceProviderContracts> getBinderProvidersByType(@Param("binCode") Long binCode,@Param("typeCode") Long typeCode, Pageable pageable);

  }
