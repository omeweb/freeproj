package com.taobao.freeproj.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import tools.PagedList;

import com.taobao.freeproj.domain.KeyValue;

/**
 * 为了适应不同的存储介质，而增加该接口 2011-11-08
 * 
 * @author liusan.dyf
 */
public interface KeyValueDao {
	/**
	 * 2011-11-11 by 63，为了改善批量插入性能
	 * 
	 * @param list
	 */
	int addBatch(Collection<Object> list);

	/**
	 * 返回插入后的id或者是其他什么东西 2012-04-12
	 * 
	 * @param entry
	 * @return
	 */
	long add(KeyValue entry);

	long save(KeyValue entry);

	int update(KeyValue entry);

	int deleteOne(String typeCode, String key);

	/**
	 * 2012-11-22 by liusan.dyf
	 * 
	 * @param typeCode
	 * @return
	 */
	int deleteAll(String typeCode);

	KeyValue getOne(String typeCode, String key);

	/**
	 * 2014-11-17 by 六三
	 * 
	 * @param id
	 * @return
	 */
	KeyValue getById(long id);

	/**
	 * 同一个typeCode下，多个key可以指向同一个value，这里找到最后指向的那个KV对象返回 2013-07-11 by liusan.dyf
	 * 
	 * @param typeCode
	 * @param key
	 * @return
	 */
	KeyValue getFinalOne(String typeCode, String key);

	Map<String, String> getMap(String typeCode);

	/**
	 * 2012-04-10 by liusan.dyf
	 * 
	 * @param typeId
	 * @return
	 */
	List<String> getKeys(String typeCode);

	PagedList<KeyValue> getPagedList(Map<String, Object> query, int pageIndex, int pageSize);
}
