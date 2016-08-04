package com.pat.app.cwtool;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pat.app.cwtool.analyzer.KeywordAnalyzer;
import com.pat.app.cwtool.analyzer.KeywordAnalyzerImpl;
import com.pat.app.cwtool.analyzer.ik.DomainConfiguration;
import com.pat.app.cwtool.batch.BankRecordProcessor;
import com.pat.app.cwtool.batch.BankRecordReader;
import com.pat.app.cwtool.batch.BaseRecordReader;
import com.pat.app.cwtool.batch.BatchExceptionHandler;
import com.pat.app.cwtool.batch.CollectCompanyUnpaidRecordsTasklet;
import com.pat.app.cwtool.batch.FinanceRecordProcessor;
import com.pat.app.cwtool.batch.FinanceRecordReader;
import com.pat.app.cwtool.batch.OutputMatchedRecordsTasklet;
import com.pat.app.cwtool.batch.OutputUnmatchedWithdrawBankRecordsTasklet;
import com.pat.app.cwtool.batch.ProcessedRecordWriter;
import com.pat.app.cwtool.match.MatchManager;
import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedRecord;

@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class CwToolConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<BankRecord, ProcessedRecord<BankRecord>> chunk(100)
				.reader(bankRecordReader()).processor(bankRecordProcessor())
				.writer(processedBankRecordWriter())
				.exceptionHandler(exceptionHandler())
				.listener(processedBankRecordWriter())
				.listener(bankRecordReader()).build();
	}

	@Bean
	public ExceptionHandler exceptionHandler() {
		return new BatchExceptionHandler();
	}

	@Bean
	public ItemWriter<? super ProcessedRecord<BankRecord>> processedBankRecordWriter() {
		return new ProcessedRecordWriter<BankRecord>(
				BankRecordProcessor.PROCESSED_BANK_RECORDS);
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
				.<FinanceRecord, ProcessedRecord<FinanceRecord>> chunk(10)
				.reader(financeRecordReader())
				.processor(financeRecordProcessor())
				.writer(financeRecordWriter())
				.exceptionHandler(exceptionHandler())
				.listener(financeRecordWriter())
				.listener(financeRecordReader()).build();
	}

	@Bean
	public ItemWriter<? super ProcessedRecord<FinanceRecord>> financeRecordWriter() {
		return new ProcessedRecordWriter<FinanceRecord>(
				FinanceRecordProcessor.PROCESSED_FINANCE_RECORDS);
	}

	@Bean
	@StepScope
	public ItemProcessor<? super FinanceRecord, ? extends ProcessedRecord<FinanceRecord>> financeRecordProcessor() {
		return new FinanceRecordProcessor();
	}

	@Bean
	@StepScope
	public FinanceRecordReader financeRecordReader() {
		return new FinanceRecordReader();
	}

	@Bean
	@StepScope
	public ItemProcessor<BankRecord, ProcessedRecord<BankRecord>> bankRecordProcessor() {
		return new BankRecordProcessor();
	}

	@Bean
	@StepScope
	public BaseRecordReader<BankRecord> bankRecordReader() {
		return new BankRecordReader();
	}

	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3")
				.tasklet(collectCompanyUnpaidRecordsTasklet())
				.listener(collectCompanyUnpaidRecordsTasklet()).build();
	}

	@Bean
	public Step step5() {
		return stepBuilderFactory.get("step5")
				.tasklet(outputUnmatchedWithdrawBankRecordsTasklet()).build();
	}

	@Bean
	public Tasklet outputUnmatchedWithdrawBankRecordsTasklet() {
		return new OutputUnmatchedWithdrawBankRecordsTasklet();
	}

	@Bean
	public Step step6() {
		return stepBuilderFactory.get("step6")
				.tasklet(outputUnmatchedDepositBankRecordsTasklet()).build();
	}

	@Bean
	public Tasklet outputUnmatchedDepositBankRecordsTasklet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	public Step step7() {
		return stepBuilderFactory.get("step7")
				.tasklet(outputUnmatchedPaymentFinanceRecordsTasklet()).build();
	}

	@Bean
	public Tasklet outputUnmatchedPaymentFinanceRecordsTasklet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	public Step step8() {
		return stepBuilderFactory.get("step8")
				.tasklet(outputUnmatchedProceedsFinanceRecordsTasklet())
				.build();
	}

	@Bean
	public Tasklet outputUnmatchedProceedsFinanceRecordsTasklet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	public Step step4() {
		return stepBuilderFactory.get("step4")
				.tasklet(outputMatchedRecordsTasklet()).build();
	}

	@Bean
	public Tasklet outputMatchedRecordsTasklet() {
		return new OutputMatchedRecordsTasklet();
	}

	@Bean
	public Tasklet collectCompanyUnpaidRecordsTasklet() {
		return new CollectCompanyUnpaidRecordsTasklet();
	}

	@Bean
	public Job job(Step step1) throws Exception {
		return jobBuilderFactory.get("job1")
				.incrementer(new RunIdIncrementer()).start(step1).next(step2())
				.next(step3()).next(step4()).next(step5()).build();
	}

	@Bean
	public KeywordAnalyzer keywordAnalyzer() {
		return new KeywordAnalyzerImpl();
	}

	@Bean
	public org.wltea.analyzer.cfg.Configuration nameConfig() {
		return new DomainConfiguration("name.dic");
	}

	@Bean
	public org.wltea.analyzer.cfg.Configuration companyConfig() {
		return new DomainConfiguration("company.dic");
	}

	@Bean
	public MatchManager getMatchManager() {
		return new MatchManager();
	}
}
