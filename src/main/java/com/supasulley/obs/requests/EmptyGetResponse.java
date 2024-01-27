package com.supasulley.obs.requests;

import java.util.function.Consumer;

import com.google.gson.JsonObject;

/**
 * Empty responses provide information, but expect no information. Think of them as commands.
 */
public abstract class EmptyGetResponse extends OBSRequest {
	
	public EmptyGetResponse(Consumer<JsonObject> json)
	{
		super(json);
	}
	
	@Override
	protected void parseResponse(JsonObject responseData) {}
}
