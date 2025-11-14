// Supabase Edge Function: offline-simulation
// Simulates character progression while the player is offline
// Time compression: 1 real hour = 6 game hours

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
    // Create Supabase client
    const supabaseClient = createClient(
      Deno.env.get("SUPABASE_URL") ?? "",
      Deno.env.get("SUPABASE_ANON_KEY") ?? "",
      {
        global: {
          headers: { Authorization: req.headers.get("Authorization")! },
        },
      }
    );

    // Get user from auth token
    const {
      data: { user },
      error: userError,
    } = await supabaseClient.auth.getUser();

    if (userError || !user) {
      return new Response(JSON.stringify({ error: "Unauthorized" }), {
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
      .single();

    if (characterError || !character) {
      return new Response(
        JSON.stringify({ error: "Character not found or access denied" }),
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

    // Time compression: 1 real hour = 6 game hours
    const gameHoursToSimulate = Math.floor(timeOfflineHours * 6);

    // Don't simulate if less than 5 minutes offline (< 0.5 game hours)
    if (gameHoursToSimulate < 0.5) {
      return new Response(
        JSON.stringify({
          message: "No simulation needed (less than 5 minutes offline)",
          gameHoursSimulated: 0,
          activities: [],
        } as SimulationResponse),
        {
          status: 200,
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        }
      );
    }

    // Cap simulation at 7 days (1008 game hours = 168 real hours)
    const cappedGameHours = Math.min(gameHoursToSimulate, 1008);

    console.log(
      `Simulating ${cappedGameHours} game hours for character ${character.name}`
    );

    // Run the offline simulation
    const simulationResult = await simulateOfflineProgress(
      character,
      cappedGameHours,
      supabaseClient
    );

    // Update character state in database
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
        inventory: simulationResult.updatedCharacter.inventory,
        equipped_items: simulationResult.updatedCharacter.equipped_items,
        discovered_locations:
          simulationResult.updatedCharacter.discovered_locations,
        last_active_at: now.toISOString(),
      })
      .eq("id", characterId);

    if (updateError) {
      console.error("Error updating character:", updateError);
      return new Response(
        JSON.stringify({ error: "Failed to update character" }),
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
      const { error: activityError } = await supabaseClient
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

      if (activityError) {
        console.error("Error inserting activity logs:", activityError);
      }
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
      },
    };

    return new Response(JSON.stringify(response), {
      status: 200,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });
  } catch (error) {
    console.error("Unexpected error:", error);
    return new Response(
      JSON.stringify({ error: "Internal server error", details: error.message }),
      {
        status: 500,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      }
    );
  }
});
