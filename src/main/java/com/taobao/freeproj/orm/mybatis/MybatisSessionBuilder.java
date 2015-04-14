package com.taobao.freeproj.orm.mybatis;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.taobao.freeproj.orm.Session;
import com.taobao.freeproj.orm.SessionBuilder;

public class MybatisSessionBuilder implements SessionBuilder {

	private SqlSessionFactory factory = null;

	@Override
	public Session build(boolean batch, int tx, boolean autoCommit) {
		if (factory == null)
			throw new IllegalArgumentException("SqlSessionFactory����Ϊnull");

		ExecutorType executorType = batch ? ExecutorType.BATCH : ExecutorType.SIMPLE;

		// TODO ������ʱ���������񼶱�
		SqlSession raw = factory.openSession(executorType, autoCommit);
		return new MybatisSession(raw, batch, tx, autoCommit);
	}

	public SqlSessionFactory getFactory() {
		return factory;
	}

	public void setFactory(SqlSessionFactory factory) {
		this.factory = factory;
	}
}
