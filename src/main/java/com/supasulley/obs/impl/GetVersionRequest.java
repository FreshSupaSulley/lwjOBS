package com.supasulley.obs.impl;

import com.google.gson.JsonObject;
import com.supasulley.obs.requests.EmptyGetRequest;

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
