package com.taobao.freeproj.common;

import tools.Global;

import com.taobao.freeproj.dao.OperateLogDaoImpl;
import com.taobao.freeproj.domain.OperateLog;

/**
 * log4j实现起来有点麻烦，不依赖log4j的形式来配置日志方式，要构造一个object，使用不方便，而且要依赖配置 <br />
 * 增加EventHandler，特殊的回调方式，仅仅是在插入日志之后完成 2013-07-16 <br />
 * 2013-07-19 取消 by liusan.dyf
 * 
 * @author liusan.dyf
 */
public class OperateLogHelper {
	private static OperateLogDaoImpl dao;

	// private static EventHandler eventHandler = null;

	/**
	 * 系统编号
	 */
	private static int systemId;

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
	public static void add(String title, String content, String code, String targetKey, Object result, int r) {
		if (getDao() == null)// 2012-05-24 by liusan.dyf
			return;

		OperateLog entry = new OperateLog();

		entry.setTitle(title);
		entry.setContent(content);
		entry.setOperationCode(code);
		entry.setTargetKey(targetKey);
		entry.setSystem(getSystemId());

		if (r >= 0)
			entry.setRevision(r);// 2014-07-11 by liusan.dyf

		if (result != null)
			entry.setResult(result.toString());

		// 追加数据
		tools.User u = Global.getCurrentUser();
		if (u != null) {
			// 可以转换【"057526"】这样的情况 2014-05-17 by liusan.dyf
			entry.setUserId(tools.Convert.toLong(u.getId(), 0));

			entry.setUserName(u.getName());

			// ip
			Object ip = u.getExtra().get("ip");
			if (ip != null)
				entry.setIp(ip.toString());
		}

		int id = getDao().add(entry);
		entry.setId(id);

		// // 2013-07-16 by liusan.dyf
		// // 特殊的回调，一般日志是不自动执行hook回调的，防止死循环
		// if (getEventHandler() != null) {
		// EventArgs args = EventArgs.create(entry).setType("operateLog.after-insert");
		// getEventHandler().onEvent(null, args);
		// }
	}

	public static void add(String title, String content, String code, String targetKey, Object result) {
		add(title, content, code, targetKey, result, -1);
	}

	public static int getSystemId() {
		return systemId;
	}

	public static void setSystemId(int systemId) {
		OperateLogHelper.systemId = systemId;
	}

	public static OperateLogDaoImpl getDao() {
		return dao;
	}

	public static void setDao(OperateLogDaoImpl dao) {
		OperateLogHelper.dao = dao;
	}

	// public static EventHandler getEventHandler() {
	// return eventHandler;
	// }
	//
	// public static void setEventHandler(EventHandler eventHandler) {
	// OperateLogHelper.eventHandler = eventHandler;
	// }
}
