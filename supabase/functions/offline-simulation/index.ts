// Supabase Edge Function: offline-simulation
// Simulates character progression while the player is offline
// Time compression: 1 real hour = 2 game hours

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.38.4";
import { simulateOfflineProgress } from "./simulator.ts";
import type { SimulationRequest, SimulationResponse } from "./types.ts";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type",
};

serve(async (req) => {
  // Handle CORS preflight requests
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    // Extract JWT token from Authorization header
    const authHeader = req.headers.get("Authorization");
    if (!authHeader || !authHeader.startsWith("Bearer ")) {
      return new Response(JSON.stringify({ error: "Missing Authorization header" }), {
        status: 401,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    const jwt = authHeader.replace("Bearer ", "");

    // Create Supabase client with the user's JWT to respect RLS policies
    // This ensures subsequent database queries use the authenticated user context
    const supabaseClient = createClient(
      Deno.env.get("SUPABASE_URL") ?? "",
      Deno.env.get("SUPABASE_ANON_KEY") ?? "",
      {
        global: {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        },
      }
    );

    // Get user from JWT token directly (stateless verification)
    const {
      data: { user },
      error: userError,
    } = await supabaseClient.auth.getUser(jwt);

    if (userError || !user) {
      return new Response(JSON.stringify({
        error: "Unauthorized",
        details: userError?.message || "No user found"
      }), {
        status: 401,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    // Parse request body
    const requestBody: SimulationRequest = await req.json();
    const { characterId } = requestBody;

    if (!characterId) {
      return new Response(
        JSON.stringify({ error: "characterId is required" }),
        {
          status: 400,
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        }
      );
    }

    // Fetch character data
    const { data: character, error: characterError } = await supabaseClient
      .from("characters")
      .select("*")
      .eq("id", characterId)
      .eq("user_id", user.id)
      .maybeSingle();

    if (characterError || !character) {
      return new Response(
        JSON.stringify({
          error: "Character not found or access denied",
          details: characterError?.message
        }),
        {
          status: 404,
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        }
      );
    }

    // Calculate time offline (in milliseconds)
    const lastActiveAt = new Date(character.last_active_at);
    const now = new Date();
    const timeOfflineMs = now.getTime() - lastActiveAt.getTime();
    const timeOfflineHours = timeOfflineMs / (1000 * 60 * 60);

    // Time compression: 1 real hour = 2 game hours
    const gameHoursToSimulate = Math.floor(timeOfflineHours * 2);

    // Don't simulate if less than 1 minute offline
    if (gameHoursToSimulate < 0.5) {
      return new Response(
        JSON.stringify({
          message: "No simulation needed (less than 1 minute offline)",
          gameHoursSimulated: 0,
          realHoursOffline: timeOfflineHours,
          activities: [],
          summary: {
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
          },
          characterState: {
            level: character.level,
            experience: character.experience,
            gold: character.gold,
            current_hp: character.current_hp,
            max_hp: character.max_hp,
            strength: character.strength,
            intelligence: character.intelligence,
            agility: character.agility,
            luck: character.luck,
            charisma: character.charisma,
            vitality: character.vitality,
          },
          characterName: character.name,
          missionProgress: {
            principalMissionSteps: 0,
            secondaryMissionsDiscovered: 0,
            loreDiscovered: 0,
          },
        } as SimulationResponse),
        {
          status: 200,
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        }
      );
    }

    // Cap simulation at 7 days (1008 game hours = 168 real hours)
    const cappedGameHours = Math.min(gameHoursToSimulate, 1008);

    // Run the offline simulation
    const simulationResult = await simulateOfflineProgress(
      character,
      cappedGameHours,
      supabaseClient
    );

    // Update character state in database
    // Note: Supabase JS client automatically handles JSON serialization for JSONB columns
    // Note: We do NOT update mission-related fields (active_principal_mission_id, etc.) because:
    // 1. The simulator doesn't modify them
    // 2. They should only be managed by the client app
    // 3. Mission progress is tracked separately in principal_mission_progress table
    const { error: updateError } = await supabaseClient
      .from("characters")
      .update({
        level: simulationResult.updatedCharacter.level,
        experience: simulationResult.updatedCharacter.experience,
        strength: simulationResult.updatedCharacter.strength,
        intelligence: simulationResult.updatedCharacter.intelligence,
        agility: simulationResult.updatedCharacter.agility,
        luck: simulationResult.updatedCharacter.luck,
        charisma: simulationResult.updatedCharacter.charisma,
        vitality: simulationResult.updatedCharacter.vitality,
        current_hp: simulationResult.updatedCharacter.current_hp,
        max_hp: simulationResult.updatedCharacter.max_hp,
        gold: simulationResult.updatedCharacter.gold,
        current_location: simulationResult.updatedCharacter.current_location,
        inventory: simulationResult.updatedCharacter.inventory, // Arrays are auto-serialized to JSONB
        equipped_items: simulationResult.updatedCharacter.equipped_items,
        discovered_locations: simulationResult.updatedCharacter.discovered_locations,
        last_active_at: now.toISOString(),
      })
      .eq("id", characterId);

    if (updateError) {
      return new Response(
        JSON.stringify({
          error: "Failed to update character",
          details: updateError.message
        }),
        {
          status: 500,
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        }
      );
    }

    // Insert activity logs in batches (Supabase has insert limits)
    const batchSize = 100;
    for (let i = 0; i < simulationResult.activities.length; i += batchSize) {
      const batch = simulationResult.activities.slice(i, i + batchSize);
      await supabaseClient
        .from("activity_logs")
        .insert(
          batch.map((activity) => ({
            character_id: characterId,
            timestamp: activity.timestamp,
            activity_type: activity.activity_type,
            description: activity.description,
            rewards: activity.rewards,
            metadata: activity.metadata,
            is_major_event: activity.is_major_event,
          }))
        );
    }

    // Return simulation summary
    const response: SimulationResponse = {
      message: `Simulated ${cappedGameHours} game hours`,
      gameHoursSimulated: cappedGameHours,
      realHoursOffline: Math.floor(timeOfflineHours * 10) / 10,
      activities: simulationResult.activities,
      summary: simulationResult.summary,
      characterState: {
        level: simulationResult.updatedCharacter.level,
        experience: simulationResult.updatedCharacter.experience,
        gold: simulationResult.updatedCharacter.gold,
        current_hp: simulationResult.updatedCharacter.current_hp,
        max_hp: simulationResult.updatedCharacter.max_hp,
        strength: simulationResult.updatedCharacter.strength,
        intelligence: simulationResult.updatedCharacter.intelligence,
        agility: simulationResult.updatedCharacter.agility,
        luck: simulationResult.updatedCharacter.luck,
        charisma: simulationResult.updatedCharacter.charisma,
        vitality: simulationResult.updatedCharacter.vitality,
      },
      characterName: character.name,
      missionProgress: {
        principalMissionSteps: simulationResult.missionStepsDiscovered,
        secondaryMissionsDiscovered: simulationResult.secondaryMissionsDiscovered,
        loreDiscovered: 0, // TODO: Track lore discoveries
      },
    };

    return new Response(JSON.stringify(response), {
      status: 200,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });
  } catch (error) {
    return new Response(
      JSON.stringify({
        error: "Internal server error",
        details: error instanceof Error ? error.message : String(error)
      }),
      {
        status: 500,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      }
    );
  }
});
