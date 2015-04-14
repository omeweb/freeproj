package com.taobao.freeproj.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.freeproj.domain.Catalog;
import com.taobao.freeproj.orm.AbstractDao;

public class CatalogDaoImpl extends AbstractDao<Catalog> implements CatalogDao {
	private static final String NAMESPACE = "catalog";

	// private static final String DIGITS = "0123456789";
	private static final int DEFAULT_LEVELED_LENGTH = 3;

	private int leveledLength = DEFAULT_LEVELED_LENGTH;

	/**
	 * 支持分表 2012-08-02 by liusan.dyf
	 */
	private String key = "common_catalog";

	@Override
	public long add(Catalog entry) {
		// 检查参数 2012-02-15 by liusan.dyf
		if (entry == null)
			throw new IllegalArgumentException("entry不能为null");

		// title
		String title = entry.getTitle();

		if (tools.StringUtil.isNullOrEmpty(title))
			throw new IllegalArgumentException("entry.title不能为空");

		// title防止HTML代码
		entry.setTitle(tools.StringUtil.encodeHTML(title));

		entry.setReservedValue(getKey());// 2012-08-02

		// 2012-02-29取消，comment里存储有json配置
		// entry.setComment(tools.StringUtil.encodeHTML(entry.getComment()));//
		// 2012-02-17

		String code = entry.getCode();
		int len = code.length();
		String parentCode = code.substring(0, len - getLeveledLength());

		// code为null，当作一级类别
		if (tools.StringUtil.isNullOrEmpty(code)) {
			// // 生成code
			// while (true) {
			// code = tools.StringUtil.getRndChars(LEN, DIGITS);// 目前是3位
			// if (null == getOneByCode(code))
			// break;
			// }

			throw new IllegalArgumentException("entry.code不能为空");
		} else {
			if ((len % getLeveledLength()) != 0)
				throw new IllegalArgumentException("entry.code的长度必须是" + getLeveledLength() + "的倍数");

			// 看父系目录是否存在
			if (len > getLeveledLength()) { // 2012-02-16，当len>LEN时才做
				if (null == getOne(parentCode))
					throw new IllegalArgumentException("父系目录并不存在，编号：" + parentCode);
			}
		}

		// 重新设置回code
		entry.setCode(code);
		entry.setParentPath(parentCode);// 2012-06-11
		// logger.info("parentCode:" + entry.getParentPath());
		entry.setLevel(entry.getCode().length() / getLeveledLength());

		long id = 0;
		Catalog c = getOne(entry.getCode());// 检查code是否有重复
		if (c != null)
			id = -1;// 重复
		else {// 不重复 可以插入

			// 检查同级目录是否有同名的 2012-03-08
			int i = checkUniquenessOfTheSiblingTitle(parentCode, entry.getTitle(), null);
			if (i > 0)
				throw new IllegalArgumentException("该级别下，已经存在这个名称了，请换一个");

			// onEvent(evtArgs);
			//
			// try {
			// getSqlMapClient().insert("insert", entry);
			// id = 1;
			// } catch (SQLException e) {
			// getLogger().error(e);
			// throw new RuntimeException(e);
			// }
			//
			// evtArgs.setType("catalog.after-add").set("result", i);
			// onEvent(evtArgs);

			id = tools.Convert.toLong(super.insert(entry), 0);
		}
		return id;
	}

	/**
	 * 2012-07-05 by liusan.dyf 类目移动
	 * 
	 * @param fromCode
	 * @param toCode
	 * @return
	 */
	public int move(String fromCode, String toCode) {
		if (tools.StringUtil.isNullOrEmpty(fromCode))// 不必判断toCode，目标可能是顶级目录
			return 0;

		// 判断目标是否是fromCode的子集
		if (fromCode.startsWith(toCode))
			throw new IllegalArgumentException("目标类别不能是要移动的类别的子类");

		// 判断两种是否存在
		Catalog from = getOne(fromCode);// fromCode不能为空，否则不知道什么类要被移动
		Catalog to = null;
		if (!tools.StringUtil.isNullOrEmpty(toCode)) {// 只有toCode不为空时才
			to = getOne(toCode);

			if (to == null)
				throw new IllegalArgumentException("目标类别不存在");
		}

		if (from == null)
			throw new IllegalArgumentException("要移动的类别不存在");

		//
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("from", fromCode);
		data.put("to", toCode);
		data.put("leveledLength", leveledLength);
		data.put("reservedValue", getKey());// 2012-08-02

		//
		int rtn = 0;

		// 发射出单独的事件 2015-4-8 17:40:01 by 六三
		Catalog entry = new Catalog();
		entry.setCode(fromCode);

		// 事件
		tools.event.EventArgs evtArgs = tools.event.EventArgs.create(entry).setType("catalog.before-move");
		onEvent(evtArgs);

		// 更新
		rtn = updateEx("move", data);

		onEvent(evtArgs.setType("catalog.after-move").set("result", rtn));

		return rtn;
	}

