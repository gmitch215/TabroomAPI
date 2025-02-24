package xyz.gmitch215.tabroom.api

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class TestFetcher {

    @Test
    fun testTournament() = runTest(timeout = 3.minutes) {
        // Jack Howe 2024
        val t1 = getTournament(31822)

        assertTrue(t1.name.isNotEmpty())
        assertTrue(t1.descriptionHTML.isNotEmpty())
        assertTrue(t1.year.isNotEmpty())
        assertTrue(t1.location.isNotEmpty())

        assertTrue(t1.events.isNotEmpty())

        // Schaumburg 2024
        val t2 = getTournament(32668)

        assertTrue(t2.name.isNotEmpty())
        assertTrue(t2.descriptionHTML.isNotEmpty())
        assertTrue(t2.year.isNotEmpty())
        assertTrue(t2.location.isNotEmpty())

        assertTrue(t2.events.isNotEmpty())
    }

}