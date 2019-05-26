/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s90805;

import Entity.Tower;
import Entity.Troop;

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
    private boolean isWin = false;
    private final int TICKS = 30;
    private final int TARGET_TIME = 1000 / TICKS;
    private int turn = 1;

    //contain all information troops
    List<Troop> listOfTroopTurn = new ArrayList<Troop>(); //contain player's troops in turn (alive troops)
    List<Tower> listOfTower = new ArrayList<Tower>(); //contain all information about towers
    //List<Tower> listOfTowerTurn = new ArrayList<Tower>(); //contain player's tower in turn
    List<Troop> troopsForChoice = new ArrayList<Troop>(); // contain 3 different troops at anytime for player choose to spawn
    Tower guard1, guard2, king;
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
        guard1 = new Tower("Guard Tower 1", 1000, 300, 100);
        guard2 = new Tower("Guard Tower 2", 1000, 300, 100);
        king = new Tower("King Tower", 2000, 500, 200); //Decrease defend to 200 origin is 300
        in = new Scanner(System.in);
    }

    private void update() {
        if (king.isAlive()) {
            //should replace win/loose status
            troopsForChoice = iniTroopsChoice(); //generate 3 different random troops per turn to choose
            //System.out.println("List: " + listOfTroop.toString());
            System.out.print("Choose Troop to spawn : ");
            int number = in.nextInt();
            Troop t = troopsForChoice.get(number);
            listOfTroopTurn.add(t); //add to alive groups
            //first guard tower still alive
            if (guard1.isAlive()) {
                Troop troop = listOfTroopTurn.get(0); //take first troop
                guard1.attackTroop(troop);
                listOfTroopTurn = checkAlive(listOfTroopTurn);
                if (troop.isAlive()) { //first troop still alive after guard 1 attack
                    troop.attackTower(guard1);
                    guard2.attackTroop(troop);
                    listOfTroopTurn = checkAlive(listOfTroopTurn);
                    if (troop.isAlive()) { //first troop still alive after guard 2 attack
                        king.attackTroop(troop);
                        listOfTroopTurn = checkAlive(listOfTroopTurn);
                        if (!listOfTroopTurn.isEmpty()) {
                            allTroopsAttack(listOfTroopTurn, 1);
                        }
                    } else if (!listOfTroopTurn.isEmpty()) { //first troop dead after guard 2 attack
                        troop = listOfTroopTurn.get(0); // take second troop
                        king.attackTroop(troop);
                        listOfTroopTurn = checkAlive(listOfTroopTurn);
                        if (!listOfTroopTurn.isEmpty()) {
                            allTroopsAttack(listOfTroopTurn, 0);
                        }
                    }
                    System.out.println("End This Turn");
                } else if (!listOfTroopTurn.isEmpty()) { //first troop dead after guard 1 attack
                    troop = listOfTroopTurn.get(0); // take second troop
                    troop.attackTower(guard1);
                    guard2.attackTroop(troop);
                    listOfTroopTurn = checkAlive(listOfTroopTurn);
                    if (troop.isAlive()) { //second troop still alive after guard 2 attack
                        king.attackTroop(troop);
                        listOfTroopTurn = checkAlive(listOfTroopTurn);
                        if (!listOfTroopTurn.isEmpty()) {
                            allTroopsAttack(listOfTroopTurn, 1);
                        }
                    } else if (!listOfTroopTurn.isEmpty()) { //second troop dead after guard 2 attack
                        troop = listOfTroopTurn.get(0); // take third troop
                        king.attackTroop(troop);
                        listOfTroopTurn = checkAlive(listOfTroopTurn);
                        if (!listOfTroopTurn.isEmpty()) {
                            allTroopsAttack(listOfTroopTurn, 0);
                        }
                    }
                    System.out.println("End This Turn");
                }
            } else if (guard2.isAlive()) { //guard tower 1 is destroy guard tower 2 and king tower is still alive
                Troop troop = listOfTroopTurn.get(0); //take first troop
                guard2.attackTroop(troop);
                listOfTroopTurn = checkAlive(listOfTroopTurn);
                if (troop.isAlive()) { //first troop still alive after guard 2 attack
                    System.out.println("Troops alive in this turn" + listOfTroopTurn);
                    System.out.print("Choose guard tower (0) or king tower (1) " + troop.getName() + " to attack");
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
                        allTroopsAttack(listOfTroopTurn, 1);
                    }
                    System.out.println("End This Turn");
                } else if (!listOfTroopTurn.isEmpty()) { //first troop dead after guard 2 attack
                    System.out.println("Troops alive in this turn" + listOfTroopTurn);
                    troop = listOfTroopTurn.get(0); // take second troop
                    System.out.print("Choose guard tower (0) or king tower (1) " + troop.getName() + " to attack");
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
                        allTroopsAttack(listOfTroopTurn, 0);
                    }
                    System.out.println("End This Turn");
                }
            } else { // only king tower alive
                Troop troop = listOfTroopTurn.get(0); //take first troop
                king.attackTroop(troop);
                listOfTroopTurn = checkAlive(listOfTroopTurn);
                if (!listOfTroopTurn.isEmpty()) {
                    allTroopsAttack(listOfTroopTurn, 0);
                }
                System.out.println("End This Turn");
            }
        } else {
            System.out.println("End Game");
        }
    }

    private void allTroopsAttack(List<Troop> listOfTroop, int index) { //index to know order of troop
        System.out.println("Troops alive in this turn" + listOfTroop);
        for (int i = index; i < listOfTroop.size(); i++) {
            Troop troop = listOfTroop.get(i);
            if (guard1.isAlive()) {
                troop.attackTower(guard1);
            } else if (guard2.isAlive() && king.isAlive()) {
                System.out.print("Choose guard tower (0) or king tower (1) " + troop.getName() + " to attack");
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
                System.out.println(t.getName() + " is dead ");
                iterator.remove();
            }
            // System.out.print(listOfTroop);
        }
        return listOfTroop;
    }

    // generate 3 troops to choose in 1 turn
    private List<Troop> iniTroopsChoice() {
        List<Troop> listOfTroop = new ArrayList<Troop>();
        Troop pawn = new Troop("Pawn", 50, 150, 100);
        Troop bishop = new Troop("Bishop", 100, 200, 150);
        Troop rook = new Troop("Rook", 250, 200, 200);
        Troop knight = new Troop("Knight", 200, 300, 150);
        Troop prince = new Troop("Prince", 500, 400, 200);
        listOfTroop.add(pawn);
        listOfTroop.add(bishop);
        listOfTroop.add(rook);
        listOfTroop.add(knight);
        listOfTroop.add(prince);
        List<Troop> troops = new ArrayList<Troop>();
        Set<Troop> troopsChoice = new HashSet<Troop>();
        while (troopsChoice.size() < 3) {
            Troop t = listOfTroop.get(rand.nextInt(listOfTroop.size()));
            troopsChoice.add(t);
        }
        troops.addAll(troopsChoice);
        Iterator<Troop> iterator = troops.iterator();
        // print out 3 troops to choose in 1 turn
        System.out.println("Available Troops for this turn: ");
        int count = 1;
        while (iterator.hasNext()) {
            Troop t = iterator.next();
            System.out.println(count++ + " : " + t.toString());
        }
        return troops;
    }
}
