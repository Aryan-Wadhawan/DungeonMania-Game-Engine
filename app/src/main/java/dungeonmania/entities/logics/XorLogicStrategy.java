package dungeonmania.entities.logics;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

/**
 * XOR logic: activated if exactly one adjacent conductor is on.
 */
public class XorLogicStrategy implements LogicalRuleStrategy {
    @Override
    public boolean isActiveLogically(GameMap gameMap, Position position) {
        int activatedCount = 0;
        for (Position pos : position.getCardinallyAdjacentPositions()) {
            for (Entity entity : gameMap.getEntities(pos)) {
                if (LogicalRuleStrategy.isActivated(entity)) {
                    activatedCount++;
                }
            }
        }
        return activatedCount == 1;
    }
}
