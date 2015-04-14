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
 * 针对KeyValueDao提供缓存服务，若被实例化多份，则相同的typeCode的kv会被缓存多份，注意init方法 2012-09-26<br />
 * 暂不提供延迟加载功能
 * 
 * @author liusan.dyf
 */
public class CachedKeyValueManager extends tools.InitializeOnce {
	private static final Log logger = LogFactory.getLog("system");
	public static final int DEFAULT_TTL = -1;

	/**
	 * 支持延迟初始化 2012-11-23 by liusan.dyf<br />
	 * 2013-02-26取消static，这样可以缓存多个typeCode，只要该类的实例不一样 by liusan.dyf
	 */
	// private/* static */AtomicBoolean ready = new AtomicBoolean(false);

	private CacheProvider cacheProvider = new tools.cache.HashMapCacheProvider(
			new ConcurrentHashMap<String, CacheItem>());

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
			getCacheProvider().set(item.getKey(), getValueFrom(item), DEFAULT_TTL);

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
	 * 这里不会从KV里删除 2013-09-28 by liusan.dyf
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(String key) {
		if (!this.isInitialized())// 如果还没有准备好，则不执行 2013-03-08 by liusan.dyf
			return false;

		logger.warn("删除" + typeCode + "：" + key);

		// keyValueDao.deleteOne(typeCode, key);
		Object temp = getCacheProvider().remove(key);
		return temp != null;
	}

	public boolean reload(String key) {
		if (!this.isInitialized())// 如果还没有准备好，则不执行 2013-03-08 by liusan.dyf
			return false;

		logger.warn("重新加载" + typeCode + "：" + key);

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
