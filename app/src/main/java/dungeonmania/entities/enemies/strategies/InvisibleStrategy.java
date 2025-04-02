package dungeonmania.entities.enemies.strategies;

import dungeonmania.entities.Player;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class InvisibleStrategy implements Strategy {
    public Position newPosit(GameMap map, Enemy enemy) {
        List<Position> pos = enemy.getPosition().getCardinallyAdjacentPositions();
        Position nextPos;
        Random randGen = new Random();
        pos = pos.stream().filter(p -> map.canMoveTo(enemy, p)).collect(Collectors.toList());
        if (pos.size() == 0) {
            nextPos = enemy.getPosition();
        } else {
            nextPos = pos.get(randGen.nextInt(pos.size()));
        }
        return nextPos;
    }

    public Position newPosit(GameMap map, Enemy enemy, Player player) {
        return null;
    }
}
