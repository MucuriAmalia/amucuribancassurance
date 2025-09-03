package com.brokersystems.brokerapp.life.repository;

import com.brokersystems.brokerapp.life.model.MaturityPayoutSchedule;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaturityPayoutScheduleRepo extends PagingAndSortingRepository<MaturityPayoutSchedule, Long> {
}