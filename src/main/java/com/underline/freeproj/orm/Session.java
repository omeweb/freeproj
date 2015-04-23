package com.underline.freeproj.orm;

import java.util.List;
import java.util.Map;

/**
 * 可以很方便的在ibatis和mybatis之间完成切换，抽象出该接口
 */
public interface Session {

	/**
	 * 是否正在批量处理中，主要是给ibatis的适配使用 2015-4-9 20:18:33 by 六三
	 *
	 * @return
	 */
	boolean isInBatch();

	/**
	 * 是否自动提交 2015-4-9 20:40:09 by 六三
	 * 
	 * @return
	 */
	boolean isAutoCommit();

	/**
	 * 事务隔离级别 2015-4-9 20:18:30 by 六三
	 *
	 * @return
	 */
	int getTransactionIsolationLevel();

	Object getRaw();

	/**
	 * Retrieve a single row mapped from the statement key
	 * 
	 * @param <T> the returned object type
	 * @param statement
	 * @return Mapped object
	 */
	<T> T selectOne(String statement);

	/**
	 * Retrieve a single row mapped from the statement key and parameter.
	 * 
	 * @param <T> the returned object type
	 * @param statement Unique identifier matching the statement to use.
	 * @param parameter A parameter object to pass to the statement.
	 * @return Mapped object
	 */
	<T> T selectOne(String statement, Object parameter);

	/**
	 * Retrieve a list of mapped objects from the statement key and parameter.
	 * 
	 * @param <E> the returned list element type
	 * @param statement Unique identifier matching the statement to use.
	 * @return List of mapped object
	 */
	<E> List<E> selectList(String statement);

	/**
	 * Retrieve a list of mapped objects from the statement key and parameter.
	 * 
	 * @param <E> the returned list element type
	 * @param statement Unique identifier matching the statement to use.
	 * @param parameter A parameter object to pass to the statement.
	 * @return List of mapped object
	 */
	<E> List<E> selectList(String statement, Object parameter);

	/**
	 * The selectMap is a special case in that it is designed to convert a list of results into a Map based on one of
	 * the properties in the resulting objects. Eg. Return a of Map[Integer,Author] for selectMap("selectAuthors","id")
	 * 
	 * @param <K> the returned Map keys type
	 * @param <V> the returned Map values type
	 * @param statement Unique identifier matching the statement to use.
	 * @param mapKey The property to use as key for each value in the list.
	 * @return Map containing key pair data.
	 */
	<K, V> Map<K, V> selectMap(String statement, String mapKey);

	/**
	 * The selectMap is a special case in that it is designed to convert a list of results into a Map based on one of
	 * the properties in the resulting objects.
	 * 
	 * @param <K> the returned Map keys type
	 * @param <V> the returned Map values type
	 * @param statement Unique identifier matching the statement to use.
	 * @param parameter A parameter object to pass to the statement.
	 * @param mapKey The property to use as key for each value in the list.
	 * @return Map containing key pair data.
	 */
	<K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);

	/**
	 * Execute an insert statement.
	 * 
	 * @param statement Unique identifier matching the statement to execute.
	 * @return int The number of rows affected by the insert.
	 */
	Object insert(String statement);

	/**
	 * Execute an insert statement with the given parameter object. Any generated autoincrement values or selectKey
	 * entries will modify the given parameter object properties. Only the number of rows affected will be returned.
	 * 
	 * @param statement Unique identifier matching the statement to execute.
	 * @param parameter A parameter object to pass to the statement.
	 * @return int The number of rows affected by the insert.
	 */
	Object insert(String statement, Object parameter);

	/**
	 * Execute an update statement. The number of rows affected will be returned.
	 * 
	 * @param statement Unique identifier matching the statement to execute.
	 * @return int The number of rows affected by the update.
	 */
	int update(String statement);

	/**
	 * Execute an update statement. The number of rows affected will be returned.
	 * 
	 * @param statement Unique identifier matching the statement to execute.
	 * @param parameter A parameter object to pass to the statement.
	 * @return int The number of rows affected by the update.
	 */
	int update(String statement, Object parameter);

	/**
	 * Execute a delete statement. The number of rows affected will be returned.
	 * 
	 * @param statement Unique identifier matching the statement to execute.
	 * @return int The number of rows affected by the delete.
	 */
	int delete(String statement);

	/**
	 * Execute a delete statement. The number of rows affected will be returned.
	 * 
	 * @param statement Unique identifier matching the statement to execute.
	 * @param parameter A parameter object to pass to the statement.
	 * @return int The number of rows affected by the delete.
	 */
	int delete(String statement, Object parameter);

	/**
	 * Flushes batch statements and commits database connection. Note that database connection will not be committed if
	 * no updates/deletes/inserts were called. To force the commit call {@link SqlSession#commit(boolean)}
	 */
	void commit();

	/**
	 * Flushes batch statements and commits database connection.
	 * 
	 * @param force forces connection commit
	 */
	void commit(boolean force);

	/**
	 * Discards pending batch statements and rolls database connection back. Note that database connection will not be
	 * rolled back if no updates/deletes/inserts were called. To force the rollback call
	 * {@link SqlSession#rollback(boolean)}
	 */
	void rollback();

	/**
	 * Discards pending batch statements and rolls database connection back. Note that database connection will not be
	 * rolled back if no updates/deletes/inserts were called.
	 * 
	 * @param force forces connection rollback
	 */
	void rollback(boolean force);

	/**
	 * Closes the session
	 */
	void close();
}
