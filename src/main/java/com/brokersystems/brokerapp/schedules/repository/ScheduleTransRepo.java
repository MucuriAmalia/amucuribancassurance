package com.brokersystems.brokerapp.schedules.repository;

import com.brokersystems.brokerapp.schedules.model.ScheduleTrans;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 2/20/2017.
 */
public interface ScheduleTransRepo extends PagingAndSortingRepository<ScheduleTrans, Long>, QueryDslPredicateExecutor<ScheduleTrans> {
}
