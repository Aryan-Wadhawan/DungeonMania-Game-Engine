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

        switch (type) {
        case "player":
        case "zombie_toast":
        case "zombie_toast_spawner":
        case "mercenary":
        case "wall":
        case "boulder":
        case "switch":
        case "exit":
        case "treasure":
        case "wood":
        case "arrow":
        case "bomb":
        case "invisibility_potion":
        case "invincibility_potion":
        case "portal":
        case "sword":
        case "spider":
        case "wire":
        case "light_bulb_off":
        case "light_bulb_on":
        case "switch_door":
        case "switch_door_open":
        case "door":
        case "key":
            return new GraphNode(factory.createEntity(jsonEntity));
        default:
            throw new IllegalArgumentException(
                    String.format("Failed to recognise '%s' entity in GraphNodeFactory", type));
        }
    }
}
