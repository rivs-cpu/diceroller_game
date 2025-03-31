package com.example.dicerollergame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenu(
    onStartGameClicked: () -> Unit,
    onAboutClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo/Title
        Text(
            text = "Dice Roller",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dice image
        Row(
            modifier = Modifier.padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Two dice images side by side
            Image(
                painter = painterResource(id = R.drawable.dice_6),
                contentDescription = "Dice",
                modifier = Modifier.size(100.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.dice_1),
                contentDescription = "Dice",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Buttons
        Button(
            onClick = onStartGameClicked,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp)
        ) {
            Text("Start Game", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAboutClicked,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp)
        ) {
            Text("About", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}