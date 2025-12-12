package io.github.freshsupasulley.lwjobs.impl;

import com.google.gson.JsonObject;
import io.github.freshsupasulley.lwjobs.requests.EmptyGetRequest;

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
