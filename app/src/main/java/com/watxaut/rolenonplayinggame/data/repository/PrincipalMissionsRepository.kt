package com.watxaut.rolenonplayinggame.data.repository

import com.watxaut.rolenonplayinggame.domain.model.*

/**
 * Repository containing all 36 principal missions (3 per job class).
 * Missions are deeply integrated with WORLD_LORE.md and follow TONE_AND_STYLE_GUIDE.md.
 *
 * Each mission has:
 * - 4+ steps with 2% discovery chance each
 * - Boss battle with 2% encounter chance after all steps
 * - Win conditions (level, equipment, stats, boss defeat)
 * - Rewards (XP, gold, guaranteed items, rare item chance)
 * - Lore fragments that reveal Aethermoor's mysteries
 */
object PrincipalMissionsRepository {

    // ========================================
    // WARRIOR MISSIONS
    // ========================================

    private val WARRIOR_MISSION_1 = PrincipalMission(
        id = "pm_warrior_1",
        name = "The Ironhide Challenge",
        description = "Prove your strength by hunting the legendary Ancient Boar, Ironhide, in the Whispering Grove.",
        jobClass = JobClass.WARRIOR,
        loreRegion = LoreRegion.HEARTLANDS,
        steps = listOf(
            MissionStep(
                id = "pm_warrior_1_step_1",
                stepNumber = 1,
                name = "Hear the Tales",
                description = "Listen to stories of Ironhide from Havenmoor's veterans",
                locationRequirement = "heartlands_havenmoor",
                loreText = "The veterans speak of Ironhide with a mixture of fear and respect. 'It's been alive since before the Sundering,' one grizzled fighter says. 'Its hide has turned to steel over the centuries.'"
            ),
            MissionStep(
                id = "pm_warrior_1_step_2",
                stepNumber = 2,
                name = "Study the Grove",
                description = "Scout the Whispering Grove for signs of Ironhide",
                locationRequirement = "heartlands_whispering_grove",
                loreText = "Trees bear deep gouges from Ironhide's tusks. The ground trembles occasionally—something massive moves beneath the earth. This is no ordinary beast."
            ),
            MissionStep(
                id = "pm_warrior_1_step_3",
                stepNumber = 3,
                name = "Gather Strength",
                description = "Train by defeating lesser boars in the Heartlands",
                locationRequirement = null,
                loreText = "Each battle hones your skills. Warriors before you fell because they underestimated Ironhide. You will not make the same mistake."
            ),
            MissionStep(
                id = "pm_warrior_1_step_4",
                stepNumber = 4,
                name = "The Challenge Issued",
                description = "Find Ironhide's sacred clearing and issue your challenge",
                locationRequirement = "heartlands_whispering_grove",
                loreText = "Ancient standing stones surround the clearing, humming with residual magic. This place predates human settlement. Ironhide has claimed it as his domain."
            )
        ),
        bossBattle = BossBattle(
            bossName = "Ironhide, the Ancient Boar",
            bossLevel = 10,
            bossDescription = "A massive boar with hide as tough as steel, awakened by the Sundering's magic",
            location = "heartlands_whispering_grove",
            loreText = "As Ironhide charges, you see the truth—this creature is a guardian, transformed by ancient magic to protect the standing stones. Your battle is a test, not of strength alone, but of worthiness."
        ),
        winConditions = WinConditions(
            minimumLevel = 8,
            requiredStats = mapOf(StatType.STRENGTH to 10, StatType.VITALITY to 8)
        ),
        rewards = PrincipalMissionRewards(
            experience = 1000,
            gold = 500,
            guaranteedItems = listOf("ironhide_trophy"),
            rareItemChance = 0.30f,
            rareItemPool = listOf("ironhide_plate_armor", "boar_tusk_greatsword")
        ),
        loreFragment = "The standing stones marked sacred places even before the Sundering. Ironhide was once a normal creature, transformed by the cataclysm to become an eternal guardian. By defeating it, you've proven yourself worthy of the ancient trials."
    )

