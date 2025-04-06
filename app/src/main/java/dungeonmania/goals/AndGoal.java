package dungeonmania.goals;

import dungeonmania.Game;

public class AndGoal implements GoalComponent {
    private final GoalComponent goal1;
    private final GoalComponent goal2;

    public AndGoal(GoalComponent goal1, GoalComponent goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    @Override
    public boolean achieved(Game game) {
        return goal1.achieved(game) && goal2.achieved(game);
    }

    @Override
    public String toString(Game game) {
        if (achieved(game))
            return "";
        return "(" + goal1.toString(game) + " AND " + goal2.toString(game) + ")";
    }
}
