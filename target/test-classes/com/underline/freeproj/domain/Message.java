package com.underline.freeproj.domain;

/**
 * 2015/4/3 16:22:47
 */
public class Message {

	/**
	*
	*/
	private int id;

	/**
	*
	*/
	private String userName;

	/**
	*
	*/
	private int userId;

	/**
	*
	*/
	private String content;

	/**
	*
	*/
	private String createTime;

	/**
	*
	*/
	private String source;

	/**
	*
	*/
	private String occurrenceTime;

	/**
	*
	*/
	private String title;

	/**
	*
	*/
	private String comment;

	/**
	*
	*/
	private String extraData;

	/**
	*
	*/
	private float numericValue;

	/**
	*
	*/
	private String ip;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return this.source;
	}

	public void setOccurrenceTime(String occurrenceTime) {
		this.occurrenceTime = occurrenceTime;
	}

	public String getOccurrenceTime() {
		return this.occurrenceTime;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return this.comment;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public String getExtraData() {
		return this.extraData;
	}

	public void setNumericValue(float numericValue) {
		this.numericValue = numericValue;
	}

	public float getNumericValue() {
		return this.numericValue;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return this.ip;
	}

}
