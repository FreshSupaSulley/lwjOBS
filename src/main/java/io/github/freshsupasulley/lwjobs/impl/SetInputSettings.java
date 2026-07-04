package io.github.freshsupasulley.lwjobs.impl;

import com.google.gson.JsonObject;
import io.github.freshsupasulley.lwjobs.requests.EmptyGetResponse;

import java.util.Map;

/**
 * incomplete cause I don't need overlay
 */
public class SetInputSettings extends EmptyGetResponse {
	
	public SetInputSettings(String inputName, Map<String, String> inputSettings)
	{
		super(json ->
		{
			JsonObject inputSettingsJSON = new JsonObject();
			json.add("inputSettings", inputSettingsJSON);
			
			for(var pair : inputSettings.entrySet())
			{
				inputSettingsJSON.addProperty(pair.getKey(), pair.getValue());
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
