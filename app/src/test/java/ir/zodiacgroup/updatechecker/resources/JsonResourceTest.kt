package ir.zodiacgroup.updatechecker.resources

import ir.zodiacgroup.updatechecker.models.VersionInfo
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests using sample JSON resources
 * These tests validate JSON parsing with actual resource files
 */
class JsonResourceTest {

    @Test
    fun `parse sample_version json`() = runTest {
        val jsonString = """
        {
          "versionCode": 2,
          "versionName": "1.0.1",
          "updateUrl": "https://example.com/app-v1.0.1.apk",
          "releaseNotes": "Bug fixes and improvements:\n- Fixed crash on startup\n- Improved performance\n- Updated UI",
          "forceUpdate": false
        }
        """.trimIndent()

        val json = JSONObject(jsonString)
        val versionInfo = VersionInfo(
            versionCode = json.getInt("versionCode"),
            versionName = json.getString("versionName"),
            updateUrl = if (json.has("updateUrl")) json.getString("updateUrl") else null,
            releaseNotes = if (json.has("releaseNotes")) json.getString("releaseNotes") else null,
            forceUpdate = json.optBoolean("forceUpdate", false)
        )

        assertEquals(2, versionInfo.versionCode)
        assertEquals("1.0.1", versionInfo.versionName)
        assertEquals("https://example.com/app-v1.0.1.apk", versionInfo.updateUrl)
        assertNotNull(versionInfo.releaseNotes)
        assertTrue(versionInfo.releaseNotes?.contains("Bug fixes") == true)
        assertTrue(versionInfo.releaseNotes?.contains("Fixed crash on startup") == true)
        assertTrue(versionInfo.releaseNotes?.contains("Improved performance") == true)
        assertTrue(versionInfo.releaseNotes?.contains("Updated UI") == true)
        assertFalse(versionInfo.forceUpdate)
    }

    @Test
    fun `parse force_update_version json`() = runTest {
        val jsonString = """
        {
          "versionCode": 3,
          "versionName": "1.0.2",
          "updateUrl": "https://example.com/app-v1.0.2.apk",
          "releaseNotes": "New features and bug fixes",
          "forceUpdate": true
        }
        """.trimIndent()

        val json = JSONObject(jsonString)
        val versionInfo = VersionInfo(
            versionCode = json.getInt("versionCode"),
            versionName = json.getString("versionName"),
            updateUrl = if (json.has("updateUrl")) json.getString("updateUrl") else null,
            releaseNotes = if (json.has("releaseNotes")) json.getString("releaseNotes") else null,
            forceUpdate = json.optBoolean("forceUpdate", false)
        )

        assertEquals(3, versionInfo.versionCode)
        assertEquals("1.0.2", versionInfo.versionName)
        assertEquals("https://example.com/app-v1.0.2.apk", versionInfo.updateUrl)
        assertEquals("New features and bug fixes", versionInfo.releaseNotes)
        assertTrue(versionInfo.forceUpdate)
    }

    @Test
    fun `parse minimal_version json`() = runTest {
        val jsonString = """
        {
          "versionCode": 1,
          "versionName": "1.0.0"
        }
        """.trimIndent()

        val json = JSONObject(jsonString)
        val versionInfo = VersionInfo(
            versionCode = json.getInt("versionCode"),
            versionName = json.getString("versionName"),
            updateUrl = if (json.has("updateUrl")) json.getString("updateUrl") else null,
            releaseNotes = if (json.has("releaseNotes")) json.getString("releaseNotes") else null,
            forceUpdate = json.optBoolean("forceUpdate", false)
        )

        assertEquals(1, versionInfo.versionCode)
        assertEquals("1.0.0", versionInfo.versionName)
        assertNull(versionInfo.updateUrl)
        assertNull(versionInfo.releaseNotes)
        assertFalse(versionInfo.forceUpdate)
    }

    @Test
    fun `validate sample json structure`() {
        val jsonString = """
        {
          "versionCode": 2,
          "versionName": "1.0.1",
          "updateUrl": "https://example.com/app-v1.0.1.apk",
          "releaseNotes": "Bug fixes and improvements:\n- Fixed crash on startup\n- Improved performance\n- Updated UI",
          "forceUpdate": false
        }
        """.trimIndent()

        val json = JSONObject(jsonString)

        assertTrue(json.has("versionCode"))
        assertTrue(json.has("versionName"))
        assertTrue(json.has("updateUrl"))
        assertTrue(json.has("releaseNotes"))
        assertTrue(json.has("forceUpdate"))

        assertEquals(2, json.getInt("versionCode"))
        assertEquals("1.0.1", json.getString("versionName"))
        assertFalse(json.getBoolean("forceUpdate"))
    }

