package com.thekeirs.games.engine;

/**
 * <h1>[internal] Used to encapsulate a message to be send on the {@link MessageBus}.
 * Not currently used.</h1>
 */

public class Message {
    public String type;
    public int x, y;
    Object obj;

    public Message(String _type) {
        this.type = _type;
    }

    public Message(String _type, int x, int y) {
        this.type = _type;
        this.x = x;
        this.y = y;
    }

    public Message(String _type, Object obj) {
        this.type = _type;
        this.obj = obj;
    }
}
