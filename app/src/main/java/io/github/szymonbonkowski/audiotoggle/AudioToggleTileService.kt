package io.github.szymonbonkowski.audiotoggle

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class AudioToggleTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        toggleSpeakerHold()
        updateTileState()
    }

    private fun toggleSpeakerHold() {
        val shouldStop = AudioHoldService.isRunning
        val svcIntent = Intent(this, AudioHoldService::class.java)

        try {
            if (shouldStop) {
                stopService(svcIntent)
            } else {
                if (Build.VERSION.SDK_INT >= 26) {
                    try {
                        startForegroundService(svcIntent)
                    } catch (e: IllegalStateException) {
                        startService(svcIntent)
                    }
                } else {
                    startService(svcIntent)
                }
            }
        } catch (t: Throwable) {
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return

        if (AudioHoldService.isRunning) {
            tile.state = Tile.STATE_ACTIVE
            tile.label = "Głośnik"
            tile.contentDescription = "Głośnik włączony"
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = "Słuchawki"
            tile.contentDescription = "Głośnik wyłączony"
        }

        tile.updateTile()
    }
}
