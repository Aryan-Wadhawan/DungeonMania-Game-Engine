package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;

public abstract class PlayerState {
    private Player player;
    private String playerState = "BaseState";

    PlayerState(Player player, String playerState) {
        this.player = player;
        this.playerState = playerState;
    }

    public String getPlayerState() {
        return playerState;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract void transitionInvisible();

    public abstract void transitionInvincible();

    public abstract void transitionBase();

    public abstract BattleStatistics applyBuff(BattleStatistics origin);
}
