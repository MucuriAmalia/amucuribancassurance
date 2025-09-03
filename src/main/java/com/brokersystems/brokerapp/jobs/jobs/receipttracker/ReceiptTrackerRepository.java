package com.brokersystems.brokerapp.jobs.jobs.receipttracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ReceiptTrackerRepository extends JpaRepository<ReceiptTracker, Long> {

    List<ReceiptTracker> findReceiptTrackersByTransactionRefIn(Set<String> transactionRefs);
}
