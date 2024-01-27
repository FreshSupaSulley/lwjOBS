package com.supasulley.obs.impl;

import com.google.gson.JsonObject;
import com.supasulley.obs.events.OBSEvent;

public class RecordStateChangedEvent extends OBSEvent {
	
	private boolean outputActive;
	private String outputState, outputPath;
	
	public boolean isOutputActive()
	{
		return outputActive;
	}
	
	public String getOutputState()
	{
		return outputState;
	}
	
	public String getOutputPath()
	{
		return outputPath;
	}
	
	@Override
	public String getEventType()
	{
		return "RecordStateChanged";
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.outputActive = responseData.get("outputActive").getAsBoolean();
		this.outputState = responseData.get("outputState").getAsString();
		this.outputPath = responseData.has("outputPath") ? responseData.get("outputPath").getAsString() : "";
	}
}
