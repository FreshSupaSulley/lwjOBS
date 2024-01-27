package com.supasulley.obs.impl;

import com.google.gson.JsonObject;
import com.supasulley.obs.events.OBSEvent;

public class CurrentProgramSceneChangedEvent extends OBSEvent {
	
	private String sceneName;
	
	public String getSceneName()
	{
		return sceneName;
	}
	
	@Override
	public String getEventType()
	{
		return "CurrentProgramSceneChanged";
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.sceneName = responseData.get("sceneName").getAsString();
	}
}
