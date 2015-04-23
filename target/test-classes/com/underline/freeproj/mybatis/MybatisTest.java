package com.underline.freeproj.mybatis;

import com.underline.freeproj.domain.Message;

public class MybatisTest extends BaseTest {
	public static void main(String[] args) {
		MessageDao dao = new MessageDao();
		System.out.println(tools.Json.toJson(dao.getOne(10)));

		Message entry = new Message();
		entry.setCreateTime(tools.DateTime.formatNow(null));
		entry.setTitle("Test");
		entry.setUserId(1114308877);
		entry.setUserName("namefree");
		entry.setSource("accounting");
		entry.setIp(tools.StringUtil.LOCAL_IP);
		entry.setOccurrenceTime(tools.DateTime.formatNow(null));

		// int id = (Integer) dao.insert(entry);
		// System.out.println(id);
	}
}
