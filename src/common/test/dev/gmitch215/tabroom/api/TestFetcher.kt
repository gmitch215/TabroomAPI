package dev.gmitch215.tabroom.api

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
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
        for (event in t1.events) {
            assertTrue { event.name.isNotEmpty() }
            assertTrue { event.entries.isNotEmpty() }

            for (entry in event.entries) {
                assertTrue { entry.name.isNotEmpty() }
                assertTrue { entry.location.isNotEmpty() }
            }

            assertTrue { event.fields.isNotEmpty() }
            for ((key, value) in event.fields) {
                assertTrue { key.isNotEmpty() }
                assertTrue { value.isNotEmpty() }
            }
        }

        val firstBallot = t1.events.filter { it.entries.any { it.ballots.isNotEmpty() } }[0].entries[0].ballots.entries.first().value
        assertTrue(firstBallot.eventName.isNotEmpty())
        assertTrue(firstBallot.round > 0)
        assertTrue(firstBallot.judge.isNotEmpty())
        assertTrue(firstBallot.tournamentDate.isNotEmpty())
        assertTrue(firstBallot.tournament.isNotEmpty())

        // Schaumburg 2024
        val t2 = getTournament(32668)

        assertTrue(t2.name.isNotEmpty())
        assertTrue(t2.descriptionHTML.isNotEmpty())
        assertTrue(t2.year.isNotEmpty())
        assertTrue(t2.location.isNotEmpty())

        assertTrue(t2.events.isNotEmpty())
        assertFalse(t2.events.any { it.entries.any { it.ballots.isNotEmpty() } })
    }

    @Test
    fun testSearchTournaments() = runTest(timeout = 3.minutes) {
        val tournaments = searchTounaments("idc")
        assertTrue(tournaments.isNotEmpty())

        for (tournament in tournaments) {
            assertTrue(tournament.name.isNotEmpty())
            assertTrue(tournament.year.isNotEmpty())
            assertTrue(tournament.location.isNotEmpty())
        }
    }

}