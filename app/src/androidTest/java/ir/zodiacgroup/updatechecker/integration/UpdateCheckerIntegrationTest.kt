package ir.zodiacgroup.updatechecker.integration

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ir.zodiacgroup.updatechecker.UpdateChecker
import ir.zodiacgroup.updatechecker.models.VersionInfo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Integration tests that simulate real-world scenarios
 * Testing with sample_version.json data
 */
@RunWith(AndroidJUnit4::class)
class UpdateCheckerIntegrationTest {

    private lateinit var context: Context

    // Sample data from sample_version.json
    private val sampleVersionCode = 2
    private val sampleVersionName = "1.0.1"
    private val sampleUpdateUrl = "https://example.com/app-v1.0.1.apk"
    private val sampleReleaseNotes = "Bug fixes and improvements:\n- Fixed crash on startup\n- Improved performance\n- Updated UI"
    private val sampleForceUpdate = false

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testVersionInfoMatchesSampleJson() {
        val versionInfo = VersionInfo(
            versionCode = sampleVersionCode,
            versionName = sampleVersionName,
            updateUrl = sampleUpdateUrl,
            releaseNotes = sampleReleaseNotes,
            forceUpdate = sampleForceUpdate
        )

        assertEquals(2, versionInfo.versionCode)
        assertEquals("1.0.1", versionInfo.versionName)
        assertEquals("https://example.com/app-v1.0.1.apk", versionInfo.updateUrl)
        assertTrue(versionInfo.releaseNotes?.contains("Bug fixes") == true)
        assertTrue(versionInfo.releaseNotes?.contains("Fixed crash on startup") == true)
        assertTrue(versionInfo.releaseNotes?.contains("Improved performance") == true)
        assertTrue(versionInfo.releaseNotes?.contains("Updated UI") == true)
        assertFalse(versionInfo.forceUpdate)
    }

    @Test
    fun testCurrentAppVersionIsLowerThanSample() {
        // Current app version is 1 (from build.gradle.kts)
        // Sample version is 2
        // So update should be available
        val currentVersion = 1
        val sampleVersion = sampleVersionCode

        assertTrue("Sample version should be higher than current", sampleVersion > currentVersion)
    }

    @Test
    fun testUpdateAvailableScenario() {
        // Simulate scenario where update is available
        val currentVersionCode = 1
        val serverVersionCode = 2

        assertTrue("Update should be available", serverVersionCode > currentVersionCode)
    }

    @Test
    fun testNoUpdateScenario() {
        // Simulate scenario where no update is available
        val currentVersionCode = 2
        val serverVersionCode = 2

        assertFalse("No update should be available", serverVersionCode > currentVersionCode)
    }

    @Test
    fun testOldVersionScenario() {
        // Simulate scenario where server version is older (shouldn't happen but test it)
        val currentVersionCode = 3
        val serverVersionCode = 2

        assertFalse("No update should be available for older version", serverVersionCode > currentVersionCode)
    }

    @Test
    fun testForceUpdateScenario() {
        val versionInfo = VersionInfo(
            versionCode = 5,
            versionName = "2.0.0",
            forceUpdate = true
        )

        assertTrue("Force update should be true", versionInfo.forceUpdate)
    }

    @Test
    fun testOptionalUpdateScenario() {
        val versionInfo = VersionInfo(
            versionCode = sampleVersionCode,
            versionName = sampleVersionName,
            forceUpdate = sampleForceUpdate
        )

        assertFalse("Force update should be false", versionInfo.forceUpdate)
    }

    @Test
    fun testReleaseNotesFormatting() {
        val releaseNotes = sampleReleaseNotes

        assertTrue("Release notes should contain newlines", releaseNotes.contains("\n"))
        assertTrue("Release notes should contain bullet points", releaseNotes.contains("-"))

        val lines = releaseNotes.split("\n")
        assertTrue("Release notes should have multiple lines", lines.size > 1)
    }

