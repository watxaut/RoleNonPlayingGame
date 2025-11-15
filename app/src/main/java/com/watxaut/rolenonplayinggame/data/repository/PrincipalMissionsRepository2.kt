package com.watxaut.rolenonplayinggame.data.repository

import com.watxaut.rolenonplayinggame.domain.model.*

/**
 * Continuation of Principal Missions Repository
 * Contains missions for remaining job classes
 */
object PrincipalMissionsRepository2 {

    // ========================================
    // ROGUE MISSIONS
    // ========================================

    val ROGUE_MISSION_1 = PrincipalMission(
        id = "pm_rogue_1",
        name = "The Great Heist",
        description = "Infiltrate Havenmoor's vault and uncover what secrets the Council of Elders is hiding",
        jobClass = JobClass.ROGUE,
        loreRegion = LoreRegion.HEARTLANDS,
        steps = listOf(
            MissionStep(
                id = "pm_rogue_1_step_1",
                stepNumber = 1,
                name = "Case the Vault",
                description = "Survey Haven's vault location and security",
                locationRequirement = "heartlands_havenmoor",
                loreText = "The vault sits beneath the Council Hall, protected by ancient wards. No one has successfully robbed it—but you'll be the first."
            ),
            MissionStep(
                id = "pm_rogue_1_step_2",
                stepNumber = 2,
                name = "Acquire the Key",
                description = "Steal or forge a key from a Council Elder",
                locationRequirement = "heartlands_havenmoor",
                loreText = "The key isn't metal—it's a magical signature. Each Elder carries a fragment. You need at least one."
            ),
            MissionStep(
                id = "pm_rogue_1_step_3",
                stepNumber = 3,
                name = "Disable the Wards",
                description = "Find a way to bypass the magical defenses",
                locationRequirement = "heartlands_havenmoor",
                loreText = "The wards are old—from the Founding Age. They're not meant to keep thieves out. They're meant to keep something IN."
            ),
            MissionStep(
                id = "pm_rogue_1_step_4",
                stepNumber = 4,
                name = "The Vault Door",
                description = "Crack the final lock on the vault",
                locationRequirement = "heartlands_havenmoor",
                loreText = "Inside, you find not gold, but records. Documents about every person Called to Aethermoor. The Council has been watching everyone from the beginning."
            )
        ),
        bossBattle = BossBattle(
            bossName = "The Warden (Projection)",
            bossLevel = 12,
            bossDescription = "A magical guardian that protects Havenmoor's deepest secrets",
            location = "heartlands_havenmoor",
            loreText = "'Thief,' a voice booms—neither hostile nor kind. 'You seek the truth. Very well. I am the Warden, protector of Havenmoor. The Council guards these records because knowledge of the Calling can drive people mad. Some truths are burdens.'"
        ),
        winConditions = WinConditions(
            minimumLevel = 10,
            requiredStats = mapOf(StatType.AGILITY to 14, StatType.INTELLIGENCE to 10)
        ),
        rewards = PrincipalMissionRewards(
            experience = 1200,
            gold = 800,
            guaranteedItems = listOf("vault_key_fragment", "calling_records"),
            rareItemChance = 0.30f,
            rareItemPool = listOf("shadow_cloak", "vault_breaker_tools", "elder_ring")
        ),
        loreFragment = "The Council of Elders maintains records of everyone Called to Aethermoor. They know more about the island's purpose than they reveal. The Warden is real—not myth—and it protects Havenmoor from both external and internal threats."
    )

