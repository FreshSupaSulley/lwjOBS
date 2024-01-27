package com.supasulley.obs;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.supasulley.obs.requests.OBSRequest;

/**
 * Used for building OBS requests.
 * <p>
 * Add a success callback and call {@link #queue()} to send the request, or call {@link #complete()} to wait for the request to finish.
 * </p>
 * 
 * @see OBSController
 * @param <T> this request type, which must be subclass of {@link OBSRequest}
 */
public class OBSRequestBuilder<T extends OBSRequest> {
	
	private final OBSController controller;
	private final T request;
	
	private Consumer<T> onSuccess;
	private Consumer<FailedRequestException> onFailed;
	
	protected OBSRequestBuilder(OBSController controller, T request)
	{
		this.controller = controller;
		this.request = request;
	}
	
	/**
	 * Schedules a consumer to be fired if the request is successful.
	 * 
	 * @param onSuccess consumer to fire if successful
	 */
	public OBSRequestBuilder<T> onSuccess(Consumer<T> onSuccess)
	{
		this.onSuccess = onSuccess;
		return this;
	}
	
	/**
	 * Schedules a consumer to be fired if the request fails.
	 * 
	 * @param onFailed consumer to fire if unsuccessful
	 */
	public OBSRequestBuilder<T> onFailure(Consumer<FailedRequestException> onFailed)
	{
		this.onFailed = onFailed;
		return this;
	}
	
	/**
	 * Adds success and failure callbacks, then sends the request.
	 * 
	 * @param onSuccess consumer to fire if successful
	 * @param onFailed  consumer to fire if unsuccessful
	 */
	public void queue(Consumer<T> onSuccess, Consumer<FailedRequestException> onFailed)
	{
		this.onSuccess = onSuccess;
		this.onFailed = onFailed;
		queue();
	}
	
	/**
	 * Adds success and failure callbacks, then sends the request and waits for it to finish.
	 * 
	 * @param onSuccess consumer to fire if successful
	 * @param onFailed  consumer to fire if unsuccessful
	 */
	public void complete(Consumer<T> onSuccess, Consumer<FailedRequestException> onFailed)
	{
		this.onSuccess = onSuccess;
		this.onFailed = onFailed;
		complete();
	}
	
	/**
	 * Delays firing the request by a certain amount of time.
	 * 
	 * @param delay the time from now to delay execution
	 * @param unit  the time unit of the delay parameter
	 */
	public void queueAfter(int delay, TimeUnit unit)
	{
		OBSController.executor.schedule(() -> queue(), delay, unit);
	}
	
	/**
	 * Sends the request to the WebSocket. Success and failure callbacks should be defined to receive notice when finished.
	 */
	public void queue()
	{
		controller.sendRequest(new OBSBuiltRequest<T>(request, onSuccess, onFailed));
	}
	
	/**
	 * Waits for the request to finish and returns it.
	 * <p>
	 * You cannot call this method in success callbacks, as it can cause a deadlock.
	 * </p>
	 * 
	 * @return the completed {@link OBSRequest} if successful, or null if failed with a failure callback attached
	 * @throws FailedRequestException if failed without a failure callback
	 */
	public T complete() throws FailedRequestException
	{
		if(CallbackContext.isCallbackContext())
		{
			throw new IllegalStateException("Cannot call complete() in callbacks, as it can cause a deadlock. Use non-blocking functions like queue()");
		}
		
		try {
			return submit().orTimeout(5, TimeUnit.SECONDS).join();
		} catch(CompletionException e) {
			// CompletionExceptions hold the exception that caused it in the cause
			Throwable cause = e.getCause();
			if(cause instanceof FailedRequestException) throw (FailedRequestException) cause;
			// Give some more info
			if(cause instanceof TimeoutException) OBSController.LOG.error("Request was timed out");
			throw e;
		}
	}
	
	/**
	 * Queues the request and returns a {@link CompletableFuture} object for asynchronous logic.
	 * 
	 * @return CompletableFuture object
	 */
	public CompletableFuture<T> submit()
	{
		return controller.sendRequest(new OBSBuiltRequest<T>(request, onSuccess, onFailed));
	}
}
