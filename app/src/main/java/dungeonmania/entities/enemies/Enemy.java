package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Enemy extends Entity implements Battleable {
    private BattleStatistics battleStatistics;

    public Enemy(Position position, double health, double attack) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
        battleStatistics = new BattleStatistics(health, attack, 0, BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_ENEMY_DAMAGE_REDUCER);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        map.battlePlayer(entity, this);
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
        map.removePlayerPotionListener(this);
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        return;
    }

    public abstract void move(Game game);

    public double getHealth() {
        return battleStatistics.getHealth();
    }

    public void updateHealth(double health) {
        battleStatistics.setHealth(health);
    }
}
