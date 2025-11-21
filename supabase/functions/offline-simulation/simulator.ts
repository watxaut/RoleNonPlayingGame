// Main offline simulation orchestrator
// Simulates game hours with time compression

import { makeDecision, getDecisionDuration, calculateRestHealing } from "./ai.ts";
import { simulateCombat } from "./combat.ts";
import { checkAndApplyLevelUp } from "./leveling.ts";
import type {
  Character,
  Activity,
  SimulationResult,
  SimulationSummary,
  DecisionType,
} from "./types.ts";

/**
 * Simulate offline progress for a character
 * @param character - The character to simulate
 * @param gameHours - Number of game hours to simulate
 * @param supabaseClient - Supabase client for database access (if needed)
 * @returns SimulationResult with updated character and activities
 */
export async function simulateOfflineProgress(
  character: Character,
  gameHours: number,
  supabaseClient: any
): Promise<SimulationResult> {
  // Deep clone character to avoid mutating original
  const char: Character = JSON.parse(JSON.stringify(character));

  // Parse JSON fields if they are strings (from database)
  if (typeof char.discovered_locations === 'string') {
    char.discovered_locations = JSON.parse(char.discovered_locations || '[]');
  }
  if (typeof char.inventory === 'string') {
    char.inventory = JSON.parse(char.inventory || '[]');
  }
  if (typeof char.active_quests === 'string') {
    char.active_quests = JSON.parse(char.active_quests || '[]');
  }
  if (typeof char.equipped_items === 'string') {
    char.equipped_items = JSON.parse(char.equipped_items || '{}');
  }

  const activities: Activity[] = [];
  const summary: SimulationSummary = {
    totalCombats: 0,
    combatsWon: 0,
    combatsLost: 0,
    totalXpGained: 0,
    totalGoldGained: 0,
    levelsGained: 0,
    deaths: 0,
    locationsDiscovered: 0,
    itemsFound: 0,
    majorEvents: [],
  };

  // Track mission progress during simulation
  const missionStepsDiscovered: string[] = [];
  const secondaryMissionsDiscovered: string[] = [];

  // Simulation loop
  let elapsedMinutes = 0;
  const totalMinutes = gameHours * 60;
  const startTime = new Date(char.last_active_at);

  while (elapsedMinutes < totalMinutes) {
    // Make AI decision
    const decision = makeDecision(char);
    const decisionDuration = getDecisionDuration(decision.type);

    // Calculate timestamp for this activity
    const activityTime = new Date(
      startTime.getTime() + elapsedMinutes * 60 * 1000
    );

    // Execute decision
    const activity = executeDecision(
      char,
      decision,
      activityTime,
      summary,
      missionStepsDiscovered,
      secondaryMissionsDiscovered
    );

    if (activity) {
      activities.push(activity);
    }

    // Check for level-up
    const levelsGained = checkAndApplyLevelUp(char);
    if (levelsGained > 0) {
      summary.levelsGained += levelsGained;
      summary.majorEvents.push(
        `Reached level ${char.level}${levelsGained > 1 ? ` (+${levelsGained} levels)` : ""}`
      );

      activities.push({
        timestamp: activityTime.toISOString(),
        activity_type: "level_up",
        description: `${char.name} leveled up to level ${char.level}!`,
        rewards: undefined,
        metadata: { new_level: char.level },
        is_major_event: true,
      });
    }

    // Check for death
    if (char.current_hp <= 0) {
      handleDeath(char, activityTime, activities, summary);
    }

    // Advance time
    elapsedMinutes += decisionDuration;

    // Safety limit: max 1000 activities to prevent runaway simulations
    if (activities.length >= 1000) {
      break;
    }
  }

  // Final HP restoration if character is in town
  if (char.current_location === "Willowdale Village") {
    char.current_hp = char.max_hp;
  }

  // Save mission progress to database if any was made
  if (missionStepsDiscovered.length > 0 && char.active_principal_mission_id) {
    try {
      // Fetch current progress
      const { data: currentProgress } = await supabaseClient
        .from("principal_mission_progress")
        .select("completed_step_ids")
        .eq("character_id", char.id)
        .eq("mission_id", char.active_principal_mission_id)
        .maybeSingle();

      const existingSteps = currentProgress?.completed_step_ids || [];
      const allSteps = [...new Set([...existingSteps, ...missionStepsDiscovered])];

      // Update or insert progress
      const { error: upsertError } = await supabaseClient
        .from("principal_mission_progress")
        .upsert({
          character_id: char.id,
          mission_id: char.active_principal_mission_id,
          status: 'in_progress',
          completed_step_ids: allSteps,
          started_at: currentProgress?.started_at || new Date().toISOString(),
          last_progress_at: new Date().toISOString(),
          progress_percentage: Math.floor((allSteps.length / 10) * 100),
        });

      if (upsertError) {
        console.error("Failed to save principal mission progress:", upsertError);
      } else {
        console.log(`✅ Saved ${allSteps.length} mission steps for mission ${char.active_principal_mission_id}`);
      }
    } catch (e) {
      console.error("Exception while saving principal mission progress:", e);
    }
  }

  if (secondaryMissionsDiscovered.length > 0) {
    try {
      for (const missionId of secondaryMissionsDiscovered) {
        await supabaseClient
          .from("secondary_mission_progress")
          .insert({
            character_id: char.id,
            mission_id: missionId,
            status: "ongoing",
            discovered_at: new Date().toISOString(),
          });
      }
    } catch (e) {
      // Silently fail - mission progress is not critical
    }
  }

  return {
    updatedCharacter: char,
    activities,
    summary,
    missionStepsDiscovered: missionStepsDiscovered.length,
    secondaryMissionsDiscovered: secondaryMissionsDiscovered.length,
  };
}

