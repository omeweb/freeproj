package com.underline.freeproj.orm;

import java.util.Map;

import tools.event.EventArgs;
import tools.event.EventContainer;

public class Configuration extends tools.InitializeOnce {
	private EventContainer eventContainer;
	private Map<String, SessionBuilder> builderMap = null;

	private static Configuration instance = null;

	public static Configuration instance() {
		if (instance == null)
			throw new IllegalArgumentException("Configuration instance不能为null，请检查配置");

		return instance;
	}

	/**
	 * 静态方法，提供接口让外部可以替换
	 * 
	 * @param v
	 */
	public static void setNewInstance(Configuration v) {
		instance = v;
	}

	/**
	 * @param key
	 * @param route
	 * @param batch
	 * @param tx
	 * @param autoCommit
	 * @return
	 */
	public Session getSession(String key, String route, boolean batch, int tx, boolean autoCommit) {
		if (builderMap == null)
			throw new IllegalArgumentException("builderMap不能为null");

		// 先取特殊定义的
		SessionBuilder rtn = builderMap.get(key + ":" + route);
		if (rtn != null)
			return rtn.build(batch, tx, autoCommit);

		// 再取通用的
		return builderMap.get("*").build(batch, tx, autoCommit);
	}

	public void onEvent(Object sender, EventArgs args) {
		// 注意，这里要严防死循环 2013-07-19 by liusan.dyf

		// String ns = getNamespace().toLowerCase();
		// if ("operatelog".equals(ns) || "revision".equals(ns) || ns.indexOf("counter") > -1)// 2012-09-19
		// return;

		// 这里使用静态方法获取，为了防止在每个子类里都set，减少繁琐 2013-12-16 by liusan.dyf
		if (eventContainer != null)
			eventContainer.onEvent(sender, args);
	}

	public EventContainer getEventContainer() {
		return eventContainer;
	}

	public void setEventContainer(EventContainer v) {
		eventContainer = v;
	}

	public Map<String, SessionBuilder> getBuilderMap() {
		return builderMap;
	}

	public void setBuilderMap(Map<String, SessionBuilder> v) {
		builderMap = v;
	}

	@Override
	protected void doInitialize() {
		instance = this;
	}
}
