package com.thekeirs.games.engine;

/**
 * Created by wurzel on 12/30/16.
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