/**
 * Execute a decision and return the resulting activity
 */
function executeDecision(
  character: Character,
  decision: any,
  timestamp: Date,
  summary: SimulationSummary,
  missionStepsDiscovered: string[],
  secondaryMissionsDiscovered: string[]
): Activity | null {
  switch (decision.type as DecisionType) {
    case "rest" as DecisionType:
      return executeRest(character, timestamp);

    case "explore" as DecisionType:
      return executeExplore(
        character,
        decision,
        timestamp,
        summary,
        missionStepsDiscovered,
        secondaryMissionsDiscovered
      );

    case "combat" as DecisionType:
      return executeCombat(
        character,
        decision,
        timestamp,
        summary,
        missionStepsDiscovered,
        secondaryMissionsDiscovered
      );

    case "flee" as DecisionType:
      return executeFlee(character, timestamp);

    case "return_to_town" as DecisionType:
      return executeReturnToTown(character, timestamp);

    default:
      return null;
  }
}

/**
 * Execute REST decision
 */
function executeRest(character: Character, timestamp: Date): Activity {
  const durationMinutes = 30 + Math.random() * 30; // 30-60 minutes
  const healing = calculateRestHealing(character, durationMinutes);

  character.current_hp = Math.min(
    character.current_hp + healing,
    character.max_hp
  );

  return {
    timestamp: timestamp.toISOString(),
    activity_type: "rest",
    description: `${character.name} rested and recovered ${healing} HP`,
    rewards: undefined,
    metadata: { hp_restored: healing },
    is_major_event: false,
  };
}

/**
 * Execute EXPLORE decision
 */
function executeExplore(
  character: Character,
  decision: any,
  timestamp: Date,
  summary: SimulationSummary,
  missionStepsDiscovered: string[],
  secondaryMissionsDiscovered: string[]
): Activity {
  const newLocation = decision.location || character.current_location;
  const isNewLocation = !character.discovered_locations.includes(newLocation);

  if (isNewLocation) {
    character.discovered_locations.push(newLocation);
    summary.locationsDiscovered++;
    summary.majorEvents.push(`Discovered ${newLocation}`);
  }

  character.current_location = newLocation;

  // Small XP reward for exploration
  const explorationXp = 5 + Math.floor(Math.random() * 10);
  character.experience += explorationXp;
  summary.totalXpGained += explorationXp;

  // Check for mission discoveries (10% chance on exploration)
  if (Math.random() < 0.1) {
    // 20% chance for principal mission step discovery if active mission
    if (character.active_principal_mission_id && Math.random() < 0.2) {
      const stepId = `story_step_${Date.now()}_${Math.floor(Math.random() * 1000)}`;
      missionStepsDiscovered.push(stepId);
      summary.majorEvents.push(`Discovered a clue about the principal mission!`);
    }
    // 30% chance for secondary mission discovery
    else if (Math.random() < 0.3) {
      // Pick a random mission ID from the SecondaryMissionsRepository (sm_001 to sm_100)
      const missionNumber = Math.floor(Math.random() * 100) + 1;
      const missionId = `sm_${String(missionNumber).padStart(3, '0')}`;
      secondaryMissionsDiscovered.push(missionId);
      summary.majorEvents.push(`Discovered a new secondary mission!`);
    }
  }

  return {
    timestamp: timestamp.toISOString(),
    activity_type: "exploration",
    description: isNewLocation
      ? `${character.name} discovered ${newLocation}!`
      : `${character.name} explored ${newLocation}`,
    rewards: { xp: explorationXp },
    metadata: { location: newLocation, new_discovery: isNewLocation },
    is_major_event: isNewLocation,
  };
}

