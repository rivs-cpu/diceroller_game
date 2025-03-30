package com.example.dicerollergame

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import kotlin.random.Random

/**
 * Utility functions for the Dice Game application
 */

/**
 * Save game stats to SharedPreferences
 */
fun saveGameStats(context: Context, playerWins: Int, computerWins: Int, totalGames: Int) {
    val sharedPref = context.getSharedPreferences("dice_game_stats", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putInt("player_wins", playerWins)
        putInt("computer_wins", computerWins)
        putInt("total_games", totalGames)
        apply()
    }
}

/**
 * Load game stats from SharedPreferences
 */
fun loadGameStats(context: Context): Triple<Int, Int, Int> {
    val sharedPref = context.getSharedPreferences("dice_game_stats", Context.MODE_PRIVATE)
    val playerWins = sharedPref.getInt("player_wins", 0)
    val computerWins = sharedPref.getInt("computer_wins", 0)
    val totalGames = sharedPref.getInt("total_games", 0)

    return Triple(playerWins, computerWins, totalGames)
}

/**
 * Clear all game stats
 */
fun resetGameStats(context: Context) {
    val sharedPref = context.getSharedPreferences("dice_game_stats", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        clear()
        apply()
    }
}

/**
 * Roll multiple dice and return the results
 */
fun rollDice(count: Int): List<Int> {
    return List(count) { Random.nextInt(1, 7) }
}

/**
 * Calculate total score from dice values
 */
fun calculateScore(dice: List<Int>): Int {
    return dice.sum()
}

/**
 * Composable to persist data when leaving a screen
 */
@Composable
fun PersistGameData(
    playerWins: Int,
    computerWins: Int,
    totalGames: Int
) {
    val context = LocalContext.current

    DisposableEffect(playerWins, computerWins, totalGames) {
        // Save when these values change
        saveGameStats(context, playerWins, computerWins, totalGames)

        onDispose {
            // Save once more when leaving the composable
            saveGameStats(context, playerWins, computerWins, totalGames)
        }
    }
}