package com.supasulley.obs.impl;

import com.google.gson.JsonObject;
import com.supasulley.obs.requests.OBSRequest;

public class GetSceneItemEnabledRequest extends OBSRequest {
	
	private boolean sceneItemEnabled;
	
	/**
	 * Gets the enable state of a scene item.
	 * 
	 * @param sceneName name of the scene the item is in
	 * @param sceneItemId numeric ID of the scene item
	 */
	public GetSceneItemEnabledRequest(String sceneName, int sceneItemId)
	{
		super(json -> {
			json.addProperty("sceneName", sceneName);
			json.addProperty("sceneItemId", sceneItemId);
		});
	}
	
	/**
	 * @return whether the scene item is enabled
	 */
	public boolean isSceneItemEnabled()
	{
		return sceneItemEnabled;
	}
	
	@Override
	public String getRequestType()
	{
		return "GetSceneItemEnabled";
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.sceneItemEnabled = responseData.get("sceneItemEnabled").getAsBoolean();
	}
}
