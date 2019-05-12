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
 *
 * @author S410U
 */
public class Game implements Runnable {

	private Thread thread;
	private boolean running;
	private boolean isWin = false;
	private final int TICKS = 30;
	private final int TARGET_TIME = 1000 / TICKS;
	private int turn = 1;

    List<Troop> listOfTroop = new ArrayList<Troop>(); //contain all information troops
    List<Troop> listOfTroopTurn = new ArrayList<Troop>(); //contain player's troops in turn (alive troops)
    List<Tower> listOfTower = new ArrayList<Tower>(); //contain all information about towers
    //List<Tower> listOfTowerTurn = new ArrayList<Tower>(); //contain player's tower in turn
    List<Troop> troopsForChoice = new ArrayList<Troop>(); // contain 3 different troops at anytime for player choose to spawn
    Tower guard, king;
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
    	guard = new Tower("Guard", 1000, 300, 100);
    	king = new Tower("King", 2000, 500, 300);
    	Troop pawn = new Troop("Pawn", 50, 150, 100);
    	Troop bisop = new Troop("Bisop", 100, 200, 150);
    	Troop rook = new Troop("Rook", 250, 200, 200);
    	Troop knight = new Troop("Knight", 200, 300, 150);
    	Troop prince = new Troop("Prince", 500, 400, 200);
    	listOfTower.add(guard);
    	listOfTower.add(king);
    	listOfTroop.add(pawn);
    	listOfTroop.add(bisop);
    	listOfTroop.add(rook);
    	listOfTroop.add(knight);
    	listOfTroop.add(prince);
    	in = new Scanner(System.in);
    }

    private void update() {
        if (!isWin) { //should replace win/loose status
            troopsForChoice = iniTroopsChoice(listOfTroop); //generate 3 different random troops per turn to choose
            //System.out.println("List: " + listOfTroop.toString());
            System.out.print("Choose Troop to spawn : ");
            int number = in.nextInt();
            Troop t = troopsForChoice.get(number);
            listOfTroopTurn.add(t); //add to alive groups
            while (t.isAlive()) {
            	System.out.println("Turn " + turn);
            	if (guard.isAlive()) {
            		guard.attackTroop(t);
            	}
            	if (king.isAlive() && t.isAlive()) {
            		king.attackTroop(t);
            	} else {
            		running = false;
            	}
            	listOfTroopTurn = checkAlive(listOfTroopTurn);
            	if (listOfTroopTurn.isEmpty()) {
            		running = false;
            	}
            	if (t.isAlive()) {
            		if (guard.isAlive()) {
            			t.attackTower(guard);
            		} else if (king.isAlive()) {
            			t.attackTower(king);
            		} else {
            			running = false;
            		}
            	}
            	turn++;
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
    private List<Troop> iniTroopsChoice(List<Troop> listOfTroop) {
    	Set<Troop> troopsChoice = new HashSet<Troop>();
    	while (troopsChoice.size() < 3) {
    		troopsChoice.add(listOfTroop.get(rand.nextInt(listOfTroop.size())));
    	}
    	troopsForChoice.addAll(troopsChoice);
    	Iterator<Troop> iterator = troopsForChoice.iterator();
        // print out 3 troops to choose in 1 turn
    	System.out.println("Available Troops for this turn: ");
    	int count = 1;
    	while (iterator.hasNext()) {
    		Troop t = iterator.next();
    		System.out.println(count++ + " : " + t.toString());
    	}

    	return troopsForChoice;
    }
}
