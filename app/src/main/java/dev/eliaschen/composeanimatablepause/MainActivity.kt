package dev.eliaschen.composeanimatablepause

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.eliaschen.composeanimatablepause.ui.theme.ComposeanimatablepauseTheme
import kotlinx.coroutines.isActive

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
    val deviceWidth = device.screenWidthDp.toFloat()
    val boxWidth = 100f

    val boxMovement = remember { Animatable(0f) }
    var oldBoxMovement by remember { mutableFloatStateOf(0f) }

    var isPause by remember { mutableStateOf(false) }
    var animationKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(isPause) {
        if (isPause) {
            oldBoxMovement = boxMovement.value
            boxMovement.stop()
        } else {
            boxMovement.animateTo(
                targetValue = deviceWidth,
                animationSpec = tween(
                    durationMillis = ((deviceWidth - oldBoxMovement) / (deviceWidth / 3) * 1000).toInt(),
                    easing = LinearEasing
                )
            )
            animationKey++
        }
    }

    LaunchedEffect(animationKey) {
        while (isActive) {
            boxMovement.snapTo(-boxWidth)
            boxMovement.animateTo(
                deviceWidth,
                animationSpec = tween(3000, easing = LinearEasing)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
        Card(
            modifier = Modifier
                .size(boxWidth.dp)
                .offset(boxMovement.value.dp, 0.dp)
        ) { }
        OutlinedButton(
            onClick = { isPause = !isPause },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(WindowInsets.navigationBars.asPaddingValues())
        ) {
            Text(if (isPause) "Resume" else "Pause")
        }
    }
}