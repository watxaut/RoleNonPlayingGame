package com.watxaut.rolenonplayinggame.data

import com.watxaut.rolenonplayinggame.domain.model.Quest
import com.watxaut.rolenonplayinggame.domain.model.QuestObjective
import com.watxaut.rolenonplayinggame.domain.model.QuestRewards
import com.watxaut.rolenonplayinggame.domain.model.QuestType

/**
 * Quest Database for Aethermoor.
 * Quests are completed autonomously by character AI.
 *
 * Phase 3 - World & Content Implementation
 */
object QuestDatabase {

    private val quests = listOf(
        // ==================== HEARTLANDS QUESTS ====================
        Quest(
            id = "quest_first_steps",
            name = "First Steps on Aethermoor",
            description = "Elder Marlow asks you to explore the Meadowbrook Fields and prove yourself.",
            questGiverId = "npc_elder_marlow",
            location = "heartlands_havenmoor",
            levelRequirement = 1,
            questType = QuestType.EXPLORE,
            objectives = listOf(
                QuestObjective("Explore Meadowbrook Fields", "heartlands_meadowbrook_fields", 1),
                QuestObjective("Defeat Slimes", "Slime", 5)
            ),
            rewards = QuestRewards(
                experience = 100,
                gold = 50,
                items = listOf("consumable_health_potion")
            ),
            lore = "Every adventurer on Aethermoor starts somewhere. The Meadowbrook Fields are where most begin their journey."
        ),
        Quest(
            id = "quest_goblin_problem",
            name = "The Goblin Problem",
            description = "Goblins have taken over the Broken Bridge. Drive them out.",
            questGiverId = "npc_elder_marlow",
            location = "heartlands_havenmoor",
            levelRequirement = 5,
            questType = QuestType.BOSS,
            objectives = listOf(
                QuestObjective("Defeat Goblin Scouts", "Goblin Scout", 10),
                QuestObjective("Defeat Goblin King Skritch", "Goblin King Skritch", 1)
            ),
            rewards = QuestRewards(
                experience = 500,
                gold = 200,
                items = listOf("weapon_iron_blade")
            )
        ),
        Quest(
            id = "quest_lost_delivery",
            name = "Lost Delivery",
            description = "Innkeeper Rosie's supply cart was attacked by bandits. Recover her goods.",
            questGiverId = "npc_innkeeper_rosie",
            location = "heartlands_havenmoor",
            levelRequirement = 3,
            questType = QuestType.COLLECT,
            objectives = listOf(
                QuestObjective("Defeat Bandits", "Bandit", 5),
                QuestObjective("Recover Supplies", "quest_item_supplies", 1)
            ),
            rewards = QuestRewards(
                experience = 200,
                gold = 100,
                items = listOf("consumable_greater_health_potion", "consumable_health_potion")
            )
        ),

        // ==================== THORNWOOD WILDS QUESTS ====================
        Quest(
            id = "quest_widow_threat",
            name = "The Widow's Threat",
            description = "The Widow Queen spider has claimed the Webbed Hollow. Slay her before she spreads.",
            questGiverId = "npc_ranger_captain_silva",
            location = "thornwood_thornguard_outpost",
            levelRequirement = 15,
            questType = QuestType.BOSS,
            objectives = listOf(
                QuestObjective("Defeat the Widow Queen", "Widow Queen", 1)
            ),
            rewards = QuestRewards(
                experience = 2000,
                gold = 500,
                items = listOf("weapon_silvered_longsword")
            ),
            lore = "The Widow Queen was once a normal spider, transformed by the Thornwood's dark magic."
        ),
        Quest(
            id = "quest_heart_tree",
            name = "The Heart Tree's Blessing",
            description = "Find the legendary Heart Tree deep in the Thornwood.",
            questGiverId = "npc_ranger_captain_silva",
            location = "thornwood_thornguard_outpost",
            levelRequirement = 18,
            questType = QuestType.EXPLORE,
            objectives = listOf(
                QuestObjective("Discover the Heart Tree", "thornwood_heart_tree", 1)
            ),
            rewards = QuestRewards(
                experience = 1500,
                gold = 300,
                items = listOf("weapon_heartwood_staff")
            ),
            lore = "The Heart Tree is as old as Aethermoor itself. Those who find it gain its blessing."
        ),
        Quest(
            id = "quest_corrupted_grove",
            name = "Cleanse the Corrupted Grove",
            description = "Thornmaw's corruption spreads. Defeat the corrupted treant.",
            questGiverId = "npc_druid_moonwhisper",
            location = "thornwood_thornguard_outpost",
            levelRequirement = 20,
            questType = QuestType.BOSS,
            objectives = listOf(
                QuestObjective("Defeat Corrupted Ents", "Corrupted Ent", 10),
                QuestObjective("Defeat Thornmaw", "Thornmaw", 1)
            ),
            rewards = QuestRewards(
                experience = 3000,
                gold = 800,
                items = listOf("armor_thornwood_guardian")
            )
        ),

        // ==================== ASHENVEIL DESERT QUESTS ====================
        Quest(
            id = "quest_bone_empress",
            name = "The Bone Empress's Tomb",
            description = "Venture into the Bone Canyons and defeat the undead empress.",
            questGiverId = "npc_sandstrider_chief",
            location = "ashenveil_sandstone_bazaar",
            levelRequirement = 28,
            questType = QuestType.BOSS,
            objectives = listOf(
                QuestObjective("Explore the Bone Canyons", "ashenveil_bone_canyons", 1),
                QuestObjective("Defeat the Bone Empress", "Bone Empress", 1)
            ),
            rewards = QuestRewards(
                experience = 5000,
                gold = 1500,
                items = listOf("accessory_moonwell_charm")
            ),
            lore = "The Bone Empress ruled the desert before the Sundering. Now she commands the dead."
        ),
        Quest(
            id = "quest_desert_secrets",
            name = "Secrets Beneath the Sand",
            description = "Uncover the mysteries of the Sunscorch Ruins.",
            questGiverId = "npc_sandstrider_chief",
            location = "ashenveil_sandstone_bazaar",
            levelRequirement = 25,
            questType = QuestType.EXPLORE,
            objectives = listOf(
                QuestObjective("Explore Sunscorch Ruins", "ashenveil_sunscorch_ruins", 1),
                QuestObjective("Defeat Fire Elementals", "Fire Elemental", 5)
            ),
            rewards = QuestRewards(
                experience = 3500,
                gold = 1000
            )
        ),

        // ==================== FROSTPEAK MOUNTAINS QUESTS ====================
        Quest(
            id = "quest_lost_hold",
            name = "Reclaim Irondelve Hold",
            description = "Help the Irondelve Reclaimers scout their ancestral home.",
            questGiverId = "npc_irondelve_leader",
            location = "frostpeak_mountain_base",
            levelRequirement = 30,
            questType = QuestType.EXPLORE,
            objectives = listOf(
                QuestObjective("Explore Irondelve Hold", "frostpeak_irondelve_hold", 1),
                QuestObjective("Defeat Frost Giants", "Frost Giant", 5)
            ),
            rewards = QuestRewards(
                experience = 6000,
                gold = 1800
            ),
            lore = "The dwarves vanished overnight, leaving everything behind. What drove them away?"
        ),
        Quest(
            id = "quest_frost_tyrant",
            name = "Overthrow the Frost Tyrant",
            description = "Defeat the frost giant who claims to have driven out the dwarves.",
            questGiverId = "npc_irondelve_leader",
            location = "frostpeak_mountain_base",
            levelRequirement = 35,
            questType = QuestType.BOSS,
            objectives = listOf(
                QuestObjective("Defeat the Frost Tyrant", "Frost Tyrant", 1)
            ),
            rewards = QuestRewards(
                experience = 8000,
                gold = 2500,
                items = listOf("weapon_frostbite")
            )
        ),
        Quest(
            id = "quest_mountain_wisdom",
            name = "Seek the Hermit's Wisdom",
            description = "Climb to the Sky Monastery and seek ancient knowledge.",
            questGiverId = "npc_mountain_hermit",
            location = "frostpeak_sky_monastery",
            levelRequirement = 35,
            questType = QuestType.EXPLORE,
            objectives = listOf(
                QuestObjective("Meditate at the Sky Monastery", "frostpeak_sky_monastery", 1)
            ),
            rewards = QuestRewards(
                experience = 7000,
                gold = 0,
                items = listOf("accessory_first_wanderers_compass")
            ),
            lore = "The hermit has lived at the monastery longer than anyone can remember."
        ),

        // ==================== STORMCOAST REACHES QUESTS ====================
        Quest(
            id = "quest_storm_crown",
            name = "The Storm Crown Fragment",
            description = "Recover a fragment of the legendary Storm Crown.",
            questGiverId = "npc_stormwatch_mage",
            location = "stormcoast_lighthouse_point",
            levelRequirement = 40,
            questType = QuestType.COLLECT,
            objectives = listOf(
                QuestObjective("Explore the Drowned Cathedral", "stormcoast_drowned_cathedral", 1),
                QuestObjective("Defeat the Tide Caller", "Tide Caller", 1)
            ),
            rewards = QuestRewards(
                experience = 10000,
                gold = 3000,
                items = listOf("accessory_storm_crown_fragment")
            ),
            lore = "The Storm Crown is said to control the eternal storms. Finding all fragments might break the curse."
        ),
        Quest(
            id = "quest_ghost_fleet",
            name = "The Shattered Fleet's Treasure",
            description = "Brave the ghostly pirates and claim their treasure.",
            questGiverId = "npc_pirate_captain",
            location = "stormcoast_wreckers_cove",
            levelRequirement = 42,
            questType = QuestType.BOSS,
            objectives = listOf(
                QuestObjective("Defeat Ghost Pirates", "Ghost Pirate", 10),
                QuestObjective("Defeat Captain Dreadmoor", "Captain Dreadmoor", 1)
            ),
            rewards = QuestRewards(
                experience = 12000,
                gold = 5000,
                items = listOf("weapon_tidecaller_trident")
            )
        ),
        Quest(
            id = "quest_tempest",
            name = "Face the Storm Leviathan",
            description = "The ultimate challenge: confront Tempest itself.",
            questGiverId = "npc_stormwatch_mage",
            location = "stormcoast_lighthouse_point",
            levelRequirement = 50,
            questType = QuestType.BOSS,
            objectives = listOf(
                QuestObjective("Defeat Tempest", "Tempest", 1)
            ),
            rewards = QuestRewards(
                experience = 20000,
                gold = 10000,
                items = listOf("armor_wardens_aegis")
            ),
            lore = "Tempest IS the storm. Defeating it might finally free Aethermoor from the eternal tempest."
        )
    )

    /**
     * Get all quests
     */
    fun getAllQuests(): List<Quest> = quests

    /**
     * Get quest by ID
     */
    fun getQuestById(id: String): Quest? {
        return quests.find { it.id == id }
    }

    /**
     * Get quests available at a given level
     */
    fun getQuestsForLevel(level: Int): List<Quest> {
        return quests.filter { it.levelRequirement <= level }
    }

    /**
     * Get quests from a specific NPC
     */
    fun getQuestsFromNPC(npcId: String): List<Quest> {
        return quests.filter { it.questGiverId == npcId }
    }

    /**
     * Get quests in a location
     */
    fun getQuestsInLocation(locationId: String): List<Quest> {
        return quests.filter { it.location == locationId }
    }

    /**
     * Get random quest appropriate for level
     */
    fun getRandomQuestForLevel(level: Int): Quest? {
        val availableQuests = getQuestsForLevel(level).filter {
            it.levelRequirement >= level - 5 // Not too low level
        }
        return availableQuests.randomOrNull()
    }
}