    private val WARRIOR_MISSION_2 = PrincipalMission(
        id = "pm_warrior_2",
        name = "The Frozen Halls",
        description = "Venture into the abandoned Irondelve Hold and uncover what drove the dwarves away",
        jobClass = JobClass.WARRIOR,
        loreRegion = LoreRegion.FROSTPEAK_MOUNTAINS,
        steps = listOf(
            MissionStep(
                id = "pm_warrior_2_step_1",
                stepNumber = 1,
                name = "The Dwarven Mystery",
                description = "Speak with the Irondelve Reclaimers about the vanishing",
                locationRequirement = "frostpeak_reclaimer_camp",
                loreText = "'One day they were there, the next... gone,' the Reclaimer says. 'Tables still set. Forges still burning. Whatever happened, it was sudden.'"
            ),
            MissionStep(
                id = "pm_warrior_2_step_2",
                stepNumber = 2,
                name = "Enter the Hold",
                description = "Brave the frozen entrance to Irondelve Hold",
                locationRequirement = "frostpeak_irondelve_hold",
                loreText = "The massive doors stand open, frozen in place. Inside, everything is as the Reclaimers described—a settlement frozen in time, waiting for masters who will never return."
            ),
            MissionStep(
                id = "pm_warrior_2_step_3",
                stepNumber = 3,
                name = "The Throne Room",
                description = "Find the throne room deep within the hold",
                locationRequirement = "frostpeak_irondelve_hold",
                loreText = "Dwarven runes on the walls glow faintly. They speak of 'the one who came from below' and 'the blue death.' The final entry: 'We must flee or join the ice.'"
            ),
            MissionStep(
                id = "pm_warrior_2_step_4",
                stepNumber = 4,
                name = "The Usurper's Presence",
                description = "Discover signs of the Frost Tyrant's occupation",
                locationRequirement = "frostpeak_irondelve_hold",
                loreText = "Fresh ice formations wind through the ancient halls. The Frost Tyrant has made this place his kingdom, but his claim is built on the tragedy of the dwarves."
            )
        ),
        bossBattle = BossBattle(
            bossName = "The Frost Tyrant",
            bossLevel = 35,
            bossDescription = "Massive frost giant who claims to have driven the dwarves from Irondelve Hold",
            location = "frostpeak_irondelve_hold",
            loreText = "'I did not drive them out,' the Frost Tyrant laughs as you fight. 'Something older did. I merely claimed what they abandoned. But YOU... you are brave enough to hear the truth. The dwarves dug too deep and awakened something that should have stayed sleeping.'"
        ),
        winConditions = WinConditions(
            minimumLevel = 30,
            requiredStats = mapOf(StatType.STRENGTH to 25, StatType.VITALITY to 20)
        ),
        rewards = PrincipalMissionRewards(
            experience = 5000,
            gold = 2000,
            guaranteedItems = listOf("dwarven_hall_key"),
            rareItemChance = 0.50f,
            rareItemPool = listOf("frost_tyrant_crown", "dwarven_runeblade", "irondelve_plate")
        ),
        loreFragment = "The dwarves didn't vanish—they fled. Something beneath Irondelve, awakened by their mining, drove them out. The Frost Tyrant merely occupies the halls, but deep below, something far more dangerous still sleeps."
    )

