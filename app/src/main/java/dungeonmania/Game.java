package dungeonmania;

import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

import dungeonmania.battles.BattleFacade;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.goals.GoalComponent;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;

public class Game {
    private String id;
    private String name;
    private GoalComponent goals;
    private GameMap map;
    private Player player;
    private BattleFacade battleFacade;
    private EntityFactory entityFactory;
    private boolean isInTick = false;
    public static final int PLAYER_MOVEMENT = 0;
    public static final int POTION_BRIBE_UPDATE = 1;
    public static final int AI_MOVEMENT = 2;

    private ComparableCallback currentAction = null;

    private int tickCount = 0;
    // Actions that should happen in the current tick
    private PriorityQueue<ComparableCallback> tickActions = new PriorityQueue<>();
    // Actions that should happen in subsequent ticks
    private PriorityQueue<ComparableCallback> futureTickActions = new PriorityQueue<>();

    public Game(String dungeonName) {
        this.name = dungeonName;
        this.map = new GameMap();
        this.battleFacade = new BattleFacade();
    }

    public void init() {
        this.id = UUID.randomUUID().toString();
        map.init();
        this.tickCount = 0;
        player = map.getPlayer();
        register(() -> player.onTick(tickCount), POTION_BRIBE_UPDATE, "potionQueue");
    }

    public Game tick(Direction movementDirection) {
        registerOnce(() -> player.move(this.getMap(), movementDirection), PLAYER_MOVEMENT, "playerMoves");
        tick();
        return this;
    }

    public Game tick(String itemUsedId) throws InvalidActionException {
        Entity item = player.getEntity(itemUsedId);
        if (item == null)
            throw new InvalidActionException(String.format("Item with id %s doesn't exist", itemUsedId));
        if (!(item instanceof Bomb) && !(item instanceof Potion))
            throw new IllegalArgumentException(String.format("%s cannot be used", item.getClass()));

        registerOnce(() -> {
            if (item instanceof Bomb bomb)
                player.use(bomb, map);
            if (item instanceof Potion potion)
                player.use(potion, tickCount);
        }, PLAYER_MOVEMENT, "playerUsesItem");
        tick();
        return this;
    }

    public void battle(Player player, Enemy enemy) {
        battleFacade.battle(this, player, enemy);
        if (player.getBattleStatistics().getHealth() <= 0) {
            map.destroyEntity(player);
        }
        if (enemy.getBattleStatistics().getHealth() <= 0) {
            map.destroyEntity(enemy);
        }
    }

    public Game build(String buildable) throws InvalidActionException {
        List<String> buildables = player.getBuildables();
        if (!buildables.contains(buildable)) {
            throw new InvalidActionException(String.format("%s cannot be built", buildable));
        }
        registerOnce(() -> player.build(buildable, entityFactory), PLAYER_MOVEMENT, "playerBuildsItem");
        tick();
        return this;
    }

    public Game interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        Entity e = map.getEntity(entityId);
        if (e == null || !(e instanceof Interactable interactable))
            throw new IllegalArgumentException("Entity cannot be interacted");
        if (!interactable.isInteractable(player)) {
            throw new InvalidActionException("Entity cannot be interacted");
        }
        registerOnce(() -> interactable.interact(player, this), PLAYER_MOVEMENT, "playerInteracts");
        tick();
        return this;
    }

    // Register a new repeated action (e.g. enemy movement)
    // If currently in the middle of a tick, don't add it to the current tick but register it for future ticks.
    public void register(Runnable r, int priority, String id) {
        if (isInTick)
            futureTickActions.add(new ComparableCallback(r, priority, id));
        else
            tickActions.add(new ComparableCallback(r, priority, id));
    }

    // Register a new action that should only be done once (e.g. player movement)
    // If currently in the middle of a tick, don't add it to the current tick but register it for future ticks.
    public void registerOnce(Runnable r, int priority, String id) {
        if (isInTick)
            futureTickActions.add(new ComparableCallback(r, priority, id, true));
        else
            tickActions.add(new ComparableCallback(r, priority, id, true));
    }

    // Remove a repeated action from the list of actions
    public void unsubscribe(String id) {
        if (this.currentAction != null && id.equals(this.currentAction.getId())) {
            this.currentAction.invalidate();
        }

        for (ComparableCallback c : tickActions) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
        for (ComparableCallback c : futureTickActions) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
    }

    public int tick() {
        isInTick = true;
        while (!tickActions.isEmpty()) {
            currentAction = tickActions.poll();
            currentAction.run();
            if (currentAction.isValid()) {
                futureTickActions.add(currentAction);
            }
        }
        isInTick = false;
        tickActions = futureTickActions;
        futureTickActions = new PriorityQueue<>();
        tickCount++;
        return tickCount;
    }

    public int getTick() {
        return this.tickCount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GoalComponent getGoals() {
        return goals;
    }

    public void setGoals(GoalComponent goals) {
        this.goals = goals;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public void setEntityFactory(EntityFactory factory) {
        entityFactory = factory;
    }

    public int getCollectedTreasureCount() {
        return player.getCollectedTreasureCount();
    }

    public Player getPlayer() {
        return player;
    }

    public BattleFacade getBattleFacade() {
        return battleFacade;
    }
}
