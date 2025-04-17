package dungeonmania.entities.logics;

import java.util.List;
import dungeonmania.entities.Wire;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Switch;

public class AndLogicStrategy implements LogicalRuleStrategy {
    @Override
    public boolean isActiveLogically(GameMap gameMap, Position position) {
        int totalConductors = 0;
        int activeConductors = 0;
        List<Position> cardinallyAdjPos = position.getCardinallyAdjacentPositions();

        for (Position pos : cardinallyAdjPos) {
            List<Entity> entities = gameMap.getEntities(pos);
            for (Entity entity : entities) {
                // Check if entity is a conductor (Wire or Switch).
                if (entity instanceof Wire || entity instanceof Switch) {
                    totalConductors++;
                    if (LogicalRuleStrategy.isActivated(entity)) {
                        activeConductors++;
                    }
                }
            }
        }

        // The AND condition is met if there are at least 2 conductors
        // and all the conductors are activated.
        return (totalConductors >= 2) && (activeConductors == totalConductors);
    }
}
