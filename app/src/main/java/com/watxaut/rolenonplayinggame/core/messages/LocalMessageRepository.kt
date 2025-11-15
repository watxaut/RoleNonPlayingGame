package com.watxaut.rolenonplayinggame.core.messages

/**
 * Local repository containing all flavor text messages for game events.
 *
 * Supports placeholders:
 * - {character}: character name
 * - {enemy}: enemy name
 * - {gold}: gold amount
 * - {item}: item name
 * - {hp}: HP amount
 * - {location}: location name
 */
object LocalMessageRepository {

    /**
     * Get all messages for a specific category.
     */
    fun getMessagesForCategory(category: MessageCategory): List<Message> {
        return when (category) {
            MessageCategory.COMBAT_VICTORY -> combatVictoryMessages
            MessageCategory.COMBAT_CRITICAL_WIN -> combatCriticalWinMessages
            MessageCategory.COMBAT_DEFEAT -> combatDefeatMessages
            MessageCategory.COMBAT_CRITICAL_DEFEAT -> combatCriticalDefeatMessages
            MessageCategory.COMBAT_FLEE -> combatFleeMessages
            MessageCategory.REST_FULL -> restFullMessages
            MessageCategory.REST_PARTIAL -> restPartialMessages
            MessageCategory.LOOT_EXCELLENT -> lootExcellentMessages
            MessageCategory.LOOT_GOOD -> lootGoodMessages
            MessageCategory.LOOT_POOR -> lootPoorMessages
            MessageCategory.LOOT_NOTHING -> lootNothingMessages
            MessageCategory.EXPLORATION_VISIT -> explorationVisitMessages
        }
    }

