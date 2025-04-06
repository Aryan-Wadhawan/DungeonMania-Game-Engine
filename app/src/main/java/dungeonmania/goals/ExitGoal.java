package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Exit;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

import java.util.List;

public class ExitGoal implements GoalComponent {
    @Override
    public boolean achieved(Game game) {
        Player player = game.getPlayer();
        if (player == null)
            return false;

        Position pos = player.getPosition();
        List<Exit> exits = game.getMap().getEntities(Exit.class);
        return exits.stream().map(Entity::getPosition).anyMatch(pos::equals);
    }

    @Override
    public String toString(Game game) {
        return achieved(game) ? "" : ":exit";
    }
}