	/**
	 * 2012-03-08 检查某父系类目下是否有同标题的类目
	 * 
	 * @param parentCode 可以为空，为空表示一级类目
	 * @param title 类目标题，必须
	 * @param exCode 可以排除的类目编号
	 * @return
	 */
	public int checkUniquenessOfTheSiblingTitle(String parentCode, String title, String exCode) {
		int rtn = 0;

		Map<String, Object> data = new HashMap<String, Object>();
		if (!tools.StringUtil.isNullOrEmpty(parentCode)) {// 普通类目
			data.put("parentCode", parentCode);
			data.put("level", (parentCode.length() / getLeveledLength()) + 1);
		} else
			data.put("level", 1);// 一级类目

		data.put("title", title);

		if (!tools.StringUtil.isNullOrEmpty(exCode))
			data.put("exCode", exCode);

		// logger.debug(data);
		data.put("reservedValue", getKey());// 2012-08-02

		rtn = (Integer) getObject("checkUniquenessOfTheSiblingTitle", data);

		return rtn;
	}

	@Override
	public int deleteAll(String code) {
		// 2012-05-24 by liusan.dyf
		Catalog entry = new Catalog();
		entry.setCode(code);

		entry.setReservedValue(getKey());// 2012-08-02

		return super.delete(entry);
	}

	/**
	 * 包含code节点本身 2012-05-21
	 */
	@Override
	public List<Catalog> getAllEx(String code) {
		if (tools.StringUtil.isNullOrEmpty(code))
			return getAll();

		HashMap<String, Object> data = new HashMap<String, Object>(1);
		data.put("code", code);
		data.put("reservedValue", getKey());// 2012-08-02

		return super.getList(data);
	}

	/**
	 * 2012-07-31 by liusan.dyf where里可以包含code（父系类目编号）、orderby，其中orderby的值只能为updateTime ，此时按照更新时间排序；否则按照类目级别排序。
	 * 
	 * @param where
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Catalog> getListBy(Map<String, Object> where) {
		List<Catalog> list = null;

		String statement = "getList";

		// ibatis dynamic语句，如果传null，则找不到属性 2012-04-11
		if (where == null)
			where = new HashMap<String, Object>();
		else {
			if ("updateTime".equals(where.get("orderby")))
				statement = "getList_order_by_update_time";
		}

		// 执行
		where.put("reservedValue", getKey());// 2012-08-02
		list = (List<Catalog>) getListEx(statement, where);

		return list;
	}

	@Override
	public List<Catalog> getSibling(String code, int level) {

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("code", code);
		data.put("level", level);
		data.put("reservedValue", getKey());// 2012-08-02

		return super.getList(data);
	}

	@Override
	public List<Catalog> getAll() {
		Map<String, Object> data = tools.MapUtil.create();
		data.put("reservedValue", getKey());// 2012-08-02
		return super.getList(data);
	}

	@Override
	public Catalog getOne(String code) {
		if (tools.StringUtil.isNullOrEmpty(code))
			return null;

		Map<String, Object> data = tools.MapUtil.create();
		data.put("reservedValue", getKey());// 2012-08-02
		data.put("code", code);

		return super.getOne(data);
	}

	@Override
	public int update(Catalog entry) {
		// 检查参数 2012-02-15 by liusan.dyf
		if (entry == null)
			throw new IllegalArgumentException("entry不能为null");

		// title
		String title = entry.getTitle();

		if (tools.StringUtil.isNullOrEmpty(title))
			throw new IllegalArgumentException("entry.title不能为空");

		// title防止HTML代码
		entry.setTitle(tools.StringUtil.encodeHTML(title));

		entry.setReservedValue(getKey());// 2012-08-02

		// 2012-03-12取消，修改应用配置的地方，comment是json，不能encode
		// entry.setComment(tools.StringUtil.encodeHTML(entry.getComment()));//
		// 2012-02-17

		// code
		String code = entry.getCode();
		if (tools.StringUtil.isNullOrEmpty(code))
			throw new IllegalArgumentException("entry.code不能为空");

		String parentCode = code.substring(0, code.length() - getLeveledLength());

		// 检查同级目录是否有同名的 2012-03-08
		int i = checkUniquenessOfTheSiblingTitle(parentCode, entry.getTitle(), code);
		if (i > 0)
			throw new IllegalArgumentException("同级类目名称重复了");

		return super.update(entry);
	}

	/**
	 * 2012-05-16 by liusan.dyf
	 * 
	 * @return
	 */
	public int getLeveledLength() {
		return leveledLength;
	}

	public void setLeveledLength(int leveledLength) {
		this.leveledLength = leveledLength;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}
}
