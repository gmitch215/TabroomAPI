package dev.gmitch215.tabroom.util

import dev.gmitch215.tabroom.api.user.getCurrentUser
import dev.gmitch215.tabroom.api.user.getEntryHistory
import dev.gmitch215.tabroom.api.user.getJudgeParadigm
import dev.gmitch215.tabroom.api.user.getRoundResults
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class TestLogin {

    @Test
    fun testLogin() = runTest(timeout = 2.minutes) { try {
        val username = System.getenv("TABROOM_USERNAME")
        val password = System.getenv("TABROOM_PASSWORD")

        if (username == null || password == null) {
            println("Warning: TABROOM_USERNAME and TABROOM_PASSWORD must be set in the environment.")
            return@runTest
        }

        login(username, password)

        // getCurrentUser
        val user = getCurrentUser()
        assertFalse { user.email.isEmpty() }
        assertFalse { user.firstName.isEmpty() }
        assertNotNull(user.middleName)
        assertFalse { user.lastName.isEmpty() }
        assertFalse { user.pronouns.isEmpty() }
        assertNotEquals("(000) 000-0000", user.phoneNumber)
        assertNotNull(user.address)
        assertNotEquals("Unknown", user.timeZone)
        assertNotEquals("Unknown", user.city)
        assertNotEquals("Unknown", user.state)
        assertNotEquals("Unknown", user.country)
        assertNotEquals(0, user.zipCode)

        assertNotNull(user.nsda)
        assertFalse { user.nsda.fullName.isEmpty() }
        assertTrue { user.nsda.memberId > 0 }
        assertTrue { user.nsda.meritPoints > 0 }
        assertTrue { user.nsda.pointsToNextDegree > -1 }
        assertFalse { user.nsda.lastPointsDate.isEmpty() }

        // getEntryHistory
        val history = getEntryHistory(5)
        assertFalse { history.isEmpty() }
        for (entry in history) {
            assertFalse { entry.tournament.name.isEmpty() }
            assertFalse { entry.tournament.year.isEmpty() }

            assertFalse { entry.division.isEmpty() }
            assertFalse { entry.code.isEmpty() }
            assertFalse { entry.date.isEmpty() }

            if (entry.rounds.isNotEmpty())
                for (round in entry.rounds) {
                    assertFalse { round.start.isEmpty() }
                    assertFalse { round.label.isEmpty() }

                    if (round.data.isNotEmpty())
                        for (data in round.data) {
                            assertTrue { data.judge.hasParadigm }
                            assertFalse { data.judgeParadigm.isEmpty() }
                            assertNotNull(data.rfd)

                            assertNotNull(data.ballot)
                            assertNotEquals("Unknown", data.ballot.eventName)
                            assertNotEquals("Unknown", data.ballot.judge)
                            assertNotEquals("Unknown", data.ballot.opponent)

                            if (data.ballot.isWin) {
                                assertEquals("W", data.ballot.decision)
                                assertEquals(1, data.ballot.ballotsWon)
                                assertEquals(0, data.ballot.ballotsLost)
                            } else {
                                assertEquals("L", data.ballot.decision)
                                assertEquals(0, data.ballot.ballotsWon)
                                assertEquals(1, data.ballot.ballotsLost)
                            }

                            assertEquals(1, data.ballot.ballotsCount)
                        }
                }
        }
    } finally { logout() }}

    @Test
    fun testLoggedOut() = runTest {
        assertFailsWith<IllegalStateException> { getCurrentUser() }
        assertFailsWith<IllegalStateException> { getJudgeParadigm(1) }
        assertFailsWith<IllegalStateException> { getRoundResults(0, 0) }
        assertFailsWith<IllegalStateException> { getEntryHistory(5) }
    }

}