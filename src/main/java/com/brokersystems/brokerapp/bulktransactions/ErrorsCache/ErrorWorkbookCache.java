package com.brokersystems.brokerapp.bulktransactions.ErrorsCache;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ErrorWorkbookCache {

    private final Map<String, File> workbookExcelMap = new ConcurrentHashMap<>();
    private final Map<String, ByteArrayOutputStream> workbookMap = new ConcurrentHashMap<>();

    public void saveWorkbook(String refCode, ByteArrayOutputStream workbookStream) {
        workbookMap.put(refCode, workbookStream);
    }

    public ByteArrayOutputStream getWorkbook(String refCode) {
        return workbookMap.get(refCode);
    }

    public void removeWorkbook(String refCode) {
        workbookMap.remove(refCode);
    }

    public void saveExcelWorkbook(String refCode, File workbookFile) {
        workbookExcelMap.put(refCode, workbookFile);
    }

    public File getExcelWorkbookFile(String refCode) {
        return workbookExcelMap.get(refCode);
    }

    public void removeExcelWorkbook(String refCode) {
        workbookExcelMap.remove(refCode);
    }

}