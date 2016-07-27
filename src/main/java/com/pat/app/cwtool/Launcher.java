package com.pat.app.cwtool;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Launcher {

	private static Logger s_logger = LoggerFactory.getLogger(Launcher.class);
	
	@Autowired
	JobLauncher jobLauncher;
	
	@Autowired 
	Job job;
	
	public void run(JobParameters params){
		try {
			jobLauncher.run(job, params);
		} catch (JobExecutionAlreadyRunningException | JobRestartException
				| JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			s_logger.error(e.getMessage(), e);
		}
	}
	
	
	public static void main(String[] args) throws IOException {
//		JobParametersBuilder builder = new JobParametersBuilder();
//		builder.addString("bankSheetPath", args[0]);
//		builder.addString("financeSheetPath", args[1]);
//		new Launcher().run(builder.toJobParameters());
		System.exit(SpringApplication.exit(SpringApplication.run(
		        CwToolConfig.class, args)));
	}

}