    private val WARRIOR_MISSION_3 = PrincipalMission(
        id = "pm_warrior_3",
        name = "Storm's End",
        description = "Confront the Storm Leviathan and attempt to end the eternal tempests",
        jobClass = JobClass.WARRIOR,
        loreRegion = LoreRegion.STORMCOAST_REACHES,
        steps = listOf(
            MissionStep(
                id = "pm_warrior_3_step_1",
                stepNumber = 1,
                name = "The Stormwatch Scholars",
                description = "Consult with the Stormwatch about the eternal storms",
                locationRequirement = "stormcoast_lighthouse_point",
                loreText = "'The storms aren't natural,' a mage explains, pointing to ancient charts. 'They're maintained by something in the deep. We call it Tempest.'"
            ),
            MissionStep(
                id = "pm_warrior_3_step_2",
                stepNumber = 2,
                name = "The Drowned Cathedral",
                description = "Explore the Drowned Cathedral at low tide",
                locationRequirement = "stormcoast_drowned_cathedral",
                loreText = "Ancient mosaics show a great serpent coiled around the island. Text beneath reads: 'The Guardian's Vigil.' This creature isn't a monster—it's a protector."
            ),
            MissionStep(
                id = "pm_warrior_3_step_3",
                stepNumber = 3,
                name = "The Storm Crown Legend",
                description = "Search the Shattered Fleet for clues about the Storm Crown",
                locationRequirement = "stormcoast_shattered_fleet",
                loreText = "Captain's logs speak of an artifact that could 'command the tempest itself.' But every ship that sought it was destroyed. The storms protect something."
            ),
            MissionStep(
                id = "pm_warrior_3_step_4",
                stepNumber = 4,
                name = "Challenge the Deep",
                description = "Venture to the deepest waters during a fierce storm",
                locationRequirement = "stormcoast_reaches",
                loreText = "Lightning illuminates something massive beneath the waves. The storm intensifies—Tempest knows you're here."
            )
        ),
        bossBattle = BossBattle(
            bossName = "Tempest, the Storm Leviathan",
            bossLevel = 50,
            bossDescription = "Massive sea serpent wreathed in lightning, guardian of Aethermoor's shores",
            location = "stormcoast_reaches",
            loreText = "'You seek to leave?' Tempest's voice rumbles like thunder. 'You cannot. The island chose you. I am its warden, bound since the Founding to keep the worthy in and the unworthy out. Prove your worth or be swept beneath the waves!'"
        ),
        winConditions = WinConditions(
            minimumLevel = 45,
            requiredStats = mapOf(StatType.STRENGTH to 35, StatType.VITALITY to 30, StatType.AGILITY to 25)
        ),
        rewards = PrincipalMissionRewards(
            experience = 10000,
            gold = 5000,
            guaranteedItems = listOf("tempest_scale"),
            rareItemChance = 0.75f,
            rareItemPool = listOf("storm_crown_fragment", "leviathan_greatsword", "tempest_plate")
        ),
        loreFragment = "Tempest is no mere monster—it is the island's guardian, placed here during the Founding Age to ensure only the worthy can stay on Aethermoor. The storms aren't a prison; they're a filter. By defeating Tempest, you've proven you belong here."
    )

    // ========================================
    // ASSASSIN MISSIONS
    // ========================================

    private val ASSASSIN_MISSION_1 = PrincipalMission(
        id = "pm_assassin_1",
        name = "Shadow's Mark",
        description = "Eliminate the Goblin King Skritch and scatter his forces",
        jobClass = JobClass.ASSASSIN,
        loreRegion = LoreRegion.HEARTLANDS,
        steps = listOf(
            MissionStep(
                id = "pm_assassin_1_step_1",
                stepNumber = 1,
                name = "Study the Target",
                description = "Observe the goblin encampment at Broken Bridge",
                locationRequirement = "heartlands_broken_bridge",
                loreText = "Skritch is cunning—he's united scattered tribes through fear and respect. He wears a crown of scrap metal and wields a stolen knight's blade."
            ),
            MissionStep(
                id = "pm_assassin_1_step_2",
                stepNumber = 2,
                name = "Sow Discord",
                description = "Eliminate key goblin lieutenants without being detected",
                locationRequirement = "heartlands_broken_bridge",
                loreText = "Each lieutenant's death sends ripples through the camp. Goblins argue over succession. Skritch's authority wavers."
            ),
            MissionStep(
                id = "pm_assassin_1_step_3",
                stepNumber = 3,
                name = "The Blade's Origin",
                description = "Discover where Skritch obtained the knight's blade",
                locationRequirement = "heartlands_broken_bridge",
                loreText = "The blade bears the mark of an ancient order of Havenmoor knights. Skritch didn't steal it from a corpse—someone gave it to him. But who would arm a goblin king?"
            ),
            MissionStep(
                id = "pm_assassin_1_step_4",
                stepNumber = 4,
                name = "Infiltrate the Court",
                description = "Sneak into Skritch's throne room",
                locationRequirement = "heartlands_broken_bridge",
                loreText = "Skritch sits alone, polishing the blade. 'They think me simple,' he mutters. 'But I know why the island wants me here. Same reason it wants you.'"
            )
        ),
        bossBattle = BossBattle(
            bossName = "The Goblin King Skritch",
            bossLevel = 8,
            bossDescription = "Unusually intelligent goblin who united the tribes with cunning and steel",
            location = "heartlands_broken_bridge",
            loreText = "Skritch fights with unexpected skill. 'The island calls all of us,' he says between strikes. 'Heroes. Monsters. We're all the same—pieces in a game we don't understand.'"
        ),
        winConditions = WinConditions(
            minimumLevel = 7,
            requiredStats = mapOf(StatType.AGILITY to 12, StatType.LUCK to 8)
        ),
        rewards = PrincipalMissionRewards(
            experience = 800,
            gold = 400,
            guaranteedItems = listOf("skritch_crown", "knight_blade"),
            rareItemChance = 0.25f,
            rareItemPool = listOf("shadow_cloak", "goblin_king_dagger")
        ),
        loreFragment = "Even monsters are called to Aethermoor. Skritch was more than a goblin—he was chosen, just like you. The island's Calling doesn't discriminate between hero and villain."
    )

