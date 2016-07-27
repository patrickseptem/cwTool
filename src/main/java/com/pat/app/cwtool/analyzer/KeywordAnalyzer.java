package com.pat.app.cwtool.analyzer;

import java.io.IOException;
import java.util.List;

public interface KeywordAnalyzer {

	List<Keyword> analyze(String text) throws IOException;

}
