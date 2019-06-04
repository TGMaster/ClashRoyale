/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.Player;
import Entity.Tower;
import Entity.Troop;

import Util.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author S410U
 */
public class GameControl {

    private final Player player1;
    private final Player player2;

    private Game game1;
    private Game game2;
    private Thread thread1;
    private Thread thread2;
    private final Thread coordinator = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
            System.out.println("coordinator stopping!");
            game1.stop();
            game2.stop();
        }
    });

    public GameControl(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void start() {
        game1 = new Game(player1, player2);
        game2 = new Game(player2, player1);
        thread1 = new Thread(game1);
        thread2 = new Thread(game2);
        thread1.start();
        thread2.start();
    }

    public void stop() {
        coordinator.start();
    }

    // Received Cmd From Websocket
    public void deployTroop(String team, String message) {
        if (team.equals("team2")) {
            game2.deployTroop(message);
        } else {
            game1.deployTroop(message);
        }

    }
}

class Game implements Runnable {

    private volatile boolean running;
    private final Random rand = new Random();

    // Websocket variable
    private volatile int lane;
    private volatile int choice;

    // contain all information troops
    private List<Tower> listOfTower = new ArrayList<>(); // contain all information about towers

    private final List<Troop> troopsDeployedLeft = new ArrayList<>();
    private final List<Troop> troopsDeployedRight = new ArrayList<>();

    // tower in turn
    private final List<Troop> troopsForChoice = new ArrayList<>(); // contain 3 different troops at anytime for player choose to

    // spawn
    private Tower guard1, guard2, king;
    private final Player player, enemy;

    public Game(Player player, Player enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public void stop() {
        running = false;
    }

    public void resetChoice(Player player) {
        choice = -1;
        lane = 0;
    }

    @Override
    public void run() {
        init(player, enemy);
        running = true;
        long wait;

        // Loop
        while (running) {
            update(player);
            wait = 2000;
            resetChoice(player);

            try {
                Thread.sleep(wait);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init(Player player, Player enemy) {
        resetChoice(player);

        // Tower
        listOfTower = listOfTowerFromJson(enemy);
        king = listOfTower.get(0);
        guard1 = listOfTower.get(1);
        guard2 = listOfTower.get(2);
    }

    private void update(Player player) {
        if (king.isAlive()) {

            // Regen Mana
            player.regenMana();
            PlayerManager.printToChat(Constant.MANA, player.getId(), "" + player.getMana());

            // Add troops
            if (troopsForChoice.size() < 3) {
                addTroopsChoice(player);
            }
            printTroopList(troopsForChoice, player);

            if (choice >= 0 && choice <= 2) {
                Troop t = troopsForChoice.get(choice);
                if (player.spawnTroop(t)) { // if enough mana to spawn troop
                    troopsForChoice.remove(t);

                    // Choose left lane
                    if (lane == 1) {
                        // Add to list Left
                        troopsDeployedLeft.add(t);
                    } else if (lane == 2) {
                        // Add to list Right
                        troopsDeployedRight.add(t);
                    }
                }
            }

            if (!troopsDeployedLeft.isEmpty()) {
                Troop troopLeft = troopsDeployedLeft.get(0); //Get first troop
                // Guard 1 is alive
                if (guard1.isAlive()) {
                    guard1.attackTroop(troopLeft);
                    checkAlive(troopsDeployedLeft);
                    allTroopsAttack(troopsDeployedLeft, guard1);
                } // Guard 1 is dead
                else {
                    king.attackTroop(troopLeft);
                    checkAlive(troopsDeployedLeft);
                    allTroopsAttack(troopsDeployedLeft, king);
                }
            }
            if (!troopsDeployedRight.isEmpty()) {
                Troop troopRight = troopsDeployedRight.get(0); //Get first troop
                // Guard 2 is alive
                if (guard2.isAlive()) {
                    guard2.attackTroop(troopRight);
                    checkAlive(troopsDeployedRight);
                    allTroopsAttack(troopsDeployedRight, guard2);
                } // Guard 2 is dead
                else {
                    king.attackTroop(troopRight);
                    checkAlive(troopsDeployedRight);
                    allTroopsAttack(troopsDeployedRight, king);
                }
            }

        } else {
//            System.out.println("End Game");
            PlayerManager.printToChatAll(Constant.GAMEOVER, player.getUsername() + "is winner");
        }
    }

    private void allTroopsAttack(List<Troop> listOfTroop, Tower tower) { // index to know order of troop
        for (Troop troop : listOfTroop) {
            if (tower.isAlive()) {
                troop.attackTower(tower);
            }
        }
    }

    private void checkAlive(List<Troop> listOfTroop) {
        for (Troop t : listOfTroop) {
            if (!t.isAlive()) {
                listOfTroop.remove(t);
                break;
            }
        }
    }

    // generate 3 troops to choose in 1 turn
    private void addTroopsChoice(Player player) {
        String id = player.getId();
        List<Troop> dataTroops = listOfTroopsFromJson();
        do {
            int randnumber = rand.nextInt(dataTroops.size());
            Troop troop = dataTroops.get(randnumber);
            if (!troopsForChoice.contains(troop)) {
                troopsForChoice.add(troop);
            }
        } while (troopsForChoice.size() < 3);
    }

    private void printTroopList(List<Troop> listOfTroop, Player player) {
//        System.out.println("Available Troops for this turn: ");
//        sendMessage("troops", "Available Troops for this turn: ");
        String message = "";
        int count = 0;
        for (Troop t : listOfTroop) {
            message += count++ + " : " + t.toString() + "<br>";
        }
        PlayerManager.printToChat(Constant.TROOPS, player.getId(), message);
    }

    private List<Troop> listOfTroopsFromJson() {
        List<Troop> listOfTroop = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(new FileReader("D:\\WORK\\GitHub\\ClashRoyale\\src\\main\\resources\\towerandtroop.json"))
                    .getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonArray troopArray = jsonObject.getAsJsonArray("troops");
        for (int i = 0; i < troopArray.size(); i++) {
            JsonObject obj = troopArray.get(i).getAsJsonObject();
            Troop t = gson.fromJson(obj, Troop.class);
            listOfTroop.add(t);
        }
        return listOfTroop;
    }

    private List<Tower> listOfTowerFromJson(Player player) {
        List<Tower> listOfTower = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(new FileReader("D:\\WORK\\GitHub\\ClashRoyale\\src\\main\\resources\\towerandtroop.json"))
                    .getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonArray towerArray = jsonObject.getAsJsonArray("tower");
        for (int i = 0; i < towerArray.size(); i++) {
            JsonObject obj = towerArray.get(i).getAsJsonObject();
            Tower t = gson.fromJson(obj, Tower.class);
            t.setName(t.getName() + " " + player.getUsername());
            listOfTower.add(t);
        }
        return listOfTower;
    }

    // Received Cmd From Websocket
    public void deployTroop(String message) {
        String[] msg = message.split(",");
        choice = Integer.parseInt(msg[0]);
        lane = Integer.parseInt(msg[1]);
    }
}
