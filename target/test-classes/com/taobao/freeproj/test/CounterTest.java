package com.taobao.freeproj.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import tools.PagedList;

import com.taobao.freeproj.common.SpringBeanUtil;
import com.taobao.freeproj.dao.CounterDaoImpl;
import com.taobao.freeproj.domain.Counter;

public class CounterTest extends BaseTest {

	CounterDaoImpl dao = SpringBeanUtil.getBean(CounterDaoImpl.class, "counterDao");

	Counter entry = new Counter();

	@Test
	public void testQuery() {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("key", "rule");
		int pageIndex = 0;
		int pageSize = 20;
		PagedList<Counter> list = dao.getPagedList(query, pageIndex, pageSize);
		System.out.println(list.getList());
		List<Counter> clist = (List<Counter>) list.getList();
		System.out.println(clist.get(0).getValue());
		System.out.println(list.getCount());
	}
}
