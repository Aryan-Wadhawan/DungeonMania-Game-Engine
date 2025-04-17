package dungeonmania.entities.buildables;

import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.EntityFactory;

public interface BuildableBluePrint {
    String getName();

    boolean canBuild(Inventory inventory);

    void consumeItems(Inventory inventory);

    Buildable build(EntityFactory factory);
}
