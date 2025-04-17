package dungeonmania.entities.logics;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Switch;
import dungeonmania.entities.Wire;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

/**
 * Provides methods to evaluate logical activation conditions for entities.
 */
public interface LogicalRuleStrategy {
    /**
     * Returns the activation tick of an entity. Only wires and switches have a valid tick.
     *
     * @param entity the entity to evaluate.
     * @return the activation tick for Wires and Switches; -1 otherwise.
     */
    static int isTickActivated(Entity entity) {
        if (entity instanceof Wire) {
            return ((Wire) entity).getActivatedTick();
        } else if (entity instanceof Switch) {
            return ((Switch) entity).getActivatedTick();
        }
        return -1;
    }

    /**
     * Checks if an entity is activated. Only wires and switches are considered conductors.
     *
     * @param entity the entity to evaluate.
     * @return true if the entity is a Wire or Switch and is activated; otherwise, false.
     */
    static boolean isActivated(Entity entity) {
        if (entity instanceof Wire) {
            return ((Wire) entity).isActivated();
        } else if (entity instanceof Switch) {
            return ((Switch) entity).isActivated();
        }
        return false;
    }

    /**
     * Determines whether the logical condition for activation is met at a given position.
     *
     * @param gameMap the current game map.
     * @param currentPosition the position from which to check adjacent entities.
     * @return true if the logical condition is satisfied; otherwise, false.
     */
    boolean isActiveLogically(GameMap gameMap, Position position);
}
