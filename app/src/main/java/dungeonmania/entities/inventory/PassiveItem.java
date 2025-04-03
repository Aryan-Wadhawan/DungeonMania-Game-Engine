package dungeonmania.entities.inventory;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.util.Position;

public abstract class PassiveItem extends InventoryItem {
    public PassiveItem(Position position) {
        super(position);
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return origin; // No buff applied
    }

    @Override
    public int getDurability() {
        return Integer.MAX_VALUE; // Infinite durability
    }
}
