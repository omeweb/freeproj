package com.underline.freeproj.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import tools.Global;

public class BaseTest {
	static {
		// System.out.println("...");
		String[] paths = { "classpath:bean/applicationContext.xml" };
		@SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
		System.out.println(ctx.getStartupDate());
		new Global().init();
	}
}
