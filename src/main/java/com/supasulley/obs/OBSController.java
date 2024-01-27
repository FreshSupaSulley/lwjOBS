package com.supasulley.obs;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.supasulley.obs.events.OBSEvent;
import com.supasulley.obs.events.OBSEventCallback;
import com.supasulley.obs.requests.EmptyGetResponse;
import com.supasulley.obs.requests.EmptyRequest;
import com.supasulley.obs.requests.OBSRequest;

/**
 * Handles all OBS WebSocket traffic according to the <a href="https://github.com/obsproject/obs-websocket/blob/master/docs/generated/protocol.md">5.X.X websocket protocol</a>.
 * 
 * @see OBSController#OBSController()
 */
public class OBSController implements WebSocketListener {
	
	public static final Logger LOG;
	
	static
	{
		boolean loggerImpl = false;
		
		// Ripped straight from JDA:
		// https://github.com/discord-jda/JDA/blob/master/src/main/java/net/dv8tion/jda/internal/utils/JDALogger.java
		try {
			Class.forName("org.slf4j.impl.StaticLoggerBinder");
			loggerImpl = true;
		} catch(ClassNotFoundException e) {
			// there was no static logger binder (SLF4J pre-1.8.x)
			try {
				Class<?> serviceProviderInterface = Class.forName("org.slf4j.spi.SLF4JServiceProvider");
				// check if there is a service implementation for the service, indicating a provider for SLF4J 1.8.x+ is installed
				loggerImpl = ServiceLoader.load(serviceProviderInterface).iterator().hasNext();
			} catch(ClassNotFoundException eService) {
				// there was no service provider interface (SLF4J 1.8.x+)
				// prints warning of missing implementation
				LoggerFactory.getLogger(OBSController.class);
				loggerImpl = false;
			}
		}
		
		// Print to console
		if(!loggerImpl)
		{
			LOG = new OBSSimpleLogger();
		}
		// Print to logger
		else
		{
			LOG = LoggerFactory.getLogger(OBSController.class);
		}
	}
	
	protected static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	
	private final WebSocketClient client;
	private Session session;
	
	private int rpcVersion;
	private String password;
	
	// Request callbacks
	private Map<String, OBSBuiltRequest<? extends OBSRequest>> callbacks;
	private Map<String, OBSEventCallback<? extends OBSEvent>> events;
	
	// Event callbacks
	private CompletableFuture<Void> onConnect;
	private Consumer<String> onDisconnected = LOG::error;
	
	/**
	 * Initializes a new controller. Use chained functions to connect to the WebSocket.
	 * <pre>
	 * OBSController controller = new OBSController()
	 * 	.registerDisconnect(System.err::println)
	 * 	.connect("ws://localhost:4444", "your_password");
	 * </pre>
	 * 
	 * It's recommended you handle errors thrown by {@link #connect(String, String) connect()} in a try catch block.
	 */
	public OBSController()
	{
		callbacks = new HashMap<String, OBSBuiltRequest<? extends OBSRequest>>();
		events = new HashMap<String, OBSEventCallback<? extends OBSEvent>>();
		client = new WebSocketClient();
		client.setStopTimeout(1000);
	}
	
	/**
	 * Fires a callback if the WebSocket connection is lost.
	 * 
	 * @param onDisconnect disconnect callback
	 * @return this, for chaining
	 */
	public OBSController registerDisconnect(Consumer<String> onDisconnect)
	{
		LOG.info("Disconnected");
		this.onDisconnected = onDisconnect;
		return this;
	}
	
	/**
	 * Registers a callback to be fired when an OBS event is received.
	 * 
	 * @param <T>      subclass of {@link OBSEvent}
	 * @param event    the event
	 * @param consumer consumer to be fired when event is received
	 * @return this, for chaining
	 */
	public <T extends OBSEvent> OBSController registerEvent(T event, Consumer<T> consumer)
	{
		events.put(event.getEventType(), new OBSEventCallback<T>(event, consumer));
		return this;
	}
	
	/**
	 * Attempts to connect to the OBS WebSocket. Holds the thread until complete.
	 * <p>
	 * If successful, you can start sending requests to OBS. Throws an error if unsuccessful.
	 * </p>
	 * 
	 * @param address URI address (ex. ws://localhost:4444)
	 * @param password password for authentication, if required
	 * @return this, for chaining
	 * @throws WebSocketException if an error occurred connecting
	 */
	public OBSController connect(String address, String password) throws WebSocketException
	{
		LOG.info("Attempting to connect to " + address);
		this.password = password;
		onConnect = new CompletableFuture<>();
		
		WebSocketListener listener = this;
		
		try {
			try {
				client.start();
				client.connect(listener, new URI(address)).get();
			} catch(Exception e) {
				LOG.trace("Completing onConnect exceptionally", e);
				onConnect.completeExceptionally(e);
			}
			
			// Wait a certain amount of time before giving up
			onConnect.get(5, TimeUnit.SECONDS);
		} catch(Exception e) {
			LOG.error("Failed to connect to " + address);
			LOG.debug("Full connection error stacktrace", e);
			// Reconstructing the error as one class type for convenience
			// I don't like the potentially endless strings of Caused by...
			Throwable cause = getTrueCause(e);
			WebSocketException exception = new WebSocketException(cause.getMessage());
		    exception.setStackTrace(cause.getStackTrace());
			throw exception;
		}
		
		return this;
	}
	
