package com.trihashstudio.alignit.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import com.trihashstudio.alignit.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

fun aiBestMove(grid: List<String>, isMovePhase: Boolean): Pair<Int?, Int?>? {
    val ai = "P2"
    val player = "P1"

    val emptyCells = grid.mapIndexedNotNull { i, v -> if (v.isEmpty()) i else null }
    if (!isMovePhase && emptyCells.isEmpty()) return null

    var bestScore = Int.MIN_VALUE
    var bestFrom: Int? = null
    var bestTo: Int? = null

    if (!isMovePhase) {
        // 🔹 Placing phase
        for (move in emptyCells) {
            val simulated = grid.toMutableList().apply { this[move] = ai }
            val score = minimaxPerfect(simulated, false, 0, Int.MIN_VALUE, Int.MAX_VALUE, false)
            if (score > bestScore) {
                bestScore = score
                bestTo = move
            }
        }
    } else {
        // 🔹 Moving phase
        val aiDots = grid.mapIndexedNotNull { i, v -> if (v == ai) i else null }
        var foundMove = false
        for (from in aiDots) {
            val moves = (0..8).filter { grid[it].isEmpty() && isAdjacent(from, it) }
            for (to in moves) {
                val simulated = grid.toMutableList().apply {
                    this[from] = ""
                    this[to] = ai
                }
                val score = minimaxPerfect(simulated, false, 0, Int.MIN_VALUE, Int.MAX_VALUE, true)
                if (score > bestScore) {
                    bestScore = score
                    bestFrom = from
                    bestTo = to
                    foundMove = true
                }
            }
        }
        if (!foundMove) return null
    }

    return Pair(bestFrom, bestTo)
}


fun minimaxPerfect(
    grid: List<String>,
    isAITurn: Boolean,
    depth: Int,
    alpha: Int,
    beta: Int,
    isMovePhase: Boolean
): Int {
    val winner = checkWinnerAI(grid)
    if (winner == "P2") return 100 - depth
    if (winner == "P1") return depth - 100
    if (grid.none { it.isEmpty() }) return 0
    if (depth > 7) return 0 // 🔸 Prevent infinite recursion

    var a = alpha
    var b = beta

    return if (isAITurn) {
        var maxEval = Int.MIN_VALUE
        if (!isMovePhase) {
            val emptyCells = grid.mapIndexedNotNull { i, v -> if (v.isEmpty()) i else null }
            for (i in emptyCells) {
                val simulated = grid.toMutableList().apply { this[i] = "P2" }
                val eval = minimaxPerfect(simulated, false, depth + 1, a, b, isMovePhase)
                maxEval = maxOf(maxEval, eval)
                a = maxOf(a, eval)
                if (b <= a) break
            }
        } else {
            val aiDots = grid.mapIndexedNotNull { i, v -> if (v == "P2") i else null }
            for (from in aiDots) {
                val possibleMoves = (0..8).filter { grid[it].isEmpty() && isAdjacent(from, it) }
                for (to in possibleMoves) {
                    val simulated = grid.toMutableList().apply {
                        this[from] = ""
                        this[to] = "P2"
                    }
                    val eval = minimaxPerfect(simulated, false, depth + 1, a, b, isMovePhase)
                    maxEval = maxOf(maxEval, eval)
                    a = maxOf(a, eval)
                    if (b <= a) break
                }
            }
        }
        maxEval
    } else {
        var minEval = Int.MAX_VALUE
        if (!isMovePhase) {
            val emptyCells = grid.mapIndexedNotNull { i, v -> if (v.isEmpty()) i else null }
            for (i in emptyCells) {
                val simulated = grid.toMutableList().apply { this[i] = "P1" }
                val eval = minimaxPerfect(simulated, true, depth + 1, a, b, isMovePhase)
                minEval = minOf(minEval, eval)
                b = minOf(b, eval)
                if (b <= a) break
            }
        } else {
            val playerDots = grid.mapIndexedNotNull { i, v -> if (v == "P1") i else null }
            for (from in playerDots) {
                val possibleMoves = (0..8).filter { grid[it].isEmpty() && isAdjacent(from, it) }
                for (to in possibleMoves) {
                    val simulated = grid.toMutableList().apply {
                        this[from] = ""
                        this[to] = "P1"
                    }
                    val eval = minimaxPerfect(simulated, true, depth + 1, a, b, isMovePhase)
                    minEval = minOf(minEval, eval)
                    b = minOf(b, eval)
                    if (b <= a) break
                }
            }
        }
        minEval
    }
}


