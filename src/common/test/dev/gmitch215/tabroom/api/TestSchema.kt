package dev.gmitch215.tabroom.api

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestSchema {

    @Test
    fun testBallot() {
        val ballot = Ballot(
            ballotsLost = 1,
            ballotsWon = 2,
            ballotsCount = 3,
            decision = "W",
            isElimination = true,
            level = DebateLevel.OPEN,
            eventName = "LD",
            judge = "Judge Name",
            side = DebateSide.AFFIRMATIVE,
            opponent = "Opponent Name",
            round = 1,
            speaker1Points = 28.0,
            speaker2Points = 27.5,
            tournament = "Tournament Name",
            tournamentDate = "2023-10-01"
        )

        assertTrue(ballot.ballotsLost == 1)
        assertTrue(ballot.ballotsWon == 2)
        assertTrue(ballot.ballotsCount == 3)
        assertTrue(ballot.decision == "W")
        assertTrue(ballot.isElimination)
        assertTrue(ballot.level == DebateLevel.OPEN)
        assertTrue(ballot.eventName == "LD")
        assertTrue(ballot.judge == "Judge Name")
        assertTrue(ballot.side == DebateSide.AFFIRMATIVE)
        assertTrue(ballot.opponent == "Opponent Name")
        assertTrue(ballot.round == 1)
        assertTrue(ballot.speaker1Points == 28.0)
        assertTrue(ballot.speaker2Points == 27.5)
        assertTrue(ballot.tournament == "Tournament Name")
        assertTrue(ballot.tournamentDate == "2023-10-01")

        assertTrue(ballot.isWin)
    }

    @Test
    fun testEvent() {
        val event = Event(
            name = "LD",
            id = 12345,
            fields = mapOf(
                "topic" to "Resolved: The United States ought to provide a universal basic income.",
                "fees" to "50",
                "rounds" to "5",
            ),
            entries = listOf(
                Entry(
                    school = "School Name",
                    location = "Location Name",
                    name = "Entry Name",
                    code = "Entry Code",
                    ballots = emptyMap()
                )
            )
        )

        assertTrue(event.name == "LD")
        assertTrue(event.id == 12345)
        assertTrue(event.fields["topic"] == "Resolved: The United States ought to provide a universal basic income.")
        assertTrue(event.fields["fees"] == "50")
        assertTrue(event.fields["rounds"] == "5")

        assertTrue(event.entries.isNotEmpty())
        assertTrue(event.entries[0].school == "School Name")
        assertTrue(event.entries[0].location == "Location Name")
        assertTrue(event.entries[0].name == "Entry Name")
        assertTrue(event.entries[0].code == "Entry Code")
        assertTrue(event.entries[0].ballots.isEmpty())
    }

    @Test
    fun testJudge() {
        val judge = Judge(
            firstName = "John",
            lastName = "Doe",
            school = "Judge School",
            location = "Judge Location",
            hasParadigm = false
        )

        assertTrue(judge.firstName == "John")
        assertTrue(judge.lastName == "Doe")
        assertTrue(judge.school == "Judge School")
        assertTrue(judge.location == "Judge Location")
        assertFalse(judge.hasParadigm)
    }
}