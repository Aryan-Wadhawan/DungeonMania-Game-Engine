package dungeonmania.entities.enemies.strategies;

import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class HostileStrategy implements Strategy {
    public Position newPosit(GameMap map, Enemy enemy) {
        return null;
    }

    public Position newPosit(GameMap map, Enemy enemy, Player player) {
        return map.dijkstraPathFind(enemy.getPosition(), player.getPosition(), enemy);
    }
}
