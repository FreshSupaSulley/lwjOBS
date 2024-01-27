package com.supasulley.obs.impl;

import com.supasulley.obs.events.EmptyEvent;

public class ExitStartedEvent extends EmptyEvent {
	
	@Override
	public String getEventType()
	{
		return "ExitStarted";
	}
}
