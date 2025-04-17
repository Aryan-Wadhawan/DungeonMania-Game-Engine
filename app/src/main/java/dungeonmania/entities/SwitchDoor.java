package dungeonmania.entities;

import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.logics.LogicalRuleStrategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

/**
 * A door that opens based on a logical condition.
 */
public class SwitchDoor extends Entity {
    private final LogicalRuleStrategy logicalRuleStrategy;
    private boolean activated;

    /**
     * Constructs a SwitchDoor with a specified position and logic.
     *
     * @param position            the door's position.
     * @param logicalRuleStrategy the logic that controls door activation.
     */
    public SwitchDoor(Position position, LogicalRuleStrategy logicalRuleStrategy) {
        super(position);
        this.logicalRuleStrategy = logicalRuleStrategy;
        this.activated = false;
    }

    /**
     * Updates the door activation by evaluating its logic.
     *
     * @param map the GameMap.
     */
    public void updateActivation(GameMap map) {
        activated = logicalRuleStrategy.isActiveLogically(map, getPosition());
    }

    /**
     * Indicates if the door is activated.
     *
     * @return true if activated; false otherwise.
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Checks if an entity can move onto this door.
     *
     * @param map    the GameMap.
     * @param entity the moving entity.
     * @return true if the door is activated or the entity is a Spider.
     */
    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return (entity instanceof Spider) || activated;
    }

    /**
     * Returns the door's logical rule.
     *
     * @return the LogicalRuleStrategy.
     */
    public LogicalRuleStrategy getRule() {
        return logicalRuleStrategy;
    }

}
