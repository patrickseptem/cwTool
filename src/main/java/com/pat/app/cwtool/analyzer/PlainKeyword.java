package com.pat.app.cwtool.analyzer;

public class PlainKeyword implements Keyword {

	private CharSequence keyword;
	private KType type;

	public PlainKeyword(CharSequence cs, KType type) {
		this.keyword = cs;
		this.type = type;
	}

	@Override
	public String getValue() {
		return keyword.toString();
	}

	@Override
	public KType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlainKeyword other = (PlainKeyword) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PlainKeyword [keyword=" + keyword + ", type=" + type + "]";
	}

}
