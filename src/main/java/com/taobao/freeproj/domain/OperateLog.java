package com.taobao.freeproj.domain;

import java.util.Date;

/**
 * 操作历史信息
 * 
 * @author <a href="mailto:wuchen.lx@taobao.com">wuchen.lx</a>
 * @version 1.0
 * @since 2012-5-24
 */
public class OperateLog {
	private long id;
	private long userId;
	private String userName;
	private Date createTime;
	private String content;
	private String result;
	private String operationCode;
	private int system;
	private String ip;
	private String title;

	/**
	 * 2012-05-24 by liusan.dyf，这里记录修改的对象的唯一id，便于检索，用String类型，灵活性更好
	 */
	private String targetKey;

	/**
	 * 目标版本号 2014-07-11 by liusan.dyf
	 */
	private int revision;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getOperationCode() {
		return operationCode;
	}

	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}

	public int getSystem() {
		return system;
	}

	public void setSystem(int system) {
		this.system = system;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTargetKey() {
		return targetKey;
	}

	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

}
