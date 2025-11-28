package ca.miladev95.updatechecker.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import ca.miladev95.updatechecker.models.VersionInfo

/**
 * Dialog for displaying update information to the user
 */
object UpdateDialog {

    /**
     * Shows an update dialog with version information
     * @param context The context to show the dialog in
     * @param versionInfo The version information to display
     */
    fun show(context: Context, versionInfo: VersionInfo) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update Available")

        // Build message
        val message = buildString {
            append("A new version (${versionInfo.versionName}) is available!\n\n")
            if (!versionInfo.releaseNotes.isNullOrEmpty()) {
                append("What's New:\n")
                append(versionInfo.releaseNotes)
            }
        }

        builder.setMessage(message)
        builder.setCancelable(!versionInfo.forceUpdate)

        // Update button
        if (!versionInfo.updateUrl.isNullOrEmpty()) {
            builder.setPositiveButton("Update") { dialog, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo.updateUrl))
                context.startActivity(intent)
                dialog.dismiss()
            }
        }

        // Later button (only if not force update)
        if (!versionInfo.forceUpdate) {
            builder.setNegativeButton("Later") { dialog, _ ->
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Shows a custom update dialog with custom callbacks
     * @param context The context to show the dialog in
     * @param versionInfo The version information to display
     * @param onUpdateClick Callback when update button is clicked
     * @param onLaterClick Callback when later button is clicked (only called if not force update)
     */
    fun showCustom(
        context: Context,
        versionInfo: VersionInfo,
        onUpdateClick: ((VersionInfo) -> Unit)? = null,
        onLaterClick: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update Available")

        val message = buildString {
            append("A new version (${versionInfo.versionName}) is available!\n\n")
            if (!versionInfo.releaseNotes.isNullOrEmpty()) {
                append("What's New:\n")
                append(versionInfo.releaseNotes)
            }
        }

        builder.setMessage(message)
        builder.setCancelable(!versionInfo.forceUpdate)

        builder.setPositiveButton("Update") { dialog, _ ->
            onUpdateClick?.invoke(versionInfo) ?: run {
                if (!versionInfo.updateUrl.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo.updateUrl))
                    context.startActivity(intent)
                }
            }
            dialog.dismiss()
        }

        if (!versionInfo.forceUpdate) {
            builder.setNegativeButton("Later") { dialog, _ ->
                onLaterClick?.invoke()
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }
}

