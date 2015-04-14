package com.taobao.freeproj.orm;

import java.util.Map;

import tools.event.EventArgs;
import tools.event.EventContainer;

public class Configuration extends tools.InitializeOnce {
	private EventContainer eventContainer;
	private Map<String, SessionBuilder> builderMap = null;

	private static Configuration instance = null;

	public static Configuration instance() {
		if (instance == null)
			throw new IllegalArgumentException("instance����Ϊnull����������");

		return instance;
	}

	/**
	 * ��̬�������ṩ�ӿ����ⲿ�����滻
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
			throw new IllegalArgumentException("builderMap����Ϊnull");

		// ��ȡ���ⶨ���
		SessionBuilder rtn = builderMap.get(key + ":" + route);
		if (rtn != null)
			return rtn.build(batch, tx, autoCommit);

		// ��ȡͨ�õ�
		return builderMap.get("*").build(batch, tx, autoCommit);
	}

	public void onEvent(Object sender, EventArgs args) {
		// ע�⣬����Ҫ�Ϸ���ѭ�� 2013-07-19 by liusan.dyf

		// String ns = getNamespace().toLowerCase();
		// if ("operatelog".equals(ns) || "revision".equals(ns) || ns.indexOf("counter") > -1)// 2012-09-19
		// return;

		// ����ʹ�þ�̬������ȡ��Ϊ�˷�ֹ��ÿ�������ﶼset�����ٷ��� 2013-12-16 by liusan.dyf
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
