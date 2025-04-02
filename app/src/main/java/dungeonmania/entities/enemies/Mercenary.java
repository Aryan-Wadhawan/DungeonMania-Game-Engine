package dungeonmania.entities.enemies;

import java.util.List;
import java.util.Random;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.PotionListener;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.strategies.AlliedStrategy;
import dungeonmania.entities.enemies.strategies.HostileStrategy;
import dungeonmania.entities.enemies.strategies.InvincibleStrategy;
import dungeonmania.entities.enemies.strategies.InvisibleStrategy;
import dungeonmania.entities.enemies.strategies.Strategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable, PotionListener {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;

    private double allyAttack;
    private double allyDefence;
    private boolean allied = false;
    private boolean wasAdjacentToPlayer = false;
    private Strategy strategy;

    private String movementType = "hostile";

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius,
            double allyAttack, double allyDefence) {
        super(position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        this.allyAttack = allyAttack;
        this.allyDefence = allyDefence;
    }

    public boolean isAllied() {
        return allied;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (allied)
            return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     */
    private boolean canBeBribed(Player player) {
        return bribeRadius >= 0 && player.countEntityOfType(Treasure.class) >= bribeAmount;
    }

    /**
     * bribe the merc
     */
    private void bribe(Player player) {
        for (int i = 0; i < bribeAmount; i++) {
            player.use(Treasure.class);
        }

    }

    public boolean isWasAdjacentToPlayer() {
        return wasAdjacentToPlayer;
    }

    public void setWasAdjacentToPlayer(boolean wasAdjacentToPlayer) {
        this.wasAdjacentToPlayer = wasAdjacentToPlayer;
    }

    @Override
    public void interact(Player player, Game game) {
        allied = true;
        movementType = "allied";
        bribe(player);
    }

    @Override
    public void move(Game game) {
        Position nextPos = null;
        GameMap map = game.getMap();
        Player player = game.getPlayer();
        switch (movementType) {
        case "allied":
            strategy = new AlliedStrategy();
            nextPos = strategy.newPosit(map, this, player);
            break;
        case "invisible":
            // Move random
            strategy = new InvisibleStrategy();
            nextPos = strategy.newPosit(map, this);
            map.moveTo(this, nextPos);
            break;
        case "invincible":
            strategy = new InvincibleStrategy();
            nextPos = strategy.newPosit(map, this);
            break;
        case "hostile":
            strategy = new HostileStrategy();
            nextPos = strategy.newPosit(map, this, player);
            break;
        default:
            break;
        }
        map.moveTo(this, nextPos);
    }

    @Override
    public boolean isInteractable(Player player) {
        return !allied && canBeBribed(player);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        if (!allied)
            return super.getBattleStatistics();
        return new BattleStatistics(0, allyAttack, allyDefence, 1, 1);
    }

    @Override
    public void notifyPotion(Potion potion) {
        if (allied)
            return;

        if (potion instanceof InvisibilityPotion)
            movementType = "invisible";
        if (potion instanceof InvincibilityPotion)
            movementType = "invincible";
    }

    @Override
    public void notifyNoPotion() {
        if (allied)
            return;

        movementType = "hostile";
    }
}
