package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.collectables.Useable;

public class Shield extends Buildable implements Useable {
    private int durability;
    private double defence;

    public Shield(int durability, double defence) {
        super(null);
        this.durability = durability;
        this.defence = defence;
    }

    @Override
    public void use(Game game) {
        durability--;
        if (durability <= 0) {
            game.removeItemFromPlayer(this);
        }
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(0, 0, defence, 1, 1));
    }

    @Override
    public int getDurability() {
        return durability;
    }

}
