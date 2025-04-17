package dungeonmania.entities;

import java.util.List;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

/**
 * A conductor entity that can transmit activation status via wires.
 */
public class Wire extends Entity {
    private boolean activated;
    private int activatedTick = -1;
    private static final int ITEM_LAYER = 1;

    /**
     * Creates a Wire at the given position, placed on the item layer.
     *
     * @param pos the wire's position.
     */
    public Wire(Position pos) {
        super(pos.asLayer(ITEM_LAYER));
    }

    /**
     * Activates the wire.
     *
     * @param activationTick the current game tick.
     */
    public void activate(int tick) {
        activatedTick = tick;
        activated = true;
    }

    /**
     * Checks if the wire is activated.
     *
     * @return true if activated.
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Deactivates the wire.
     */
    public void deactivate() {
        activated = false;
        activatedTick = -1;
    }

    /**
     * Returns the tick when the wire was activated.
     *
     * @return the activation tick.
     */
    public int getActivatedTick() {
        return activatedTick;
    }

    /**
     * Wires are always walkable.
     *
     * @param map    the GameMap.
     * @param entity the moving entity.
     * @return true.
     */
    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    /**
    * Activates this wire and then recursively activates adjacent wires that arent active
    * in the current tick.
    *
    * @param gameMap the current game map
    * @param activationTick the game tick at which activation is occurring
    */
    public void activateWiresNearby(GameMap gameMap, int tick) {
        // Activate this wire with the given tick.
        activate(tick);

        // Retrieve the list of cardinally adjacent positions.
        List<Position> adjacentPositions = getPosition().getCardinallyAdjacentPositions();

        // Iterate over each adjacent position.
        for (Position position : adjacentPositions) {
            // Get all entities at the adjacent position.
            List<Entity> entities = gameMap.getEntities(position);

            // Check each entity at this position.
            for (Entity entity : entities) {
                // Process only if the entity is a Wire.
                if (entity instanceof Wire) {
                    Wire adjacentWire = (Wire) entity;
                    // If this adjacent wire has not been activated in the current tick,
                    // recursively activate its surrounding wires.
                    if (adjacentWire.getActivatedTick() != tick) {
                        adjacentWire.activateWiresNearby(gameMap, tick);
                    }
                }
            }
        }
    }

}
