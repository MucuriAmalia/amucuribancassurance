package com.brokersystems.brokerapp.jobs.jobs.receipttracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReceiptTrackerServiceImpl implements ReceiptTrackerService {

    private final ReceiptTrackerRepository receiptTrackerRepository;

    @Autowired
    public ReceiptTrackerServiceImpl(ReceiptTrackerRepository receiptTrackerRepository) {
        this.receiptTrackerRepository = receiptTrackerRepository;
    }

    @Override
    public void saveReadReceipts(List<ReceiptTracker> readReceiptTrackers, Date readDate) {
        if (readReceiptTrackers == null || readReceiptTrackers.isEmpty()) {

            log.warn("No (or empty List) read receipt trackers parsed");
            return;
        }

        Set<String> inputTransactionRefs = readReceiptTrackers.stream()
                .map(ReceiptTracker::getTransactionRef)
                .collect(Collectors.toSet());

        List<ReceiptTracker> existingRecords = receiptTrackerRepository.findReceiptTrackersByTransactionRefIn(inputTransactionRefs);

        Set<String> successfulTransactionRefs = existingRecords.stream()
                .filter(tracker -> ReceiptStatus.SUCCESS.equals(tracker.getReceiptStatus()))
                .map(ReceiptTracker::getTransactionRef)
                .collect(Collectors.toSet());

        Map<String, ReceiptTracker> existingRecordsByTransactionRef = existingRecords.stream()
                .collect(Collectors.toMap(
                        ReceiptTracker::getTransactionRef,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        List<ReceiptTracker> receiptTrackersToSave = new ArrayList<>();

        for (ReceiptTracker newTracker : readReceiptTrackers) {

            String transactionRef = newTracker.getTransactionRef();

            if (successfulTransactionRefs.contains(transactionRef)) {

                continue;
            }

            ReceiptTracker existingTracker = existingRecordsByTransactionRef.get(transactionRef);

            if (existingTracker != null) {

                existingTracker.setReceiptStatus(newTracker.getReceiptStatus());
                existingTracker.setReceiptDate(readDate);
                existingTracker.setComments(newTracker.getComments());
                receiptTrackersToSave.add(existingTracker);

            } else {

                newTracker.setReceiptDate(readDate);
                receiptTrackersToSave.add(newTracker);

            }
        }

        if (!receiptTrackersToSave.isEmpty()) {
            receiptTrackerRepository.save(receiptTrackersToSave);
        }
    }
}
