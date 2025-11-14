package com.watxaut.rolenonplayinggame.domain.model

/**
 * Non-Player Character (NPC) model.
 * Based on TONE_AND_STYLE_GUIDE.md for dialogue patterns
 */
data class NPC(
    val id: String,
    val name: String,
    val title: String,
    val location: String,
    val npcType: NPCType,
    val personality: NPCPersonality,
    val description: String,
    val greeting: String,
    val questIds: List<String> = emptyList(),
    val shopInventory: List<String> = emptyList() // Item IDs
)

enum class NPCType {
    QUEST_GIVER,    // Offers quests
    MERCHANT,       // Sells items
    INNKEEPER,      // Offers rest and information
    TRAINER,        // Trains skills (future)
    LOREKEEPER,     // Provides world lore
    BOTH            // Quest giver + merchant
}

enum class NPCPersonality {
    FRIENDLY,       // Warm and welcoming
    GRUFF,          // Rough but helpful
    MYSTERIOUS,     // Cryptic and enigmatic
    ECCENTRIC,      // Quirky and unusual
    PROFESSIONAL,   // Formal and business-like
    WISE            // Ancient and knowledgeable
}
