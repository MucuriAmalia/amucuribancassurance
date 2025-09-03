package com.brokersystems.brokerapp.jobs.jobs.receipttracker;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface ReceiptTrackerService {

    void saveReadReceipts(List<ReceiptTracker> readReceiptTrackers, Date readDate);
}
