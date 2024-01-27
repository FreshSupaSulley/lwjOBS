package com.supasulley.obs.impl;

import com.google.gson.JsonObject;
import com.supasulley.obs.requests.OBSRequest;

public class GetSceneItemIdRequest extends OBSRequest {
	
	private int sceneItemId;
	
	/**
	 * Searches a scene for a source, and returns its id.
	 * 
	 * @param sceneName name of the scene or group to search in
	 * @param sourceName name of the source to find
	 * @param searchOffset number of matches to skip during search. >= 0 means first forward. -1 means last (top) item
	 */
	public GetSceneItemIdRequest(String sceneName, String sourceName, int searchOffset)
	{
		super(json -> {
			json.addProperty("sceneName", sceneName);
			json.addProperty("sourceName", sourceName);
			json.addProperty("searchOffset", searchOffset);
		});
	}
	
	/**
	 * @return numeric ID of the scene item
	 */
	public int getSceneItemId()
	{
		return sceneItemId;
	}
	
	@Override
	public String getRequestType()
	{
		return "GetSceneItemId";
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.sceneItemId = responseData.get("sceneItemId").getAsInt();
	}
}
