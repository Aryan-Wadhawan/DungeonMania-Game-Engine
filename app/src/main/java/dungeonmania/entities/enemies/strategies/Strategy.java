package dungeonmania.entities.enemies.strategies;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;
import dungeonmania.entities.Player;

public interface Strategy {
    public Position newPosit(GameMap map, Enemy enemy);

    public Position newPosit(GameMap map, Enemy enemy, Player player);
}
