package com.underline.freeproj.domain;

/**
 * 2012-02-29
 * 
 * @author liusan.dyf
 */
public class Timestamp {
	private long timestamp;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private int yearMonth;
	private int yearMonthDay;
	private int yearMonthDayHour;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getYearMonthDay() {
		return yearMonthDay;
	}

	public void setYearMonthDay(int yearMonthDay) {
		this.yearMonthDay = yearMonthDay;
	}

	public int getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(int yearMonth) {
		this.yearMonth = yearMonth;
	}

	public int getYearMonthDayHour() {
		return yearMonthDayHour;
	}

	public void setYearMonthDayHour(int yearMonthDayHour) {
		this.yearMonthDayHour = yearMonthDayHour;
	}
}
