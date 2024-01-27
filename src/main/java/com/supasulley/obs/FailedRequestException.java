package com.supasulley.obs;

/**
 * Holds details on why an OBS request failed. Returned from failure callbacks.
 */
public class FailedRequestException extends RuntimeException {
	
	private static final long serialVersionUID = -2189793262067618472L;
	
	private int code;
	private String comment, rawResponse;
	
	protected FailedRequestException(int code, String comment, String rawResponse)
	{
		this.code = code;
		this.comment = comment;
		this.rawResponse = rawResponse;
	}
	
	/**
	 * @return RequestStatus error code
	 */
	public int getCode()
	{
		return code;
	}
	
	/**
	 * @return possibly empty comment on why the request failed
	 */
	public String getComment()
	{
		return comment;
	}
	
	/**
	 * @return raw response from WebSocket
	 */
	public String getRawResponse()
	{
		return rawResponse;
	}
	
	@Override
	public String toString()
	{
		return  "(" + code + ") \"" + comment + "\"";
	}
}
