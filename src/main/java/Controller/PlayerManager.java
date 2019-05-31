/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Util.Constant;
import Entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Session;

import s90805.Game;

/**
 *
 * @author Tien
 */
public class PlayerManager {

    public final static Set<Player> PLAYERS = new HashSet<Player>();
    public final static HashMap<String, Session> playerSession = new HashMap<String, Session>();

    public static void joinGame(Player player, Session session) {
        PLAYERS.add(player);
        playerSession.put(player.getId(), session);
    }

    public static void leaveGame(String id) {
        Player player = getUserById(id);
        PLAYERS.remove(player);
        playerSession.remove(id);
    }

    private static Player getUserById(String id) {
        for (Player p : PLAYERS) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    // Send back to front end
    private static void sendToSession(Session session, JsonObject message) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(message.toString());
            }
        } catch (IOException ex) {
            //Logger.getLogger(UserSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
            //System.out.println("Session already destroyed..");
        }
    }

    // Create Message
    private static JsonObject createMessage(String id, String message) {
        if (id.equals("-1")) {
            JsonObject addMessage = new JsonObject();
            addMessage.addProperty("action", Constant.COMMAND);
            addMessage.addProperty("id", id);
            addMessage.addProperty("name", "Server");
            addMessage.addProperty("message", message);
            return addMessage;
        } else {
            Player player = getUserById(id);
            if (player != null) {
                JsonObject addMessage = new JsonObject();
                addMessage.addProperty("action", Constant.COMMAND);
                addMessage.addProperty("id", id);
                addMessage.addProperty("name", player.getUsername());
                addMessage.addProperty("message", message);
                return addMessage;
            }
        }
        return null;
    }

    public static void receiveCmd(String id, String message) {
        Player player = new Player(id, "Tester", "123", 5);
//        Runnable r = new Game(player, message);
//        new Thread(r).start();

    }

    public static void sendCmd(String message) {
        JsonObject messageJson = createMessage("0", message);
        //sendToSession(pSession.get(sender), chatMessage);
        //broadcast messages to others...
        for (Player p : PLAYERS) {
            sendToSession(playerSession.get(p.getId()), messageJson);
        }
    }
}
