package com.example.semester

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.semester.ui.theme.SemesterTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SemesterTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val currentPage by viewModel.currentPage
    val timeLeft by viewModel.timeLeft
    val navigate by viewModel.navigate

    val context = LocalContext.current

    // Timer starts once when screen is composed
    LaunchedEffect(Unit) {
        viewModel.startTimer()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (navigate == 1) {
                BottomAppBar(
                    modifier = Modifier.height(80.dp),
                    containerColor = Color(50, 100, 100)
                ) {
                    Button(
                        onClick = { viewModel.currentPage.intValue = 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    ) { Text("Explore") }

                    Button(
                        onClick = { viewModel.currentPage.intValue = 2 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    ) { Text("My Pals") }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background image
            val backgroundImage = if (currentPage == 1) {
                if (navigate == 0) {
                    painterResource(id = R.drawable.background)
                } else {
                    painterResource(id = R.drawable.background3)
                }
            } else {
                painterResource(id = R.drawable.background2)
            }

            Image(
                painter = backgroundImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Timer in top-right corner
            Text(
                text = formatTime(timeLeft),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )

            // Page Content
            when (currentPage) {
                1 -> PageOne(viewModel)
                2 -> PageTwo(viewModel)
            }

            // Encounter logic when timer hits 0
            if (timeLeft < 1 && currentPage == 1) {
                val randomNumber = viewModel.generateRandomEncounter()
                viewModel.navigate.intValue = 0

                val firstDigit = randomNumber / 10
                val monDigit = "mon$firstDigit"
                val imageId = remember(monDigit) {
                    context.resources.getIdentifier(monDigit, "drawable", context.packageName)
                }
                val colorIndex = randomNumber % 10
                val tintColor = viewModel.colorList.getOrElse(colorIndex) { Color.Black }

                var buttonColor = Color.Red
                var buttonPhrase = "Press to Catch"
                var goldIncrease = 0

                if (monDigit == "mon0") {
                    buttonColor = Color.Blue
                    buttonPhrase = "Press to Open"
                    goldIncrease = 50

                    Image(
                        painter = painterResource(id = imageId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center)
                            .offset(y = 150.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = imageId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center)
                            .offset(y = 150.dp)
                    )
                    Image(
                        painter = painterResource(id = imageId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center)
                            .offset(y = 150.dp),
                        colorFilter = ColorFilter.tint(tintColor.copy(alpha = .7f))
                    )
                }

                Text(
                    text = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 300.dp),
                    color = tintColor,
                    fontSize = 40.sp
                )
                Button(
                    onClick = {
                        viewModel.handleCatch(randomNumber)
                        viewModel.increaseGold(goldIncrease)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .scale(1.5f)
                ) {
                    Text(buttonPhrase)
                }

            } else if (currentPage == 1 && timeLeft > 2) {
                Button(
                    onClick = { viewModel.timeLeft.intValue -= 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(171, 200, 55)
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 200.dp)
                        .scale(2.5f)
                ) {
                    Text("Press to Help Search!")
                }

            }
        }
    }
}

@Composable
fun PageOne(viewModel: MainViewModel) {
    val gold by viewModel.gold
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Gold: $gold",
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontSize = 30.sp
        )
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun PageTwo(viewModel: MainViewModel) {
    val context = LocalContext.current
    val collected = viewModel.collected
    val colorList = viewModel.colorList

    // Outer display of small icons, matching original layout
    for (i in 1..9) {
        Row {
            val id1 = "mon$i"
            val imageId = remember(id1) {
                context.resources.getIdentifier(id1, "drawable", context.packageName)
            }
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .offset(x = 0.dp, y = (80 * (i)).dp)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 90.dp, start = 60.dp, end = 15.dp),
            verticalArrangement = Arrangement.spacedBy(56.dp)
        ) {
            var index = 10
            for (i in 1..9) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (j in 0..9) {
                        val id1 = "mon$i"
                        val imageId = remember(id1) {
                            context.resources.getIdentifier(id1, "drawable", context.packageName)
                        }

                        val colorTemp = if (collected.getOrNull(index) == 0)
                            Color.Black
                        else
                            colorList.getOrElse(j) { Color.Gray }

                        Image(
                            painter = painterResource(id = imageId),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                            colorFilter = ColorFilter.tint(colorTemp)
                        )
                        index++
                    }
                }
            }
        }

        Text(
            text = "",
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            fontSize = 16.sp
        )

        Text(
            text = "Collection",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            color = Color.White,
            fontSize = 24.sp
        )
    }
}

// Timer display helper
fun formatTime(seconds: Int): String {
    val minutesPart = seconds / 60
    val secondsPart = seconds % 60
    return "%02d:%02d".format(minutesPart, secondsPart)
}
