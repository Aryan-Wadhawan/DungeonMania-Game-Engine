package dungeonmania.entities.collectables;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.PassiveItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Treasure extends PassiveItem {
    public Treasure(Position position) {
        super(position);
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player player) {
            if (!player.pickUp(this))
                return;
            map.destroyEntity(this);
        }
    }
}
