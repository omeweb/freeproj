package com.underline.freeproj.orm;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tools.Convert;
import tools.PagedList;
import tools.event.EventArgs;
import tools.event.EventContainer;

/**
 * 需要在sqlmap里提供如下key的语句：getPagedList、getCount、getOne、delete、update、insert、
 * getList。要求ibatis强制使用namespace，不过对外接口，则不需要提供namespace<br />
 * getPagedList_hook_main = query，result=count，同getList<br />
 * getOne_hook_main = id，result=entry<br />
 * ibatis Could not find SQL statement to include with refid 'xxx'，将这个被引用的sql片段放到使用它的sql前面，就可以了<br />
 * xxx方法都不需要添加namespace前缀，会自动添加 2015-4-8 21:42:32 by 六三
 * 
 * @author liusan.dyf
 * @param <T>
 */
public abstract class AbstractDao<T> {

	/**
	 * 这里不用static，是为了让不同的dao可以有不同的logger 2012-08-09 by liusan.dyf
	 */
	private Log logger = LogFactory.getLog("orm");

	// 0并没有启用
	private int maxPageSize = 0;// 怕客户端传入过大的pageSize而导致服务器响应慢 2015-1-19 13:37:02 by 六三

	private boolean autoCommit = false;// 可以配置autoCommit 2015-7-13 13:44:14 by liusan.dyf

	/*-------------子类要用到的方法------------*/

	public Session openSession(boolean batch, int tx, boolean autoCommit) {
		return Configuration.instance().getSession(this.getNamespace(), null, batch, tx, autoCommit);
	}

	public void onEvent(EventArgs args) {
		// 注意，这里要严防死循环 2013-07-19 by liusan.dyf

		// String ns = getNamespace().toLowerCase();
		// if ("operatelog".equals(ns) || "revision".equals(ns) || ns.indexOf("counter") > -1)// 2012-09-19
		// return;

		// 这里使用静态方法获取，为了防止在每个子类里都set，减少繁琐 2013-12-16 by liusan.dyf
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

	/*-------------子类要用到的方法2 2012-08-10------------*/

	public abstract String getNamespace();

	/**
	 * 2012-08-10 by liusan.dyf，必须在sqlmap里配置getPagedList和getCount方法
	 * 
	 * @param query
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PagedList<T> getPagedList(Map<String, Object> query, int pageIndex, int pageSize) {
		// 检查参数 2013-03-06 by liusan.dyf
		if (pageIndex < 0)
			pageIndex = 0;
		if (pageSize <= 0)
			pageSize = 10;

		// 2015-1-19 13:40:04 by 六三
		if (maxPageSize != 0) {
			if (pageSize > maxPageSize) {
				throw new IllegalArgumentException("参数错误，pageSize过大，当前为" + pageSize + "，不能超过" + maxPageSize);
			}
		}

		int skip = pageIndex * pageSize;

		if (query == null)
			query = tools.MapUtil.create();

		query.put("skip", skip);
		query.put("limit", pageSize);

		String statementId = "getPagedList";

		// hook事件
		String bizKey = getBizKey();
		EventArgs evtArgs = EventArgs.create(query).setType(bizKey + ".before-" + statementId);
		onEvent(evtArgs);

		List<T> list;
		Session session = openSession(false, 0, autoCommit);
		try {
			list = (List<T>) session.selectList(getNamespace() + "." + statementId, query);
			PagedList<T> rtn = new PagedList<T>();
			rtn.setList(list);

			// 设置count信息
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

		// hook事件
		String bizKey = getBizKey();
		EventArgs evtArgs = EventArgs.create(query).setType(bizKey + ".before-" + statementId);
		onEvent(evtArgs);

		Session session = openSession(false, 0, autoCommit);
		try {
			r = session.selectOne(getNamespace() + "." + statementId, query);
			onEvent(evtArgs.set("result", r).setType(bizKey + ".after-" + statementId));
		} finally {
			session.close();
		}

		return r;
	}

	/**
	 * 2012-08-23 by liusan.dyf，用到sqlmap里的getCount
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

		// hook事件
		String bizKey = getBizKey();
		EventArgs evtArgs = EventArgs.create(query).setType(bizKey + ".before-" + statementId);
		onEvent(evtArgs);

		List<?> r = null;
		Session session = openSession(false, 0, autoCommit);
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
	 * 需要有delete的sqlmap
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

		Session session = openSession(false, 0, autoCommit);
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

		Session session = openSession(false, 0, autoCommit);
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
	 * KeyValueMapper -》 keyvalue
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

		Session session = openSession(false, 0, autoCommit);
		try {
			// 2012-08-13，insert不一定会返回int
			rtn = session.insert(getNamespace() + "." + statementId, entry);
			// rtn = tools.Convert.toInt(obj, 0);// 2013-01-14 可能返回long，即数据库的bigint，这里依然转换为int
			session.commit();
		} finally {
			session.close();
		}

		evtArgs.setType(bizKey + ".after-" + statementId).set("result", rtn);
		onEvent(evtArgs);

		return rtn;
	}

	/**
	 * 2012-08-10 by liusan.dyf，必须要在sqlmap里配置insert方法
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int insertBatch(List<T> list) {
		return insertBatch("insert", (List<Object>) list);
	}

	/**
	 * 2012-08-22 by liusan.dyf 默认使用事务
	 * 
	 * @param statementId
	 * @param list
	 * @return
	 */
	public int insertBatch(String statementId, List<Object> list) {
		return this.insertBatchEx(statementId, list, true);
	}

	/**
	 * 使用了重载（jsonrpc无法使用），可以选择批量时不使用事务 2013-02-25 by liusan.dyf
	 * 
	 * @param statementId
	 * @param list
	 * @param useTransaction
	 * @return
	 */
	public int insertBatchEx(String statementId, List<Object> list, boolean useTransaction) {
		int ret = 0;

		String fullStatementId = getNamespace() + "." + statementId;
		Session session = openSession(true, 0, false);// 这里的autoCommit应该是false 2015-7-13 13:47:35 by liusan.dyf
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

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
}