    private val ASSASSIN_MISSION_2 = PrincipalMission(
        id = "pm_assassin_2",
        name = "The Widow's Web",
        description = "Eliminate the Widow Queen and liberate the Webbed Hollow",
        jobClass = JobClass.ASSASSIN,
        loreRegion = LoreRegion.THORNWOOD_WILDS,
        steps = listOf(
            MissionStep(
                id = "pm_assassin_2_step_1",
                stepNumber = 1,
                name = "Lost Scouts",
                description = "Investigate the disappearance of Thornguard scouts",
                locationRequirement = "thornwood_thornguard_outpost",
                loreText = "'Three scouts, vanished in a week,' the ranger captain says grimly. 'We found web... so much web. And something worse.'"
            ),
            MissionStep(
                id = "pm_assassin_2_step_2",
                stepNumber = 2,
                name = "The Hollow's Edge",
                description = "Scout the entrance to the Webbed Hollow",
                locationRequirement = "thornwood_webbed_hollow",
                loreText = "Cocoons hang from the trees—dozens of them. Some are old, others fresh. The Widow Queen doesn't just feed; she collects."
            ),
            MissionStep(
                id = "pm_assassin_2_step_3",
                stepNumber = 3,
                name = "Free the Survivors",
                description = "Rescue any survivors from the outer webs",
                locationRequirement = "thornwood_webbed_hollow",
                loreText = "You free a trembling merchant. 'She... she talks,' he whispers. 'The spider talks. She said she's waiting for someone. Someone worthy.'"
            ),
            MissionStep(
                id = "pm_assassin_2_step_4",
                stepNumber = 4,
                name = "The Queen's Chamber",
                description = "Navigate to the heart of the Webbed Hollow",
                locationRequirement = "thornwood_webbed_hollow",
                loreText = "The chamber is vast, hung with webs like curtains. At the center, the Widow Queen waits. She's enormous, and her many eyes track your every movement."
            )
        ),
        bossBattle = BossBattle(
            bossName = "The Widow Queen",
            bossLevel = 18,
            bossDescription = "Giant spider of unnatural size and cunning, ruler of the Webbed Hollow",
            location = "thornwood_webbed_hollow",
            loreText = "'Assassin,' she hisses, her voice like rustling leaves. 'I have waited. The forest grows too wild. The corruption spreads. End me, and perhaps you can stop what I cannot.'"
        ),
        winConditions = WinConditions(
            minimumLevel = 15,
            requiredStats = mapOf(StatType.AGILITY to 18, StatType.INTELLIGENCE to 12)
        ),
        rewards = PrincipalMissionRewards(
            experience = 2500,
            gold = 1200,
            guaranteedItems = listOf("widow_fang"),
            rareItemChance = 0.40f,
            rareItemPool = listOf("widow_silk_armor", "venom_dagger", "web_cloak")
        ),
        loreFragment = "The Widow Queen wasn't just a predator—she was a sentinel, trying to contain the Thornwood's corruption. Her death may have consequences you didn't foresee."
    )

