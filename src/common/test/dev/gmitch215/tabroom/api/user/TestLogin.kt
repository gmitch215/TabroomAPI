package dev.gmitch215.tabroom.api.user

import dev.gmitch215.tabroom.util.isLoggedIn
import dev.gmitch215.tabroom.util.login
import dev.gmitch215.tabroom.util.logout
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
        val username = getEnv("TABROOM_USERNAME")
        val password = getEnv("TABROOM_PASSWORD")

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

        val nsda = assertNotNull(user.nsda)
        assertFalse { nsda.fullName.isEmpty() }
        assertTrue { nsda.memberId > 0 }
        assertTrue { nsda.meritPoints > 0 }
        assertTrue { nsda.pointsToNextDegree > -1 }
        assertFalse {nsda.lastPointsDate.isEmpty() }

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

                            val ballot = assertNotNull(data.ballot)
                            assertNotEquals("Unknown", ballot.eventName)
                            assertNotEquals("Unknown", ballot.judge)
                            assertNotEquals("Unknown", ballot.opponent)

                            if (ballot.isWin) {
                                assertEquals("W", ballot.decision)
                                assertEquals(1, ballot.ballotsWon)
                                assertEquals(0, ballot.ballotsLost)
                            } else {
                                assertEquals("L", ballot.decision)
                                assertEquals(0, ballot.ballotsWon)
                                assertEquals(1, ballot.ballotsLost)
                            }

                            assertEquals(1, ballot.ballotsCount)
                        }
                }
        }

        // getCurrentSessions
        val sessions = getCurrentSessions()
        assertFalse { sessions.isEmpty() }
        assertTrue { sessions.any { it.isCurrent } }
        assertTrue { sessions.any { it.lastActiveTime != null } }
        assertTrue { sessions.any { it.lastActiveDate != null } }

        for (session in sessions) {
            assertTrue { session.browser.isNotEmpty() }
            assertTrue { session.ip.isNotEmpty() }
        }
    } finally {
        logout()
    }}

    @Test
    fun testLoggedOut() = runTest {
        assertFailsWith<IllegalStateException> { getCurrentUser() }
        assertFailsWith<IllegalStateException> { getJudgeParadigm(1) }
        assertFailsWith<IllegalStateException> { getRoundResults(0, 0) }
        assertFailsWith<IllegalStateException> { getEntryHistory(5) }
        assertFailsWith<IllegalStateException> { getCurrentSessions() }
    }

    @Test
    fun testLogout() = runTest {
        val username = getEnv("TABROOM_USERNAME")
        val password = getEnv("TABROOM_PASSWORD")

        if (username == null || password == null) {
            println("Warning: TABROOM_USERNAME and TABROOM_PASSWORD must be set in the environment.")
            return@runTest
        }

        login(username, password)
        assertTrue { isLoggedIn }

        login(username, password) // re-login
        assertTrue { isLoggedIn }

        logout()
        assertFalse { isLoggedIn }

        logout()
        assertFalse { isLoggedIn }
    }

}