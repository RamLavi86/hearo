package com.hearo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hearo.core.Ear
import com.hearo.core.RelativeLevelMapper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Surface(Modifier.fillMaxSize()) { HomeScreen() } } }
    }
}

@androidx.compose.runtime.Composable
private fun HomeScreen() {
    val context = LocalContext.current
    val detector = remember { HeadphoneDetector(context) }
    val player = remember { TonePlayer() }
    // Placeholder mapping until calibration is implemented; the very quiet level is deliberate.
    val mapper = remember { RelativeLevelMapper(dbFsAtZero = -80.0, maxDbFs = -30.0) }
    var kind by remember { mutableStateOf(detector.current()) }

    DisposableEffect(Unit) {
        val cb = detector.register { kind = detector.current() }
        onDispose { detector.unregister(cb) }
    }

    Column(
        Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
        Text(
            stringResource(
                when (kind) {
                    HeadphoneKind.NONE -> R.string.headphones_none
                    HeadphoneKind.WIRED -> R.string.headphones_wired
                    HeadphoneKind.USB -> R.string.headphones_usb
                    HeadphoneKind.BLUETOOTH -> R.string.headphones_bluetooth
                }
            )
        )
        if (kind == HeadphoneKind.BLUETOOTH) Text(stringResource(R.string.bluetooth_warning))

        val enabled = kind != HeadphoneKind.NONE
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(enabled = enabled, onClick = {
                player.play(Ear.LEFT, 1000, mapper.dbFs(Ear.LEFT, 1000, 40))
            }) { Text(stringResource(R.string.test_tone_left)) }
            Button(enabled = enabled, onClick = {
                player.play(Ear.RIGHT, 1000, mapper.dbFs(Ear.RIGHT, 1000, 40))
            }) { Text(stringResource(R.string.test_tone_right)) }
        }
        Text(stringResource(R.string.screening_disclaimer), style = MaterialTheme.typography.bodySmall)
    }
}
