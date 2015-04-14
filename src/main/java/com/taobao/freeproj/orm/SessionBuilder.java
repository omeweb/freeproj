package com.taobao.freeproj.orm;

/**
 * 用来创建Orm Session，一个builder对应一个connection，所以这里不再托管数据库连接对象
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2015年4月7日
 */
public interface SessionBuilder {
	/**
	 * @param batch
	 * @param tx 默认为0
	 * @param autoCommit
	 * @return
	 */
	Session build(boolean batch, int tx, boolean autoCommit);
}
