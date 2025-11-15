package com.watxaut.rolenonplayinggame.domain.model

/**
 * Lore Discovery system for tracking what lore the player has discovered.
 * Per user requirements:
 * - Lore tab shows world info, hero history, and discoveries
 * - Lore is revealed through principal mission steps and boss battles
 */
data class LoreDiscovery(
    val id: String,
    val category: LoreCategory,
    val title: String,
    val content: String,
    val discoveredAt: Long = System.currentTimeMillis(),
    val sourceType: LoreSourceType,
    val sourceId: String // Mission ID, boss ID, etc.
)

/**
 * Categories of lore for organization
 */
enum class LoreCategory(val displayName: String) {
    WORLD_HISTORY("World History"),
    REGIONS("Regions of Aethermoor"),
    FACTIONS("Factions & Settlements"),
    MYSTERIES("Mysteries"),
    LEGENDARY_CREATURES("Legendary Creatures"),
    HERO_JOURNEY("Hero's Journey"),
    ARTIFACTS("Artifacts & Relics")
}

/**
 * Source of lore discovery
 */
enum class LoreSourceType {
    PRINCIPAL_MISSION_STEP,
    BOSS_BATTLE,
    LOCATION_DISCOVERY,
    ITEM_ACQUIRED,
    NPC_INTERACTION
}

/**
 * Predefined lore entries for the world (based on WORLD_LORE.md)
 */
object PredefinedLore {

    /**
     * World overview lore - available from the start
     */
    val AETHERMOOR_OVERVIEW = LoreEntry(
        id = "lore_aethermoor_overview",
        category = LoreCategory.WORLD_HISTORY,
        title = "The Island of Aethermoor",
        content = """
            Aethermoor is a vast island continent, isolated from the wider world by treacherous seas and perpetual storms.
            Those who arrive here—whether by shipwreck, portal, or mysterious summoning—find themselves unable to leave.
            The island is a place of ancient magic, forgotten ruins, and untold dangers, where adventurers forge their own destinies.

            The island spans five distinct regions, each with its own geography, dangers, and secrets waiting to be discovered.
        """.trimIndent(),
        unlockedByDefault = true
    )

    val THE_CALLING = LoreEntry(
        id = "lore_the_calling",
        category = LoreCategory.MYSTERIES,
        title = "The Calling",
        content = """
            Every adventurer who arrives on Aethermoor reports the same experience—a pull, a calling, a sense that they were meant to be here.
            Some arrived by accident, others by design, but all feel the island chose them.

            What purpose does the island have for those it summons?
        """.trimIndent(),
        unlockedByDefault = true
    )

    /**
     * Regional lore - unlocked when visiting each region
     */
    val HEARTLANDS_LORE = LoreEntry(
        id = "lore_heartlands",
        category = LoreCategory.REGIONS,
        title = "The Heartlands",
        content = """
            The Heartlands surround Havenmoor and represent the safest territory on Aethermoor.
            Here, new adventurers take their first steps, encountering minor threats like wolves, goblins, and bandits.

            Ancient standing stones hidden in the fields hint at the island's mysterious past.
            Some say they mark ley lines of power that crisscross the entire island.
        """.trimIndent(),
        unlockedByDefault = false,
        unlockCondition = "Visit the Heartlands"
    )

    val THORNWOOD_LORE = LoreEntry(
        id = "lore_thornwood",
        category = LoreCategory.REGIONS,
        title = "The Thornwood Wilds",
        content = """
            West of the Heartlands lies the Thornwood, a vast and ancient forest where sunlight barely penetrates the thick canopy.
            The trees here grow twisted and gnarled, and the paths shift when unwatched.

            Deep within the forest lies the Heart Tree, an ancient oak said to be as old as the island itself.
            Those who find it claim to hear whispers of forgotten knowledge.
        """.trimIndent(),
        unlockedByDefault = false,
        unlockCondition = "Visit the Thornwood Wilds"
    )

    val ASHENVEIL_LORE = LoreEntry(
        id = "lore_ashenveil",
        category = LoreCategory.REGIONS,
        title = "The Ashenveil Desert",
        content = """
            The southern reaches of Aethermoor are dominated by the Ashenveil, a harsh desert born from the Sundering's flames.
            Once a fertile land, it was scorched into lifeless sand and stone.

            Beneath the sands lie the ruins of a pre-Sundering civilization. Treasures of immense power are said to rest in vaults
            protected by ancient guardians and deadly traps.
        """.trimIndent(),
        unlockedByDefault = false,
        unlockCondition = "Visit the Ashenveil Desert"
    )

    val FROSTPEAK_LORE = LoreEntry(
        id = "lore_frostpeak",
        category = LoreCategory.REGIONS,
        title = "The Frostpeak Mountains",
        content = """
            The northern spine of Aethermoor is dominated by the Frostpeak range, mountains so tall their peaks disappear into the clouds.
            Ancient dwarven halls are carved into the mountain's heart.

            The dwarves who once lived here vanished overnight. Their forges, still warm, and their tables, still set for meals,
            suggest something catastrophic and sudden drove them away—or worse.
        """.trimIndent(),
        unlockedByDefault = false,
        unlockCondition = "Visit the Frostpeak Mountains"
    )

    val STORMCOAST_LORE = LoreEntry(
        id = "lore_stormcoast",
        category = LoreCategory.REGIONS,
        title = "The Stormcoast Reaches",
        content = """
            The eastern edge of Aethermoor is a place of perpetual tempest. Massive waves crash against jagged cliffs,
            and lightning splits the sky day and night.

            Some believe the storms are not natural but maintained by something—or someone—to keep Aethermoor isolated.
            Ancient texts speak of a Storm Crown artifact that might control the tempests, but none have found it.
        """.trimIndent(),
        unlockedByDefault = false,
        unlockCondition = "Visit the Stormcoast Reaches"
    )

    /**
     * Get all default lore entries that should be unlocked from the start
     */
    fun getDefaultUnlockedLore(): List<LoreEntry> {
        return listOf(AETHERMOOR_OVERVIEW, THE_CALLING)
    }
}

/**
 * Template for lore entries
 */
data class LoreEntry(
    val id: String,
    val category: LoreCategory,
    val title: String,
    val content: String,
    val unlockedByDefault: Boolean = false,
    val unlockCondition: String = ""
)
