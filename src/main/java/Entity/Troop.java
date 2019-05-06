package Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Troop {
    private String name;
    private int hp;
    private int damage;
    private int defense;
    //attack tower
    public void attackTower(Tower tower){
        int dmg = this.damage - tower.getDefense();
        if(dmg > 0){
            tower.setHp(tower.getHp() - dmg);
        }
    }
    //check alive
    public boolean isAlive(){
        return (this.getHp() > 0) ? true : false;
    }
}
