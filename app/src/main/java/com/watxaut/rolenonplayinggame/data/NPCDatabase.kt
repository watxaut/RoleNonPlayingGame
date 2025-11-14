package com.watxaut.rolenonplayinggame.data

import com.watxaut.rolenonplayinggame.domain.model.NPC
import com.watxaut.rolenonplayinggame.domain.model.NPCPersonality
import com.watxaut.rolenonplayinggame.domain.model.NPCType

/**
 * NPC Database for all regions of Aethermoor.
 * Based on TONE_AND_STYLE_GUIDE.md - NPC dialogue patterns
 *
 * Phase 3 - World & Content Implementation
 */
object NPCDatabase {

    private val npcs = listOf(
        // ==================== HEARTLANDS NPCs ====================
        NPC(
            id = "npc_elder_marlow",
            name = "Elder Marlow",
            title = "Village Elder",
            location = "heartlands_havenmoor",
            npcType = NPCType.QUEST_GIVER,
            personality = NPCPersonality.WISE,
            description = "The ancient elder of Havenmoor. Has seen countless adventurers come and go.",
            greeting = "Welcome, young wanderer. The island chose you for a reason.",
            questIds = listOf("quest_first_steps", "quest_goblin_problem")
        ),
        NPC(
            id = "npc_blacksmith_thorne",
            name = "Thorne",
            title = "Master Blacksmith",
            location = "heartlands_havenmoor",
            npcType = NPCType.MERCHANT,
            personality = NPCPersonality.GRUFF,
            description = "A burly dwarf who runs the town smithy. His work is legendary.",
            greeting = "Need a blade? I've got the best steel on the island.",
            shopInventory = listOf(
                "weapon_iron_blade",
                "armor_iron_chainmail",
                "consumable_health_potion"
            )
        ),
        NPC(
            id = "npc_innkeeper_rosie",
            name = "Rosie",
            title = "Innkeeper",
            location = "heartlands_havenmoor",
            npcType = NPCType.INNKEEPER,
            personality = NPCPersonality.FRIENDLY,
            description = "The warm-hearted owner of the Wanderer's Rest inn.",
            greeting = "Come in, come in! Rest your weary feet.",
            questIds = listOf("quest_lost_delivery")
        ),
        NPC(
            id = "npc_merchant_felix",
            name = "Felix the Fortunate",
            title = "Traveling Merchant",
            location = "heartlands_millers_rest",
            npcType = NPCType.MERCHANT,
            personality = NPCPersonality.ECCENTRIC,
            description = "A peculiar merchant with unusual wares. Claims to be blessed by luck itself.",
            greeting = "Fortune smiles upon those who shop wisely!",
            shopInventory = listOf(
                "accessory_silver_ring",
                "consumable_greater_health_potion"
            )
        ),

        // ==================== THORNWOOD WILDS NPCs ====================
        NPC(
            id = "npc_ranger_captain_silva",
            name = "Captain Silva",
            title = "Ranger Captain",
            location = "thornwood_thornguard_outpost",
            npcType = NPCType.BOTH,
            personality = NPCPersonality.PROFESSIONAL,
            description = "Leader of the Thornguard Rangers. Knows the forest better than anyone.",
            greeting = "The Thornwood is dangerous. Stay alert, or don't stay at all.",
            questIds = listOf("quest_widow_threat", "quest_heart_tree"),
            shopInventory = listOf(
                "weapon_hunters_bow",
                "consumable_health_potion"
            )
        ),
        NPC(
            id = "npc_druid_moonwhisper",
            name = "Moonwhisper",
            title = "Forest Druid",
            location = "thornwood_thornguard_outpost",
            npcType = NPCType.LOREKEEPER,
            personality = NPCPersonality.MYSTERIOUS,
            description = "An enigmatic druid who speaks in riddles about the forest's secrets.",
            greeting = "The trees remember what mortals forget...",
            questIds = listOf("quest_corrupted_grove")
        ),

        // ==================== ASHENVEIL DESERT NPCs ====================
        NPC(
            id = "npc_sandstrider_chief",
            name = "Chief Zahara",
            title = "Sandstrider Chieftain",
            location = "ashenveil_sandstone_bazaar",
            npcType = NPCType.BOTH,
            personality = NPCPersonality.WISE,
            description = "Leader of the Sandstrider Clans. Knows every oasis and hidden path.",
            greeting = "The desert provides for those who respect it.",
            questIds = listOf("quest_bone_empress", "quest_desert_secrets"),
            shopInventory = listOf(
                "consumable_greater_health_potion",
                "accessory_scholars_pendant"
            )
        ),

        // ==================== FROSTPEAK MOUNTAINS NPCs ====================
        NPC(
            id = "npc_irondelve_leader",
            name = "Borin Irondelve",
            title = "Reclaimer Leader",
            location = "frostpeak_mountain_base",
            npcType = NPCType.QUEST_GIVER,
            personality = NPCPersonality.GRUFF,
            description = "Descendant of the vanished dwarves. Determined to reclaim his ancestors' hold.",
            greeting = "The mountains remember the dwarves. We'll reclaim what's ours.",
            questIds = listOf("quest_lost_hold", "quest_frost_tyrant")
        ),
        NPC(
            id = "npc_mountain_hermit",
            name = "The Hermit",
            title = "Sky Monastery Keeper",
            location = "frostpeak_sky_monastery",
            npcType = NPCType.LOREKEEPER,
            personality = NPCPersonality.WISE,
            description = "An ancient monk who has lived in solitude for decades.",
            greeting = "You have climbed far, seeker. What answers do you seek?",
            questIds = listOf("quest_mountain_wisdom")
        ),

        // ==================== STORMCOAST REACHES NPCs ====================
        NPC(
            id = "npc_stormwatch_mage",
            name = "Magister Tempus",
            title = "Stormwatch Leader",
            location = "stormcoast_lighthouse_point",
            npcType = NPCType.QUEST_GIVER,
            personality = NPCPersonality.PROFESSIONAL,
            description = "Chief researcher of the Stormwatch. Obsessed with breaking the eternal storms.",
            greeting = "The storms hide secrets. We will uncover them all.",
            questIds = listOf("quest_storm_crown", "quest_tempest")
        ),
        NPC(
            id = "npc_pirate_captain",
            name = "Captain Redbeard",
            title = "Pirate Lord",
            location = "stormcoast_wreckers_cove",
            npcType = NPCType.BOTH,
            personality = NPCPersonality.GRUFF,
            description = "The notorious leader of Wrecker's Cove. Respect the code, or walk the plank.",
            greeting = "Yarr! Welcome to Wrecker's Cove. Mind the rules, or feed the fish.",
            questIds = listOf("quest_ghost_fleet"),
            shopInventory = listOf(
                "weapon_stormcaller",
                "accessory_storm_crown_fragment"
            )
        )
    )

    /**
     * Get all NPCs
     */
    fun getAllNPCs(): List<NPC> = npcs

    /**
     * Get NPC by ID
     */
    fun getNPCById(id: String): NPC? {
        return npcs.find { it.id == id }
    }

    /**
     * Get NPCs in a specific location
     */
    fun getNPCsInLocation(locationId: String): List<NPC> {
        return npcs.filter { it.location == locationId }
    }

    /**
     * Get quest givers
     */
    fun getQuestGivers(): List<NPC> {
        return npcs.filter {
            it.npcType == NPCType.QUEST_GIVER || it.npcType == NPCType.BOTH
        }
    }

    /**
     * Get merchants
     */
    fun getMerchants(): List<NPC> {
        return npcs.filter {
            it.npcType == NPCType.MERCHANT || it.npcType == NPCType.BOTH
        }
    }

    /**
     * Get NPCs offering quests
     */
    fun getNPCsWithQuests(): List<NPC> {
        return npcs.filter { it.questIds.isNotEmpty() }
    }
}
