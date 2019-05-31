/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s90805;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * @author S410U
 */
public class Game implements Runnable {

    private Thread thread;
    private boolean running;
    private final int TICKS = 30;
    private final int TARGET_TIME = 1000 / TICKS;
    private int turn = 1;

    private String message = "";

    //contain all information troops

    List<Troop> listOfTroopTurn = new ArrayList<Troop>(); //contain player's troops in turn (alive troops)
    List<Tower> listOfTower = new ArrayList<Tower>(); //contain all information about towers
    //List<Tower> listOfTowerTurn = new ArrayList<Tower>(); //contain player's tower in turn
    List<Troop> troopsForChoice = new ArrayList<Troop>(); // contain 3 different troops at anytime for player choose to spawn
    Tower guard1, guard2, king;
    Player player;
    Random rand = new Random();
    private Scanner in;

    public Game() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

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

//            wait = TARGET_TIME - elapsed / 1000000;
//            if (wait < 0) {
//                wait = TARGET_TIME;
//            }
            wait = 2000;

            try {
                Thread.sleep(wait);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        running = true;
        listOfTower = listOfTowerFromJson();
        king = listOfTower.get(0); //Decrease defend to 200 origin is 300
        guard1 = listOfTower.get(1);
        guard2 = listOfTower.get(2);
        player = new Player("1", "test", "123", 5);
        in = new Scanner(System.in);
    }

