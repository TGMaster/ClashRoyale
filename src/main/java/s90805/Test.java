/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s90805;

import Entity.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Kuro
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//        Tower guard = new Tower("Guard", 1000, 300, 100);
//        Tower king = new Tower("King", 2000, 500, 300);
//        Troop pawn = new Troop("Pawn", 50, 150, 100);
//        Troop bisop = new Troop("Bisop", 100, 200, 150);
//        Troop rook = new Troop("Rook", 250, 200, 200);
//        Troop knight = new Troop("Knight", 200, 300, 150);
//        Troop prince = new Troop("Prince", 500, 400, 300);
//        List<Tower> listOfTower = new ArrayList<Tower>();
//        listOfTower.add(guard);
//        listOfTower.add(king);
//        List<Troop> listOfTroop = new ArrayList<Troop>();
//        listOfTroop.add(pawn);
//        listOfTroop.add(bisop);
//        listOfTroop.add(rook);
//        listOfTroop.add(knight);
//        listOfTroop.add(prince);
//        Random rand = new Random();
//        //System.out.println(listOfTroop.size()); 
//        int turn = 0;
//        while (!listOfTroop.isEmpty()) {
//            System.out.println("Turn " + turn);
//            king.attackTroop(listOfTroop.get(rand.nextInt(listOfTroop.size())));
//            listOfTroop = checkAlive(listOfTroop);
//            for (Troop t : listOfTroop) {
//                t.attackTower(guard);
//            }
//            turn++;
//        }
        
        Game game = new Game();
    }

}