    private val ASSASSIN_MISSION_3 = PrincipalMission(
        id = "pm_assassin_3",
        name = "The Wraith Admiral",
        description = "Assassinate the undead pirate Captain Dreadmoor and claim his treasure",
        jobClass = JobClass.ASSASSIN,
        loreRegion = LoreRegion.STORMCOAST_REACHES,
        steps = listOf(
            MissionStep(
                id = "pm_assassin_3_step_1",
                stepNumber = 1,
                name = "Tales of Dreadmoor",
                description = "Hear stories at Wrecker's Cove",
                locationRequirement = "stormcoast_wreckers_cove",
                loreText = "'He tried to escape Aethermoor fifty years ago,' an old pirate croaks. 'The storm took him, but death couldn't hold him. Now he guards the wrecks, forever cursed.'"
            ),
            MissionStep(
                id = "pm_assassin_3_step_2",
                stepNumber = 2,
                name = "The Shattered Fleet",
                description = "Search the shipwrecks for Dreadmoor's flagship",
                locationRequirement = "stormcoast_shattered_fleet",
                loreText = "Ships from every era litter the coast. Dreadmoor's flag—a black skull on crimson—still flies from the largest wreck."
            ),
            MissionStep(
                id = "pm_assassin_3_step_3",
                stepNumber = 3,
                name = "The Ghostly Crew",
                description = "Evade or eliminate Dreadmoor's spectral pirates",
                locationRequirement = "stormcoast_shattered_fleet",
                loreText = "The crew are hollow echoes of men. They attack not from malice, but from duty. Even in death, they serve their captain."
            ),
            MissionStep(
                id = "pm_assassin_3_step_4",
                stepNumber = 4,
                name = "The Captain's Quarters",
                description = "Infiltrate Dreadmoor's rotting flagship",
                locationRequirement = "stormcoast_shattered_fleet",
                loreText = "Charts cover the walls, each showing failed escape routes. One message, carved deep: 'The island will not let us go.'"
            )
        ),
        bossBattle = BossBattle(
            bossName = "Captain Dreadmoor, the Wraith Admiral",
            bossLevel = 42,
            bossDescription = "Cursed pirate captain who tried to escape Aethermoor and failed",
            location = "stormcoast_shattered_fleet",
            loreText = "'Assassin!' Dreadmoor's ghostly form roars. 'You think to kill what's already dead? I tried to leave this cursed island. The storms took me. Now I'm bound here forever. If you defeat me, know this: the island will never let you go either.'"
        ),
        winConditions = WinConditions(
            minimumLevel = 38,
            requiredStats = mapOf(StatType.AGILITY to 30, StatType.LUCK to 25)
        ),
        rewards = PrincipalMissionRewards(
            experience = 7000,
            gold = 3500,
            guaranteedItems = listOf("dreadmoor_compass"),
            rareItemChance = 0.60f,
            rareItemPool = listOf("wraith_admiral_cutlass", "ghost_ship_cloak", "cursed_treasure_map")
        ),
        loreFragment = "Captain Dreadmoor's curse reveals a dark truth: Aethermoor doesn't just prevent escape through storms—it binds those who try. Death is not freedom from this island."
    )

    // ========================================
    // MAGE MISSIONS
    // ========================================

    private val MAGE_MISSION_1 = PrincipalMission(
        id = "pm_mage_1",
        name = "The Eldergrove Secrets",
        description = "Uncover the secrets of the ancient elven civilization in the Thornwood",
        jobClass = JobClass.MAGE,
        loreRegion = LoreRegion.THORNWOOD_WILDS,
        steps = listOf(
            MissionStep(
                id = "pm_mage_1_step_1",
                stepNumber = 1,
                name = "Elven Ruins",
                description = "Study the Eldergrove Ruins",
                locationRequirement = "thornwood_eldergrove_ruins",
                loreText = "Elvish script covers the crumbling walls. You decipher: 'We retreat to the Heart. The Sundering comes. May the tree protect what we cannot save.'"
            ),
            MissionStep(
                id = "pm_mage_1_step_2",
                stepNumber = 2,
                name = "Moonwell Mysteries",
                description = "Investigate the magical Moonwell Glade",
                locationRequirement = "thornwood_moonwell_glade",
                loreText = "The glade thrums with residual magic. Lights dance—not faerie fire, but echoes of powerful enchantments cast long ago."
            ),
            MissionStep(
                id = "pm_mage_1_step_3",
                stepNumber = 3,
                name = "The Guardian Wolf",
                description = "Observe Silvermane, protector of Moonwell Glade",
                locationRequirement = "thornwood_moonwell_glade",
                loreText = "Silvermane doesn't attack—it watches. This dire wolf guards the glade with purpose. Someone or something commands it."
            ),
            MissionStep(
                id = "pm_mage_1_step_4",
                stepNumber = 4,
                name = "The Heart Tree",
                description = "Find the legendary Heart Tree deep in Thornwood",
                locationRequirement = "thornwood_deep_forest",
                loreText = "You find it—an oak of impossible size, older than memory. Its bark glows with runes. This is where the elves made their final stand."
            )
        ),
        bossBattle = BossBattle(
            bossName = "Thornmaw, the Corrupted Treant",
            bossLevel = 20,
            bossDescription = "Once guardian of the forest, now twisted by dark magic",
            location = "thornwood_deep_forest",
            loreText = "'I... guarded... this place,' Thornmaw's voice creaks like breaking wood. 'The elves... entrusted me. But the corruption... too strong. End... my suffering... protect... the Heart...' The corruption isn't natural—something deliberate poisoned this guardian."
        ),
        winConditions = WinConditions(
            minimumLevel = 18,
            requiredStats = mapOf(StatType.INTELLIGENCE to 20, StatType.VITALITY to 12)
        ),
        rewards = PrincipalMissionRewards(
            experience = 2800,
            gold = 1000,
            guaranteedItems = listOf("heart_tree_branch"),
            rareItemChance = 0.45f,
            rareItemPool = listOf("elven_staff", "moonwell_robes", "nature_codex")
        ),
        loreFragment = "The elves foresaw the Sundering and retreated to the Heart Tree for protection. Thornmaw was their guardian, corrupted by the same dark magic that threatens the entire forest. The corruption's source remains unknown."
    )

