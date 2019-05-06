package Entity;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Troop {
    private String name;
    private int hp;
    private int damage;
    private int defense;
}
