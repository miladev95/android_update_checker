package ir.zodiacgroup.updatechecker.models

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for VersionInfo data class
 */
class VersionInfoTest {

    @Test
    fun `create VersionInfo with required fields only`() {
        val versionInfo = VersionInfo(
            versionCode = 2,
            versionName = "1.0.1"
        )

        assertEquals(2, versionInfo.versionCode)
        assertEquals("1.0.1", versionInfo.versionName)
        assertNull(versionInfo.updateUrl)
        assertNull(versionInfo.releaseNotes)
        assertFalse(versionInfo.forceUpdate)
    }

    @Test
    fun `create VersionInfo with all fields`() {
        val versionInfo = VersionInfo(
            versionCode = 2,
            versionName = "1.0.1",
            updateUrl = "https://example.com/app.apk",
            releaseNotes = "Bug fixes",
            forceUpdate = true
        )

        assertEquals(2, versionInfo.versionCode)
        assertEquals("1.0.1", versionInfo.versionName)
        assertEquals("https://example.com/app.apk", versionInfo.updateUrl)
        assertEquals("Bug fixes", versionInfo.releaseNotes)
        assertTrue(versionInfo.forceUpdate)
    }

    @Test
    fun `VersionInfo default values are correct`() {
        val versionInfo = VersionInfo(
            versionCode = 1,
            versionName = "1.0.0"
        )

        assertNull(versionInfo.updateUrl)
        assertNull(versionInfo.releaseNotes)
        assertFalse(versionInfo.forceUpdate)
    }

    @Test
    fun `VersionInfo data class equality works`() {
        val v1 = VersionInfo(1, "1.0.0")
        val v2 = VersionInfo(1, "1.0.0")
        val v3 = VersionInfo(2, "1.0.1")

        assertEquals(v1, v2)
        assertNotEquals(v1, v3)
    }

    @Test
    fun `VersionInfo copy works correctly`() {
        val original = VersionInfo(
            versionCode = 1,
            versionName = "1.0.0",
            updateUrl = "https://example.com"
        )

        val copy = original.copy(versionCode = 2)

        assertEquals(2, copy.versionCode)
        assertEquals(original.versionName, copy.versionName)
        assertEquals(original.updateUrl, copy.updateUrl)
    }
}

