package com.pat.app.cwtool.model;

import java.util.Date;

public interface FinanceRecord {

	String SUMMARY = "summary";

	public abstract double getPayment();

	public abstract double getProceeds();

	public abstract String getSummary();

	public abstract String getFinanceId();

	public abstract Date getDate();

	public abstract int getRowNum();

}
