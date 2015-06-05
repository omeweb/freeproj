package com.underline.freeproj.page;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PageSourceProvider {
	String getSource(String key);

	boolean output(HttpServletRequest request, HttpServletResponse response, String charset) throws IOException;
}
