package com.underline.freeproj.dao;

import com.underline.freeproj.orm.AbstractDao;

/**
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2013-11-27
 */
public class CommonDaoImpl extends AbstractDao<Object> {
	@Override
	public String getNamespace() {
		return "common";
	}
}
