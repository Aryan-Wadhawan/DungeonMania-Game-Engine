package dungeonmania.map;

import org.json.JSONObject;
import dungeonmania.entities.EntityFactory;

/**
 * Factory that creates a GraphNode from a JSON Object
 */
public class GraphNodeFactory {
    public static GraphNode createEntity(JSONObject jsonEntity, EntityFactory factory) {
        return constructEntity(jsonEntity, factory);
    }

    private static GraphNode constructEntity(JSONObject jsonEntity, EntityFactory factory) {
        String type = jsonEntity.getString("type");
        return switch (type) {
        case "player", "zombie_toast", "zombie_toast_spawner", "mercenary", "wall", "boulder", "switch", "exit", "treasure", "wood", "arrow", "bomb", "invisibility_potion", "invincibility_potion", "portal", "sword", "spider", "wire", "light_bulb_off", "light_bulb_on", "switch_door", "switch_door_open", "door", "key" -> new GraphNode(
                factory.createEntity(jsonEntity));
        default -> throw new IllegalArgumentException(
                String.format("Failed to recognise '%s' entity in GraphNodeFactory", type));
        };
    }
}
