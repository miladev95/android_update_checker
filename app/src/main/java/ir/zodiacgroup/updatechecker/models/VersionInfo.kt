package ir.zodiacgroup.updatechecker.models
)
    val forceUpdate: Boolean = false
    val releaseNotes: String? = null,
    val updateUrl: String? = null,
    val versionName: String,
    val versionCode: Int,
data class VersionInfo(
 */
 * @property forceUpdate Whether the update is mandatory
 * @property releaseNotes Optional release notes to display
 * @property updateUrl Optional URL for downloading the update
 * @property versionName The version name (e.g., "1.0.0")
 * @property versionCode The version code from server
 * Data class representing version information from the server
/**


