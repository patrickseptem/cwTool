package com.pat.app.cwtool.analyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.pat.app.cwtool.analyzer.ik.LexemeKeyword;

public class KeywordAnalyzerImpl implements KeywordAnalyzer {

	private IKSegmenter segmenter = new IKSegmenter(null, false);

	@Override
	public List<Keyword> analyze(String text) throws IOException {
		StringReader reader = new StringReader(text);
		segmenter.reset(reader);

		List<Keyword> kws = new ArrayList<Keyword>();
		Lexeme kw;
		while ((kw = segmenter.next()) != null) {
			kws.add(new LexemeKeyword(kw));
		}

		return kws;
	}
}
