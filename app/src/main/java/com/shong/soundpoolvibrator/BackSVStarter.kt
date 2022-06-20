package com.shong.soundpoolvibrator

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.util.Log

// 진동은 포그라운드에서만 동작 가능
// 따라서 백그라운드 -> 포그라운드 핸들링 각 sdk별로 대응해줘야함
class BackSVStarter {
    private val TAG = this::class.java.simpleName + "_sHong"
    var SOUND_VIBE_AVAILABLE = true

    fun backSoundStarter_ForService(context: Context, intent: Intent) {
        if (!SOUND_VIBE_AVAILABLE) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startService(intent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioManager: AudioManager =
                context.getSystemService(Service.AUDIO_SERVICE) as AudioManager
            when (audioManager.ringerMode) {
                AudioManager.RINGER_MODE_VIBRATE -> {
                    Log.d(TAG, "알림음/진동 사용 ok / 휴대폰 모드 : 진동 모드")
                    context.startForegroundService(intent)
                }
                else -> context.startService(intent)
            }

        } else {
            context.startService(intent)
        }
    }
}