package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.uw.dtos.RenewalDTO;
import org.easybatch.core.record.GenericRecord;
import org.easybatch.core.record.Header;
import org.easybatch.core.record.Record;

public class BatchRecord extends GenericRecord<BatchRecordDTO> implements Record<BatchRecordDTO> {


    public BatchRecord(Header header, BatchRecordDTO payload) {
        super(header, payload);
    }
}
