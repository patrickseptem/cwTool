package com.pat.app.cwtool.model;

import java.util.Date;

public class FinanceRecordImpl implements FinanceRecord {

	private int rowNum;
	private Date date;
	private String financeId;
	private String summary;
	private double proceeds;
	private double payment;

	public FinanceRecordImpl(int rowNum, Date date, String financeId,
			String summary, double proceeds, double payment) {
		this.rowNum = rowNum;
		this.date = date;
		this.financeId = financeId;
		this.summary = summary;
		this.proceeds = proceeds;
		this.payment = payment;
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
	public String getFinanceId() {
		return financeId;
	}

	public void setFinanceId(String financeId) {
		this.financeId = financeId;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public double getProceeds() {
		return proceeds;
	}

	public void setProceeds(double proceeds) {
		this.proceeds = proceeds;
	}

	@Override
	public double getPayment() {
		return payment;
	}

	public void setPayment(double payment) {
		this.payment = payment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((financeId == null) ? 0 : financeId.hashCode());
		long temp;
		temp = Double.doubleToLongBits(payment);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(proceeds);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + rowNum;
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
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
		FinanceRecordImpl other = (FinanceRecordImpl) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (financeId == null) {
			if (other.financeId != null)
				return false;
		} else if (!financeId.equals(other.financeId))
			return false;
		if (Double.doubleToLongBits(payment) != Double
				.doubleToLongBits(other.payment))
			return false;
		if (Double.doubleToLongBits(proceeds) != Double
				.doubleToLongBits(other.proceeds))
			return false;
		if (rowNum != other.rowNum)
			return false;
		if (summary == null) {
			if (other.summary != null)
				return false;
		} else if (!summary.equals(other.summary))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FinanceRecordImpl [rowNum=" + rowNum + ", date=" + date
				+ ", financeId=" + financeId + ", summary=" + summary
				+ ", proceeds=" + proceeds + ", payment=" + payment + "]";
	}

}
