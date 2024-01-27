package com.supasulley.obs.impl;

import com.google.gson.JsonObject;
import com.supasulley.obs.requests.EmptyGetRequest;

public class GetRecordStatusRequest extends EmptyGetRequest {
	
	private boolean recording;
	
	public boolean isRecording()
	{
		return recording;
	}
	
	@Override
	public String getRequestType()
	{
		return "GetRecordStatus";
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.recording = responseData.get("outputActive").getAsBoolean();
	}
}
