package com.pat.app.cwtool.model;

import java.util.Date;

public class BankRecordImpl implements BankRecord {

	private int rowNum;
	private Date date;
	private String tradeNum;
	private double withdraw;
	private double deposit;
	private String account;
	private String summary;
	private String comment;

	public BankRecordImpl(int rowNum, Date date, String tradeNum,
			double withdraw, double deposit, String account, String summary,
			String comment) {
		this.rowNum = rowNum;
		this.date = date;
		this.tradeNum = tradeNum;
		this.withdraw = withdraw;
		this.deposit = deposit;
		this.account = account;
		this.summary = summary;
		this.comment = comment;
	}

	@Override
	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	@Override
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String getTradeNum() {
		return tradeNum;
	}

	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}

	@Override
	public double getWithdraw() {
		return withdraw;
	}

	public void setWithdraw(double withdraw) {
		this.withdraw = withdraw;
	}

	@Override
	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

	@Override
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		long temp;
		temp = Double.doubleToLongBits(deposit);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + rowNum;
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
		result = prime * result
				+ ((tradeNum == null) ? 0 : tradeNum.hashCode());
		temp = Double.doubleToLongBits(withdraw);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		BankRecordImpl other = (BankRecordImpl) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (Double.doubleToLongBits(deposit) != Double
				.doubleToLongBits(other.deposit))
			return false;
		if (rowNum != other.rowNum)
			return false;
		if (summary == null) {
			if (other.summary != null)
				return false;
		} else if (!summary.equals(other.summary))
			return false;
		if (tradeNum == null) {
			if (other.tradeNum != null)
				return false;
		} else if (!tradeNum.equals(other.tradeNum))
			return false;
		if (Double.doubleToLongBits(withdraw) != Double
				.doubleToLongBits(other.withdraw))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BankRecordImpl [rowNum=" + rowNum + ", date=" + date
				+ ", tradeNum=" + tradeNum + ", withdraw=" + withdraw
				+ ", deposit=" + deposit + ", account=" + account
				+ ", summary=" + summary + ", comment=" + comment + "]";
	}

}