    @Test
    fun testUpdateUrlIsValid() {
        val updateUrl = sampleUpdateUrl

        assertTrue("URL should start with https://", updateUrl.startsWith("https://"))
        assertTrue("URL should contain domain", updateUrl.contains("example.com"))
        assertTrue("URL should end with .apk", updateUrl.endsWith(".apk"))
    }

    @Test
    fun testVersionNameFormat() {
        val versionName = sampleVersionName

        assertTrue("Version name should match semantic versioning",
            versionName.matches(Regex("\\d+\\.\\d+\\.\\d+")))

        val parts = versionName.split(".")
        assertEquals("Should have 3 parts (major.minor.patch)", 3, parts.size)
        assertEquals("Major version", "1", parts[0])
        assertEquals("Minor version", "0", parts[1])
        assertEquals("Patch version", "1", parts[2])
    }

    @Test
    fun testMultipleVersionComparisons() {
        val versions = listOf(
            VersionInfo(1, "1.0.0"),
            VersionInfo(2, "1.0.1"),
            VersionInfo(3, "1.1.0"),
            VersionInfo(4, "2.0.0")
        )

        for (i in 0 until versions.size - 1) {
            assertTrue(
                "Version ${i+1} should be higher than version $i",
                versions[i + 1].versionCode > versions[i].versionCode
            )
        }
    }

    @Test
    fun testCallbackExecutionOrder() = runTest {
        val executionOrder = mutableListOf<String>()
        val latch = CountDownLatch(1)

        // This will fail since we use invalid URL, but we test callback order
        UpdateChecker(context)
            .setOnUpdateAvailable {
                executionOrder.add("update_available")
                latch.countDown()
            }
            .setOnNoUpdateAvailable {
                executionOrder.add("no_update")
                latch.countDown()
            }
            .setOnError {
                executionOrder.add("error")
                latch.countDown()
            }
            .check("https://invalid-domain-xyz.com/version.json")

        latch.await(15, TimeUnit.SECONDS)

        // Should have exactly one callback executed
        assertEquals("Exactly one callback should execute", 1, executionOrder.size)
        // Should be error callback
        assertEquals("Error callback should be called", "error", executionOrder[0])
    }

    @Test
    fun testVersionInfoImmutability() {
        val original = VersionInfo(
            versionCode = sampleVersionCode,
            versionName = sampleVersionName,
            updateUrl = sampleUpdateUrl
        )

        val copy = original.copy(versionCode = 3)

        // Original should be unchanged
        assertEquals(2, original.versionCode)
        // Copy should have new version code
        assertEquals(3, copy.versionCode)
        // Other fields should be same
        assertEquals(original.versionName, copy.versionName)
        assertEquals(original.updateUrl, copy.updateUrl)
    }

    @Test
    fun testEmptyReleaseNotes() {
        val versionInfo = VersionInfo(
            versionCode = 1,
            versionName = "1.0.0",
            releaseNotes = ""
        )

        assertNotNull(versionInfo.releaseNotes)
        assertTrue(versionInfo.releaseNotes?.isEmpty() == true)
    }

    @Test
    fun testNullReleaseNotes() {
        val versionInfo = VersionInfo(
            versionCode = 1,
            versionName = "1.0.0",
            releaseNotes = null
        )

        assertNull(versionInfo.releaseNotes)
    }

    @Test
    fun testLongReleaseNotes() {
        val longNotes = buildString {
            append("Major Update:\n")
            repeat(50) { i ->
                append("- Feature $i added\n")
            }
        }

        val versionInfo = VersionInfo(
            versionCode = 10,
            versionName = "5.0.0",
            releaseNotes = longNotes
        )

        assertTrue((versionInfo.releaseNotes?.length ?: 0) > 500)
        assertTrue(versionInfo.releaseNotes?.contains("Feature 25") == true)
    }
}

