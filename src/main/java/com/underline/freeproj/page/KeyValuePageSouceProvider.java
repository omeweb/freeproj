package com.underline.freeproj.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tools.Convert;

import com.underline.freeproj.common.CachedKeyValueProvider;
import com.underline.freeproj.domain.KeyValue;

import javax.servlet.http.*;

/**
 * 注意设置KeyValueDao和typeCode，默认的typeCode为pageSource<br />
 * 目前没有解决循环依赖的问题。2013-09-29
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2012-11-14
 */
public class KeyValuePageSouceProvider extends CachedKeyValueProvider implements PageSourceProvider {
	private static final Log logger = LogFactory.getLog("system");

	private boolean outputWithComment = true;// 表示要追加注释 2014-04-27
	private String openIncludeTag = "<include>";// 用<>标记，因为：如果解析失败，页面不会显示异常 2014-12-09 by 六三
	private String closeIncludeTag = "</include>";

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
	 * CachedKeyValueManager的接口
	 */
	@Override
	public KeyValue adjustingBeforeCache(KeyValue entry) {
		if (entry == null)
			return null;

		String key = entry.getKey();
		if (key == null)
			key = "";

		// 新增：只分析依赖，不解决 2013-09-28 by liusan.dyf
		analyzeIncludes(key, entry.getValue(), false);

		// 表示要追加注释 2013-09-28 by liusan.dyf
		if (outputWithComment) {
			// 一些备注、调试信息
			String x = tools.StringUtil.LOCAL_HOST + " @ " + Convert.toString(entry.getLastUpdateTime()) + " by "
					+ entry.getLastOperator() + ", loaded at " + tools.MySqlFunction.now();

			// css、js等资源文件 2013-03-02 by liusan.dyf
			if (key.endsWith(".js") || key.endsWith(".css")) {
				entry.setValue(entry.getValue() + "\r\n/* " + x + " */");// 资源文件
				return entry;
			} else {
				entry.setValue(entry.getValue() + "\r\n<!-- " + x + " -->");// 其他html等页面文件
				return entry;
			}
		} else
			return entry;
	}

	/*------------------------为解决include而新增字段和方法 2013-09-28 by liusan.dyf-------*/

	private Set<String> changes = new java.util.concurrent.ConcurrentSkipListSet<String>();
	private Map<String, Set<String>> includes = tools.MapUtil.concurrentHashMap();// 仅仅是目前的依赖，可能是过期的

	/**
	 * 一定要在全部加载OK了再执行，否则有的依赖项还没有加载，无法完成替换
	 */
	@Override
	public int refreshAll() {
		// 调用父类方法，已经把内容刷新到了内存里了
		int i = super.refreshAll();

		// 全部加载结束了，要替换里面的依赖，解决include
		Iterator<String> iterator = includes.keySet().iterator();

		String key = null;
		while (iterator.hasNext()) {
			key = iterator.next();
			if (includes.get(key).size() > 0) {
				// 当作调试日志 2014-12-09 by 六三
				if (logger.isDebugEnabled())
					logger.debug("检测" + key + "的依赖：" + includes.get(key));

				refreshDependentValue(key);
			}
		}

		return i;
	}

