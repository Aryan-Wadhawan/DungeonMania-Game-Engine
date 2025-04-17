# Assignment II Pair Blog Template

## Task 0) Core Investigation 🔎

> What is the difference in purpose between the `DungeonManiaController` and the `Game` classes? (1 mark)

[Answer]

> In the game, player and enemy actions to be performed later are stored as "comparable callbacks". Callbacks are pieces of code that can be run at a later time. However, for what purpose are these callbacks made _comparable_? (1 mark)

[Answer]

> Why is it so important that the dungeon files used for testing follow the technical specification? (4.1.1 in the MVP spec) (1 mark)

[Answer]

> The Game class includes a method with the signature public Game tick(Direction movementDirection). Provide a detailed explanation of what this method does, including an overview of all the other methods it calls. Additionally, explain the purpose of the callback system it interacts with, and clarify the intentions behind the tickActions, futureTickActions, and currentAction fields. (1 mark)

[Answer]

> A player with 10 health and 1 attack, holding a sword which gives a +1 attack bonus, battles a spider with 4 health and 1 attack. How many rounds of battle occur? How many ticks does the battle take? Explain how you came to this conclusion, referring to lines of code. How do the answers to these questions change if the player has additionally drunk an invisibility potion? (1 mark)

[Answer]

## Task 1) Code Analysis and Refactoring ⛏️

### a) From DRY to Design Patterns

[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/25T1/groups/M15B_SHIBA/assignment-ii/-/merge_requests/6)

> i. Look inside src/main/java/dungeonmania/entities/enemies. Where can you notice an instance of repeated code? Note down the particular offending lines/methods/fields.

I noticed that the move methods in the Mercenary and ZombieToast classes contain repeated code. Specifically, the code for the invincible case in Mercenary and the runAway case in ZombieToast are the same. Also, the code for the invisible case in Mercenary and the random case in ZombieToast are the same.

> ii. What Design Pattern could be used to improve the quality of the code and avoid repetition? Justify your choice by relating the scenario to the key characteristics of your chosen Design Pattern.

We can use the Strategy Pattern to improve the quality of the code and avoid repetition. The Strategy Pattern provides an interface with various implementations of the same methods, allowing for different behaviors based on the class in use. In this case, we could create an interface with implementations that vary depending on whether the enemy's movementType is invisible, invincible or allied.

> iii. Using your chosen Design Pattern, refactor the code to remove the repetition.

I refactored the code by implementing the Strategy Pattern to eliminate repetition in the movement logic of the Mercenary and ZombieToast classes. I created a Strategy interface along with specific implementations for each movement behavior: InvisibleStrategy, InvincibleStrategy, AlliedStrategy, and HostileStrategy. I integrated this interface into the enemy classes, allowing them to assign and switch strategies based on the player's status. This approach removed repeated code, enhanced maintainability, and improved the flexibility of enemy behaviors in the game.

### b) Pattern Analysis

[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/25T1/groups/M15B_SHIBA/assignment-ii/-/merge_requests/7)

> i. Identify one place where the State Pattern is present in the codebase. Do you think this is an appropriate use of the State Pattern?

The State pattern is valid in this implementation but has not been used effectively, resulting in a suboptimal design. While the state-specific behaviors are encapsulated into separate state classes, the design could be improved by better utilizing the pattern's principles.

> ii. (Option 1) If you answered that it was an appropriate use of the State Pattern, explain why. In your answer, explain how the implementation relates to the purpose and the key characteristics of the State Pattern. Include relevant snippets of code to support your answer.

> (Option 2) If you answered that it was not an appropriate use of the State Pattern, refactor the code to improve the implementation. You may choose to improve the usage of the pattern, switch to a different design pattern, or remove the pattern entirely.

To address this, the PlayerState was modified to use a String to represent the current state instead of a boolean. Additionally, an abstract applyBuff method was introduced, allowing each potion state to define its own implementation. This change eliminates the need for conditional statements in the Player class, as it can now simply call the applyBuff method for state transitions.

### c) Inheritance Design

[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/25T1/groups/M15B_SHIBA/assignment-ii/-/merge_requests/3)

> i. List one design principle that is violated by collectable objects based on the description above. Briefly justify your answer.

