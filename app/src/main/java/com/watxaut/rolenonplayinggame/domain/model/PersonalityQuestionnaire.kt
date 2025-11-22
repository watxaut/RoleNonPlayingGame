package com.watxaut.rolenonplayinggame.domain.model

import kotlin.random.Random

/**
 * The complete personality questionnaire for character creation
 * Contains 25 questions mixing personality assessment with D&D-style humor
 */
object PersonalityQuestionnaire {

    /**
     * All 25 personality questions
     * Players will be shown 5 random questions during character creation
     */
    val allQuestions = listOf(
        // Question 1: Tavern Encounter (social, courage, aggression)
        PersonalityQuestion(
            id = "tavern_lizard",
            text = "A half-human, half-lizard approaches you in a tavern. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Hit on them",
                    effects = PersonalityEffects(social = 0.2f, courage = 0.15f, aggression = -0.1f)
                ),
                QuestionOption(
                    text = "Ask where the other half went",
                    effects = PersonalityEffects(curiosity = 0.2f, impulsive = 0.15f, aggression = -0.15f)
                ),
                QuestionOption(
                    text = "Invite them to a drink",
                    effects = PersonalityEffects(social = 0.25f, aggression = -0.2f)
                ),
                QuestionOption(
                    text = "Challenge them to arm-wrestling",
                    effects = PersonalityEffects(courage = 0.2f, aggression = 0.25f)
                ),
                QuestionOption(
                    text = "Pretend not to notice",
                    effects = PersonalityEffects(social = -0.25f, courage = -0.2f)
                )
            )
        ),

        // Question 2: Found Treasure (greed, impulsive)
        PersonalityQuestion(
            id = "dungeon_chest",
            text = "You find a chest of gold in an abandoned dungeon. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Take it all immediately",
                    effects = PersonalityEffects(greed = 0.3f, impulsive = 0.25f)
                ),
                QuestionOption(
                    text = "Check for traps first",
                    effects = PersonalityEffects(impulsive = -0.25f)
                ),
                QuestionOption(
                    text = "Leave it, it's probably cursed",
                    effects = PersonalityEffects(greed = -0.2f, impulsive = -0.15f)
                ),
                QuestionOption(
                    text = "Take half and leave the rest",
                    effects = PersonalityEffects(greed = -0.1f)
                ),
                QuestionOption(
                    text = "Yell 'IS ANYONE MISSING A CHEST?'",
                    effects = PersonalityEffects(social = 0.25f, greed = -0.25f, impulsive = 0.2f)
                )
            )
        ),

        // Question 3: Party Formation (social) - SCALE
        PersonalityQuestion(
            id = "companions",
            text = "How much do you agree: 'Adventures are better with companions'",
            type = QuestionType.SCALE
        ),

        // Question 4: Dragon Risk (courage, impulsive, greed)
        PersonalityQuestion(
            id = "sleeping_dragon",
            text = "A dragon sleeps on a pile of treasure. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Sneak past quietly",
                    effects = PersonalityEffects(courage = -0.15f, impulsive = -0.2f, greed = -0.1f)
                ),
                QuestionOption(
                    text = "Try to steal some treasure",
                    effects = PersonalityEffects(courage = 0.25f, greed = 0.25f, impulsive = 0.2f)
                ),
                QuestionOption(
                    text = "Wake it up to fight honorably",
                    effects = PersonalityEffects(courage = 0.3f, aggression = 0.25f, impulsive = -0.15f)
                ),
                QuestionOption(
                    text = "Run away",
                    effects = PersonalityEffects(courage = -0.3f)
                ),
                QuestionOption(
                    text = "Attempt to befriend it",
                    effects = PersonalityEffects(social = 0.25f, curiosity = 0.2f, aggression = -0.25f)
                )
            )
        ),

        // Question 5: Exploration (curiosity) - SCALE
        PersonalityQuestion(
            id = "unexplored_path",
            text = "How much do you agree: 'The best path is the unexplored one'",
            type = QuestionType.SCALE
        ),

        // Question 6: Orc Duel (aggression, courage, social)
        PersonalityQuestion(
            id = "orc_duel",
            text = "An orc challenges you to a duel. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Accept immediately",
                    effects = PersonalityEffects(aggression = 0.25f, courage = 0.25f)
                ),
                QuestionOption(
                    text = "Try to talk them down",
                    effects = PersonalityEffects(aggression = -0.25f, social = 0.2f)
                ),
                QuestionOption(
                    text = "Suggest a drinking contest instead",
                    effects = PersonalityEffects(aggression = -0.2f, social = 0.25f)
                ),
                QuestionOption(
                    text = "Ignore them and walk away",
                    effects = PersonalityEffects(aggression = -0.2f, social = -0.15f, courage = -0.1f)
                ),
                QuestionOption(
                    text = "Accept, but plan to cheat",
                    effects = PersonalityEffects(aggression = 0.2f, impulsive = -0.15f, greed = 0.15f)
                )
            )
        ),

        // Question 7: Starving Beggar (greed, social) - YES/NO
        PersonalityQuestion(
            id = "starving_beggar",
            text = "A starving beggar asks for food. Do you share?",
            type = QuestionType.YES_NO,
            options = listOf(
                QuestionOption(
                    text = "Yes",
                    effects = PersonalityEffects(greed = -0.25f, social = 0.2f)
                ),
                QuestionOption(
                    text = "No",
                    effects = PersonalityEffects(greed = 0.25f, social = -0.2f)
                )
            )
        ),

        // Question 8: Planning (impulsive) - SCALE
        PersonalityQuestion(
            id = "planning",
            text = "How much do you agree: 'Plans are for people who lack confidence'",
            type = QuestionType.SCALE
        ),

        // Question 9: Glowing Orb (curiosity, impulsive)
        PersonalityQuestion(
            id = "glowing_orb",
            text = "You find a mysterious glowing orb. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Touch it immediately",
                    effects = PersonalityEffects(impulsive = 0.3f, curiosity = 0.25f, courage = 0.15f)
                ),
                QuestionOption(
                    text = "Study it from a safe distance",
                    effects = PersonalityEffects(curiosity = 0.25f, impulsive = -0.25f)
                ),
                QuestionOption(
                    text = "Ignore it, focus on your quest",
                    effects = PersonalityEffects(curiosity = -0.25f, impulsive = -0.15f)
                ),
                QuestionOption(
                    text = "Sell it to the highest bidder",
                    effects = PersonalityEffects(greed = 0.3f, curiosity = -0.2f)
                ),
                QuestionOption(
                    text = "Ask others what they think first",
                    effects = PersonalityEffects(social = 0.25f, impulsive = -0.2f)
                )
            )
        ),

        // Question 10: Loot Priority (greed, social)
        PersonalityQuestion(
            id = "legendary_loot",
            text = "After defeating a boss, you find legendary loot. Your party member needs it more. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Take it anyway, you earned it",
                    effects = PersonalityEffects(greed = 0.3f, social = -0.25f)
                ),
                QuestionOption(
                    text = "Give it to them",
                    effects = PersonalityEffects(greed = -0.3f, social = 0.3f)
                ),
                QuestionOption(
                    text = "Suggest rolling dice for it",
                    effects = PersonalityEffects(social = 0.1f)
                ),
                QuestionOption(
                    text = "Trade it for something else",
                    effects = PersonalityEffects(greed = 0.15f, social = 0.1f)
                ),
                QuestionOption(
                    text = "Offer to share the benefits",
                    effects = PersonalityEffects(greed = -0.15f, social = 0.25f)
                )
            )
        ),

        // Question 11: Fortune Favors Bold (courage) - SCALE
        PersonalityQuestion(
            id = "fortune_favors_bold",
            text = "How much do you agree: 'Fortune favors the bold'",
            type = QuestionType.SCALE
        ),

        // Question 12: Bandits (courage, aggression, social)
        PersonalityQuestion(
            id = "bandits",
            text = "You witness bandits robbing merchants. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Charge in to help",
                    effects = PersonalityEffects(courage = 0.25f, aggression = 0.2f, impulsive = 0.15f)
                ),
                QuestionOption(
                    text = "Sneak around to ambush them",
                    effects = PersonalityEffects(courage = 0.2f, impulsive = -0.15f, aggression = 0.15f)
                ),
                QuestionOption(
                    text = "Alert the town guards",
                    effects = PersonalityEffects(social = 0.2f, courage = -0.1f, impulsive = -0.1f)
                ),
                QuestionOption(
                    text = "Keep walking, not your problem",
                    effects = PersonalityEffects(courage = -0.25f, social = -0.25f, aggression = -0.15f)
                ),
                QuestionOption(
                    text = "Try to negotiate peace",
                    effects = PersonalityEffects(aggression = -0.25f, social = 0.25f, courage = 0.1f)
                )
            )
        ),

        // Question 13: Mysterious Quest (curiosity, impulsive)
        PersonalityQuestion(
            id = "hooded_figure",
            text = "A hooded figure offers you a quest with no details. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Accept immediately, sounds exciting!",
                    effects = PersonalityEffects(impulsive = 0.3f, curiosity = 0.25f)
                ),
                QuestionOption(
                    text = "Ask for more information first",
                    effects = PersonalityEffects(curiosity = 0.2f, impulsive = -0.25f)
                ),
                QuestionOption(
                    text = "Decline politely",
                    effects = PersonalityEffects(curiosity = -0.2f, social = 0.15f)
                ),
                QuestionOption(
                    text = "Ignore them completely",
                    effects = PersonalityEffects(social = -0.25f, curiosity = -0.25f)
                ),
                QuestionOption(
                    text = "Demand payment upfront",
                    effects = PersonalityEffects(greed = 0.25f, impulsive = -0.15f)
                )
            )
        ),

        // Question 14: Sabotage Rival (aggression, greed) - YES/NO
        PersonalityQuestion(
            id = "sabotage",
            text = "Would you sabotage a rival adventurer to get ahead?",
            type = QuestionType.YES_NO,
            options = listOf(
                QuestionOption(
                    text = "Yes",
                    effects = PersonalityEffects(aggression = 0.25f, greed = 0.25f, social = -0.2f)
                ),
                QuestionOption(
                    text = "No",
                    effects = PersonalityEffects(aggression = -0.25f, greed = -0.15f, social = 0.15f)
                )
            )
        ),

        // Question 15: Trap Ahead (impulsive, courage)
        PersonalityQuestion(
            id = "trap_ahead",
            text = "You suspect a trap ahead. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Rush forward anyway",
                    effects = PersonalityEffects(impulsive = 0.3f, courage = 0.2f)
                ),
                QuestionOption(
                    text = "Carefully check for traps first",
                    effects = PersonalityEffects(impulsive = -0.3f)
                ),
                QuestionOption(
                    text = "Send your companion first",
                    effects = PersonalityEffects(courage = -0.25f, social = -0.15f)
                ),
                QuestionOption(
                    text = "Find another way",
                    effects = PersonalityEffects(courage = -0.15f, curiosity = 0.1f)
                ),
                QuestionOption(
                    text = "Trigger it from a distance",
                    effects = PersonalityEffects(impulsive = -0.2f, curiosity = 0.15f)
                )
            )
        ),

        // Question 16: Tavern Brawl (aggression, social)
        PersonalityQuestion(
            id = "bar_fight",
            text = "A bar fight breaks out. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Join in for fun!",
                    effects = PersonalityEffects(aggression = 0.25f, impulsive = 0.25f)
                ),
                QuestionOption(
                    text = "Break it up and calm everyone down",
                    effects = PersonalityEffects(aggression = -0.25f, social = 0.25f)
                ),
                QuestionOption(
                    text = "Bet on who will win",
                    effects = PersonalityEffects(greed = 0.2f, aggression = -0.15f)
                ),
                QuestionOption(
                    text = "Slip out quietly",
                    effects = PersonalityEffects(aggression = -0.2f, social = -0.15f, courage = -0.1f)
                ),
                QuestionOption(
                    text = "Protect the innocent bystanders",
                    effects = PersonalityEffects(courage = 0.2f, aggression = -0.15f, social = 0.15f)
                )
            )
        ),

        // Question 17: Do Not Enter (curiosity, impulsive) - YES/NO
        PersonalityQuestion(
            id = "do_not_enter",
            text = "There's a door marked 'DO NOT ENTER'. Do you enter?",
            type = QuestionType.YES_NO,
            options = listOf(
                QuestionOption(
                    text = "Yes",
                    effects = PersonalityEffects(curiosity = 0.3f, impulsive = 0.25f, courage = 0.2f)
                ),
                QuestionOption(
                    text = "No",
                    effects = PersonalityEffects(curiosity = -0.2f, impulsive = -0.25f)
                )
            )
        ),

        // Question 18: Mine vs Yours (greed) - SCALE
        PersonalityQuestion(
            id = "mine_vs_yours",
            text = "How much do you agree: 'What's mine is mine, what's yours is negotiable'",
            type = QuestionType.SCALE
        ),

        // Question 19: Combat Style (aggression, impulsive)
        PersonalityQuestion(
            id = "combat_style",
            text = "Your preferred combat approach:",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Charge first, ask questions later",
                    effects = PersonalityEffects(aggression = 0.3f, impulsive = 0.25f, courage = 0.2f)
                ),
                QuestionOption(
                    text = "Observe and strike at the right moment",
                    effects = PersonalityEffects(impulsive = -0.3f, aggression = 0.1f)
                ),
                QuestionOption(
                    text = "Avoid combat when possible",
                    effects = PersonalityEffects(aggression = -0.3f, courage = -0.15f)
                ),
                QuestionOption(
                    text = "Fight dirty to win",
                    effects = PersonalityEffects(aggression = 0.25f, impulsive = 0.2f)
                ),
                QuestionOption(
                    text = "Coordinate with allies",
                    effects = PersonalityEffects(social = 0.3f, impulsive = -0.2f)
                )
            )
        ),

        // Question 20: Treasure Map (curiosity, impulsive, greed)
        PersonalityQuestion(
            id = "treasure_map",
            text = "You find a treasure map to an unknown location. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Drop everything and go immediately",
                    effects = PersonalityEffects(impulsive = 0.3f, curiosity = 0.25f, greed = 0.2f)
                ),
                QuestionOption(
                    text = "Plan the journey carefully first",
                    effects = PersonalityEffects(impulsive = -0.25f, curiosity = 0.2f)
                ),
                QuestionOption(
                    text = "Sell the map",
                    effects = PersonalityEffects(greed = 0.3f, curiosity = -0.25f)
                ),
                QuestionOption(
                    text = "Ignore it, stick to your current path",
                    effects = PersonalityEffects(curiosity = -0.3f, impulsive = -0.2f)
                ),
                QuestionOption(
                    text = "Gather a party to explore together",
                    effects = PersonalityEffects(social = 0.3f, curiosity = 0.2f)
                )
            )
        ),

        // Question 21: Diplomacy (aggression) - SCALE
        PersonalityQuestion(
            id = "diplomacy_warfare",
            text = "How much do you agree: 'Diplomacy is just warfare by other means'",
            type = QuestionType.SCALE
        ),

        // Question 22: Underpriced Ring (greed, social)
        PersonalityQuestion(
            id = "underpriced_ring",
            text = "You find a magic ring. The shopkeeper clearly underpriced it. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Buy it and say nothing",
                    effects = PersonalityEffects(greed = 0.25f, social = -0.1f)
                ),
                QuestionOption(
                    text = "Tell them it's worth more",
                    effects = PersonalityEffects(greed = -0.3f, social = 0.25f)
                ),
                QuestionOption(
                    text = "Haggle for an even lower price",
                    effects = PersonalityEffects(greed = 0.3f, impulsive = 0.15f)
                ),
                QuestionOption(
                    text = "Leave it, feels wrong",
                    effects = PersonalityEffects(greed = -0.25f)
                ),
                QuestionOption(
                    text = "Buy it and give them a tip later",
                    effects = PersonalityEffects(greed = 0.1f, social = 0.2f)
                )
            )
        ),

        // Question 23: Unknown Creature (courage, curiosity, aggression)
        PersonalityQuestion(
            id = "unknown_creature",
            text = "You encounter a creature you've never seen before. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Attack first, safety first",
                    effects = PersonalityEffects(aggression = 0.3f, courage = 0.15f, curiosity = -0.2f)
                ),
                QuestionOption(
                    text = "Observe its behavior",
                    effects = PersonalityEffects(curiosity = 0.3f, impulsive = -0.25f)
                ),
                QuestionOption(
                    text = "Try to communicate with it",
                    effects = PersonalityEffects(curiosity = 0.25f, social = 0.25f, aggression = -0.25f)
                ),
                QuestionOption(
                    text = "Run away",
                    effects = PersonalityEffects(courage = -0.3f, curiosity = -0.15f)
                ),
                QuestionOption(
                    text = "Consult your bestiary first",
                    effects = PersonalityEffects(curiosity = 0.2f, impulsive = -0.3f)
                )
            )
        ),

        // Question 24: Act Now Think Later (impulsive) - SCALE
        PersonalityQuestion(
            id = "act_now_think_later",
            text = "How much do you agree: 'Act now, think later'",
            type = QuestionType.SCALE
        ),

        // Question 25: Victory Spoils (greed, social, impulsive)
        PersonalityQuestion(
            id = "quest_reward",
            text = "After a successful quest, you receive a large reward. What do you do?",
            type = QuestionType.MULTIPLE_CHOICE,
            options = listOf(
                QuestionOption(
                    text = "Save it all",
                    effects = PersonalityEffects(greed = 0.25f, impulsive = -0.25f)
                ),
                QuestionOption(
                    text = "Spend it immediately on upgrades",
                    effects = PersonalityEffects(impulsive = 0.25f, greed = 0.1f)
                ),
                QuestionOption(
                    text = "Share with your companions",
                    effects = PersonalityEffects(greed = -0.3f, social = 0.3f)
                ),
                QuestionOption(
                    text = "Gamble it for a chance at more",
                    effects = PersonalityEffects(impulsive = 0.3f, greed = 0.2f, courage = 0.2f)
                ),
                QuestionOption(
                    text = "Donate some to the poor",
                    effects = PersonalityEffects(greed = -0.25f, social = 0.25f)
                )
            )
        )
    )

    /**
     * Get 5 random questions for character creation
     * Ensures good distribution across question types
     */
    fun getRandomQuestions(random: Random = Random.Default): List<PersonalityQuestion> {
        // Ensure we get at least 1 of each type
        val yesNo = allQuestions.filter { it.type == QuestionType.YES_NO }.shuffled(random)
        val scale = allQuestions.filter { it.type == QuestionType.SCALE }.shuffled(random)
        val multipleChoice = allQuestions.filter { it.type == QuestionType.MULTIPLE_CHOICE }.shuffled(random)

        val selected = mutableListOf<PersonalityQuestion>()

        // Add 1 yes/no, 2 scale, 2 multiple choice
        yesNo.firstOrNull()?.let { selected.add(it) }
        scale.take(2).forEach { selected.add(it) }
        multipleChoice.take(2).forEach { selected.add(it) }

        return selected.shuffled(random)
    }

    /**
     * Calculate personality effects from a scale answer (0-5)
     * Maps to the trait being measured by that question
     */
    fun getScaleEffects(questionId: String, value: Int): PersonalityEffects {
        require(value in 0..5) { "Scale value must be between 0 and 5" }

        // Map 0-5 to -0.3 to +0.3
        val normalizedEffect = ((value - 2.5f) / 2.5f) * 0.3f

        return when (questionId) {
            "companions" -> PersonalityEffects(social = normalizedEffect)
            "unexplored_path" -> PersonalityEffects(curiosity = normalizedEffect)
            "planning" -> PersonalityEffects(impulsive = normalizedEffect)
            "fortune_favors_bold" -> PersonalityEffects(courage = normalizedEffect)
            "mine_vs_yours" -> PersonalityEffects(greed = normalizedEffect)
            "diplomacy_warfare" -> PersonalityEffects(aggression = normalizedEffect)
            "act_now_think_later" -> PersonalityEffects(impulsive = normalizedEffect)
            else -> PersonalityEffects()
        }
    }
}
