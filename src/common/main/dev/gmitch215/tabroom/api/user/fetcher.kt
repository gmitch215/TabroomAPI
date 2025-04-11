package dev.gmitch215.tabroom.api.user

import dev.gmitch215.tabroom.api.Ballot
import dev.gmitch215.tabroom.api.DebateLevel
import dev.gmitch215.tabroom.api.DebateSide
import dev.gmitch215.tabroom.api.Judge
import dev.gmitch215.tabroom.api.getTournament
import dev.gmitch215.tabroom.util.JUDGE_PARADIGM
import dev.gmitch215.tabroom.util.USER_HOME
import dev.gmitch215.tabroom.util.USER_PROFILE
import dev.gmitch215.tabroom.util.USER_RESULTS_HISTORY
import dev.gmitch215.tabroom.util.fetchDocument
import dev.gmitch215.tabroom.util.html.inputValue
import dev.gmitch215.tabroom.util.html.querySelector
import dev.gmitch215.tabroom.util.html.querySelectorAll
import dev.gmitch215.tabroom.util.isLoggedIn
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmName

private const val USER_TIME_ZONE = "select[name=\"timezone\"] option[selected]"
private const val USER_STATE = "select[name=\"state\"] option[selected]"
private const val USER_COUNTRY = "select[name=\"country\"] option[selected]"
private const val NSDA_BOX_INFO = "div.odd.smallish > span.half"

/**
 * Fetches the current user from Tabroom.
 * @throws IllegalStateException if not logged in
 */
@JvmName("getCurrentUserAsync")
@JsName("getCurrentUserAsync")
suspend fun getCurrentUser(): User = coroutineScope {
    if (!isLoggedIn) throw IllegalStateException("User is not logged in")

    val doc = USER_PROFILE.fetchDocument()

    val nsda = async {
        val nsdaInfo = doc.querySelectorAll(NSDA_BOX_INFO)
        if (nsdaInfo.isEmpty()) return@async null
        if (nsdaInfo.size < 10) throw IllegalStateException("NSDA info is incomplete: expected 10 elements, got ${nsdaInfo.size}")

        return@async NSDAUser(
            nsdaInfo[1].textContent.trim(),
            nsdaInfo[3].textContent.run { substring(1) }.toIntOrNull() ?: 0,
            nsdaInfo[5].textContent.toIntOrNull() ?: 0,
            nsdaInfo[7].textContent.toIntOrNull() ?: -1,
            nsdaInfo[9].textContent.trim(),
        )
    }

    val email = async { doc.inputValue("email") ?: "" }
    val first = async { doc.inputValue("first") ?: "" }
    val middle = async { doc.inputValue("middle") ?: "" }
    val last = async { doc.inputValue("last") ?: "" }
    val phone = async { doc.inputValue("phone") ?: "(000) 000-0000" }
    val pronouns = async { doc.inputValue("pronoun") ?: "He/Him" }
    val timeZone = async { doc.querySelector(USER_TIME_ZONE)?.textContent?.trim() ?: "Unknown" }
    val address = async { doc.inputValue("street") ?: "" }
    val city = async { doc.inputValue("city") ?: "Unknown" }
    val state = async { doc.querySelector(USER_STATE)?.textContent?.trim() ?: "Unknown" }
    val country = async { doc.querySelector(USER_COUNTRY)?.textContent?.trim() ?: "Unknown" }
    val zip = async { doc.inputValue("zip")?.toIntOrNull() ?: 0 }

    return@coroutineScope User(
        email.await(),
        first.await(),
        middle.await(),
        last.await(),
        phone.await(),
        pronouns.await(),
        timeZone.await(),
        address.await(),
        city.await(),
        state.await(),
        country.await(),
        zip.await(),
        nsda.await()
    )
}

private const val PARADIGM_TEXT = "div.screens > div.paradigm"

/**
 * Fetches the judge paradigm for a given judge ID.
 * @param judgeId The ID of the judge
 * @throws IllegalStateException if not logged in
 */
@JvmName("getJudgeParadigmAsync")
@JsName("getJudgeParadigmAsync")
suspend fun getJudgeParadigm(judgeId: Int): String = coroutineScope {
    if (!isLoggedIn) throw IllegalStateException("User is not logged in")
    if (judgeId <= 0) return@coroutineScope ""

    val paradigm = JUDGE_PARADIGM(judgeId).fetchDocument()
    return@coroutineScope paradigm.querySelector(PARADIGM_TEXT)?.textContent?.trim() ?: ""
}

