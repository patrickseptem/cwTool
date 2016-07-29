package com.pat.app.cwtool.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.pat.app.cwtool.analyzer.Keyword;
import com.pat.app.cwtool.analyzer.KeywordAnalyzer;
import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedRecord;
import com.pat.app.cwtool.model.ProcessedRecordImpl;

public class FinanceRecordProcessor implements
		ItemProcessor<FinanceRecord, ProcessedRecord<FinanceRecord>> {

	@Autowired
	private KeywordAnalyzer analyzer;
	public static final String PROCESSED_FINANCE_RECORDS = "processedFinanceRecords";

	@Override
	public ProcessedRecord<FinanceRecord> process(FinanceRecord record)
			throws Exception {
		// TODO encapsulate process for different field to different handler
		// classes; abstract processing to handler
		Map<String, List<? extends Keyword>> kws = new HashMap<>();

		List<Keyword> commentKws = analyzer.analyze(record.getSummary());
		kws.put(FinanceRecord.SUMMARY, commentKws);

		List<Keyword> all = new ArrayList<>();
		Collection<List<? extends Keyword>> values = kws.values();
		for (List<? extends Keyword> list : values) {
			all.addAll(list);
		}
		kws.put(ProcessedRecord.ALL_KEYWORDS, all);

		return new ProcessedRecordImpl<FinanceRecord>(record, kws);
	}

}
