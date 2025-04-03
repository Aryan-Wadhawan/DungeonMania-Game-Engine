package dungeonmania.goals;

import dungeonmania.Game;

public interface GoalComponent {
    boolean achieved(Game game);

    String toString(Game game);
}
