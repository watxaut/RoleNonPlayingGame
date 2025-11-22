// Edge Function: Coordinate Encounter
// Handles autonomous character-to-character interactions
// Runs on Supabase Edge Functions (Deno runtime)

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

// Encounter request interface
interface EncounterRequest {
  character1Id: string;
  character2Id: string;
  location: string;
}

// Character interface
interface Character {
  id: string;
  name: string;
  level: number;
  strength: number;
  intelligence: number;
  agility: number;
  luck: number;
  charisma: number;
  vitality: number;
  personality_courage: number;
  personality_greed: number;
  personality_curiosity: number;
  personality_aggression: number;
  personality_social: number;
  personality_impulsive: number;
  job_class: string;
  current_hp: number;
  max_hp: number;
  gold: number;
}

// Encounter AI class
class EncounterAI {
  character: Character;

  constructor(character: Character) {
    this.character = character;
  }

  // Evaluate what type of interaction this character prefers
  evaluateInteractionPreference(otherChar: Character): string {
    const { personality_social, personality_aggression, personality_greed, personality_courage } = this.character;

    // Calculate level difference
    const levelDiff = Math.abs(this.character.level - otherChar.level);

    // Very low HP = flee/ignore
    if (this.character.current_hp < this.character.max_hp * 0.3) {
      return "ignore";
    }

    // High aggression + big level advantage = combat
    if (personality_aggression > 0.7 && this.character.level > otherChar.level + 3) {
      return "combat";
    }

    // High social personality = greeting or trade
    if (personality_social > 0.6) {
      // Greedy characters prefer trading
      if (personality_greed > 0.6) {
        return "trade";
      }
      // Brave/curious characters want to party
      if (personality_courage > 0.5 || personality_curiosity > 0.5) {
        return "party";
      }
      return "greeting";
    }

    // Low social, low aggression = ignore
    if (personality_social < 0.3 && personality_aggression < 0.3) {
      return "ignore";
    }

    // Similar levels + moderate courage = party
    if (levelDiff <= 2 && personality_courage > 0.4) {
      return "party";
    }

    // Default to greeting
    return "greeting";
  }
}

// Negotiate interaction between two characters
function negotiateInteraction(
  char1Preference: string,
  char2Preference: string
): string {
  // Both ignore = ignore
  if (char1Preference === "ignore" || char2Preference === "ignore") {
    return "ignore";
  }

  // Both want combat = combat
  if (char1Preference === "combat" && char2Preference === "combat") {
    return "combat";
  }

  // One wants combat, other doesn't = combat (aggressor wins)
  if (char1Preference === "combat" || char2Preference === "combat") {
    return "combat";
  }

  // Both want party = party
  if (char1Preference === "party" && char2Preference === "party") {
    return "party";
  }

  // Both want trade = trade
  if (char1Preference === "trade" && char2Preference === "trade") {
    return "trade";
  }

  // One wants party, other wants trade or greeting = party
  if (char1Preference === "party" || char2Preference === "party") {
    return "party";
  }

  // One wants trade, other wants greeting = trade
  if (char1Preference === "trade" || char2Preference === "trade") {
    return "trade";
  }

  // Default to greeting
  return "greeting";
}

// Execute greeting interaction
function executeGreeting(char1: Character, char2: Character): any {
  const charismaSum = char1.charisma + char2.charisma;
  const friendlyChance = charismaSum / 20.0; // 0-1 scale

  const roll = Math.random();

  if (roll < friendlyChance) {
    return {
      success: true,
      description: `${char1.name} and ${char2.name} exchanged friendly greetings.`,
      relationship: "friendly",
    };
  } else {
    return {
      success: true,
      description: `${char1.name} and ${char2.name} briefly acknowledged each other.`,
      relationship: "neutral",
    };
  }
}

// Execute trade interaction
function executeTrade(char1: Character, char2: Character): any {
  // Simplified trading - both characters exchange small amount of gold
  const char1Offer = Math.floor(char1.gold * 0.05); // 5% of gold
  const char2Offer = Math.floor(char2.gold * 0.05);

  if (char1Offer < 10 || char2Offer < 10) {
    return {
      success: false,
      description: `${char1.name} and ${char2.name} couldn't agree on a trade. Insufficient resources.`,
    };
  }

  // Charisma check influences trade success
  const char1Charisma = char1.charisma + d21();
  const char2Charisma = char2.charisma + d21();

  if (char1Charisma + char2Charisma > 20) {
    return {
      success: true,
      description: `${char1.name} and ${char2.name} successfully traded items and gold.`,
      char1_gold_change: -char1Offer + char2Offer,
      char2_gold_change: -char2Offer + char1Offer,
    };
  } else {
    return {
      success: false,
      description: `${char1.name} and ${char2.name} couldn't reach a fair trade agreement.`,
    };
  }
}

