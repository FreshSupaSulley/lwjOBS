package io.github.freshsupasulley.lwjobs.events;

import com.google.gson.JsonObject;

import java.util.function.Consumer;

public class OBSEventCallback<T extends OBSEvent> {
	
	private T event;
	private Consumer<T> consumer;
	
	public OBSEventCallback(T event, Consumer<T> consumer)
	{
		this.event = event;
		this.consumer = consumer;
	}
	
	public Consumer<T> getConsumer()
	{
		return consumer;
	}
	
	public void accept(JsonObject json)
	{
		event.parseResponse(json);
		consumer.accept(event);
	}
}
