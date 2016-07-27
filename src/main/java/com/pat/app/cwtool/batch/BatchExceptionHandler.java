package com.pat.app.cwtool.batch;

import java.text.ParseException;

import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.exception.ExceptionHandler;

public class BatchExceptionHandler implements ExceptionHandler {

	@Override
	public void handleException(RepeatContext context, Throwable throwable)
			throws Throwable {
		if(throwable instanceof UnexpectedInputException){
			return;
		}else if (throwable instanceof ParseException){
			return;
		}

	}

}
