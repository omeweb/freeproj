package com.taobao.freeproj.test;

import tools.event.EventArgs;
import tools.event.EventHandler;

public class DefaultEventHandler implements EventHandler {

	@Override
	public void onEvent(Object sender, EventArgs args) {
		System.out.println(tools.Json.toString(args));
	}
}
