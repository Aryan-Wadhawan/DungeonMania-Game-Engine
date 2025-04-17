package dungeonmania.entities;

import dungeonmania.entities.logics.LogicalRuleStrategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class LightBulb extends Entity {
    private LogicalRuleStrategy logicalRuleStrategy;
    private boolean activated;

    public LightBulb(Position position, LogicalRuleStrategy logicalRuleStrategy) {
        super(position);
        this.logicalRuleStrategy = logicalRuleStrategy;
        activated = false;
    }

    public LogicalRuleStrategy getLogicalRule() {
        return logicalRuleStrategy;
    }

    public boolean isActivated() {
        return activated;
    }

    public void updateLightState(GameMap map) {
        activated = logicalRuleStrategy.isActiveLogically(map, getPosition());
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

}
