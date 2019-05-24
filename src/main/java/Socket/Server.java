/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Socket;

/**
 *
 * @author Tien
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/game")
public class Server {

    private static Integer numPlayers = 0;

    @OnOpen
    public void handleOpen(Session session) {
        numPlayers++;
        if (numPlayers > 2) {
            if (session.isOpen()) {
                try {
                    CloseReason rs = new CloseReason((CloseReason.CloseCodes.VIOLATED_POLICY), "The room is full");
                    session.close(rs);
                } catch (IOException ex) {
                }
            }
        }
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = gson.fromJson(message, JsonElement.class).getAsJsonObject();
        if (json.get("action").getAsString().equals("")) {
            
        }
    }
    
    @OnClose
    public void handleClose() {
        numPlayers--;
    }
    
    @OnError
    public void handleError() {
    }
}
