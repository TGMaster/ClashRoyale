/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Config.Config;
import Entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Session;

/**
 *
 * @author Tien
 */
public class UserManager {

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
    
    public static void Login(String username, String password) throws Exception {
        JsonObject loginMsg = new JsonObject(); // Login message
        loginMsg.addProperty("action", Config.LOGIN);
        if (username.equals("") || password.equals("")) {
            loginMsg.addProperty("message", "Missing username or password");
        } else {
            // Read database
            JsonObject jsonObject = new JsonParser().parse(new FileReader("src/main/resources/database.json")).getAsJsonObject();

            boolean isLogin = false;
            JsonArray arr = jsonObject.getAsJsonArray("Player");
            for (int i = 0; i < arr.size(); i++) {
                JsonObject player = arr.get(i).getAsJsonObject();
                if (username.equals(player.get("username").getAsString()) && password.equals(player.get("password").getAsString())) {
                    isLogin = true;
                    break;
                }
            }

            if (isLogin) {
                loginMsg.addProperty("username", username);
                loginMsg.addProperty("message", "Login successfully");
            } else {
                loginMsg.addProperty("message", "Login failed");
            }
        }
    }
}
