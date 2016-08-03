package com.pat.app.cwtool.analyzer.ik;

import org.wltea.analyzer.core.Lexeme;

import com.pat.app.cwtool.analyzer.KType;
import com.pat.app.cwtool.analyzer.Keyword;

public class LexemeKeyword implements Keyword {

	private Lexeme keyword;

	public LexemeKeyword(Lexeme kw) {
		this.keyword = kw;
	}

	@Override
	public String getValue() {
		return keyword.getLexemeText();
	}

	@Override
	public KType getType() {
		return KType.Lexeme;
	}

	public Lexeme getLexeme() {
		return keyword;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((keyword == null) ? 0 : keyword.getLexemeText().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getValue().equals(obj.toString()))
			return true;
		if (getClass() != obj.getClass())
			return false;
		return getValue().equals(((Keyword) obj).getValue());
	}

	@Override
	public String toString() {
		return "LexemeKeyword [" + keyword.getLexemeText() + "]";
	}

}
