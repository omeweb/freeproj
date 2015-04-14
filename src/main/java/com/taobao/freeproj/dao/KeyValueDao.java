package com.taobao.freeproj.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import tools.PagedList;

import com.taobao.freeproj.domain.KeyValue;

/**
 * Ϊ����Ӧ��ͬ�Ĵ洢���ʣ������Ӹýӿ� 2011-11-08
 * 
 * @author liusan.dyf
 */
public interface KeyValueDao {
	/**
	 * 2011-11-11 by 63��Ϊ�˸���������������
	 * 
	 * @param list
	 */
	int addBatch(Collection<Object> list);

	/**
	 * ���ز�����id����������ʲô���� 2012-04-12
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
	 * 2014-11-17 by ����
	 * 
	 * @param id
	 * @return
	 */
	KeyValue getById(long id);

	/**
	 * ͬһ��typeCode�£����key����ָ��ͬһ��value�������ҵ����ָ����Ǹ�KV���󷵻� 2013-07-11 by liusan.dyf
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
