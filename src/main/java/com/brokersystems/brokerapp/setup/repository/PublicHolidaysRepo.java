package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.PublicHolidays;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface PublicHolidaysRepo extends PagingAndSortingRepository<PublicHolidays, Long>, QueryDslPredicateExecutor<PublicHolidays> {

    List<PublicHolidays> findByActiveStatus(String status);
}