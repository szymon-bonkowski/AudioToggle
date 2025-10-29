package io.github.szymonbonkowski.audiotoggle

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBattery = findViewById<Button>(R.id.btn_battery)
        val btnPause = findViewById<Button>(R.id.btn_pause)
        val btnGithub = findViewById<Button>(R.id.btn_github)

        btnBattery.setOnClickListener { requestIgnoreBatteryOptimizations() }
        btnPause.setOnClickListener { openAppInfoForBatteryAndPause() }
        btnGithub.setOnClickListener { openGitHub() }
    }

    private fun requestIgnoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                try {
                    startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                } catch (ex: Exception) {
                    Toast.makeText(this,
                        "Can't open battery optimization settings. Open Settings → Battery → Battery optimization manually.",
                        Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Option not available on Android < 6.0", Toast.LENGTH_LONG).show()
        }
    }

    private fun openAppInfoForBatteryAndPause() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this,
                "Can't open app info. Open Settings → Apps → your app manually.",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun openGitHub() {
        val url = "https://github.com/szymon-bonkowski/AudioToggle"
        try {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(i)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No browser found, can't open link.", Toast.LENGTH_SHORT).show()
        }
    }
}