package ca.miladev95.updatechecker

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import ca.miladev95.updatechecker.models.VersionInfo
import ca.miladev95.updatechecker.network.UpdateClient
import ca.miladev95.updatechecker.ui.UpdateDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        } catch (_: Exception) {
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

    // New overload: allow passing callbacks directly when calling check()
    /**
     * Checks for updates and invokes the provided callbacks. Returns this for chaining.
     * onUpdate will be invoked with the VersionInfo when an update is available.
     */
    fun check(
        url: String,
        onUpdate: (VersionInfo) -> Unit,
        onNoUpdate: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ): UpdateChecker {
        // wire provided callbacks to this instance then start check
        this.onUpdateAvailable = onUpdate
        if (onNoUpdate != null) this.onNoUpdateAvailable = onNoUpdate
        if (onError != null) this.onError = onError

        check(url)
        return this
    }

    /**
     * Checks for updates, optionally shows dialog and invokes the provided callbacks.
     * onUpdate will be invoked with the VersionInfo when an update is available.
     */
    fun checkAndShowDialog(
        url: String,
        showDialogOnUpdate: Boolean = true,
        onUpdate: (VersionInfo) -> Unit,
        onNoUpdate: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ): UpdateChecker {
        this.onUpdateAvailable = onUpdate
        if (onNoUpdate != null) this.onNoUpdateAvailable = onNoUpdate
        if (onError != null) this.onError = onError

        checkAndShowDialog(url, showDialogOnUpdate)
        return this
    }
}
