## Week 5

- Spent time familiarising myself with the DungeonMania codebase and reviewing the MVP specification.
- Began exploring class relationships and game flow but did not make any functional changes.

## Week 6

- Investigated the inheritance structure of collectable entities and battle logic.
- No code written yet, but I began planning for the upcoming SRP refactor.

## Week 7

- Completed Task 1 Parts **c**, **d**, and **e**:
  - **Part c (Inheritance Design)**: Refactored the collectables hierarchy to follow the **Single Responsibility Principle**. I introduced `PassiveItem` and `BattleItem` to separate battle-relevant and passive items.
  - **Part d (Feature Envy Code Smell)**: Moved `activateBombs()` logic from `Switch.java` into the `Bomb` class to reduce coupling and encapsulate behavior.
  - **Part e (Open-Closed Principle for Goals)**: Replaced hardcoded goal logic with the **Composite Design Pattern**. Created `GoalComponent`, `AndGoal`, `OrGoal`, and various leaf goal classes to enable extensible goal combinations.

## Week 8

- Implemented **Task 2 Part f (Logical Entities)**:
  - Designed and implemented logic handling for `SwitchDoor` and `LightBulb` using different logic strategies (AND, OR, XOR, CO-AND).
  - Created `LogicalRuleStrategy` interface and strategy classes for each logic type.
  - Updated `Switch` and `Wire` to propagate activation signals correctly using cardinal adjacency.
  - Added `updateWires()` and `updateLogicals()` phases to the `Game` tick loop.
  - Wrote tests to validate combinations of logic behavior.

## Week 9

- Continued refinement of logic entities and cleaned up the codebase.
- Fixed important MVP bugs:
  - **Key pickup bug**: Player was able to collect multiple keys — I modified `pickUp()` to enforce one-key inventory logic and changed the test(4.4).
  - **Zombie teleportation bug**: Removed `ZombieToast` from portal logic — now portals only affect players and mercenaries and also changed the test.
- Updated existing and added new test cases to ensure compliance with the MVP.
- Documented all bug fixes in Task 3 (Investigation Task) and submitted merge requests.
