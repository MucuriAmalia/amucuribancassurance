package com.brokersystems.brokerapp.reconciliation.sfi.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SFIService {

    List<Map<String, String>> readMortgagesData(String sheetName) throws IOException;;
}
