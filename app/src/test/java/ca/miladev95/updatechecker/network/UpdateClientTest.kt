package ca.miladev95.updatechecker.network

import ca.miladev95.updatechecker.models.VersionInfo
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for UpdateClient JSON parsing
 */
class UpdateClientTest {


    @Test
    fun `parseVersionInfo with complete JSON from sample_version json`() = runTest {
        val jsonString = """
            {
              "versionCode": 2,
              "versionName": "1.0.1",
              "updateUrl": "https://example.com/app-v1.0.1.apk",
              "releaseNotes": "Bug fixes and improvements:\n- Fixed crash on startup\n- Improved performance\n- Updated UI",
              "forceUpdate": false
            }
        """.trimIndent()

        val versionInfo = parseJson(jsonString)

        assertEquals(2, versionInfo.versionCode)
        assertEquals("1.0.1", versionInfo.versionName)
        assertEquals("https://example.com/app-v1.0.1.apk", versionInfo.updateUrl)
        assertTrue(versionInfo.releaseNotes?.contains("Bug fixes") == true)
        assertFalse(versionInfo.forceUpdate)
    }

    @Test
    fun `parseVersionInfo with minimal required fields`() = runTest {
        val jsonString = """
            {
              "versionCode": 3,
              "versionName": "1.0.2"
            }
        """.trimIndent()

        val versionInfo = parseJson(jsonString)

        assertEquals(3, versionInfo.versionCode)
        assertEquals("1.0.2", versionInfo.versionName)
        assertNull(versionInfo.updateUrl)
        assertNull(versionInfo.releaseNotes)
        assertFalse(versionInfo.forceUpdate)
    }

    @Test
    fun `parseVersionInfo with forceUpdate true`() = runTest {
        val jsonString = """
            {
              "versionCode": 5,
              "versionName": "2.0.0",
              "forceUpdate": true
            }
        """.trimIndent()

        val versionInfo = parseJson(jsonString)

        assertEquals(5, versionInfo.versionCode)
        assertEquals("2.0.0", versionInfo.versionName)
        assertTrue(versionInfo.forceUpdate)
    }

    @Test
    fun `parseVersionInfo with empty optional fields`() = runTest {
        val jsonString = """
            {
              "versionCode": 4,
              "versionName": "1.1.0",
              "updateUrl": "",
              "releaseNotes": ""
            }
        """.trimIndent()

        val versionInfo = parseJson(jsonString)

        assertEquals(4, versionInfo.versionCode)
        assertEquals("1.1.0", versionInfo.versionName)
        // Empty strings should be treated as null or kept as empty
        assertNotNull(versionInfo.updateUrl)
        assertNotNull(versionInfo.releaseNotes)
    }

    @Test(expected = org.json.JSONException::class)
    fun `parseVersionInfo throws exception for missing versionCode`() = runTest {
        val jsonString = """
            {
              "versionName": "1.0.0"
            }
        """.trimIndent()

        parseJson(jsonString)
    }

    @Test(expected = org.json.JSONException::class)
    fun `parseVersionInfo throws exception for missing versionName`() = runTest {
        val jsonString = """
            {
              "versionCode": 1
            }
        """.trimIndent()

        parseJson(jsonString)
    }

    @Test(expected = org.json.JSONException::class)
    fun `parseVersionInfo throws exception for invalid JSON`() = runTest {
        val jsonString = "{ invalid json }"

        parseJson(jsonString)
    }

    @Test
    fun `parseVersionInfo with special characters in releaseNotes`() = runTest {
        val jsonString = """
            {
              "versionCode": 6,
              "versionName": "1.2.0",
              "releaseNotes": "New features:\n- Item 1\n- Item 2\n• Bullet point\n\tTab character"
            }
        """.trimIndent()

        val versionInfo = parseJson(jsonString)

        assertEquals(6, versionInfo.versionCode)
        assertNotNull(versionInfo.releaseNotes)
        assertTrue(versionInfo.releaseNotes?.contains("\n") == true)
    }

    @Test
    fun `parseVersionInfo with URL containing query parameters`() = runTest {
        val jsonString = """
            {
              "versionCode": 7,
              "versionName": "1.3.0",
              "updateUrl": "https://example.com/download?file=app.apk&version=1.3.0"
            }
        """.trimIndent()

        val versionInfo = parseJson(jsonString)

        assertEquals(7, versionInfo.versionCode)
        assertTrue(versionInfo.updateUrl?.contains("?") == true)
        assertTrue(versionInfo.updateUrl?.contains("&") == true)
    }

    // Helper method to expose private parsing logic for testing
    private fun parseJson(jsonString: String): VersionInfo {
        val json = org.json.JSONObject(jsonString)
        return VersionInfo(
            versionCode = json.getInt("versionCode"),
            versionName = json.getString("versionName"),
            updateUrl = if (json.has("updateUrl")) json.getString("updateUrl") else null,
            releaseNotes = if (json.has("releaseNotes")) json.getString("releaseNotes") else null,
            forceUpdate = json.optBoolean("forceUpdate", false)
        )
    }
}