private const val ROUND_RESULTS_ROW = "div.main > div.full > table > tbody > tr.row"
private const val ROUND_FEEDBACK_ROW = "div.main > div.full > table > tbody > tr.feedback"

/**
 * Gets the round results for the current user.
 * @param tourneyId The ID of the tournament
 * @param studentId The ID of the student for the results
 * @throws IllegalStateException if not logged in
 */
@JvmName("getRoundResultsAsync")
@JsName("getRoundResultsAsync")
suspend fun getRoundResults(tourneyId: Int, studentId: Int): List<Round> = coroutineScope {
    if (!isLoggedIn) throw IllegalStateException("User is not logged in")

    val results = USER_RESULTS_HISTORY(tourneyId, studentId).fetchDocument()
    val entries = results.querySelectorAll(ROUND_RESULTS_ROW)
    val rounds = mutableListOf<Round>()

    if (entries.isEmpty()) return@coroutineScope rounds

    coroutineScope {
        for ((i, entry) in entries.withIndex())
            launch {
                val label = entry.children[0].children[0].textContent.trim()
                val start = entry.children[1].textContent
                val room = entry.children[2].textContent
                val side = DebateSide.fromString(entry.children[3].textContent)
                val opponent = entry.children[4].textContent
                val docShareLink = entry.children[5].children[0]["href"] ?: ""

                val data = mutableListOf<RoundData>()

                if (entry.children.size > 6 && entry.children[6].children.isNotEmpty() && entry.children[6].children[0].children.isNotEmpty()) {
                    val rest = entry.children[6].children[0].children[0] // two children down

                    coroutineScope {
                        for (child in rest.children)
                            launch {
                                val judgeData = async {
                                    val firstElement = child.children[0].children[0]
                                    val hasParadigm =
                                        firstElement.children.isNotEmpty() // Check if first is a paradigm span element with an inner anchor link
                                    if (hasParadigm) {
                                        val paradigmLink =
                                            firstElement.children[0]["href"] ?: return@async null to ""
                                        val judgeId = paradigmLink.substringAfter("judge_person_id=").toIntOrNull()
                                            ?: return@async null to ""
                                        val paradigm = async { getJudgeParadigm(judgeId) }

                                        val secondElement = child.children[0].children[1]
                                        val judgeName = secondElement.textContent.trim()
                                        val (firstName, lastName) = judgeName.split(", ", limit = 2)

                                        return@async Judge(
                                            firstName,
                                            lastName,
                                            "Unknown",
                                            "Unknown",
                                            true
                                        ) to paradigm.await()
                                    } else {
                                        val judgeName = firstElement.textContent.trim()
                                        val (firstName, lastName) = judgeName.split(", ", limit = 2)
                                        return@async Judge(firstName, lastName, "Unknown", "Unknown", false) to ""
                                    }
                                }

                                val ballot = if (child.children.size > 1 && child.children[1].children.size > 1) {
                                    val ballotData = child.children[1]
                                    val decision = ballotData.children[0].textContent.trim()
                                    val wc = if (decision == "W") 1 else 0
                                    val restOfResult = ballotData.children[1]

                                    if (restOfResult.children.isNotEmpty()) {
                                        val speaker1Points =
                                            restOfResult.children[0].children[1].textContent.toDoubleOrNull() ?: 0.0
                                        val speaker2Points = if (restOfResult.children.size > 1) {
                                            restOfResult.children[1].children[1].textContent.toDoubleOrNull() ?: 0.0
                                        } else 0.0

                                        Ballot(
                                            1 - wc,
                                            wc,
                                            1,
                                            decision,
                                            false,
                                            side = side,
                                            opponent = opponent,
                                            tournament = "Unknown",
                                            tournamentDate = "Unknown",
                                            speaker1Points = speaker1Points,
                                            speaker2Points = speaker2Points
                                        )
                                    } else
                                        Ballot(
                                            1 - wc,
                                            wc,
                                            1,
                                            decision,
                                            false,
                                            side = side,
                                            opponent = opponent,
                                            tournament = "Unknown",
                                            tournamentDate = "Unknown"
                                        )
                                } else null

                                val rfd = if (rest.children[0].children.size > 1) {
                                    results.querySelectorAll(ROUND_FEEDBACK_ROW)[i].textContent.trim()
                                } else ""

                                val (judge, judgeParadigm) = judgeData.await()
                                if (judge == null) return@launch

                                data.add(RoundData(judge, judgeParadigm, ballot, rfd))
                            }
                    }
                }

                rounds.add(Round(label, start, room, docShareLink, data))
            }
    }

    return@coroutineScope rounds
}