	private Throwable getTrueCause(Throwable t)
	{
		Throwable cause = t.getCause();
		if(cause == null) return t;
		return getTrueCause(cause);
	}
	
	/**
	 * Attempts to connect to the OBS WebSocket without a password. Holds the thread until complete.
	 * <p>
	 * If successful, you can start sending requests to OBS. Throws an error if unsuccessful.
	 * </p>
	 * 
	 * @param address URI address (ex. ws://localhost:4444)
	 * @return this, for chaining
	 * @throws WebSocketException if an error occurred connecting
	 */
	public OBSController connect(String address) throws WebSocketException
	{
		LOG.debug("No password provided");
		return connect(address, null);
	}
	
	/**
	 * Disconnects from the WebSocket.
	 */
	public void disconnect()
	{
		LOG.debug("Disconnect command received");
		
		try {
			client.stop();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Builds an OBS request. <b>This does <u>NOT</u> fire the request.</b>
	 * 
	 * <p><b>Example:</b></p>
	 * 
	 * <pre>
	 * controller.build(new GetVersionRequest()).queue((response) -> {
	 * 	System.out.println(response.getOBSVersion());
	 * }, (failure) -> {
	 * 	System.err.println("Failed to get OBS version");
	 * });
	 * </pre>
	 * 
	 * Adding a success callback allows you to access information returned from the WebSocket.
	 * However, some requests don't receive information at all (such as {@link EmptyGetResponse} objects), meaning there's no purpose for a success callback other than confirming the request was successful.
	 * 
	 * <p><b>Example without callbacks:</b></p>
	 * 
	 * <pre>
	 * controller.build(new SetCurrentProgramScene("newscene")).queue();
	 * </pre>
	 * 
	 * <p>Furthermore, some OBS requests send nothing and receive nothing in return. This library handles these as {@link EmptyRequest EmptyRequests}. These requests include "StartStream", "ToggleStream", "SaveReplayBuffer", etc.
	 * Instead of creating classes for each empty request, you can instantiate EmptyRequest and supply it directly as an argument to the build function.</p>
	 * 
	 * <p><b>Empty Request Example:</b></p>
	 * <pre>
	 * controller.build(new EmptyRequest("StartRecord")).queue();
	 * </pre>
	 * 
	 * <p><b>IMPORTANT:</b> Always finish building with {@link OBSRequestBuilder#queue() queue()} or {@link OBSRequestBuilder#complete() complete()}, or the request won't be sent!</p>
	 * 
	 * @param <T>     {@link OBSRequest} subclass
	 * @param request a newly created OBSRequest subclass
	 * @return {@link OBSRequestBuilder} object
	 */
	public <T extends OBSRequest> OBSRequestBuilder<T> build(T request)
	{
		LOG.trace("Building new request: " + request);
		
		if(!onConnect.isDone())
		{
			throw new IllegalStateException("Connect to OBS before building requests");
		}
		
		return new OBSRequestBuilder<T>(this, request);
	}
	
	protected <T extends OBSRequest> OBSBuiltRequest<T> sendRequest(OBSBuiltRequest<T> type)
	{
		T request = type.request;
		
		// Generate unique requestID
		// 1 in a billion chance using UUIDs will ever produce duplication problems but this makes me feel better
		String requestID = UUID.randomUUID().toString();
		while(callbacks.containsKey(requestID))
		{
			requestID = UUID.randomUUID().toString();
		}
		
		// Always use opCode 6 for requests
		JsonObject json = new JsonObject();
		json.addProperty("op", 6);
		
		// Data
		JsonObject dataObject = new JsonObject();
		json.add("d", dataObject);
		dataObject.addProperty("requestType", request.getRequestType());
		dataObject.addProperty("requestId", requestID);
		
		JsonObject requestData = new JsonObject();
		dataObject.add("requestData", requestData);
		request.applyJSON(requestData);
		
		callbacks.put(requestID, type);
		session.getRemote().sendStringByFuture(json.toString());
		LOG.trace("OUTBOUND: " + json.toString());
		return type;
	}
	
	// EmptyRequest commands, included for convenience and as examples
	// Lowkey probably shouldn't include this to set an example that you need to make your own requests but eh
	public OBSRequestBuilder<EmptyRequest> startRecording() { return build(new EmptyRequest("StartRecord")); }
	public OBSRequestBuilder<EmptyRequest> stopRecording() { return build(new EmptyRequest("StopRecord")); }
	public OBSRequestBuilder<EmptyRequest> startStreaming() { return build(new EmptyRequest("StartStream")); }
	public OBSRequestBuilder<EmptyRequest> stopStreaming() { return build(new EmptyRequest("StopStream")); }
	
	@Override
	public void onWebSocketConnect(Session session)
	{
		this.session = session;
		LOG.info("Connected to OBS WS");
	}
	
	@Override
	public void onWebSocketText(String message)
	{
		LOG.trace("INBOUND: " + message);
		
		JsonObject values = JsonParser.parseString(message).getAsJsonObject();
		JsonObject data = values.get("d").getAsJsonObject();
		int opCode = values.get("op").getAsInt();
		
		switch(opCode)
		{
			// Hello
			case 0:
			{
				rpcVersion = data.get("rpcVersion").getAsInt();
				LOG.debug("Version " + data.get("obsWebSocketVersion").getAsString() + " - RPC: " + rpcVersion);
				
				JsonObject json = new JsonObject();
				json.addProperty("op", 1);
				
				JsonObject d = new JsonObject();
				d.addProperty("rpcVersion", 1);
				json.add("d", d);
				
				// If authentication is required
				if(data.has("authentication"))
				{
					// Check if password was supplied
					if(password == null)
					{
						onConnect.completeExceptionally(new IOException("OBS expected a password. Provide one in OBSController.connect()"));
						return;
					}
					
					JsonObject map = data.get("authentication").getAsJsonObject();
					String challenge = map.get("challenge").getAsString();
					String salt = map.get("salt").getAsString();
					
					LOG.debug("Authentication is required");
					String secret = Base64.encodeBase64String(DigestUtils.sha256(password + salt));
					
					d.addProperty("authentication", Base64.encodeBase64String(DigestUtils.sha256(secret + challenge)));
				}
				else
				{
					// authentication property is not required
					LOG.debug("No authentication required");
				}
				
				session.getRemote().sendStringByFuture(json.toString());
				break;
			}
			// Identified
			case 2:
			{
				rpcVersion = data.get("negotiatedRpcVersion").getAsInt();
				onConnect.complete(null);
				break;
			}
			// Events
			case 5:
			{
				JsonObject eventData = data.get("eventData").getAsJsonObject();
				OBSEventCallback<?> callback = events.get(data.get("eventType").toString());
				
				if(callback != null)
				{
					callback.accept(eventData);
				}
				
				break;
			}
			// Responses
			case 7:
			{
				JsonObject requestStatus = data.get("requestStatus").getAsJsonObject();
				int code = requestStatus.get("code").getAsInt();
				String comment = requestStatus.has("comment") ? requestStatus.get("comment").getAsString() : "";
				
				// Get callback that was waiting for a response
				OBSBuiltRequest<?> callback = callbacks.remove(data.get("requestId").getAsString());
				boolean success = requestStatus.get("result").getAsBoolean();
				
				// If unsuccessful
				if(!success)
				{
					// Failure
					callback.fireFailure(code, comment, message);
				}
				// Successful
				else
				{
					callback.fireSuccess(code, data.has("responseData") ? data.get("responseData").getAsJsonObject() : null);
				}
				
				break;
			}
			default:
			{
				LOG.debug("Unhandled OpCode " + opCode + " - " + message);
				break;
			}
		}
	}
	
	@Override
	public void onWebSocketClose(int statusCode, String reason)
	{
		LOG.info("WebSocket closed: (" + statusCode + ") " + reason);
		
		if(!onConnect.isDone())
		{
			String error = "Failed to connect";
			
			switch(statusCode)
			{
				case 4009:
				{
					error = "Password incorrect. Check WebSockets Server Settings";
				}
			}
			
			onConnect.completeExceptionally(new IOException(error + ": (" + statusCode + ") - " + reason));
		}
		else
		{
			onDisconnected.accept("Disconnected from OBS WebSocket: (" + statusCode + ") - " + reason);
		}
	}
	
	@Override
	public void onWebSocketError(Throwable cause)
	{
		LOG.trace("WebSocket error received", cause);
		
		if(!onConnect.isDone())
		{
			onConnect.completeExceptionally(cause);
		}
		else
		{
			LOG.error("WebSocket error", cause);
		}
	}
	
	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {}
}
