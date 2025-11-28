package ca.miladev95.updatechecker.models

/**
 * Data class representing version information from the server
 * @property versionCode The version code from server
 * @property versionName The version name (e.g., "1.0.0")
 * @property updateUrl Optional URL for downloading the update
 * @property releaseNotes Optional release notes to display
 * @property forceUpdate Whether the update is mandatory
 */
data class VersionInfo(
    val versionCode: Int,
    val versionName: String,
    val updateUrl: String? = null,
    val releaseNotes: String? = null,
    val forceUpdate: Boolean = false
)


