package com.supasulley.obs.requests;

/**
 * Empty get requests do not have any parameters sent to the WebSocket, but receives information in return.
 */
public abstract class EmptyGetRequest extends OBSRequest {
	
	public EmptyGetRequest()
	{
		super((json) -> {});
	}
}
