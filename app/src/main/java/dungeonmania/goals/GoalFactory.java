package dungeonmania.goals;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoalFactory {
    public static Goal createGoal(JSONObject jsonGoal, JSONObject config) {
        JSONArray subgoals;
        return switch (jsonGoal.getString("goal")) {
            case "AND" -> {
                subgoals = jsonGoal.getJSONArray("subgoals");
                yield new Goal("AND", createGoal(subgoals.getJSONObject(0), config),
                        createGoal(subgoals.getJSONObject(1), config));
            }
            case "OR" -> {
                subgoals = jsonGoal.getJSONArray("subgoals");
                yield new Goal("OR", createGoal(subgoals.getJSONObject(0), config),
                        createGoal(subgoals.getJSONObject(1), config));
            }
            case "exit" -> new Goal("exit");
            case "boulders" -> new Goal("boulders");
            case "treasure" -> {
                int treasureGoal = config.optInt("treasure_goal", 1);
                yield new Goal("treasure", treasureGoal);
            }
            default -> null;
        };
    }
}