[Answer]
The code violates the **Single Responsibility Principle (SRP)**.
This principle states that a class should only have one reason to change. In the current codebase, passive collectables like Treasure, Wood, Key, Arrow, and Bomb are forced to implement methods like applyBuff() and getDurability() which are related to battle logic — something they do not participate in. This results in these classes taking on responsibilities they shouldn’t have, leading to bloated and misleading code.

> ii. Refactor the inheritance structure of the code, and in the process remove the design principle violation you identified.

[Briefly explain what you did]
I refactored the collectable class hierarchy to separate battle-relevant and passive items:

- Introduced a new abstract class `PassiveItem`, which extends `InventoryItem`. It implements default behavior for `applyBuff()` (no buff) and `getDurability()` (infinite durability).
- Moved all non-battle collectables (`Wood`, `Treasure`, `Key`, `Arrow`, `Bomb`) to extend `PassiveItem` instead of `InventoryItem`.
- Introduced another abstract class `BattleItem`, which requires implementing custom battle logic. This was used for `Sword`, which now extends `BattleItem`.
- Removed the unnecessary battle logic from passive item classes.

This restructure adheres to SRP, improves code clarity, and paves the way for easier extension in the future.

### d) More Code Smells

[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/25T1/groups/M15B_SHIBA/assignment-ii/-/merge_requests/3)

> i. What code smell is present in the above snippet?

[Answer]

The code snippet in `Switch.java` violates the **Feature Envy** design principle.  
The method `activateBombs()` is overly interested in the internal details of the `Bomb` class such as accessing its position and radius directly and using that information to compute blast coordinates. This breaks encapsulation and places the responsibility of bomb specific behavior inside the `Switch` class, even though `Bomb` should own its own detonation logic.

> ii. Refactor the code to resolve the smell and underlying problem causing it.

[Briefly explain what you did]
To resolve the code smell, I moved the responsibility for blast logic from `Switch` into the `Bomb` class itself. I added a new method `detonate(GameMap map)` in `Bomb.java`, which handles destroying entities in its blast radius. I then updated `Switch.activateBombs()` to simply call this method on each bomb.

This change improves encapsulation and keeps the behavior of each object inside the class that owns it, following the principles of object oriented design.

### e) Open-Closed Goals

[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/25T1/groups/M15B_SHIBA/assignment-ii/-/merge_requests/4)

> i. Do you think the design is of good quality here? Do you think it complies with the open-closed principle? Do you think the design should be changed?

[Answer]

No, I strongly believe the design does not comply with the Open-Closed Principle. The original `Goal` class handled all logic using string types and a large `switch` statement inside the `achieved()` and `toString()` methods. This means that any time a new goal type is added, the class would have to be modified. This violates the principle that classes should be open for extension but closed for modification.

> ii. If you think the design is sufficient as it is, justify your decision. If you think the answer is no, pick a suitable Design Pattern that would improve the quality of the code and refactor the code accordingly.

[Briefly explain what you did]

I refactored the goal system using the **Composite design pattern**. I created a `GoalComponent` interface that all goal types implement. Each individual goal type (like `ExitGoal`, `TreasureGoal`, and `BouldersGoal`) implements its own `achieved()` and `toString()` logic. I also created composite goals `AndGoal` and `OrGoal` which allow nesting of subgoals. This allows the system to be extended easily in the future or later tasks without modifying existing code.

### f) Open Refactoring

