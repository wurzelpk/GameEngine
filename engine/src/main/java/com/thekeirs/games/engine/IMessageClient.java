package com.thekeirs.games.engine;

/**
 * <h1>[internal] Interface for clients who want to receive messages from the MessageBus</h1>
 */

public interface IMessageClient {
    void handleMessage(Message msg);
}
