package dungeonmania.mvp.task2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class LogicalEntitiesTests {

    // Helper: Create a new controller.
    private DungeonManiaController createController() {
        return new DungeonManiaController();
    }

    // Helper: Start a new game with the given dungeon name.
    private DungeonResponse startGame(DungeonManiaController dmc, String dungeonName) {
        return dmc.newGame(dungeonName, "simple");
    }

    @Test
    @Tag("1")
    @DisplayName("Test that XOR and OR switch doors open and AND switch door remains closed")
    public void switchDoorLogicTest() {
        // Arrange
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalEntitiesTest_SwitchDoor", "simple");

        // Push
        res = dmc.tick(Direction.RIGHT);

        // XOR
        EntityResponse before = TestUtils.getPlayer(res).get();
        EntityResponse expectedXor = new EntityResponse(before.getId(), before.getType(), new Position(5, 4), false);
        DungeonResponse afterXor = dmc.tick(Direction.DOWN);
        EntityResponse actualXor = TestUtils.getPlayer(afterXor).get();
        assertTrue(TestUtils.entityResponsesEqual(expectedXor, actualXor));

        // OR
        res = afterXor;
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        EntityResponse actualOr = TestUtils.getPlayer(res).get();
        EntityResponse expectedOr = new EntityResponse(actualOr.getId(), actualOr.getType(), new Position(7, 4), false);
        assertTrue(TestUtils.entityResponsesEqual(expectedOr, actualOr));

        // AND
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        EntityResponse actualAnd = TestUtils.getPlayer(res).get();
        EntityResponse expectedAnd = new EntityResponse(actualAnd.getId(), actualAnd.getType(), new Position(6, 4),
                false);
        assertTrue(TestUtils.entityResponsesEqual(expectedAnd, actualAnd));
    }

    @Test
    @Tag("2")
    @DisplayName("CoAnd logic toggles light bulbs correctly with a switch–wire network")
    public void testCoAndLightBulbToggling() {
        // Arrange
        DungeonManiaController controller = createController();
        DungeonResponse response = startGame(controller, "d_logicalEntitiesTest_CoAndTest");

        // Initially, both light bulbs should be off
        assertEquals(2, TestUtils.countType(response, "light_bulb_off"));

        // Act & Assert Step 1: Activate the first switch -> one bulb should turn on
        response = controller.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.countType(response, "light_bulb_on"));

        // Retrace back to start
        response = controller.tick(Direction.LEFT);

        // Act & Assert Step 2: Activate the second switch -> both bulbs should turn on
        response = controller.tick(Direction.UP);
        response = controller.tick(Direction.UP);
        response = controller.tick(Direction.RIGHT);

        // Act & Assert Step 3: Deactivate the first switch -> one bulb should turn off
        response = controller.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.countType(response, "light_bulb_on"));

        // Retrace and deactivate the second switch -> both bulbs should be off again
        response = controller.tick(Direction.LEFT);
        response = controller.tick(Direction.DOWN);
        response = controller.tick(Direction.DOWN);
        response = controller.tick(Direction.DOWN);
        response = controller.tick(Direction.UP);
        response = controller.tick(Direction.UP);
        response = controller.tick(Direction.DOWN);
        response = controller.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.countType(response, "light_bulb_off"));
    }

    @Test
    @Tag("3")
    @DisplayName("Validate composite logic sequences for light bulbs")
    public void verifyCompositeLogicBulbs() {
        // Initialize game and controller
        DungeonManiaController controller = createController();
        DungeonResponse response = startGame(controller, "d_logicalEntitiesTest_LogicRulesTest");

        // Initial state: all bulbs off
        int onCount = (int) TestUtils.countType(response, "light_bulb_on");
        int offCount = (int) TestUtils.countType(response, "light_bulb_off");
        assertEquals(0, onCount);
        assertEquals(8, offCount);

        // Move into the OR/XOR region
        response = controller.tick(Direction.UP);
        response = controller.tick(Direction.RIGHT);
        onCount = (int) TestUtils.countType(response, "light_bulb_on");
        offCount = (int) TestUtils.countType(response, "light_bulb_off");
        assertEquals(5, onCount);
        assertEquals(3, offCount);

        // Navigate to the AND/co-AND section
        response = controller.tick(Direction.DOWN);
        response = controller.tick(Direction.LEFT);
        response = controller.tick(Direction.DOWN);
        response = controller.tick(Direction.RIGHT);
        onCount = (int) TestUtils.countType(response, "light_bulb_on");
        offCount = (int) TestUtils.countType(response, "light_bulb_off");
        assertEquals(6, onCount);
        assertEquals(2, offCount);

        // Return and reset all switches
        response = controller.tick(Direction.UP);
        response = controller.tick(Direction.RIGHT);
        response = controller.tick(Direction.UP);
        onCount = (int) TestUtils.countType(response, "light_bulb_on");
        offCount = (int) TestUtils.countType(response, "light_bulb_off");
        assertEquals(0, onCount);
        assertEquals(8, offCount);
    }

    @Test
    @Tag("4")
    @DisplayName("OR door blocks movement when not energized")
    public void verifyOrDoorBlocksWhenInactive() {
        // Initialize
        DungeonManiaController controller = createController();
        DungeonResponse response = startGame(controller, "d_logicalEntitiesTest_OR_door_block");

        // Start
        assertEquals(new Position(3, 3), TestUtils.getEntities(response, "player").get(0).getPosition());
        // Go around
        response = controller.tick(Direction.RIGHT);
        response = controller.tick(Direction.RIGHT);

        Position beforeBlock = TestUtils.getEntities(response, "player").get(0).getPosition();

        // Block
        response = controller.tick(Direction.RIGHT);
        assertEquals(beforeBlock, TestUtils.getEntities(response, "player").get(0).getPosition());

        // Check bulb still off
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_off", new Position(6, 5)));
    }

    @Test
    @Tag("5")
    @DisplayName("OR Test: player can walk through an activated switch door, switch turns light on")
    public void testOrDoorActivationWalkThrough() {
        // setup
        DungeonManiaController controller = createController();
        DungeonResponse response = startGame(controller, "d_logicalEntitiesTest_OR_door_block");

        // verify
        assertEquals(new Position(3, 3), TestUtils.getEntities(response, "player").get(0).getPosition());
        assertTrue(TestUtils.entityAtPosition(response, "switch_door", new Position(7, 3)));
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_off", new Position(6, 5)));

        // activate
        response = controller.tick(Direction.RIGHT);
        assertTrue(TestUtils.entityAtPosition(response, "switch_door_open", new Position(7, 3)));
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_on", new Position(6, 5)));

        // advance
        response = controller.tick(Direction.RIGHT);

        // confirm
        assertTrue(TestUtils.entityAtPosition(response, "switch_door", new Position(7, 3)));
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_off", new Position(6, 5)));
    }

    @Test
    @Tag("6")
    @DisplayName("AND door opens only when both inputs are active")
    public void testAndDoorLogic() {
        // init
        DungeonManiaController ctrl = createController();
        DungeonResponse resp = startGame(ctrl, "d_logicalEntitiesTest_AND_switch_door_both");

        // off
        assertTrue(TestUtils.entityAtPosition(resp, "switch_door", new Position(6, 2)));

        // one
        resp = ctrl.tick(Direction.RIGHT);
        assertTrue(TestUtils.entityAtPosition(resp, "switch_door", new Position(6, 2)));

        // two
        resp = ctrl.tick(Direction.LEFT);
        resp = ctrl.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(resp, "switch_door_open", new Position(6, 2)));

        // pass
        resp = ctrl.tick(Direction.RIGHT);
        resp = ctrl.tick(Direction.RIGHT);
        resp = ctrl.tick(Direction.RIGHT);
        resp = ctrl.tick(Direction.RIGHT);
        assertFalse(TestUtils.entityAtPosition(resp, "player", new Position(6, 2)));

        // back
        resp = ctrl.tick(Direction.LEFT);
        resp = ctrl.tick(Direction.LEFT);
        assertTrue(TestUtils.entityAtPosition(resp, "switch_door", new Position(6, 2)));
    }

    @Test
    @Tag("7")
    @DisplayName("AND light bulb activates only with two active input wires")
    public void testAndLightBulbActivation() {
        // setup
        DungeonManiaController controller = createController();
        DungeonResponse response = startGame(controller, "d_logicalEntitiesTest_AND_light_bulb");
        Position bulbPos = new Position(7, 3);

        // verify off default
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_off", bulbPos));

        // first
        response = controller.tick(Direction.LEFT);
        response = controller.tick(Direction.RIGHT);
        response = controller.tick(Direction.RIGHT);
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_off", bulbPos));

        // second

        response = controller.tick(Direction.LEFT);
        response = controller.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_on", bulbPos));

        // reset
        response = controller.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_off", bulbPos));
    }

    @Test
    @Tag("8")
    @DisplayName("AND gate: Light bulb powers only with three inputs")
    public void lightBulbRequiresThreeInputs() {
        // init
        DungeonManiaController ctrl = createController();
        DungeonResponse rsp = startGame(ctrl, "d_logicalEntitiesTest_AND_light_bulbs");
        Position bulb = new Position(7, 3);

        // off
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", bulb));

        // one
        rsp = ctrl.tick(Direction.LEFT);
        rsp = ctrl.tick(Direction.RIGHT);
        rsp = ctrl.tick(Direction.RIGHT);
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", bulb));

        // two
        rsp = ctrl.tick(Direction.LEFT);
        rsp = ctrl.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", bulb));

        // three
        rsp = ctrl.tick(Direction.UP);
        rsp = ctrl.tick(Direction.UP);
        rsp = ctrl.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_on", bulb));

        // reset
        rsp = ctrl.tick(Direction.UP);
        rsp = ctrl.tick(Direction.UP);
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", bulb));
    }

    @Test
    @Tag("9")
    @DisplayName("XOR gate: light toggles only with a single active input")
    public void xorLightBulbBehavior() {
        // setup
        DungeonManiaController controller = createController();
        DungeonResponse response = startGame(controller, "d_logicalEntitiesTest_XOR_light_bulb");
        Position bulbPos = new Position(7, 3);

        // off
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_off", bulbPos));

        // one
        response = controller.tick(Direction.RIGHT);
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_on", bulbPos));

        // two
        response = controller.tick(Direction.LEFT);
        response = controller.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_off", bulbPos));

        // three
        response = controller.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_on", bulbPos));

        // reset
        response = controller.tick(Direction.UP);
        response = controller.tick(Direction.UP);
        response = controller.tick(Direction.RIGHT);
        response = controller.tick(Direction.RIGHT);
        assertTrue(TestUtils.entityAtPosition(response, "light_bulb_off", bulbPos));
    }

    @Test
    @Tag("10")
    @DisplayName("CO_AND gate: extra activations don't toggle bulb off")
    public void testCoAndPersistence() {
        // setup
        DungeonManiaController ctrl = createController();
        DungeonResponse rsp = startGame(ctrl, "d_logicalEntitiesTest_CO_AND_lightbulb");
        Position bulbPos = new Position(6, 2);

        // initial
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", bulbPos));

        // activate
        Direction[] seq1 = {
                Direction.UP, Direction.UP, Direction.LEFT, Direction.LEFT, Direction.LEFT, Direction.RIGHT,
                Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT,
                Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.DOWN, Direction.LEFT
        };
        for (Direction d : seq1) {
            rsp = ctrl.tick(d);
        }
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_on", bulbPos));

        // ectra activation
        Direction[] seq2 = {
                Direction.UP, Direction.LEFT, Direction.LEFT, Direction.DOWN, Direction.DOWN
        };
        for (Direction d : seq2) {
            rsp = ctrl.tick(d);
        }
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_on", bulbPos));
    }

    @Test
    @Tag("11")
    @DisplayName("Mixed Logic Entities Test")
    public void logicMixed() {
        DungeonManiaController ctrl = createController();
        DungeonResponse rsp = startGame(ctrl, "d_logicalEntitiesTest_all");

        Position a = new Position(4, -2);
        Position b = new Position(4, 5);
        Position c = new Position(0, 2);
        Position d = new Position(-1, 5);

        // init
        assertEquals(new Position(2, 0), TestUtils.getPlayer(rsp).get().getPosition());
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", a));
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", b));
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", c));
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", d));

        // or
        rsp = ctrl.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_on", a));
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_on", b));

        // and
        rsp = ctrl.tick(Direction.RIGHT);
        rsp = ctrl.tick(Direction.DOWN);
        rsp = ctrl.tick(Direction.DOWN);
        rsp = ctrl.tick(Direction.LEFT);
        rsp = ctrl.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_on", a));
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", b));
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_on", c));
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_on", d));

        // reset
        rsp = ctrl.tick(Direction.UP);
        rsp = ctrl.tick(Direction.UP);
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", a));
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_on", b));
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_off", c));
        assertTrue(TestUtils.entityAtPosition(rsp, "light_bulb_on", d));
    }

}
