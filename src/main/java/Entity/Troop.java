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
            PlayerManager.sendCmd("cmd", this.name + " has attacked " + tower.getName() + " " + dmg + " damage");
//            System.out.println(this.name + " has attacked " + tower.getName() + " " + dmg + " damage");
            if (tower.getHp() <= 0) {
                PlayerManager.sendCmd("cmd", this.name + " has destroyed " + tower.getName());
//                System.out.println(this.name + " destroyed " + tower.getName() + " Tower");
            } else {
//                System.out.println(tower.getName() + " has " + tower.getHp() + " HP left ");
            }

        } else {
            PlayerManager.sendCmd("cmd", this.name + " cannot attack " + tower.getName());
//            System.out.println(this.name + " cannot attack " + tower.getName());
        }
    }

    //check alive
    public boolean isAlive() {
        return (this.hp > 0);
    }
}
