package com.supasulley.obs;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.supasulley.obs.requests.OBSRequest;

public class OBSBuiltRequest<T extends OBSRequest> extends CompletableFuture<T> {
	
	protected T request;
	private Consumer<T> onSuccess;
	private Consumer<FailedRequestException> onFailed;
	
	protected OBSBuiltRequest(T request, Consumer<T> onSuccess, Consumer<FailedRequestException> onFailed)
	{
		this.request = request;
		this.onSuccess = onSuccess;
		this.onFailed = onFailed;
	}
	
	protected void fireSuccess(int code, JsonObject requestData)
	{
		request.accept(code, requestData);
		complete(request);
		
		// If there's also a success callback attached
		if(onSuccess != null)
		{
			safeConsume(request, onSuccess);
		}
	}
	
	protected void fireFailure(int code, String comment, String rawResponse)
	{
		FailedRequestException failedRequest = new FailedRequestException(code, comment, rawResponse);
		OBSController.LOG.debug("Request failed: " + failedRequest);
		
		// If there's a failure callback
		if(onFailed != null)
		{
			complete(null);
			safeConsume(failedRequest, onFailed);
		}
		// If there's no failure callback attached
		else
		{
			// Throw an error to instead
			OBSController.LOG.error(request + " failed. ", failedRequest);
			this.completeExceptionally(failedRequest);
		}
	}
	
	/**
	 * Catches any errors that may occur in callbacks to avoid deadlocks.
	 * 
	 * @param <U>      type of object
	 * @param object   object to be consumed
	 * @param consumer the consumer
	 */
	private <U> void safeConsume(U object, Consumer<U> consumer)
	{
		try (CallbackContext ___ = CallbackContext.getInstance()) {
			consumer.accept(object);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
}
