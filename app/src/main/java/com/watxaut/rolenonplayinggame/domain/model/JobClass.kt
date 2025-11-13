package com.watxaut.rolenonplayinggame.domain.model

/**
 * Character job classes as defined in the functional design document.
 * Each class has different stat priorities and behaviors.
 */
enum class JobClass(
    val displayName: String,
    val description: String,
    val primaryStat: StatType,
    val secondaryStat: StatType
) {
    WARRIOR(
        "Warrior",
        "Strong melee combatant with high vitality",
        StatType.STRENGTH,
        StatType.VITALITY
    ),
    ASSASSIN(
        "Assassin",
        "Swift striker focusing on critical hits",
        StatType.AGILITY,
        StatType.LUCK
    ),
    ROGUE(
        "Rogue",
        "Cunning adventurer skilled in stealth and luck",
        StatType.AGILITY,
        StatType.LUCK
    ),
    ARCHER(
        "Archer",
        "Ranged attacker with precision",
        StatType.AGILITY,
        StatType.STRENGTH
    ),
    MAGE(
        "Mage",
        "Master of arcane magic",
        StatType.INTELLIGENCE,
        StatType.VITALITY
    ),
    PRIEST(
        "Priest",
        "Holy healer and support",
        StatType.INTELLIGENCE,
        StatType.CHARISMA
    ),
    WARLOCK(
        "Warlock",
        "Dark magic wielder",
        StatType.INTELLIGENCE,
        StatType.CHARISMA
    ),
    BARD(
        "Bard",
        "Charismatic performer and buffer",
        StatType.CHARISMA,
        StatType.LUCK
    ),
    MERCHANT(
        "Merchant",
        "Trader focused on wealth accumulation",
        StatType.CHARISMA,
        StatType.LUCK
    ),
    SCHOLAR(
        "Scholar",
        "Knowledge seeker and lore master",
        StatType.INTELLIGENCE,
        StatType.VITALITY
    ),
    PALADIN(
        "Paladin",
        "Holy warrior combining strength and faith",
        StatType.STRENGTH,
        StatType.CHARISMA
    ),
    BATTLE_MAGE(
        "Battle Mage",
        "Hybrid fighter combining magic and melee",
        StatType.INTELLIGENCE,
        StatType.STRENGTH
    ),
    RANGER(
        "Ranger",
        "Wilderness expert and tracker",
        StatType.AGILITY,
        StatType.VITALITY
    );

    fun getStatAllocationWeights(): Map<StatType, Float> {
        return mapOf(
            primaryStat to 0.5f,
            secondaryStat to 0.3f
        )
    }
}

/**
 * Character stat types
 */
enum class StatType {
    STRENGTH,    // Physical power, melee damage
    INTELLIGENCE, // Magic power, spell effectiveness
    AGILITY,     // Speed, dodge, ranged accuracy
    LUCK,        // Critical hit chance, loot quality, rerolls
    CHARISMA,    // Social interactions, trade prices
    VITALITY     // Health points, defense
}
