package dungeonmania.entities.enemies;

import java.util.List;
import java.util.Random;

import dungeonmania.Game;
import dungeonmania.entities.PotionListener;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.strategies.InvisibleStrategy;
import dungeonmania.entities.enemies.strategies.InvincibleStrategy;
import dungeonmania.entities.enemies.strategies.Strategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy implements PotionListener {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;
    private Random randGen = new Random();
    private Strategy strategy;

    private String movementType = "random";

    public ZombieToast(Position position, double health, double attack) {
        super(position, health, attack);
    }

    @Override
    public void move(Game game) {
        Position nextPos = null;
        GameMap map = game.getMap();
        switch (movementType) {
        case "random":
            strategy = new InvisibleStrategy();
            nextPos = strategy.newPosit(map, this);
            map.moveTo(this, nextPos);
            break;
        case "runAway":
            strategy = new InvincibleStrategy();
            nextPos = strategy.newPosit(map, this);
            break;
        default:
            break;
        }
        game.getMap().moveTo(this, nextPos);

    }

    @Override
    public void notifyPotion(Potion potion) {
        if (potion instanceof InvincibilityPotion)
            movementType = "runAway";
    }

    @Override
    public void notifyNoPotion() {
        movementType = "random";
    }

}
