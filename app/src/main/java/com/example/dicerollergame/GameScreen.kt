package com.example.dicerollergame

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onNavigateBack: () -> Unit
) {
    // Game state
    var humanDice by rememberSaveable { mutableStateOf(List(5) { 1 }) }
    var computerDice by rememberSaveable { mutableStateOf(List(5) { 1 }) }
    var animationTrigger by rememberSaveable { mutableStateOf(0) }
    var gameResult by rememberSaveable { mutableStateOf<GameResult?>(null) }
    var totalHumanWins by rememberSaveable { mutableStateOf(0) }
    var totalComputerWins by rememberSaveable { mutableStateOf(0) }
    var roundCount by rememberSaveable { mutableStateOf(0) }

    // Target score (default 101)
    var targetScore by rememberSaveable { mutableStateOf(101) }

    // Cumulative scores
    var humanCumulativeScore by rememberSaveable { mutableStateOf(0) }
    var computerCumulativeScore by rememberSaveable { mutableStateOf(0) }

    // Track roll attempts within a single turn
    var currentRollCount by rememberSaveable { mutableStateOf(0) }
    val maxRollsPerTurn = 3

    // Track which dice the player wants to keep for rerolls
    var keptDiceIndices by rememberSaveable { mutableStateOf(setOf<Int>()) }

    // Track if a tiebreaker is in progress
    var isTiebreaker by rememberSaveable { mutableStateOf(false) }

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
            // Game stats display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Win stats
                Text(
                    text = "H:$totalHumanWins/C:$totalComputerWins",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Target score
                if (gameResult == null) {
                    Text(
                        text = "Target: $targetScore",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Current scores
                if (roundCount > 0) {
                    Text(
                        text = "Scores: ${humanCumulativeScore}-${computerCumulativeScore}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Setup screen for new game
            if (roundCount == 0 && gameResult == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Set target score:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = { if (targetScore > 10) targetScore -= 10 }) {
                                Text("-10")
                            }

                            Text(
                                text = "$targetScore",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Button(onClick = { targetScore += 10 }) {
                                Text("+10")
                            }
                        }

                        Button(
                            onClick = { roundCount = 1 },  // Start the game
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                        ) {
                            Text("Start Game", fontSize = 18.sp)
                        }
                    }
                }
            } else {
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
                                GameResult.TIE -> {
                                    if (isTiebreaker) "Keep rolling to break the tie!"
                                    else "It's a Tie!"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (roundCount > 0) {
                    // Computer's section
                    Text(
                        "Computer's Dice" + if (isTiebreaker) " (Tiebreaker)" else "",
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

                    // Computer score display
                    val computerCurrentScore = computerDice.sum()
                    Text(
                        text = if (isTiebreaker)
                            "Score: $computerCurrentScore"
                        else
                            "Roll: $computerCurrentScore | Total: $computerCumulativeScore",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )

                    // Human's section
                    Text(
                        "Your Dice" + if (isTiebreaker) " (Tiebreaker)" else "",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (!isTiebreaker && currentRollCount > 0 && currentRollCount < maxRollsPerTurn) {
                        Text(
                            "Tap dice to keep for reroll",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

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
                                    modifier = Modifier
                                        .size(64.dp)
                                        .let {
                                            if (!isTiebreaker && currentRollCount > 0 && currentRollCount < maxRollsPerTurn) {
                                                it.clickable {
                                                    keptDiceIndices = if (keptDiceIndices.contains(i)) {
                                                        keptDiceIndices - i
                                                    } else {
                                                        keptDiceIndices + i
                                                    }
                                                }
                                            } else {
                                                it
                                            }
                                        }
                                        .let {
                                            if (keptDiceIndices.contains(i)) {
                                                it.border(2.dp, MaterialTheme.colorScheme.primary)
                                            } else {
                                                it
                                            }
                                        },
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
                                    modifier = Modifier
                                        .size(64.dp)
                                        .let {
                                            if (!isTiebreaker && currentRollCount > 0 && currentRollCount < maxRollsPerTurn) {
                                                it.clickable {
                                                    keptDiceIndices = if (keptDiceIndices.contains(i)) {
                                                        keptDiceIndices - i
                                                    } else {
                                                        keptDiceIndices + i
                                                    }
                                                }
                                            } else {
                                                it
                                            }
                                        }
                                        .let {
                                            if (keptDiceIndices.contains(i)) {
                                                it.border(2.dp, MaterialTheme.colorScheme.primary)
                                            } else {
                                                it
                                            }
                                        },
                                    key = "$i-$animationTrigger-human"
                                )
                            }
                        }
                    }

                    // Human score display
                    val humanCurrentScore = humanDice.sum()
                    Text(
                        text = if (isTiebreaker)
                            "Score: $humanCurrentScore"
                        else
                            "Roll: $humanCurrentScore | Total: $humanCumulativeScore",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    // Roll count display
                    if (!isTiebreaker && currentRollCount > 0) {
                        Text(
                            text = "Roll ${currentRollCount}/${maxRollsPerTurn}",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Game control buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Roll button
                    Button(
                        onClick = {
                            if (gameResult != null && !isTiebreaker) {
                                // Reset game
                                humanCumulativeScore = 0
                                computerCumulativeScore = 0
                                currentRollCount = 0
                                keptDiceIndices = setOf()
                                gameResult = null
                                roundCount = 0  // Back to config screen
                                isTiebreaker = false
                            } else if (isTiebreaker) {
                                // Handle tiebreaker roll
                                humanDice = List(5) { (1..6).random() }
                                computerDice = List(5) { (1..6).random() }
                                animationTrigger++

                                // Check tiebreaker result
                                val humanScore = humanDice.sum()
                                val computerScore = computerDice.sum()

                                if (humanScore != computerScore) {
                                    // Tie is broken
                                    gameResult = if (humanScore > computerScore) {
                                        totalHumanWins++
                                        GameResult.WIN
                                    } else {
                                        totalComputerWins++
                                        GameResult.LOSE
                                    }
                                    isTiebreaker = false
                                }
                            } else if (currentRollCount >= maxRollsPerTurn) {
                                // End of turn, score the current roll
                                val humanScore = humanDice.sum()
                                val computerScore = computerDice.sum()

                                humanCumulativeScore += humanScore
                                computerCumulativeScore += computerScore

                                // Check if game is over
                                if (humanCumulativeScore >= targetScore || computerCumulativeScore >= targetScore) {
                                    if (humanCumulativeScore >= targetScore && computerCumulativeScore >= targetScore) {
                                        if (humanCumulativeScore > computerCumulativeScore) {
                                            totalHumanWins++
                                            gameResult = GameResult.WIN
                                        } else if (humanCumulativeScore < computerCumulativeScore) {
                                            totalComputerWins++
                                            gameResult = GameResult.LOSE
                                        } else {
                                            // Exact tie - start tiebreaker
                                            gameResult = GameResult.TIE
                                            isTiebreaker = true
                                        }
                                    } else if (humanCumulativeScore >= targetScore) {
                                        totalHumanWins++
                                        gameResult = GameResult.WIN
                                    } else {
                                        totalComputerWins++
                                        gameResult = GameResult.LOSE
                                    }
                                }

                                // Reset for next turn
                                currentRollCount = 0
                                keptDiceIndices = setOf()
                            } else {
                                // Roll or reroll the dice
                                if (currentRollCount == 0) {
                                    // First roll of a turn - roll all dice
                                    humanDice = List(5) { (1..6).random() }
                                    computerDice = List(5) { (1..6).random() }
                                } else {
                                    // Reroll - keep selected dice for human
                                    humanDice = humanDice.mapIndexed { index, value ->
                                        if (index in keptDiceIndices) value else (1..6).random()
                                    }

                                    // Computer AI strategy - Random for now
                                    // Randomly choose which dice to keep
                                    val computerKeptIndices = List(5) { it }
                                        .filter { Random.nextBoolean() }
                                        .toSet()

                                    computerDice = computerDice.mapIndexed { index, value ->
                                        if (index in computerKeptIndices) value else (1..6).random()
                                    }
                                }

                                // Increment roll count
                                currentRollCount++

                                // Trigger animation
                                animationTrigger++
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text(
                            when {
                                gameResult != null && !isTiebreaker -> "New Game"
                                isTiebreaker -> "Roll Tiebreaker"
                                currentRollCount >= maxRollsPerTurn -> "Score & Next Turn"
                                currentRollCount == 0 -> "Roll Dice"
                                else -> "Reroll"
                            },
                            fontSize = 16.sp
                        )
                    }

                    // Score button - only shown during a turn when rerolls are available
                    if (!isTiebreaker && gameResult == null && currentRollCount > 0 && currentRollCount < maxRollsPerTurn) {
                        Button(
                            onClick = {
                                // Score current roll
                                humanCumulativeScore += humanDice.sum()

                                // Computer completes its strategy
                                // Randomly decide whether to reroll again (if possible)
                                if (currentRollCount < maxRollsPerTurn - 1 && Random.nextBoolean()) {
                                    // Computer rerolls once more
                                    val computerKeptIndices = List(5) { it }
                                        .filter { Random.nextBoolean() }
                                        .toSet()

                                    computerDice = computerDice.mapIndexed { index, value ->
                                        if (index in computerKeptIndices) value else (1..6).random()
                                    }
                                }

                                // Score computer's roll
                                computerCumulativeScore += computerDice.sum()

                                // Check if game is over
                                if (humanCumulativeScore >= targetScore || computerCumulativeScore >= targetScore) {
                                    if (humanCumulativeScore >= targetScore && computerCumulativeScore >= targetScore) {
                                        if (humanCumulativeScore > computerCumulativeScore) {
                                            totalHumanWins++
                                            gameResult = GameResult.WIN
                                        } else if (humanCumulativeScore < computerCumulativeScore) {
                                            totalComputerWins++
                                            gameResult = GameResult.LOSE
                                        } else {
                                            // Exact tie - start tiebreaker
                                            gameResult = GameResult.TIE
                                            isTiebreaker = true
                                        }
                                    } else if (humanCumulativeScore >= targetScore) {
                                        totalHumanWins++
                                        gameResult = GameResult.WIN
                                    } else {
                                        totalComputerWins++
                                        gameResult = GameResult.LOSE
                                    }
                                }

                                // Reset for next turn
                                currentRollCount = 0
                                keptDiceIndices = setOf()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        ) {
                            Text("Score", fontSize = 16.sp)
                        }
                    }
                }
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