// Execute party formation
function executePartyFormation(char1: Character, char2: Character): any {
  // Both characters form temporary party
  const duration = 15 + Math.floor(Math.random() * 15); // 15-30 minutes

  return {
    success: true,
    description: `${char1.name} and ${char2.name} formed a party and will adventure together.`,
    party_duration_minutes: duration,
    // In actual implementation, this would create a party record
  };
}

// Execute combat
function executeCombat(char1: Character, char2: Character): any {
  // Simplified combat using power calculation
  const char1Power = calculatePower(char1);
  const char2Power = calculatePower(char2);

  const char1Roll = d21();
  const char2Roll = d21();

  // Critical results
  if (char1Roll === 21 && char2Roll === 21) {
    return {
      success: true,
      description: `${char1.name} and ${char2.name} fought fiercely but both landed critical hits. Both withdrew.`,
      result: "draw",
      char1_damage: Math.floor(char1.max_hp * 0.3),
      char2_damage: Math.floor(char2.max_hp * 0.3),
    };
  }

  if (char1Roll === 21) {
    return {
      success: true,
      description: `${char1.name} landed a CRITICAL HIT on ${char2.name} and won the duel!`,
      result: "char1_win",
      char2_damage: Math.floor(char2.max_hp * 0.5),
      char1_gold_gain: Math.floor(char2.gold * 0.1),
      char2_gold_loss: Math.floor(char2.gold * 0.1),
    };
  }

  if (char2Roll === 21) {
    return {
      success: true,
      description: `${char2.name} landed a CRITICAL HIT on ${char1.name} and won the duel!`,
      result: "char2_win",
      char1_damage: Math.floor(char1.max_hp * 0.5),
      char2_gold_gain: Math.floor(char1.gold * 0.1),
      char1_gold_loss: Math.floor(char1.gold * 0.1),
    };
  }

  // Normal combat resolution
  const char1Score = char1Roll + char1Power;
  const char2Score = char2Roll + char2Power;

  if (char1Score > char2Score + 5) {
    return {
      success: true,
      description: `${char1.name} defeated ${char2.name} in combat.`,
      result: "char1_win",
      char2_damage: Math.floor(char2.max_hp * 0.3),
      char1_gold_gain: Math.floor(char2.gold * 0.05),
      char2_gold_loss: Math.floor(char2.gold * 0.05),
    };
  } else if (char2Score > char1Score + 5) {
    return {
      success: true,
      description: `${char2.name} defeated ${char1.name} in combat.`,
      result: "char2_win",
      char1_damage: Math.floor(char1.max_hp * 0.3),
      char2_gold_gain: Math.floor(char1.gold * 0.05),
      char1_gold_loss: Math.floor(char1.gold * 0.05),
    };
  } else {
    return {
      success: true,
      description: `${char1.name} and ${char2.name} fought but neither could gain advantage. Draw.`,
      result: "draw",
      char1_damage: Math.floor(char1.max_hp * 0.15),
      char2_damage: Math.floor(char2.max_hp * 0.15),
    };
  }
}

// Helper: calculate character power
function calculatePower(char: Character): number {
  return Math.floor((char.strength + char.intelligence + char.agility + char.vitality) / 4 + char.level * 2);
}

// Helper: d21 roll
function d21(): number {
  return Math.floor(Math.random() * 21) + 1;
}

