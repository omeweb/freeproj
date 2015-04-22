package com.taobao.freeproj.domain;

import java.util.Date;

/**
 * 2012-02-15 by liusan.dyf，用于计数器统计<br />
 * 增加reservedValue 2013-04-28 by liusan.dyf
 * 
 * @author liusan.dyf
 */
public class Counter {
	private String key;
	private long value;
	private long timestamp;
	/**
	 * 一些对key的备注信息 2012-05-24 by liusan.dyf
	 */
	private String comment;

	/**
	 * 最后更新时间 2012-05-24 by liusan.dyf
	 */
	private Date lastUpdateTime;

	private String reservedValue;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getReservedValue() {
		return reservedValue;
	}

	public void setReservedValue(String reservedValue) {
		this.reservedValue = reservedValue;
	}
}
