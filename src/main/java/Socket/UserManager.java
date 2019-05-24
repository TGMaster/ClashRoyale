/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Socket;

import Entity.Player;
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
}
