package com.supasulley.obs.impl;

import com.google.gson.JsonObject;
import com.supasulley.obs.events.OBSEvent;

public class SceneTransitionEndedEvent extends OBSEvent {
	
	private String transitionName;
	
	public String getTransitionName()
	{
		return transitionName;
	}
	
	@Override
	public String getEventType()
	{
		return "SceneTransitionEnded";
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.transitionName = responseData.get("transitionName").getAsString();
	}
}
