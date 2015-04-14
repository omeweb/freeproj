package com.taobao.freeproj.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.LogFactory;

import tools.PagedList;
import tools.cache.CacheItem;
import tools.cache.CacheProvider;

import org.apache.commons.logging.Log;
import com.taobao.freeproj.dao.KeyValueDao;
import com.taobao.freeproj.domain.KeyValue;

/**
 * ���KeyValueDao�ṩ�����������ʵ������ݣ�����ͬ��typeCode��kv�ᱻ�����ݣ�ע��init���� 2012-09-26<br />
 * �ݲ��ṩ�ӳټ��ع���
 * 
 * @author liusan.dyf
 */
public class CachedKeyValueManager extends tools.InitializeOnce {
	private static final Log logger = LogFactory.getLog("system");
	public static final int DEFAULT_TTL = -1;

	/**
	 * ֧���ӳٳ�ʼ�� 2012-11-23 by liusan.dyf<br />
	 * 2013-02-26ȡ��static���������Ի�����typeCode��ֻҪ�����ʵ����һ�� by liusan.dyf
	 */
	// private/* static */AtomicBoolean ready = new AtomicBoolean(false);

	private CacheProvider cacheProvider = new tools.cache.HashMapCacheProvider(
			new ConcurrentHashMap<String, CacheItem>());

	private KeyValueDao keyValueDao;
	private String typeCode = null;

	/**
	 * Ҫ������typeCode
	 */
	@Override
	protected void doInitialize() {
		if (tools.StringUtil.isNullOrEmpty(typeCode))
			throw new IllegalArgumentException("typeCode����Ϊ��");

		refreshAll();
	}

	public int refreshAll() {
		// �����ѯ����
		Map<String, Object> query = tools.MapUtil.create();
		query.put("typeCode", typeCode);

		PagedList<KeyValue> pagedList = keyValueDao.getPagedList(query, 0, Integer.MAX_VALUE);

		if (pagedList == null)
			return 0;

		// ����
		for (KeyValue item : pagedList.getList()) {
			getCacheProvider().set(item.getKey(), getValueFrom(item), DEFAULT_TTL);

			if (logger.isDebugEnabled())
				logger.debug("���ؽ�����" + typeCode + "." + item.getKey());
		}

		// logger.warn("����" + typeCode + "������count��" + pagedList.getList().size());

		// LogFactory.getLog("system").warn(cacheProvider.getAll());

		return pagedList.getList().size();
	}

	/**
	 * ��ԭֵ����һЩ���� 2013-03-04 by liusan.dyf
	 * 
	 * @param entry
	 * @return
	 */
	public String getValueFrom(KeyValue entry) {
		return entry.getValue();
	}

	/**
	 * 2013-03-04 by liusan.dyf
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return (String) getCacheProvider().get(key);
	}

	/**
	 * ���ﲻ���KV��ɾ�� 2013-09-28 by liusan.dyf
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(String key) {
		if (!this.isInitialized())// �����û��׼���ã���ִ�� 2013-03-08 by liusan.dyf
			return false;

		logger.warn("ɾ��" + typeCode + "��" + key);

		// keyValueDao.deleteOne(typeCode, key);
		Object temp = getCacheProvider().remove(key);
		return temp != null;
	}

	public boolean reload(String key) {
		if (!this.isInitialized())// �����û��׼���ã���ִ�� 2013-03-08 by liusan.dyf
			return false;

		logger.warn("���¼���" + typeCode + "��" + key);

		KeyValue entry = keyValueDao.getOne(typeCode, key);
		getCacheProvider().set(entry.getKey(), getValueFrom(entry), DEFAULT_TTL);
		return true;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public KeyValueDao getKeyValueDao() {
		return keyValueDao;
	}

	public void setKeyValueDao(KeyValueDao v) {
		keyValueDao = v;
	}

	public CacheProvider getCacheProvider() {
		return cacheProvider;
	}
}
