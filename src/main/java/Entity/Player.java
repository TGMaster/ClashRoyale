/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author TGMaster
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private String id;
    private String username;
    private String password;

    //TODO
    private int mana;

    public void regenMana() {
        if (this.mana < 10) {
            this.setMana(this.mana + 1);
        }
    }

    public boolean spawnTroop(Troop troop) {
        if (this.mana >= troop.getMana()) {
            this.setMana(this.mana - troop.getMana());
            return true;
        } else {
            System.out.println("Not enough mana to create " + troop.getName());
            return false;
        }
    }
}
