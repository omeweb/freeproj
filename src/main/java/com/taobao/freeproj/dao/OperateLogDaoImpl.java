package com.taobao.freeproj.dao;

import tools.Global;

import com.taobao.freeproj.domain.OperateLog;
import com.taobao.freeproj.orm.AbstractDao;

public class OperateLogDaoImpl extends AbstractDao<OperateLog> {
	/**
	 * ϵͳ���
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
	 * 2012-05-24 by liusan.dyf
	 * 
	 * @param title eg �޸������¡��Ա�����
	 * @param content eg ��������
	 * @param code eg catalog.after-add
	 * @param targetKey eg ����id������12345
	 * @param result �����Ľ��
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

		int id = add(entry);
		entry.setId(id);

		// // 2013-07-16 by liusan.dyf
		// // ����Ļص���һ����־�ǲ��Զ�ִ��hook�ص��ģ���ֹ��ѭ��
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