    private val MAGE_MISSION_2 = PrincipalMission(
        id = "pm_mage_2",
        name = "Sands of Power",
        description = "Unearth the buried city of Sunscorch and claim its magical artifacts",
        jobClass = JobClass.MAGE,
        loreRegion = LoreRegion.ASHENVEIL_DESERT,
        steps = listOf(
            MissionStep(
                id = "pm_mage_2_step_1",
                stepNumber = 1,
                name = "Desert Scholars",
                description = "Consult with the Sandstrider Clans about the buried city",
                locationRequirement = "ashenveil_sandstone_bazaar",
                loreText = "'The sands shift and reveal, shift and hide,' an elder explains. 'Sunscorch was a city of mages, destroyed in the Sundering. Its vaults hold power—and curses.'"
            ),
            MissionStep(
                id = "pm_mage_2_step_2",
                stepNumber = 2,
                name = "The Revealing Sands",
                description = "Find Sunscorch Ruins when the desert winds uncover them",
                locationRequirement = "ashenveil_sunscorch_ruins",
                loreText = "Pillars of black stone emerge from the sand. Magical wards still flicker on doorways after centuries. This city was built to last forever."
            ),
            MissionStep(
                id = "pm_mage_2_step_3",
                stepNumber = 3,
                name = "The Vault Seals",
                description = "Decipher the magical seals protecting the vaults",
                locationRequirement = "ashenveil_sunscorch_ruins",
                loreText = "Inscriptions warn: 'Only the worthy may enter. The unworthy face the Guardian's Flame.' These mages didn't just protect treasure—they protected knowledge."
            ),
            MissionStep(
                id = "pm_mage_2_step_4",
                stepNumber = 4,
                name = "The Trial of Fire",
                description = "Pass through the flame-wreathed corridors",
                locationRequirement = "ashenveil_sunscorch_ruins",
                loreText = "The flames don't burn—they test. They read your magical potential, your intent. Only true mages can pass unharmed."
            )
        ),
        bossBattle = BossBattle(
            bossName = "Ashstorm, the Scorched Elemental",
            bossLevel = 30,
            bossDescription = "Fire elemental born from the Sundering's flames, guardian of Sunscorch",
            location = "ashenveil_sunscorch_ruins",
            loreText = "'I am... the city's last defense,' Ashstorm roars. 'The mages who built this place... bound me here... to guard their secrets. Prove you are... worthy of their legacy!'"
        ),
        winConditions = WinConditions(
            minimumLevel = 27,
            requiredStats = mapOf(StatType.INTELLIGENCE to 28, StatType.LUCK to 15)
        ),
        rewards = PrincipalMissionRewards(
            experience = 4500,
            gold = 1800,
            guaranteedItems = listOf("sunscorch_codex"),
            rareItemChance = 0.55f,
            rareItemPool = listOf("ashstorm_staff", "flame_robes", "desert_mage_crown")
        ),
        loreFragment = "Sunscorch was a city of mages who foresaw the Sundering but couldn't prevent it. They sealed their knowledge in vaults, guarded by elemental magic, hoping future mages would be worthy to claim it."
    )

