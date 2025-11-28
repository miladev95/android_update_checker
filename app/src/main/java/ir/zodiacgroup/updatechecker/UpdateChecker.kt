package ir.zodiacgroup.updatechecker

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import ir.zodiacgroup.updatechecker.models.VersionInfo
import ir.zodiacgroup.updatechecker.network.UpdateClient
import ir.zodiacgroup.updatechecker.ui.UpdateDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main class for checking application updates
 */
class UpdateChecker(private val context: Context) {

    private val updateClient = UpdateClient()
    private var onUpdateAvailable: ((VersionInfo) -> Unit)? = null
    private var onNoUpdateAvailable: (() -> Unit)? = null
    private var onError: ((Exception) -> Unit)? = null

    /**
     * Gets the current application version code
     */
    private fun getCurrentVersionCode(): Int {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Sets callback for when an update is available
     */
    fun setOnUpdateAvailable(callback: (VersionInfo) -> Unit): UpdateChecker {
        this.onUpdateAvailable = callback
        return this
    }

    /**
     * Sets callback for when no update is available
     */
    fun setOnNoUpdateAvailable(callback: () -> Unit): UpdateChecker {
        this.onNoUpdateAvailable = callback
        return this
    }

    /**
     * Sets callback for when an error occurs
     */
    fun setOnError(callback: (Exception) -> Unit): UpdateChecker {
        this.onError = callback
        return this
    }

    /**
     * Checks for updates from the specified URL
     * @param url The URL to check for version information
     */
    fun check(url: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val versionInfo = updateClient.fetchVersionInfo(url)
                val currentVersionCode = getCurrentVersionCode()

                if (versionInfo.versionCode > currentVersionCode) {
                    onUpdateAvailable?.invoke(versionInfo)
                } else {
                    onNoUpdateAvailable?.invoke()
                }
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    /**
     * Checks for updates and automatically shows a dialog if update is available
     * @param url The URL to check for version information
     * @param showDialogOnUpdate If true, automatically shows update dialog when update is available
     */
    fun checkAndShowDialog(url: String, showDialogOnUpdate: Boolean = true) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val versionInfo = updateClient.fetchVersionInfo(url)
                val currentVersionCode = getCurrentVersionCode()

                if (versionInfo.versionCode > currentVersionCode) {
                    if (showDialogOnUpdate) {
                        UpdateDialog.show(context, versionInfo)
                    }
                    onUpdateAvailable?.invoke(versionInfo)
                } else {
                    onNoUpdateAvailable?.invoke()
                }
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }
}

