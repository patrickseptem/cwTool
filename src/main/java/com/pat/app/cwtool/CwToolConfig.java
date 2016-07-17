package com.pat.app.cwtool;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wltea.analyzer.core.IKSegmenter;

import com.pat.app.cwtool.analyzer.KeywordAnalyzer;
import com.pat.app.cwtool.model.BankRecord;
import com.pat.app.cwtool.model.FinanceRecord;
import com.pat.app.cwtool.model.ProcessedBankRecord;
import com.pat.app.cwtool.model.ProcessedFinanceRecord;

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
				.<BankRecord, ProcessedBankRecord> chunk(10)
				.reader(bankRecordReader()).processor(bankRecordProcessor())
				.build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
				.<FinanceRecord, ProcessedFinanceRecord> chunk(10)
				.reader(financeRecordReader())
				.processor(financeRecordProcessor()).build();
	}

	@Bean
	public ItemProcessor<? super FinanceRecord, ? extends ProcessedFinanceRecord> financeRecordProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	private ItemReader<? extends FinanceRecord> financeRecordReader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	public ItemProcessor<BankRecord, ProcessedBankRecord> bankRecordProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	public ItemReader<BankRecord> bankRecordReader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	public Job job(Step step1) throws Exception {
		return jobBuilderFactory.get("job1")
				.incrementer(new RunIdIncrementer()).start(step1).build();
	}
	
	@Bean
	public KeywordAnalyzer customizedKeywordAnalyzer(){
		return null;
	}
}
