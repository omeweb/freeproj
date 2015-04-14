package com.taobao.freeproj.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tools.Convert;

import com.taobao.freeproj.common.CachedKeyValueManager;
import com.taobao.freeproj.domain.KeyValue;

/**
 * ע������KeyValueDao��typeCode��Ĭ�ϵ�typeCodeΪpageSource<br />
 * Ŀǰû�н��ѭ�����������⡣2013-09-29
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2012-11-14
 */
public class KeyValuePageSouceProvider extends CachedKeyValueManager implements PageSourceProvider {
	private static final Log logger = LogFactory.getLog("system");
	private int flag = 0;// Ϊ0��ʾҪ׷��ע�� 2014-04-27
	private String openIncludeTag = "<include>";// ��<>��ǣ���Ϊ���������ʧ�ܣ�ҳ�治����ʾ�쳣 2014-12-09 by ����
	private String closeIncludeTag = "</include>";

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getOpenIncludeTag() {
		return openIncludeTag;
	}

	public void setOpenIncludeTag(String openIncludeTag) {
		this.openIncludeTag = openIncludeTag;
	}

	public String getCloseIncludeTag() {
		return closeIncludeTag;
	}

	public void setCloseIncludeTag(String closeIncludeTag) {
		this.closeIncludeTag = closeIncludeTag;
	}

	@Override
	public void doInitialize() {
		super.setTypeCode("pageSource");
		super.doInitialize();
	}

	/**
	 * CachedKeyValueManager�Ľӿ�
	 */
	@Override
	public String getValueFrom(KeyValue entry) {
		if (entry == null)
			return null;

		String key = entry.getKey();
		if (key == null)
			key = "";

		// ������ֻ��������������� 2013-09-28 by liusan.dyf
		analyzeInclude(key, entry.getValue(), false);

		// ���flagΪ0��ʾҪ׷��ע�� 2013-09-28 by liusan.dyf
		if (flag == 0) {
			// һЩ��ע��������Ϣ
			String x = tools.StringUtil.LOCAL_HOST + " @ " + Convert.toString(entry.getLastUpdateTime()) + " by "
					+ entry.getLastOperator() + ", loaded at " + tools.MySqlFunction.now();

			// css��js����Դ�ļ� 2013-03-02 by liusan.dyf
			if (key.endsWith(".js") || key.endsWith(".css"))
				return entry.getValue() + "\r\n/* " + x + " */";
			else
				return entry.getValue() + "\r\n<!-- " + x + " -->";// ����html��ҳ���ļ�
		} else
			return entry.getValue();
	}

	/*------------------------Ϊ���include�������ֶκͷ��� 2013-09-28 by liusan.dyf-------*/

	private Set<String> changes = new java.util.concurrent.ConcurrentSkipListSet<String>();
	private Map<String, Set<String>> includes = tools.MapUtil.concurrentHashMap();// ������Ŀǰ�������������ǹ��ڵ�

	/**
	 * һ��Ҫ��ȫ������OK����ִ�У������е������û�м��أ��޷�����滻
	 */
	@Override
	public int refreshAll() {
		// ���ø��෽��
		int i = super.refreshAll();

		// ȫ�����ؽ����ˣ�Ҫ�滻��������������include
		Iterator<String> iterator = includes.keySet().iterator();

		String key = null;
		while (iterator.hasNext()) {
			key = iterator.next();
			if (includes.get(key).size() > 0) {
				// ����������־ 2014-12-09 by ����
				if (logger.isDebugEnabled())
					logger.debug("���" + key + "��������" + includes.get(key));

				refreshValue(key);
			}
		}

		return i;
	}

	@Override
	public String get(String key) {
		String value = super.get(key);

		// �п��Լ��Ƿ��б仯
		if (changes.contains(key)) {
			Object locker = includes.get(key);// key�������
			if (locker != null) {
				synchronized (locker) {
					if (changes.contains(key)) {
						try {
							logger.warn("��⵽�仯��" + key);
							this.reload(key);
						} finally {
							changes.remove(key);// ����OK�󣬰ѱ��ɾ��
						}
					}
				}

				return get(key);// ���µ��ñ�����
			}

			// û�����������仯
			return value;
		}

		return value;
	}

	@Override
	public boolean remove(String key) {
		markChanges(key);
		return super.remove(key);
	}

	@Override
	public boolean reload(String key) {
		if (!this.isInitialized()) {
			logger.warn("δ��ʼ��������ʧ�ܣ�" + key);// 2014-04-27 by liusan.dyf
			return false;
		}

		boolean f = super.reload(key);// ���¼��أ���ΪrefreshAll��ʱ����ܶ�ֵ�Ѿ������滻

		refreshValue(key);// ����ˢ��ֵ���������
		markChanges(key);// ��keyҲ�����˱仯���������

		return f;
	}

	private void refreshValue(String key) {
		// �������������
		String value = super.get(key);// һ��Ҫ�Ӹ���get�������get�ǻ��жϱ仯�����include��
		String newValue = analyzeInclude(key, value, true);

		if (logger.isDebugEnabled())
			logger.debug(key + "��value��" + newValue);

		// �洢��ֵ
		super.getCacheProvider().set(key, newValue, DEFAULT_TTL);
	}

	private void markChanges(String key) {
		// ����������������ָ����key��˭����
		Iterator<String> iterator = includes.keySet().iterator();// ֧�ֲ�������

		List<String> list = new ArrayList<String>();
		String itemKey = null;
		while (iterator.hasNext()) {
			itemKey = iterator.next();
			if (includes.get(itemKey).contains(key)) // ���ҵ������������֮
				list.add(itemKey);
		}

		if (list.size() > 0) {// 2014-12-25 17:50:56 by ����
			changes.addAll(list);
			logger.warn(key + "�����˸��ģ��ҵ���������������ע�˱仯��" + list);
		}
	}

	private String analyzeInclude(final String key, final String value, final boolean resolve) {
		// ���include�����⣬�п��������û�м��أ��п�����Ƕ������ 2013-09-26 by liusan.dyf

		final Set<String> dependenceList = new HashSet<String>();
		final String notExistsFormat = "";// "<!--{0} not exists-->";// ����html��������ͣ�����쳣

		// ��������
		tools.token.GenericTokenParser p = new tools.token.GenericTokenParser(openIncludeTag, closeIncludeTag,
				new tools.token.TokenHandler() {
					@Override
					public String handle(String token) {
						// ���ܺ�������<include>"/a.html"</include>
						String text = tools.StringUtil.replaceAll(token, "\"", "");
						text = tools.StringUtil.trim(text);// ȥ���ո�

						// �������
						dependenceList.add(text);
						// logger.warn("��⵽" + key + "��������" + content);

						if (resolve) {// ������include
							String value = get(text);
							if (value == null)
								logger.warn("�����ڵ�������" + text);
							return (value == null) ? tools.token.SimpleParser.format(notExistsFormat, text) : value;
						}

						return null;// tools.token.SimpleParser.format(notExistsFormat, text);
					}
				});

		// ����
		String newValue = p.parse(value);

		// ���������⣬�����Ǿɵ�ֵ
		includes.put(key, dependenceList);

		if (logger.isDebugEnabled())// ����debug����־ 2014-12-09 by ����
			logger.debug((resolve ? "���" : "����") + key + "��������" + dependenceList);

		return newValue;
	}
}

// class AnalysisResult {
// private String key;
// private Set<String> children;
// private String parentKey;
// }
