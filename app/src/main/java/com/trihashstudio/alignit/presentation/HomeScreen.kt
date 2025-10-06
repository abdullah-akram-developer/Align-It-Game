package com.trihashstudio.alignit.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.trihashstudio.alignit.R

@Composable
fun HomeScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(R.drawable.background_1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Foreground content (on top of background)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 0.dp, horizontal = 22.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ALIGN IT!", style = TextStyle(
                        fontWeight = FontWeight.Bold, fontSize = 60.sp,   // ✅ 600.sp is way too big
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Can You Connect Three?", style = TextStyle(
                        fontSize = 20.sp, color = Color.White
                    )
                )
                Image(
                    painter = painterResource(R.drawable.align_game),
                    contentDescription = null,
                    modifier = Modifier.size(250.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameMenuButton(
                    text = "Play vs Friend",
                    iconRes = R.drawable.support,
                    onClick = { navController.navigate("friend") })
                GameMenuButton(
                    text = "Play vs Ai", iconRes = R.drawable.ai, onClick = { navController.navigate("ai") })
                GameMenuButton(
                    text = "Online Play",
                    iconRes = R.drawable.globe,
                    onClick = { /* handle click */ })
            }


        }
    }
}

@Composable
fun GameMenuButton(
    text: String, @DrawableRes iconRes: Int, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF31136C), // Neon purple
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(2.dp, Color(0xFF462095)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 12.dp)
            .height(60.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 8.dp, pressedElevation = 12.dp
        )
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size(30.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(
            text = text, style = TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
        )
    }
}

