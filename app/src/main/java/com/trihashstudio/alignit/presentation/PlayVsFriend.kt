package com.trihashstudio.alignit.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.trihashstudio.alignit.R

@Composable
fun GlowingButton(
    borderColor: Color, icon: ImageVector, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(Color.Transparent, CircleShape)
            .clickable { onClick() }
            .shadow(12.dp, CircleShape)
            .border(3.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = borderColor,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun PlayVsFriend(navController: NavHostController) {
    var gridState by remember { mutableStateOf(List(9) { "" }) }
    var isPlayerOneTurn by remember { mutableStateOf(true) }
    var selectedDotIndex by remember { mutableStateOf<Int?>(null) }
    var winner by remember { mutableStateOf<String?>(null) }

    val player1Count = gridState.count { it == "P1" }
    val player2Count = gridState.count { it == "P2" }
    val isMovePhase = player1Count == 3 && player2Count == 3
    val currentPlayerStr = if (isPlayerOneTurn) "P1" else "P2"

    fun checkWinner(): String? {
        val winPatterns = listOf(
            listOf(0, 1, 2), // top row
            listOf(3, 4, 5), // middle row
            listOf(6, 7, 8), // bottom row
            listOf(0, 3, 6), // left column
            listOf(1, 4, 7), // middle column
            listOf(2, 5, 8), // right column
            listOf(0, 4, 8), // diagonal
            listOf(2, 4, 6)  // diagonal
        )
        for (pattern in winPatterns) {
            val (a, b, c) = pattern
            if (gridState[a].isNotEmpty() && gridState[a] == gridState[b] && gridState[a] == gridState[c]) {
                return gridState[a]
            }
        }
        return null
    }

    fun resetGame() {
        gridState = List(9) { "" }
        isPlayerOneTurn = true
        selectedDotIndex = null
        winner = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.background_2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Play Vs Friend",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    text = if (!isMovePhase) "Each Player Has 3 Dots - Align It To Win!"
                    else "Move Your Dot To Align 3 In A Row!",
                    style = TextStyle(
                        fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(top = 15.dp, bottom = 16.dp)
                )
            }

            // Show Turn or Winner
            if (winner == null) {
                Text(
                    text = if (isPlayerOneTurn) "Player 1’s Turn (Blue)" else "Player 2’s Turn (Pink)",
                    color = if (isPlayerOneTurn) Color(0xFF00BFFF) else Color(0xFFFF69B4),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = if (winner == "P1") "💙 Blue Wins!" else "💖 Pink Wins!",
                    color = if (winner == "P1") Color(0xFF00BFFF) else Color(0xFFFF69B4),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            GlowingReserveDots(
                remainingDots = 3 - player1Count,
                dotImage = R.drawable.blue_dot,
                glowColor = Color(0xFF00BFFF),
                isActive = isPlayerOneTurn && winner == null
            )
            // Grid
            GridBoard(
                gridState = gridState,
                onCellClick = { index ->
                    if (winner != null) return@GridBoard // stop if game ended

                    if (!isMovePhase) {
                        // placement phase
                        if (gridState[index].isEmpty()) {
                            if (isPlayerOneTurn && player1Count < 3) {
                                gridState = gridState.toMutableList().apply { this[index] = "P1" }
                                winner = checkWinner()
                                isPlayerOneTurn = !isPlayerOneTurn
                            } else if (!isPlayerOneTurn && player2Count < 3) {
                                gridState = gridState.toMutableList().apply { this[index] = "P2" }
                                winner = checkWinner()
                                isPlayerOneTurn = !isPlayerOneTurn
                            }
                        }
                    } else {
                        // move phase
                        val currentPlayer = currentPlayerStr
                        if (selectedDotIndex == null) {
                            if (gridState[index] == currentPlayer) selectedDotIndex = index
                        } else {
                            if (gridState[index] == currentPlayer) {
                                selectedDotIndex = index
                                return@GridBoard
                            }
                            if (gridState[index].isEmpty() && isAdjacent(
                                    selectedDotIndex!!, index
                                )
                            ) {
                                val temp = gridState.toMutableList()
                                temp[selectedDotIndex!!] = ""
                                temp[index] = currentPlayer
                                gridState = temp
                                winner = checkWinner()
                                isPlayerOneTurn = !isPlayerOneTurn
                                selectedDotIndex = null
                            } else {
                                selectedDotIndex = null
                            }
                        }
                    }
                },
                selectedDotIndex = selectedDotIndex,
                currentPlayer = currentPlayerStr,
                isMovePhase = isMovePhase
            )
            GlowingReserveDots(
                remainingDots = 3 - player2Count,
                dotImage = R.drawable.pink_dot,
                glowColor = Color(0xFFFF69B4),
                isActive = !isPlayerOneTurn && winner == null
            )

            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
            ) {
                GlowingButton(
                    borderColor = Color(0xFF8A2BE2), icon = Icons.Default.Refresh
                ) { resetGame() }
                GlowingButton(
                    borderColor = Color(0xFF8A2BE2),
                    icon = Icons.Default.Home
                ) { navController.navigate("home") }
//                GlowingButton(borderColor = Color(0xFF8A2BE2), icon = Icons.Default.Settings) {}
            }
        }
    }
}


fun isAdjacent(from: Int, to: Int): Boolean {
    val adj = arrayOf(
        listOf(1, 3, 4),           // 0 (1): -> 2,4,5
        listOf(0, 2, 4),           // 1 (2): -> 1,3,5
        listOf(1, 5, 4),           // 2 (3): -> 2,6,5
        listOf(0, 4, 6),           // 3 (4): -> 1,5,7
        listOf(0, 1, 2, 3, 5, 6, 7, 8), // 4 (5): center -> all others
        listOf(2, 4, 8),           // 5 (6): -> 3,5,9
        listOf(3, 4, 7),           // 6 (7): -> 4,5,8
        listOf(6, 8, 4),           // 7 (8): -> 7,9,5
        listOf(7, 5, 4)            // 8 (9): -> 8,6,5
    )
    return adj.getOrNull(from)?.contains(to) == true
}

@Composable
fun GridBoard(
    gridState: List<String>,
    onCellClick: (Int) -> Unit,
    selectedDotIndex: Int?,
    currentPlayer: String,
    isMovePhase: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        for (row in 0..2) {
            Row {
                for (col in 0..2) {
                    val index = row * 3 + col

                    // Determine highlight states
                    val isSelected = selectedDotIndex == index
                    val isValidTarget =
                        selectedDotIndex != null && gridState[selectedDotIndex] == currentPlayer && gridState[index].isEmpty() && isAdjacent(
                            selectedDotIndex, index
                        )

                    val borderColor = when {
                        isSelected -> Color.Yellow
                        isValidTarget -> Color(0xFF00E5FF) // bright cyan highlight for valid target
                        else -> Color.Gray.copy(alpha = 0.4f)
                    }

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(6.dp)
                            .clickable { onCellClick(index) }, contentAlignment = Alignment.Center
                    ) {
                        when (gridState[index]) {
                            "P1" -> Image(
                                painter = painterResource(R.drawable.blue_dot),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp)
                            )

                            "P2" -> Image(
                                painter = painterResource(R.drawable.pink_dot),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp)
                            )

                            else -> Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .border(2.dp, borderColor, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlowingReserveDots(
    remainingDots: Int,
    dotImage: Int,
    glowColor: Color,
    isActive: Boolean
) {
    // Animate glow only for active player's reserve
    val glowAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.9f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(6.dp)
    ) {
        repeat(remainingDots) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .padding(4.dp)
                    .shadow(
                        if (isActive) 20.dp else 0.dp,
                        shape = CircleShape,
                        ambientColor = glowColor.copy(alpha = glowAlpha),
                        spotColor = glowColor.copy(alpha = glowAlpha)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(dotImage),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

