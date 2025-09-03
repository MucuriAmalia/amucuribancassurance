package com.brokersystems.brokerapp.uw.validators;

import org.easybatch.core.job.JobParameters;
import org.easybatch.core.job.JobReport;
import org.easybatch.core.listener.JobListener;
import org.easybatch.core.listener.PipelineListener;
import org.easybatch.core.record.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RenewalJobListener implements PipelineListener,JobListener {
	
	private final Logger logger = LoggerFactory.getLogger(RenewalJobListener.class);
	
	
	
	@Override
	public void afterRecordProcessing(Record arg0, Record arg1) {

		
	}

	@Override
	public Record beforeRecordProcessing(Record record) {
		logger.info("Before Processing record.."+record);
		return record;
	}

	@Override
	public void onRecordProcessingException(Record record, Throwable error) {
		
		logger.info( error.getMessage());
		
	}

	@Override
	public void afterJobEnd(JobReport jobReport) {
		logger.info(jobReport.toString());
		
	}

	@Override
	public void beforeJobStart(JobParameters arg0) {
		// TODO Auto-generated method stub
		
	}



}
