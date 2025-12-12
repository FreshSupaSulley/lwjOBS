package io.github.freshsupasulley.lwjobs.impl;

import com.google.gson.JsonObject;
import io.github.freshsupasulley.lwjobs.requests.EmptyGetRequest;

public class GetStreamStatusRequest extends EmptyGetRequest {
	
	private boolean streaming;
	
	public boolean isStreaming()
	{
		return streaming;
	}
	
	@Override
	public String getRequestType()
	{
		return "GetStreamStatus";
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.streaming = responseData.get("outputActive").getAsBoolean();
	}
}
