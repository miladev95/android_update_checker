package ca.miladev95.updatechecker.examples

import android.app.Activity
import android.widget.Toast
import ca.miladev95.updatechecker.UpdateChecker
import ca.miladev95.updatechecker.models.VersionInfo
import ca.miladev95.updatechecker.ui.UpdateDialog

/**
 * Examples of how to use the UpdateChecker library
 * NOTE: This file is for reference only and is not part of the library
 */
class UsageExamples {

    /**
     * Example 1: Simple check with auto-dialog
     * This is the simplest way to check for updates
     */
    fun simpleCheck(activity: Activity) {
        UpdateChecker(activity)
            .checkAndShowDialog("https://your-server.com/version.json")
    }

    /**
     * Example 2: Check with callbacks
     * This allows you to handle events when updates are available or not
     */
    fun checkWithCallbacks(activity: Activity) {
        UpdateChecker(activity)
            .setOnUpdateAvailable { versionInfo ->
                // Called when update is available
                Toast.makeText(
                    activity,
                    "New version ${versionInfo.versionName} available!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setOnNoUpdateAvailable {
                // Called when no update is available
                Toast.makeText(
                    activity,
                    "You have the latest version",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setOnError { error ->
                // Called when an error occurs
                Toast.makeText(
                    activity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .checkAndShowDialog("https://your-server.com/version.json")
    }

    /**
     * Example 3: Manual check without auto-dialog
     * This gives you full control over when to show the dialog
     */
    fun manualCheck(activity: Activity) {
        UpdateChecker(activity)
            .setOnUpdateAvailable { versionInfo ->
                // You decide when to show the dialog
                UpdateDialog.show(activity, versionInfo)
            }
            .setOnNoUpdateAvailable {
                Toast.makeText(
                    activity,
                    "No updates available",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .check("https://your-server.com/version.json")
    }

    /**
     * Example 4: Silent check on app startup
     * Only shows dialog if update is available, no toast on error
     */
    fun silentCheckOnStartup(activity: Activity) {
        UpdateChecker(activity)
            .setOnError { error ->
                // Silently log the error
                android.util.Log.e("UpdateCheck", "Failed to check updates", error)
            }
            .checkAndShowDialog("https://your-server.com/version.json")
    }

    /**
     * Example 5: Custom dialog with custom actions
     * This allows you to implement custom behavior when user clicks buttons
     */
    fun customDialogActions(activity: Activity) {
        UpdateChecker(activity)
            .setOnUpdateAvailable { versionInfo ->
                UpdateDialog.showCustom(
                    context = activity,
                    versionInfo = versionInfo,
                    onUpdateClick = { info ->
                        // Custom update logic
                        // For example, download APK through your own download manager
                        downloadUpdate(activity, info.updateUrl)
                    },
                    onLaterClick = {
                        // Custom "later" logic
                        // For example, schedule a reminder
                        scheduleReminder(activity)
                    }
                )
            }
            .check("https://your-server.com/version.json")
    }

    /**
     * Example 6: Check only without showing dialog
     * Useful for analytics or logging
     */
    fun checkOnlyForAnalytics(activity: Activity) {
        UpdateChecker(activity)
            .setOnUpdateAvailable { versionInfo ->
                // Log to analytics
                logToAnalytics("update_available", versionInfo)
                // Don't show dialog immediately
            }
            .check("https://your-server.com/version.json")
    }

    /**
     * Example 7: Show dialog only for major updates
     * Check the version and decide whether to show dialog
     */
    fun conditionalDialogDisplay(activity: Activity) {
        UpdateChecker(activity)
            .setOnUpdateAvailable { versionInfo ->
                // Only show dialog if it's a major update or force update
                if (versionInfo.forceUpdate || isMajorUpdate(versionInfo)) {
                    UpdateDialog.show(activity, versionInfo)
                } else {
                    // Show a less intrusive notification
                    showSmallUpdateNotification(activity, versionInfo)
                }
            }
            .check("https://your-server.com/version.json")
    }

    // Helper methods (implementation not shown)
    private fun downloadUpdate(activity: Activity, url: String?) {
        // Implement your download logic
    }

    private fun scheduleReminder(activity: Activity) {
        // Implement reminder scheduling
    }

    private fun logToAnalytics(event: String, versionInfo: VersionInfo) {
        // Implement analytics logging
    }

    private fun isMajorUpdate(versionInfo: VersionInfo): Boolean {
        // Implement logic to determine if it's a major update
        return false
    }

    private fun showSmallUpdateNotification(activity: Activity, versionInfo: VersionInfo) {
        // Show a small notification instead of full dialog
    }
}

