package com.taobao.freeproj.page;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public interface PageSourceProvider {
	String get(String key);

	boolean output(String url, String charset, HttpServletResponse httpResponse) throws IOException;
}
