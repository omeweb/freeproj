package com.underline.freeproj.test;

import tools.event.EventArgs;
import tools.event.EventHandler;

public class DefaultEventHandler implements EventHandler {

	@Override
	public void fire(Object sender, EventArgs args) {
		System.out.println(tools.Json.toString(args));
	}
}
