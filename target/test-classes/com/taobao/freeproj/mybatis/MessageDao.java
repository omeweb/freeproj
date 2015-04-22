package com.taobao.freeproj.mybatis;

import com.taobao.freeproj.domain.Message;
import com.taobao.freeproj.orm.AbstractDao;

public class MessageDao extends AbstractDao<Message> {

	@Override
	public String getNamespace() {
		return "message";
	}

}
