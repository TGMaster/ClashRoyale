/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s90805;

import Controller.PlayerManager;
import Entity.Player;
import Entity.Tower;
import Entity.Troop;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * @author S410U
 */
public class Game implements Runnable {

    private volatile boolean running;
    private final int TICKS = 30;
    private final int TARGET_TIME = 1000 / TICKS;

    // Websocket variable
    private volatile int lane;
    private volatile int choice;
    private String message = "";

    // contain all information troops
    private List<Troop> listOfTroopLeft = new ArrayList<Troop>(); // contain player's troops in left (alive troops)
    private List<Troop> listOfTroopRight = new ArrayList<Troop>(); // contain player's troops in right (alive troops)
    private List<Tower> listOfTower = new ArrayList<Tower>(); // contain all information about towers

    // List<Tower> listOfTowerTurn = new ArrayList<Tower>(); //contain player's
    // tower in turn
    List<Troop> troopsForChoice = new ArrayList<Troop>(); // contain 3 different troops at anytime for player choose to
    // spawn
    private Tower guard1, guard2, king;
    Player player;
    private final Random rand = new Random();
    private Scanner in;

    public Game(Player player) {
        this.player = player;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        init();
        long start;
        long elapsed;
        long wait;

        // Loop
        while (running) {
            start = System.nanoTime();
            update();
            elapsed = System.nanoTime() - start;

            // wait = TARGET_TIME - elapsed / 1000000;
            // if (wait < 0) {
            // wait = TARGET_TIME;
            // }
            wait = 1000;
            choice = -1;
            lane = 0;

            try {
                Thread.sleep(wait);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        running = true;
        choice = -1;
        lane = 0;
        listOfTower = listOfTowerFromJson();
        king = listOfTower.get(0); // Decrease defend to 200 origin is 300
        guard1 = listOfTower.get(1);
        guard2 = listOfTower.get(2);
//        in = new Scanner(System.in);
    }

    private void update() {
        if (king.isAlive()) {
            player.regenMana();
//            System.out.println(player.getUsername() + " turn ");
//            System.out.println("Mana pool of " + player.getUsername() + " in this turn : " + player.getMana());
            sendMessage("mana", "" + player.getMana());

            if (troopsForChoice.size() < 3) {
                addTroopsChoice(); // auto add new troops
            }
            printTroopList(troopsForChoice);
//            System.out.print("Choose Troop to spawn (0-2): ");
//            int choice = in.nextInt();

            if (choice >= 0 && choice <= 2) {
                Troop t = troopsForChoice.get(choice);
                if (player.spawnTroop(t)) { // if enough mana to spawn troop
                    troopsForChoice.remove(t);
//                    System.out.print("Choose lane 0 - Left, 1 - Right: ");
//                    int lane = in.nextInt();

                    // Choose left lane
                    if (lane == 1) {
                        // Add to list Left
                        listOfTroopLeft.add(t);
                    } else if (lane == 2) {
                        // Add to list Right
                        listOfTroopRight.add(t);
                    }
                }
            }

            if (!listOfTroopLeft.isEmpty()) {
                Troop troopLeft = listOfTroopLeft.get(0); //Get first troop
                // Guard 1 is alive
                if (guard1.isAlive()) {
                    guard1.attackTroop(troopLeft);
                    listOfTroopLeft = checkAlive(listOfTroopLeft);
                    allTroopsAttack(listOfTroopLeft, guard1);
                } // Guard 1 is dead
                else {
                    king.attackTroop(troopLeft);
                    listOfTroopLeft = checkAlive(listOfTroopLeft);
                    allTroopsAttack(listOfTroopLeft, king);
                }
            }
            if (!listOfTroopRight.isEmpty()) {
                Troop troopRight = listOfTroopRight.get(0); //Get first troop
                // Guard 2 is alive
                if (guard2.isAlive()) {
                    guard2.attackTroop(troopRight);
                    listOfTroopRight = checkAlive(listOfTroopRight);
                    allTroopsAttack(listOfTroopRight, guard2);
                } // Guard 2 is dead
                else {
                    king.attackTroop(troopRight);
                    listOfTroopRight = checkAlive(listOfTroopRight);
                    allTroopsAttack(listOfTroopRight, king);
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

    private List<Troop> checkAlive(List<Troop> listOfTroop) {
        for (Troop t : listOfTroop) {
            if (!t.isAlive()) {
//                System.out.println(t.getName() + " is dead ");
                listOfTroop.remove(t);
                break;
            }
//            System.out.print(listOfTroop);
        }
        return listOfTroop;
    }

    // generate 3 troops to choose in 1 turn
    private void addTroopsChoice() {
        List<Troop> dataTroops = listOfTroopsFromJson();

        while (troopsForChoice.size() < 3) {
            int randnumber = rand.nextInt(dataTroops.size());
            Troop troop = dataTroops.get(randnumber);
            if (!troopsForChoice.contains(troop)) {
                troopsForChoice.add(troop);
            }
        }
    }

    private void printTroopList(List<Troop> listOfTroop) {
//        System.out.println("Available Troops for this turn: ");
//        sendMessage("troops", "Available Troops for this turn: ");
        int count = 0;
        for (Troop t : listOfTroop) {
            message += count++ + " : " + t.toString() + "<br>";
        }
        message = sendMessage("troops", message);
    }

    private List<Troop> listOfTroopsFromJson() {
        List<Troop> listOfTroop = new ArrayList<Troop>();
        Gson gson = new Gson();
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(new FileReader("src/main/resources/towerandtroop.json"))
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

    private List<Tower> listOfTowerFromJson() {
        List<Tower> listOfTower = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(new FileReader("src/main/resources/towerandtroop.json"))
                    .getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonArray towerArray = jsonObject.getAsJsonArray("tower");
        for (int i = 0; i < towerArray.size(); i++) {
            JsonObject obj = towerArray.get(i).getAsJsonObject();
            Tower t = gson.fromJson(obj, Tower.class);
            listOfTower.add(t);
        }
        return listOfTower;
    }

    private String sendMessage(String action, String msg) {
        if (!msg.equals("")) {
            PlayerManager.sendCmd(action, msg);
        }
        return "";
    }

    // Received Cmd From Websocket
    public void deployTroop(String message) {
        String[] msg = message.split(",");
        choice = Integer.parseInt(msg[0]);
        lane = Integer.parseInt(msg[1]);
    }
}
