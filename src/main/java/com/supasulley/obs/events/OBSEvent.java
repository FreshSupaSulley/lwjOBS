package com.supasulley.obs.events;

import com.google.gson.JsonObject;

/**
 * Generic OBS event.
 */
public abstract class OBSEvent {
	
	public abstract String getEventType();
	
	/**
	 * Parses response data and puts its data into this class.
	 * @param responseData JSON object
	 */
	protected abstract void parseResponse(JsonObject responseData);
}
