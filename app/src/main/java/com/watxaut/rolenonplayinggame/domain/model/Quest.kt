package com.watxaut.rolenonplayinggame.domain.model

/**
 * Quest model for autonomous quest completion.
 * Based on FUNCTIONAL_DESIGN_DOCUMENT.md - Quest system
 */
data class Quest(
    val id: String,
    val name: String,
    val description: String,
    val questGiverId: String,
    val location: String,
    val levelRequirement: Int,
    val questType: QuestType,
    val objectives: List<QuestObjective>,
    val rewards: QuestRewards,
    val lore: String? = null
)

enum class QuestType {
    KILL,          // Kill X enemies
    EXPLORE,       // Discover location
    COLLECT,       // Gather items
    DELIVERY,      // Deliver item to NPC
    BOSS,          // Defeat a boss enemy
    CHAIN          // Part of a quest chain
}

data class QuestObjective(
    val description: String,
    val target: String,          // Enemy name, location ID, or item ID
    val quantity: Int = 1,
    val currentProgress: Int = 0
) {
    fun isComplete(): Boolean = currentProgress >= quantity

    fun getProgressString(): String = "$currentProgress/$quantity"
}

data class QuestRewards(
    val experience: Long,
    val gold: Int,
    val items: List<String> = emptyList(), // Item IDs
    val reputation: Int = 0 // Future: faction reputation
)

/**
 * Active quest progress for a character
 */
data class ActiveQuest(
    val questId: String,
    val objectives: List<QuestObjective>,
    val acceptedAt: Long = System.currentTimeMillis()
) {
    fun isComplete(): Boolean = objectives.all { it.isComplete() }

    fun getCompletionPercentage(): Int {
        if (objectives.isEmpty()) return 0
        val completed = objectives.count { it.isComplete() }
        return (completed * 100) / objectives.size
    }
}
