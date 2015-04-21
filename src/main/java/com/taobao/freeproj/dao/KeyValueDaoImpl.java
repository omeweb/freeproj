package com.taobao.freeproj.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.PagedList;
import tools.StringUtil;

import com.taobao.freeproj.domain.KeyValue;
import com.taobao.freeproj.orm.AbstractDao;

/**
 * 基于ibatis2的dao层，请确保在调用dao层方法时数据已经得到校验 2011-11-08 加入了分表功能 <br />
 * 分表采用key的方式，一个实例，只操作一张表 2012-03-26
 * 
 * @author liusan.dyf
 */
public class KeyValueDaoImpl extends AbstractDao<KeyValue> implements KeyValueDao {

	private static final String NAMESPACE = "keyValue";
	private static final String TYPE_CODE = "typeCode";
	private static final String KEY = "key";
	private static final String ID = "id";
	private static final String RESERVED_VALUE = "reservedValue";
	private static final String ORDER_BY = "orderBy";
	private static final String KEYWORD = "keyword";

	/**
	 * 表示分表的表名，用注入的方式赋值 2013-08-02 by liusan.dyf
	 */
	private String key = "common_key_value";

	/**
	 * 转移到kvdao里面 2015-4-21 10:06:13 by 六三
	 */
	public void loadSettings() {
		// ===加载全局设置，从数据库里加载
		KeyValue kv = this.getOne("50", "globalSettings");
		if (kv == null) {
			return; // 直接退出
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> settings = tools.Json.toObject(kv.getValue(), Map.class);

		// 2012-02-15
		if (settings == null) {
			settings = new HashMap<String, Object>();
		}

		tools.Global.setSettings(settings);
	}

	/**
	 * 2012-03-12 by liusan.dyf 2012-04-11 修改为public，想在子类里重写
	 * 
	 * @param entry
	 */
	public void validate(KeyValue entry) {
		if (entry == null)
			throw new IllegalArgumentException("entry对象不能为空");

		if (tools.StringUtil.isNullOrEmpty(entry.getKey()))
			throw new IllegalArgumentException("entry.key不能为空");

		// if (tools.Validate.isBlank(entry.getreservedValue()))
		entry.setReservedValue(getKey());// 切记

		// key不允许html代码 2012-02-15
		entry.setKey(tools.StringUtil.encodeHTML(entry.getKey()));

		// status 2012-03-31
		if (entry.getStatus() == 0)
			entry.setStatus(9527);
	}

	/**
	 * 2012-04-06 by liusan.dyf
	 * 
	 * @param entry
	 * @return
	 */
	public long save(KeyValue entry) {
		if (entry == null)
			throw new IllegalArgumentException("entry对象不能为空");

		// KeyValue keyValue = getOne(entry.getTypeCode(), entry.getKey());
		// if (keyValue != null)// 更新
		// return update(entry);
		// else
		return add(entry);// 插入
	}

	public long add(KeyValue entry) {
		validate(entry);

		entry.setStatus(10);// 2012-03-31

		// 2012-04-12取消判断，由insert ignore 完成
		// // 同一类别key不能重复
		// KeyValue keyValue = getOne(entry.getTypeId(), entry.getKey());
		// if (keyValue != null)
		// throw new IllegalArgumentException("key重复，不能添加");

		// 2012-06-01 eventargs
		long v = tools.Convert.toLong(super.insert(entry), 0);

		return v == 0 ? 1 : v;// 这个方法是不可能返回0的，所以这里做了判断，为0就返回1 2015-4-10 12:01:24 by 六三
	}

	public KeyValue getOne(String typeCode, String key) {
		// 构造参数
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(KEY, key);
		data.put(TYPE_CODE, typeCode);
		data.put(RESERVED_VALUE, getKey());// 切记

		return super.getOne(data);
	}

	/**
	 * 仅仅是读取单个kv的时候，才会根据value的link属性继续拉取目标kv 2013-07-10 by liusan.dyf
	 */
	@Override
	public KeyValue getFinalOne(String typeCode, String key) {
		KeyValue entry = this.getOne(typeCode, key);

		if (entry != null) {
			// 如果value是【link:key1】，则表示再从key1里读取value 2013-07-10 by liusan.dyf
			String value = entry.getValue();// eg link:targetKey[@targetTypeCode]
			String prefix = "link:";
			if (value != null && value.startsWith(prefix)) {
				String targetKey = value.substring(prefix.length());
				return this.getOne(typeCode, targetKey);
			}
		}
		return entry;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getMap(String typeCode) {
		// 构造参数
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(TYPE_CODE, typeCode);
		data.put(RESERVED_VALUE, getKey());// 切记

		List<KeyValue> list = (List<KeyValue>) super.getListEx("getListByTypeCode", data);
		Map<String, String> map = new HashMap<String, String>();
		for (KeyValue item : list)
			map.put(item.getKey(), item.getValue());

		return map;
	}

	/**
	 * reservedValue重置为空
	 * 
	 * @param entry
	 */
	// private void fix(KeyValue entry) {
	// if (entry != null)
	// entry.setreservedValue(null);
	// }

	/**
	 * 2014-11-17 by 六三 是否是合法的orderBy字段
	 * 
	 * @param orderBy
	 * @return
	 */
	private boolean isValidOrderField(String orderBy) {
		return "key_".equals(orderBy) || "value".equals(orderBy) || "lastUpdateTime".equals(orderBy)
				|| "reservedInt".equals(orderBy) || "sortNumber".equals(orderBy);
	}

	private void attachOrderBy(Map<String, Object> query) {
		String orderBy = null;
		Object z = query.get(ORDER_BY);
		if (z != null)
			orderBy = z.toString();// "`" + tools.StringUtil.escapeSQL(z.toString()) + "`";// 2012-04-19增加分解符

		// 2012-11-23 by liusan.dyf
		boolean setDefaultOrderBy = false;
		if (!tools.StringUtil.isNullOrEmpty(orderBy)) {
			// 2013-05-22 增加lastUpdateTime by liusan.dyf
			// 2014-11-16 增加reservedInt by 六三
			if (isValidOrderField(orderBy)) {
				// 合法的排序字段
				query.put(ORDER_BY, "`" + orderBy + "`");// 增加分解符
			} else
				setDefaultOrderBy = true;
		}

		if (setDefaultOrderBy)
			query.put(ORDER_BY, ID);// 2012-05-09 强制排序，防止注入
	}

	public PagedList<KeyValue> getPagedList(Map<String, Object> query, int pageIndex, int pageSize) {
		// if(typeId==0) typeId = 100;
		String typeCode = null;
		String keyword = null;

		if (query != null) {
			Object x = query.get(TYPE_CODE);
			if (x != null)
				typeCode = x.toString();

			Object y = query.get(KEYWORD);
			if (y != null)
				keyword = y.toString();
		} else
			query = new HashMap<String, Object>();

		if (typeCode != null)
			query.put(TYPE_CODE, typeCode);
		// if (orderBy != null)
		// query.put(ORDER_BY, orderBy);

		attachOrderBy(query);

		// 注意，这里的String，如果为null，则toString为【null】，并不是我们需要的结果
		// nosql 2011-11-07 19:20
		query.put(KEYWORD, keyword == null ? "" : StringUtil.escapeSQL(keyword));
		query.put(RESERVED_VALUE, getKey());// 切记

		return super.getPagedList(query, pageIndex, pageSize);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int addBatch(Collection<Object> list) {
		if (list == null || list.size() == 0)
			return 0;
		// TODO 批量操作这里要加强
		int rtn = 0;
		try {
			// Collection<KeyValue> toInsert = new
			// ArrayList<KeyValue>(list.size());
			//
			// // 检测有没有重复的
			// for (KeyValue item : list) {
			// if (getOne(item.getTypeId(), item.getKey()) == null)
			// toInsert.add(item);
			// }

			// 开始批量插入 2012-04-05

			String statusKey = "status";

			for (Object item : list) {
				// 2012-05-26
				// 从client批量提交KV，无法完整序列化为Collection<KeyValue>，只能是Collection<Map>结构
				if (item instanceof KeyValue) {
					((KeyValue) item).setReservedValue(getKey());
					super.insertEx("insert", item);
				} else if (item instanceof Map) {
					Map<String, Object> m = (Map<String, Object>) item;
					m.put(RESERVED_VALUE, getKey());

					if (!m.containsKey(statusKey))
						m.put(statusKey, 10);// 补全数据
					insertEx("insert_", item);
				}
			}
		} finally {

		}

		return rtn;
	}

	@Override
	public int deleteOne(String typeCode, String key) {
		if (tools.Validate.isNullOrEmpty(typeCode))// typeCode 必须 2014-11-17 by 六三
			return 0;

		if (tools.Validate.isNullOrEmpty(key))// deleteOne 只允许删除一个 2014-11-17 by 六三
			return 0;

		// 构造参数
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(TYPE_CODE, typeCode);

		if (key != null && key.length() != 0) {// 2012-11-22 by liusan.dyf
			data.put(KEY, key);
		}

		data.put(RESERVED_VALUE, getKey());// 切记
		return super.delete(data);
	}

	@Override
	public int update(KeyValue entry) {
		validate(entry);

		return super.update(entry);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getKeys(String typeCode) {
		// 构造参数
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(TYPE_CODE, typeCode);
		data.put(RESERVED_VALUE, getKey());// 切记

		// 2012-09-26 by liusan.dyf
		return (List<String>) super.getListEx("getKeysByTypeCode", data);

		// List<String> list;
		// try {
		// list = (List<String>) getSqlMapClient().queryForList(
		// "getKeysByTypeCode", data);
		//
		// return list;
		// } catch (SQLException e) {
		// throw new RuntimeException(e);
		// }
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public int deleteAll(String typeCode) {
		if (tools.Validate.isNullOrEmpty(typeCode))// typeCode 必须 2014-11-17 by 六三
			return 0;

		// 构造参数
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(TYPE_CODE, typeCode);
		data.put(RESERVED_VALUE, getKey());// 切记

		// 2012-09-26 by liusan.dyf
		return (Integer) super.delete("delete", data);
	}

	@Override
	public KeyValue getById(long id) {
		// 构造参数
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(ID, id);
		data.put(RESERVED_VALUE, getKey());// 切记

		return (KeyValue) super.getObject("getById", data);
	}
}
