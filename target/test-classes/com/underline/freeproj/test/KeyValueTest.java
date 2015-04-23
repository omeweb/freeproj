package com.underline.freeproj.test;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import tools.PagedList;

import com.underline.freeproj.common.SpringBeanUtil;
import com.underline.freeproj.dao.KeyValueDao;
import com.underline.freeproj.domain.KeyValue;

public class KeyValueTest extends BaseTest {
	KeyValueDao dao = SpringBeanUtil.getBean(KeyValueDao.class, "keyValueDao");

	@Test
	public void getListTest() {
		// System.out.println(dao == null);
		Assert.assertTrue(dao.getKeys("50").size() > 0);
	}

	// @Test
	public void addTest() {
		KeyValue entry = new KeyValue();
		entry.setTypeCode("50");
		entry.setKey("dyf.");
		entry.setValue("2012-08-03");
		dao.add(entry);
		// Assert.assertTrue(dao.getKeys("50").size() > 0);
	}

	@Test
	public void pagedTest() {
		Map<String, Object> data = tools.MapUtil.create();
		data.put("typeCode", 60);

		PagedList<KeyValue> list = dao.getPagedList(data, 0, 5);
		
		System.out.println(tools.Json.toString(list));

		Assert.assertEquals(5, list.getList().size());
	}
}
