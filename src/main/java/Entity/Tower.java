package Entity;

import Controller.PlayerManager;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Tower {

    private String name;
    private int hp;
    private int damage;
    private int defense;

    //attack troop
    public void attackTroop(Troop troop) {
        int dmg = this.damage - troop.getDefense();
        if (dmg > 0) {
            troop.setHp(troop.getHp() - dmg);
            PlayerManager.printToChatAll("cmd", this.name + " has attacked " + troop.getName() + " " + dmg + " damage");
            if (troop.getHp() < 0) {
                PlayerManager.printToChatAll("cmd", this.name + " has killed " + troop.getName());
            }
        } else {
            PlayerManager.printToChatAll("cmd", this.name + " cannot attack " + troop.getName());
        }

    }

    //check alive
    public boolean isAlive() {
        return (this.hp > 0);
    }
}
