package com.supasulley.obs;

/**
 * Ripped from JDA (<a href="https://github.com/discord-jda/JDA/blob/master/src/main/java/net/dv8tion/jda/internal/requests/CallbackContext.java">here</a>)
 * <p>Prevents callbacks from deadlocking the controller.</p>
 */
public class CallbackContext implements AutoCloseable {
	
	private static final ThreadLocal<Boolean> callback = ThreadLocal.withInitial(() -> false);
	private static final CallbackContext instance = new CallbackContext();
	
	public static CallbackContext getInstance()
	{
		callback.set(true);
		return instance;
	}
	
	public static boolean isCallbackContext()
	{
		return callback.get();
	}
	
	@Override
	public void close()
	{
		callback.set(false);
	}
}
