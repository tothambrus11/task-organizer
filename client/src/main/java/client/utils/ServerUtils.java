/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import commons.messages.Message;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static commons.Constants.API_PATH;
import static commons.Constants.WEBSOCKET_PATH;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private String serverAddress = "A";

    private StompSession session;

    public <T> T get(String path, GenericType<T> responseType) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(getBaseURL()).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(responseType);
    }

    public <T> T post(String path, Object body, GenericType<T> responseType) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(getBaseURL()).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(body, APPLICATION_JSON), responseType);
    }

    public <T> T put(String path, Object body, GenericType<T> responseType) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(getBaseURL()).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(body, APPLICATION_JSON), responseType);
    }

    public <T> T delete(String path, GenericType<T> responseType) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(getBaseURL()).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(responseType);
    }

    public <T> CompletableFuture<T> getAsync(String path, GenericType<T> responseType) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        Client client = ClientBuilder.newClient(new ClientConfig());
        Invocation.Builder builder  = client.target(getBaseURL()).path(path)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON);

        builder.async().get(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    T result = response.readEntity(responseType);
                    completableFuture.complete(result);
                } else {
                    completableFuture.completeExceptionally(new Exception("Long polling request failed with status: " + response.getStatus()));
                }
            }

            @Override
            public void failed(Throwable throwable) {
                completableFuture.completeExceptionally(throwable);
            }
        });

        return completableFuture;
    }


    /**
     * Request a websocket connection at the specified url
     *
     * @param url - url of the websocket connection
     * @return websocket session
     */
    private StompSession connect(String url) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(566666);
        var client = new StandardWebSocketClient(container);
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException();
    }

    /**
     * Listens for websocket messages at the specified destination
     *
     * @param type     - the class of the message to be decoded
     * @param consumer - callback where the message will be passed onto
     * @param <T>      - class of object the consumer is expecting
     * @return
     */
    public <T extends Message> StompSession.Subscription listen(Class<T> type, Consumer<T> consumer) {
        String destination = "/user/topic/" + Message.getTopic(type);

        System.out.printf("[Websocket] Listening for messages of type %s at %s%n", type.getSimpleName(), destination);
        return session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void handleFrame(StompHeaders headers, Object payload) {
                Platform.runLater(() -> consumer.accept((T) payload));
            }
        });
    }


    /**
     * Sends a websocket message to the specified destination
     *
     * @param message - the message to be sent
     */
    public void send(Message message) {
        session.send("/app/" + Message.getTopic(message.getClass()), message);
    }

    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Returns the API Base URL
     *
     * @return API Base URL
     */
    public String getBaseURL() {
        return "http://" + getServerAddress() + API_PATH;
    }

    /**
     * Returns the WebSocket Base URL
     *
     * @return Websocket Base URL
     */
    public String getWebSocketURL() {
        return "ws://" + serverAddress + WEBSOCKET_PATH;
    }


    public boolean isServerReachable(String address) {
        try {
            ClientBuilder.newClient(new ClientConfig())
                .target("http://" + address + API_PATH)
                .path("/ping")
                .request()
                .accept(APPLICATION_JSON)
                .get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void connectToServer(String address) {
        if (isServerReachable(address)) {
            this.serverAddress = address;

            this.session = connect(getWebSocketURL());
        } else {
            throw new IllegalArgumentException("The server at address " + address + " is not reachable.");
        }
    }
}