    private void update() {
        if (king.isAlive()) {
            player.regenMana();
            //System.out.println(player.getUsername() + " turn ");
            //System.out.println("Mana pool of " + player.getUsername() + " in this turn : " + player.getMana());
            message += "Mana pool of " + player.getUsername() + " in this turn : " + player.getMana() + "\n";
            troopsForChoice = iniTroopsChoice(troopsForChoice); //generate 3 different random troops per turn to choose
            boolean flag = false;
            while (!flag) {
                printTroopList(troopsForChoice);
                //System.out.print("Choose Troop to spawn (0-2) or End this turn (9) : ");
                //int number = in.nextInt();

                int number = -1; //Initialize
                if (number == 9) { //end turn
                    //System.out.println("End this turn");
                    flag = true;
                } else if (number >= 0 && number <= 2) {
                    Troop t = troopsForChoice.get(number);
                    if (player.spawnTroop(t)) { //if enough mana to spawn troop
                        flag = true;
                        listOfTroopTurn.add(t); //add to alive groups
                        troopsForChoice.remove(t);
                        //first guard tower still alive
                        if (guard1.isAlive() && guard2.isAlive()) {
                            Troop troop = listOfTroopTurn.get(0); //take first troop
                            guard1.attackTroop(troop);
                            listOfTroopTurn = checkAlive(listOfTroopTurn);
                            if (troop.isAlive()) { //first troop still alive after guard 1 attack
                                //System.out.print("Choose guard tower 1 (0) or guard tower 2 (1) " + troop.getName() + " to attack: ");
                                int choiceTower = in.nextInt();
                                if (choiceTower == 0) {
                                    if (guard1.isAlive()) {
                                        troop.attackTower(guard1);
                                    }
                                } else {
                                    if (guard2.isAlive()) {
                                        troop.attackTower(guard2);
                                    }
                                }
                                guard2.attackTroop(troop);
                                listOfTroopTurn = checkAlive(listOfTroopTurn);
                                if (troop.isAlive()) { //first troop still alive after guard 2 attack
                                    king.attackTroop(troop);
                                    listOfTroopTurn = checkAlive(listOfTroopTurn);
                                    if (!listOfTroopTurn.isEmpty()) {
                                        if (troop.isAlive()) { //first troop still alive after king attack
                                            allTroopsAttack(listOfTroopTurn, 1);
                                        } else { //first troop dead after king attack
                                            allTroopsAttack(listOfTroopTurn, 0);
                                        }
                                    }
                                } else if (!listOfTroopTurn.isEmpty()) { //first troop dead after guard 2 attack
                                    troop = listOfTroopTurn.get(0); // take second troop
                                    king.attackTroop(troop);
                                    listOfTroopTurn = checkAlive(listOfTroopTurn);
                                    if (!listOfTroopTurn.isEmpty()) {
                                        allTroopsAttack(listOfTroopTurn, 0);
                                    }
                                }
                                //System.out.println("End This Turn");
                            } else if (!listOfTroopTurn.isEmpty()) { //first troop dead after guard 1 attack
                                troop = listOfTroopTurn.get(0); // take second troop
                                //System.out.print("Choose guard tower 1 (0) or guard tower 2 (1) " + troop.getName() + " to attack: ");
                                int choiceTower = in.nextInt();
                                if (choiceTower == 0) {
                                    if (guard1.isAlive()) {
                                        troop.attackTower(guard1);
                                    }
                                } else {
                                    if (guard2.isAlive()) {
                                        troop.attackTower(guard2);
                                    }
                                }
                                guard2.attackTroop(troop);
                                listOfTroopTurn = checkAlive(listOfTroopTurn);
                                if (troop.isAlive()) { //second troop still alive after guard 2 attack
                                    king.attackTroop(troop);
                                    listOfTroopTurn = checkAlive(listOfTroopTurn);
                                    if (!listOfTroopTurn.isEmpty()) {
                                        if (troop.isAlive()) { //second troop still alive after king attack
                                            allTroopsAttack(listOfTroopTurn, 1);
                                        } else { //second troop dead after king attack
                                            allTroopsAttack(listOfTroopTurn, 0);
                                        }
                                    }
                                } else if (!listOfTroopTurn.isEmpty()) { //second troop dead after guard 2 attack
                                    troop = listOfTroopTurn.get(0); // take third troop
                                    king.attackTroop(troop);
                                    listOfTroopTurn = checkAlive(listOfTroopTurn);
                                    if (!listOfTroopTurn.isEmpty()) {
                                        allTroopsAttack(listOfTroopTurn, 0);
                                    }
                                }
                                //System.out.println("End This Turn");
                            }
                        } else if (guard2.isAlive() && !guard1.isAlive()) { //guard tower 1 is destroy guard tower 2 and king tower is still alive
                            Troop troop = listOfTroopTurn.get(0); //take first troop
                            guard2.attackTroop(troop);
                            listOfTroopTurn = checkAlive(listOfTroopTurn);
                            if (troop.isAlive()) { //first troop still alive after guard 2 attack
                                //System.out.println("Troops alive in this turn" + listOfTroopTurn);
                                //System.out.print("Choose guard tower (0) or king tower (1) " + troop.getName() + " to attack: ");
                                int choiceTower = in.nextInt();
                                if (choiceTower == 0) {
                                    if (guard2.isAlive()) {
                                        troop.attackTower(guard2);
                                    } else {
                                        troop.attackTower(king);
                                    }
                                } else {
                                    if (king.isAlive()) {
                                        troop.attackTower(king);
                                    }
                                }
                                king.attackTroop(troop);
                                listOfTroopTurn = checkAlive(listOfTroopTurn);
                                if (!listOfTroopTurn.isEmpty()) {
                                    if (troop.isAlive()) { //first troop still alive after king attack
                                        allTroopsAttack(listOfTroopTurn, 1);
                                    } else { //first troop dead after king attack
                                        allTroopsAttack(listOfTroopTurn, 0);
                                    }
                                }
                                //System.out.println("End This Turn");
                            } else if (!listOfTroopTurn.isEmpty()) { //first troop dead after guard 2 attack
                                //System.out.println("Troops alive in this turn" + listOfTroopTurn);
                                troop = listOfTroopTurn.get(0); // take second troop
                                //System.out.print("Choose guard tower (0) or king tower (1) " + troop.getName() + " to attack: ");
                                int choiceTower = in.nextInt();
                                if (choiceTower == 0) {
                                    if (guard2.isAlive()) {
                                        troop.attackTower(guard2);
                                    }
                                } else {
                                    if (king.isAlive()) {
                                        troop.attackTower(king);
                                    }
                                }
                                king.attackTroop(troop);
                                listOfTroopTurn = checkAlive(listOfTroopTurn);
                                if (!listOfTroopTurn.isEmpty()) {
                                    if (troop.isAlive()) { //second troop still alive after king attack
                                        allTroopsAttack(listOfTroopTurn, 1);
                                    } else { //second troop dead after king attack
                                        allTroopsAttack(listOfTroopTurn, 0);
                                    }
                                }
                                //System.out.println("End This Turn");
                            }
                        } else if (guard1.isAlive() && !guard2.isAlive()) { //guard tower 2 is destroy guard tower 2 and king tower is still alive
                            Troop troop = listOfTroopTurn.get(0); //take first troop
                            guard1.attackTroop(troop);
                            listOfTroopTurn = checkAlive(listOfTroopTurn);
                            if (troop.isAlive()) { //first troop still alive after guard 1 attack
                                //System.out.println("Troops alive in this turn" + listOfTroopTurn);
                                //System.out.print("Choose guard tower (0) or king tower (1) " + troop.getName() + " to attack: ");
                                int choiceTower = in.nextInt();
                                if (choiceTower == 0) {
                                    if (guard1.isAlive()) {
                                        troop.attackTower(guard1);
                                    } else {
                                        troop.attackTower(king);
                                    }
                                } else {
                                    if (king.isAlive()) {
                                        troop.attackTower(king);
                                    }
                                }
                                king.attackTroop(troop);
                                listOfTroopTurn = checkAlive(listOfTroopTurn);
                                if (!listOfTroopTurn.isEmpty()) {
                                    if (troop.isAlive()) { //first troop still alive after king attack
                                        allTroopsAttack(listOfTroopTurn, 1);
                                    } else { //first troop dead after king attack
                                        allTroopsAttack(listOfTroopTurn, 0);
                                    }
                                }
                                //System.out.println("End This Turn");
                            } else if (!listOfTroopTurn.isEmpty()) { //first troop dead after guard 2 attack
                                //System.out.println("Troops alive in this turn" + listOfTroopTurn);
                                troop = listOfTroopTurn.get(0); // take second troop
                                //System.out.print("Choose guard tower (0) or king tower (1) " + troop.getName() + " to attack: ");
                                int choiceTower = in.nextInt();
                                if (choiceTower == 0) {
                                    if (guard1.isAlive()) {
                                        troop.attackTower(guard2);
                                    }
                                } else {
                                    if (king.isAlive()) {
                                        troop.attackTower(king);
                                    }
                                }
                                king.attackTroop(troop);
                                listOfTroopTurn = checkAlive(listOfTroopTurn);
                                if (!listOfTroopTurn.isEmpty()) {
                                    if (troop.isAlive()) { //second troop still alive after king attack
                                        allTroopsAttack(listOfTroopTurn, 1);
                                    } else { //second troop dead after king attack
                                        allTroopsAttack(listOfTroopTurn, 0);
                                    }
                                }
                                //System.out.println("End This Turn");
                            }
                        } else { // only king tower alive
                            Troop troop = listOfTroopTurn.get(0); //take first troop
                            king.attackTroop(troop);
                            listOfTroopTurn = checkAlive(listOfTroopTurn);
                            if (!listOfTroopTurn.isEmpty()) {
                                allTroopsAttack(listOfTroopTurn, 0);
                            }
                            //System.out.println("End This Turn");
                        }
                    } else {
                        continue;
                    }
                }
            }


        } else {
            System.out.println("End Game");
            running = false;
        }
    }

