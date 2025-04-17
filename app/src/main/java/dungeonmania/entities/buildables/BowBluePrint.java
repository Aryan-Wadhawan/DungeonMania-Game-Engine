package dungeonmania.entities.buildables;

import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.EntityFactory;

public class BowBluePrint implements BuildableBluePrint {
    @Override
    public String getName() {
        return "bow";
    }

    @Override
    public boolean canBuild(Inventory inv) {
        return inv.count(Wood.class) >= 1 && inv.count(Arrow.class) >= 3;
    }

    @Override
    public void consumeItems(Inventory inv) {
        inv.removeFirst(Wood.class);
        inv.removeFirst(Arrow.class, 3);
    }

    @Override
    public Buildable build(EntityFactory factory) {
        return factory.buildBow();
    }
}