fun checkWinnerAI(grid: List<String>): String? {
    val winPatterns = listOf(
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )
    for (pattern in winPatterns) {
        val (a, b, c) = pattern
        if (grid[a].isNotEmpty() && grid[a] == grid[b] && grid[a] == grid[c]) return grid[a]
    }
    return null
}


@Composable
fun PlayVsAI(navController: NavHostController) {
    var gridState by remember { mutableStateOf(List(9) { "" }) }
    var currentPlayer by remember { mutableStateOf("P1") }
    var selectedDotIndex by remember { mutableStateOf<Int?>(null) }
    var isMovePhase by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf<String?>(null) }

    // 🎯 Function to count how many dots each player has
    fun countDots(player: String): Int = gridState.count { it == player }


    // 🧩 AI Turn logic
    LaunchedEffect(currentPlayer, isMovePhase) {
        if (currentPlayer == "P2" && winner == null) {
            delay(600)
            val move = aiBestMove(gridState, isMovePhase)
            if (move != null) {
                val (from, to) = move
                val mutableGrid = gridState.toMutableList()

                if (!isMovePhase && to != null && mutableGrid[to].isEmpty()) {
                    mutableGrid[to] = "P2"
                } else if (isMovePhase && from != null && to != null && from != to) {
                    mutableGrid[from] = ""
                    mutableGrid[to] = "P2"
                }

                gridState = mutableGrid
                winner = checkWinnerAI(gridState)
                if (countDots("P2") == 3) isMovePhase = true
                if (winner == null) currentPlayer = "P1"
            } else {
                currentPlayer = "P1" // fallback if AI can't move
            }
        }
    }

    // 🖼️ Background + Layout
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                Text(
                    text = "Play vs AI",
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Challenge the unbeatable AI opponent 🤖",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(top = 15.dp, bottom = 16.dp)
                )
            }

            if (winner == null) {
                Text(
                    text = if (currentPlayer == "P1") "Your Turn (💙 Blue)"
                    else "AI is thinking...",
                    color = if (currentPlayer == "P1") Color(0xFF00BFFF) else Color(0xFFFF69B4),
                    fontSize = 20.sp
                )
            } else {
                Text(
                    text = if (winner == "P1") "💙 You Win!" else "💖 AI Wins!",
                    color = if (winner == "P1") Color(0xFF00BFFF) else Color(0xFFFF69B4),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                repeat(3 - countDots("P2")) {
                    Image(
                        painter = painterResource(R.drawable.pink_dot),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                    )
                }
            }
            // 🎮 Game Board
            GridBoard(
                gridState = gridState,
                onCellClick = { index ->
                    if (winner != null || currentPlayer != "P1") return@GridBoard

                    val mutableGrid = gridState.toMutableList()

                    // First phase: placing dots
                    if (!isMovePhase) {
                        if (mutableGrid[index].isEmpty()) {
                            mutableGrid[index] = "P1"
                            gridState = mutableGrid
                            if (countDots("P1") == 3 && countDots("P2") == 3) {
                                isMovePhase = true
                            }
                            winner = checkWinnerAI(gridState)
                            if (winner == null) currentPlayer = "P2"
                        }
                    } else {
                        // Move phase
                        if (selectedDotIndex == null) {
                            if (mutableGrid[index] == "P1") selectedDotIndex = index
                        } else if (mutableGrid[index].isEmpty() && isAdjacent(
                                selectedDotIndex!!,
                                index
                            )
                        ) {
                            mutableGrid[index] = "P1"
                            mutableGrid[selectedDotIndex!!] = ""
                            gridState = mutableGrid
                            selectedDotIndex = null
                            winner = checkWinnerAI(gridState)
                            if (winner == null) currentPlayer = "P2"
                        } else if (mutableGrid[index] == "P1") {
                            selectedDotIndex = index
                        }
                    }
                },
                selectedDotIndex = selectedDotIndex,
                currentPlayer = currentPlayer,
                isMovePhase = isMovePhase
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                repeat(3 - countDots("P1")) {
                    Image(
                        painter = painterResource(R.drawable.blue_dot),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                    )
                }
            }
            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
            ) {
                GlowingButton(
                    borderColor = Color(0xFF8A2BE2), icon = Icons.Default.Refresh
                ) {
                    gridState = List(9) { "" }
                    currentPlayer = "P1"
                    winner = null
                    selectedDotIndex = null
                    isMovePhase = false
                }
                GlowingButton(
                    borderColor = Color(0xFF8A2BE2),
                    icon = Icons.Default.Home
                ) { navController.navigate("home") }
//                GlowingButton(borderColor = Color(0xFF8A2BE2), icon = Icons.Default.Settings) {}
            }
        }
    }
}
