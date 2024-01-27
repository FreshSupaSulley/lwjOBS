package com.supasulley.obs.impl;

import com.supasulley.obs.requests.EmptyGetResponse;

public class SetSceneItemEnabled extends EmptyGetResponse {
	
	/**
	 * Sets the enable state of a scene item.
	 * 
	 * @param sceneName name of the scene the item is in
	 * @param sceneItemId numeric ID of the scene item
	 * @param sceneItemEnabled new enable state of the scene item
	 */
	public SetSceneItemEnabled(String sceneName, int sceneItemId, boolean sceneItemEnabled)
	{
		super(json -> {
			json.addProperty("sceneName", sceneName);
			json.addProperty("sceneItemId", sceneItemId);
			json.addProperty("sceneItemEnabled", sceneItemEnabled);
		});
	}
	
	@Override
	public String getRequestType()
	{
		return "SetSceneItemEnabled";
	}
}
