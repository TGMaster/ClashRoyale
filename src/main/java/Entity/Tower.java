package Entity;

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
            System.out.println(this.getName() + " Tower has attacked " + troop.getName() + " " + dmg + " damage");
            if (troop.getHp() < 0) {
                System.out.println(this.getName() + " Tower destroyed " + troop.getName());
            } else {
                System.out.println(troop.getName() + "  has " + troop.getHp() + " HP left ");
            }
        }

    }

    //check alive
    public boolean isAlive() {
        return (this.getHp() > 0);
    }
}
