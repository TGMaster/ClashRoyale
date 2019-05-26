/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

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
    private final static HashMap<String, Session> playerSession = new HashMap<String, Session>();

    protected static void joinGame(Player player, Session session) {

    }

    protected static void leaveGame(String id) {

    }

    private static Player getUserById(String id) {
        for (Player p : PLAYERS) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
    
    protected void Login(String username, String password) throws Exception {
        if (username.equals("") || password.equals("")) {
            System.out.println("Please enter full username and password!!!");
        } else {
            // Check if it is owner
            JsonObject loginMsg = new JsonObject();
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
//                loginMsg.addProperty("action", Config.REMOVE_MATCH);
//                loginMsg.addProperty("id", id);
                loginMsg.addProperty("message", "Login successfully");
            } else {
                System.out.println("Login failed!!!");
            }
        }
    }
}
