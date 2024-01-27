package com.supasulley.obs.impl;

import com.google.gson.JsonObject;
import com.supasulley.obs.requests.OBSRequest;

public class GetInputSettingsRequest extends OBSRequest {
	
	private JsonObject inputSettings;
	private String inputKind;
	
	public GetInputSettingsRequest(String inputName)
	{
		super((json) -> json.addProperty("inputName", inputName));
	}
	
	public String getInputKind()
	{
		return inputKind;
	}
	
	public JsonObject getInputSettings()
	{
		return inputSettings;
	}
	
	@Override
	protected void parseResponse(JsonObject responseData)
	{
		this.inputKind = responseData.get("inputKind").getAsString();
		this.inputSettings = responseData.get("inputSettings").getAsJsonObject();
	}
	
	@Override
	public String getRequestType()
	{
		return "GetInputSettings";
	}
}
