/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.Player;
import Entity.Tower;
import Entity.Troop;
import Service.PlayerService;

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
public class Game implements Runnable {

    private volatile boolean running;
    private final int TICKS = 30;
    private final int TARGET_TIME = 1000 / TICKS;
    private final Random rand = new Random();

    // Websocket variable
    private volatile HashMap<String, Integer> lane = new HashMap<>();
    private volatile HashMap<String, Integer> choice = new HashMap<>();

    // contain all information troops
    private List<Troop> LeftLane = new ArrayList<>(); // contain player's troops in left (alive troops)
    private List<Troop> RightLane = new ArrayList<>(); // contain player's troops in right (alive troops)
    private List<Tower> listOfTower = new ArrayList<>(); // contain all information about towers

    private final HashMap<String, List<Troop>> troopsDeployedLeft = new HashMap<>();
    private final HashMap<String, List<Troop>> troopsDeployedRight = new HashMap<>();
    private final HashMap<String, List<Tower>> towersOfPlayer = new HashMap<>();

    // tower in turn
    private final HashMap<String, List<Troop>> troopsForChoice = new HashMap<>(); // contain 3 different troops at anytime for player choose to

    // spawn
    private Tower guard1, guard2, king;
    Player player1, player2;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void stop() {
        running = false;
    }

    public void resetChoice(Player player) {
        choice.clear();
        lane.clear();
    }

    @Override
    public void run() {
        init(player1);
        init(player2);
        running = true;
        long start;
        long elapsed;
        long wait;

        // Loop
        while (running) {
            start = System.nanoTime();
            update(player1);
            update(player2);
            elapsed = System.nanoTime() - start;

            // wait = TARGET_TIME - elapsed / 1000000;
            // if (wait < 0) {
            // wait = TARGET_TIME;
            // }
            wait = 1000;
            resetChoice(player1);
            resetChoice(player2);

            try {
                Thread.sleep(wait);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init(Player player) {
        resetChoice(player1);
        resetChoice(player2);
        troopsForChoice.put(player.getId(), new ArrayList<Troop>());
        troopsDeployedLeft.put(player.getId(), new ArrayList<Troop>());
        troopsDeployedRight.put(player.getId(), new ArrayList<Troop>());

        // Tower
        listOfTower = listOfTowerFromJson(player);
        towersOfPlayer.put(player.getId(), listOfTower);
    }

    private void update(Player player) {
        String id = player.getId();
        king = towersOfPlayer.get(id).get(0);
        if (king.isAlive()) {

            // Regen Mana
            player.regenMana();
//            System.out.println(player.getUsername() + " turn ");
//            System.out.println("Mana pool of " + player.getUsername() + " in this turn : " + player.getMana());
            PlayerManager.printToChat("mana", player.getId(), "" + player.getMana());

            // Add troops
            if (troopsForChoice.get(id).size() < 3) {
                addTroopsChoice(player);
            }
            printTroopList(troopsForChoice.get(id), player);

//            System.out.print("Choose Troop to spawn (0-2): ");
//            int choice = in.nextInt();
            if (!choice.isEmpty() && choice.get(id) != null && choice.get(id) >= 0 && choice.get(id) <= 2) {
                Troop t = troopsForChoice.get(id).get(choice.get(id));
                if (player.spawnTroop(t)) { // if enough mana to spawn troop
                    troopsForChoice.get(id).remove(t);
//                    System.out.print("Choose lane 0 - Left, 1 - Right: ");
//                    int lane = in.nextInt();

                    if (!lane.isEmpty()) {
                        // Choose left lane
                        if (lane.get(id) == 1) {
                            // Add to list Left
                            LeftLane.add(t);
                            troopsDeployedLeft.get(id).add(t);
                        } else if (lane.get(id) == 2) {
                            // Add to list Right
                            RightLane.add(t);
                            troopsDeployedRight.get(id).add(t);
                        }
                    }
                }
            }

            if (!troopsDeployedLeft.isEmpty() && !troopsDeployedLeft.get(id).isEmpty() && troopsDeployedLeft.get(id) != null) {
                guard1 = towersOfPlayer.get(id).get(1);
                Troop troopLeft = troopsDeployedLeft.get(id).get(0); //Get first troop
                // Guard 1 is alive
                if (guard1.isAlive()) {
                    guard1.attackTroop(troopLeft);
                    checkAlive(troopsDeployedLeft.get(id));
                    allTroopsAttack(troopsDeployedLeft.get(id), guard1);
                } // Guard 1 is dead
                else {
                    king.attackTroop(troopLeft);
                    checkAlive(troopsDeployedLeft.get(id));
                    allTroopsAttack(troopsDeployedLeft.get(id), king);
                }
            }
            if (!troopsDeployedLeft.isEmpty() && !troopsDeployedRight.get(id).isEmpty() && troopsDeployedRight.get(id) != null) {
                guard2 = towersOfPlayer.get(id).get(2);
                Troop troopRight = troopsDeployedRight.get(id).get(0); //Get first troop
                // Guard 2 is alive
                if (guard2.isAlive()) {
                    guard2.attackTroop(troopRight);
                    checkAlive(troopsDeployedRight.get(id));
                    allTroopsAttack(troopsDeployedRight.get(id), guard2);
                } // Guard 2 is dead
                else {
                    king.attackTroop(troopRight);
                    checkAlive(troopsDeployedRight.get(id));
                    allTroopsAttack(troopsDeployedRight.get(id), king);
                }
            }

        } else {
//            System.out.println("End Game");
            running = false;
        }
    }

    private void allTroopsAttack(List<Troop> listOfTroop, Tower tower) { // index to know order of troop
//        System.out.println("Troops alive in this turn: " + listOfTroop.toString());
        for (Troop troop : listOfTroop) {
            if (tower.isAlive()) {
                troop.attackTower(tower);
            }
        }
    }

    private void checkAlive(List<Troop> listOfTroop) {
        for (Troop t : listOfTroop) {
            if (!t.isAlive()) {
//                System.out.println(t.getName() + " is dead ");
                listOfTroop.remove(t);
                break;
            }
//            System.out.print(listOfTroop);
        }
    }

    // generate 3 troops to choose in 1 turn
    private void addTroopsChoice(Player player) {
        String id = player.getId();
        List<Troop> dataTroops = listOfTroopsFromJson();
        do {
            int randnumber = rand.nextInt(dataTroops.size());
            Troop troop = dataTroops.get(randnumber);
            if (!troopsForChoice.get(id).contains(troop)) {
                troopsForChoice.get(id).add(troop);
            }
        } while (troopsForChoice.get(id).size() < 3);
    }

    private void printTroopList(List<Troop> listOfTroop, Player player) {
//        System.out.println("Available Troops for this turn: ");
//        sendMessage("troops", "Available Troops for this turn: ");
        String message = "";
        int count = 0;
        for (Troop t : listOfTroop) {
            message += count++ + " : " + t.toString() + "<br>";
        }
        PlayerManager.printToChat("troops", player.getId(), message);
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
    public void deployTroop(String id, String message) {
        String[] msg = message.split(",");
        choice.put(id, Integer.parseInt(msg[0]));
        lane.put(id, Integer.parseInt(msg[1]));
    }
}
