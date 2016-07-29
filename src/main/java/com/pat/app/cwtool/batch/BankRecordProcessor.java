package com.pat.app.cwtool.batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.pat.app.cwtool.analyzer.KType;
import com.pat.app.cwtool.analyzer.Keyword;
import com.pat.app.cwtool.analyzer.KeywordAnalyzer;
import com.pat.app.cwtool.analyzer.PlainKeyword;
import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.ProcessedRecord;
import com.pat.app.cwtool.model.ProcessedRecordImpl;

public class BankRecordProcessor implements
		ItemProcessor<BankRecord, ProcessedRecord<BankRecord>> {

	private static final String[] BIZ_POSTFIXES = new String[] { "公司", "院",
			"中心", "厂", "会", "馆", "店", "所", "部", "户" };

	@Autowired
	private KeywordAnalyzer analyzer;

	public static final String PROCESSED_BANK_RECORDS = "processedBankRecords";

	@Override
	public ProcessedRecord<BankRecord> process(BankRecord record)
			throws Exception {
		// TODO encapsulate process for different field to different handler
		// classes; abstract processing to handler
		Map<String, List<? extends Keyword>> kws = new HashMap<>();
		String account = record.getAccount();
		if (!account.isEmpty()) {
			KType accountType = null;
			if (endsWithPostfix(record.getAccount(), BIZ_POSTFIXES)) {
				accountType = KType.Business;
			} else {
				accountType = KType.Person;
			}
			List<Keyword> accountKws = new ArrayList<>();
			PlainKeyword nameKeyword = new PlainKeyword(account, accountType);
			accountKws.add(nameKeyword);

			accountKws.addAll(analyzer.analyze(account));
			kws.put(BankRecord.ACCOUNT, accountKws);
			if (KType.Person == accountType) {
				kws.put(BankRecord.NAME, Arrays.asList(nameKeyword));
			}
		}
		List<Keyword> commentKws = analyzer.analyze(record.getComment());
		kws.put(BankRecord.COMMENT, commentKws);

		List<Keyword> all = new ArrayList<>();
		Collection<List<? extends Keyword>> values = kws.values();
		for (List<? extends Keyword> list : values) {
			all.addAll(list);
		}
		kws.put(ProcessedRecord.ALL_KEYWORDS, all);

		return new ProcessedRecordImpl<BankRecord>(record, kws);
	}

	private boolean endsWithPostfix(String text, String[] postfixes) {
		if (text.length() <= 4) {
			return false;
		}
		String text1 = text;
		int pos = text1.lastIndexOf("（");
		if (pos == -1) {
			pos = text1.lastIndexOf("(");
		}
		if (pos != -1) {
			text1 = text1.substring(0, pos).trim();
		}
		for (String p : postfixes) {
			if (text1.endsWith(p)) {
				return true;
			}
		}
		return false;
	}

}
