package dungeonmania.goals;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoalFactory {
    public static GoalComponent createGoal(JSONObject jsonGoal, JSONObject config) {
        String goalType = jsonGoal.getString("goal");

        return switch (goalType) {
        case "AND" -> {
            JSONArray subgoals = jsonGoal.getJSONArray("subgoals");
            yield new AndGoal(createGoal(subgoals.getJSONObject(0), config),
                    createGoal(subgoals.getJSONObject(1), config));
        }
        case "OR" -> {
            JSONArray subgoals = jsonGoal.getJSONArray("subgoals");
            yield new OrGoal(createGoal(subgoals.getJSONObject(0), config),
                    createGoal(subgoals.getJSONObject(1), config));
        }
        case "exit" -> new ExitGoal();
        case "boulders" -> new BouldersGoal();
        case "treasure" -> {
            int target = config.optInt("treasure_goal", 1);
            yield new TreasureGoal(target);
        }
        case "enemies" -> {
            int target = config.optInt("enemy_goal", 1);
            yield new EnemyGoal(target);
        }
        default -> throw new IllegalArgumentException("Unknown goal type: " + goalType);
        };
    }
}
