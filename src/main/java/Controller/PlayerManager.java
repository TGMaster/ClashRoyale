/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Util.Constant;
import Entity.Player;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Session;
import s90805.Game;

//import s90805.TestThread;

/**
 *
 * @author Tien
 */
public class PlayerManager {

    public final static Set<Player> PLAYERS = new HashSet<Player>();
    public final static HashMap<String, Session> playerSession = new HashMap<String, Session>();
    
    private static Game game;

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
    private static JsonObject createMessage(String action, String id, String message) {
        JsonObject addMessage = new JsonObject();
        addMessage.addProperty("action", action);
        addMessage.addProperty("id", id);
        addMessage.addProperty("message", message);
        if (id.equals("-1")) {
            addMessage.addProperty("name", "Server");
        } else {
            Player player = getUserById(id);
            if (player != null) {
                addMessage.addProperty("name", player.getUsername());
            }
        }
        return addMessage;
    }

    public static void receiveCmd(String id, String message) {
        Player player = new Player(id, "Tester", "123", 5);
        JsonObject messageJson = createMessage(Constant.COMMAND, id, message);
        //sendToSession(pSession.get(sender), chatMessage);
        //broadcast messages to others...
        if (message.equals("run")) {
            System.out.println("Run Game");
            game = new Game(player);
            new Thread(game).start();
        }
        if (message.equals("stop")) {
            System.out.println("Stop Game");
            game.stop();
        }
        if (message.contains("spawn")) {
            System.out.println("Spawn troop");
            message = message.replace("spawn:", "");
            game.deployTroop(message);
        }
        for (Player p : PLAYERS) {
            sendToSession(playerSession.get(p.getId()), messageJson);
        }
    }

    public static void sendCmd(String action, String message) {
        JsonObject messageJson = createMessage(action, "-1", message);
        //sendToSession(pSession.get(sender), chatMessage);
        //broadcast messages to others...
        for (Player p : PLAYERS) {
            sendToSession(playerSession.get(p.getId()), messageJson);
        }
    }
}
