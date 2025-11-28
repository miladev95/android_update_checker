package ir.zodiacgroup.updatechecker

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Sample Activity demonstrating how to use the UpdateChecker library
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCheckUpdate = findViewById<Button>(R.id.btnCheckUpdate)

        btnCheckUpdate.setOnClickListener {
            checkForUpdates()
        }
    }

    private fun checkForUpdates() {
        // Example URL - Replace with your actual URL
        // Expected JSON format:
        // {
        //   "versionCode": 2,
        //   "versionName": "1.0.1",
        //   "updateUrl": "https://example.com/app.apk",
        //   "releaseNotes": "Bug fixes and improvements",
        //   "forceUpdate": false
        // }
        val updateUrl = "https://your-server.com/version.json"

        // Option 1: Check and show dialog automatically
        UpdateChecker(this)
            .setOnUpdateAvailable { versionInfo ->
                Toast.makeText(this, "Update available: ${versionInfo.versionName}", Toast.LENGTH_SHORT).show()
            }
            .setOnNoUpdateAvailable {
                Toast.makeText(this, "You have the latest version", Toast.LENGTH_SHORT).show()
            }
            .setOnError { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
            .checkAndShowDialog(updateUrl)

        // Option 2: Check manually and handle yourself
        /*
        UpdateChecker(this)
            .setOnUpdateAvailable { versionInfo ->
                // Handle update available
                UpdateDialog.show(this, versionInfo)
            }
            .setOnNoUpdateAvailable {
                Toast.makeText(this, "You have the latest version", Toast.LENGTH_SHORT).show()
            }
            .setOnError { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
            .check(updateUrl)
        */
    }
}