	/**
	 * 2015-4-22 21:15:11 by liusan.dyf
	 * 
	 * @param request
	 * @param respone
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	@Override
	public boolean output(HttpServletRequest request, HttpServletResponse response, String charset) throws IOException {
		if (request == null)
			return false;

		String url = request.getRequestURI();

		// 得到etag，这里是对应value的hashcode 2015-6-5 21:24:13 by liusan.dyf
		// from http://www.infoq.com/cn/articles/etags
		KeyValue entry = this.get(url);
		if (entry == null)
			return false; // 无法处理

		String v = entry.getValue();
		return tools.web.ServletUtil.output(request, response, v, charset, entry.getLastUpdateTime());
	}

	@Override
	public KeyValue get(String key) {
		KeyValue value = super.get(key);

		// 有看自己是否有变化
		if (changes.contains(key)) {
			Object locker = includes.get(key);// key级别的锁
			if (locker != null) {
				synchronized (locker) {
					if (changes.contains(key)) {
						try {
							logger.warn("检测到变化：" + key);
							this.reload(key);
						} finally {
							changes.remove(key);// 加载OK后，把变更删除
						}
					}
				}

				return get(key);// 重新调用本方法
			}

			// 没有依赖发生变化
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
			logger.warn("未初始化，加载失败：" + key);// 2014-04-27 by liusan.dyf
			return false;
		}

		boolean f = super.reload(key);// 重新加载，因为refreshAll的时候可能对值已经做了替换

		refreshDependentValue(key);// 重新刷新值，解决依赖
		markChanges(key);// 该key也发生了变化，继续标记

		return f;
	}

	/**
	 * 刷新有依赖的key的value
	 * 
	 * @param key
	 */
	private void refreshDependentValue(String key) {
		KeyValue entry = super.get(key);
		if (entry == null)
			return;

		// 分析并解决依赖
		String value = entry.getValue();// 一定要从父类get，，本类的get是会判断变化、解决include的
		String newValue = analyzeIncludes(key, value, true);

		if (logger.isDebugEnabled())
			logger.debug(key + "新value：" + newValue);

		// 存储新值
		entry.setValue(newValue);

		super.getCacheProvider().set(key, entry, DEFAULT_TTL);
	}

	private void markChanges(String key) {
		// 遍历依赖树，看看指定的key被谁依赖
		Iterator<String> iterator = includes.keySet().iterator();// 支持并发遍历

		List<String> list = new ArrayList<String>();
		String itemKey = null;
		while (iterator.hasNext()) {
			itemKey = iterator.next();
			if (includes.get(itemKey).contains(key)) // 查找到了依赖，标记之
				list.add(itemKey);
		}

		if (list.size() > 0) {// 2014-12-25 17:50:56 by 六三
			changes.addAll(list);
			logger.warn(key + "发生了更改，找到了它的依赖并标注了变化：" + list);
		}
	}

	private String analyzeIncludes(final String key, final String value, final boolean resolve) {
		// 解决include的问题，有可能依赖项还没有加载，有可能是嵌套依赖 2013-09-26 by liusan.dyf

		final Set<String> dependenceList = new HashSet<String>();
		final String notExistsFormat = "";// "<!--{0} not exists-->";// 仅限html，别的类型，会出异常

		// 解析依赖
		tools.token.GenericTokenParser p = new tools.token.GenericTokenParser(openIncludeTag, closeIncludeTag,
				new tools.token.TokenHandler() {
					@Override
					public String handle(String token) {
						// 不能含有引号<include>"/a.html"</include>
						String text = tools.StringUtil.replaceAll(token, "\"", "");
						text = tools.StringUtil.trim(text);// 去掉空格

						// 添加依赖
						dependenceList.add(text);
						// logger.warn("检测到" + key + "的依赖：" + content);

						if (resolve) {// 如果解决include
							String value = getSource(text);
							if (value == null)
								logger.warn("不存在的依赖：" + text);
							return (value == null) ? tools.token.SimpleParser.format(notExistsFormat, text) : value;
						}

						return null;// tools.token.SimpleParser.format(notExistsFormat, text);
					}
				});

		// 分析
		String newValue = p.parse(value);

		// 加入依赖库，并覆盖旧的值
		includes.put(key, dependenceList);

		if (logger.isDebugEnabled()) // 当作debug的日志 2014-12-09 by 六三
			logger.debug((resolve ? "解决" : "分析") + key + "的依赖：" + dependenceList);

		return newValue;
	}

	@Override
	public String getSource(String key) {
		KeyValue entry = this.get(key);
		if (entry == null)
			return null;

		return entry.getValue();
	}

	public boolean isOutputWithComment() {
		return outputWithComment;
	}

	public void setOutputWithComment(boolean outputWithComment) {
		this.outputWithComment = outputWithComment;
	}
}

// class AnalysisResult {
// private String key;
// private Set<String> children;
// private String parentKey;
// }