    val ROGUE_MISSION_2 = PrincipalMission(
        id = "pm_rogue_2",
        name = "The Bone Empress's Treasury",
        description = "Rob the tomb of the Bone Empress and escape with her legendary treasures",
        jobClass = JobClass.ROGUE,
        loreRegion = LoreRegion.ASHENVEIL_DESERT,
        steps = listOf(
            MissionStep(
                id = "pm_rogue_2_step_1",
                stepNumber = 1,
                name = "Tomb Raiders",
                description = "Learn from failed treasure hunters at Sandstone Bazaar",
                locationRequirement = "ashenveil_sandstone_bazaar",
                loreText = "'The tomb is trapped, every inch of it,' a scarred treasure hunter warns. 'But the real danger is HER. She commands the dead. Take her crown, and you command them too.'"
            ),
            MissionStep(
                id = "pm_rogue_2_step_2",
                stepNumber = 2,
                name = "The Bone Canyons",
                description = "Navigate the maze-like Bone Canyons",
                locationRequirement = "ashenveil_bone_canyons",
                loreText = "Skeletal warriors patrol the ravines. They don't attack—they guide. They're leading you to their queen."
            ),
            MissionStep(
                id = "pm_rogue_2_step_3",
                stepNumber = 3,
                name = "Disarm the Traps",
                description = "Navigate the trapped corridors of the Empress's tomb",
                locationRequirement = "ashenveil_bone_canyons",
                loreText = "Poison darts, collapsing floors, cursed idols—the desert mages who built this didn't want anyone reaching the inner sanctum."
            ),
            MissionStep(
                id = "pm_rogue_2_step_4",
                stepNumber = 4,
                name = "The Throne Room",
                description = "Reach the Bone Empress on her throne",
                locationRequirement = "ashenveil_bone_canyons",
                loreText = "She sits perfectly preserved, crown upon her desiccated head. Her eyes open. 'Finally,' she whispers. 'Someone clever enough to reach me.'"
            )
        ),
        bossBattle = BossBattle(
            bossName = "The Bone Empress",
            bossLevel = 32,
            bossDescription = "Preserved corpse of an ancient desert queen, animated by necromantic magic",
            location = "ashenveil_bone_canyons",
            loreText = "'Take my crown, thief,' she commands her skeletal guards to stand down. 'I have ruled the dead for too long. The crown grants power over death itself, but it comes with a price—eternal duty. Will you pay it, or will you prove yourself wiser than I was?'"
        ),
        winConditions = WinConditions(
            minimumLevel = 28,
            requiredStats = mapOf(StatType.AGILITY to 26, StatType.LUCK to 20)
        ),
        rewards = PrincipalMissionRewards(
            experience = 4800,
            gold = 3000,
            guaranteedItems = listOf("bone_empress_treasure"),
            rareItemChance = 0.70f,
            rareItemPool = listOf("necromancer_crown", "bone_empress_dagger", "desert_queen_riches")
        ),
        loreFragment = "The Bone Empress was a desert mage-queen who achieved immortality through necromancy, only to realize too late that eternity is a curse. Her crown grants power over death, but binds the wearer to guard the desert's tombs forever."
    )

    val ROGUE_MISSION_3 = PrincipalMission(
        id = "pm_rogue_3",
        name = "Wrecker's Fortune",
        description = "Unite the pirates of Wrecker's Cove and claim the legendary pirate hoard",
        jobClass = JobClass.ROGUE,
        loreRegion = LoreRegion.STORMCOAST_REACHES,
        steps = listOf(
            MissionStep(
                id = "pm_rogue_3_step_1",
                stepNumber = 1,
                name = "Pirate Politics",
                description = "Navigate the power struggles at Wrecker's Cove",
                locationRequirement = "stormcoast_wreckers_cove",
                loreText = "Three captains vie for control of the Cove. None trust each other. You'll need to outmaneuver them all."
            ),
            MissionStep(
                id = "pm_rogue_3_step_2",
                stepNumber = 2,
                name = "The First Captain",
                description = "Win the loyalty or eliminate the first captain",
                locationRequirement = "stormcoast_wreckers_cove",
                loreText = "Captain Redwave falls for your deception. One down, two to go."
            ),
            MissionStep(
                id = "pm_rogue_3_step_3",
                stepNumber = 3,
                name = "The Second Captain",
                description = "Outmaneuver the second captain through cunning",
                locationRequirement = "stormcoast_wreckers_cove",
                loreText = "Captain Blackwater is smarter. This one requires finesse, not force."
            ),
            MissionStep(
                id = "pm_rogue_3_step_4",
                stepNumber = 4,
                name = "The Pirate King's Challenge",
                description = "Face the final captain who claims the legendary hoard",
                locationRequirement = "stormcoast_wreckers_cove",
                loreText = "Captain Ironheart knows where the greatest pirate treasure lies—accumulated over centuries of Aethermoor's trapped sailors."
            )
        ),
        bossBattle = BossBattle(
            bossName = "Captain Ironheart",
            bossLevel = 44,
            bossDescription = "Cunning pirate lord who controls Wrecker's Cove through wit and steel",
            location = "stormcoast_wreckers_cove",
            loreText = "'Well played, rogue,' Ironheart grins, blade drawn. 'You've bested my rivals. But I didn't become lord of Wrecker's Cove by being easy to kill. Let's see if your cunning matches your ambition!'"
        ),
        winConditions = WinConditions(
            minimumLevel = 40,
            requiredStats = mapOf(StatType.AGILITY to 32, StatType.CHARISMA to 26)
        ),
        rewards = PrincipalMissionRewards(
            experience = 7500,
            gold = 5000,
            guaranteedItems = listOf("pirate_lords_treasure"),
            rareItemChance = 0.80f,
            rareItemPool = listOf("ironheart_cutlass", "pirate_king_coat", "legendary_pirate_hoard")
        ),
        loreFragment = "Wrecker's Cove exists because Aethermoor has trapped sailors for centuries. The pirates aren't villains—they're survivors, making the best of their eternal imprisonment. The treasure they've accumulated represents generations of lost ships and dashed hopes of escape."
    )

