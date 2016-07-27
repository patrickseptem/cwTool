package com.pat.app.cwtool.model;

import java.util.List;
import java.util.Map;

import com.pat.app.cwtool.analyzer.Keyword;

public interface ProcessedRecord<T> {

	void setKeywords(Map<String, List<? extends Keyword>> keywords);

	Map<String, List<? extends Keyword>> getKeywords();

	T getRecord();

}
