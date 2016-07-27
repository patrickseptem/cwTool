package com.pat.app.cwtool.model;

import java.util.List;
import java.util.Map;

import com.pat.app.cwtool.analyzer.Keyword;

public class ProcessedRecordImpl<T> implements ProcessedRecord<T> {

	private T record;
	private Map<String, List<? extends Keyword>> keywords;

	public ProcessedRecordImpl(T item, Map<String, List<? extends Keyword>> kws) {
		this.record = item;
		this.keywords = kws;
	}

	@Override
	public T getRecord() {
		return record;
	}

	public void setRecord(T record) {
		this.record = record;
	}

	@Override
	public Map<String, List<? extends Keyword>> getKeywords() {
		return keywords;
	}

	@Override
	public void setKeywords(Map<String, List<? extends Keyword>> keywords) {
		this.keywords = keywords;
	}

}
