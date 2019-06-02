package Entity;

import Controller.PlayerManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Troop {

    private String name;
    private int hp;
    private int damage;
    private int defense;
    private int mana;

    //attack tower
    public void attackTower(Tower tower) {
        int dmg = this.damage - tower.getDefense();
        if (dmg > 0) {
            tower.setHp(tower.getHp() - dmg);
            PlayerManager.printToChatAll("cmd", this.name + " has attacked " + tower.getName() + " " + dmg + " damage");
            if (tower.getHp() <= 0) {
                PlayerManager.printToChatAll("cmd", this.name + " has destroyed " + tower.getName());
            } else {
//                System.out.println(tower.getName() + " has " + tower.getHp() + " HP left ");
            }

        } else {
            PlayerManager.printToChatAll("cmd", this.name + " cannot attack " + tower.getName());
        }
    }

    //check alive
    public boolean isAlive() {
        return (this.hp > 0);
    }

    //attack troop
    public void attackTroop(Troop troop) {
        int dmg = this.damage - troop.getDefense();
        if (dmg > 0) {
            troop.setHp(troop.getHp() - dmg);
            PlayerManager.printToChatAll("cmd", this.name + " has attacked " + troop.getName() + " " + dmg + " damage");
            if (troop.getHp() <= 0) {
                PlayerManager.printToChatAll("cmd", this.name + " has killed " + troop.getName());
            } else {
//                System.out.println(tower.getName() + " has " + tower.getHp() + " HP left ");
            }

        } else {
            PlayerManager.printToChatAll("cmd", this.name + " cannot attack " + troop.getName());
        }
    }
}
