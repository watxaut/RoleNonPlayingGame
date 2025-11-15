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
    // COMBAT VICTORY MESSAGES (Normal Win - 100 messages) - NOW 20% LONGER & FUNNER!
    // ============================================================================
    private val combatVictoryMessages = listOf(
        Message("{character} defeats the {enemy} in glorious combat and strikes a victorious pose!", MessageCategory.COMBAT_VICTORY),
        Message("{character} emerges victorious against the {enemy} after an intense clash of steel and will!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} falls before {character}'s unstoppable might! Another one bites the dust!", MessageCategory.COMBAT_VICTORY),
        Message("{character} bests the {enemy} in battle with style and a touch of flair!", MessageCategory.COMBAT_VICTORY),
        Message("Victory! {character} has slain the {enemy} and lives to tell the tale!", MessageCategory.COMBAT_VICTORY),
        Message("{character} triumphs over the {enemy} in spectacular fashion! What a fight!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is absolutely no match for {character}'s combat prowess!", MessageCategory.COMBAT_VICTORY),
        Message("{character} claims decisive victory over the {enemy} and earns bragging rights!", MessageCategory.COMBAT_VICTORY),
        Message("Another {enemy} bites the dust at {character}'s skilled hands! That's what you get!", MessageCategory.COMBAT_VICTORY),
        Message("{character} proves definitively superior to the {enemy} in every way possible!", MessageCategory.COMBAT_VICTORY),

        Message("The {enemy} is utterly defeated! {character} stands tall and victorious over the fallen foe!", MessageCategory.COMBAT_VICTORY),
        Message("{character} outmaneuvers and defeats the {enemy} with clever tactics and raw skill!", MessageCategory.COMBAT_VICTORY),
        Message("One dead {enemy}, courtesy of {character}! The body count rises!", MessageCategory.COMBAT_VICTORY),
        Message("{character} strikes true and fells the {enemy} with a perfectly executed finishing blow!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} simply couldn't handle {character}'s overwhelming power! Not even close!", MessageCategory.COMBAT_VICTORY),
        Message("{character} adds another {enemy} to the ever-growing victory count! Impressive!", MessageCategory.COMBAT_VICTORY),
        Message("Skills over strength! {character} defeats the {enemy} through superior technique and cunning!", MessageCategory.COMBAT_VICTORY),
        Message("{character} emerges from the brutal battle, bloodied but victorious over the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} seriously underestimated {character}... and paid the ultimate price for it!", MessageCategory.COMBAT_VICTORY),
        Message("{character} wins the hard-fought fight against the {enemy}! Victory tastes sweet!", MessageCategory.COMBAT_VICTORY),

        Message("That {enemy} never stood a chance against the legendary {character}! Total domination!", MessageCategory.COMBAT_VICTORY),
        Message("{character} delivers the final, crushing blow to the {enemy}! It's all over!", MessageCategory.COMBAT_VICTORY),
        Message("One {enemy} down, many more to go for the unstoppable {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} stands triumphantly over the defeated {enemy}'s crumpled body!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} crumples pathetically before {character}'s relentless assault! No mercy!", MessageCategory.COMBAT_VICTORY),
        Message("{character} outlasts the {enemy} in prolonged combat through sheer determination!", MessageCategory.COMBAT_VICTORY),
        Message("Victory is deliciously sweet for {character}, devastatingly bitter for the fallen {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} shows the {enemy} exactly who's boss around here! No contest!", MessageCategory.COMBAT_VICTORY),
        Message("Another successful hunt! {character} defeats the {enemy} and collects the spoils!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is completely vanquished by the mighty {character}'s blade!", MessageCategory.COMBAT_VICTORY),

        Message("{character} fights with brilliant precision and defeats the {enemy} handily!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} thought it had a chance. It was hilariously wrong. {character} wins decisively!", MessageCategory.COMBAT_VICTORY),
        Message("{character} systematically dismantles the {enemy} piece by piece! Brutal efficiency!", MessageCategory.COMBAT_VICTORY),
        Message("Textbook combat! {character} defeats the {enemy} by the book! Perfect execution!", MessageCategory.COMBAT_VICTORY),
        Message("{character} proves undeniably victorious against the outmatched {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} falls, and {character} continues onward to greater glory!", MessageCategory.COMBAT_VICTORY),
        Message("{character} absolutely dominates the {enemy} in combat! Total superiority!", MessageCategory.COMBAT_VICTORY),
        Message("Flawless execution! {character} defeats the {enemy} without breaking a sweat!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} immediately regrets challenging {character}! Terrible mistake!", MessageCategory.COMBAT_VICTORY),
        Message("{character} cuts down the {enemy} with cold, brutal efficiency!", MessageCategory.COMBAT_VICTORY),

        Message("Another trophy for {character}'s collection: one thoroughly dead {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} dispatches the {enemy} with impressive skill and grace!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is completely outclassed by the superior {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} walks away victorious, casually leaving the defeated {enemy} behind!", MessageCategory.COMBAT_VICTORY),
        Message("Score one for {character}, zero for the pathetic {enemy}! Easy win!", MessageCategory.COMBAT_VICTORY),
        Message("{character} handles the {enemy} with practiced ease! Like taking candy from a baby!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is barely an obstacle for the mighty {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} overcomes the {enemy} through superior tactics and battle-hardened experience!", MessageCategory.COMBAT_VICTORY),
        Message("Another successful battle for the legendary {character} against the unfortunate {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} wins decisively! The {enemy} is slain and forgotten!", MessageCategory.COMBAT_VICTORY),

        Message("The {enemy} falls dramatically before {character}'s gleaming blade! What a sight!", MessageCategory.COMBAT_VICTORY),
        Message("{character} makes ridiculously short work of the outmatched {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Victory favors the bold {character} over the foolish {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} bests the {enemy} in honorable combat! Glory to the victor!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} simply couldn't withstand {character}'s devastating assault! Overwhelming!", MessageCategory.COMBAT_VICTORY),
        Message("{character} defeats the {enemy} soundly and without question!", MessageCategory.COMBAT_VICTORY),
        Message("One more glorious victory for {character}, one less {enemy} plaguing the world!", MessageCategory.COMBAT_VICTORY),
        Message("{character} conquers the {enemy} with ruthless determination!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is brought down hard by the unstoppable {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} proves their legendary worth by defeating the {enemy} in style!", MessageCategory.COMBAT_VICTORY),

        Message("The {enemy} is thoroughly vanquished! {character} stands victorious and proud!", MessageCategory.COMBAT_VICTORY),
        Message("{character} finishes off the {enemy} decisively with a final, crushing blow!", MessageCategory.COMBAT_VICTORY),
        Message("Another successful hunt for {character}! The {enemy} falls like all the others!", MessageCategory.COMBAT_VICTORY),
        Message("{character} demonstrates clear superiority over the hapless {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is utterly defeated by {character}'s incredible prowess!", MessageCategory.COMBAT_VICTORY),
        Message("{character} claims yet another hard-fought victory over the {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Well fought! {character} defeats the {enemy} after an epic struggle!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is no more, thanks to {character}'s swift justice!", MessageCategory.COMBAT_VICTORY),
        Message("{character} overcomes the stubborn {enemy} in honorable battle!", MessageCategory.COMBAT_VICTORY),
        Message("Victory to the mighty {character}, crushing defeat to the fallen {enemy}!", MessageCategory.COMBAT_VICTORY),

        Message("{character} slays the {enemy} with a devastating decisive strike! Brutal!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is expertly felled by {character}'s masterful combat skill!", MessageCategory.COMBAT_VICTORY),
        Message("{character} emerges the undisputed winner against the outmatched {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Another {enemy} thoroughly bested by the unstoppable {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} dispatches the {enemy} with cold, calculated efficiency!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} meets its inevitable end at {character}'s skilled hands!", MessageCategory.COMBAT_VICTORY),
        Message("{character} completely overpowers the {enemy}! Utter domination!", MessageCategory.COMBAT_VICTORY),
        Message("Triumph! {character} defeats the {enemy} and raises their weapon high!", MessageCategory.COMBAT_VICTORY),
        Message("{character} outfights the {enemy} and wins through superior skill!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} couldn't possibly match {character}'s overwhelming strength!", MessageCategory.COMBAT_VICTORY),

        Message("{character} takes down the {enemy} victoriously and without mercy!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is struck down decisively by the legendary {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} defeats the {enemy} in a fierce, brutal battle! Epic!", MessageCategory.COMBAT_VICTORY),
        Message("One {enemy} permanently eliminated by the ruthless {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} proves absolutely superior in combat against the pathetic {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is conquered completely by the unstoppable {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} battles fiercely and defeats the {enemy} decisively!", MessageCategory.COMBAT_VICTORY),
        Message("Victory belongs solely to {character}, not the defeated {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} brings down the {enemy} with spectacular flair!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is thoroughly bested by {character}'s superior combat skill!", MessageCategory.COMBAT_VICTORY),

        Message("{character} wins the day triumphantly against the unfortunate {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} falls dramatically to {character}'s overwhelming power!", MessageCategory.COMBAT_VICTORY),
        Message("{character} prevails decisively over the outclassed {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("Another victory notch carved for {character} - {enemy} defeated in style!", MessageCategory.COMBAT_VICTORY),
        Message("{character} absolutely crushes the {enemy} in brutal combat!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} is defeated, {character} lives to fight yet another day!", MessageCategory.COMBAT_VICTORY),
        Message("{character} successfully defeats the {enemy} through skill and determination!", MessageCategory.COMBAT_VICTORY),
        Message("The {enemy} simply couldn't overcome the magnificent {character}!", MessageCategory.COMBAT_VICTORY),
        Message("{character} achieves glorious victory over the fallen {enemy}!", MessageCategory.COMBAT_VICTORY),
        Message("One more fallen {enemy}, one more glorious victory for {character}! Unstoppable!", MessageCategory.COMBAT_VICTORY)
    )

    // ============================================================================
    // COMBAT CRITICAL WIN MESSAGES (Roll 21 - 100 messages) - EVEN MORE EPIC!
    // ============================================================================
    private val combatCriticalWinMessages = listOf(
        Message("{character} lands a CRITICAL STRIKE of legendary proportions! The {enemy} is utterly obliterated in one hit!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL HIT! {character} decimates the {enemy} with impossible power! Absolutely devastating!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers a devastating blow that shakes the earth! The {enemy} doesn't stand a chance!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PERFECT STRIKE! {character} annihilates the {enemy} with flawless precision! Amazing!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} channels incredible otherworldly power and obliterates the {enemy} instantly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A LEGENDARY blow from {character} reduces the {enemy} to absolutely nothing! Gone!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL SUCCESS! {character} defeats the {enemy} so flawlessly it looks effortless!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s attack strikes perfectly true! The {enemy} is instantly vanquished before it can react!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("INCREDIBLE! {character} lands the absolutely perfect hit on the {enemy}! Critical perfection!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} finds the {enemy}'s weak spot and exploits it with masterful precision!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("A CRUSHING BLOW! {character} destroys the {enemy} in one devastating strike! Unbelievable!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} executes a FLAWLESS attack! The {enemy} crumbles like paper! Total domination!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("MASTERFUL! {character} defeats the {enemy} with supreme skill that would make legends weep!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers an OVERWHELMING strike! The {enemy} falls instantly without hope!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL! {character} shows absolutely no mercy to the {enemy}! Brutal execution!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} unleashes a DEVASTATING combo that leaves nothing! The {enemy} is obliterated!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PERFECT FORM! {character} eliminates the {enemy} effortlessly with textbook technique!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} strikes with INCREDIBLE precision! The {enemy} doesn't even know what hit it!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A SPECTACULAR victory! {character} dominates the {enemy} completely and utterly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} lands a GODLIKE strike that echoes through the ages! The {enemy} is eradicated!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("UNSTOPPABLE! {character} decimates the {enemy} with ridiculous ease! Not even close!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s critical strike sends the {enemy} straight to oblivion! See you never!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PHENOMENAL! {character} crushes the {enemy} completely and without mercy!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A critical hit from {character} leaves absolutely nothing of the {enemy}! Vaporized!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} demonstrates SUPREME power against the helpless {enemy}! Overwhelming!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("FLAWLESS EXECUTION! {character} annihilates the {enemy} with perfect technique!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers a strike so powerful, the {enemy} literally evaporates! Gone!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL STRIKE! {character} makes it look absolutely easy against the {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} lands the PERFECT attack! The {enemy} is history! Never stood a chance!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("OVERWHELMING FORCE! {character} obliterates the {enemy} with unstoppable power!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} achieves a LEGENDARY victory that will be sung about! The {enemy} is defeated!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL SUCCESS! The {enemy} never stood even a remote chance against {character}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} unleashes MAXIMUM power on the {enemy}! Full force! Total destruction!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A PERFECT strike from {character} ends the {enemy} immediately! One-hit wonder!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} demonstrates absolute MASTERY! The {enemy} is utterly defeated!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("INCREDIBLE POWER! {character} destroys the {enemy} effortlessly! Like swatting a fly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} lands a CRUSHING blow! The {enemy} is vaporized on contact!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("SUPREME TECHNIQUE! {character} eliminates the {enemy} with perfect form!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s CRITICAL strike leaves the {enemy} in scattered ruins! Devastating!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("MAGNIFICENT! {character} dominates the {enemy} completely and totally!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} executes a SPECTACULAR attack! The {enemy} is finished before it began!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL VICTORY! {character} overpowers the {enemy} instantly! No contest!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers an UNSTOPPABLE blow to the {enemy}! Nothing can stop this!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PERFECT TIMING! {character} defeats the {enemy} flawlessly with split-second precision!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} shows EXCEPTIONAL skill against the {enemy}! Master class performance!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A MASTERFUL strike! {character} vanquishes the {enemy} completely! Textbook perfection!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} lands a DEVASTATING hit! The {enemy} crumbles instantly like dust!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("INCREDIBLE TECHNIQUE! {character} destroys the {enemy} with impossible skill!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves absolute PERFECTION! The {enemy} is defeated utterly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL HIT! {character} makes defeating the {enemy} look absolutely effortless!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} unleashes a FLAWLESS combo! The {enemy} stands no chance whatsoever!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("OVERWHELMING VICTORY! {character} crushes the {enemy} completely and without mercy!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} demonstrates SUPREME combat mastery against the pathetic {enemy}!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("LEGENDARY STRIKE! {character} obliterates the {enemy} with mythical power!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s PERFECT execution leaves the {enemy} utterly defeated! Flawless!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL SUCCESS! {character} dominates the {enemy} entirely! Total domination!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers a GODLIKE blow! The {enemy} is annihilated! Divine power!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("MAXIMUM POWER! {character} eradicates the {enemy} with overwhelming force!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves a FLAWLESS victory over the {enemy}! Absolute perfection!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("DEVASTATING! {character} reduces the {enemy} to absolutely nothing! Gone forever!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} lands the ULTIMATE strike! The {enemy} is destroyed instantly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL PERFECTION! {character} eliminates the {enemy} instantly! One hit!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} exhibits INCREDIBLE power against the helpless {enemy}! Overwhelming!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A MASTERWORK strike! {character} defeats the {enemy} spectacularly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers SUPREME justice to the {enemy}! Swift and brutal!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PHENOMENAL STRIKE! {character} crushes the {enemy} effortlessly! Too easy!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves a PERFECT kill on the {enemy}! Flawless execution!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("UNSTOPPABLE FORCE! {character} obliterates the {enemy} like it's nothing!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s CRITICAL attack destroys the {enemy} completely! Total annihilation!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("SPECTACULAR VICTORY! {character} dominates the {enemy} beyond all hope!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} unleashes a LEGENDARY attack! The {enemy} falls like a house of cards!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL MASTERY! {character} defeats the {enemy} with incredible ease!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} delivers an OVERWHELMING blow to the {enemy}! Crushing defeat!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PERFECT STRIKE! {character} annihilates the {enemy} instantly! One-shot!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} demonstrates GODLIKE skill against the {enemy}! Divine intervention!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A CRUSHING critical! {character} devastates the {enemy} completely!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves ULTIMATE victory over the {enemy}! Supreme triumph!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("FLAWLESS COMBAT! {character} destroys the {enemy} perfectly! Textbook!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s SUPREME strike eliminates the {enemy} without question!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL DOMINANCE! {character} overpowers the {enemy} completely! Total control!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} lands a MAGNIFICENT blow! The {enemy} is obliterated beyond recognition!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("INCREDIBLE VICTORY! {character} crushes the {enemy} effortlessly! Not even trying!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} executes a PERFECT finishing move on the {enemy}! Spectacular!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL ANNIHILATION! {character} destroys the {enemy} completely! Utter devastation!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} demonstrates LEGENDARY power against the {enemy}! Mythical!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("A DEVASTATING critical from {character} ends the {enemy}! It's all over!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves SUPREME perfection! The {enemy} is defeated instantly!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("OVERWHELMING FORCE! {character} obliterates the {enemy} instantly! No mercy!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s MASTERFUL strike leaves the {enemy} in complete ruins!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL EXCELLENCE! {character} dominates the {enemy} completely and utterly!", MessageCategory.COMBAT_CRITICAL_WIN),

        Message("{character} delivers an UNSTOPPABLE critical to the {enemy}! Nothing stops this!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("PERFECT EXECUTION! {character} annihilates the {enemy} with divine precision!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} achieves GODLIKE victory over the {enemy}! Legendary performance!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("SUPREME CRITICAL! {character} destroys the {enemy} effortlessly! Child's play!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character} lands a PHENOMENAL strike! The {enemy} is vaporized on impact!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("LEGENDARY COMBAT! {character} defeats the {enemy} spectacularly! Epic!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("{character}'s ULTIMATE attack crushes the {enemy} completely! Total destruction!", MessageCategory.COMBAT_CRITICAL_WIN),
        Message("CRITICAL PERFECTION achieved! {character} eliminates the {enemy} flawlessly!", MessageCategory.COMBAT_CRITICAL_WIN)
    )

    // ============================================================================
    // COMBAT DEFEAT MESSAGES (Death - 100 messages) - NOW WITH MORE DRAMA!
    // ============================================================================
    private val combatDefeatMessages = generateDefeatMessages()

    private fun generateDefeatMessages() = listOf(
        Message("{character} is brutally defeated by the {enemy} and falls hard in battle...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves far too strong for {character}. Death comes swiftly and mercilessly.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is tragically slain by the {enemy}! Game over!", MessageCategory.COMBAT_DEFEAT),
        Message("Defeat! {character} falls before the {enemy} in crushing defeat...", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} strikes {character} down decisively! Another fallen hero!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't overcome the {enemy}. RIP. Better luck next time.", MessageCategory.COMBAT_DEFEAT),
        Message("A fatal mistake! {character} is killed mercilessly by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes at the deadly hands of the {enemy}... so sad.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} emerges victorious, {character} falls defeated and broken.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is completely overwhelmed by the powerful {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Game over! {character} is slain by the {enemy}! Better luck next time!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} meets their tragic end against the {enemy}... how unfortunate.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves definitively superior. {character} dies in brutal combat.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is defeated and killed by the {enemy}! What a shame!", MessageCategory.COMBAT_DEFEAT),
        Message("A tragic loss! {character} falls to the {enemy} in crushing defeat!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't withstand the {enemy}'s relentless assault... fatal.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} claims {character}'s life without mercy! Another victim!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is brought down hard by the {enemy}! Devastating!", MessageCategory.COMBAT_DEFEAT),
        Message("Defeat and death! {character} is slain by the {enemy} in battle!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls victim to the {enemy}... such a waste of potential.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} delivers the final blow to {character}! It's all over!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is outmatched and slain by the superior {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes in battle against the {enemy}... how tragic.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves too powerful. {character} dies without glory.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is defeated and killed by the {enemy}! Fatal encounter!", MessageCategory.COMBAT_DEFEAT),
        Message("A crushing defeat! {character} falls to the {enemy} in shame!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't survive the {enemy}'s devastating attacks... RIP.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} triumphs over {character}. Death ensues immediately.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is slain in brutal combat with the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Fatal blow! {character} dies to the {enemy}! So much for that!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is overcome and killed by the {enemy}... another one bites the dust.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} defeats {character} fatally! No mercy given!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls in crushing defeat to the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Death claims {character}, courtesy of the {enemy}... how sad.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is bested and slain by the {enemy}! Total defeat!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} overpowers {character}. Death is the inevitable result.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't handle the {enemy}. Game over. The end.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is struck down fatally by the {enemy}! Brutal!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} claims victory over {character}'s lifeless corpse... tragic.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes against the {enemy}! Another fallen warrior!", MessageCategory.COMBAT_DEFEAT),
        Message("A fatal encounter! {character} is killed by the {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls defeated and dead to the {enemy}... so close.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves lethal. {character} dies without hope.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is slain by the fierce {enemy}! No survivors!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} meets a grim end against the {enemy}... darkness falls.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} defeats {character} with fatal force! Overwhelming!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is overcome and dies to the {enemy}! Final breath!", MessageCategory.COMBAT_DEFEAT),
        Message("Death takes {character} at the {enemy}'s cruel hands... merciless.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls lifeless before the triumphant {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} slays {character} in combat! Another victim!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't defeat the {enemy}. Death follows swiftly.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is killed by the merciless {enemy}! No escape!", MessageCategory.COMBAT_DEFEAT),
        Message("A bitter defeat! {character} dies to the {enemy} in shame!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes in the fight against the {enemy}... tragic end.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} ends {character}'s life without hesitation!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is fatally defeated by the {enemy}! Game over!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls dead at the {enemy}'s feet... humiliating.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} overpowers and kills {character}! Fatal blow!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is slain! The {enemy} stands victorious over the corpse.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't survive the {enemy}... died fighting, at least.", MessageCategory.COMBAT_DEFEAT),
        Message("Death! {character} falls to the {enemy}! Another fallen hero!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is defeated fatally by the {enemy}! No mercy!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} strikes down {character} permanently... it's all over.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes against the strong {enemy}! Outmatched!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} meets death courtesy of the {enemy}! Final moments!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} claims {character}'s life in battle! Another victim!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is overcome and slain by the {enemy}... tragic.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls defeated, death inevitable... fade to black.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves too much. {character} dies in battle.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is brutally defeated by the {enemy}! No survivors!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't withstand the {enemy}'s overwhelming power... fatal.", MessageCategory.COMBAT_DEFEAT),
        Message("Fatal defeat! {character} is slain by the {enemy}! RIP!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes in combat with the {enemy}! Final stand!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} delivers death to {character}! Swift and merciless!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is struck down and killed by the {enemy}... tragedy.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls victim to the {enemy}'s overwhelming might!", MessageCategory.COMBAT_DEFEAT),
        Message("Game over for {character}. The {enemy} wins fatally. The end.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is defeated and dies at the {enemy}'s hands... cruel fate.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} slays {character} without mercy! Another corpse!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't overcome the {enemy}. Death comes for all.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls in battle, killed by the {enemy}! Fatal!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes before the {enemy}... lights out.", MessageCategory.COMBAT_DEFEAT),
        Message("A crushing blow! {character} dies to the {enemy}! Fatal strike!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is slain by the powerful {enemy}! Overwhelming force!", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} overpowers {character} fatally... no hope left.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} meets their demise against the {enemy}! Final breath!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is killed in combat by the {enemy}! Another fallen!", MessageCategory.COMBAT_DEFEAT),
        Message("Death claims {character}. The {enemy} prevails. Game over.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} couldn't defeat the {enemy}. RIP. Better luck next time.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} defeats {character} with deadly force! Brutal ending!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is overcome and dies to the {enemy}... final moments.", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls slain before the victorious {enemy}!", MessageCategory.COMBAT_DEFEAT),
        Message("Fatal encounter! {character} is killed by the {enemy}! No escape!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} perishes at the {enemy}'s cruel hands... merciless.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} proves superior and kills {character}! Overwhelming!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} is defeated fatally by the {enemy}! Game over!", MessageCategory.COMBAT_DEFEAT),
        Message("{character} falls dead in combat with the {enemy}... tragic.", MessageCategory.COMBAT_DEFEAT),
        Message("The {enemy} claims victory over the slain {character}! Brutal!", MessageCategory.COMBAT_DEFEAT)
    )

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
            "{character} CRITICALLY FAILS against the {enemy} and is utterly destroyed! Total disaster!",
            "{character} makes a FATAL MISTAKE! The {enemy} strikes them down instantly! One-shot!",
            "CRITICAL FAILURE! {character} is annihilated by the {enemy} in embarrassing fashion!",
            "{character} stumbles badly at the worst moment and the {enemy} capitalizes brutally!",
            "DEVASTATING DEFEAT! {character} is obliterated by the {enemy}! Crushing!",
            "{character} couldn't have done worse. The {enemy} destroys them utterly!",
            "A CATASTROPHIC blunder! {character} is slain instantly by the {enemy}! Fatal!",
            "{character} trips at the absolute worst moment. The {enemy} ends them! Brutal!",
            "CRITICAL DISASTER! {character} is crushed by the {enemy}! Overwhelming!",
            "{character}'s luck runs completely out. The {enemy} deals a fatal critical! RIP!"
        )
        Message(messages[index % messages.size], MessageCategory.COMBAT_CRITICAL_DEFEAT)
    }

    private fun generateFleeMessages() = List(100) { index ->
        val messages = listOf(
            "{character} fights the {enemy} desperately but is forced to retreat in panic!",
            "{character} barely escapes from the {enemy} with their life! Close call!",
            "{character} flees from the {enemy} to fight another day! Smart move!",
            "{character} realizes the {enemy} is way too strong and runs! Tactical retreat!",
            "Tactical retreat! {character} escapes the {enemy} with strategic withdrawal!",
            "{character} disengages from combat with the {enemy} before it's too late!",
            "{character} makes a hasty escape from the {enemy}! Running for dear life!",
            "{character} retreats from the overwhelming {enemy} while they still can!",
            "{character} survives by fleeing the {enemy}! Live to fight another day!",
            "{character} wisely withdraws from the {enemy}! Better safe than sorry!"
        )
        Message(messages[index % messages.size], MessageCategory.COMBAT_FLEE)
    }

    private fun generateRestFullMessages() = List(100) { index ->
        val messages = listOf(
            "{character} rests peacefully and recovers {hp} HP! Feeling refreshed and ready!",
            "{character} finds a perfectly cozy spot and fully recovers! Best sleep ever!",
            "{character} sleeps soundly, waking fully refreshed and energized! Good as new!",
            "{character} takes a well-deserved rest and heals completely! Back to full strength!",
            "{character} recovers all {hp} HP from a good night's sleep! Rejuvenated!",
            "{character} rests by a warm fire and fully heals! Comfortable and safe!",
            "{character} enjoys a peaceful rest, recovering {hp} HP! Sweet dreams!",
            "{character} sleeps deeply and wakes at full health! Perfectly rested!",
            "{character} finds the perfect resting spot and heals completely! Ideal conditions!",
            "{character} dreams of victory and recovers all HP! Optimistic outlook!"
        )
        Message(messages[index % messages.size], MessageCategory.REST_FULL)
    }

    private fun generateRestPartialMessages() = List(100) { index ->
        val messages = listOf(
            "{character} rests uncomfortably but only recovers {hp} HP... could be better.",
            "{character} has a fitful sleep, regaining only {hp} HP. Not very restful.",
            "{character} rests uncomfortably and heals {hp} HP. Better than nothing.",
            "{character} doesn't sleep well, only recovering {hp} HP. Restless night.",
            "{character} takes a short rest and regains {hp} HP. Quick break.",
            "{character} rests on cold ground, healing {hp} HP. Not ideal conditions.",
            "{character} has a restless night, recovering only {hp} HP. Poor sleep.",
            "{character} wakes still tired, having healed {hp} HP. Could use more rest.",
            "{character} rests poorly and gains {hp} HP back. Suboptimal conditions.",
            "{character} struggles to sleep, recovering {hp} HP. Uncomfortable spot."
        )
        Message(messages[index % messages.size], MessageCategory.REST_PARTIAL)
    }

    private fun generateLootExcellentMessages() = List(100) { index ->
        val messages = listOf(
            "{character} finds excellent loot: {gold} gold and {item}! What a haul!",
            "JACKPOT! {character} discovers {gold} gold and {item}! Lucky day!",
            "{character} strikes it rich with {gold} gold and {item}! Amazing find!",
            "Amazing find! {character} loots {gold} gold and {item}! Score!",
            "{character} hits the motherload: {gold} gold plus {item}! Incredible!",
            "Incredible loot! {character} finds {gold} gold and {item}! Treasure!",
            "{character} discovers a treasure trove: {gold} gold and {item}! Jackpot!",
            "What a haul! {character} finds {gold} gold and {item}! Perfect!",
            "{character} stumbles upon riches: {gold} gold and {item}! So lucky!",
            "Fantastic! {character} loots {gold} gold and {item}! Great success!"
        )
        Message(messages[index % messages.size], MessageCategory.LOOT_EXCELLENT)
    }

    private fun generateLootGoodMessages() = List(100) { index ->
        val messages = listOf(
            "{character} finds decent loot: {gold} gold! Not bad at all!",
            "{character} discovers {gold} gold! Solid find!",
            "Good find! {character} loots {gold} gold! Better than nothing!",
            "{character} finds a nice stash of {gold} gold! Decent haul!",
            "{character} comes across {gold} gold! Respectable amount!",
            "Not bad! {character} finds {gold} gold! Could be worse!",
            "{character} discovers {gold} gold in the area! Fair loot!",
            "{character} loots {gold} gold from the remains! Acceptable!",
            "{character} finds {gold} gold lying around! Nice surprise!",
            "Decent haul! {character} collects {gold} gold! Worth it!"
        )
        Message(messages[index % messages.size], MessageCategory.LOOT_GOOD)
    }

    private fun generateLootPoorMessages() = List(100) { index ->
        val messages = listOf(
            "{character} finds very little: only {gold} gold... disappointing.",
            "{character} barely finds anything. Just {gold} gold. Unfortunate.",
            "Slim pickings. {character} finds {gold} gold. Better than nothing.",
            "{character} only discovers {gold} gold. Hardly worth it.",
            "Not much here... {character} finds {gold} gold. Sad.",
            "{character} finds a measly {gold} gold. Pathetic loot.",
            "Poor loot. {character} only gets {gold} gold. Disappointing.",
            "{character} finds {gold} gold. Disappointing. Expected more.",
            "Barely worth it. {character} finds {gold} gold. Minimal.",
            "{character} scrounges up {gold} gold. Could be better."
        )
        Message(messages[index % messages.size], MessageCategory.LOOT_POOR)
    }

    private fun generateLootNothingMessages() = List(100) { index ->
        val messages = listOf(
            "{character} finds absolutely nothing! Empty-handed! What a waste!",
            "{character} searches thoroughly but comes up empty-handed. Nothing.",
            "Nothing! {character} finds zilch. Nada. Zero. Empty.",
            "{character} finds no loot whatsoever. Completely empty.",
            "Complete waste of time. {character} finds nothing. Disappointing.",
            "{character} searches thoroughly but finds nothing at all. Empty.",
            "Empty! {character} finds not a single coin. Totally empty.",
            "{character} has no luck finding loot today. Nothing.",
            "Nothing to loot here. {character} moves on empty-handed.",
            "{character} finds nothing of value whatsoever. Unfortunate."
        )
        Message(messages[index % messages.size], MessageCategory.LOOT_NOTHING)
    }

    private fun generateExplorationVisitMessages() = List(100) { index ->
        val messages = listOf(
            "{character} visits {location} and takes in all the sights with wonder and curiosity.",
            "{character} strolls through {location}, enjoying the peaceful atmosphere and scenery.",
            "{character} explores {location}, admiring the beautiful scenery all around.",
            "{character} wanders around {location}, observing the locals going about their daily business.",
            "{character} passes through {location} without incident, enjoying the journey.",
            "{character} relaxes in {location} for a while, taking a much-needed break.",
            "{character} explores the winding streets of {location}, discovering hidden corners.",
            "{character} visits the local landmarks in {location}, taking in the culture.",
            "{character} takes a leisurely walk through {location}, enjoying the fresh air.",
            "{character} spends some quality time exploring {location} at their own pace.",
            "{character} enjoys the peaceful atmosphere of {location}, feeling content.",
            "{character} wanders the paths of {location}, discovering interesting sights.",
            "{character} observes the daily life in {location}, learning about the place.",
            "{character} takes in the beauty of {location}, appreciating the scenery.",
            "{character} strolls casually through {location}, lost in peaceful thoughts.",
            "{character} explores {location} at a relaxed pace, enjoying the moment.",
            "{character} visits {location} to see what it has to offer, curious.",
            "{character} walks around {location}, enjoying the view from all angles.",
            "{character} spends some time in {location}, making mental notes.",
            "{character} explores the area around {location}, discovering new spots.",
            "{character} takes a break to enjoy {location}, relaxing completely.",
            "{character} wanders through {location}, taking it all in slowly.",
            "{character} visits {location} for a change of scenery, refreshing.",
            "{character} explores the surroundings of {location}, curious about everything.",
            "{character} strolls through {location} peacefully, feeling at ease.",
            "{character} visits {location} to relax and observe, no rush.",
            "{character} explores {location} without any particular goal, just wandering.",
            "{character} takes a moment to appreciate {location}, soaking it in.",
            "{character} wanders around {location} aimlessly, enjoying the freedom.",
            "{character} explores {location}, finding it pleasant and welcoming.",
            "{character} visits {location} to stretch their legs, getting exercise.",
            "{character} walks through {location}, enjoying the fresh air and sunshine.",
            "{character} explores {location} at their own pace, no hurry.",
            "{character} visits {location} to take in the sights and sounds.",
            "{character} spends time wandering through {location}, discovering corners.",
            "{character} explores the interesting spots in {location}, intrigued.",
            "{character} visits {location}, feeling at peace with the world.",
            "{character} takes a tour of {location}, seeing all it offers.",
            "{character} wanders {location}, observing everything with interest.",
            "{character} explores {location} with curiosity, wanting to learn.",
            "{character} visits {location} and finds it charming and lovely.",
            "{character} strolls through {location}, lost in thought and reflection.",
            "{character} explores {location}, enjoying the experience thoroughly.",
            "{character} visits {location} to see what's new, curious.",
            "{character} wanders around {location}, taking mental notes of details.",
            "{character} explores {location} thoroughly, leaving no stone unturned.",
            "{character} visits {location} and appreciates its unique character.",
            "{character} takes time to explore {location} properly and carefully.",
            "{character} wanders through {location}, admiring the fine details.",
            "{character} visits {location} for the simple pleasure of it.",
            "{character} explores {location}, soaking in the atmosphere completely.",
            "{character} strolls around {location}, feeling content and happy.",
            "{character} visits {location} and enjoys the tranquility it offers.",
            "{character} explores {location} with interest, discovering new things.",
            "{character} wanders {location}, discovering small wonders everywhere.",
            "{character} visits {location} to clear their mind and relax.",
            "{character} explores {location}, finding new perspectives on life.",
            "{character} takes a peaceful walk through {location}, contemplative.",
            "{character} visits {location} and feels refreshed and renewed.",
            "{character} explores {location}, noticing all the little things.",
            "{character} wanders through {location}, appreciating nature's beauty.",
            "{character} visits {location} to enjoy the scenery and views.",
            "{character} explores {location} with a sense of wonder and awe.",
            "{character} strolls casually around {location}, taking it easy.",
            "{character} visits {location} and finds it welcoming and warm.",
            "{character} explores {location}, making memories to cherish.",
            "{character} wanders {location}, enjoying the moment completely.",
            "{character} visits {location} to experience its charm firsthand.",
            "{character} explores {location} with an open mind and heart.",
            "{character} takes a quiet stroll through {location}, peaceful.",
            "{character} visits {location} and enjoys the peace it brings.",
            "{character} explores {location}, learning about the area's history.",
            "{character} wanders around {location}, feeling adventurous today.",
            "{character} visits {location} to broaden their horizons and knowledge.",
            "{character} explores {location}, finding inspiration everywhere.",
            "{character} strolls through {location}, deep in thought and wonder.",
            "{character} visits {location} and discovers its secrets gradually.",
            "{character} explores {location} with keen eyes, observing.",
            "{character} wanders {location}, enjoying the freedom of exploration.",
            "{character} visits {location} to experience something new today.",
            "{character} explores {location}, creating stories and memories.",
            "{character} takes a meandering path through {location}, unhurried.",
            "{character} visits {location} and feels alive and present.",
            "{character} explores {location}, collecting precious memories.",
            "{character} wanders around {location}, enjoying peaceful solitude.",
            "{character} visits {location} to appreciate its natural beauty.",
            "{character} explores {location} with enthusiasm and excitement.",
            "{character} strolls through {location}, perfectly content with life.",
            "{character} visits {location} and finds joy in simplicity.",
            "{character} explores {location}, savoring every moment of the experience.",
            "{character} wanders {location}, embracing the journey ahead.",
            "{character} visits {location} to feel connected to the world.",
            "{character} explores {location}, finding peace and purpose within.",
            "{character} takes a final look around {location} before moving on.",
            "{character} visits {location} one more time, then continues their journey.",
            "{character} explores {location} completely, then heads to new adventures.",
            "{character} wanders through {location}, satisfied with the visit today.",
            "{character} visits {location} and leaves with fond memories to treasure.",
            "{character} explores {location} fully before departing to other places.",
            "{character} bids farewell to {location} and moves forward confidently."
        )
        Message(messages[index % messages.size], MessageCategory.EXPLORATION_VISIT)
    }

    private val explorationVisitMessages = generateExplorationVisitMessages()
}
