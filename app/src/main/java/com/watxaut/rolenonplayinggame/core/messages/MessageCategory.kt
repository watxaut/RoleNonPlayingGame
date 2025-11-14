package com.watxaut.rolenonplayinggame.core.messages

/**
 * Categories of messages for different game situations.
 */
enum class MessageCategory {
    // Combat outcomes
    COMBAT_VICTORY,         // Character wins fight
    COMBAT_CRITICAL_WIN,    // Character wins with critical roll (21)
    COMBAT_DEFEAT,          // Character loses and dies
    COMBAT_CRITICAL_DEFEAT, // Character loses with critical failure (1)
    COMBAT_FLEE,            // Character escapes but doesn't win

    // Resting
    REST_FULL,              // Full HP recovery
    REST_PARTIAL,           // Partial HP recovery

    // Looting
    LOOT_EXCELLENT,         // Great loot found
    LOOT_GOOD,              // Decent loot
    LOOT_POOR,              // Minimal loot
    LOOT_NOTHING            // No loot found
}

/**
 * A single message template with placeholder support.
 *
 * Placeholders:
 * - {character} = character name
 * - {enemy} = enemy name
 * - {gold} = gold amount
 * - {item} = item name
 * - {hp} = HP amount
 * - {location} = location name
 */
data class Message(
    val template: String,
    val category: MessageCategory
)

/**
 * Substitutes placeholders in a message template with actual values.
 */
fun Message.substitute(
    character: String? = null,
    enemy: String? = null,
    gold: Int? = null,
    item: String? = null,
    hp: Int? = null,
    location: String? = null
): String {
    var result = template

    character?.let { result = result.replace("{character}", it) }
    enemy?.let { result = result.replace("{enemy}", it) }
    gold?.let { result = result.replace("{gold}", it.toString()) }
    item?.let { result = result.replace("{item}", it) }
    hp?.let { result = result.replace("{hp}", it.toString()) }
    location?.let { result = result.replace("{location}", it) }

    return result
}
