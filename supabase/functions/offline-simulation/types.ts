// Type definitions for offline simulation

export interface SimulationRequest {
  characterId: string;
}

export interface SimulationResponse {
  message: string;
  gameHoursSimulated: number;
  realHoursOffline: number;
  activities: Activity[];
  summary: SimulationSummary;
  characterState: {
    level: number;
    experience: number;
    gold: number;
    current_hp: number;
    max_hp: number;
  };
}

export interface Character {
  id: string;
  user_id: string;
  name: string;
  level: number;
  experience: number;
  strength: number;
  intelligence: number;
  agility: number;
  luck: number;
  charisma: number;
  vitality: number;
  current_hp: number;
  max_hp: number;
  current_location: string;
  personality_courage: number;
  personality_greed: number;
  personality_curiosity: number;
  personality_aggression: number;
  personality_social: number;
  personality_impulsive: number;
  job_class: string;
  gold: number;
  last_active_at: string;
  created_at: string;
  inventory: any[];
  equipped_items: any;
  discovered_locations: string[];
  active_quests: any[];
}

export interface Activity {
  timestamp: string;
  activity_type: string; // combat, exploration, social, rest, etc.
  description: string;
  rewards?: {
    xp?: number;
    gold?: number;
    items?: any[];
  };
  metadata?: any;
  is_major_event: boolean;
}

export interface SimulationSummary {
  totalCombats: number;
  combatsWon: number;
  combatsLost: number;
  totalXpGained: number;
  totalGoldGained: number;
  levelsGained: number;
  deaths: number;
  locationsDiscovered: number;
  itemsFound: number;
  majorEvents: string[];
}

export interface SimulationResult {
  updatedCharacter: Character;
  activities: Activity[];
  summary: SimulationSummary;
}

export interface Enemy {
  name: string;
  level: number;
  health: number;
  attack: number;
  defense: number;
  xpReward: number;
  goldReward: number;
}

export interface CombatResult {
  victory: boolean;
  xpGained: number;
  goldGained: number;
  hpLost: number;
  itemsFound: any[];
  description: string;
}

export interface Decision {
  type: DecisionType;
  reason: string;
  location?: string;
  targetEnemy?: Enemy;
}

export enum DecisionType {
  REST = "rest",
  EXPLORE = "explore",
  COMBAT = "combat",
  FLEE = "flee",
  RETURN_TO_TOWN = "return_to_town",
}

export interface JobClassConfig {
  name: string;
  primaryStat: string;
  secondaryStats: string[];
  statWeights: {
    strength: number;
    intelligence: number;
    agility: number;
    luck: number;
    charisma: number;
    vitality: number;
  };
}
