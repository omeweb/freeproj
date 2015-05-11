package com.underline.freeproj.dao;

import tools.Global;

import com.underline.freeproj.domain.OperateLog;
import com.underline.freeproj.orm.AbstractDao;

public class OperateLogDaoImpl extends AbstractDao<OperateLog> {
	/**
	 * 系统编号
	 */
	private int systemId = 100;

	public int add(OperateLog entry) {
		return tools.Convert.toInt(super.insert(entry), 0);
	}

	@Override
	public String getNamespace() {
		return "operateLog";
	}

	/**
	 * 简化版的添加日志，title和content都不是必须的，2015-5-10 14:56:07 by liusan.dyf
	 * 
	 * @param userId
	 * @param userName
	 * @param code
	 * @param targetKey
	 * @param result
	 * @param ip
	 */
	public void addSimple(long userId, String userName, String code, String targetKey, Object result, String ip) {
		OperateLog entry = new OperateLog();
		entry.setOperationCode(code);
		entry.setTargetKey(targetKey);

		entry.setSystem(systemId);
		entry.setIp(ip);

		entry.setUserId(userId);
		entry.setUserName(userName);

		if (result != null)
			entry.setResult(result.toString());

		fixEntry(entry);

		add(entry);
	}

	private void fixEntry(OperateLog entry) {
		// 追加数据
		tools.User u = Global.getCurrentUser();
		if (u != null) {
			// 可以转换【"057526"】这样的情况 2014-05-17 by liusan.dyf
			long userId = tools.Convert.toLong(u.getId(), 0);
			String userName = u.getName();

			// ip
			String ip = tools.Convert.toString(u.getExtra().get("ip"));

			if (entry.getUserId() == 0)
				entry.setUserId(userId);

			if (tools.Validate.isNullOrEmpty(entry.getUserName())) {
				entry.setUserName(userName);
			}

			if (tools.Validate.isNullOrEmpty(entry.getIp())) {
				entry.setIp(ip);
			}
		}
	}

	/**
	 * 2012-05-24 by liusan.dyf
	 * 
	 * @param title eg 修改了文章《淘宝规则》
	 * @param content eg 文章内容
	 * @param code eg catalog.after-add
	 * @param targetKey eg 文章id，比如12345
	 * @param result 操作的结果
	 * @param r
	 */
	public void add(String title, String content, String code, String targetKey, Object result, int r) {
		OperateLog entry = new OperateLog();
		entry.setTitle(title);
		entry.setContent(content);
		entry.setOperationCode(code);
		entry.setTargetKey(targetKey);
		entry.setSystem(systemId);

		if (r >= 0)
			entry.setRevision(r);// 2014-07-11 by liusan.dyf

		if (result != null)
			entry.setResult(result.toString());

		// 追加数据
		fixEntry(entry);

		int id = add(entry);
		entry.setId(id);

		// // 2013-07-16 by liusan.dyf
		// // 特殊的回调，一般日志是不自动执行hook回调的，防止死循环
		// if (getEventHandler() != null) {
		// EventArgs args = EventArgs.create(entry).setType("operateLog.after-insert");
		// getEventHandler().onEvent(null, args);
		// }
	}

	public void add(String title, String content, String code, String targetKey, Object result) {
		add(title, content, code, targetKey, result, -1);
	}

	public int getSystemId() {
		return systemId;
	}

	public void setSystemId(int v) {
		systemId = v;
	}
}
