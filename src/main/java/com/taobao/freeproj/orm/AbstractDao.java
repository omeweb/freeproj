package com.taobao.freeproj.orm;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tools.Convert;
import tools.PagedList;
import tools.event.EventArgs;
import tools.event.EventContainer;

/**
 * ��Ҫ��sqlmap���ṩ����key����䣺getPagedList��getCount��getOne��delete��update��insert��
 * getList��Ҫ��ibatisǿ��ʹ��namespace����������ӿڣ�����Ҫ�ṩnamespace<br />
 * getPagedList_hook_main = query��result=count��ͬgetList<br />
 * getOne_hook_main = id��result=entry<br />
 * ibatis Could not find SQL statement to include with refid 'xxx'������������õ�sqlƬ�ηŵ�ʹ������sqlǰ�棬�Ϳ�����<br />
 * xxx����������Ҫ���namespaceǰ׺�����Զ���� 2015-4-8 21:42:32 by ����
 * 
 * @author liusan.dyf
 * @param <T>
 */
public abstract class AbstractDao<T> {

	/**
	 * ���ﲻ��static����Ϊ���ò�ͬ��dao�����в�ͬ��logger 2012-08-09 by liusan.dyf
	 */
	private Log logger = LogFactory.getLog("orm");

	// 0��û������
	private int maxPageSize = 0;// �¿ͻ��˴�������pageSize�����·�������Ӧ�� 2015-1-19 13:37:02 by ����

	/*-------------����Ҫ�õ��ķ���------------*/

	public Session openSession(boolean batch, int tx, boolean autoCommit) {
		return Configuration.instance().getSession(this.getNamespace(), null, batch, tx, autoCommit);
	}

	public void onEvent(EventArgs args) {
		// ע�⣬����Ҫ�Ϸ���ѭ�� 2013-07-19 by liusan.dyf

		// String ns = getNamespace().toLowerCase();
		// if ("operatelog".equals(ns) || "revision".equals(ns) || ns.indexOf("counter") > -1)// 2012-09-19
		// return;

		// ����ʹ�þ�̬������ȡ��Ϊ�˷�ֹ��ÿ�������ﶼset�����ٷ��� 2013-12-16 by liusan.dyf
		EventContainer h = Configuration.instance().getEventContainer();
		if (h != null)
			h.onEvent(this, args);
	}

	public Log getLogger() {
		return logger;
	}

	public void setLogger(Log logger) {
		this.logger = logger;
	}

	/*-------------����Ҫ�õ��ķ���2 2012-08-10------------*/

	public abstract String getNamespace();

	/**
	 * 2012-08-10 by liusan.dyf��������sqlmap������getPagedList��getCount����
	 * 
	 * @param query
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PagedList<T> getPagedList(Map<String, Object> query, int pageIndex, int pageSize) {
		// ������ 2013-03-06 by liusan.dyf
		if (pageIndex < 0)
			pageIndex = 0;
		if (pageSize <= 0)
			pageSize = 10;

		// 2015-1-19 13:40:04 by ����
		if (maxPageSize != 0) {
			if (pageSize > maxPageSize) {
				throw new IllegalArgumentException("��������pageSize���󣬵�ǰΪ" + pageSize + "�����ܳ���" + maxPageSize);
			}
		}

		int skip = pageIndex * pageSize;

		if (query == null)
			query = tools.MapUtil.create();

		query.put("skip", skip);
		query.put("limit", pageSize);

		String statementId = "getPagedList";

		// hook�¼�
		String bizKey = getBizKey();
		EventArgs evtArgs = EventArgs.create(query).setType(bizKey + ".before-" + statementId);
		onEvent(evtArgs);

		List<T> list;
		Session session = openSession(false, 0, false);
		try {
			list = (List<T>) session.selectList(getNamespace() + "." + statementId, query);
			PagedList<T> rtn = new PagedList<T>();
			rtn.setList(list);

			// ����count��Ϣ
			if (!query.containsKey("noCount"))// 2013-03-06 by liusan.dyf
				if (pageIndex == 0) {
					int c = Convert.toInt(session.selectOne(getNamespace() + ".getCount", query), 0);
					rtn.setCount(c);
				}

			onEvent(evtArgs.set("result", list.size()).setType(bizKey + ".after-" + statementId));

			return rtn;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public T getOne(Object param) {
		return (T) getObject("getOne", param);
	}

	/**
	 * 2012-08-20 by liusan.dyf
	 * 
	 * @param statementId
	 * @param query
	 * @return
	 */
	public Object getObject(String statementId, Object query) {
		Object r = null;

		// hook�¼�
		String bizKey = getBizKey();
		EventArgs evtArgs = EventArgs.create(query).setType(bizKey + ".before-" + statementId);
		onEvent(evtArgs);

		Session session = openSession(false, 0, false);
		try {
			r = session.selectOne(getNamespace() + "." + statementId, query);
			onEvent(evtArgs.set("result", r).setType(bizKey + ".after-" + statementId));
		} finally {
			session.close();
		}

		return r;
	}

	/**
	 * 2012-08-23 by liusan.dyf���õ�sqlmap���getCount
	 * 
	 * @param params
	 * @return
	 */
	public int getCount(Object query) {
		Object obj = getObject("getCount", query);
		return tools.Convert.toInt(obj, 0);
	}

