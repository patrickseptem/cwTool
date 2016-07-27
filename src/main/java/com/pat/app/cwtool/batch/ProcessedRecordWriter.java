package com.pat.app.cwtool.batch;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import com.pat.app.cwtool.model.ProcessedRecord;

public class ProcessedRecordWriter<T> implements ItemWriter<ProcessedRecord<T>> {

	private static Logger s_logger = LoggerFactory
			.getLogger(ProcessedRecord.class);

	private List<ProcessedRecord<T>> records;

	private String ctxKey;
	
	public ProcessedRecordWriter(String ctxKey){
		this.ctxKey = ctxKey;
	}

	@Override
	public void write(List<? extends ProcessedRecord<T>> items)
			throws Exception {
		s_logger.info("Receive " + items.size() + " processed records.");
		records.addAll(items);
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		records = new ArrayList<ProcessedRecord<T>>(1000);
		ExecutionContext jobExecutionCtx = stepExecution.getJobExecution()
				.getExecutionContext();
		jobExecutionCtx.put(ctxKey, records);
	}

}
