package com.brokersystems.brokerapp.bulktransactions.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BatchStatusService {

    private final Map<String, String> jobStatusMap = new ConcurrentHashMap<>();

    public void setStatus(String jobId, String status) {
        jobStatusMap.put(jobId, status);
    }

    public String getStatus(String jobId) {
        return jobStatusMap.getOrDefault(jobId, "UNKNOWN");
    }

    public boolean isAnyJobRunning() {
        return jobStatusMap.values().stream().anyMatch("RUNNING"::equals);
    }
}
