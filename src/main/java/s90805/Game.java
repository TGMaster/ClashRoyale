/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s90805;

import Entity.Tower;
import Entity.Troop;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author S410U
 */
public class Game implements Runnable {

    private Thread thread;
    private boolean running;
    private final int TICKS = 30;
    private final int TARGET_TIME = 1000 / TICKS;
    private int turn = 0;

    List<Troop> listOfTroop = new ArrayList<Troop>();
    List<Tower> listOfTower = new ArrayList<Tower>();
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
        Troop prince = new Troop("Prince", 500, 400, 300);
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
        if (!listOfTroop.isEmpty()) {
            System.out.println("List: " + listOfTroop.toString());
            System.out.print("Input: ");
            int number = in.nextInt();
            Troop t = listOfTroop.get(number);
            while (t.isAlive()) {
                System.out.println("Turn " + turn);
                guard.attackTroop(t);
                listOfTroop = checkAlive(listOfTroop);
                if (listOfTroop.isEmpty()) {
                    running = false;
                }
                t.attackTower(guard);

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
}
