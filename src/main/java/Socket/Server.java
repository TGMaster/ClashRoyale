/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Socket;

/**
 *
 * @author TGMaster
 */
import Util.Constant;
import Controller.PlayerManager;
import Entity.Player;
import Service.PlayerService;

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

@ServerEndpoint(value = "/room")
public class Server {

    private static Integer numPlayers = 0;
    private PlayerService playerService = new PlayerService();

    public static Integer getNumPlayers() {
        return numPlayers;
    }

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
        if (Constant.JOIN.equals(json.get("action").getAsString())) {
            joinGame(json, session);
            System.out.println("A client just connected");
        }
        if (Constant.LEAVE.equals(json.get("action").getAsString())) {
            leaveGame(json);
            System.out.println("A client just disconnected");
        }
        if (Constant.COMMAND.equals(json.get("action").getAsString())) {
            cmdGame(json);
            System.out.println("A client just sent a message");
        }
        if (Constant.GAMEOVER.equals(json.get("action").getAsString())) {
            stopGame();
        }
    }

    @OnClose
    public void handleClose() {
        numPlayers--;
    }

    @OnError
    public void handleError(Throwable T) {
    }

    private void joinGame(JsonObject json, Session session) {
        Player p = playerService.findPlayerById(json.get("id").getAsString());
        PlayerManager.joinGame(p, session);
    }

    private void leaveGame(JsonObject json) {
        String id = json.get("id").getAsString();
        PlayerManager.leaveGame(id);
    }

    private void cmdGame(JsonObject json) {
        String id = json.get("id").getAsString();
        String msg = json.get("message").getAsString();
        String team = json.get("team").getAsString();
        PlayerManager.receiveCmd(id, team, msg);
    }

    private void stopGame() {
        PlayerManager.stopGame();
    }
}
