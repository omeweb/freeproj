package com.underline.freeproj.domain;

import java.util.Date;

/**
 * 类目bean<br />
 * 2013-03-05 by liusan.dyf 增加authorizationCode，可用来存储授权码等
 * 
 * @author xialei.cg
 */
public class Catalog {
	private long id;
	private String title;
	private String code;
	private int parentId;
	private String parentPath;
	private int sortNumber;
	private int level;
	private Date createTime;
	private int reservedInt;
	private int status;

	/**
	 * 备注信息 2012-02-17 by liusan.dyf
	 */
	private String comment;

	// /**
	// * 抽象的userId，表示目录归谁所有 2012-02-21
	// */
	// private long userId;

	private String creator;

	private Date lastUpdateTime;

	private String lastOperator;

	/**
	 * 以此支持分表 2012-08-02
	 */
	private String reservedValue;

	/**
	 * 2012-08-31 by liusan.dyf
	 */
	private String reservedString;

	private String authorizationCode;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public int getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(int sortNumber) {
		this.sortNumber = sortNumber;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getReservedInt() {
		return reservedInt;
	}

	public void setReservedInt(int reservedInt) {
		this.reservedInt = reservedInt;
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

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
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

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public String getReservedValue() {
		return reservedValue;
	}

	public void setReservedValue(String reservedValue) {
		this.reservedValue = reservedValue;
	}

	// public long getUserId() {
	// return userId;
	// }
	//
	// public void setUserId(long userId) {
	// this.userId = userId;
	// }
}
