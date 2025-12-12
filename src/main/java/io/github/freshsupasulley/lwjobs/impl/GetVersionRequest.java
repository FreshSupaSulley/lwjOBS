package io.github.freshsupasulley.lwjobs.impl;

import com.google.gson.JsonObject;
import io.github.freshsupasulley.lwjobs.requests.EmptyGetRequest;

public class GetVersionRequest extends EmptyGetRequest {
	
	private String obsVersion, socketVersion;
	
	public String getOBSVersion()
	{
		return obsVersion;
	}
	
	public String getSocketVersion()
	{
		return socketVersion;
	}
	
	@Override
	public String getRequestType()
	{
		return "GetVersion";
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.obsVersion = responseData.get("obsVersion").getAsString();
		this.socketVersion = responseData.get("obsWebSocketVersion").getAsString();
	}
}
