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
        float dmg = (float) (this.damage * ((100.0 / (100 + troop.getDefense()))));
        troop.setHp((int) (troop.getHp() - dmg));
        PlayerManager.printToChatAll("cmd", this.name + " has attacked " + troop.getName() + " " + (int) dmg + " damage");
        if (troop.getHp() <= 0) {
            PlayerManager.printToChatAll("cmd", this.name + " has killed " + troop.getName());
        }
    }

    //check alive
    public boolean isAlive() {
        return (this.hp > 0);
    }
}
