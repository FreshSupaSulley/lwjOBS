package io.github.freshsupasulley.lwjobs.impl;

import io.github.freshsupasulley.lwjobs.events.EmptyEvent;

public class ExitStartedEvent extends EmptyEvent {
	
	@Override
	public String getEventType()
	{
		return "ExitStarted";
	}
}
