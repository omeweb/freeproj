package com.underline.freeproj.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import tools.PagedList;
import tools.Resource;

import com.underline.freeproj.dao.KeyValueDao;
import com.underline.freeproj.domain.KeyValue;
import com.underline.freeproj.page.KeyValuePageSouceProvider;

public class PageProviderTest {
	static KeyValuePageSouceProvider provider;
	static {
		provider = new KeyValuePageSouceProvider();
		provider.setKeyValueDao(new KV());
		provider.setOutputWithComment(false);
		provider.init();
	}

	public static void main(String[] args) {
		System.out.println(provider.get("/part1.html"));
		System.out.println(provider.get("/part2.html"));
		System.out.println(provider.get("/include.html"));
		System.out.println(provider.get("/include_nest.html"));
		System.out.println(provider.get("/include_null.html"));

		// 依赖发生了变化
		provider.reload("/part1.html");

		System.out.println(provider.get("/include.html"));
		System.out.println(provider.get("/include_nest.html"));

		// 依赖发生了变化
		provider.remove("/part1.html");

		System.out.println(provider.get("/include.html"));
		System.out.println(provider.get("/include_nest.html"));
	}
}

class KV implements KeyValueDao {

	private List<KeyValue> list = null;

	@Override
	public int addBatch(Collection<Object> list) {
		throw new RuntimeException();
	}

	@Override
	public long add(KeyValue entry) {
		throw new RuntimeException();
	}

	@Override
	public long save(KeyValue entry) {
		throw new RuntimeException();
	}

	@Override
	public int update(KeyValue entry) {
		throw new RuntimeException();
	}

	@Override
	public int deleteOne(String typeCode, String key) {
		throw new RuntimeException();
	}

	@Override
	public int deleteAll(String typeCode) {
		throw new RuntimeException();
	}

	@Override
	public KeyValue getOne(String typeCode, String key) {
		// 这里对value做一些修改
		for (KeyValue item : list) {
			if (item.getKey().equals(key)) {
				item.setValue(item.getValue() + "--changed.");
				return item;
			}
		}
		return null;
	}

	@Override
	public KeyValue getFinalOne(String typeCode, String key) {
		return null;
	}

	@Override
	public Map<String, String> getMap(String typeCode) {
		throw new RuntimeException();
	}

	@Override
	public List<String> getKeys(String typeCode) {
		throw new RuntimeException();
	}

	@Override
	public PagedList<KeyValue> getPagedList(Map<String, Object> query, int pageIndex, int pageSize) {
		PagedList<KeyValue> pl = new PagedList<KeyValue>();
		pl.setCount(0);

		// 构造list
		String json = Resource.getResourceAsString(this.getClass().getClassLoader(), "kv.txt", "gbk");

		list = tools.Json.toObject(json, new com.fasterxml.jackson.core.type.TypeReference<List<KeyValue>>() {
		});

		pl.setList(list);

		return pl;
	}

	@Override
	public KeyValue getById(long id) {
		// TODO Auto-generated method stub
		return null;
	}
}