    private val MAGE_MISSION_3 = PrincipalMission(
        id = "pm_mage_3",
        name = "The Drowned Mysteries",
        description = "Explore the Drowned Cathedral and communicate with the Tide Caller",
        jobClass = JobClass.MAGE,
        loreRegion = LoreRegion.STORMCOAST_REACHES,
        steps = listOf(
            MissionStep(
                id = "pm_mage_3_step_1",
                stepNumber = 1,
                name = "The Lighthouse Keeper",
                description = "Study ancient texts at Lighthouse Point",
                locationRequirement = "stormcoast_lighthouse_point",
                loreText = "'The cathedral predates Havenmoor,' the keeper explains. 'It was built to honor the sea gods. But the Sundering flooded it, and something... remained.'"
            ),
            MissionStep(
                id = "pm_mage_3_step_2",
                stepNumber = 2,
                name = "Low Tide Exploration",
                description = "Enter the Drowned Cathedral at low tide",
                locationRequirement = "stormcoast_drowned_cathedral",
                loreText = "Water drips from ancient mosaics. Altars stand submerged. This was a place of great power, dedicated to entities that governed the seas."
            ),
            MissionStep(
                id = "pm_mage_3_step_3",
                stepNumber = 3,
                name = "The Calling Song",
                description = "Hear the strange singing from the deep chambers",
                locationRequirement = "stormcoast_drowned_cathedral",
                loreText = "A voice, neither male nor female, sings in a language that predates human civilization. It speaks of guardianship, duty, and eternal vigil."
            ),
            MissionStep(
                id = "pm_mage_3_step_4",
                stepNumber = 4,
                name = "The Deep Chamber",
                description = "Descend to the cathedral's flooded heart",
                locationRequirement = "stormcoast_drowned_cathedral",
                loreText = "The chamber floods completely at high tide. Whatever dwells here controls the very waters that surround Aethermoor."
            )
        ),
        bossBattle = BossBattle(
            bossName = "The Tide Caller",
            bossLevel = 40,
            bossDescription = "Ancient entity that dwells in the Drowned Cathedral, controller of tides and seas",
            location = "stormcoast_drowned_cathedral",
            loreText = "'Mage,' the Tide Caller's voice echoes through the water. 'You seek knowledge of the island's binding? The Founding Age established three guardians: Tempest in the waves, the Warden beneath Havenmoor, and I in the depths. We were placed here to protect the world FROM Aethermoor, not the reverse. This island harbors something that must never escape.'"
        ),
        winConditions = WinConditions(
            minimumLevel = 36,
            requiredStats = mapOf(StatType.INTELLIGENCE to 32, StatType.CHARISMA to 18)
        ),
        rewards = PrincipalMissionRewards(
            experience = 6500,
            gold = 2800,
            guaranteedItems = listOf("tide_caller_pearl"),
            rareItemChance = 0.65f,
            rareItemPool = listOf("tidal_staff", "drowned_cathedral_robes", "sea_god_crown")
        ),
        loreFragment = "The Tide Caller reveals the truth: Aethermoor is not a prison for those who arrive, but a prison for something ON the island. The guardians don't keep people in—they keep something else from getting out."
    )

    // ========================================
    // Get All Missions
    // ========================================

    /**
     * Get all principal missions for all job classes
     */
    fun getAllMissions(): List<PrincipalMission> {
        return listOf(
            // Warriors
            WARRIOR_MISSION_1,
            WARRIOR_MISSION_2,
            WARRIOR_MISSION_3,

            // Assassins
            ASSASSIN_MISSION_1,
            ASSASSIN_MISSION_2,
            ASSASSIN_MISSION_3,

            // Mages
            MAGE_MISSION_1,
            MAGE_MISSION_2,
            MAGE_MISSION_3,

            // TODO: Add remaining missions for other classes
            // This file is getting long - will continue in next part
        )
    }

    /**
     * Get missions for a specific job class
     */
    fun getMissionsForJobClass(jobClass: JobClass): List<PrincipalMission> {
        return getAllMissions().filter { it.jobClass == jobClass }
    }

    /**
     * Get a random mission for a job class (for assignment at character creation)
     */
    fun getRandomMissionForJobClass(jobClass: JobClass): PrincipalMission? {
        return getMissionsForJobClass(jobClass).randomOrNull()
    }

    /**
     * Get mission by ID
     */
    fun getMissionById(id: String): PrincipalMission? {
        return getAllMissions().firstOrNull { it.id == id }
    }
}
