package com.taobao.freeproj.orm.ibatis;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.taobao.freeproj.orm.Session;
import com.taobao.freeproj.orm.SessionBuilder;

public class IbatisSessionBuilder implements SessionBuilder {

	private SqlMapClient sqlMapClient = null;

	@Override
	public Session build(boolean batch, int tx, boolean autoCommit) {
		if (batch)
			try {
				sqlMapClient.startBatch();
			} catch (SQLException e) {

			}

		try {
			if (autoCommit && sqlMapClient.getCurrentConnection() != null)
				sqlMapClient.getCurrentConnection().setAutoCommit(autoCommit);
		} catch (SQLException e) {

		}

		return new IbatisSession(sqlMapClient, batch, tx, autoCommit);
	}

	public SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}

	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		this.sqlMapClient = sqlMapClient;
	}
}
