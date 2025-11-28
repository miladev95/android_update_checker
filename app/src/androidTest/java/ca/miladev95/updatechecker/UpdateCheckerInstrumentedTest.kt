package ca.miladev95.updatechecker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.miladev95.updatechecker.models.VersionInfo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented tests for UpdateChecker
 * These tests run on an Android device/emulator
 */
@RunWith(AndroidJUnit4::class)
class UpdateCheckerInstrumentedTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testUpdateCheckerInstantiation() {
        val updateChecker = UpdateChecker(context)
        assertNotNull(updateChecker)
    }

    @Test
    fun testCallbackChaining() {
        val updateChecker = UpdateChecker(context)
            .setOnUpdateAvailable { /* callback set */ }
            .setOnNoUpdateAvailable { /* callback set */ }
            .setOnError { /* callback set */ }

        assertNotNull(updateChecker)
    }

    @Test
    fun testVersionInfoCreation() {
        val versionInfo = VersionInfo(
            versionCode = 2,
            versionName = "1.0.1",
            updateUrl = "https://example.com/app-v1.0.1.apk",
            releaseNotes = "Bug fixes and improvements:\n- Fixed crash on startup\n- Improved performance\n- Updated UI",
            forceUpdate = false
        )

        assertEquals(2, versionInfo.versionCode)
        assertEquals("1.0.1", versionInfo.versionName)
        assertEquals("https://example.com/app-v1.0.1.apk", versionInfo.updateUrl)
        assertNotNull(versionInfo.releaseNotes)
        assertFalse(versionInfo.forceUpdate)
    }

    @Test
    fun testErrorCallbackOnInvalidUrl() = runTest {
        val latch = CountDownLatch(1)
        var errorOccurred = false
        var errorMessage: String? = null

        UpdateChecker(context)
            .setOnError { error ->
                errorOccurred = true
                errorMessage = error.message
                latch.countDown()
            }
            .check("https://invalid-url-that-does-not-exist.com/version.json")

        // Wait up to 15 seconds for the error callback
        latch.await(15, TimeUnit.SECONDS)

        assertTrue("Error callback should be called for invalid URL", errorOccurred)
        assertNotNull("Error message should not be null", errorMessage)
    }

    @Test
    fun testMultipleCallbacksCanBeSet() {
        var count = 0

        val checker = UpdateChecker(context)
            .setOnUpdateAvailable { count++ }
            .setOnNoUpdateAvailable { count++ }
            .setOnError { count++ }

        assertNotNull(checker)
        assertEquals(0, count) // Callbacks not invoked yet
    }

    @Test
    fun testVersionInfoWithMinimalFields() {
        val versionInfo = VersionInfo(
            versionCode = 1,
            versionName = "1.0.0"
        )

        assertEquals(1, versionInfo.versionCode)
        assertEquals("1.0.0", versionInfo.versionName)
        assertNull(versionInfo.updateUrl)
        assertNull(versionInfo.releaseNotes)
        assertFalse(versionInfo.forceUpdate)
    }

    @Test
    fun testVersionInfoWithForceUpdate() {
        val versionInfo = VersionInfo(
            versionCode = 5,
            versionName = "2.0.0",
            forceUpdate = true
        )

        assertTrue(versionInfo.forceUpdate)
    }

    @Test
    fun testVersionComparison() {
        val v1 = VersionInfo(1, "1.0.0")
        val v2 = VersionInfo(2, "1.0.1")
        val v3 = VersionInfo(3, "1.0.2")

        assertTrue(v2.versionCode > v1.versionCode)
        assertTrue(v3.versionCode > v2.versionCode)
        assertTrue(v3.versionCode > v1.versionCode)
    }

    @Test
    fun testVersionInfoEquality() {
        val v1 = VersionInfo(
            versionCode = 2,
            versionName = "1.0.1",
            updateUrl = "https://example.com/app.apk"
        )

        val v2 = VersionInfo(
            versionCode = 2,
            versionName = "1.0.1",
            updateUrl = "https://example.com/app.apk"
        )

        val v3 = VersionInfo(
            versionCode = 3,
            versionName = "1.0.1",
            updateUrl = "https://example.com/app.apk"
        )

        assertEquals(v1, v2)
        assertNotEquals(v1, v3)
    }

    @Test
    fun testContextIsValid() {
        assertNotNull(context)
        assertNotNull(context.packageName)
        assertEquals("ca.miladev95.updatechecker", context.packageName)
    }
}

