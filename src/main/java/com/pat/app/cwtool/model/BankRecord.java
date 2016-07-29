package com.pat.app.cwtool.model;

import java.util.Date;

public interface BankRecord {

	String ACCOUNT = "account";
	String COMMENT = "comment";
	public static final String NAME = "Name";

	String getAccount();

	String getComment();

	Date getDate();

	double getDeposit();

	int getRowNum();

	String getSummary();

	String getTradeNum();

	double getWithdraw();

}
