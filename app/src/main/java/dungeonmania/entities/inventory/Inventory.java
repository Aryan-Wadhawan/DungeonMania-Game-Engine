package dungeonmania.entities.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.BuildableRegister;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Useable;

public class Inventory {
    private List<InventoryItem> items = new ArrayList<>();

    public boolean add(InventoryItem item) {
        items.add(item);
        return true;
    }

    public void remove(InventoryItem item) {
        items.remove(item);
    }

    // Get the list of possible buildables
    public List<String> getBuildables() {
        return BuildableRegister.getAvailableBuildables(this);
    }

    // Check whether a player has the supplies to build a particular buildable. If so, build the item.
    // Currently since there are only two buildables we have a boolean to keep track of which buildable it is.
    public InventoryItem build(String name, EntityFactory factory) {
        return BuildableRegister.build(name, this, factory);
    }

    public <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item))
                return itemType.cast(item);
        return null;
    }

    public <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item))
                count++;
        return count;
    }

    public Entity getEntity(String itemUsedId) {
        for (InventoryItem item : items)
            if (item.getId().equals(itemUsedId))
                return item;
        return null;
    }

    public List<Entity> getEntities() {
        return items.stream().map(Entity.class::cast).collect(Collectors.toList());
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return items.stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public boolean hasWeapon() {
        return getFirst(Sword.class) != null || getFirst(Bow.class) != null;
    }

    public Useable getWeapon() {
        Useable weapon = getFirst(Sword.class);
        if (weapon == null)
            return getFirst(Bow.class);
        return weapon;
    }

    public void useWeapon(Game game) {
        getWeapon().use(game);
    }

    public <T extends InventoryItem> void removeFirst(Class<T> type) {
        T item = getFirst(type);
        if (item != null) {
            remove(item);
        }
    }

    public <T extends InventoryItem> void removeFirst(Class<T> type, int count) {
        int removed = 0;
        while (removed < count) {
            T item = getFirst(type);
            if (item == null)
                break;
            remove(item);
            removed++;
        }
    }
}
