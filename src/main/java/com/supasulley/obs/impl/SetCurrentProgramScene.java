package com.supasulley.obs.impl;

import com.supasulley.obs.requests.EmptyGetResponse;

public class SetCurrentProgramScene extends EmptyGetResponse {
	
	public SetCurrentProgramScene(String sceneName)
	{
		super(json -> {
			json.addProperty("sceneName", sceneName);
		});
	}
	
	@Override
	public String getRequestType()
	{
		return "SetCurrentProgramScene";
	}
}
