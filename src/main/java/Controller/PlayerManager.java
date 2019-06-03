/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Util.Constant;
import Entity.Player;
import Socket.Server;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.websocket.Session;

/**
 *
 * @author Tien
 */
public class PlayerManager {

    public final static List<Player> PLAYERS = new ArrayList<Player>();
    public final static HashMap<String, Session> playerSession = new HashMap<String, Session>();
    private final static HashMap<String, Boolean> team1 = new HashMap<>();

    private static GameControl game;

    public static void joinGame(Player player, Session session) {
        // Check owner
        int count = Server.getNumPlayers();
        if (count == 1) {
            team1.put(player.getId(), true);
        } else {
            team1.put(player.getId(), false);
        }
        PLAYERS.add(player);
        playerSession.put(player.getId(), session);
        announceAll(Constant.JOIN, player.getId(), "has joined");
    }

    public static void leaveGame(String id) {
        Player player = getUserById(id);
        team1.remove(id);
        PLAYERS.remove(player);
        playerSession.remove(id);
        announceAll(Constant.LEAVE, id, "has left");
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
        addMessage.addProperty("team", team1.get(id));
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

    public static void receiveCmd(String id, String team, String message) {
        JsonObject messageJson = createMessage(Constant.COMMAND, id, message);
        if (PLAYERS.size() >= 2) {
            Player player1 = null, player2 = null;
            if (team1.get(id)) {
                player1 = getUserById(id);
                for (Player p : PLAYERS) {
                    if (!p.getId().equals(id)) {
                        player2 = p;
                        break;
                    }
                }

            } else {
                player2 = getUserById(id);
                for (Player p : PLAYERS) {
                    if (!p.getId().equals(id)) {
                        player1 = p;
                        break;
                    }
                }
            }
            //sendToSession(pSession.get(sender), chatMessage);
            //broadcast messages to others...
            if (message.equals("start")) {
                System.out.println("Run Game");
                game = new GameControl(player1, player2);
                game.start();
            }
            if (message.equals("stop")) {
                System.out.println("Stop Game");
                game.stop();
            }
            if (message.contains("spawn")) {
                System.out.println("Spawn troop");
                message = message.replace("spawn:", "");
                game.deployTroop(id, team, message);
            }
        }
        for (Player p : PLAYERS) {
            sendToSession(playerSession.get(p.getId()), messageJson);
        }
    }

    public static void printToChatAll(String action, String message) {
        JsonObject messageJson = createMessage(action, "-1", message);
        //sendToSession(pSession.get(sender), chatMessage);
        //broadcast messages to others...
        for (Player p : PLAYERS) {
            sendToSession(playerSession.get(p.getId()), messageJson);
        }
    }

    public static void printToChat(String action, String id, String message) {
        JsonObject messageJson = createMessage(action, id, message);
        sendToSession(playerSession.get(id), messageJson);
    }

    public static void announceAll(String action, String id, String message) {
        JsonObject messageJson = createMessage(action, id, message);
        for (Player p : PLAYERS) {
            sendToSession(playerSession.get(p.getId()), messageJson);
        }
    }
}