    private void allTroopsAttack(List<Troop> listOfTroop, int index) { //index to know order of troop
        //System.out.println("Troops alive in this turn" + listOfTroop);
        for (int i = index; i < listOfTroop.size(); i++) {
            Troop troop = listOfTroop.get(i);
            if (guard1.isAlive() && guard2.isAlive()) {
                //System.out.print("Choose guard tower 1 (0) or guard tower 2 (1) " + troop.getName() + " to attack: ");
                int choiceTower = in.nextInt();
                if (choiceTower == 0) {
                    if (guard1.isAlive()) {
                        troop.attackTower(guard1);
                    }
                } else {
                    if (guard2.isAlive()) {
                        troop.attackTower(guard2);
                    }
                }
            } else if (guard2.isAlive() && !guard1.isAlive()) {
                //System.out.print("Choose guard tower (0) or king tower (1) " + troop.getName() + " to attack: ");
                int choiceTower = in.nextInt();
                if (choiceTower == 0) {
                    if (guard2.isAlive()) {
                        troop.attackTower(guard2);
                    } else {
                        troop.attackTower(king);
                    }
                } else {
                    if (king.isAlive()) {
                        troop.attackTower(king);
                    }
                }
            } else if (guard1.isAlive() && !guard2.isAlive()) {
                //System.out.print("Choose guard tower (0) or king tower (1) " + troop.getName() + " to attack: ");
                int choiceTower = in.nextInt();
                if (choiceTower == 0) {
                    if (guard1.isAlive()) {
                        troop.attackTower(guard1);
                    } else {
                        troop.attackTower(king);
                    }
                } else {
                    if (king.isAlive()) {
                        troop.attackTower(king);
                    }
                }
            } else {
                if (king.isAlive()) {
                    troop.attackTower(king);
                }
            }
        }
    }

    private List<Troop> checkAlive(List<Troop> listOfTroop) {
        Iterator<Troop> iterator = listOfTroop.iterator();
        while (iterator.hasNext()) {
            Troop t = iterator.next();
            if (!t.isAlive()) {
                //System.out.println(t.getName() + " is dead ");
                iterator.remove();
            }
            // System.out.print(listOfTroop);
        }
        return listOfTroop;
    }

    // generate 3 troops to choose in 1 turn
    private List<Troop> iniTroopsChoice(List<Troop> listOfTroops) {
        List<Troop> dataTroops = listOfTroopsFromJson();
        List<Troop> tempList = new ArrayList<Troop>();
        Set<Troop> troopsChoice = new HashSet<Troop>(listOfTroops);
        ;
        //troopsChoice.addAll(troops);
        while (troopsChoice.size() < 3) {
            Troop t = dataTroops.get(rand.nextInt(dataTroops.size()));
            troopsChoice.add(t);
        }
        tempList.addAll(troopsChoice);
        listOfTroops = tempList;
        return listOfTroops;
    }

    private void printTroopList(List<Troop> listOfTroop) {
        //System.out.println("Available Troops for this turn: ");
        int count = 0;
        for (Troop t : listOfTroop) {

            //System.out.println(count++ + " : " + t.toString());
        }
    }

    private List<Troop> listOfTroopsFromJson() {
        List<Troop> listOfTroop = new ArrayList<Troop>();
        Gson gson = new Gson();
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(new FileReader("src/main/resources/towerandtroop.json")).getAsJsonObject();
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
            jsonObject = new JsonParser().parse(new FileReader("src/main/resources/towerandtroop.json")).getAsJsonObject();
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
}