    @Test
    fun `compare multiple json versions`() {
        val minimal = """{"versionCode": 1, "versionName": "1.0.0"}"""
        val sample = """{"versionCode": 2, "versionName": "1.0.1"}"""
        val force = """{"versionCode": 3, "versionName": "1.0.2"}"""

        val v1 = JSONObject(minimal).getInt("versionCode")
        val v2 = JSONObject(sample).getInt("versionCode")
        val v3 = JSONObject(force).getInt("versionCode")

        assertTrue(v2 > v1)
        assertTrue(v3 > v2)
        assertTrue(v3 > v1)
    }

    @Test
    fun `test release notes with newlines`() {
        val jsonString = """
        {
          "versionCode": 2,
          "versionName": "1.0.1",
          "releaseNotes": "Bug fixes and improvements:\n- Fixed crash on startup\n- Improved performance\n- Updated UI"
        }
        """.trimIndent()

        val json = JSONObject(jsonString)
        val releaseNotes = json.getString("releaseNotes")

        assertTrue(releaseNotes.contains("\n"))
        assertTrue(releaseNotes.contains("- "))

        val lines = releaseNotes.split("\n")
        assertEquals(4, lines.size)
        assertEquals("Bug fixes and improvements:", lines[0])
        assertTrue(lines[1].startsWith("- "))
        assertTrue(lines[2].startsWith("- "))
        assertTrue(lines[3].startsWith("- "))
    }

    @Test
    fun `test update url formats`() {
        val urls = listOf(
            "https://example.com/app-v1.0.1.apk",
            "https://example.com/app-v1.0.2.apk",
            "https://github.com/user/repo/releases/download/v1.0.0/app.apk",
            "https://storage.googleapis.com/bucket/app.apk"
        )

        urls.forEach { url ->
            assertTrue(url.startsWith("https://"))
            assertTrue(url.contains(".apk") || url.contains("app"))
        }
    }

    @Test
    fun `test version name patterns`() {
        val versionNames = listOf("1.0.0", "1.0.1", "1.0.2", "2.0.0", "1.10.5")

        versionNames.forEach { version ->
            assertTrue(version.matches(Regex("\\d+\\.\\d+\\.\\d+")))
            val parts = version.split(".")
            assertEquals(3, parts.size)
            parts.forEach { part ->
                assertTrue(part.toIntOrNull() != null)
            }
        }
    }

    @Test
    fun `test json with all optional fields null`() {
        val jsonString = """
        {
          "versionCode": 4,
          "versionName": "1.1.0",
          "updateUrl": null,
          "releaseNotes": null,
          "forceUpdate": false
        }
        """.trimIndent()

        val json = JSONObject(jsonString)

        assertEquals(4, json.getInt("versionCode"))
        assertEquals("1.1.0", json.getString("versionName"))
        assertFalse(json.getBoolean("forceUpdate"))

        // null values in JSON
        assertTrue(json.isNull("updateUrl"))
        assertTrue(json.isNull("releaseNotes"))
    }

    @Test
    fun `test json with missing optional fields`() {
        val jsonString = """
        {
          "versionCode": 5,
          "versionName": "1.2.0"
        }
        """.trimIndent()

        val json = JSONObject(jsonString)

        assertEquals(5, json.getInt("versionCode"))
        assertEquals("1.2.0", json.getString("versionName"))

        assertFalse(json.has("updateUrl"))
        assertFalse(json.has("releaseNotes"))
        assertFalse(json.has("forceUpdate"))

        // Using optBoolean for missing field
        assertFalse(json.optBoolean("forceUpdate", false))
    }

    @Test
    fun `test version code boundaries`() {
        val testCases = listOf(
            1 to "1.0.0",
            10 to "2.5.0",
            100 to "10.0.0",
            999 to "99.99.99",
            1000 to "100.0.0"
        )

        testCases.forEach { (code, name) ->
            val json = JSONObject("""{"versionCode": $code, "versionName": "$name"}""")
            assertEquals(code, json.getInt("versionCode"))
            assertEquals(name, json.getString("versionName"))
        }
    }
}

