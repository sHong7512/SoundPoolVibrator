package com.shong.soundpoolvibrator

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.*
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shong.soundpoolvibrator.ui.theme.SoundPoolVibratorTheme

class MainActivity : ComponentActivity() {
    private val TAG = this::class.java.simpleName + "_sHong"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSoundPool()
        initViberator()

        setContent {
            SoundPoolVibratorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting {
                        val audioManager: AudioManager =
                            getSystemService(AUDIO_SERVICE) as AudioManager
                        when (audioManager.ringerMode) {
                            AudioManager.RINGER_MODE_NORMAL -> soundStart()
                            AudioManager.RINGER_MODE_VIBRATE -> vibeStart()
                            AudioManager.RINGER_MODE_SILENT -> null
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        soundDestroy()
        super.onDestroy()
    }

    lateinit var soundPool: SoundPool
    var soundId: Int = 0
    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        soundPool = SoundPool.Builder().apply {
            setMaxStreams(6)
            setAudioAttributes(audioAttributes)
        }.build()
    }

    private fun soundStart() {
        try {
            soundId = soundPool.load(this, R.raw.sound, 1)
            soundPool.setOnLoadCompleteListener { soundPool, i, i2 ->
                soundPool.play(soundId, 1F, 1F, 1, 0, 1f)
            }
        } catch (e: Exception) {
            Log.e(TAG, "알림음 재생 에러")
        }
    }

    private fun soundDestroy() {
        soundPool.release()
    }

    lateinit var vibrator: Vibrator
    lateinit var vibratorManager: VibratorManager
    private fun initViberator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            vibratorManager =
                this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        else
            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    private val VIBE_TIME = 500L
    private fun vibeStart() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibrationEffect = VibrationEffect.createOneShot(
                    VIBE_TIME,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
                val combinedVibration = CombinedVibration.createParallel(vibrationEffect)
                vibratorManager.vibrate(combinedVibration)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect =
                    VibrationEffect.createOneShot(VIBE_TIME, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect)
            } else {
                vibrator.vibrate(VIBE_TIME)
            }
        } catch (e: Exception) {
            Log.e(TAG, "진동 재생 에러")
        }
    }
}

@Composable
fun Greeting(click: () -> Unit) {
    var cnt by remember { mutableStateOf(0) }

    val operation = {
        cnt++
        click()
    }

    Surface(
        color = MaterialTheme.colors.secondary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(10.dp),
                text = "SoundPoolVibrator Example",
            )
            Text(
                modifier = Modifier.padding(10.dp),
                text = "클릭 횟수 : $cnt"
            )

            Button(onClick = operation) {
                Text(text = "Click")
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SoundPoolVibratorTheme {
        Greeting { }
    }
}