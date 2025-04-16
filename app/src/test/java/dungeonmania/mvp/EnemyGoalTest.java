package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class EnemyGoalTest {
    @Test
    public void zombieToastDefeatGoalSuccess() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoal_ZombieToastSuccess", "c_enemyGoal");

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    public void zombieToastDefeatGoalFail() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoal_ZombieToastFail", "c_enemyGoal");

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
    }

    @Test
    public void twoEnemiesSuccess() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoal_twoEnemiesSuccess", "c_enemyGoal_twoEnemies");

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    public void destroySpawnerSuccess() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoal_SpawnerSuccess", "c_enemyGoal");

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.interact(TestUtils.getEntityAtPos(res, "zombie_toast_spawner", new Position(1, 5)).get().getId());
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals(0, TestUtils.countEntityOfType(TestUtils.getEntities(res), "zombie_toast_spawner"));

        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    public void destroySpawnerFail() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoal_SpawnerFail", "c_enemyGoal");

        String spwnId = TestUtils.getEntitiesStream(res, "zombie_toast_spawner").findFirst().get().getId();

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertThrows(dungeonmania.exceptions.InvalidActionException.class, () -> dmc.interact(spwnId));
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertFalse(TestUtils.getGoals(res).contains(":exit"));
    }
}
