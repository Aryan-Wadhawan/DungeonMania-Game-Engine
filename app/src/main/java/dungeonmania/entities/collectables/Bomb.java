package dungeonmania.entities.collectables;

import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.Switch;
import dungeonmania.entities.inventory.PassiveItem;
import dungeonmania.map.GameMap;

public class Bomb extends PassiveItem {
    public enum State {
        SPAWNED, PLACED
    }

    public static final int DEFAULT_RADIUS = 1;
    private State state;
    private int radius;

    private List<Switch> subs = new ArrayList<>();

    public Bomb(Position position, int radius) {
        super(position);
        state = State.SPAWNED;
        this.radius = radius;
    }

    public void subscribe(Switch s) {
        this.subs.add(s);
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (state != State.SPAWNED)
            return;
        if (entity instanceof Player player) {
            if (!player.pickUp(this))
                return;
            subs.stream().forEach(s -> s.unsubscribe(this));
            map.destroyEntity(this);
        }
    }

    public int getRadius() {
        return radius;
    }

    public void onPutDown(GameMap map, Position p) {
        translate(Position.calculatePositionBetween(getPosition(), p));
        map.addEntity(this);
        this.state = State.PLACED;
        List<Position> adjPosList = getPosition().getCardinallyAdjacentPositions();
        adjPosList.stream().forEach(node -> {
            List<Entity> entities = map.getEntities(node).stream().filter(Switch.class::isInstance).toList();
            entities.stream().map(Switch.class::cast).forEach(s -> s.subscribe(this, map));
            entities.stream().map(Switch.class::cast).forEach(this::subscribe);
        });
    }

    public State getState() {
        return state;
    }
}
