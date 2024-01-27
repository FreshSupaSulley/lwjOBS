package com.supasulley.obs.requests;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.supasulley.obs.OBSController;

/**
 * Generic OBS request. Accepted by {@link OBSController#build(OBSRequest)}. You can extend this class to provide support for custom requests.
 */
public abstract class OBSRequest {
	
	private Consumer<JsonObject> consumer;
	private int code;
	
	/**
	 * Instantiates a new OBS request object. Provide a JsonObject as the request body to be sent to the WS.
	 * 
	 * @param consumer JSON object
	 */
	public OBSRequest(Consumer<JsonObject> consumer)
	{
		this.consumer = consumer;
	}
	
	/**
	 * Accepts response data from OBS and fills instance variables.
	 * 
	 * @param code         code
	 * @param comment      comment
	 * @param responseData JSON object
	 */
	public void accept(int code, JsonObject responseData)
	{
		this.code = code;
		parseResponse(responseData);
	}
	
	/**
	 * Parses response data and puts its data into this class.
	 * 
	 * @param responseData JSON object
	 */
	protected abstract void parseResponse(JsonObject responseData);
	
	/**
	 * @return name of the request, as accepted by the WebSocket
	 */
	public abstract String getRequestType();
	
	/**
	 * Fills a JSON object with the request information.
	 * @param json
	 */
	public void applyJSON(JsonObject json)
	{
		consumer.accept(json);
	}
	
	/**
	 * @return successful RequestStatus code (usually 100)
	 */
	public int getCode()
	{
		return code;
	}
}
