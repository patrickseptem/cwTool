package com.pat.app.cwtool;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

@SpringBootApplication
public class Launcher {

	public static void main(String[] args) throws IOException {
		Configuration config = new Configuration() {

			Configuration m_target = DefaultConfig.getInstance();

			@Override
			public boolean useSmart() {
				return m_target.useSmart();
			}

			@Override
			public void setUseSmart(boolean useSmart) {
				m_target.setUseSmart(useSmart);
			}

			@Override
			public String getQuantifierDicionary() {
				return m_target.getQuantifierDicionary();
			}

			@Override
			public String getMainDictionary() {
				List<String> exts = m_target.getExtDictionarys();
				return exts.isEmpty() ? m_target.getMainDictionary() : exts
						.get(0);
			}

			@Override
			public List<String> getExtStopWordDictionarys() {
				return m_target.getExtStopWordDictionarys();
			}

			@Override
			public List<String> getExtDictionarys() {
				return Collections.EMPTY_LIST;
//				List<String> exts = m_target.getExtDictionarys();
//				return (!exts.isEmpty()) ? Arrays.asList(m_target
//						.getMainDictionary()) : exts;
			}
		};
		config.setUseSmart(true);
		IKSegmenter parser = new IKSegmenter(new StringReader("胡卫民机票款"), config);
		Lexeme word = null;
		while ((word = parser.next()) != null) {
			System.out.println(word.getLexemeText() + "\t- "
					+ word.getLexemeType());
		}
		
		System.exit(SpringApplication.exit(SpringApplication.run(
		        CwToolConfig.class, args)));
	}

}
