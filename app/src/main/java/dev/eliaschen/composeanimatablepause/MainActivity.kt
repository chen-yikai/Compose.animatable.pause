package dev.eliaschen.composeanimatablepause

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.eliaschen.composeanimatablepause.ui.theme.ComposeanimatablepauseTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeanimatablepauseTheme {
                BoxAnimation()
            }
        }
    }
}

@Composable
fun BoxAnimation() {
    val device = LocalConfiguration.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val deviceWidth = device.screenWidthDp.toFloat()
    val boxWidth = 100f

    val boxMovement = remember { Animatable(0f) }
    var oldBoxMovement by remember { mutableFloatStateOf(0f) }

    var isPause by remember { mutableStateOf(false) }
    var animationKey by remember { mutableIntStateOf(0) }
    val duration = 4000
    val velocity = (deviceWidth + boxWidth) / duration
    val targetPos = -deviceWidth

    suspend fun speedUp() {
        val speedUpDuration = 2000
        val speedUpFactor = 4
        var boxPos = boxMovement.value
        val speedUpVelocity = velocity * speedUpFactor

        boxMovement.stop()
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsedTime = System.currentTimeMillis() - startTime
            boxPos -= speedUpVelocity * 16
            if (elapsedTime >= speedUpDuration || boxPos <= targetPos) break
            boxMovement.snapTo(boxPos)
            delay(16)
        }
        animationKey++
    }


    LaunchedEffect(isPause) {
        if (isPause) {
            oldBoxMovement = boxMovement.value
            boxMovement.stop()
        } else {
            val remainingDistance = oldBoxMovement - (-boxWidth)
            val timeLeft = (remainingDistance / velocity).toInt()
            boxMovement.animateTo(
                targetValue = targetPos,
                animationSpec = tween(
                    durationMillis = timeLeft,
                    easing = LinearEasing
                )
            )
            animationKey++
        }
    }

    LaunchedEffect(animationKey) {
        while (isActive) {
            boxMovement.snapTo(deviceWidth)
            boxMovement.animateTo(
                -boxWidth,
                animationSpec = tween(duration, easing = LinearEasing)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
        Card(
            modifier = Modifier
                .size(boxWidth.dp)
                .offset(boxMovement.value.dp, 0.dp)
        ) { }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(WindowInsets.navigationBars.asPaddingValues()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { isPause = !isPause },
            ) {
                Text(if (isPause) "Resume" else "Pause")
            }
            OutlinedButton(onClick = {
                scope.launch {
                    speedUp()
                }
            }) { Text("Speed Up") }
        }
    }
}