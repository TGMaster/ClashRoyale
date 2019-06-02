/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import Entity.Player;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 * @author TGMaster
 */
public class PlayerService {

    public Player findPlayerById(String id) {
        Player p = new Player();
        // Read database
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(new FileReader("C:\\Users\\TGMaster\\Documents\\Projects\\ClashRoyale\\database.json")).getAsJsonObject();
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JsonArray arr = jsonObject.getAsJsonArray("Player");
        for (int i = 0; i < arr.size(); i++) {
            JsonObject player = arr.get(i).getAsJsonObject();
            if (player.get("id").getAsString().equals(id)) {
                p.setId(player.get("id").getAsString());
                p.setUsername(player.get("username").getAsString());
                p.setPassword(player.get("password").getAsString());
                p.setMana(5);
                break;
            }
        }
        return p;
    }

    public Player findPlayerByUsername(String username) {
        Player p = new Player();
        // Read database
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(new FileReader("D:\\WORK\\GitHub\\ClashRoyale\\src\\main\\resources\\database.json")).getAsJsonObject();
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JsonArray arr = jsonObject.getAsJsonArray("Player");
        for (int i = 0; i < arr.size(); i++) {
            JsonObject player = arr.get(i).getAsJsonObject();
            if (player.get("username").getAsString().equals(username)) {
                p.setId(player.get("id").getAsString());
                p.setUsername(player.get("username").getAsString());
                p.setPassword(player.get("password").getAsString());
                p.setMana(5);
                break;
            }
        }
        return p;
    }
}
