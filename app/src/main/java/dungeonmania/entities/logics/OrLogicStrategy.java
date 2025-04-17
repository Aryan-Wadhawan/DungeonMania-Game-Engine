package dungeonmania.entities.logics;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

/**
 * OR logic: activated if any adjacent conductor is on.
 */
public class OrLogicStrategy implements LogicalRuleStrategy {
    @Override
    public boolean isActiveLogically(GameMap gameMap, Position position) {
        for (Position pos : position.getCardinallyAdjacentPositions()) {
            for (Entity entity : gameMap.getEntities(pos)) {
                if (LogicalRuleStrategy.isActivated(entity)) {
                    return true;
                }
            }
        }
        return false;
    }
}
