package dungeonmania.entities.logics;

import java.util.ArrayList;
import java.util.HashSet;
import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

import java.util.List;

/**
 * Logical strategy for CO_AND condition.
 * The entity is activated if at least two adjacent conductors are activated
 * and at least two of them were activated on the same game tick
 */
public class CoAndLogicStrategy implements LogicalRuleStrategy {
    @Override
    public boolean isActiveLogically(GameMap gameMap, Position position) {
        int activatedCount = 0;
        List<Integer> activationTicks = new ArrayList<>();

        // Iterate over cardinally adjacent positions
        for (Position pos : position.getCardinallyAdjacentPositions()) {
            for (Entity entity : gameMap.getEntities(pos)) {
                if (LogicalRuleStrategy.isActivated(entity)) {
                    activatedCount++;
                    int tick = LogicalRuleStrategy.isTickActivated(entity);
                    if (tick != -1) {
                        activationTicks.add(tick);
                    }
                }
            }
        }

        boolean activatedSimultaneously = activationTicks.size() > new HashSet<>(activationTicks).size();
        return activatedCount >= 2 && activatedSimultaneously;
    }
}
