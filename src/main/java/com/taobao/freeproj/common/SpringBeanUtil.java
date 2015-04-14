package com.taobao.freeproj.common;

/**
 * 使用静态方法，方便使用，记得该类要被spring创建一次
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2012-2-28
 */
public class SpringBeanUtil {

	public static Object getBean(String name) {
		return tools.spring.SpringContext.getBean(name);
	}

	/**
	 * 2012-03-05 by liusan.dyf
	 * 
	 * @param t
	 * @param name
	 * @return
	 */
	public static <T> T getBean(Class<T> t, String name) {
		return tools.spring.SpringContext.getBean(t, name);
	}
}
