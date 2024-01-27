package com.supasulley.obs.requests;

/**
 * The best of both worlds. No information provided, no information received. Just a command.
 */
public class EmptyRequest extends EmptyGetResponse {
	
	private String requestType;
	
	public EmptyRequest(String requestType)
	{
		super((json) -> {});
		this.requestType = requestType;
	}
	
	@Override
	public String getRequestType()
	{
		return requestType;
	}
}