// Main handler
serve(async (req) => {
  try {
    // CORS headers
    if (req.method === "OPTIONS") {
      return new Response(null, {
        headers: {
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Methods": "POST, OPTIONS",
          "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
        },
      });
    }

    const { character1Id, character2Id, location }: EncounterRequest = await req.json();

    // Validate input
    if (!character1Id || !character2Id || !location) {
      return new Response(
        JSON.stringify({ error: "Missing required parameters" }),
        { status: 400, headers: { "Content-Type": "application/json" } }
      );
    }

    // Initialize Supabase client
    const supabase = createClient(
      Deno.env.get("SUPABASE_URL") ?? "",
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? ""
    );

    // Fetch both characters
    const { data: characters, error: charError } = await supabase
      .from("characters")
      .select("*")
      .in("id", [character1Id, character2Id]);

    if (charError || !characters || characters.length !== 2) {
      return new Response(
        JSON.stringify({ error: "Characters not found" }),
        { status: 404, headers: { "Content-Type": "application/json" } }
      );
    }

    const char1 = characters.find(c => c.id === character1Id)!;
    const char2 = characters.find(c => c.id === character2Id)!;

    // Both characters evaluate interaction
    const ai1 = new EncounterAI(char1);
    const ai2 = new EncounterAI(char2);

    const char1Preference = ai1.evaluateInteractionPreference(char2);
    const char2Preference = ai2.evaluateInteractionPreference(char1);

    // Negotiate final interaction type
    const interactionType = negotiateInteraction(char1Preference, char2Preference);

    // Create encounter record
    const { data: encounter, error: encounterError } = await supabase
      .from("encounters")
      .insert({
        character1_id: character1Id,
        character2_id: character2Id,
        location,
        encounter_type: interactionType,
        status: "initiated",
      })
      .select()
      .single();

    if (encounterError) {
      throw encounterError;
    }

    // Execute interaction based on type
    let outcome: any;
    switch (interactionType) {
      case "greeting":
        outcome = executeGreeting(char1, char2);
        break;
      case "trade":
        outcome = executeTrade(char1, char2);
        break;
      case "party":
        outcome = executePartyFormation(char1, char2);
        break;
      case "combat":
        outcome = executeCombat(char1, char2);
        break;
      case "ignore":
        outcome = {
          success: true,
          description: `${char1.name} and ${char2.name} passed by each other without interaction.`,
        };
        break;
      default:
        outcome = { success: false, description: "Unknown interaction type" };
    }

    // Update encounter with outcome
    await supabase
      .from("encounters")
      .update({
        status: "completed",
        outcome,
        completed_at: new Date().toISOString(),
      })
      .eq("id", encounter.id);

    // Apply gold/HP changes if applicable
    if (outcome.char1_gold_change || outcome.char1_damage) {
      await supabase
        .from("characters")
        .update({
          gold: Math.max(0, char1.gold + (outcome.char1_gold_change || 0) + (outcome.char1_gold_gain || 0) - (outcome.char1_gold_loss || 0)),
          current_hp: Math.max(0, char1.current_hp - (outcome.char1_damage || 0)),
        })
        .eq("id", character1Id);
    }

    if (outcome.char2_gold_change || outcome.char2_damage) {
      await supabase
        .from("characters")
        .update({
          gold: Math.max(0, char2.gold + (outcome.char2_gold_change || 0) + (outcome.char2_gold_gain || 0) - (outcome.char2_gold_loss || 0)),
          current_hp: Math.max(0, char2.current_hp - (outcome.char2_damage || 0)),
        })
        .eq("id", character2Id);
    }

    // Log activities for both characters
    await supabase.from("activity_logs").insert([
      {
        character_id: character1Id,
        activity_type: "social",
        description: `Encountered ${char2.name}. ${outcome.description}`,
        metadata: {
          encounter_id: encounter.id,
          encounter_type: interactionType,
          other_character: char2.name,
        },
        is_major_event: interactionType === "combat" || interactionType === "party",
      },
      {
        character_id: character2Id,
        activity_type: "social",
        description: `Encountered ${char1.name}. ${outcome.description}`,
        metadata: {
          encounter_id: encounter.id,
          encounter_type: interactionType,
          other_character: char1.name,
        },
        is_major_event: interactionType === "combat" || interactionType === "party",
      },
    ]);

    return new Response(
      JSON.stringify({
        success: true,
        encounter,
        outcome,
        interaction_type: interactionType,
        char1_preference: char1Preference,
        char2_preference: char2Preference,
      }),
      {
        headers: {
          "Content-Type": "application/json",
          "Access-Control-Allow-Origin": "*",
        },
      }
    );
  } catch (error) {
    console.error("Encounter coordination error:", error);
    return new Response(
      JSON.stringify({ error: error.message }),
      {
        status: 500,
        headers: {
          "Content-Type": "application/json",
          "Access-Control-Allow-Origin": "*",
        },
      }
    );
  }
});
