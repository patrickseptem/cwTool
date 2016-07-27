package com.pat.app.cwtool.analyzer.ik;

import java.util.Collections;
import java.util.List;

import org.wltea.analyzer.cfg.Configuration;

public class DomainConfiguration implements Configuration {

	private boolean useSmart;

	private String mainDict;

	public DomainConfiguration(String mainDict) {
		this.mainDict = mainDict;
	}

	@Override
	public boolean useSmart() {
		return useSmart;
	}

	@Override
	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}

	@Override
	public String getMainDictionary() {
		return mainDict;
	}

	@Override
	public String getQuantifierDicionary() {
		return null;
	}

	@Override
	public List<String> getExtDictionarys() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getExtStopWordDictionarys() {
		return Collections.emptyList();
	}

}
