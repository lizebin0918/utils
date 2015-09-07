package com.ztgame.logback.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class SampleFilter extends Filter<ILoggingEvent> {
	private String key;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (event.getMessage() != null && event.getMessage().contains(key)) {
			return FilterReply.ACCEPT;
		} else {
			return FilterReply.NEUTRAL;
		}
	}

}
