package com.watxaut.rolenonplayinggame.presentation.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Character creation screen
 * Allows player to create a new autonomous character
 */
@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Your Character",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Character creation UI will be implemented here:\n" +
                    "- Name input\n" +
                    "- Job class selection\n" +
                    "- Stat allocation (10 points)",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                // TODO: Create character and get ID
                onCharacterCreated("temp-character-id")
            }
        ) {
            Text("Create Character (Placeholder)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateBack
        ) {
            Text("Back")
        }
    }
}
