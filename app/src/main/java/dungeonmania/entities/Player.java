package dungeonmania.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.Shield;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Useable;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.BaseState;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends Entity implements Battleable {
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 5.0;
    private BattleStatistics battleStatistics;
    private Inventory inventory;
    private Queue<Potion> queue = new LinkedList<>();
    private Potion inEffective = null;
    private int nextTrigger = 0;
    private Set<PotionListener> potionListeners = new HashSet<>();

    private int collectedTreasureCount = 0;
    private int defeatedEnemy = 0;

    private PlayerState state;

    public Player(Position position, double health, double attack) {
        super(position);
        battleStatistics = new BattleStatistics(health, attack, 0, BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
        inventory = new Inventory();
        state = new BaseState(this);
    }

    public int getCollectedTreasureCount() {
        return collectedTreasureCount;
    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public Useable getWeapon() {
        return inventory.getWeapon();
    }

    public List<String> getBuildables() {
        return inventory.getBuildables();
    }

    public boolean build(String entity, EntityFactory factory) {
        InventoryItem item = inventory.build(entity, factory);
        return item != null && inventory.add(item);
    }

    public void move(GameMap map, Direction direction) {
        this.setFacing(direction);
        map.moveTo(this, Position.translateBy(this.getPosition(), direction));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy enemy) {
            if (enemy instanceof Mercenary mercenary && mercenary.isAllied())
                return;
            map.getGame().battle(this, enemy);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public Entity getEntity(String itemUsedId) {
        return inventory.getEntity(itemUsedId);
    }

    public boolean pickUp(Entity item) {
        // Handle treasure count
        if (item instanceof Treasure) {
            collectedTreasureCount++;
        }

        // Prevent multiple keys
        if (item instanceof Key) {
            boolean hasKey = inventory.getEntities().stream().anyMatch(i -> i instanceof Key);
            if (hasKey)
                return false;
        }

        return inventory.add((InventoryItem) item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Potion getEffectivePotion() {
        return inEffective;
    }

    public <T extends InventoryItem> void use(Class<T> itemType) {
        T item = inventory.getFirst(itemType);
        if (item != null)
            inventory.remove(item);
    }

    public void use(Bomb bomb, GameMap map) {
        inventory.remove(bomb);
        bomb.onPutDown(map, getPosition());
    }

    public void triggerNext(int currentTick) {
        if (queue.isEmpty()) {
            inEffective = null;
            state.transitionBase();
            potionListeners.forEach(PotionListener::notifyNoPotion);
            return;
        }
        inEffective = queue.remove();
        if (inEffective instanceof InvincibilityPotion) {
            state.transitionInvincible();
        } else {
            state.transitionInvisible();
        }
        potionListeners.forEach(e -> e.notifyPotion(inEffective));
        nextTrigger = currentTick + inEffective.getDuration();
    }

    public void changeState(PlayerState playerState) {
        state = playerState;
    }

    public void use(Potion potion, int tick) {
        inventory.remove(potion);
        queue.add(potion);
        if (inEffective == null) {
            triggerNext(tick);
        }
    }

    public void onTick(int tick) {
        if (inEffective == null || tick == nextTrigger) {
            triggerNext(tick);
        }
    }

    public void remove(InventoryItem item) {
        inventory.remove(item);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
        return inventory.count(itemType);
    }

    public BattleStatistics applyBuff(BattleStatistics origin) {
        return state.applyBuff(origin);
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        return;
    }

    @Override
    public void onDestroy(GameMap gameMap) {
        return;
    }

    public void registerPotionListener(PotionListener e) {
        potionListeners.add(e);

        if (getEffectivePotion() != null)
            e.notifyPotion(getEffectivePotion());
    }

    public void removePotionListener(PotionListener e) {
        potionListeners.remove(e);
    }

    public double getHealth() {
        return battleStatistics.getHealth();
    }

    public void updateHealth(double health) {
        battleStatistics.setHealth(health);
    }

    public List<InventoryItem> getUsableItems() {
        return inventory.getEntities(InventoryItem.class).stream()
                .filter(item -> item instanceof Bow || item instanceof Shield || item instanceof Sword)
                .collect(Collectors.toList());
    }

    public void useWeaponFromInventory(Game game) {
        inventory.useWeapon(game);
    }

    public void defeatEnemy() {
        defeatedEnemy++;
    }

    public int getDefeatedEnemy() {
        return defeatedEnemy;
    }
}
