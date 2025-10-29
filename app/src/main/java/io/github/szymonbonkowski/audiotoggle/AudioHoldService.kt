package io.github.szymonbonkowski.audiotoggle

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AudioHoldService : Service() {

    companion object {
        @Volatile
        var isRunning = false
    }

    private var audioTrack: AudioTrack? = null
    private var audioManager: AudioManager? = null
    private var writeThread: Thread? = null
    private var running = false

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notif: Notification = buildNotification()

        if (Build.VERSION.SDK_INT >= 34) {
            @Suppress("DEPRECATION")
            startForeground(NotificationId.NOTIF_ID, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NotificationId.NOTIF_ID, notif)
        }

        startHoldingAudio()
        return START_STICKY
    }

    override fun onDestroy() {
        stopHoldingAudio()
        stopForeground(true)
        isRunning = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            val chan = NotificationChannel(
                NotificationId.CHANNEL_ID,
                "Audio Hold",
                NotificationManager.IMPORTANCE_LOW
            )
            chan.setSound(null, null)
            nm.createNotificationChannel(chan)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, NotificationId.CHANNEL_ID)
            .setContentTitle("Audio Output: głośnik")
            .setContentText("Kafelek aktywny — utrzymywanie routingu audio")
            .setSmallIcon(R.drawable.ic_headphones)
            .setOngoing(true)
            .build()
    }

    private fun startHoldingAudio() {
        if (running) return
        running = true
        isRunning = true

        audioManager?.let { am ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val attr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                val afr = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(attr)
                    .setOnAudioFocusChangeListener { /* no-op */ }
                    .build()
                try {
                    am.requestAudioFocus(afr)
                } catch (_: Throwable) { /* ignore */ }
            } else {
                @Suppress("DEPRECATION")
                try {
                    am.requestAudioFocus(
                        { },
                        AudioManager.STREAM_VOICE_CALL,
                        AudioManager.AUDIOFOCUS_GAIN
                    )
                } catch (_: Throwable) { }
            }

            am.mode = AudioManager.MODE_IN_COMMUNICATION
            @Suppress("DEPRECATION")
            am.isSpeakerphoneOn = true
        }

        val sampleRate = 44100
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(2048)
        val buffer = ShortArray(minBuf)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        } catch (t: Throwable) {
            running = false
            isRunning = false
            return
        }

        audioTrack?.play()

        writeThread = Thread {
            try {
                while (running) {
                    val local = audioTrack
                    if (local == null || local.playState != AudioTrack.PLAYSTATE_PLAYING) {
                        break
                    }
                    try {
                        local.write(buffer, 0, buffer.size)
                    } catch (_: Throwable) {
                        break
                    }
                }
            } finally {
            }
        }.apply { isDaemon = true; start() }
    }

    private fun stopHoldingAudio() {
        if (!running && audioTrack == null) return

        running = false

        try {
            writeThread?.join(300)
        } catch (_: InterruptedException) { /* ignore */ }
        writeThread = null

        try {
            audioTrack?.let {
                try { it.stop() } catch (_: Throwable) { }
                try { it.release() } catch (_: Throwable) { }
            }
        } catch (_: Throwable) { }
        audioTrack = null

        audioManager?.let { am ->
            try { am.mode = AudioManager.MODE_NORMAL } catch (_: Throwable) { }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                } catch (_: Throwable) { }
            } else {
                @Suppress("DEPRECATION")
                try { am.abandonAudioFocus { } } catch (_: Throwable) { }
            }
        }

        isRunning = false
    }

    private object NotificationId {
        const val CHANNEL_ID = "audio_hold_channel"
        const val NOTIF_ID = 1001
    }
}
