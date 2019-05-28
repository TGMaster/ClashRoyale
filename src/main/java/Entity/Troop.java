package Entity;

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
            System.out.println(this.name + " has attacked " + tower.getName() + " Tower " + dmg + " damage");
            if (tower.getHp() <= 0) {
                System.out.println(this.name + " destroyed " + tower.getName() + " Tower");
            } else {
                System.out.println(tower.getName() + " has " + tower.getHp() + " HP left ");
            }

        } else {
            System.out.println(this.name + " cannot attack " + tower.getName());
        }
    }

    //check alive
    public boolean isAlive() {
        return (this.hp > 0);
    }
}
