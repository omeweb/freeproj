package com.taobao.freeproj.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.freeproj.dao.KeyValueDao;
import com.taobao.freeproj.domain.KeyValue;

/**
 * ʹ�þ�̬����������ʹ�ã�spring bean��ע��init-method = init
 * 
 * @author liusan.dyf
 */
public class Global extends tools.Global {
	private static final Log logger = LogFactory.getLog("system");// ��־
	private static KeyValueDao keyValueDao = null;// KV dao

	public void doInitialize() {
		super.doInitialize();
		loadSettings();
	}

	@SuppressWarnings("unchecked")
	public static void loadSettings() {
		// ===�����Ȳ�����jobScheduler�����Դ����Ϊ����addjobʱ�����ص�

		if (getKeyValueDao() == null)// 2014-01-16 by liusan.dyf
			return;

		// ===����ȫ�����ã������ݿ������
		KeyValue kv = getKeyValueDao().getOne("50", "globalSettings");
		if (kv == null) {
			logger.error("globalSettings�������ڣ�������");
			return; // ֱ���˳�
		}

		Map<String, Object> settings = tools.Json.toObject(kv.getValue(), Map.class);

		logger.warn("globalSettings��ȡ�ɹ�");

		// 2012-02-15
		if (settings == null) {
			logger.error("δ����settings");
			settings = new HashMap<String, Object>();
		}

		tools.Global.setSettings(settings);

		// ===2012-09-20 by liusan.dyf ȡ���ö�ʱ����

		// // ===�������־û���job 2012-02-16
		// jobScheduler.addJob(new Date(), SaveCounterJob.class, null,
		// "COUNTER_JOB", tools.Convert.toInt(getSettings().getParams()
		// .get("counter_saving_interval"), 60), -1);

		logger.warn("global��ʼ������");
	}

	public static KeyValueDao getKeyValueDao() {
		return keyValueDao;
	}

	public static void setKeyValueDao(KeyValueDao v) {
		keyValueDao = v;
	}
}
