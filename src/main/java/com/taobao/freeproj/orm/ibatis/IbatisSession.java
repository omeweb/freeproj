package com.taobao.freeproj.orm.ibatis;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.taobao.freeproj.orm.Session;

/**
 * 目前不支持事务
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2015年4月8日
 */
public class IbatisSession implements Session {
	private SqlMapClient raw;
	private boolean batch;
	private int tx;
	private boolean autoCommit;

	/**
	 * 是否已经执行过commit操作 2015-4-9 20:47:53 by 六三
	 */
	private boolean isCommitted = false;// committed

	public IbatisSession(SqlMapClient v, boolean batch, int tx, boolean autoCommit) {
		raw = v;

		this.batch = batch;
		this.tx = tx;
		this.autoCommit = autoCommit;
	}

	@Override
	public Object getRaw() {
		return raw;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T selectOne(String statement) {
		try {
			return (T) raw.queryForObject(statement);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T selectOne(String statement, Object parameter) {
		try {
			return (T) raw.queryForObject(statement, parameter);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> List<E> selectList(String statement) {
		try {
			return raw.queryForList(statement);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> List<E> selectList(String statement, Object parameter) {
		try {
			return raw.queryForList(statement, parameter);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		throw new tools.NotSupportedException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		try {
			return raw.queryForMap(statement, parameter, mapKey);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object insert(String statement) {
		try {
			return raw.insert(statement);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object insert(String statement, Object parameter) {
		try {
			return raw.insert(statement, parameter);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int update(String statement) {
		try {
			return raw.update(statement);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int update(String statement, Object parameter) {
		try {
			return raw.update(statement, parameter);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int delete(String statement) {
		try {
			return raw.delete(statement);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int delete(String statement, Object parameter) {
		try {
			return raw.delete(statement, parameter);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void commit() {
		commit(true);
	}

	@Override
	public void commit(boolean force) {
		// try {
		// raw.commitTransaction();
		// } catch (SQLException e) {
		// throw new RuntimeException(e);
		// }

		if (isCommitted)
			return;

		isCommitted = true;

		if (this.isInBatch()) {
			try {
				this.raw.executeBatch();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void rollback() {
		// rollback(true);
	}

	@Override
	public void rollback(boolean force) {
		// try {
		// raw.endTransaction();
		// } catch (SQLException e) {
		// throw new RuntimeException(e);
		// }
	}

	@Override
	public void close() {
		commit(true);
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
