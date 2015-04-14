package com.taobao.freeproj.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.freeproj.dao.KeyValueDao;
import com.taobao.freeproj.domain.KeyValue;

/**
 * 使用静态方法，方便使用，spring bean，注意init-method = init
 * 
 * @author liusan.dyf
 */
public class Global extends tools.Global {
	private static final Log logger = LogFactory.getLog("system");// 日志
	private static KeyValueDao keyValueDao = null;// KV dao

	public void doInitialize() {
		super.doInitialize();
		loadSettings();
	}

	@SuppressWarnings("unchecked")
	public static void loadSettings() {
		// ===这里先不清理jobScheduler里的资源，因为下面addjob时会判重的

		if (getKeyValueDao() == null)// 2014-01-16 by liusan.dyf
			return;

		// ===加载全局设置，从数据库里加载
		KeyValue kv = getKeyValueDao().getOne("50", "globalSettings");
		if (kv == null) {
			logger.error("globalSettings并不存在，请配置");
			return; // 直接退出
		}

		Map<String, Object> settings = tools.Json.toObject(kv.getValue(), Map.class);

		logger.warn("globalSettings获取成功");

		// 2012-02-15
		if (settings == null) {
			logger.error("未配置settings");
			settings = new HashMap<String, Object>();
		}

		tools.Global.setSettings(settings);

		// ===2012-09-20 by liusan.dyf 取消该定时任务

		// // ===计数器持久化的job 2012-02-16
		// jobScheduler.addJob(new Date(), SaveCounterJob.class, null,
		// "COUNTER_JOB", tools.Convert.toInt(getSettings().getParams()
		// .get("counter_saving_interval"), 60), -1);

		logger.warn("global初始化结束");
	}

	public static KeyValueDao getKeyValueDao() {
		return keyValueDao;
	}

	public static void setKeyValueDao(KeyValueDao v) {
		keyValueDao = v;
	}
}
