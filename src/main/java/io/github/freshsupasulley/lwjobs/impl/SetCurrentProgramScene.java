package io.github.freshsupasulley.lwjobs.impl;

import io.github.freshsupasulley.lwjobs.requests.EmptyGetResponse;

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