    // ========================================
    // ARCHER MISSIONS
    // ========================================

    val ARCHER_MISSION_1 = PrincipalMission(
        id = "pm_archer_1",
        name = "The Silver Hunt",
        description = "Track and face Silvermane, the legendary dire wolf of Moonwell Glade",
        jobClass = JobClass.ARCHER,
        loreRegion = LoreRegion.THORNWOOD_WILDS,
        steps = listOf(
            MissionStep(
                id = "pm_archer_1_step_1",
                stepNumber = 1,
                name = "The Ranger's Tale",
                description = "Learn about Silvermane from Thornguard rangers",
                locationRequirement = "thornwood_thornguard_outpost",
                loreText = "'Silvermane is no ordinary wolf,' the ranger explains. 'It guards Moonwell Glade with purpose. Kill it, and something precious will be lost.'"
            ),
            MissionStep(
                id = "pm_archer_1_step_2",
                stepNumber = 2,
                name = "Track the Alpha",
                description = "Follow Silvermane's tracks through the Thornwood",
                locationRequirement = "thornwood_wilds",
                loreText = "The tracks are enormous, and they lead in circles around Moonwell Glade. Silvermane patrols with discipline, like a soldier."
            ),
            MissionStep(
                id = "pm_archer_1_step_3",
                stepNumber = 3,
                name = "The Moonwell at Night",
                description = "Observe the glade under moonlight",
                locationRequirement = "thornwood_moonwell_glade",
                loreText = "Under the moon, the glade's magic becomes visible. Silvermane howls—not in aggression, but in mourning."
            ),
            MissionStep(
                id = "pm_archer_1_step_4",
                stepNumber = 4,
                name = "The Challenge",
                description = "Face Silvermane in honorable combat",
                locationRequirement = "thornwood_moonwell_glade",
                loreText = "Silvermane stands before you, unafraid. Its eyes hold intelligence. This is not prey—this is an equal."
            )
        ),
        bossBattle = BossBattle(
            bossName = "Silvermane, the Dire Alpha",
            bossLevel = 15,
            bossDescription = "Massive dire wolf with moonlit fur, guardian of Moonwell Glade",
            location = "thornwood_moonwell_glade",
            loreText = "As you face Silvermane, you realize this isn't a fight to the death—it's a test. The wolf fights with honor, testing your skill. When you prove yourself worthy, it backs down, howling in respect. You've earned the right to enter Moonwell Glade freely."
        ),
        winConditions = WinConditions(
            minimumLevel = 13,
            requiredStats = mapOf(StatType.AGILITY to 16, StatType.STRENGTH to 10)
        ),
        rewards = PrincipalMissionRewards(
            experience = 1800,
            gold = 600,
            guaranteedItems = listOf("silvermane_fang"),
            rareItemChance = 0.35f,
            rareItemPool = listOf("moonlight_bow", "dire_wolf_cloak", "alpha_tooth_necklace")
        ),
        loreFragment = "Silvermane is a guardian placed by the elves to protect Moonwell Glade. It doesn't kill trespassers—it tests them. Only those who prove themselves through skill and honor are permitted to access the glade's ancient magic."
    )

    val ARCHER_MISSION_2 = PrincipalMission(
        id = "pm_archer_2",
        name = "The Sky Monastery",
        description = "Climb to the highest peak and discover the secrets of the Sky Monastery",
        jobClass = JobClass.ARCHER,
        loreRegion = LoreRegion.FROSTPEAK_MOUNTAINS,
        steps = listOf(
            MissionStep(
                id = "pm_archer_2_step_1",
                stepNumber = 1,
                name = "The Climb Begins",
                description = "Ascend Avalanche Pass toward the peaks",
                locationRequirement = "frostpeak_avalanche_pass",
                loreText = "The air thins as you climb. Few dare venture this high. Those who do rarely return."
            ),
            MissionStep(
                id = "pm_archer_2_step_2",
                stepNumber = 2,
                name = "The Yeti Territory",
                description = "Navigate past Avalanche, the Yeti Patriarch",
                locationRequirement = "frostpeak_avalanche_pass",
                loreText = "The massive yeti watches from above but doesn't attack. It seems to recognize that you're headed somewhere specific."
            ),
            MissionStep(
                id = "pm_archer_2_step_3",
                stepNumber = 3,
                name = "The Monastery Gates",
                description = "Reach the abandoned Sky Monastery",
                locationRequirement = "frostpeak_sky_monastery",
                loreText = "The monastery sits on a peak that pierces the clouds. Ancient monks once meditated here, seeking enlightenment at the top of the world."
            ),
            MissionStep(
                id = "pm_archer_2_step_4",
                stepNumber = 4,
                name = "The Frozen Wisdom",
                description = "Explore the monastery's preserved halls",
                locationRequirement = "frostpeak_sky_monastery",
                loreText = "Books and scrolls, perfectly preserved by ice. The monks recorded everything—including prophecies about Aethermoor's future."
            )
        ),
        bossBattle = BossBattle(
            bossName = "Avalanche, the Yeti Patriarch",
            bossLevel = 28,
            bossDescription = "Ancient yeti that protects the path to the Sky Monastery",
            location = "frostpeak_avalanche_pass",
            loreText = "'You return,' Avalanche's roar echoes across the mountains. 'You have seen the monastery's secrets. The monks prophesied that one day, someone would climb to learn the truth. The Sundering was not an accident—it was a seal. Something beneath the island was imprisoned.'"
        ),
        winConditions = WinConditions(
            minimumLevel = 25,
            requiredStats = mapOf(StatType.AGILITY to 24, StatType.VITALITY to 18)
        ),
        rewards = PrincipalMissionRewards(
            experience = 4000,
            gold = 1500,
            guaranteedItems = listOf("monastery_scrolls"),
            rareItemChance = 0.50f,
            rareItemPool = listOf("sky_bow", "monk_robes", "frozen_wisdom_tome")
        ),
        loreFragment = "The monks of the Sky Monastery prophesied the Sundering before it happened. They recorded that Aethermoor itself is a seal—a prison created to contain something dangerous. The Calling brings worthy souls to maintain the seal."
    )

