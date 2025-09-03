package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkRenewalError;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BulkRenewalErrorRepo extends JpaRepository<BulkRenewalError, Long> {

    List<BulkRenewalError> findByBatchId(String batchId);
}