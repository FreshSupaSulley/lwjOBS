package com.supasulley.obs.impl;

import com.google.gson.JsonObject;
import com.supasulley.obs.events.OBSEvent;

public class StreamStateChangedEvent extends OBSEvent {
	
	private boolean outputActive;
	private String outputState;
	
	public boolean isOutputActive()
	{
		return outputActive;
	}
	
	public String getOutputState()
	{
		return outputState;
	}
	
	@Override
	public String getEventType()
	{
		return "StreamStateChanged";
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.outputActive = responseData.get("outputActive").getAsBoolean();
		this.outputState = responseData.get("outputState").getAsString();
	}
}
