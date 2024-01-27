package com.supasulley.obs.events;

import java.util.function.Consumer;

import com.google.gson.JsonObject;

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
