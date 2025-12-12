package io.github.freshsupasulley.lwjobs.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.freshsupasulley.lwjobs.requests.EmptyGetRequest;

public class GetSceneListRequest extends EmptyGetRequest {
	
	private String currentScene;
	private String[] scenes;
	
	public String getCurrentScene()
	{
		return currentScene;
	}
	
	public String[] getScenes()
	{
		return scenes;
	}
	
	@Override
	public String getRequestType()
	{
		return "GetSceneList";
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.currentScene = responseData.get("currentProgramSceneName").getAsString();
		
		// Parse scenes
		JsonArray scenes = responseData.get("scenes").getAsJsonArray();
		this.scenes = new String[scenes.size()];
		
		for(int i = 0; i < scenes.size(); i++)
		{
			JsonObject object = scenes.get(i).getAsJsonObject();
			int index = object.get("sceneIndex").getAsInt();
			this.scenes[index] = object.get("sceneName").getAsString();
		}
	}
}