private const val USER_RESULTS_ROW = "div.screens.results > table > tbody > tr"

/**
 * Fetches the entry history of the current user.
 *
 * This function retrieves the entry history of the user, including tournament entries,
 * dates, codes, divisions, and round results. Tabroom will go to a 502 error page if
 * the application requests too many entries at once, so this function will only fetch the first
 * entries up to the specified limit. You can specify `0` to fetch all entries. By default, it
 * will fetch the last 10 entries.
 *
 * @param limit The maximum number of entries to fetch
 * @throws IllegalStateException if not logged in
 */
@JvmName("getEntryHistoryAsync")
@JsName("getEntryHistoryAsync")
suspend fun getEntryHistory(limit: Int = 10): List<TournamentEntry> = coroutineScope {
    if (!isLoggedIn) throw IllegalStateException("User is not logged in")

    val home = USER_HOME.fetchDocument()
    val entries = home.querySelectorAll(USER_RESULTS_ROW).take(if (limit == 0) Int.MAX_VALUE else limit)

    val history = mutableListOf<TournamentEntry>()

    coroutineScope {
        for (entry in entries)
            launch {
                val date = entry.children[1].textContent.trim()
                val code = entry.children[2].textContent.trim()
                val division = entry.children[3].textContent.trim()

                val resultsLink = entry.children[4].children[0]["href"] ?: return@launch
                val (tourneyId, studentId) = resultsLink.substringAfter("tourn_id=").split("&student_id=").map { it.toIntOrNull() ?: 0 }

                val tourney = async { getTournament(tourneyId) }
                val rounds = async { getRoundResults(tourneyId, studentId) }

                history.add(TournamentEntry(
                    tourney.await(),
                    date,
                    code,
                    division,
                    rounds.await()
                ))
            }
    }

    for (entry in history)
        for (round in entry.rounds)
            for (data in round.data)
                if (data.ballot != null) {
                    data.ballot.tournament = entry.tournament.name
                    data.ballot.tournamentDate = entry.tournament.year
                    data.ballot.eventName = entry.division
                    data.ballot.judge = "${data.judge.lastName}, ${data.judge.firstName}"
                    data.ballot.level = when {
                        "varsity" in entry.division.lowercase() || "varisty" in entry.tournament.name.lowercase() -> DebateLevel.VARSITY
                        "jv" in entry.division.lowercase() -> DebateLevel.JV
                        "novice" in entry.division.lowercase() -> DebateLevel.NOVICE
                        else -> DebateLevel.OPEN
                    }
                }

    return@coroutineScope history
}

private const val SESSION_ROW = "div.main > div.full.flexrow.row.ltborderbottom.smallish"

/**
 * Fetches the current sessions for the user when logged in.
 * @return A list of login sessions for the user
 * @throws IllegalStateException if not logged in
 */
@JvmName("getCurrentSessionsAsync")
@JsName("getCurrentSessionsAsync")
suspend fun getCurrentSessions(): List<Session> = coroutineScope {
    if (!isLoggedIn) throw IllegalStateException("User is not logged in")

    val doc = USER_PROFILE.fetchDocument()

    val sessions = doc.querySelectorAll(SESSION_ROW)
    val sessionList = mutableListOf<Session>()

    for (child in sessions) {
        val lastActiveTime = child.children[0].textContent.trim()
        val lastActiveDate = child.children[1].textContent.trim()
        val browser = child.children[2].textContent.trim()
        val ip = child.children[3].textContent.trim()
        val isp = child.children[4].textContent.trim()
        val location = child.children[5].textContent.trim()

        sessionList.add(
            Session(
                lastActiveTime.ifEmpty { null },
                lastActiveDate.ifEmpty { null },
                browser,
                ip,
                isp.ifEmpty { null },
                location.ifEmpty { null }
            )
        )
    }
    return@coroutineScope sessionList
}