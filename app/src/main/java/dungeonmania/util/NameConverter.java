package dungeonmania.util;

import java.util.Arrays;
import java.util.Iterator;

import dungeonmania.entities.Door;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Portal;
import dungeonmania.entities.LightBulb;
import dungeonmania.entities.SwitchDoor;

/*
 * Converts the name of a class to snake case. This snake case string is the
 * `type` returned in EntityResponses.
 *
 * e.g. "InvisibilityPotion" --> "invisibility_potion"
 *
 * Can manually modify the result as needed e.g. door_open
 */
public class NameConverter {
    public static String toSnakeCase(Entity entity) {
        String nameBasic = toSnakeCase(entity.getClass().getSimpleName());
        if (entity instanceof SwitchDoor) {
            String open = ((SwitchDoor) entity).isActivated() ? "_open" : "";
            return nameBasic + open;
        }

        if (entity instanceof Portal portal) {
            String color = "_" + portal.getColor().toLowerCase();
            return nameBasic + color;
        }
        if (entity instanceof Door door) {
            String open = door.isOpen() ? "_open" : "";
            return nameBasic + open;
        }
        if (entity instanceof LightBulb) {
            String open = ((LightBulb) entity).isActivated() ? "_on" : "_off";
            return nameBasic + open;
        }

        return nameBasic;
    }

    public static String toSnakeCase(String name) {
        String[] words = name.split("(?=[A-Z])");
        if (words.length == 1)
            return words[0].toLowerCase();

        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = Arrays.stream(words).iterator();
        builder.append(iter.next().toLowerCase());

        while (iter.hasNext())
            builder.append("_").append(iter.next().toLowerCase());

        return builder.toString();
    }

}
