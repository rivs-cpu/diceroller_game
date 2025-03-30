package com.example.dicerollergame

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onNavigateBack: () -> Unit
) {
    var humanDice by remember { mutableStateOf(List(5) { 1 }) }
    var computerDice by remember { mutableStateOf(List(5) { 1 }) }
    var animationTrigger by remember { mutableStateOf(0) }
    var gameResult by remember { mutableStateOf<GameResult?>(null) }
    var totalHumanWins by remember { mutableStateOf(0) }
    var totalComputerWins by remember { mutableStateOf(0) }
    var roundCount by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dice Game") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("You", fontWeight = FontWeight.Bold)
                    Text("$totalHumanWins", fontSize = 24.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Round", fontWeight = FontWeight.Bold)
                    Text("$roundCount", fontSize = 24.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Computer", fontWeight = FontWeight.Bold)
                    Text("$totalComputerWins", fontSize = 24.sp)
                }
            }

            // Game result announcement
            gameResult?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (it) {
                            GameResult.WIN -> Color(0xFFDCEDC8)  // Light green
                            GameResult.LOSE -> Color(0xFFFFCDD2)  // Light red
                            GameResult.TIE -> Color(0xFFE1F5FE)   // Light blue
                        }
                    )
                ) {
                    Text(
                        text = when (it) {
                            GameResult.WIN -> "You Win!"
                            GameResult.LOSE -> "Computer Wins!"
                            GameResult.TIE -> "It's a Tie!"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Computer's dice
            Text(
                "Computer's Dice",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // Display computer dice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // First row: 3 dice
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 0..2) {
                        AnimatedDiceImage(
                            diceValue = computerDice[i],
                            modifier = Modifier.size(64.dp),
                            colorFilter = ColorFilter.tint(
                                color = Color.Green.copy(alpha = 0.3f),
                                blendMode = BlendMode.SrcAtop
                            ),
                            key = "$i-$animationTrigger-computer"
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // Second row: 2 dice
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 3..4) {
                        AnimatedDiceImage(
                            diceValue = computerDice[i],
                            modifier = Modifier.size(64.dp),
                            colorFilter = ColorFilter.tint(
                                color = Color.Green.copy(alpha = 0.3f),
                                blendMode = BlendMode.SrcAtop
                            ),
                            key = "$i-$animationTrigger-computer"
                        )
                    }
                }
            }

            // Computer score
            val computerScore = computerDice.sum()
            Text(
                text = "Score: $computerScore",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            // Human's dice
            Text(
                "Your Dice",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Display human dice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // First row: 3 dice
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 0..2) {
                        AnimatedDiceImage(
                            diceValue = humanDice[i],
                            modifier = Modifier.size(64.dp),
                            key = "$i-$animationTrigger-human"
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // Second row: 2 dice
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 3..4) {
                        AnimatedDiceImage(
                            diceValue = humanDice[i],
                            modifier = Modifier.size(64.dp),
                            key = "$i-$animationTrigger-human"
                        )
                    }
                }
            }

            // Human score
            val humanScore = humanDice.sum()
            Text(
                text = "Score: $humanScore",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Roll button
            Button(
                onClick = {
                    // Roll dice
                    humanDice = List(5) { (1..6).random() }
                    computerDice = List(5) { (1..6).random() }

                    // Trigger animation
                    animationTrigger++

                    // Increment round count
                    roundCount++

                    // Determine winner
                    val humanTotal = humanDice.sum()
                    val computerTotal = computerDice.sum()

                    gameResult = when {
                        humanTotal > computerTotal -> {
                            totalHumanWins++
                            GameResult.WIN
                        }
                        humanTotal < computerTotal -> {
                            totalComputerWins++
                            GameResult.LOSE
                        }
                        else -> GameResult.TIE
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Roll Dice", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun AnimatedDiceImage(
    diceValue: Int,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    key: Any
) {
    // Animation states
    var isRolling by remember { mutableStateOf(false) }

    // Animated rotation value
    val rotation by animateFloatAsState(
        targetValue = if (isRolling) 360f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "rotation"
    )

    // Animated scale value
    val scale by animateFloatAsState(
        targetValue = if (isRolling) 1.2f else 1.0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    // Animated vertical offset for jump effect
    val yOffset by animateFloatAsState(
        targetValue = if (isRolling) -20f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "yOffset"
    )

    // Trigger animation when key changes
    LaunchedEffect(key) {
        isRolling = true
        kotlinx.coroutines.delay(500)
        isRolling = false
    }

    val imageResource = when (diceValue) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    Image(
        painter = painterResource(imageResource),
        contentDescription = diceValue.toString(),
        modifier = modifier
            .graphicsLayer {
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
                translationY = yOffset
            },
        colorFilter = colorFilter
    )
}

enum class GameResult {
    WIN, LOSE, TIE
}