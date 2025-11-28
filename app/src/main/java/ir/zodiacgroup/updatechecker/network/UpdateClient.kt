package ir.zodiacgroup.updatechecker.network

import ir.zodiacgroup.updatechecker.models.VersionInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Client for fetching version information from a remote URL
 */
class UpdateClient {

    /**
     * Fetches version information from the specified URL
     * @param url The URL to fetch version info from
     * @return VersionInfo object containing the server version data
     * @throws Exception if network request fails or JSON parsing fails
     */
    suspend fun fetchVersionInfo(url: String): VersionInfo = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("Accept", "application/json")

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("HTTP error code: $responseCode")
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.use { it.readText() }

            parseVersionInfo(response)
        } finally {
            connection.disconnect()
        }
    }

    /**
     * Parses JSON response to VersionInfo object
     * Expected JSON format:
     * {
     *   "versionCode": 2,
     *   "versionName": "1.0.1",
     *   "updateUrl": "https://...",
     *   "releaseNotes": "Bug fixes",
     *   "forceUpdate": false
     * }
     */
    private fun parseVersionInfo(jsonString: String): VersionInfo {
        val json = JSONObject(jsonString)
        return VersionInfo(
            versionCode = json.getInt("versionCode"),
            versionName = json.getString("versionName"),
            updateUrl = json.optString("updateUrl", null),
            releaseNotes = json.optString("releaseNotes", null),
            forceUpdate = json.optBoolean("forceUpdate", false)
        )
    }
}

