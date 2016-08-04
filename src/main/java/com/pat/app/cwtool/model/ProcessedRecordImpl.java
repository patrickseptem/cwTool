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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((keywords == null) ? 0 : keywords.hashCode());
		result = prime * result + ((record == null) ? 0 : record.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessedRecordImpl other = (ProcessedRecordImpl) obj;
		if (keywords == null) {
			if (other.keywords != null)
				return false;
		} else if (!keywords.equals(other.keywords))
			return false;
		if (record == null) {
			if (other.record != null)
				return false;
		} else if (!record.equals(other.record))
			return false;
		return true;
	}

}
