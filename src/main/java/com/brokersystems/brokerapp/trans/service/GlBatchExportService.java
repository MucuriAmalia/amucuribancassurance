package com.brokersystems.brokerapp.trans.service;
import java.io.IOException;
import java.util.Date;

public interface GlBatchExportService {
    void exportAllBatchesToExcel(String folderPath) throws IOException;
}
