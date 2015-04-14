package com.taobao.freeproj.common;

import tools.Global;

import com.taobao.freeproj.dao.OperateLogDaoImpl;
import com.taobao.freeproj.domain.OperateLog;

/**
 * log4jʵ�������е��鷳��������log4j����ʽ��������־��ʽ��Ҫ����һ��object��ʹ�ò����㣬����Ҫ�������� <br />
 * ����EventHandler������Ļص���ʽ���������ڲ�����־֮����� 2013-07-16 <br />
 * 2013-07-19 ȡ�� by liusan.dyf
 * 
 * @author liusan.dyf
 */
public class OperateLogHelper {
	private static OperateLogDaoImpl dao;

	// private static EventHandler eventHandler = null;

	/**
	 * ϵͳ���
	 */
	private static int systemId;

	/**
	 * 2012-05-24 by liusan.dyf
	 * 
	 * @param title eg �޸������¡��Ա�����
	 * @param content eg ��������
	 * @param code eg catalog.after-add
	 * @param targetKey eg ����id������12345
	 * @param result �����Ľ��
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

		// ׷������
		tools.User u = Global.getCurrentUser();
		if (u != null) {
			// ����ת����"057526"����������� 2014-05-17 by liusan.dyf
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
		// // ����Ļص���һ����־�ǲ��Զ�ִ��hook�ص��ģ���ֹ��ѭ��
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