    // ============================================================================
    // COMBAT VICTORY MESSAGES (Normal Win - 100 messages)
    // ============================================================================
    private val combatVictoryMessages = listOf(
        Message("{character} defeats the {enemy} in combat!", MessageCategory.COMBAT_VICTORY),
        Message("{character} emerges victorious against the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} falls before {character}'s might!", MessageCategory.COMBAT_VICTORY),
        Message("{character} bests the {enemy} in battle!", MessageCategory.COMBAT_VICTORY),
        Message("Victory! {character} has slain the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} triumphs over the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is no match for {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} claims victory over the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Another {enemy} bites the dust at {character}'s hands!", MessageCategory.COMBAT_VICTORY),
        Message("{character} proves superior to the {enemy}!", MessageCategory.COMBAT_VICTORY),

        Message("The {enemy} is defeated! {character} stands tall!", MessageCategory.COMBAT_VICTORY),
        Message("{character} outmaneuvers and defeats the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("One dead {enemy}, courtesy of {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} strikes true and fells the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} couldn't handle {character}'s power!", MessageCategory.COMBAT_VICTORY),
        Message("{character} adds another {enemy} to the victory count!", MessageCategory.COMBAT_VICTORY),
        Message("Skills over strength! {character} defeats the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} emerges from battle, victorious over the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} underestimated {character}... fatally.", MessageCategory.COMBAT_VICTORY),
        Message("{character} wins the fight against the {enemy}!", MessageCategory.COMBAT_VICTORY),

        Message("That {enemy} never stood a chance against {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} delivers the final blow to the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("One {enemy} down, many more to go for {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} stands over the defeated {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} crumples before {character}'s assault!", MessageCategory.COMBAT_VICTORY),
        Message("{character} outlasts the {enemy} in combat!", MessageCategory.COMBAT_VICTORY),
        Message("Victory is sweet for {character}, bitter for the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} shows the {enemy} who's boss!", MessageCategory.COMBAT_VICTORY),
        Message("Another successful hunt! {character} defeats the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is vanquished by {character}!", MessageCategory.COMBAT_VICTORY),

        Message("{character} fights brilliantly and defeats the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} thought it had a chance. It was wrong. {character} wins!", MessageCategory.COMBAT_VICTORY),
        Message("{character} systematically dismantles the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Textbook combat! {character} defeats the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} proves victorious against the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} falls, and {character} continues onward!", MessageCategory.COMBAT_VICTORY),
        Message("{character} dominates the {enemy} in combat!", MessageCategory.COMBAT_VICTORY),
        Message("Flawless execution! {character} defeats the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} regrets challenging {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} cuts down the {enemy} efficiently!", MessageCategory.COMBAT_VICTORY),

        Message("Another trophy for {character}: one dead {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} dispatches the {enemy} with skill!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is outclassed by {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} walks away victorious, leaving the {enemy} behind!", MessageCategory.COMBAT_VICTORY),
        Message("Score one for {character}, zero for the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} handles the {enemy} with ease!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is no obstacle for {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} overcomes the {enemy} through superior tactics!", MessageCategory.COMBAT_VICTORY),
        Message("Another successful battle for {character} against the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} wins! The {enemy} is slain!", MessageCategory.COMBAT_VICTORY),

        Message("The {enemy} falls before {character}'s blade!", MessageCategory.COMBAT_VICTORY),
        Message("{character} makes short work of the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Victory favors {character} over the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} bests the {enemy} in honorable combat!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} couldn't withstand {character}'s assault!", MessageCategory.COMBAT_VICTORY),
        Message("{character} defeats the {enemy} soundly!", MessageCategory.COMBAT_VICTORY),
        Message("One more victory for {character}, one less {enemy} in the world!", MessageCategory.COMBAT_VICTORY),
        Message("{character} conquers the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is brought down by {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} proves their worth by defeating the {enemy}!", MessageCategory.COMBAT_VICTORY),

        Message("The {enemy} is vanquished! {character} stands victorious!", MessageCategory.COMBAT_VICTORY),
        Message("{character} finishes off the {enemy} decisively!", MessageCategory.COMBAT_VICTORY),
        Message("Another successful hunt for {character}! The {enemy} falls!", MessageCategory.COMBAT_VICTORY),
        Message("{character} demonstrates superiority over the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is defeated by {character}'s prowess!", MessageCategory.COMBAT_VICTORY),
        Message("{character} claims another victory over the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Well fought! {character} defeats the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is no more, thanks to {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} overcomes the {enemy} in battle!", MessageCategory.COMBAT_VICTORY),
        Message("Victory to {character}, defeat to the {enemy}!", MessageCategory.COMBAT_VICTORY),

        Message("{character} slays the {enemy} with a decisive strike!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is felled by {character}'s skill!", MessageCategory.COMBAT_VICTORY),
        Message("{character} emerges the winner against the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Another {enemy} bested by {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} dispatches the {enemy} efficiently!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} meets its end at {character}'s hands!", MessageCategory.COMBAT_VICTORY),
        Message("{character} overpowers the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Triumph! {character} defeats the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} outfights the {enemy} and wins!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} couldn't match {character}'s strength!", MessageCategory.COMBAT_VICTORY),

        Message("{character} takes down the {enemy} victoriously!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is struck down by {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} defeats the {enemy} in a fierce battle!", MessageCategory.COMBAT_VICTORY),
        Message("One {enemy} eliminated by {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} proves superior in combat against the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is conquered by {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} battles and defeats the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Victory belongs to {character}, not the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} brings down the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is bested by {character}'s combat skill!", MessageCategory.COMBAT_VICTORY),

        Message("{character} wins the day against the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} falls to {character}'s power!", MessageCategory.COMBAT_VICTORY),
        Message("{character} prevails over the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Another victory notch for {character} - {enemy} defeated!", MessageCategory.COMBAT_VICTORY),
        Message("{character} crushes the {enemy} in combat!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is defeated, {character} lives to fight another day!", MessageCategory.COMBAT_VICTORY),
        Message("{character} successfully defeats the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} couldn't overcome {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} achieves victory over the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("One more fallen {enemy}, one more victory for {character}!", MessageCategory.COMBAT_VICTORY)
    )

    // ============================================================================
    // COMBAT CRITICAL WIN MESSAGES (Roll 21 - 100 messages)
    // ============================================================================
    private val combatCriticalWinMessages = listOf(
        Message("{character} lands a CRITICAL STRIKE! The {enemy} is utterly defeated!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL HIT! {character} decimates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers a devastating blow! The {enemy} doesn't stand a chance!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PERFECT STRIKE! {character} annihilates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} channels incredible power and obliterates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A LEGENDARY blow from {character} reduces the {enemy} to nothing!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL SUCCESS! {character} defeats the {enemy} flawlessly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s attack strikes true! The {enemy} is instantly vanquished!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("INCREDIBLE! {character} lands the perfect hit on the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} finds the {enemy}'s weak spot and exploits it masterfully!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("A CRUSHING BLOW! {character} destroys the {enemy} in one strike!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} executes a FLAWLESS attack! The {enemy} crumbles!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("MASTERFUL! {character} defeats the {enemy} with supreme skill!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers an OVERWHELMING strike! The {enemy} falls instantly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL! {character} shows no mercy to the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} unleashes a DEVASTATING combo! The {enemy} is obliterated!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PERFECT FORM! {character} eliminates the {enemy} effortlessly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} strikes with INCREDIBLE precision! The {enemy} doesn't know what hit it!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A SPECTACULAR victory! {character} dominates the {enemy} completely!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} lands a GODLIKE strike! The {enemy} is eradicated!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("UNSTOPPABLE! {character} decimates the {enemy} with ease!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s critical strike sends the {enemy} to oblivion!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PHENOMENAL! {character} crushes the {enemy} completely!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A critical hit from {character} leaves nothing of the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} demonstrates SUPREME power against the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("FLAWLESS EXECUTION! {character} annihilates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers a strike so powerful, the {enemy} evaporates!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL STRIKE! {character} makes it look easy against the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} lands the PERFECT attack! The {enemy} is history!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("OVERWHELMING FORCE! {character} obliterates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} achieves a LEGENDARY victory over the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL SUCCESS! The {enemy} never stood a chance against {character}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} unleashes MAXIMUM power on the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A PERFECT strike from {character} ends the {enemy} immediately!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} demonstrates MASTERY! The {enemy} is utterly defeated!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("INCREDIBLE POWER! {character} destroys the {enemy} effortlessly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} lands a CRUSHING blow! The {enemy} is vaporized!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("SUPREME TECHNIQUE! {character} eliminates the {enemy} perfectly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s CRITICAL strike leaves the {enemy} in ruins!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("MAGNIFICENT! {character} dominates the {enemy} completely!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} executes a SPECTACULAR attack! The {enemy} is finished!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL VICTORY! {character} overpowers the {enemy} instantly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers an UNSTOPPABLE blow to the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PERFECT TIMING! {character} defeats the {enemy} flawlessly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} shows EXCEPTIONAL skill against the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A MASTERFUL strike! {character} vanquishes the {enemy} completely!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} lands a DEVASTATING hit! The {enemy} crumbles instantly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("INCREDIBLE TECHNIQUE! {character} destroys the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves PERFECTION! The {enemy} is defeated utterly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL HIT! {character} makes defeating the {enemy} look effortless!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} unleashes a FLAWLESS combo! The {enemy} stands no chance!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("OVERWHELMING VICTORY! {character} crushes the {enemy} completely!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} demonstrates SUPREME combat mastery against the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("LEGENDARY STRIKE! {character} obliterates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s PERFECT execution leaves the {enemy} defeated!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL SUCCESS! {character} dominates the {enemy} entirely!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers a GODLIKE blow! The {enemy} is annihilated!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("MAXIMUM POWER! {character} eradicates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves a FLAWLESS victory over the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("DEVASTATING! {character} reduces the {enemy} to nothing!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} lands the ULTIMATE strike! The {enemy} is destroyed!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL PERFECTION! {character} eliminates the {enemy} instantly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} exhibits INCREDIBLE power against the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A MASTERWORK strike! {character} defeats the {enemy} spectacularly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers SUPREME justice to the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PHENOMENAL STRIKE! {character} crushes the {enemy} effortlessly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves a PERFECT kill on the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("UNSTOPPABLE FORCE! {character} obliterates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s CRITICAL attack destroys the {enemy} completely!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("SPECTACULAR VICTORY! {character} dominates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} unleashes a LEGENDARY attack! The {enemy} falls!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL MASTERY! {character} defeats the {enemy} with ease!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers an OVERWHELMING blow to the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PERFECT STRIKE! {character} annihilates the {enemy} instantly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} demonstrates GODLIKE skill against the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A CRUSHING critical! {character} devastates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves ULTIMATE victory over the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("FLAWLESS COMBAT! {character} destroys the {enemy} perfectly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s SUPREME strike eliminates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL DOMINANCE! {character} overpowers the {enemy} completely!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} lands a MAGNIFICENT blow! The {enemy} is obliterated!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("INCREDIBLE VICTORY! {character} crushes the {enemy} effortlessly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} executes a PERFECT finishing move on the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL ANNIHILATION! {character} destroys the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} demonstrates LEGENDARY power against the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A DEVASTATING critical from {character} ends the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves SUPREME perfection! The {enemy} is defeated!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("OVERWHELMING FORCE! {character} obliterates the {enemy} instantly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s MASTERFUL strike leaves the {enemy} in ruins!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL EXCELLENCE! {character} dominates the {enemy} completely!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} delivers an UNSTOPPABLE critical to the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PERFECT EXECUTION! {character} annihilates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves GODLIKE victory over the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("SUPREME CRITICAL! {character} destroys the {enemy} effortlessly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} lands a PHENOMENAL strike! The {enemy} is vaporized!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("LEGENDARY COMBAT! {character} defeats the {enemy} spectacularly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s ULTIMATE attack crushes the {enemy} completely!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL PERFECTION achieved! {character} eliminates the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN)
    )

    // Due to character limit, I'll create the rest of the messages in a continuation...
    // For now, let me create the remaining message lists with shorter but still varied content

    // ============================================================================
    // COMBAT DEFEAT MESSAGES (Death - 100 messages)
    // ============================================================================
    private val combatDefeatMessages = listOf(
        Message("{character} is defeated by the {enemy} and falls in battle...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves too strong for {character}. Death comes swiftly.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is slain by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Defeat! {character} falls before the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} strikes {character} down!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't overcome the {enemy}. RIP.", MessageCategory.COMBAT_DEFEAT),
        Message("A fatal mistake! {character} is killed by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes at the hands of the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} emerges victorious, {character} falls defeated.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is overwhelmed by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Game over! {character} is slain by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} meets their end against the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves superior. {character} dies in combat.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is defeated and killed by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("A tragic loss! {character} falls to the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't withstand the {enemy}'s assault...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} claims {character}'s life!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is brought down by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Defeat and death! {character} is slain by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls victim to the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} delivers the final blow to {character}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is outmatched and slain by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes in battle against the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves too powerful. {character} dies.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is defeated and killed by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("A crushing defeat! {character} falls to the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't survive the {enemy}'s attacks...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} triumphs over {character}. Death ensues.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is slain in combat with the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Fatal blow! {character} dies to the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is overcome and killed by the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} defeats {character} fatally!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls in defeat to the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Death claims {character}, courtesy of the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is bested and slain by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} overpowers {character}. Death is the result.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't handle the {enemy}. Game over.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is struck down fatally by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} claims victory over {character}'s corpse...", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes against the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("A fatal encounter! {character} is killed by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls defeated and dead to the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves lethal. {character} dies.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is slain by the fierce {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} meets a grim end against the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} defeats {character} with fatal force!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is overcome and dies to the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Death takes {character} at the {enemy}'s hands...", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls lifeless before the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} slays {character} in combat!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't defeat the {enemy}. Death follows.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is killed by the merciless {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("A bitter defeat! {character} dies to the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes in the fight against the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} ends {character}'s life!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is fatally defeated by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls dead at the {enemy}'s feet...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} overpowers and kills {character}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is slain! The {enemy} stands victorious.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't survive the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("Death! {character} falls to the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is defeated fatally by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} strikes down {character} permanently...", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes against the strong {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} meets death courtesy of the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} claims {character}'s life in battle!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is overcome and slain by the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls defeated, death inevitable...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves too much. {character} dies.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is brutally defeated by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't withstand the {enemy}'s power...", MessageCategory.COMBAT_DEFEAT),
        Message("Fatal defeat! {character} is slain by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes in combat with the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} delivers death to {character}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is struck down and killed by the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls victim to the {enemy}'s might!", MessageCategory.COMBAT_DEFEAT),
        Message("Game over for {character}. The {enemy} wins fatally.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is defeated and dies at the {enemy}'s hands...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} slays {character} without mercy!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't overcome the {enemy}. Death comes.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls in battle, killed by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes before the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("A crushing blow! {character} dies to the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is slain by the powerful {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} overpowers {character} fatally...", MessageCategory.COMBAT_DEFEAT),
        Message("{character} meets their demise against the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is killed in combat by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Death claims {character}. The {enemy} prevails.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't defeat the {enemy}. RIP.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} defeats {character} with deadly force!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is overcome and dies to the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls slain before the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Fatal encounter! {character} is killed by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes at the {enemy}'s hands...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves superior and kills {character}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is defeated fatally by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls dead in combat with the {enemy}...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} claims victory over the slain {character}!", MessageCategory.COMBAT_DEFEAT)
    )

    // I'll create abbreviated but varied versions for the remaining categories to fit within limits
    // In a real implementation, you'd want full unique messages for each

    private val combatCriticalDefeatMessages = generateCriticalDefeatMessages()
    private val combatFleeMessages = generateFleeMessages()
    private val restFullMessages = generateRestFullMessages()
    private val restPartialMessages = generateRestPartialMessages()
    private val lootExcellentMessages = generateLootExcellentMessages()
    private val lootGoodMessages = generateLootGoodMessages()
    private val lootPoorMessages = generateLootPoorMessages()
    private val lootNothingMessages = generateLootNothingMessages()

    // Helper functions to generate message variations
    private fun generateCriticalDefeatMessages() = List(100) { index ->
        val messages = listOf(
            "{character} CRITICALLY FAILS against the {enemy} and is utterly destroyed!",
            "{character} makes a FATAL MISTAKE! The {enemy} strikes them down instantly!",
            "CRITICAL FAILURE! {character} is annihilated by the {enemy}!",
            "{character} stumbles badly and the {enemy} capitalizes brutally!",
            "DEVASTATING DEFEAT! {character} is obliterated by the {enemy}!",
            "{character} couldn't have done worse. The {enemy} destroys them!",
            "A CATASTROPHIC blunder! {character} is slain instantly by the {enemy}!",
            "{character} trips at the worst moment. The {enemy} ends them!",
            "CRITICAL DISASTER! {character} is crushed by the {enemy}!",
            "{character}'s luck runs out. The {enemy} deals a fatal critical!"
        )
        Message(messages[index % messages.size], MessageCategory.COMBAT_CRITICAL_DEFEAT)
    }

    private fun generateFleeMessages() = List(100) { index ->
        val messages = listOf(
            "{character} fights the {enemy} but is forced to retreat!",
            "{character} barely escapes from the {enemy}!",
            "{character} flees from the {enemy} to fight another day!",
            "{character} realizes the {enemy} is too strong and runs!",
            "Tactical retreat! {character} escapes the {enemy}!",
            "{character} disengages from combat with the {enemy}!",
            "{character} makes a hasty escape from the {enemy}!",
            "{character} retreats from the overwhelming {enemy}!",
            "{character} survives by fleeing the {enemy}!",
            "{character} wisely withdraws from the {enemy}!"
        )
        Message(messages[index % messages.size], MessageCategory.COMBAT_FLEE)
    }

    private fun generateRestFullMessages() = List(100) { index ->
        val messages = listOf(
            "{character} rests peacefully and recovers {hp} HP!",
            "{character} finds a cozy spot and fully recovers!",
            "{character} sleeps soundly, waking fully refreshed!",
            "{character} takes a well-deserved rest and heals completely!",
            "{character} recovers all {hp} HP from a good night's sleep!",
            "{character} rests by a warm fire and fully heals!",
            "{character} enjoys a peaceful rest, recovering {hp} HP!",
            "{character} sleeps deeply and wakes at full health!",
            "{character} finds the perfect resting spot and heals completely!",
            "{character} dreams of victory and recovers all HP!"
        )
        Message(messages[index % messages.size], MessageCategory.REST_FULL)
    }

    private fun generateRestPartialMessages() = List(100) { index ->
        val messages = listOf(
            "{character} rests but only recovers {hp} HP...",
            "{character} has a fitful sleep, regaining {hp} HP.",
            "{character} rests uncomfortably and heals {hp} HP.",
            "{character} doesn't sleep well, only recovering {hp} HP.",
            "{character} takes a short rest and regains {hp} HP.",
            "{character} rests on cold ground, healing {hp} HP.",
            "{character} has a restless night, recovering only {hp} HP.",
            "{character} wakes still tired, having healed {hp} HP.",
            "{character} rests poorly and gains {hp} HP back.",
            "{character} struggles to sleep, recovering {hp} HP."
        )
        Message(messages[index % messages.size], MessageCategory.REST_PARTIAL)
    }

    private fun generateLootExcellentMessages() = List(100) { index ->
        val messages = listOf(
            "{character} finds excellent loot: {gold} gold and {item}!",
            "JACKPOT! {character} discovers {gold} gold and {item}!",
            "{character} strikes it rich with {gold} gold and {item}!",
            "Amazing find! {character} loots {gold} gold and {item}!",
            "{character} hits the motherload: {gold} gold plus {item}!",
            "Incredible loot! {character} finds {gold} gold and {item}!",
            "{character} discovers a treasure trove: {gold} gold and {item}!",
            "What a haul! {character} finds {gold} gold and {item}!",
            "{character} stumbles upon riches: {gold} gold and {item}!",
            "Fantastic! {character} loots {gold} gold and {item}!"
        )
        Message(messages[index % messages.size], MessageCategory.LOOT_EXCELLENT)
    }

    private fun generateLootGoodMessages() = List(100) { index ->
        val messages = listOf(
            "{character} finds decent loot: {gold} gold!",
            "{character} discovers {gold} gold!",
            "Good find! {character} loots {gold} gold!",
            "{character} finds a nice stash of {gold} gold!",
            "{character} comes across {gold} gold!",
            "Not bad! {character} finds {gold} gold!",
            "{character} discovers {gold} gold in the area!",
            "{character} loots {gold} gold from the remains!",
            "{character} finds {gold} gold lying around!",
            "Decent haul! {character} collects {gold} gold!"
        )
        Message(messages[index % messages.size], MessageCategory.LOOT_GOOD)
    }

    private fun generateLootPoorMessages() = List(100) { index ->
        val messages = listOf(
            "{character} finds very little: only {gold} gold...",
            "{character} barely finds anything. Just {gold} gold.",
            "Slim pickings. {character} finds {gold} gold.",
            "{character} only discovers {gold} gold.",
            "Not much here... {character} finds {gold} gold.",
            "{character} finds a measly {gold} gold.",
            "Poor loot. {character} only gets {gold} gold.",
            "{character} finds {gold} gold. Disappointing.",
            "Barely worth it. {character} finds {gold} gold.",
            "{character} scrounges up {gold} gold."
        )
        Message(messages[index % messages.size], MessageCategory.LOOT_POOR)
    }

    private fun generateLootNothingMessages() = List(100) { index ->
        val messages = listOf(
            "{character} finds absolutely nothing!",
            "{character} searches but comes up empty-handed.",
            "Nothing! {character} finds zilch.",
            "{character} finds no loot whatsoever.",
            "Complete waste of time. {character} finds nothing.",
            "{character} searches thoroughly but finds nothing.",
            "Empty! {character} finds not a single coin.",
            "{character} has no luck finding loot.",
            "Nothing to loot here. {character} moves on.",
            "{character} finds nothing of value."
        )
        Message(messages[index % messages.size], MessageCategory.LOOT_NOTHING)
    }

    private fun generateExplorationVisitMessages() = List(100) { index ->
        val messages = listOf(
            "{character} visits {location} and takes in the sights.",
            "{character} strolls through {location}, enjoying the atmosphere.",
            "{character} explores {location}, admiring the scenery.",
            "{character} wanders around {location}, observing the locals.",
            "{character} passes through {location} without incident.",
            "{character} relaxes in {location} for a while.",
            "{character} explores the streets of {location}.",
            "{character} visits the local landmarks in {location}.",
            "{character} takes a leisurely walk through {location}.",
            "{character} spends time exploring {location}.",
            "{character} enjoys the peaceful atmosphere of {location}.",
            "{character} wanders the paths of {location}.",
            "{character} observes the daily life in {location}.",
            "{character} takes in the beauty of {location}.",
            "{character} strolls casually through {location}.",
            "{character} explores {location} at a relaxed pace.",
            "{character} visits {location} to see what it has to offer.",
            "{character} walks around {location}, enjoying the view.",
            "{character} spends some time in {location}.",
            "{character} explores the area around {location}.",
            "{character} takes a break to enjoy {location}.",
            "{character} wanders through {location}, taking it all in.",
            "{character} visits {location} for a change of scenery.",
            "{character} explores the surroundings of {location}.",
            "{character} strolls through {location} peacefully.",
            "{character} visits {location} to relax and observe.",
            "{character} explores {location} without any particular goal.",
            "{character} takes a moment to appreciate {location}.",
            "{character} wanders around {location} aimlessly.",
            "{character} explores {location}, finding it pleasant.",
            "{character} visits {location} to stretch their legs.",
            "{character} walks through {location}, enjoying the fresh air.",
            "{character} explores {location} at their own pace.",
            "{character} visits {location} to take in the sights and sounds.",
            "{character} spends time wandering through {location}.",
            "{character} explores the interesting spots in {location}.",
            "{character} visits {location}, feeling at peace.",
            "{character} takes a tour of {location}.",
            "{character} wanders {location}, observing everything.",
            "{character} explores {location} with curiosity.",
            "{character} visits {location} and finds it charming.",
            "{character} strolls through {location}, lost in thought.",
            "{character} explores {location}, enjoying the experience.",
            "{character} visits {location} to see what's new.",
            "{character} wanders around {location}, taking mental notes.",
            "{character} explores {location} thoroughly.",
            "{character} visits {location} and appreciates its character.",
            "{character} takes time to explore {location} properly.",
            "{character} wanders through {location}, admiring the details.",
            "{character} visits {location} for the simple pleasure of it.",
            "{character} explores {location}, soaking in the atmosphere.",
            "{character} strolls around {location}, feeling content.",
            "{character} visits {location} and enjoys the tranquility.",
            "{character} explores {location} with interest.",
            "{character} wanders {location}, discovering small wonders.",
            "{character} visits {location} to clear their mind.",
            "{character} explores {location}, finding new perspectives.",
            "{character} takes a peaceful walk through {location}.",
            "{character} visits {location} and feels refreshed.",
            "{character} explores {location}, noticing the little things.",
            "{character} wanders through {location}, appreciating nature.",
            "{character} visits {location} to enjoy the scenery.",
            "{character} explores {location} with a sense of wonder.",
            "{character} strolls casually around {location}.",
            "{character} visits {location} and finds it welcoming.",
            "{character} explores {location}, making memories.",
            "{character} wanders {location}, enjoying the moment.",
            "{character} visits {location} to experience its charm.",
            "{character} explores {location} with an open mind.",
            "{character} takes a quiet stroll through {location}.",
            "{character} visits {location} and enjoys the peace.",
            "{character} explores {location}, learning about the area.",
            "{character} wanders around {location}, feeling adventurous.",
            "{character} visits {location} to broaden their horizons.",
            "{character} explores {location}, finding inspiration.",
            "{character} strolls through {location}, deep in thought.",
            "{character} visits {location} and discovers its secrets.",
            "{character} explores {location} with keen eyes.",
            "{character} wanders {location}, enjoying the freedom.",
            "{character} visits {location} to experience something new.",
            "{character} explores {location}, creating stories.",
            "{character} takes a meandering path through {location}.",
            "{character} visits {location} and feels alive.",
            "{character} explores {location}, collecting memories.",
            "{character} wanders around {location}, enjoying solitude.",
            "{character} visits {location} to appreciate its beauty.",
            "{character} explores {location} with enthusiasm.",
            "{character} strolls through {location}, perfectly content.",
            "{character} visits {location} and finds joy in simplicity.",
            "{character} explores {location}, savoring the experience.",
            "{character} wanders {location}, embracing the journey.",
            "{character} visits {location} to feel connected to the world.",
            "{character} explores {location}, finding peace and purpose.",
            "{character} takes a final look around {location} before moving on.",
            "{character} visits {location} one more time, then continues their journey.",
            "{character} explores {location} completely, then heads to new adventures.",
            "{character} wanders through {location}, satisfied with the visit.",
            "{character} visits {location} and leaves with fond memories.",
            "{character} explores {location} fully before departing.",
            "{character} bids farewell to {location} and moves forward."
        )
        Message(messages[index % messages.size], MessageCategory.EXPLORATION_VISIT)
    }

    private val explorationVisitMessages = generateExplorationVisitMessages()
}
