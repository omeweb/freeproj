package com.taobao.freeproj.orm;

/**
 * ��������Orm Session��һ��builder��Ӧһ��connection���������ﲻ���й����ݿ����Ӷ���
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2015��4��7��
 */
public interface SessionBuilder {
	/**
	 * @param batch
	 * @param tx Ĭ��Ϊ0
	 * @param autoCommit
	 * @return
	 */
	Session build(boolean batch, int tx, boolean autoCommit);
}
