package dungeonmania.entities.buildables;

import java.util.*;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.EntityFactory;

public class BuildableRegister {
    private static final List<BuildableBluePrint> blueprints = List.of(new BowBluePrint(), new ShieldBluePrint());

    public static List<String> getAvailableBuildables(Inventory inventory) {
        List<String> result = new ArrayList<>();
        for (BuildableBluePrint b : blueprints) {
            if (b.canBuild(inventory))
                result.add(b.getName());
        }
        return result;
    }

    public static Buildable build(String name, Inventory inventory, EntityFactory factory) {
        for (BuildableBluePrint b : blueprints) {
            if (b.getName().equals(name) && b.canBuild(inventory)) {
                b.consumeItems(inventory);
                return b.build(factory);
            }
        }
        return null;
    }
}
