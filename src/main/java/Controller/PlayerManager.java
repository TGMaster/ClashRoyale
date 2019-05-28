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

/**
 *
 * @author Tien
 */
public class PlayerManager {

    private final static Set<Player> PLAYERS = new HashSet<Player>();
    private final static HashMap<String, Set<Player>> onlinePlayers = new HashMap<String, Set<Player>>();
    private final static HashMap<String, Session> playerSession = new HashMap<String, Session>();

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
    private static JsonObject gameCmd(String sender, String message) {
        Player player = getUserById(sender);
        if (player != null) {
            JsonObject addMessage = new JsonObject();
            addMessage.addProperty("action", Constant.COMMAND);
            addMessage.addProperty("id", sender);
            addMessage.addProperty("name", player.getUsername());
            addMessage.addProperty("message", message);
            return addMessage;
        } else {
            return null;
        }
    }
}
