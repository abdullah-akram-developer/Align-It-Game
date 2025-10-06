package com.trihashstudio.alignit.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.trihashstudio.alignit.R
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(1000)

        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }




    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.align_it_logo),
                contentDescription = null,
                modifier = Modifier.size(350.dp)
            )
        }
    }
}