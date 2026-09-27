package com.example.ui.security

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

/**
 * Enforces Android FLAG_SECURE on the Activity Window to block screenshots and screen recording
 * system-wide during active Video/Voice Calls and Ephemeral Media viewing.
 * Also configures AudioManager to block third-party audio stream capture during calls.
 */
@Composable
fun SecureWindowEffect(
    flagSecureEnabled: Boolean,
    voiceRecordingBlockEnabled: Boolean = false
) {
    val context = LocalContext.current
    DisposableEffect(flagSecureEnabled, voiceRecordingBlockEnabled) {
        val activity = context.findActivity()
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

        if (flagSecureEnabled) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        if (voiceRecordingBlockEnabled) {
            try {
                audioManager?.mode = AudioManager.MODE_IN_COMMUNICATION
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    audioManager?.allowedCapturePolicy = AudioAttributes.ALLOW_CAPTURE_BY_NONE
                }
            } catch (_: SecurityException) {
                // Graceful fallback if system restricts mode change
            }
        }

        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            if (voiceRecordingBlockEnabled) {
                try {
                    audioManager?.mode = AudioManager.MODE_NORMAL
                } catch (_: Exception) {
                }
            }
        }
    }
}
