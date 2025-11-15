package com.watxaut.rolenonplayinggame.core.messages

import kotlin.random.Random

/**
 * Provides randomized messages for game events with placeholder substitution.
 *
 * Currently uses local messages, but designed to support remote message fetching
 * from Supabase in the future.
 */
class MessageProvider(
    private val random: Random = Random.Default
) {

    /**
     * Get a random message for combat victory (normal win).
     */
    fun getCombatVictoryMessage(
        characterName: String,
        enemyName: String
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.COMBAT_VICTORY)
        val message = messages.random(random)
        return message.substitute(character = characterName, enemy = enemyName)
    }

    /**
     * Get a random message for critical combat victory (roll 21).
     */
    fun getCombatCriticalWinMessage(
        characterName: String,
        enemyName: String
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.COMBAT_CRITICAL_WIN)
        val message = messages.random(random)
        return message.substitute(character = characterName, enemy = enemyName)
    }

    /**
     * Get a random message for combat defeat (death).
     */
    fun getCombatDefeatMessage(
        characterName: String,
        enemyName: String
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.COMBAT_DEFEAT)
        val message = messages.random(random)
        return message.substitute(character = characterName, enemy = enemyName)
    }

    /**
     * Get a random message for critical combat defeat (roll 1).
     */
    fun getCombatCriticalDefeatMessage(
        characterName: String,
        enemyName: String
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.COMBAT_CRITICAL_DEFEAT)
        val message = messages.random(random)
        return message.substitute(character = characterName, enemy = enemyName)
    }

    /**
     * Get a random message for fleeing combat.
     */
    fun getCombatFleeMessage(
        characterName: String,
        enemyName: String
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.COMBAT_FLEE)
        val message = messages.random(random)
        return message.substitute(character = characterName, enemy = enemyName)
    }

    /**
     * Get a random message for full rest/recovery.
     */
    fun getRestFullMessage(
        characterName: String,
        hpRecovered: Int
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.REST_FULL)
        val message = messages.random(random)
        return message.substitute(character = characterName, hp = hpRecovered)
    }

    /**
     * Get a random message for partial rest/recovery.
     */
    fun getRestPartialMessage(
        characterName: String,
        hpRecovered: Int
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.REST_PARTIAL)
        val message = messages.random(random)
        return message.substitute(character = characterName, hp = hpRecovered)
    }

    /**
     * Get a random message for excellent loot.
     */
    fun getLootExcellentMessage(
        characterName: String,
        gold: Int,
        itemName: String? = null
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.LOOT_EXCELLENT)
        val message = messages.random(random)
        return message.substitute(
            character = characterName,
            gold = gold,
            item = itemName ?: "rare treasure"
        )
    }

    /**
     * Get a random message for good loot.
     */
    fun getLootGoodMessage(
        characterName: String,
        gold: Int
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.LOOT_GOOD)
        val message = messages.random(random)
        return message.substitute(character = characterName, gold = gold)
    }

    /**
     * Get a random message for poor loot.
     */
    fun getLootPoorMessage(
        characterName: String,
        gold: Int
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.LOOT_POOR)
        val message = messages.random(random)
        return message.substitute(character = characterName, gold = gold)
    }

    /**
     * Get a random message for finding no loot.
     */
    fun getLootNothingMessage(
        characterName: String
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.LOOT_NOTHING)
        val message = messages.random(random)
        return message.substitute(character = characterName)
    }

    /**
     * Get a random message for visiting a location.
     */
    fun getExplorationVisitMessage(
        characterName: String,
        locationName: String
    ): String {
        val messages = LocalMessageRepository.getMessagesForCategory(MessageCategory.EXPLORATION_VISIT)
        val message = messages.random(random)
        return message.substitute(character = characterName, location = locationName)
    }

    /**
     * Get a combat message based on outcome and whether it was a critical roll.
     */
    fun getCombatMessage(
        outcome: com.watxaut.rolenonplayinggame.core.combat.CombatOutcome,
        characterName: String,
        enemyName: String,
        wasCritical: Boolean
    ): String {
        return when (outcome) {
            com.watxaut.rolenonplayinggame.core.combat.CombatOutcome.WIN -> {
                if (wasCritical) {
                    getCombatCriticalWinMessage(characterName, enemyName)
                } else {
                    getCombatVictoryMessage(characterName, enemyName)
                }
            }
            com.watxaut.rolenonplayinggame.core.combat.CombatOutcome.DEATH -> {
                if (wasCritical) {
                    getCombatCriticalDefeatMessage(characterName, enemyName)
                } else {
                    getCombatDefeatMessage(characterName, enemyName)
                }
            }
            com.watxaut.rolenonplayinggame.core.combat.CombatOutcome.FLEE -> {
                getCombatFleeMessage(characterName, enemyName)
            }
        }
    }
}
