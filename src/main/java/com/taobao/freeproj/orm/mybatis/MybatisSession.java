package com.taobao.freeproj.orm.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.taobao.freeproj.orm.Session;

/**
 * 把mybatis的session对象转换为orm的session对象
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2015年4月7日
 */
public class MybatisSession implements Session {

	private SqlSession raw = null;
	private boolean batch;
	private int tx;
	private boolean autoCommit;

	public MybatisSession(SqlSession v, boolean batch, int tx, boolean autoCommit) {
		if (v == null)
			throw new IllegalArgumentException("SqlSession不能为空");

		raw = v;
		this.batch = batch;
		this.tx = tx;
		this.autoCommit = autoCommit;
	}

	@Override
	public Object getRaw() {
		return raw;
	}

	@Override
	public <T> T selectOne(String statement) {
		return this.raw.selectOne(statement);
	}

	@Override
	public <T> T selectOne(String statement, Object parameter) {
		return this.raw.selectOne(statement, parameter);
	}

	@Override
	public <E> List<E> selectList(String statement) {
		return this.raw.selectList(statement);
	}

	@Override
	public <E> List<E> selectList(String statement, Object parameter) {
		return this.raw.selectList(statement, parameter);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		return this.raw.selectMap(statement, mapKey);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		return this.raw.selectMap(statement, parameter, mapKey);
	}

	@Override
	public Object insert(String statement) {
		return this.raw.insert(statement);
	}

	@Override
	public Object insert(String statement, Object parameter) {
		return this.raw.insert(statement, parameter);
	}

	@Override
	public int update(String statement) {
		return this.raw.delete(statement);
	}

	@Override
	public int update(String statement, Object parameter) {
		return this.raw.update(statement, parameter);
	}

	@Override
	public int delete(String statement) {
		return this.raw.delete(statement);
	}

	@Override
	public int delete(String statement, Object parameter) {
		return this.raw.delete(statement, parameter);
	}

	@Override
	public void commit() {
		this.raw.commit();
	}

	@Override
	public void commit(boolean force) {
		this.raw.commit(force);
	}

	@Override
	public void rollback() {
		this.raw.rollback();
	}

	@Override
	public void rollback(boolean force) {
		this.raw.rollback(force);
	}

	@Override
	public void close() {
		this.raw.close();
	}

	@Override
	public boolean isInBatch() {
		return batch;
	}

	@Override
	public boolean isAutoCommit() {
		return autoCommit;
	}

	@Override
	public int getTransactionIsolationLevel() {
		return tx;
	}
}
