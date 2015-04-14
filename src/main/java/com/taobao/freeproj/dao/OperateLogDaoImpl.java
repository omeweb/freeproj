package com.taobao.freeproj.dao;

import com.taobao.freeproj.domain.OperateLog;
import com.taobao.freeproj.orm.AbstractDao;

public class OperateLogDaoImpl extends AbstractDao<OperateLog> {
	public int add(OperateLog entry) {
		return tools.Convert.toInt(super.insert(entry), 0);
	}

	@Override
	public String getNamespace() {
		return "operateLog";
	}
}
