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

    //attack tower
    public void attackTower(Tower tower) {
        if (tower.isAlive()) {
            int dmg = this.damage - tower.getDefense();
            if (dmg > 0) {
                tower.setHp(tower.getHp() - dmg);
                System.out.println(this.getName() + " has attacked " + tower.getName() + " Tower " + dmg + " damage");
                if (tower.getHp() <= 0) {
                    System.out.println(this.getName() + " destroyed " + tower.getName() + " Tower");
                } else {
                    System.out.println(tower.getName() + " Tower has " + tower.getHp() + " HP left ");
                }

            }
        } else {
            System.out.println("no tower to attack");
        }

    }

    //check alive
    public boolean isAlive() {
        return (this.getHp() > 0);
    }
}