    val ARCHER_MISSION_3 = PrincipalMission(
        id = "pm_archer_3",
        name = "The Tempest's Eye",
        description = "Navigate the heart of the eternal storms and discover the source",
        jobClass = JobClass.ARCHER,
        loreRegion = LoreRegion.STORMCOAST_REACHES,
        steps = listOf(
            MissionStep(
                id = "pm_archer_3_step_1",
                stepNumber = 1,
                name = "Storm Patterns",
                description = "Study the storm movements from Lighthouse Point",
                locationRequirement = "stormcoast_lighthouse_point",
                loreText = "The storms aren't random—they follow precise patterns, like something is conducting them."
            ),
            MissionStep(
                id = "pm_archer_3_step_2",
                stepNumber = 2,
                name = "Into the Tempest",
                description = "Venture into the fiercest part of the storm",
                locationRequirement = "stormcoast_reaches",
                loreText = "Lightning strikes where you just stood. The storm is aware of you, testing you."
            ),
            MissionStep(
                id = "pm_archer_3_step_3",
                stepNumber = 3,
                name = "The Eye",
                description = "Reach the calm center of the eternal storm",
                locationRequirement = "stormcoast_reaches",
                loreText = "Perfect calm. In the center, an ancient structure rises from the water—a lighthouse older than any other on Aethermoor."
            ),
            MissionStep(
                id = "pm_archer_3_step_4",
                stepNumber = 4,
                name = "The Storm Beacon",
                description = "Investigate the ancient lighthouse at the storm's heart",
                locationRequirement = "stormcoast_reaches",
                loreText = "This lighthouse predates the Founding. It's the source—the beacon that creates the eternal storms, powered by something beneath the waves."
            )
        ),
        bossBattle = BossBattle(
            bossName = "Tempest, the Storm Leviathan",
            bossLevel = 50,
            bossDescription = "Massive sea serpent that embodies the storm itself",
            location = "stormcoast_reaches",
            loreText = "'Archer,' Tempest's voice is the thunder itself. 'You've reached the beacon. Know this: the storms exist to protect the world beyond Aethermoor. What sleeps beneath this island must never awaken. I am one of three guardians who maintain the seal.'"
        ),
        winConditions = WinConditions(
            minimumLevel = 46,
            requiredStats = mapOf(StatType.AGILITY to 38, StatType.LUCK to 28)
        ),
        rewards = PrincipalMissionRewards(
            experience = 10000,
            gold = 4500,
            guaranteedItems = listOf("storm_beacon_fragment"),
            rareItemChance = 0.75f,
            rareItemPool = listOf("tempest_bow", "storm_walker_cloak", "lightning_quiver")
        ),
        loreFragment = "The eternal storms are powered by an ancient beacon at their center, predating even the Founding Age. Tempest maintains the storms not as a prison, but as a necessary barrier to protect the outside world from something imprisoned beneath Aethermoor."
    )

    // ========================================
    // Get All Additional Missions
    // ========================================

    fun getAllAdditionalMissions(): List<PrincipalMission> {
        return listOf(
            // Rogues
            ROGUE_MISSION_1,
            ROGUE_MISSION_2,
            ROGUE_MISSION_3,

            // Archers
            ARCHER_MISSION_1,
            ARCHER_MISSION_2,
            ARCHER_MISSION_3
        )
    }
}
