package com.underline.freeproj.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.LogFactory;

import tools.PagedList;
import tools.cache.CacheItem;
import tools.cache.CacheProvider;
import tools.cache.HashMapCacheProvider;

import org.apache.commons.logging.Log;

import com.underline.freeproj.dao.KeyValueDao;
import com.underline.freeproj.domain.KeyValue;

/**
 * 针对KeyValueDao提供缓存服务，若被实例化多份，则相同的typeCode的kv会被缓存多份，注意init方法 2012-09-26<br />
 * 暂不提供延迟加载功能
 * 
 * @author liusan.dyf
 */
public class CachedKeyValueProvider extends tools.InitializeOnce {
	public static final int DEFAULT_TTL = -1;
	private static final Log logger = LogFactory.getLog("system");

	/**
	 * 支持延迟初始化 2012-11-23 by liusan.dyf<br />
	 * 2013-02-26取消static，这样可以缓存多个typeCode，只要该类的实例不一样 by liusan.dyf
	 */
	// private/* static */AtomicBoolean ready = new AtomicBoolean(false);

	private CacheProvider cacheProvider = new HashMapCacheProvider(new ConcurrentHashMap<String, CacheItem>());
	private KeyValueDao keyValueDao;
	private String typeCode = null;

	/**
	 * 要先设置typeCode
	 */
	@Override
	protected void doInitialize() {
		if (tools.StringUtil.isNullOrEmpty(typeCode))
			throw new IllegalArgumentException("typeCode不能为空");

		refreshAll();
	}

	public int refreshAll() {
		// 构造查询参数
		Map<String, Object> query = tools.MapUtil.create();
		query.put("typeCode", typeCode);

		PagedList<KeyValue> pagedList = keyValueDao.getPagedList(query, 0, Integer.MAX_VALUE);

		if (pagedList == null)
			return 0;

		// 缓存
		for (KeyValue item : pagedList.getList()) {
			getCacheProvider().set(item.getKey(), adjustingBeforeCache(item), DEFAULT_TTL);

			if (logger.isDebugEnabled())
				logger.debug("加载结束：" + typeCode + "." + item.getKey());
		}

		// logger.warn("加载" + typeCode + "结束，count：" + pagedList.getList().size());

		// LogFactory.getLog("system").warn(cacheProvider.getAll());

		return pagedList.getList().size();
	}

	/**
	 * 对原值进行一些修正 2013-03-04 by liusan.dyf
	 * 
	 * @param entry
	 * @return
	 */
	public KeyValue adjustingBeforeCache(KeyValue entry) {
		return entry;
	}

	/**
	 * 2013-03-04 by liusan.dyf
	 * 
	 * @param key
	 * @return
	 */
	public KeyValue get(String key) {
		return (KeyValue) getCacheProvider().get(key);
	}

	/**
	 * 从后端数据源处获取并做调整 2015-9-2 10:50:32 by liusan.dyf
	 * 
	 * @param key
	 * @return
	 */
	public KeyValue getFromBackendAndAdjust(String key) {
		return adjustingBeforeCache(keyValueDao.getOne(typeCode, key));
	}

	/**
	 * 这里不会从KV里删除 2013-09-28 by liusan.dyf
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(String key) {
		if (!this.isInitialized()) // 如果还没有准备好，则不执行 2013-03-08 by liusan.dyf
			return false;

		logger.warn("删除" + typeCode + "：" + key);

		// keyValueDao.deleteOne(typeCode, key);
		Object temp = getCacheProvider().remove(key);
		return temp != null;
	}

	public boolean reload(String key) {
		if (!this.isInitialized()) // 如果还没有准备好，则不执行 2013-03-08 by liusan.dyf
			return false;

		logger.warn("重新加载" + typeCode + "：" + key);

		KeyValue entry = getFromBackendAndAdjust(key);
		if (entry == null)
			return false;

		getCacheProvider().set(entry.getKey(), entry, DEFAULT_TTL);
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
