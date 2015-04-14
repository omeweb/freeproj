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
	 * ֧�ֱַ� 2012-08-02 by liusan.dyf
	 */
	private String key = "common_catalog";

	@Override
	public long add(Catalog entry) {
		// ������ 2012-02-15 by liusan.dyf
		if (entry == null)
			throw new IllegalArgumentException("entry����Ϊnull");

		// title
		String title = entry.getTitle();

		if (tools.StringUtil.isNullOrEmpty(title))
			throw new IllegalArgumentException("entry.title����Ϊ��");

		// title��ֹHTML����
		entry.setTitle(tools.StringUtil.encodeHTML(title));

		entry.setReservedValue(getKey());// 2012-08-02

		// 2012-02-29ȡ����comment��洢��json����
		// entry.setComment(tools.StringUtil.encodeHTML(entry.getComment()));//
		// 2012-02-17

		String code = entry.getCode();
		int len = code.length();
		String parentCode = code.substring(0, len - getLeveledLength());

		// codeΪnull������һ�����
		if (tools.StringUtil.isNullOrEmpty(code)) {
			// // ����code
			// while (true) {
			// code = tools.StringUtil.getRndChars(LEN, DIGITS);// Ŀǰ��3λ
			// if (null == getOneByCode(code))
			// break;
			// }

			throw new IllegalArgumentException("entry.code����Ϊ��");
		} else {
			if ((len % getLeveledLength()) != 0)
				throw new IllegalArgumentException("entry.code�ĳ��ȱ�����" + getLeveledLength() + "�ı���");

			// ����ϵĿ¼�Ƿ����
			if (len > getLeveledLength()) { // 2012-02-16����len>LENʱ����
				if (null == getOne(parentCode))
					throw new IllegalArgumentException("��ϵĿ¼�������ڣ���ţ�" + parentCode);
			}
		}

		// �������û�code
		entry.setCode(code);
		entry.setParentPath(parentCode);// 2012-06-11
		// logger.info("parentCode:" + entry.getParentPath());
		entry.setLevel(entry.getCode().length() / getLeveledLength());

		long id = 0;
		Catalog c = getOne(entry.getCode());// ���code�Ƿ����ظ�
		if (c != null)
			id = -1;// �ظ�
		else {// ���ظ� ���Բ���

			// ���ͬ��Ŀ¼�Ƿ���ͬ���� 2012-03-08
			int i = checkUniquenessOfTheSiblingTitle(parentCode, entry.getTitle(), null);
			if (i > 0)
				throw new IllegalArgumentException("�ü����£��Ѿ�������������ˣ��뻻һ��");

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
	 * 2012-07-05 by liusan.dyf ��Ŀ�ƶ�
	 * 
	 * @param fromCode
	 * @param toCode
	 * @return
	 */
	public int move(String fromCode, String toCode) {
		if (tools.StringUtil.isNullOrEmpty(fromCode))// �����ж�toCode��Ŀ������Ƕ���Ŀ¼
			return 0;

		// �ж�Ŀ���Ƿ���fromCode���Ӽ�
		if (fromCode.startsWith(toCode))
			throw new IllegalArgumentException("Ŀ���������Ҫ�ƶ�����������");

		// �ж������Ƿ����
		Catalog from = getOne(fromCode);// fromCode����Ϊ�գ�����֪��ʲô��Ҫ���ƶ�
		Catalog to = null;
		if (!tools.StringUtil.isNullOrEmpty(toCode)) {// ֻ��toCode��Ϊ��ʱ��
			to = getOne(toCode);

			if (to == null)
				throw new IllegalArgumentException("Ŀ����𲻴���");
		}

		if (from == null)
			throw new IllegalArgumentException("Ҫ�ƶ�����𲻴���");

		//
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("from", fromCode);
		data.put("to", toCode);
		data.put("leveledLength", leveledLength);
		data.put("reservedValue", getKey());// 2012-08-02

		//
		int rtn = 0;

		// ������������¼� 2015-4-8 17:40:01 by ����
		Catalog entry = new Catalog();
		entry.setCode(fromCode);

		// �¼�
		tools.event.EventArgs evtArgs = tools.event.EventArgs.create(entry).setType("catalog.before-move");
		onEvent(evtArgs);

		// ����
		rtn = updateEx("move", data);

		onEvent(evtArgs.setType("catalog.after-move").set("result", rtn));

		return rtn;
	}

	/**
	 * 2012-03-08 ���ĳ��ϵ��Ŀ���Ƿ���ͬ�������Ŀ
	 * 
	 * @param parentCode ����Ϊ�գ�Ϊ�ձ�ʾһ����Ŀ
	 * @param title ��Ŀ���⣬����
	 * @param exCode �����ų�����Ŀ���
	 * @return
	 */
	public int checkUniquenessOfTheSiblingTitle(String parentCode, String title, String exCode) {
		int rtn = 0;

		Map<String, Object> data = new HashMap<String, Object>();
		if (!tools.StringUtil.isNullOrEmpty(parentCode)) {// ��ͨ��Ŀ
			data.put("parentCode", parentCode);
			data.put("level", (parentCode.length() / getLeveledLength()) + 1);
		} else
			data.put("level", 1);// һ����Ŀ

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
	 * ����code�ڵ㱾�� 2012-05-21
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
	 * 2012-07-31 by liusan.dyf where����԰���code����ϵ��Ŀ��ţ���orderby������orderby��ֵֻ��ΪupdateTime ����ʱ���ո���ʱ�����򣻷�������Ŀ��������
	 * 
	 * @param where
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Catalog> getListBy(Map<String, Object> where) {
		List<Catalog> list = null;

		String statement = "getList";

		// ibatis dynamic��䣬�����null�����Ҳ������� 2012-04-11
		if (where == null)
			where = new HashMap<String, Object>();
		else {
			if ("updateTime".equals(where.get("orderby")))
				statement = "getList_order_by_update_time";
		}

		// ִ��
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
		// ������ 2012-02-15 by liusan.dyf
		if (entry == null)
			throw new IllegalArgumentException("entry����Ϊnull");

		// title
		String title = entry.getTitle();

		if (tools.StringUtil.isNullOrEmpty(title))
			throw new IllegalArgumentException("entry.title����Ϊ��");

		// title��ֹHTML����
		entry.setTitle(tools.StringUtil.encodeHTML(title));

		entry.setReservedValue(getKey());// 2012-08-02

		// 2012-03-12ȡ�����޸�Ӧ�����õĵط���comment��json������encode
		// entry.setComment(tools.StringUtil.encodeHTML(entry.getComment()));//
		// 2012-02-17

		// code
		String code = entry.getCode();
		if (tools.StringUtil.isNullOrEmpty(code))
			throw new IllegalArgumentException("entry.code����Ϊ��");

		String parentCode = code.substring(0, code.length() - getLeveledLength());

		// ���ͬ��Ŀ¼�Ƿ���ͬ���� 2012-03-08
		int i = checkUniquenessOfTheSiblingTitle(parentCode, entry.getTitle(), code);
		if (i > 0)
			throw new IllegalArgumentException("ͬ����Ŀ�����ظ���");

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
