package com.thekeirs.games.engine;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wurzel on 12/30/16.
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
