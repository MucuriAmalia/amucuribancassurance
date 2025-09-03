package com.brokersystems.brokerapp.uw.model;

import com.brokersystems.brokerapp.uw.dtos.RenewalDTO;
import org.easybatch.core.record.GenericRecord;
import org.easybatch.core.record.Header;
import org.easybatch.core.record.Record;

public class RenewalRecord extends GenericRecord<RenewalDTO> implements Record<RenewalDTO> {

	public RenewalRecord(Header header, RenewalDTO payload) {
		super(header, payload);
	}

}