	/**
	 * @param statementId
	 * @param query
	 * @return
	 */
	public List<?> getListEx(String statementId, Object query) {
		if (query == null)
			return null;

		// hook�¼�
		String bizKey = getBizKey();
		EventArgs evtArgs = EventArgs.create(query).setType(bizKey + ".before-" + statementId);
		onEvent(evtArgs);

		List<?> r = null;
		Session session = openSession(false, 0, false);
		try {
			r = (List<?>) session.selectList(getNamespace() + "." + statementId, query);
			onEvent(evtArgs.set("result", r.size()).setType(bizKey + ".after-" + statementId));
		} finally {
			session.close();
		}
		return r;
	}

	@SuppressWarnings("unchecked")
	public List<T> getList(Object query) {
		return (List<T>) getListEx("getList", query);
	}

	/**
	 * ��Ҫ��delete��sqlmap
	 */
	public int delete(Object param) {
		return delete("delete", param);
	}

	/**
	 * 2012-09-17 by liusan.dyf
	 * 
	 * @param statementId
	 * @param param
	 * @return
	 */
	public int delete(String statementId, Object param) {
		int rtn = 0;
		String bizKey = getBizKey();

		EventArgs evtArgs = EventArgs.create(param).setType(bizKey + ".before-" + statementId);
		onEvent(evtArgs);

		Session session = openSession(false, 0, false);
		try {
			rtn = session.delete(getNamespace() + "." + statementId, param);
			onEvent(evtArgs.set("result", rtn).setType(bizKey + ".after-" + statementId));
			session.commit();
		} finally {
			session.close();
		}

		return rtn;
	}

	/**
	 * 2013-07-17 by liusan.dyf
	 * 
	 * @param statementId
	 * @param param
	 * @return
	 */
	public int updateEx(String statementId, Object param) {
		int rtn = 0;
		String bizKey = getBizKey();

		EventArgs evtArgs = EventArgs.create(param).setType(bizKey + ".before-" + statementId);
		onEvent(evtArgs);

		Session session = openSession(false, 0, false);
		try {
			rtn = session.update(getNamespace() + "." + statementId, param);
			onEvent(evtArgs.set("result", rtn).setType(bizKey + ".after-" + statementId));
			session.commit();
		} finally {
			session.close();
		}

		return rtn;
	}

	/**
	 * KeyValueMapper -�� keyvalue
	 * 
	 * @return
	 */
	private String getBizKey() {
		return getNamespace();
	}

	public int update(T entry) {
		// int rtn = 0;
		// String bizKey = getBizKey();
		//
		// String statementId = "update";
		//
		// try {
		// EventArgs evtArgs = EventArgs.create(entry).setType(bizKey + ".before-" + statementId);
		// onEvent(evtArgs);
		//
		// rtn = getSqlMapClient().update(getNamespace() + "." + statementId, entry);
		//
		// onEvent(evtArgs.set("result", rtn).setType(bizKey + ".after-" + statementId));
		// } catch (SQLException e) {
		// getLogger().error(e);
		// throw new RuntimeException(e);
		// }
		// return rtn;

		// 2013-07-17 by liusan.dyf
		return this.updateEx("update", entry);
	}

	public Object insert(T entry) {
		return insertEx("insert", entry);
	}

	/**
	 * 2012-09-19 by liusan.dyf
	 * 
	 * @param statementId
	 * @param entry
	 * @return
	 */
	public Object insertEx(String statementId, Object entry) {
		Object rtn = 0;
		String bizKey = getBizKey();

		EventArgs evtArgs = EventArgs.create(entry).setType(bizKey + ".before-" + statementId);
		onEvent(evtArgs);

		Session session = openSession(false, 0, false);
		try {
			// 2012-08-13��insert��һ���᷵��int
			rtn = session.insert(getNamespace() + "." + statementId, entry);
			// rtn = tools.Convert.toInt(obj, 0);// 2013-01-14 ���ܷ���long�������ݿ��bigint��������Ȼת��Ϊint
			session.commit();
		} finally {
			session.close();
		}

		evtArgs.setType(bizKey + ".after-" + statementId).set("result", rtn);
		onEvent(evtArgs);

		return rtn;
	}

	/**
	 * 2012-08-10 by liusan.dyf������Ҫ��sqlmap������insert����
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int insertBatch(List<T> list) {
		return insertBatch("insert", (List<Object>) list);
	}

	/**
	 * 2012-08-22 by liusan.dyf Ĭ��ʹ������
	 * 
	 * @param statementId
	 * @param list
	 * @return
	 */
	public int insertBatch(String statementId, List<Object> list) {
		return this.insertBatchEx(statementId, list, true);
	}

	/**
	 * ʹ�������أ�jsonrpc�޷�ʹ�ã�������ѡ������ʱ��ʹ������ 2013-02-25 by liusan.dyf
	 * 
	 * @param statementId
	 * @param list
	 * @param useTransaction
	 * @return
	 */
	public int insertBatchEx(String statementId, List<Object> list, boolean useTransaction) {
		int ret = 0;

		String fullStatementId = getNamespace() + "." + statementId;
		Session session = openSession(true, 0, false);
		try {
			// 2012-08-24 by liusan.dyf
			// client.getDataSource().getConnection().setAutoCommit(false);
			// if (useTransaction)
			// session.getConnection();

			for (Object item : list) {
				session.insert(fullStatementId, item);
			}

			session.commit();
		} finally {
			session.close();
		}

		return ret;
	}

	public int getMaxPageSize() {
		return maxPageSize;
	}

	public void setMaxPageSize(int maxPageSize) {
		this.maxPageSize = maxPageSize;
	}
}
