package com.supasulley.obs.events;

import com.google.gson.JsonObject;

/**
 * Events that do not provide any fields or additional information.
 */
public abstract class EmptyEvent extends OBSEvent {
	
	@Override
	protected void parseResponse(JsonObject responseData) {}
}
