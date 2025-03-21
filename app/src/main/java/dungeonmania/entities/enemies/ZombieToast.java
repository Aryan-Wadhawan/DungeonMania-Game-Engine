package dungeonmania.entities.enemies;

import java.util.List;
import java.util.Random;

import dungeonmania.Game;
import dungeonmania.entities.PotionListener;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy implements PotionListener {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;
    private Random randGen = new Random();

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
            List<Position> pos = getPosition().getCardinallyAdjacentPositions();
            pos = pos.stream().filter(p -> map.canMoveTo(this, p)).toList();
            if (pos.size() == 0) {
                nextPos = getPosition();
            } else {
                nextPos = pos.get(randGen.nextInt(pos.size()));
            }
            break;
        case "runAway":
            // Check whether the zombie should flee left or right & up or down
            Position plrDiff = Position.calculatePositionBetween(map.getPlayer().getPosition(), getPosition());
            Position moveX = (plrDiff.getX() >= 0) ? Position.translateBy(getPosition(), Direction.RIGHT)
                    : Position.translateBy(getPosition(), Direction.LEFT);
            Position moveY = (plrDiff.getY() >= 0) ? Position.translateBy(getPosition(), Direction.DOWN)
                    : Position.translateBy(getPosition(), Direction.UP);
            Position offset = getPosition();
            // If on the same Y axis and can flee left or right, do so.
            if (plrDiff.getY() == 0 && map.canMoveTo(this, moveX))
                offset = moveX;
            // Or if on the same X axis and can flee up or down, do so.
            else if (plrDiff.getX() == 0 && map.canMoveTo(this, moveY))
                offset = moveY;
            // Prioritise Y movement if further away on the X axis
            else if (Math.abs(plrDiff.getX()) >= Math.abs(plrDiff.getY())) {
                if (map.canMoveTo(this, moveY))
                    offset = moveY;
                else if (map.canMoveTo(this, moveX))
                    offset = moveX;
                else
                    offset = getPosition();
                // Prioritise X movement if further away on the Y axis
            } else {
                if (map.canMoveTo(this, moveX))
                    offset = moveX;
                else if (map.canMoveTo(this, moveY))
                    offset = moveY;
                else
                    offset = getPosition();
            }
            nextPos = offset;
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
