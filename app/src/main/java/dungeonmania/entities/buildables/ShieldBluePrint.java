package dungeonmania.entities.buildables;

import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.EntityFactory;

public class ShieldBluePrint implements BuildableBluePrint {
    @Override
    public String getName() {
        return "shield";
    }

    @Override
    public boolean canBuild(Inventory inv) {
        return inv.count(Wood.class) >= 2 && (inv.count(Treasure.class) >= 1 || inv.count(Key.class) >= 1);
    }

    @Override
    public void consumeItems(Inventory inv) {
        inv.removeFirst(Wood.class, 2);
        if (inv.count(Treasure.class) >= 1) {
            inv.removeFirst(Treasure.class);
        } else {
            inv.removeFirst(Key.class);
        }
    }

    @Override
    public Buildable build(EntityFactory factory) {
        return factory.buildShield();
    }
}
