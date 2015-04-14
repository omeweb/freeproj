package com.taobao.freeproj.domain;

import java.util.Date;

/**
 * 2012-09-26增加lastUpdateTime、creator，2012-09-27增加lastOperator <br />
 * 2012-09-28增加reservedString、reservedInt
 * 
 * @author liusan.dyf
 */
public class KeyValue {
	private long id;
	private String key;
	private String value;
	private String typeCode;
	private int status;
	private String comment;
	private String reservedValue;

	/**
	 * 2012-09-26 by liusan.dyf
	 */
	private Date lastUpdateTime;

	/**
	 * 2012-09-26 by liusan.dyf
	 */
	private String creator;

	/**
	 * 2012-09-27 by liusan.dyf
	 */
	private String lastOperator;
	private String reservedString;
	private int reservedInt;

	/**
	 * 2014-11-17 by 六三，注意类型为Integer，sortNumber可能为null
	 */
	private int sortNumber;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getLastOperator() {
		return lastOperator;
	}

	public void setLastOperator(String lastOperator) {
		this.lastOperator = lastOperator;
	}

	public String getReservedString() {
		return reservedString;
	}

	public void setReservedString(String reservedString) {
		this.reservedString = reservedString;
	}

	public int getReservedInt() {
		return reservedInt;
	}

	public void setReservedInt(int reservedInt) {
		this.reservedInt = reservedInt;
	}

	public String getReservedValue() {
		return reservedValue;
	}

	public void setReservedValue(String reservedValue) {
		this.reservedValue = reservedValue;
	}

	public int getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(Integer sortNumber) {
		if (sortNumber == null)
			return;// 2014-11-17 by 六三
		this.sortNumber = sortNumber;
	}
}
