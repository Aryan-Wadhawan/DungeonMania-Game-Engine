package dungeonmania.entities.enemies.strategies;

import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class AlliedStrategy implements Strategy {
    public Position newPosit(GameMap map, Enemy enemy, Player player) {
        Position nextPos = null;
        boolean isAdjacentToPlayer = Position.isAdjacent(player.getPosition(), enemy.getPosition());
        if (enemy instanceof Mercenary) {
            Mercenary mercenary = (Mercenary) enemy; // Cast to Mercenary
            if (mercenary.isWasAdjacentToPlayer() && !isAdjacentToPlayer) {
                nextPos = player.getPreviousDistinctPosition();
            } else {
                // If currently still adjacent, wait in place. Else pursue the player.
                nextPos = isAdjacentToPlayer ? enemy.getPosition()
                        : map.dijkstraPathFind(enemy.getPosition(), player.getPosition(), enemy);
                mercenary.setWasAdjacentToPlayer(Position.isAdjacent(player.getPosition(), nextPos));
            }
        }
        return nextPos;
    }

    public Position newPosit(GameMap map, Enemy enemy) {
        return null;
    }
}
