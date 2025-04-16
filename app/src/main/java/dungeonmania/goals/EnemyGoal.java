package dungeonmania.goals;

import dungeonmania.Game;

public class EnemyGoal implements GoalComponent {
    private int target;

    public EnemyGoal(int target) {
        this.target = target;
    }

    @Override
    public boolean achieved(Game game) {
        if (game.getPlayer() == null)
            return false;
        System.out.println(game.countSpawner());
        return game.countDefeatedEnemy() >= target && game.countSpawner() <= 0;
    }

    @Override
    public String toString(Game game) {
        if (achieved(game))
            return "";
        return ":enemies";
    }
}