/**
 * Execute COMBAT decision
 */
function executeCombat(
  character: Character,
  decision: any,
  timestamp: Date,
  summary: SimulationSummary,
  missionStepsDiscovered: string[],
  secondaryMissionsDiscovered: string[]
): Activity {
  const enemy = decision.targetEnemy;
  const combatResult = simulateCombat(character, enemy);

  summary.totalCombats++;

  if (combatResult.victory) {
    summary.combatsWon++;
    summary.totalXpGained += combatResult.xpGained;
    summary.totalGoldGained += combatResult.goldGained;
    summary.itemsFound += combatResult.itemsFound.length;

    character.experience += combatResult.xpGained;
    character.gold += combatResult.goldGained;
    character.current_hp -= combatResult.hpLost;

    // Add items to inventory
    if (combatResult.itemsFound.length > 0) {
      character.inventory.push(...combatResult.itemsFound);
    }

    // Check for mission progress after victory (5% chance)
    if (Math.random() < 0.05) {
      // Principal mission step discovery if active mission
      if (character.active_principal_mission_id && Math.random() < 0.4) {
        const stepId = `combat_step_${Date.now()}_${Math.floor(Math.random() * 1000)}`;
        missionStepsDiscovered.push(stepId);
        summary.majorEvents.push(`Combat revealed a clue about the principal mission!`);
      }
    }

    return {
      timestamp: timestamp.toISOString(),
      activity_type: "combat",
      description: combatResult.description,
      rewards: {
        xp: combatResult.xpGained,
        gold: combatResult.goldGained,
        items: combatResult.itemsFound,
      },
      metadata: { enemy: enemy.name, victory: true },
      is_major_event: false,
    };
  } else {
    summary.combatsLost++;
    summary.totalXpGained += combatResult.xpGained; // Small XP for trying

    character.experience += combatResult.xpGained;
    character.current_hp -= combatResult.hpLost;

    return {
      timestamp: timestamp.toISOString(),
      activity_type: "combat",
      description: combatResult.description,
      rewards: { xp: combatResult.xpGained },
      metadata: { enemy: enemy.name, victory: false },
      is_major_event: false,
    };
  }
}

/**
 * Execute FLEE decision
 */
function executeFlee(character: Character, timestamp: Date): Activity {
  return {
    timestamp: timestamp.toISOString(),
    activity_type: "flee",
    description: `${character.name} fled from danger`,
    rewards: undefined,
    metadata: {},
    is_major_event: false,
  };
}

/**
 * Execute RETURN_TO_TOWN decision
 */
function executeReturnToTown(character: Character, timestamp: Date): Activity {
  character.current_location = "Willowdale Village";
  character.current_hp = character.max_hp; // Full heal in town

  return {
    timestamp: timestamp.toISOString(),
    activity_type: "return_to_town",
    description: `${character.name} returned to Willowdale Village and fully recovered`,
    rewards: undefined,
    metadata: { location: "Willowdale Village" },
    is_major_event: false,
  };
}

/**
 * Handle character death and respawn
 */
function handleDeath(
  character: Character,
  timestamp: Date,
  activities: Activity[],
  summary: SimulationSummary
): void {
  summary.deaths++;
  summary.majorEvents.push(`Died and respawned at Willowdale Village`);

  // Death penalties
  const goldLost = Math.floor(character.gold * 0.1); // Lose 10% gold
  const xpLost = Math.floor(character.experience * 0.05); // Lose 5% XP

  character.gold -= goldLost;
  character.experience = Math.max(0, character.experience - xpLost);

  // Respawn
  character.current_location = "Willowdale Village";
  character.current_hp = character.max_hp;

  activities.push({
    timestamp: timestamp.toISOString(),
    activity_type: "death",
    description: `${character.name} died and respawned at Willowdale Village. Lost ${goldLost} gold and ${xpLost} XP.`,
    rewards: undefined,
    metadata: { gold_lost: goldLost, xp_lost: xpLost },
    is_major_event: true,
  });
}
