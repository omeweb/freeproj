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
 * ����ibatis2��dao�㣬��ȷ���ڵ���dao�㷽��ʱ�����Ѿ��õ�У�� 2011-11-08 �����˷ֱ��� <br />
 * �ֱ����key�ķ�ʽ��һ��ʵ����ֻ����һ�ű� 2012-03-26
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
	 * ��ʾ�ֱ�ı�������ע��ķ�ʽ��ֵ 2013-08-02 by liusan.dyf
	 */
	private String key = "common_key_value";

	/**
	 * ת�Ƶ�kvdao���� 2015-4-21 10:06:13 by ����
	 */
	public void loadSettings() {
		// ===����ȫ�����ã������ݿ������
		KeyValue kv = this.getOne("50", "globalSettings");
		if (kv == null) {
			return; // ֱ���˳�
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
	 * 2012-03-12 by liusan.dyf 2012-04-11 �޸�Ϊpublic��������������д
	 * 
	 * @param entry
	 */
	public void validate(KeyValue entry) {
		if (entry == null)
			throw new IllegalArgumentException("entry������Ϊ��");

		if (tools.StringUtil.isNullOrEmpty(entry.getKey()))
			throw new IllegalArgumentException("entry.key����Ϊ��");

		// if (tools.Validate.isBlank(entry.getreservedValue()))
		entry.setReservedValue(getKey());// �м�

		// key������html���� 2012-02-15
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
			throw new IllegalArgumentException("entry������Ϊ��");

		// KeyValue keyValue = getOne(entry.getTypeCode(), entry.getKey());
		// if (keyValue != null)// ����
		// return update(entry);
		// else
		return add(entry);// ����
	}

	public long add(KeyValue entry) {
		validate(entry);

		entry.setStatus(10);// 2012-03-31

		// 2012-04-12ȡ���жϣ���insert ignore ���
		// // ͬһ���key�����ظ�
		// KeyValue keyValue = getOne(entry.getTypeId(), entry.getKey());
		// if (keyValue != null)
		// throw new IllegalArgumentException("key�ظ����������");

		// 2012-06-01 eventargs
		long v = tools.Convert.toLong(super.insert(entry), 0);

		return v == 0 ? 1 : v;// ��������ǲ����ܷ���0�ģ��������������жϣ�Ϊ0�ͷ���1 2015-4-10 12:01:24 by ����
	}

	public KeyValue getOne(String typeCode, String key) {
		// �������
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(KEY, key);
		data.put(TYPE_CODE, typeCode);
		data.put(RESERVED_VALUE, getKey());// �м�

		return super.getOne(data);
	}

	/**
	 * �����Ƕ�ȡ����kv��ʱ�򣬲Ż����value��link���Լ�����ȡĿ��kv 2013-07-10 by liusan.dyf
	 */
	@Override
	public KeyValue getFinalOne(String typeCode, String key) {
		KeyValue entry = this.getOne(typeCode, key);

		if (entry != null) {
			// ���value�ǡ�link:key1�������ʾ�ٴ�key1���ȡvalue 2013-07-10 by liusan.dyf
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
		// �������
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(TYPE_CODE, typeCode);
		data.put(RESERVED_VALUE, getKey());// �м�

		List<KeyValue> list = (List<KeyValue>) super.getListEx("getListByTypeCode", data);
		Map<String, String> map = new HashMap<String, String>();
		for (KeyValue item : list)
			map.put(item.getKey(), item.getValue());

		return map;
	}

	/**
	 * reservedValue����Ϊ��
	 * 
	 * @param entry
	 */
	// private void fix(KeyValue entry) {
	// if (entry != null)
	// entry.setreservedValue(null);
	// }

	/**
	 * 2014-11-17 by ���� �Ƿ��ǺϷ���orderBy�ֶ�
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
			orderBy = z.toString();// "`" + tools.StringUtil.escapeSQL(z.toString()) + "`";// 2012-04-19���ӷֽ��

		// 2012-11-23 by liusan.dyf
		boolean setDefaultOrderBy = false;
		if (!tools.StringUtil.isNullOrEmpty(orderBy)) {
			// 2013-05-22 ����lastUpdateTime by liusan.dyf
			// 2014-11-16 ����reservedInt by ����
			if (isValidOrderField(orderBy)) {
				// �Ϸ��������ֶ�
				query.put(ORDER_BY, "`" + orderBy + "`");// ���ӷֽ��
			} else
				setDefaultOrderBy = true;
		}

		if (setDefaultOrderBy)
			query.put(ORDER_BY, ID);// 2012-05-09 ǿ�����򣬷�ֹע��
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

		// ע�⣬�����String�����Ϊnull����toStringΪ��null����������������Ҫ�Ľ��
		// nosql 2011-11-07 19:20
		query.put(KEYWORD, keyword == null ? "" : StringUtil.escapeSQL(keyword));
		query.put(RESERVED_VALUE, getKey());// �м�

		return super.getPagedList(query, pageIndex, pageSize);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int addBatch(Collection<Object> list) {
		if (list == null || list.size() == 0)
			return 0;
		// TODO ������������Ҫ��ǿ
		int rtn = 0;
		try {
			// Collection<KeyValue> toInsert = new
			// ArrayList<KeyValue>(list.size());
			//
			// // �����û���ظ���
			// for (KeyValue item : list) {
			// if (getOne(item.getTypeId(), item.getKey()) == null)
			// toInsert.add(item);
			// }

			// ��ʼ�������� 2012-04-05

			String statusKey = "status";

			for (Object item : list) {
				// 2012-05-26
				// ��client�����ύKV���޷��������л�ΪCollection<KeyValue>��ֻ����Collection<Map>�ṹ
				if (item instanceof KeyValue) {
					((KeyValue) item).setReservedValue(getKey());
					super.insertEx("insert", item);
				} else if (item instanceof Map) {
					Map<String, Object> m = (Map<String, Object>) item;
					m.put(RESERVED_VALUE, getKey());

					if (!m.containsKey(statusKey))
						m.put(statusKey, 10);// ��ȫ����
					insertEx("insert_", item);
				}
			}
		} finally {

		}

		return rtn;
	}

	@Override
	public int deleteOne(String typeCode, String key) {
		if (tools.Validate.isNullOrEmpty(typeCode))// typeCode ���� 2014-11-17 by ����
			return 0;

		if (tools.Validate.isNullOrEmpty(key))// deleteOne ֻ����ɾ��һ�� 2014-11-17 by ����
			return 0;

		// �������
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(TYPE_CODE, typeCode);

		if (key != null && key.length() != 0) {// 2012-11-22 by liusan.dyf
			data.put(KEY, key);
		}

		data.put(RESERVED_VALUE, getKey());// �м�
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
		// �������
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(TYPE_CODE, typeCode);
		data.put(RESERVED_VALUE, getKey());// �м�

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
		if (tools.Validate.isNullOrEmpty(typeCode))// typeCode ���� 2014-11-17 by ����
			return 0;

		// �������
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(TYPE_CODE, typeCode);
		data.put(RESERVED_VALUE, getKey());// �м�

		// 2012-09-26 by liusan.dyf
		return (Integer) super.delete("delete", data);
	}

	@Override
	public KeyValue getById(long id) {
		// �������
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put(ID, id);
		data.put(RESERVED_VALUE, getKey());// �м�

		return (KeyValue) super.getObject("getById", data);
	}
}
