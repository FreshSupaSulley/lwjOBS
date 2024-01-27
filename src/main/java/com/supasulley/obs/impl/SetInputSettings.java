package com.supasulley.obs.impl;

import org.apache.http.NameValuePair;

import com.google.gson.JsonObject;
import com.supasulley.obs.requests.EmptyGetResponse;

/**
 * incomplete cause I don't need overlay
 */
public class SetInputSettings extends EmptyGetResponse {
	
	public SetInputSettings(String inputName, NameValuePair... inputSettings)
	{
		super(json -> {
			JsonObject inputSettingsJSON = new JsonObject();
			json.add("inputSettings", inputSettingsJSON);
			
			for(NameValuePair pair : inputSettings)
			{
				inputSettingsJSON.addProperty(pair.getName(), pair.getValue());
			}
			
			json.addProperty("inputName", inputName);
		});
	}
	
	@Override
	public String getRequestType()
	{
		return "SetInputSettings";
	}
}
