package com.thekeirs.games.engine;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>[internal] Originally the MessageBus was a central part of the game engine's design, but
 * it hasn't proven terribly useful and may be going away.</h1>
 */

public final class MessageBus {
    private List<IMessageClient> mClients;

    public MessageBus() {
        mClients = new ArrayList<>();
    }
    public void addClient(IMessageClient client) {
        mClients.add(client);
    }

    public void postMessage(Message msg) {
        if (!msg.type.equals("update_redraw")) {
            Log.d("msg", msg.type + " (" + msg.x + "," + msg.y + ")");
        }
        for (IMessageClient client: mClients) {
            client.handleMessage(msg);
        }
    }
}
