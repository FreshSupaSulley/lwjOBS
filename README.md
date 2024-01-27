# lwjOBS
Lightweight Java OBS WebSocket controller.

## Quickstart
Make sure to download the latest release and add it to your build path.

```java
// Connect to the WebSocket
OBSController controller = new OBSController().connect("ws://localhost:4444");
// Make your first request
controller.build(new GetVersionRequest()).queue(success -> {
  System.out.println(success.getOBSVersion());
}, failure -> {
  System.err.println("Failed to get version");
});
```

## Overview
This library lets you communicate with the OBS WebSocket (5.X.X). It handles communication, but relies on you to add support for requests and events by subclassing `OBSRequest` and `OBSEvent`.

Luckily, the library makes it easy. Adding support for OBS requests and events is largely the same process:

### Adding requests
1. Find the request you'd like to support in the [docs](https://github.com/obsproject/obs-websocket/blob/master/docs/generated/protocol.md#requests).
2. Subclass `OBSRequest` and create a constructor that generates the JSON request body using the consumer.
```java
// example request included in library
/**
 * Searches a scene for a source, and returns its id.
 * 
 * @param sceneName name of the scene or group to search in
 * @param sourceName name of the source to find
 * @param searchOffset number of matches to skip during search. >= 0 means first forward. -1 means last (top) item
 */
public GetSceneItemIdRequest(String sceneName, String sourceName, int searchOffset)
{
  super(json -> {
    json.addProperty("sceneName", sceneName);
    json.addProperty("sourceName", sourceName);
    json.addProperty("searchOffset", searchOffset);
  });
}
```
3. Implement abstract methods and provide getters to access the data.
```java
public int getSceneItemId()
{
  return sceneItemId;
}

@Override
public String getRequestType()
{
  return "GetSceneItemId";
}

@Override
protected void parseResponse(JsonObject responseData)
{
  this.sceneItemId = responseData.get("sceneItemId").getAsInt();
}
```

Then you can provide instances of your request to the controller:
```java
controller.build(new GetSceneItemIdRequest("gameplay", "camera", 0)).queue(success -> {
  System.out.println(success.getSceneItemId());
}, failure -> {
  System.err.println("Failed to get scene item ID");
});
```

### Adding events
Adding events is half the work of adding requests because they don't send any information to the WebSocket.

1. Find the event you'd like to support in the [docs](https://github.com/obsproject/obs-websocket/blob/master/docs/generated/protocol.md#events).
2. Subclass `OBSEvent` and implement the abstract methods.

Then you can register the event with the controller:
```java
controller.registerEvent(new RecordStateChangedEvent(), (onEvent) -> {
  System.out.println(onEvent.getOutputPath());
});
```

## Waiting with async/sync logic
You can choose to use synchronous or asynchronous methods to fire requests.

### Asynchronous
Use `queue` to dispatch the event and handle the response using callbacks. This is the recommended approach.

```java
controller.build(new GetVersionRequest()).queue(success -> {
  System.out.println(success.getOBSVersion());
}, failure -> {
  System.err.println("Failed to get version");
});
```

### Synchronous
Use `complete` to wait for a response from OBS.

```java
// blocks thread until complete
GetSceneItemIdRequest request = controller.build(new GetSceneItemIdRequest("gameplay", "camera", 0)).complete();
System.out.println(request.getRequestType());
```

If you choose to use `complete`, keep in mind that any supplied callbacks will still be fired. If the request fails, the failure callback is fired. Without one, null is returned.

> [!CAUTION]
> Nesting requests with `complete` in success callbacks are not allowed as it causes a deadlock. Nest requests with `queue` (or use `submit` instead for asynchronous logic).

## SLF4J
SLF4J is supported in the library. Although not required, you can add an SLF4J implementation to handle logging better (such as Logback). Otherwise, all INFO and ERROR level logs are printed to the console.

## Limitations
This library provides the framework for implementing events and requests only. More advanced OpCodes are not supported ([full list](https://github.com/obsproject/obs-websocket/blob/master/docs/generated/protocol.md#websocketopcode)).

## Contributing
Feel free to help contribute to the library, such as adding support for more OpCodes, making fundamental changes to the controller, or adding additional event/request implementations to be bundled with the library.
