package com.underline.freeproj.mybatis;

import com.underline.freeproj.domain.Message;
import com.underline.freeproj.orm.AbstractDao;

public class MessageDao extends AbstractDao<Message> {

	@Override
	public String getNamespace() {
		return "message";
	}

}
