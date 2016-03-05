package com.underline.freeproj.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import tools.PagedList;

import com.underline.freeproj.dao.OperateLogDaoImpl;
import com.underline.freeproj.domain.OperateLog;

public class OperateLogTest extends BaseTest {
	OperateLogDaoImpl dao = tools.spring.SpringContext.getBean(OperateLogDaoImpl.class, "operateLogDao");

	OperateLog entry = new OperateLog();

	@Test
	public void testInsert() {
		entry.setContent("test");
		entry.setCreateTime(new Date());
		entry.setUserId(102);
		entry.setUserName("wuchen.lx");
		// entry.setResult("success");
		entry.setOperationCode("del");
		entry.setSystemId(110);
		entry.setIp("127.0.0.1");
		entry.setTitle("{postId:29,revision:1}");
		System.out.println(dao.add(entry));
	}

	@Test
	public void testQuery() {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("userName", "wuchen.lx");
		// query.put("content", "test");
		// query.put("operationCode","upd");
		int pageIndex = 0;
		int pageSize = 20;
		PagedList<OperateLog> list = dao.getPagedList(query, pageIndex, pageSize);
		System.out.println(list.getCount());
	}
}