[Merge Request 1](https://nw-syd-gitlab.cseunsw.tech/COMP2511/25T1/groups/M15B_SHIBA/assignment-ii/-/merge_requests/5)

I fixed the violations of the Law of Demeter to ensure the code has low coupling and high cohesion.

[Merge Request 2](/put/links/here)

[Briefly explain what you did]

[Merge Request 3](/put/links/here)

[Briefly explain what you did]

[Merge Request 4](/put/links/here)

[Briefly explain what you did]

## Task 2) Evolution of Requirements 👽

### a) Microevolution - Enemy Goal

[Links to your merge requests](/put/links/here)

**Assumptions**

[Any assumptions made]

**Design**

[Design]

**Changes after review**

[Design review/Changes made]

**Test list**

[Test List]

**Other notes**

[Any other notes]

### Choice 1 (Insert choice)

[Links to your merge requests](/put/links/here)

**Assumptions**

[Any assumptions made]

**Design**

[Design]

**Changes after review**

[Design review/Changes made]

**Test list**

[Test List]

**Other notes**

[Any other notes]

### Choice 2 (Insert choice)

[Links to your merge requests](/put/links/here)

**Assumptions**

[Any assumptions made]

**Design**

[Design]

**Changes after review**

[Design review/Changes made]

**Test list**

[Test List]

**Other notes**

[Any other notes]

### Choice 3 (Logic Switches)

[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/25T1/groups/M15B_SHIBA/assignment-ii/-/merge_requests/11)

**Assumptions**

[Any assumptions made]

- **Initial state**: all logical entities (`light_bulb_off`, `switch_door`) start deactivated.
- **Conductors**: only `Switch` and `Wire` propagate current; adjacency is strictly cardinal.
- **Activation ticks**: `Switch` records the tick when first activated by a boulder; `Wire` inherits that tick.

**Design**

[Design]

1. **`LogicalRuleStrategy`**
   - Static helpers:
     - `isActivated(Entity)` -> checks `Wire` or `Switch`.
     - `isTickActivated(Entity)` -> returns activation tick or –1.
   - Core: `isActiveLogically(GameMap, Position)`.
2. **Concrete strategies**
   - **OR**: any adjacent conductor active.
   - **XOR**: exactly one active.
   - **AND**: all adjacent conductors active and ≥2.
   - **CO_AND**: ≥2 share the **maximum** activation tick.
3. **Entities**
   - `Switch` records activation tick on boulder overlap, calls `activateWires()`.
   - `Wire` recurses `activateWiresNearby(...)`, skips already activated this tick.
   - `LightBulb` & `SwitchDoor` hold a strategy, run `updateActivation()` each tick.
4. **Tick ordering**
   - **Phase 1**: `updateWires()`deactivate all wires, then propagate from active switches.
   - **Phase 2**: `updateLogicals()`evaluate each logical entity’s strategy.

**Changes after review**

[Design review/Changes made]

- Added functionality for LightBulb to infer its on/off state each tick in side NameConverter class
- Enhanced Switch class with methods to activate connected wires.
- Extended GameMap to run `updateWires()` and `updateLogicals()` on every tick in Game class

**Test list**

[Test List]

1. **switchDoorLogicTest**  
   _“Test that XOR and OR switch doors open and AND switch door remains closed.”_

2. **testCoAndLightBulbToggling**  
   _“CoAnd logic toggles light bulbs correctly with a switch–wire network.”_

3. **verifyCompositeLogicBulbs**  
   _“Validate composite logic sequences (OR, XOR, AND, CO_AND) for multiple light bulbs.”_

4. **verifyOrDoorBlocksWhenInactive**  
   _“OR switch door blocks movement when not energized.”_

5. **testOrDoorActivationWalkThrough**  
   _“OR switch door opens once activated and toggles its linked light bulb.”_

6. **testAndDoorLogic**  
   _“AND switch door opens only when both input switches are active.”_

7. **testAndLightBulbActivation**  
   _“AND light bulb activates only when its two input conductors are both active.”_

8. **lightBulbRequiresThreeInputs**  
   _“3‑input AND light bulb powers on only when all three adjacent inputs are active.”_

9. **xorLightBulbBehavior**  
   _“XOR light bulb toggles on/off based on exactly one active input.”_

10. **testCoAndPersistence**  
    _“CO_AND light bulb stays on when extra activations occur from other switches.”_

11. **logicMixed**  
    _“Full‐map mixed‐logic test covering OR, XOR, AND and CO_AND gates together.”_

**Other notes**

[Any other notes]
KEY logic seems wrong, player can pickup multiple keys at once, this I will fix in next MR

### Choice 3 (Insert choice) (If you have a 3rd member)

[Links to your merge requests](/put/links/here)

**Assumptions**

[Any assumptions made]

**Design**

[Design]

**Changes after review**

[Design review/Changes made]

**Test list**

[Test List]

**Other notes**

[Any other notes]

## Task 3) Investigation Task ⁉️

[Merge Request 1](/put/links/here)

[Briefly explain what you did]

[Merge Request 2](/put/links/here)

[Briefly explain what you did]

Add all other changes you made in the same format here:
